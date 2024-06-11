package com.goal.coupon.service.impl;

import com.goal.coupon.config.RabbitMQConfig;
import com.goal.domain.mq.CouponRecordMessage;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

@Service
public class RabbitMQService {

    @Resource
    private RabbitMQConfig rabbitMQConfig;

    @Resource
    private RabbitTemplate rabbitTemplate;

    /**
     * 发送优惠券记录延迟消息
     * @param couponRecordMessage 消息对象
     */
    public void sendCouponRecordTaskMsg(CouponRecordMessage couponRecordMessage) {
        sendMessageToDelayQueue(couponRecordMessage);
    }

    /**
     * 发送消息到延迟队列
     * @param message 消息对象
     */
    private void sendMessageToDelayQueue(Object message) {
        rabbitTemplate.convertAndSend(rabbitMQConfig.getEventExchange(), rabbitMQConfig.getReleaseDelayRoutingKey(),
                message);
    }
}
