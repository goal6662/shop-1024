package com.goal.product.service;

import com.goal.product.domain.dto.CartItemDTO;
import com.goal.product.domain.vo.CartItemVO;
import com.goal.product.domain.vo.CartVO;
import com.goal.utils.Result;

import java.util.List;

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

    /**
     * 查询我的购物车
     * @return
     */
    CartVO getMyCart();

    /**
     * 删除购物项
     * @param productId
     */
    void deleteItemById(Long productId);

    void changeItemNum(CartItemDTO cartItemDTO);

    /**
     * 返回商品的最新价格，同时从购物车删除该项
     * @param productIdList
     * @return
     */
    List<CartItemVO> confirmOrderCartItems(List<Long> productIdList);

    /**
     * 添加商品到购物车
     * @param cartItemDTOList
     * @param userId
     */
    void addItemsToCart(List<CartItemDTO> cartItemDTOList, Long userId);
}
