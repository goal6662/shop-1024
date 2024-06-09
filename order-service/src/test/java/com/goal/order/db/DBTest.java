package com.goal.order.db;

import com.goal.order.OrderApplication;
import com.goal.order.mapper.ProductOrderMapper;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = OrderApplication.class)
public class DBTest {

    @Resource
    private ProductOrderMapper productOrderMapper;

    @Test
    public void test() {
        System.out.println(productOrderMapper.getStateByOutTradeNo("123456").getState());
    }

}
