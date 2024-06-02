package com.goal.coupon.controller;

import com.goal.coupon.service.CouponService;
import com.goal.utils.Result;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

@RestController
@RequestMapping("api/${app.config.api.version}/coupon")
public class CouponController {

    @Resource
    private CouponService couponService;

    @ApiOperation("分页查询优惠券")
    @GetMapping("page_coupon")
    public Result pageCouponList(
            @ApiParam("当前页") @RequestParam(value = "page", defaultValue = "1") int page,
            @ApiParam("每页记录数") @RequestParam(value = "size", defaultValue = "10") int size
            ) {
        return couponService.pageCoupon(page, size);
    }

}
