package com.goal.order.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.goal.enums.BizCodeEnum;
import com.goal.exception.BizException;
import com.goal.order.domain.dto.OrderConfirmDTO;
import com.goal.order.domain.po.ProductOrder;
import com.goal.order.service.ProductOrderService;
import com.goal.order.mapper.ProductOrderMapper;
import com.goal.utils.Result;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
* @author Goal
* @description 针对表【product_order】的数据库操作Service实现
* @createDate 2024-06-06 16:42:25
*/
@Service
public class ProductOrderServiceImpl extends ServiceImpl<ProductOrderMapper, ProductOrder>
    implements ProductOrderService{

    @Resource
    private ProductOrderMapper productOrderMapper;

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
        return null;
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




