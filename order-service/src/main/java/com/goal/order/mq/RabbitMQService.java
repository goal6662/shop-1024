package com.goal.order.mq;

import cn.hutool.json.JSONUtil;
import com.goal.order.config.RabbitMQConfig;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessagePostProcessor;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

@Slf4j
@Component
public class RabbitMQService {

    @Resource
    private RabbitTemplate rabbitTemplate;

    @Resource
    private RabbitMQConfig rabbitMQConfig;

    public void sendMessageToDelayQueue(Object message) {

        rabbitTemplate.convertAndSend(rabbitMQConfig.getEventExchange(),
                rabbitMQConfig.getReleaseDelayRoutingKey(), message);

    }

    /**
     * 发送时指定过期时间
     * @param message 要发送的消息
     * @param expiration 过期时间（毫秒）
     */
    public void sendMessageToDelayQueue(Object message, long expiration) {

        rabbitTemplate.convertAndSend(rabbitMQConfig.getEventExchange(),
                rabbitMQConfig.getReleaseDelayRoutingKey(), message,
                // 发送消息前进行处理
                msg -> {
                    MessageProperties properties = new MessageProperties();
                    properties.setExpiration(String.valueOf(expiration));
                    return msg;
                });

    }
}
