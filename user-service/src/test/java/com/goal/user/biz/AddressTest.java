package com.goal.user.biz;

import com.goal.user.UserApplication;
import com.goal.user.domain.Address;
import com.goal.user.service.AddressService;
import lombok.extern.slf4j.Slf4j;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;

@Slf4j
@RunWith(SpringRunner.class)
@SpringBootTest(classes = UserApplication.class)    // 启动类
public class AddressTest {

    @Resource
    private AddressService addressService;

    @Test
    public void testAddressDetail() {
        Address address = addressService.getById(39);

        log.info(address.toString());
        Assert.assertNotNull(address);
    }

}
