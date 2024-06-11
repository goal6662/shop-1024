package com.goal.order.domain.dto;

import lombok.Data;

import java.util.List;

/**
 * 订单使用的优惠券记录
 */
@Data
public class CouponLockDTO {

    /**
     * 优惠券记录ID
     */
    private List<Long> lockCouponRecordIds;

    /**
     * 订单号
     */
    private String orderOutTradeNo;

}
