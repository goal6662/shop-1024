package com.goal.order.domain.po;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import lombok.Data;

/**
 * 
 * @TableName product_order
 */
@TableName(value ="product_order")
@Data
public class ProductOrder implements Serializable {
    /**
     * 
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 订单唯一标识
     */
    private String outTradeNo;

    /**
     * NEW 未支付订单,PAY已经支付订单,CANCEL超时取消订单
     */
    private String state;

    /**
     * 订单生成时间
     */
    private Date createTime;

    /**
     * 订单总金额
     */
    private BigDecimal totalPrice;

    /**
     * 订单实际支付价格
     */
    private BigDecimal payPrice;

    /**
     * 支付类型，微信-银行-支付宝
     */
    private String payType;

    /**
     * 昵称
     */
    private String nickname;

    /**
     * 头像
     */
    private String headImg;

    /**
     * 用户id
     */
    private Long userId;

    /**
     * 0表示未删除，1表示已经删除
     */
    private Integer del;

    /**
     * 更新时间
     */
    private Date updateTime;

    /**
     * 订单类型 DAILY普通单，PROMOTION促销订单
     */
    private String orderType;

    /**
     * 收货地址 json存储
     */
    private String receiverAddress;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}