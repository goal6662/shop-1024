package com.goal.domain.mq;

import lombok.Data;

import java.util.List;

/**
 * 删除购物项的消息
 */
@Data
public class CartClearMessage {

    private String messageId;

    /**
     * 订单号
     */
    private String outTradeNo;

    /**
     * 商品项 ID
     */
    private List<Long> productIdList;

    /**
     * 购物车缓存键
     */
    private String cartCacheKey;
}
