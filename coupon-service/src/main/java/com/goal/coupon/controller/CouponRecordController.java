package com.goal.coupon.controller;

import com.goal.coupon.service.CouponRecordService;
import com.goal.utils.Result;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

@RestController
@RequestMapping("api/${app.config.api.version}/coupon_record")
public class CouponRecordController {

    @Resource
    private CouponRecordService couponRecordService;

    @ApiOperation("分页查询个人优惠券")
    @GetMapping("page")
    public Result pageCoupon(
            @ApiParam(value = "当前页", defaultValue = "1")
            @RequestParam(value = "page", defaultValue = "1") int page,
            @ApiParam(value = "每页记录数", defaultValue = "10")
            @RequestParam(value = "size", defaultValue = "10") int size
    ) {
        return couponRecordService.pageUserCouponRecord(page, size);
    }

}
