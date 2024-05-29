package com.goal.user.common;

public interface RedisConstants {

    String USER_CAPTCHA_KEY = "user-service:captcha:";
    long USER_CAPTCHA_KEY_TTL = 1000 * 60 * 5;

}
