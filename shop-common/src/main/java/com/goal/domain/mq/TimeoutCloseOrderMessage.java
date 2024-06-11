package com.goal.domain.mq;

import lombok.Data;

@Data
public class TimeoutCloseOrderMessage {

    private String messageId;

    /**
     * 订单号
     */
    private String outTradeNo;

}
