package com.goal.product.service.impl;

import com.goal.constant.CacheKey;
import com.goal.product.domain.vo.CartItemVO;
import com.goal.product.domain.vo.CartVO;
import com.goal.product.service.CartService;
import com.goal.utils.UserContext;

import java.util.List;

abstract public class AbstractCartService implements CartService {
    /**
     * 购物车缓存 key
     * @return
     */
    protected String getCartKey() {
        Long userId = UserContext.getUser().getId();
        return CacheKey.getCartKey(userId);
    }

    @Override
    public CartVO getMyCart() {
        // 1. 查询所有购物项
        List<CartItemVO> cartItemVOList = buildCartItem(false);

        CartVO cartVO = new CartVO();
        cartVO.setCartItems(cartItemVOList);

        return cartVO;
    }

    /**
     * 获取购物项
     * @param latestPrice 是否获取最新价格, 下单时一定要获取最新价格
     * @return
     */
    protected abstract List<CartItemVO> buildCartItem(boolean latestPrice);
}
