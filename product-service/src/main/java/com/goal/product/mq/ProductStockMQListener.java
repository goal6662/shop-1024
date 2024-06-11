package com.goal.product.mq;

import com.goal.domain.mq.ProductMessage;
import com.goal.product.service.ProductService;
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
@RabbitListener(queues = "${mq.config.product.release_queue}")
public class ProductStockMQListener {

    @Resource
    private ProductService productService;

    /**
     * 监听优惠券记录消息
     * @param productMessage 消息类型，不是该类型的消息不会被接收
     * @param message
     * @param channel
     * @throws IOException
     */
    @RabbitHandler
    public void releaseCouponRecord(ProductMessage productMessage,
                                    Message message, Channel channel) throws IOException {

        log.info("监听到消息：{}", productMessage);

        long msgTag = message.getMessageProperties().getDeliveryTag();
        boolean flag = productService.releaseProductStock(productMessage);

        try {
            if (flag) {
                // 确认消息消费成功
                channel.basicAck(msgTag, false);
            } else {
                log.error("释放商品库存失败：{}", productMessage);
                channel.basicReject(msgTag, true);  // true 消费失败重新入队
            }
        } catch (Exception e) {
            log.error("释放商品库存异常：{}", e.getMessage());
            channel.basicReject(msgTag, true);  // true 消费失败重新入队
        }

    }


}
