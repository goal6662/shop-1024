package com.goal.order.service;

import com.goal.order.domain.dto.OrderConfirmDTO;
import com.goal.order.domain.po.ProductOrder;
import com.baomidou.mybatisplus.extension.service.IService;
import com.goal.utils.Result;

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
}
