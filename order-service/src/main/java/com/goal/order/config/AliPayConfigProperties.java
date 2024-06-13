package com.goal.order.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * 沙箱环境的支付宝配置：与正式支付有差异
 */
@Data
@ConfigurationProperties(prefix = "alipay.config", ignoreInvalidFields = true)
public class AliPayConfigProperties {

    /**
     * 是否开启
     */
    private boolean enabled;

    /**
     * 支付宝网关：服务器地址
     */
    private String serverUrl;

    /**
     * 开发者id
     */
    private String appid;

    /**
     * 商户私钥
     */
    private String privateKey;

    /**
     * 参数返回格式，仅支持JSON
     */
    private String format;

    /**
     * 编码方式
     */
    private String charset;

    /**
     * 支付宝公钥
     */
    private String alipayPublicKey;

    /**
     * 签名方式
     */
    private String signType;

    /**
     * 异步通知回调
     */
    private String notifyUrl;

    /**
     * 页面跳转同步通知页面路径
     * 外网正常访问，不能包含路径参数
     */
    private String returnUrl;
}
