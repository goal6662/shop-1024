package com.goal.product.service.impl;

import com.goal.constant.CacheKey;
import com.goal.product.service.CartService;
import com.goal.utils.UserContext;

abstract public class AbstractCartService implements CartService {
    /**
     * 购物车缓存 key
     * @return
     */
    protected String getCartKey() {
        Long userId = UserContext.getUser().getId();
        return CacheKey.getCartKey(userId);
    }


}
