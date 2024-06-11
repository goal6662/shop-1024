package com.goal.order.feign;

import com.goal.utils.Result;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient("user-service")
public interface UserFeignService {

    @GetMapping("api/${app.config.api.version}/address/find/{id}")
    Result detail(@PathVariable("id") Long id);

}
