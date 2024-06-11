package com.goal.order.domain.dto;

import lombok.Data;

import java.util.List;

@Data
public class ProductLockDTO {

    /**
     * 订单号
     */
    private String outTradeNo;

    /**
     * 商品ID
     */
    private List<CartItemDTO> orderItemList;

}
