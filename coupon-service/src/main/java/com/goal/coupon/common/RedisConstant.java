package com.goal.coupon.common;

public interface RedisConstant {

    String COUPON_LOCK_KEY = "lock:coupon:key:";

    long COUPON_LOCK_TTL = 1000 * 30;

}
