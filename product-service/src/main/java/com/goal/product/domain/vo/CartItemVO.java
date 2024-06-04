package com.goal.product.domain.vo;

import lombok.Data;

import java.math.BigDecimal;

/**
 * 购物车项
 */
@Data
public class CartItemVO {

    /**
     * 商品id
     */
    private Long id;

    /**
     * 商品id
     */
    private Long productId;

    /**
     * 购买数量
     */
    private Integer buyNum;

    /**
     * 商品标题
     */
    private String productTitle;

    /**
     * 商品图片
     */
    private String productImg;

    /**
     * 商品单价
     */
    private BigDecimal price;

    /**
     * 商品总价
     */
    private BigDecimal totalPrice;

    /**
     * 获取总价
     * @return 数量 * 单价
     */
    public BigDecimal getTotalAmount() {
        return this.price.multiply(new BigDecimal(this.buyNum));
    }

}
