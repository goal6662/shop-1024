package com.goal.order.component.strategy;

import com.goal.order.component.PayStrategy;
import com.goal.order.domain.vo.PayInfoVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@Service
public class AlipayStrategy implements PayStrategy {

    /**
     * 下单
     * @param payInfoVO
     * @return
     */
    @Override
    public String unifiedOrder(PayInfoVO payInfoVO) {
        // TODO: 2024/6/12 下单
        Map<String, String> content = new HashMap<>();
        // 订单号
        content.put("out_trade_no", payInfoVO.getOutTradeNo());
        // 固定信息
        content.put("product_code", "FAST_INSTANT_TRADE_PAY");

        // 总金额
        content.put("total_amount", payInfoVO.getPayFee().toString());
        // 商品标题
        content.put("subject", payInfoVO.getTitle());
        // 商品描述
        content.put("body", payInfoVO.getDescription());
        content.put("timeout_express", payInfoVO.getOrderPayTimeout().toString());



        return null;
    }
}
