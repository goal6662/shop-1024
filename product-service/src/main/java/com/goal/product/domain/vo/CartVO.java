package com.goal.product.domain.vo;

import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

/**
 * 购物车
 */
@Data
public class CartVO {

    private List<CartItemVO> cartItems;

    /**
     * 商品总件数
     */
    private Integer totalNum;

    /**
     * 总价
     */
    private BigDecimal totalPrice;

    /**
     * 实际支付价格
     */
    private BigDecimal realPayPrice;

    /**
     * 获取总件数
     * @return
     */
    public Integer getTotalNum() {
        if (this.cartItems.isEmpty()) {
            return 0;
        }
        return cartItems.stream().mapToInt(CartItemVO::getBuyNum).sum();
    }

    public BigDecimal getTotalPrice() {
        if (cartItems.isEmpty()) {
            return BigDecimal.ZERO;
        }

        BigDecimal total = BigDecimal.ZERO;
        for (CartItemVO cartItem : cartItems) {
            total = total.add(cartItem.getTotalAmount());
        }
        return total;
    }

    public BigDecimal getRealPayPrice() {
        return getTotalPrice();
    }
}
