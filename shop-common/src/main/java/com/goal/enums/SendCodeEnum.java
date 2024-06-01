package com.goal.enums;

public enum SendCodeEnum {

    USER_REGISTER;

    public String getCacheKey(String type) {
        return "code:" + this.name() + ":" + type;
    }

}
