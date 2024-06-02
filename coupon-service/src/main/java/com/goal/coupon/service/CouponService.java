package com.goal.coupon.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.goal.coupon.domain.po.Coupon;
import com.baomidou.mybatisplus.extension.service.IService;
import com.goal.utils.Result;

import java.util.List;

/**
* @author Goal
* @description 针对表【coupon】的数据库操作Service
* @createDate 2024-06-02 16:39:50
*/
public interface CouponService extends IService<Coupon> {

    /**
     * 分页查询优惠券
     * @param page 当前页
     * @param size 每页记录数
     * @return 查询结果
     */
    Result pageCoupon(int page, int size);
}
