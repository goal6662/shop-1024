package com.goal.product.mq;

import com.goal.domain.ProductMessage;
import com.goal.product.ProductApplication;
import com.goal.utils.Result;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;

@Slf4j
@RunWith(SpringRunner.class)
@SpringBootTest(classes = ProductApplication.class)
public class MQTest {

    @Resource
    private RabbitMQService rabbitMQService;

    @Test
    public void mqTest() {

        ProductMessage productMessage = new ProductMessage();
        productMessage.setTaskId(1L);
        productMessage.setOutTradeNo("123456");

        rabbitMQService.sendMessageToDelayQueue(productMessage);
    }

}
