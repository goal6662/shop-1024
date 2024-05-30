package com.goal.enums;

import lombok.Getter;

/**
 * 同意状态码，错误信息
 *  前三位是服务类型
 *  后三位是错误类型
 */
@Getter
public enum BizCodeEnum {

    /**
     * 通用操作
     */
    OPS_SUCCESS(100000, "操作成功"),
    OPS_ERROR(110001, "操作失败"),
    OPS_REPEAT(110002, "重复操作"),

    /**
     * 验证码相关
     */
    CODE_TO_ERROR(240001, "接收号码不合规"),
    CODE_LIMITED(240002, "验证码发送过快"),
    CODE_ERROR(240003, "验证码错误"),
    CODE_CAPTCHA_ERROR(240101, "图形验证码错误"),

    /**
     * 账号相关
     */
    ACCOUNT_REPEAT(250001, "账号已经存在"),
    ACCOUNT_UNREGISTER(250002, "账号不存在"),
    ACCOUNT_PWD_ERROR(250003, "账号或密码错误");

    private final String message;
    private final int code;

    BizCodeEnum(int code, String message) {
        this.code = code;
        this.message = message;
    }


}
