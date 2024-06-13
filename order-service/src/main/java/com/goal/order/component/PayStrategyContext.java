package com.goal.order.component;


import com.goal.order.domain.vo.PayInfoVO;

public class PayStrategyContext {

    private final PayStrategy payStrategy;

    public PayStrategyContext(PayStrategy payStrategy) {
        this.payStrategy = payStrategy;
    }

    /**
     * 下单
     * @param payInfoVO
     * @return
     */
    public String executeUnifiedOrder(PayInfoVO payInfoVO) {
        return payStrategy.unifiedOrder(payInfoVO);
    }

    /**
     * 查询支付状态
     * @param payInfoVO
     * @return
     */
    public String executeQueryPaySuccess(PayInfoVO payInfoVO) {
        return payStrategy.queryPaySuccess(payInfoVO);
    }

    /**
     * 退款
     * @param payInfoVO
     * @return
     */
    public String executeRefund(PayInfoVO payInfoVO) {
        return payStrategy.refund(payInfoVO);
    }

}
