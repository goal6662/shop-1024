package com.goal.coupon;

import com.goal.coupon.config.RabbitMQConfig;
import com.goal.domain.mq.CouponRecordMessage;
import com.goal.enums.BizCodeEnum;
import com.goal.utils.Result;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;

@Slf4j
@RunWith(SpringRunner.class)
@SpringBootTest(classes = CouponApplication.class)
public class MQTest {

    @Resource
    private RabbitTemplate rabbitTemplate;

    @Resource
    private RabbitMQConfig rabbitMQConfig;

    @Test
    public void mqTest() {

        rabbitTemplate.convertAndSend(rabbitMQConfig.getEventExchange(), rabbitMQConfig.getReleaseDelayRoutingKey(),
                Result.fail(BizCodeEnum.COUPON_NO_STOCK));

    }

    @Test
    public void testCouponRecordRelease() {

        CouponRecordMessage couponRecordMessage = new CouponRecordMessage();
        couponRecordMessage.setOutTradeNo("123456");
        couponRecordMessage.setTaskId(18L);

        rabbitTemplate.convertAndSend(rabbitMQConfig.getEventExchange(),
                rabbitMQConfig.getReleaseDelayRoutingKey(), couponRecordMessage);

    }

}
