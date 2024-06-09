package com.goal.coupon.feign;

import com.goal.utils.Result;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient("order-service")
public interface OrderFeignService {


    @GetMapping("api/${app.config.api.version}/order/query_state/{out_trade_no}")
    Result<String> queryProductOrderState(@PathVariable("out_trade_no") String outTradeNo);

}
