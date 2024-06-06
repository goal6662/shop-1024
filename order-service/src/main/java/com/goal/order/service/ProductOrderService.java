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
}
