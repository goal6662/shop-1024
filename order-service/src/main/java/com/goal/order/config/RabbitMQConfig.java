package com.goal.order.config;

import lombok.Data;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.Exchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;

/**
 * MQ配置
 *  1 个交换机  eventExchange
 *  2 个队列
 *      延迟队列
 *      死信队列
 *  2 个绑定关系
 *      延迟队列 (release路由) 交换机
 *      死信队列 (delay路由) 交换机
 */
@Data
@Configuration
@ConfigurationProperties(prefix = "mq.config.order")
public class RabbitMQConfig {
    /**
     * 交换机
     */
    private String eventExchange;


    /**
     * 延迟队列：第一个队列
     */
    private String releaseDelayQueue;

    /**
     * 延迟队列的路由key
     *  进入队列的路由key
     */
    private String releaseDelayRoutingKey;


    /**
     * 死信队列
     *  被监听恢复库存的队列
     */
    private String releaseQueue;

    /**
     * 死信队列的路由key
     *  即进入死信队列的路由key
     */
    private String releaseRoutingKey;

    /**
     * 过期时间
     */
    @Value("${mq.config.ttl}")
    private Integer ttl;

    /**
     * 消息转换器
     * @return
     */
    @Bean
    public MessageConverter messageConverter() {
        return new Jackson2JsonMessageConverter();
    }

    /**
     * Topic类型的交换机
     *  一般一个微服务一个交换机
     * @return
     */
    @Bean
    public Exchange couponEventChange() {
        return new TopicExchange(eventExchange, true, false);
    }

    /**
     * 延迟队列
     * @return
     */
    @Bean
    public Queue couponReleaseDelayQueue() {

        Map<String, Object> args = new HashMap<>(3);
        args.put("x-message-ttl", ttl);
        args.put("x-dead-letter-exchange", eventExchange);  // 死信交换机
        args.put("x-dead-letter-routing-key", releaseRoutingKey);   // 死信队列路由

        return new Queue(releaseDelayQueue,
                true, false, false,
                args);
    }

    /**
     * 死信队列：一个普通队列，用于被监听
     * @return
     */
    @Bean
    public Queue couponReleaseQueue() {
        return new Queue(releaseQueue, true, false, false);
    }

    /**
     * 死信队列绑定关系
     *  队列通过路由 key 绑定到交换机
     * @return
     */
    @Bean
    public Binding couponReleaseBinding() {
        return new Binding(releaseQueue, Binding.DestinationType.QUEUE,
                eventExchange, releaseRoutingKey, null);
    }

    /**
     * 延迟队列绑定关系
     * @return
     */
    @Bean
    public Binding couponDelayBinding() {
        return new Binding(releaseDelayQueue, Binding.DestinationType.QUEUE,
                eventExchange, releaseDelayRoutingKey, null);
    }
}
