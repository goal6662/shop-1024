package com.goal.constant;

public class CacheKey {
    public static final String CACHE_CODE_KEY = "code:%s:%s";

    /**
     * 购物车缓存 key
     */
    public static final String CART_KEY = "cart:%s:";

    /**
     * 获取用户购物车缓存Key
     * @param userId 用户ID，或为一标识
     * @return
     */
    public static String getCartKey(long userId) {
        return String.format(CART_KEY, userId);
    }
}
