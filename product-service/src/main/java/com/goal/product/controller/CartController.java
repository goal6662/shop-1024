package com.goal.product.controller;

import com.goal.product.domain.dto.CartItemDTO;
import com.goal.product.service.CartService;
import com.goal.utils.Result;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

@Api("购物车服务")
@RestController
@RequestMapping("api/${app.config.api.version}/cart")
public class CartController {

    @Resource
    private CartService cartService;

    @ApiOperation("添加商品到购物车")
    @PostMapping("add")
    public Result addToCart(
            @ApiParam("商品信息")
            @RequestBody CartItemDTO cartItemDTO
            ) {
        cartService.addToCart(cartItemDTO);

        return Result.success();
    }

}
