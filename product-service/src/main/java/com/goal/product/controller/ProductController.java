package com.goal.product.controller;

import com.goal.product.domain.vo.ProductVO;
import com.goal.product.service.ProductService;
import com.goal.utils.Result;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

@RestController
@RequestMapping("api/${app.config.api.version}/product")
public class ProductController {

    @Resource
    private ProductService productService;


    @ApiOperation("查询商品详情")
    @GetMapping("find/{product_id}")
    public Result<ProductVO> findDetail(
            @ApiParam(value = "商品id", required = true)
            @PathVariable("product_id") long productId
    ) {
        ProductVO productVO = productService.findDetailById(productId);
        return Result.success(productVO);
    }

    @ApiOperation("分页查询商品")
    @GetMapping("page_product")
    public Result pageProduct(
            @ApiParam("当前页") @RequestParam(value = "page", defaultValue = "1") int page,
            @ApiParam("每页记录数") @RequestParam(value = "size", defaultValue = "30") int size
    ) {
        return productService.pageProduct(page, size);
    }

}
