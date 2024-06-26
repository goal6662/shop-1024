package com.goal.order.controller;

import cn.hutool.http.ContentType;
import com.goal.constant.CacheKey;
import com.goal.enums.BizCodeEnum;
import com.goal.enums.order.ClientTypeEnum;
import com.goal.enums.order.PayTypeEnum;
import com.goal.order.domain.dto.OrderConfirmDTO;
import com.goal.order.service.ProductOrderService;
import com.goal.utils.CommonUtil;
import com.goal.utils.Result;
import com.goal.utils.UserContext;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;

@Slf4j
@Api("订单模块")
@RestController
@RequestMapping("api/${app.config.api.version}/order")
public class ProductOrderController {

    @Resource
    private ProductOrderService productOrderService;

    @ApiOperation("查询订单状态")
    @GetMapping("query_state/{out_trade_no}")
    public Result<String> getState(@ApiParam("订单号") @PathVariable("out_trade_no") String outTradeNo) {
       return productOrderService.getOrderStateByOutTradeNo(outTradeNo);
    }

    @ApiOperation("获取提交订单令牌")
    @GetMapping("get_token")
    public Result<String> getOrderToken() {
        return productOrderService.getSubmitToken();
    }

    @ApiOperation("创建订单")
    @PostMapping("submit")
    public void submitOrder(
            @ApiParam("订单对象") @RequestBody OrderConfirmDTO orderConfirmDTO,
            HttpServletResponse response
            ) {
        Result result = productOrderService.submitOrder(orderConfirmDTO);

        if (Result.isSuccess(result)) {
            String client = orderConfirmDTO.getClientType();
            String payType = orderConfirmDTO.getPayType();

            // 判断支付方式
            if (payType.equalsIgnoreCase(PayTypeEnum.ALIPAY.name())) {
                log.info("创建支付宝订单成功：{}", result.getData());

                if (client.equalsIgnoreCase(ClientTypeEnum.WEB.name())) {
                    // 写回html数据
                    writeData(response, result);
                } else {
                    // TODO: 2024/6/6 APP处理
                }
            }

        } else {
            log.error("创建订单失败: {}", result);
        }
    }

    private void writeData(HttpServletResponse response, Result result) {

        try {
            response.setContentType(ContentType.build(ContentType.TEXT_HTML.name(), StandardCharsets.UTF_8));
            PrintWriter writer = response.getWriter();

            writer.write(result.getData().toString());
            writer.flush();
            writer.close();

        } catch (IOException e) {
            log.error(e.getMessage());
        }

    }

}
