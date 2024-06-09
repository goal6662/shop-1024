package com.goal.coupon.domain.po;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import java.util.Date;
import lombok.Data;

/**
 * 
 * @TableName coupon_task
 */
@TableName(value ="coupon_task")
@Data
public class CouponTask implements Serializable {
    /**
     * 
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 优惠券记录id
     */
    private Long couponRecordId;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 订单号
     */
    private String outTradeNo;

    /**
     * 锁定状态 锁定LOCK-完成FINISH 取消CANCEL
     */
    private String lockState;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}