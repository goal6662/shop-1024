package com.goal.user.common;

public interface RedisConstants {

    String USER_CAPTCHA_KEY = "user-service:captcha:";
    long USER_CAPTCHA_KEY_TTL = 1000 * 60 * 5;

    String USER_REGISTER_KEY = "user-service:register:captcha:";
    long USER_REGISTER_KEY_TTL = 1000 * 60 * 5;

    long REFRESH_TOKEN_TTL = 1000 * 60 * 60 * 24 * 30L;  // 30Day
}
