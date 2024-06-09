package com.goal.coupon.controller;

import com.goal.coupon.domain.dto.CouponLockDTO;
import com.goal.coupon.domain.vo.CouponRecordVO;
import com.goal.coupon.service.CouponRecordService;
import com.goal.enums.BizCodeEnum;
import com.goal.utils.Result;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.web.bind.annotation.*;

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

    @ApiOperation("查询优惠券记录详情")
    @GetMapping("detail/{record_id}")
    public Result<CouponRecordVO> getCouponRecordDetail(
            @ApiParam("优惠券记录ID") @PathVariable("record_id") long recordId
    ) {
        CouponRecordVO couponRecordVO = couponRecordService.findById(recordId);
        return couponRecordVO == null ?
                Result.fail(BizCodeEnum.COUPON_NOT_EXIST) : Result.success(couponRecordVO);
    }

    @ApiOperation("RPC-锁定优惠券记录")
    @PostMapping("lock_record")
    public Result lockCouponRecords(
            @ApiParam("锁定优惠券信息") @RequestBody CouponLockDTO couponLockDTO
            ) {
        return couponRecordService.lockCouponRecords(couponLockDTO);
    }

}
