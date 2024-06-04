package com.goal.coupon.controller;

import com.goal.coupon.domain.dto.CouponNewUserDTO;
import com.goal.coupon.domain.enums.CouponCategoryEnum;
import com.goal.coupon.service.CouponService;
import com.goal.utils.Result;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

@Api("优惠券服务")
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

    @ApiOperation("领取促销类型的优惠券")
    @GetMapping("add/promotion/{coupon_id}")
    public Result addPromotionCoupon(
            @ApiParam("优惠券ID")
            @PathVariable("coupon_id") long couponId
    ) {
        return couponService.addCoupon(couponId, CouponCategoryEnum.PROMOTION);
    }

    @ApiOperation("RPC-新用户注册福利")
    @PostMapping("/new_user_coupon")
    public Result addNewUserCoupon(
            @ApiParam("用户对象") @RequestBody CouponNewUserDTO couponNewUserDTO) {
        return couponService.initNewUserCoupon(couponNewUserDTO);
    }

}
