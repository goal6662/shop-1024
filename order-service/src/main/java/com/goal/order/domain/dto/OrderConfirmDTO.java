package com.goal.order.domain.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
public class OrderConfirmDTO {

    /**
     * 购物车使用的优惠券
     *  注意：传空或者小于0，表示不使用优惠券
     */
    private Long couponRecordId;

    /**
     * 商品ID列表
     *  传入ID，购买数量从购物车获取
     */
    private List<Long> productIdList;

    /**
     * 支付方式
     */
    private String payType;

    /**
     * 端类型
     */
    private String clientType;

    /**
     * 地址ID
     */
    private Long addressId;

    /**
     * 总价
     *  前后端都需要验价
     */
    private BigDecimal totalPrice;

    /**
     * 实际支付价格
     *  使用优惠券折扣后的价格，不使用优惠券时 = totalPrice
     */
    private BigDecimal realPayPrice;

    /**
     * 防止重复提交的令牌
     */
    private String token;

}
