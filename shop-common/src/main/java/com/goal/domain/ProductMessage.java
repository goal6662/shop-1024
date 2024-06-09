package com.goal.domain;

import lombok.Data;

@Data
public class ProductMessage {

    /**
     * 消息ID
     */
    private String messageId;

    /**
     * 订单号
     */
    private String outTradeNo;

    /**
     * 任务ID
     */
    private Long taskId;

}
