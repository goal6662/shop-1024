package com.goal.order.feign;

import com.goal.order.domain.dto.ProductLockDTO;
import com.goal.order.domain.vo.CartItemVO;
import com.goal.utils.Result;
import io.swagger.annotations.ApiParam;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

@FeignClient("product-service")
public interface ProductCartFeignService {

    @PostMapping("api/${app.config.api.version}/cart/confirm_order_cart_items")
    Result<List<CartItemVO>> confirmOrderCartItem(@RequestBody List<Long> productIdList);

    @PostMapping("api/${app.config.api.version}/product/lock_products")
    Result lockProducts(@RequestBody ProductLockDTO productLockDTO);
}
