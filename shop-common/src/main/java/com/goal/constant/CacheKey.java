package com.goal.constant;

public class CacheKey {
    public static final String SUBMIT_ORDER_TOKEN_KEY = "order:submit:%s";

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

    /**
     * 获取用户下单的缓存key
     * @param userId 用户id，或2唯一标识
     * @return
     */
    public static String getSubmitOrderTokenKey(long userId) {
        return String.format(SUBMIT_ORDER_TOKEN_KEY, userId);
    }
}
