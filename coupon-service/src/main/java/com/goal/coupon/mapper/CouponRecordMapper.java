package com.goal.coupon.mapper;

import com.goal.coupon.domain.po.CouponRecord;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;

/**
* @author Goal
* @description 针对表【coupon_record】的数据库操作Mapper
* @createDate 2024-06-02 16:39:50
* @Entity com.goal.coupon.domain.po.CouponRecord
*/
public interface CouponRecordMapper extends BaseMapper<CouponRecord> {

    /**
     * 获取用户领取某优惠券的个数
     * @param userId 用户id
     * @param couponId 优惠券id
     * @return 领取个数
     */
    int getUserClaimCount(@Param("userId") Long userId,
                          @Param("couponId") Long couponId);
}




