package com.goal.coupon.service;

import com.goal.coupon.domain.dto.CouponLockDTO;
import com.goal.coupon.domain.po.CouponRecord;
import com.baomidou.mybatisplus.extension.service.IService;
import com.goal.coupon.domain.vo.CouponRecordVO;
import com.goal.domain.CouponRecordMessage;
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

    /**
     * 查询用户个人优惠券记录
     * @param recordId 优惠券记录ID
     * @return 用户的优惠券记录
     */
    CouponRecordVO findById(long recordId);

    /**
     * 锁定优惠券信息
     * @param couponLockDTO
     */
    Result lockCouponRecords(CouponLockDTO couponLockDTO);

    /**
     * 释放优惠券记录
     * @param couponRecordMessage 消息对象
     * @return true: 消息处理成功、false: 消息处理失败
     */
    boolean releaseCouponRecord(CouponRecordMessage couponRecordMessage);
}
