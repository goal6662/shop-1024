package com.goal.product.controller;

import com.goal.product.domain.vo.BannerVO;
import com.goal.product.service.BannerService;
import com.goal.utils.Result;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;

@Api("轮播图模块")
@RestController
@RequestMapping("api/${app.config.api.version}/banner")
public class BannerController {

    @Resource
    private BannerService bannerService;

    @ApiOperation("轮播图列表")
    @GetMapping("list")
    public Result<List<BannerVO>> list() {
        return bannerService.listAll();
    }

}
