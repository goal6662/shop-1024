package com.goal.coupon.mapper;

import com.goal.coupon.domain.po.Coupon;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;

/**
* @author Goal
* @description 针对表【coupon】的数据库操作Mapper
* @createDate 2024-06-02 16:39:50
* @Entity com.goal.coupon.domain.po.Coupon
*/
public interface CouponMapper extends BaseMapper<Coupon> {

    /**
     * 根据id、类型获取可用的优惠券
     * @param couponId 优惠券id
     * @param category 优惠券类型
     * @return
     */
    Coupon getAvailableCouponById(@Param("id") long couponId,
                                  @Param("category") String category);

    /**
     * 扣减优惠券库存
     * @param couponId 优惠券id
     * @return
     */
    int reduceStock(@Param("id") long couponId);
}




