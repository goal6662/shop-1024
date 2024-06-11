package com.goal.coupon.service;

import com.goal.coupon.domain.dto.CouponNewUserDTO;
import com.goal.enums.coupon.CouponCategoryEnum;
import com.goal.coupon.domain.po.Coupon;
import com.baomidou.mybatisplus.extension.service.IService;
import com.goal.utils.Result;

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

    /**
     * 领取优惠券
     * @param couponId 优惠券id
     * @param couponCategoryEnum 优惠券类型
     * @return
     */
    Result addCoupon(long couponId, CouponCategoryEnum couponCategoryEnum);

    /**
     * 新用户注册发放优惠券
     * @param couponNewUserDTO
     * @return
     */
    Result initNewUserCoupon(CouponNewUserDTO couponNewUserDTO);
}
