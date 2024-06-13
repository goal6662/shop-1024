package com.goal.constant;

public class RedisConstant {

    /**
     * 优惠券
     */
    public static final String COUPON_LOCK_KEY = "lock:coupon:key:";

    public static final long COUPON_LOCK_TTL = 1000 * 30;

    /**
     * 校验验证码
     */
    public static final String USER_CAPTCHA_KEY = "user-service:captcha:";
    public static final long USER_CAPTCHA_KEY_TTL = 1000 * 60 * 5;

    /**
     * 注册验证码
     */
    public static final String USER_REGISTER_KEY = "user-service:register:captcha:";
    public static final long USER_REGISTER_KEY_TTL = 1000 * 60 * 5;

    public static final long REFRESH_TOKEN_TTL = 1000 * 60 * 60 * 24 * 30L;  // 30Day

}
