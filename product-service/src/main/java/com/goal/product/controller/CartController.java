package com.goal.product.controller;

import com.goal.product.domain.dto.CartItemDTO;
import com.goal.product.domain.vo.CartItemVO;
import com.goal.product.domain.vo.CartVO;
import com.goal.product.service.CartService;
import com.goal.utils.Result;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

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

    @ApiOperation("添加商品到购物车")
    @PostMapping("mq/add/{user_id}")
    public Result mqAddToCart(
            @ApiParam("商品信息")
            @RequestBody List<CartItemDTO> cartItemDTOList,
            @ApiParam("用户ID")
            @PathVariable("user_id") Long userId) {

        cartService.addItemsToCart(cartItemDTOList, userId);
        return Result.success();
    }


    @ApiOperation("清空购物车")
    @DeleteMapping("clear")
    public Result clearCart() {
        cartService.clearUserCart();
        return Result.success();
    }


    @ApiOperation("查看我的购物车")
    @GetMapping("/my_cart")
    public Result findMyCart() {
        CartVO cartVO = cartService.getMyCart();

        return Result.success(cartVO);
    }


    @ApiOperation("删除购物项")
    @DeleteMapping("delete/{product_id}")
    public Result deleteItem(
            @ApiParam("商品ID") @PathVariable("product_id") Long productId
    ) {
        cartService.deleteItemById(productId);
        return Result.success();
    }


    @ApiOperation("修改购物项")
    @PutMapping("change")
    public Result changeItem(
            @ApiParam("购物项") @RequestBody CartItemDTO cartItemDTO
    ) {
        cartService.changeItemNum(cartItemDTO);
        return Result.success();
    }



    @ApiOperation("查询商品最新价格，并从购物车移除商品")
    @PostMapping("/confirm_order_cart_items")
    Result<List<CartItemVO>> confirmOrderCartItem(
            @ApiParam("商品ID") @RequestBody List<Long> productIdList) {
        List<CartItemVO> cartItemVOList = cartService.confirmOrderCartItems(productIdList);
        return Result.success(cartItemVOList);
    }
}
