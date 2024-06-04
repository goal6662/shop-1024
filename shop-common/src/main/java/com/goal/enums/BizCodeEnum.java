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
     * 请求相关
     */
    REQ_PARAM_ILLEGAL(160001, "请求参数不合法"),

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
    ACCOUNT_PWD_ERROR(250003, "账号或密码错误"),
    ACCOUNT_LOGIN_EXPIRED(250004, "登录信息已过期"),
    ACCOUNT_ADDRESS_NOT_EXIST(250201, "当前地址不存在"),

    /**
     * 文件相关
     */
    FILE_UPLOAD_ERROR(260001, "文件上传失败"),


    /**
     * 优惠券相关
     */
    COUPON_UNAVAILABLE(300001, "当前优惠券不可用"),
    COUPON_OUT_OF_LIMIT(300002, "优惠券领取次数达到上限"),
    COUPON_NO_STOCK(300003, "优惠券库存不足"),
    COUPON_NOT_EXIST(300004, "查询优惠券不存在");

    private final String message;
    private final int code;

    BizCodeEnum(int code, String message) {
        this.code = code;
        this.message = message;
    }


}
