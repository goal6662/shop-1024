package com.goal.user.feign;

import com.goal.user.domain.dto.CouponNewUserDTO;
import com.goal.utils.Result;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient("coupon-service")
public interface CouponFeignService {

    /**
     * 新用户注册发放优惠券
     * @param couponNewUserDTO
     * @return
     */
    @PostMapping("/api/${app.config.api.version}/coupon/new_user_coupon")
    Result addNewUserCoupon(@RequestBody CouponNewUserDTO couponNewUserDTO);

}
