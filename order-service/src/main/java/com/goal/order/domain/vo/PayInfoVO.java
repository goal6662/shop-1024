package com.goal.order.domain.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PayInfoVO {

    /**
     * 订单号
     */
    private String outTradeNo;

    /**
     * 订单总额
     */
    private BigDecimal payFee;

    /**
     * 支付方式
     */
    private String payType;

    /**
     * 客户端类型
     */
    private String clientType;

    /**
     * 标题
     */
    private String title;

    /**
     * 描述
     */
    private String description;

    /**
     * 订单支付超时时间：毫秒
     */
    private Long orderPayTimeout;

}
