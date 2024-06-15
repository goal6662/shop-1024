package com.goal.order.service.impl;

import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.goal.constant.CacheKey;
import com.goal.constant.TimeConstant;
import com.goal.domain.LoginUser;
import com.goal.domain.mq.TimeoutCloseOrderMessage;
import com.goal.enums.BizCodeEnum;
import com.goal.enums.coupon.CouponRecordStatusEnum;
import com.goal.enums.order.PayTypeEnum;
import com.goal.enums.order.ProductOrderStateEnum;
import com.goal.enums.order.ProductOrderTypeEnum;
import com.goal.exception.BizException;
import com.goal.order.component.PayFactory;
import com.goal.order.component.strategy.AliPayConstants;
import com.goal.order.domain.dto.CartItemDTO;
import com.goal.order.domain.dto.CouponLockDTO;
import com.goal.order.domain.dto.OrderConfirmDTO;
import com.goal.order.domain.dto.ProductLockDTO;
import com.goal.order.domain.mq.CartRecoveryMessage;
import com.goal.order.domain.po.ProductOrder;
import com.goal.order.domain.po.ProductOrderItem;
import com.goal.order.domain.vo.CartItemVO;
import com.goal.order.domain.vo.CouponRecordVO;
import com.goal.order.domain.vo.PayInfoVO;
import com.goal.order.domain.vo.ProductOrderAddressVO;
import com.goal.order.feign.CouponFeignService;
import com.goal.order.feign.ProductCartFeignService;
import com.goal.order.feign.UserFeignService;
import com.goal.order.mapper.ProductOrderItemMapper;
import com.goal.order.mapper.ProductOrderMapper;
import com.goal.order.mq.RabbitMQService;
import com.goal.order.service.ProductOrderService;
import com.goal.utils.CommonUtil;
import com.goal.utils.Result;
import com.goal.utils.UserContext;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * @author Goal
 * @description 针对表【product_order】的数据库操作Service实现
 * @createDate 2024-06-06 16:42:25
 */
@Slf4j
@Service
public class ProductOrderServiceImpl extends ServiceImpl<ProductOrderMapper, ProductOrder>
        implements ProductOrderService {

    @Resource
    private ProductOrderMapper productOrderMapper;

    @Resource
    private ProductOrderItemMapper productOrderItemMapper;

    @Resource
    private RedisTemplate redisTemplate;

    @Resource
    private RabbitMQService rabbitMQService;

    @Resource
    private UserFeignService userFeignService;

    @Resource
    private ProductCartFeignService productCartFeignService;

    @Resource
    private CouponFeignService couponFeignService;

    @Resource
    private PayFactory payFactory;

    @Resource
    private ThreadPoolTaskExecutor taskExecutor;

    /**
     * TODO 可以使用线程池异步执行的有：购物车情空、优惠券锁定、商品库存锁定、订单验价
     * 防止重复提交
     * 用户微服务-确认收获地址
     * 商品微服务-获取购物车购物项的最新价格
     * 订单验价
     *  优惠券微服务-获取优惠券
     *  验证价格
     * 锁定优惠券
     * 锁定商品库存
     * 创建订单对象
     * 发送延迟消息-用于自动关单 [请在 15min 支付订单，超时订单将自动取消]
     * 创建支付消息-对接第三方支付
     *
     * @param orderConfirmDTO
     * @return
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result submitOrder(OrderConfirmDTO orderConfirmDTO) {

        LoginUser loginUser = UserContext.getUser();
        String orderTradeNo = CommonUtil.getStringNumRandom(32);

        // 获取收货地址信息
        ProductOrderAddressVO addressVO = this.getUserAddress(orderConfirmDTO.getAddressId());

        // 购物车清空：获取用户加入购物车的商品
        List<Long> productIdList = orderConfirmDTO.getProductIdList();

        Result<List<CartItemVO>> result = productCartFeignService.confirmOrderCartItem(productIdList);

        List<CartItemVO> cartItemVOList;
        if (result.getCode() != BizCodeEnum.OPS_SUCCESS.getCode()
                || (cartItemVOList = result.getData()) == null || cartItemVOList.isEmpty()) {
            throw new BizException(BizCodeEnum.ORDER_SUBMIT_CART_ITEM_NOT_EXIST);
        }
        // TODO: 2024/6/11 发送定时任务，恢复购物项
        sendCartRecoveryMessage(cartItemVOList, orderTradeNo, loginUser.getId());

        log.info("获取商品：{}", cartItemVOList);

        // 订单验价
        this.checkPrice(cartItemVOList, orderConfirmDTO);

        // 锁定库存
        this.lockProductStock(cartItemVOList, orderTradeNo);

        // 锁定优惠券
        this.lockCouponRecord(orderConfirmDTO, orderTradeNo);

        // 创建订单
        ProductOrder productOrder = createOrder(orderConfirmDTO, loginUser, addressVO, orderTradeNo);

        // 创建订单项
        createProductOrderItems(cartItemVOList, orderTradeNo, productOrder);

        // 超时关单
        TimeoutCloseOrderMessage timeoutCloseOrderMessage = new TimeoutCloseOrderMessage();
        timeoutCloseOrderMessage.setOutTradeNo(orderTradeNo);
        rabbitMQService.sendMessageToDelayQueue(timeoutCloseOrderMessage);

        // 创建支付
        PayInfoVO payInfoVO = new PayInfoVO(orderTradeNo, productOrder.getPayPrice(), orderConfirmDTO.getPayType(),
                orderConfirmDTO.getClientType(), "orderOutTradeNo", "",
                TimeConstant.ORDER_PAY_TIMEOUT_MILLS);
        String payResult = payFactory.pay(payInfoVO);
        if (StringUtils.isBlank(payResult)) {
            return Result.fail(BizCodeEnum.PAY_ORDER_FAIL);
        }

        return Result.success(payResult);
    }

    /**
     * 发送 恢复购物车 购物项 的消息
     *
     * @param cartItemVOList 购物项
     * @param orderTradeNo   订单号
     * @param userId         用户ID
     */
    private void sendCartRecoveryMessage(List<CartItemVO> cartItemVOList, String orderTradeNo, Long userId) {

        List<CartItemDTO> cartItemDTOList = cartItemVOList.stream().map((item) -> {
            CartItemDTO cartItemDTO = new CartItemDTO();
            BeanUtils.copyProperties(item, cartItemDTO);
            return cartItemDTO;
        }).collect(Collectors.toList());

        CartRecoveryMessage cartRecoveryMessage = new CartRecoveryMessage();
        cartRecoveryMessage.setCartItemDTOList(cartItemDTOList);
        cartRecoveryMessage.setOutTradeNo(orderTradeNo);
        cartRecoveryMessage.setCartCacheKey(String.valueOf(userId));

        // 10s 存活时间
        rabbitMQService.sendMessageToDelayQueue(cartRecoveryMessage, 10 * 1000);
    }

    private void createProductOrderItems(List<CartItemVO> cartItemVOList, String orderTradeNo, ProductOrder productOrder) {
        List<ProductOrderItem> productOrderItemList = cartItemVOList.stream().map((item) -> {
            ProductOrderItem productOrderItem = new ProductOrderItem();

            // 商品基本信息
            productOrderItem.setProductId(item.getProductId());
            productOrderItem.setProductImg(item.getProductImg());
            productOrderItem.setProductName(item.getProductTitle());
            productOrderItem.setPrice(item.getPrice());

            productOrderItem.setBuyNum(item.getBuyNum());
            productOrderItem.setOutTradeNo(orderTradeNo);   // 订单号
            productOrderItem.setProductOrderId(productOrder.getId());   // 订单ID
            productOrderItem.setCreateTime(productOrder.getCreateTime());

            productOrderItem.setTotalPrice(item.getTotalPrice());

            return productOrderItem;
        }).collect(Collectors.toList());

        int rows = productOrderItemMapper.insertBatch(productOrderItemList);
    }

    private ProductOrder createOrder(OrderConfirmDTO orderConfirmDTO, LoginUser loginUser, ProductOrderAddressVO addressVO, String orderTradeNo) {
        ProductOrder productOrder = new ProductOrder();

        // 用户相关
        productOrder.setUserId(loginUser.getId());
        productOrder.setHeadImg(loginUser.getHeadImg());
        productOrder.setNickname(loginUser.getName());
        productOrder.setReceiverAddress(JSONUtil.toJsonStr(addressVO));

        // 订单信息
        productOrder.setOutTradeNo(orderTradeNo);
        productOrder.setCreateTime(new Date());
        productOrder.setDel(0);
        productOrder.setOrderType(ProductOrderTypeEnum.DAILY.name());

        // 支付相关
        productOrder.setPayPrice(orderConfirmDTO.getRealPayPrice());
        productOrder.setTotalPrice(orderConfirmDTO.getTotalPrice());
        productOrder.setState(ProductOrderStateEnum.NEW.name());
        productOrder.setPayType(PayTypeEnum.valueOf(orderConfirmDTO.getPayType()).name());

        productOrderMapper.insert(productOrder);

        return productOrder;
    }

    /**
     * 锁定库存
     *
     * @param cartItemVOList 购物项
     * @param orderTradeNo
     */
    private void lockProductStock(List<CartItemVO> cartItemVOList, String orderTradeNo) {

        ProductLockDTO productLockDTO = new ProductLockDTO();
        // 订单号
        productLockDTO.setOutTradeNo(orderTradeNo);
        // 购买物品
        List<CartItemDTO> cartItemDTOList = cartItemVOList.stream().map((item) -> {
            CartItemDTO cartItemDTO = new CartItemDTO();
            cartItemDTO.setProductId(item.getProductId());
            cartItemDTO.setBuyNum(item.getBuyNum());
            return cartItemDTO;
        }).collect(Collectors.toList());
        productLockDTO.setOrderItemList(cartItemDTOList);

        Result result = productCartFeignService.lockProducts(productLockDTO);
        if (result.getCode() != BizCodeEnum.OPS_SUCCESS.getCode()) {
            log.error("锁定商品库存失败：{}", productLockDTO);
            throw new BizException(BizCodeEnum.PRODUCT_STOCK_LOCK_FAIL);
        }

    }

    /**
     * 锁定优惠券
     *
     * @param orderConfirmDTO
     * @param orderTradeNo
     */
    private void lockCouponRecord(OrderConfirmDTO orderConfirmDTO, String orderTradeNo) {

        if (orderConfirmDTO.getCouponRecordId() < 0) {
            return;
        }

        CouponLockDTO couponLockDTO = new CouponLockDTO();
        couponLockDTO.setOrderOutTradeNo(orderTradeNo);
        couponLockDTO.setLockCouponRecordIds(List.of(orderConfirmDTO.getCouponRecordId()));

        Result result = couponFeignService.lockCouponRecords(couponLockDTO);
        if (result.getCode() != BizCodeEnum.OPS_SUCCESS.getCode()) {
            log.error("锁定优惠券失败：{}", couponLockDTO);
            throw new BizException(BizCodeEnum.COUPON_RECORD_LOCK_FAIL);
        }
    }

    /**
     * 验证价格
     *
     * @param cartItemVOList  购买商品
     * @param orderConfirmDTO 请求
     */
    private void checkPrice(List<CartItemVO> cartItemVOList, OrderConfirmDTO orderConfirmDTO) {

        // 统计商品总价
        BigDecimal realPayPrice = BigDecimal.ZERO;
        BigDecimal totalPrice = BigDecimal.ZERO;
        if (cartItemVOList != null) {
            for (CartItemVO cartItemVO : cartItemVOList) {
                totalPrice = totalPrice.add(cartItemVO.getTotalPrice());
            }
        }

        // 获取优惠券，判断是否可用
        CouponRecordVO couponRecordVO = getCartCouponRecord(orderConfirmDTO.getCouponRecordId());
        if (couponRecordVO != null) {

            if (totalPrice.compareTo(couponRecordVO.getConditionPrice()) < 0) {
                // 优惠券不可以使用
                throw new BizException(BizCodeEnum.COUPON_UNAVAILABLE);
            }

            // 商品价格不能低于优惠券优惠价格
            if (totalPrice.compareTo(couponRecordVO.getPrice()) > 0) {
                // 商品总价大于优惠券折扣额度
                realPayPrice = totalPrice.subtract(couponRecordVO.getPrice());
            }
        } else {
            realPayPrice = totalPrice;
        }

        // 比较前后端价格是否一致
        if (totalPrice.compareTo(orderConfirmDTO.getTotalPrice()) != 0
                || realPayPrice.compareTo(orderConfirmDTO.getRealPayPrice()) != 0) {
            throw new BizException(BizCodeEnum.ORDER_PRICE_CHANGE);
        }

    }

    private CouponRecordVO getCartCouponRecord(Long couponRecordId) {

        if (couponRecordId == null || couponRecordId < 0) {
            return null;
        }

        Result<CouponRecordVO> result = couponFeignService.findUserCouponRecordById(couponRecordId);
        if (result.getCode() != BizCodeEnum.OPS_SUCCESS.getCode()) {
            // 远程调用不成功
            throw new BizException(BizCodeEnum.COUPON_NOT_EXIST);
        }

        CouponRecordVO couponRecordVO = result.getData();
        if (!couponAvailable(couponRecordVO)) {
            // 优惠券不可用
            throw new BizException(BizCodeEnum.COUPON_UNAVAILABLE);
        }

        return couponRecordVO;
    }

    private boolean couponAvailable(CouponRecordVO couponRecordVO) {

        if (couponRecordVO.getUseState().equalsIgnoreCase(CouponRecordStatusEnum.NEW.name())) {
            long current = CommonUtil.getCurrentTimeStamp();

            long begin = couponRecordVO.getStartTime().getTime();
            long end = couponRecordVO.getEndTime().getTime();

            return begin <= current && current <= end;

        }
        return false;
    }

    /**
     * 根据ID获取收货地址详情
     *
     * @return
     */
    private ProductOrderAddressVO getUserAddress(Long addressId) {
        Result addressDetail = userFeignService.detail(addressId);

        if (addressDetail.getCode() != BizCodeEnum.OPS_SUCCESS.getCode()) {
            log.error("获取收货地址失败");
            throw new BizException(BizCodeEnum.ACCOUNT_ADDRESS_NOT_EXIST);
        }

        // 转换对象，先转为JSON -> Obj
        Object data = addressDetail.getData();
        ProductOrderAddressVO addressVO = JSONUtil.toBean(JSONUtil.toJsonStr(data), ProductOrderAddressVO.class);
        log.info("收获地址信息：{}", addressVO);

        return addressVO;
    }

    @Override
    public Result<String> getOrderStateById(Long id) {
        ProductOrder productOrder = productOrderMapper.selectById(id);
        return Result.success(productOrder.getState());
    }

    @Override
    public Result<String> getOrderStateByOutTradeNo(String outTradeNo) {
        ProductOrder productOrder = productOrderMapper.getStateByOutTradeNo(outTradeNo);

        if (productOrder == null) {
            throw new BizException(BizCodeEnum.ORDER_NO_EXIST);
        }
        return Result.success(productOrder.getState());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean timeoutCloseOrder(TimeoutCloseOrderMessage closeOrderMessage) {
        String outTradeNo = closeOrderMessage.getOutTradeNo();
        // 1. 查询订单状态
        ProductOrder productOrder = productOrderMapper.getStateByOutTradeNo(outTradeNo);
        if (productOrder == null) {
            log.warn("直接确认消息，订单不存在：{}", closeOrderMessage);
            return true;
        }

        // 2. 订单已支付
        if (productOrder.getState().equalsIgnoreCase(ProductOrderStateEnum.PAY.name())) {
            log.warn("订单已支付：{}", closeOrderMessage);
            return true;
        }

        // 向第三方发送请求查询订单是否支付
        PayInfoVO payInfoVO = new PayInfoVO();
        payInfoVO.setOutTradeNo(outTradeNo);
        payInfoVO.setPayType(productOrder.getPayType());

        String payResult = payFactory.querySuccess(payInfoVO);
        if (StringUtils.isNotBlank(payResult) &&
                (AliPayConstants.TRADE_SUCCESS.equalsIgnoreCase(payResult)
                        || AliPayConstants.TRADE_FINISH.equals(payResult))) {
            // 订单支付成功
            int rows = productOrderMapper.updateOrderPayStatus(outTradeNo, ProductOrderStateEnum.PAY.name(),
                    ProductOrderStateEnum.NEW.name());
            log.warn("订单支付成功，之前未接受到回调信息，注意排查：{}", closeOrderMessage);
        } else {
            // 订单未支付，超时关单，变更订单状态
            int rows = productOrderMapper.updateOrderPayStatus(outTradeNo, ProductOrderStateEnum.CANCEL.name(),
                    ProductOrderStateEnum.NEW.name());
            log.info("订单未支付，取消订单：{}", closeOrderMessage);
        }
        return true;
    }

    /**
     * 远程调用不成功需要消息重新入队
     *
     * @param recoveryMessage 购物项信息
     * @return
     */
    @Override
    public boolean recoveryCartItems(CartRecoveryMessage recoveryMessage) {

        String outTradeNo = recoveryMessage.getOutTradeNo();
        // 1. 订单未被创建
        ProductOrder productOrder = productOrderMapper.getOrderByOutTradeNo(outTradeNo);
        if (productOrder == null) {
            // 订单未被创建，执行恢复
            Result result = productCartFeignService.addCartItems(recoveryMessage.getCartItemDTOList(),
                    Long.valueOf(recoveryMessage.getCartCacheKey()));
            if (result.getCode() != BizCodeEnum.OPS_SUCCESS.getCode()) {
                log.error("购物车恢复失败：{}", recoveryMessage);
                return false;
            }
        }

        return true;
    }

    @Override
    public Result handlerOrderCallbackMsg(String payType, Map<String, String> paramsMap) {
        // 处理支付回调

        if (payType.equalsIgnoreCase(PayTypeEnum.ALIPAY.name())) {
            return handlerAlipayCallbackMsg(paramsMap);
        }

        return Result.fail(BizCodeEnum.PAY_TYPE_NOT_SUPPORT);
    }

    @Override
    public Result<String> getSubmitToken() {
        Long userId = UserContext.getUser().getId();

        String orderTokenKey = CacheKey.getSubmitOrderTokenKey(userId);
        String token = CommonUtil.getStringNumRandom(32);

        redisTemplate.opsForValue().set(orderTokenKey, token, 30, TimeUnit.MINUTES);

        return Result.success(token);
    }

    /**
     * 处理支付宝回调消息
     * @param paramsMap 消息参数
     */
    private Result handlerAlipayCallbackMsg(Map<String, String> paramsMap) {

        // 商户订单号
        String outTradeNo = paramsMap.get(AliPayConstants.OUT_TRADE_NO);
        // 交易状态
        String tradeStatus = paramsMap.get(AliPayConstants.TRADE_STATUS);

        if (tradeStatus.equalsIgnoreCase(AliPayConstants.TRADE_SUCCESS)
                || tradeStatus.equalsIgnoreCase(AliPayConstants.TRADE_FINISH)) {
            // 支付成功，更新订单状态 NEW -> PAY
            productOrderMapper.updateOrderPayStatus(outTradeNo,
                    ProductOrderStateEnum.PAY.name(), ProductOrderStateEnum.NEW.name());

            return Result.success();
        }

        return Result.fail(BizCodeEnum.PAY_ORDER_CALLBACK_FAIL);
    }

}




