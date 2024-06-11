package com.goal.coupon;

import com.goal.enums.coupon.CouponCategoryEnum;
import com.goal.coupon.domain.po.Coupon;
import com.goal.coupon.mapper.CouponMapper;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;
import java.util.List;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = CouponApplication.class)
public class DBTest {

    @Resource
    private CouponMapper couponMapper;

    @Test
    public void queryTest() {
        List<Coupon> coupons =
                couponMapper.listCouponByCategory(CouponCategoryEnum.NEW_USER.name());

        System.out.println(coupons);
    }

}
