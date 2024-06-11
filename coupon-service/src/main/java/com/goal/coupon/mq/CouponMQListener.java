package com.goal.coupon.mq;

import com.goal.coupon.service.CouponRecordService;
import com.goal.domain.mq.CouponRecordMessage;
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
@RabbitListener(queues = "${mq.config.coupon.release_queue}")
public class CouponMQListener {

    @Resource
    private CouponRecordService couponRecordService;

    /**
     * 监听优惠券记录消息
     * @param couponRecordMessage 消息类型，不是该类型的消息不会被接收
     * @param message
     * @param channel
     * @throws IOException
     */
    @RabbitHandler
    public void releaseCouponRecord(CouponRecordMessage couponRecordMessage,
                                    Message message, Channel channel) throws IOException {

        log.info("监听到消息：{}", couponRecordMessage);

        long msgTag = message.getMessageProperties().getDeliveryTag();
        boolean flag = couponRecordService.releaseCouponRecord(couponRecordMessage);

        try {
            if (flag) {
                // 确认消息消费成功
                channel.basicAck(msgTag, false);
            } else {
                log.error("释放优惠券失败：{}", couponRecordMessage);
                channel.basicReject(msgTag, true);  // true 消费失败重新入队
            }
        } catch (Exception e) {
            log.error("释放优惠券异常：{}", e.getMessage());
            channel.basicReject(msgTag, true);  // true 消费失败重新入队
        }

    }

}
