package com.goal.order.mq;

import com.goal.domain.mq.TimeoutCloseOrderMessage;
import com.goal.order.domain.mq.CartRecoveryMessage;
import com.goal.order.service.ProductOrderService;
import com.rabbitmq.client.Channel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.io.IOException;

@Slf4j
@Component
@RabbitListener(queues = "${mq.config.order.release_queue}")
public class TimeoutCloseOrderMQListener {

    @Resource
    private ProductOrderService productOrderService;

    /**
     * 超时关单消息
     * @param closeOrderMessage 消息类型，不是该类型的消息不会被接收
     * @param message 信息
     * @param channel 确认消息
     */
    @RabbitHandler
    public void timeoutCloseOrder(TimeoutCloseOrderMessage closeOrderMessage,
                                    Message message, Channel channel) throws IOException {

        log.info("监听到消息：{}", closeOrderMessage);

        long msgTag = message.getMessageProperties().getDeliveryTag();
        boolean flag = productOrderService.timeoutCloseOrder(closeOrderMessage);

        try {
            if (flag) {
                // 确认消息消费成功
                channel.basicAck(msgTag, false);
            } else {
                log.error("超时关单失败：{}", closeOrderMessage);
                channel.basicReject(msgTag, true);  // true 消费失败重新入队
            }
        } catch (Exception e) {
            log.error("超时关单异常：{}", e.getMessage());
            channel.basicReject(msgTag, true);  // true 消费失败重新入队
        }

    }

    @RabbitHandler
    public void recoveryCartItems(CartRecoveryMessage recoveryMessage,
                                  Message message, Channel channel) throws IOException {
        log.info("监听到消息：{}", recoveryMessage);

        long msgTag = message.getMessageProperties().getDeliveryTag();
        boolean flag = productOrderService.recoveryCartItems(recoveryMessage);

        try {
            if (flag) {
                // 确认消息消费成功
                channel.basicAck(msgTag, false);
            } else {
                log.error("恢复购物车失败：{}", recoveryMessage);
                channel.basicReject(msgTag, true);  // true 消费失败重新入队
            }
        } catch (Exception e) {
            log.error("恢复购物车异常：{}", e.getMessage());
            channel.basicReject(msgTag, true);  // true 消费失败重新入队
        }
    }

}
