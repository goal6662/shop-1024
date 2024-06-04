package com.goal.coupon.domain.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

@Data
public class CouponRecordVO {

    private Long id;

    /**
     * 优惠券id
     */
    private Long couponId;

    /**
     * 创建时间获得时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd hh:mm:ss", locale = "zh", timezone = "GMT+8") // 时间格式
    private Date createTime;

    /**
     * 使用状态  可用 NEW,已使用USED,过期 EXPIRED;
     */
    private String useState;

    /**
     * 优惠券标题
     */
    private String couponTitle;

    /**
     * 开始时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd hh:mm:ss", locale = "zh", timezone = "GMT+8") // 时间格式
    private Date startTime;

    /**
     * 结束时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd hh:mm:ss", locale = "zh", timezone = "GMT+8") // 时间格式
    private Date endTime;

    /**
     * 抵扣价格
     */
    private BigDecimal price;

    /**
     * 满多少才可以使用
     */
    private BigDecimal conditionPrice;

}
