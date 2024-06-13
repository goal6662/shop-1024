package com.goal.order.component.strategy;

import cn.hutool.json.JSONUtil;
import com.alipay.api.AlipayApiException;
import com.alipay.api.AlipayClient;
import com.alipay.api.request.AlipayTradePagePayRequest;
import com.alipay.api.request.AlipayTradeQueryRequest;
import com.alipay.api.request.AlipayTradeWapPayRequest;
import com.alipay.api.response.AlipayTradePagePayResponse;
import com.alipay.api.response.AlipayTradeQueryResponse;
import com.alipay.api.response.AlipayTradeWapPayResponse;
import com.goal.enums.BizCodeEnum;
import com.goal.enums.order.ClientTypeEnum;
import com.goal.exception.BizException;
import com.goal.order.component.PayStrategy;
import com.goal.order.domain.vo.PayInfoVO;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Service
public class AlipayStrategy implements PayStrategy {

    @Value("${alipay.config.notify-url}")
    private String notifyUrl;

    @Value("${alipay.config.return-url}")
    private String returnUrl;

    @Resource
    private AlipayClient alipayClient;

    /**
     * 下单
     *
     * @param payInfoVO
     * @return
     */
    @Override
    public String unifiedOrder(PayInfoVO payInfoVO) {
        // 下单
        String bizContent = createBizContent(payInfoVO);

        String clientType = payInfoVO.getClientType();
        String form = "";
        try {
            if (clientType.equalsIgnoreCase(ClientTypeEnum.APP.name())) {
                // H5 手机网页支付
                AlipayTradeWapPayRequest request = new AlipayTradeWapPayRequest();
                request.setBizContent(bizContent);
                request.setNotifyUrl(notifyUrl);
                request.setReturnUrl(returnUrl);

                AlipayTradeWapPayResponse payResponse = alipayClient.execute(request);
                if (payResponse.isSuccess()) {
                    form = payResponse.getBody();
                }
            } else if (clientType.equalsIgnoreCase(ClientTypeEnum.WEB.name())) {
                AlipayTradePagePayRequest request = new AlipayTradePagePayRequest();
                request.setBizContent(bizContent);
                request.setNotifyUrl(notifyUrl);
                request.setReturnUrl(returnUrl);

                AlipayTradePagePayResponse payResponse = alipayClient.pageExecute(request);
                if (payResponse.isSuccess()) {
                    form = payResponse.getBody();
                }
            }

        } catch (AlipayApiException e) {
            log.error("支付宝构建{}表单异常：{}", clientType, e.toString());

        } finally {
            if (StringUtils.isBlank(form)) {
                log.error("支付宝构建{}表单失败", clientType);
            } else {
                log.info("支付宝构建{}表单成功", clientType);
            }
        }

        return form;
    }

    @Override
    public String queryPaySuccess(PayInfoVO payInfoVO) {

        // 查询已支付未通知订单
        // 1. 创建订单请求
        AlipayTradeQueryRequest request = new AlipayTradeQueryRequest();
        Map<String, String> content = new HashMap<>();
        // 订单号
        content.put("out_trade_no", payInfoVO.getOutTradeNo());
        // 1.1 添加请求信息
        request.setBizContent(JSONUtil.toJsonStr(content));

        // 2. 发送查询请求
        AlipayTradeQueryResponse response = null;
        try {
            response = alipayClient.execute(request);
        } catch (AlipayApiException e) {
            log.error("查询订单支付状态异常：{}", e.toString());
        }

        // 3. 返回订单状态
        if (response != null && response.isSuccess()) {
            String tradeStatus = response.getTradeStatus();
            log.error("查询订单支付状态成功：{}，状态：{}", payInfoVO, tradeStatus);
            return tradeStatus;
        } else {
            log.error("查询订单支付状态失败：{}", payInfoVO);
            return "";
        }
    }

    /**
     * 请求数据
     * @param payInfoVO
     * @return
     */
    private String createBizContent(PayInfoVO payInfoVO) {
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

        // 剩余支付时间：分钟，向下取整
        double timeout = Math.floor(payInfoVO.getOrderPayTimeout() / (1000.0 * 60));
        if (timeout < 1) {
            throw new BizException(BizCodeEnum.PAY_ORDER_TIMEOUT);
        }
        content.put("timeout_express", Double.valueOf(timeout).intValue() + "m");

        return JSONUtil.toJsonStr(content);
    }
}
