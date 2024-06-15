package com.goal.order.service;

import com.goal.domain.mq.TimeoutCloseOrderMessage;
import com.goal.order.domain.dto.OrderConfirmDTO;
import com.goal.order.domain.mq.CartRecoveryMessage;
import com.goal.order.domain.po.ProductOrder;
import com.baomidou.mybatisplus.extension.service.IService;
import com.goal.utils.Result;

import java.util.Map;

/**
* @author Goal
* @description 针对表【product_order】的数据库操作Service
* @createDate 2024-06-06 16:42:25
*/
public interface ProductOrderService extends IService<ProductOrder> {

    /**
     * 创建订单
     * @param orderConfirmDTO
     * @return
     */
    Result submitOrder(OrderConfirmDTO orderConfirmDTO);

    /**
     * 查询订单状态
     * @param id 订单ID
     * @return 订单状态
     */
    Result<String> getOrderStateById(Long id);

    /**
     * 根据订单号查询订单状态
     * @param outTradeNo 订单号
     * @return 订单状态
     */
    Result<String> getOrderStateByOutTradeNo(String outTradeNo);

    /**
     * 超时关单
     * @param closeOrderMessage 请求信息
     * @return 消息是否被处理
     */
    boolean timeoutCloseOrder(TimeoutCloseOrderMessage closeOrderMessage);

    /**
     * 超时未创建订单恢复购物项
     * @param recoveryMessage 购物项信息
     * @return 信息是否被处理
     */
    boolean recoveryCartItems(CartRecoveryMessage recoveryMessage);

    /**
     * 处理支付回调信息
     * @param payType 支付方式
     * @param paramsMap 消息参数
     * @return 处理结果
     */
    Result handlerOrderCallbackMsg(String payType, Map<String, String> paramsMap);

    /**
     * 生成下单token，并保存在redis
     * @return token
     */
    Result<String> getSubmitToken();
}
