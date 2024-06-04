package com.goal.coupon.service;

import com.goal.coupon.domain.po.CouponRecord;
import com.baomidou.mybatisplus.extension.service.IService;
import com.goal.utils.Result;

/**
* @author Goal
* @description 针对表【coupon_record】的数据库操作Service
* @createDate 2024-06-02 16:39:50
*/
public interface CouponRecordService extends IService<CouponRecord> {

    /**
     * 分页查询用户个人优惠券
     * @param page 当前页
     * @param size 每页记录数
     * @return
     */
    Result pageUserCouponRecord(int page, int size);
}
