package com.goal.coupon.mapper;

import com.goal.coupon.domain.po.CouponTask;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
* @author Goal
* @description 针对表【coupon_task】的数据库操作Mapper
* @createDate 2024-06-09 11:18:21
* @Entity com.goal.coupon.domain.po.CouponTask
*/
public interface CouponTaskMapper extends BaseMapper<CouponTask> {

    /**
     * 批量插入
     * @param couponTaskList
     * @return
     */
    int insertBatch(@Param("couponTaskList") List<CouponTask> couponTaskList);
}




