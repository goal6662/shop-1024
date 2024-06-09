package com.goal.product.domain.dto;

import lombok.Data;

@Data
public class CartItemDTO {

    /**
     * 商品id
     */
    private long productId;

    /**
     * 购买数量
     */
    private int buyNum;


}
