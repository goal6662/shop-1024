package com.goal.order.component;

import com.goal.enums.order.PayTypeEnum;
import com.goal.order.component.strategy.AlipayStrategy;
import com.goal.order.domain.vo.PayInfoVO;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

// TODO: 2024/6/13 有种奇怪的感觉
@Component
public class PayFactory {

    @Resource
    private AlipayStrategy alipayStrategy;

    public String pay(PayInfoVO payInfoVO) {

        String payType = payInfoVO.getPayType();

        if (payType.equalsIgnoreCase(PayTypeEnum.ALIPAY.name())) {
            PayStrategyContext payStrategyContext = new PayStrategyContext(alipayStrategy);

            return payStrategyContext.executeUnifiedOrder(payInfoVO);
        }

        return null;
    }

}





