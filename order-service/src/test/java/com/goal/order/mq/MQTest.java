package com.goal.order.mq;

import com.goal.domain.mq.TimeoutCloseOrderMessage;
import com.goal.order.OrderApplication;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;

@Slf4j
@RunWith(SpringRunner.class)
@SpringBootTest(classes = OrderApplication.class)
public class MQTest {

    @Resource
    private RabbitMQService rabbitMQService;

    @Test
    public void test() {

        TimeoutCloseOrderMessage timeoutCloseOrderMessage = new TimeoutCloseOrderMessage();
        timeoutCloseOrderMessage.setOutTradeNo("123456");

        rabbitMQService.sendMessageToDelayQueue(timeoutCloseOrderMessage);
    }

}
