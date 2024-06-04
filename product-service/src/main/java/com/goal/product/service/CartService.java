package com.goal.product.service;

import com.goal.product.domain.dto.CartItemDTO;

public interface CartService {
    /**
     * 添加商品到购物车
     * @param cartItemDTO
     */
    void addToCart(CartItemDTO cartItemDTO);

    /**
     * 清空用户购物车
     */
    void clearUserCart();
}
