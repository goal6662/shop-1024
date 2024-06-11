package com.goal.order.domain.mq;

import com.goal.order.domain.dto.CartItemDTO;
import lombok.Data;

import java.util.List;

@Data
public class CartRecoveryMessage {

    /**
     * 购物项
     */
    private List<CartItemDTO> cartItemDTOList;

    /**
     * 购物车标识
     */
    private String cartCacheKey;

    /**
     * 订单号
     */
    private String outTradeNo;

}
