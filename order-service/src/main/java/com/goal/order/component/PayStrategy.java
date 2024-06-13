package com.goal.order.component;

import com.goal.order.domain.vo.PayInfoVO;

public interface PayStrategy {

    /**
     * 下单
     * @param payInfoVO
     * @return
     */
    String unifiedOrder(PayInfoVO payInfoVO);

    /**
     * 退款
     * @param payInfoVO 支付信息
     * @return
     */
    default String refund(PayInfoVO payInfoVO) {
        return "";
    }

    /**
     * 查询支付状态
     * @param payInfoVO
     * @return
     */
    default String queryPaySuccess(PayInfoVO payInfoVO) {
        return "";
    }
}
