package com.goal.order.service.impl;

import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.goal.domain.LoginUser;
import com.goal.enums.BizCodeEnum;
import com.goal.exception.BizException;
import com.goal.order.domain.dto.OrderConfirmDTO;
import com.goal.order.domain.po.ProductOrder;
import com.goal.order.domain.vo.CartItemVO;
import com.goal.order.domain.vo.ProductOrderAddressVO;
import com.goal.order.feign.ProductCartFeignService;
import com.goal.order.feign.UserFeignService;
import com.goal.order.service.ProductOrderService;
import com.goal.order.mapper.ProductOrderMapper;
import com.goal.utils.CommonUtil;
import com.goal.utils.Result;
import com.goal.utils.UserContext;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
* @author Goal
* @description 针对表【product_order】的数据库操作Service实现
* @createDate 2024-06-06 16:42:25
*/
@Slf4j
@Service
public class ProductOrderServiceImpl extends ServiceImpl<ProductOrderMapper, ProductOrder>
    implements ProductOrderService{

    @Resource
    private ProductOrderMapper productOrderMapper;

    @Resource
    private UserFeignService userFeignService;

    @Resource
    private ProductCartFeignService productCartFeignService;

    /**
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
     * @param orderConfirmDTO
     * @return
     */
    @Override
    public Result submitOrder(OrderConfirmDTO orderConfirmDTO) {

        // TODO: 2024/6/6 创建订单
        LoginUser loginUser = UserContext.getUser();
        String orderTradeNo = CommonUtil.getStringNumRandom(32);

        // 获取收货地址信息
        ProductOrderAddressVO addressVO = this.getUserAddress(orderConfirmDTO.getAddressId());

        // 获取用户加入购物车的商品
        List<Long> productIdList = orderConfirmDTO.getProductIdList();

        Result<List<CartItemVO>> result = productCartFeignService.confirmOrderCartItem(productIdList);

        List<CartItemVO> cartItemVOList;
        if (result.getCode() != BizCodeEnum.OPS_SUCCESS.getCode()
                || (cartItemVOList = result.getData()) == null) {
            throw new BizException(BizCodeEnum.ORDER_SUBMIT_CART_ITEM_NOT_EXIST);
        }
        log.info("获取商品：{}", cartItemVOList);




        return null;
    }

    /**
     * 根据ID获取收货地址详情
     * @param addressId
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
}




