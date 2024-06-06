package com.goal.order.controller;

import cn.hutool.http.ContentType;
import com.goal.enums.BizCodeEnum;
import com.goal.enums.order.ClientTypeEnum;
import com.goal.enums.order.PayTypeEnum;
import com.goal.order.domain.dto.OrderConfirmDTO;
import com.goal.order.service.ProductOrderService;
import com.goal.utils.Result;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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


    @ApiOperation("创建订单")
    @PostMapping("submit")
    public void submitOrder(
            @ApiParam("订单对象") @RequestBody OrderConfirmDTO orderConfirmDTO,
            HttpServletResponse response
            ) {
        Result result = productOrderService.submitOrder(orderConfirmDTO);

        if (result.getCode() == BizCodeEnum.OPS_SUCCESS.getCode()) {
            String client = orderConfirmDTO.getClientType();
            String payType = orderConfirmDTO.getPayType();

            // 判断支付方式
            if (payType.equalsIgnoreCase(PayTypeEnum.ALIPAY.name())) {
                log.info("创建支付宝订单成功：{}", orderConfirmDTO);

                if (client.equalsIgnoreCase(ClientTypeEnum.APP.name())) {
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
