package com.goal.order.controller;

import com.alipay.api.internal.util.AlipaySignature;
import com.goal.enums.BizCodeEnum;
import com.goal.enums.order.PayTypeEnum;
import com.goal.order.component.strategy.AliPayConstants;
import com.goal.order.service.ProductOrderService;
import com.goal.utils.Result;
import io.swagger.annotations.Api;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Api("支付渠道回调")
@RestController
@RequestMapping("api/${app.config.api.version}/sale")
public class CallbackController {

    @Resource
    private ProductOrderService productOrderService;

    @Value("${alipay.config.alipay-public-key}")
    private String alipayPublicKey;



    private final SimpleDateFormat dateFormat = new SimpleDateFormat(AliPayConstants.DATE_FORMAT);

    /**
     * 支付信息回调
     * @param request
     * @return
     */
    @PostMapping("alipay/pay_notify")
    public String payNotify(HttpServletRequest request) {
        try {
            log.info("支付回调，消息接收 {}", request.getParameter(AliPayConstants.TRADE_STATUS));
            if (request.getParameter(AliPayConstants.TRADE_STATUS).equals(AliPayConstants.TRADE_SUCCESS)) {
                // 获取请求参数
                Map<String, String> params = new HashMap<>();
                Map<String, String[]> requestParameters = request.getParameterMap();
                for (String name : requestParameters.keySet()) {
                    params.put(name, request.getParameter(name));
                }

                String sign = params.get(AliPayConstants.SIGN);
                String content = AlipaySignature.getSignCheckContentV1(params);
                boolean checkSignature = AlipaySignature.rsa256CheckContent(content, sign,
                        alipayPublicKey, "UTF-8");

                // 支付宝验签通过
                if (checkSignature) {
                    Result result = productOrderService.handlerOrderCallbackMsg(PayTypeEnum.ALIPAY.name(), params);
                    if (result.getCode() == BizCodeEnum.OPS_SUCCESS.getCode()) {
                        return "success";
                    }
                }

            }

        } catch (Exception e) {
            log.error("支付回调，处理失败", e);
        }
        return "failure";
    }


}
