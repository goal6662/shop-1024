package com.goal.user.controller;

import com.goal.enums.BizCodeEnum;
import com.goal.exception.BizException;
import com.goal.user.domain.Address;
import com.goal.user.service.AddressService;
import com.goal.utils.Result;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

@Api("收货地址服务")
@RestController
@RequestMapping("api/${app.config.api.version}/addresses")
public class AddressController {

    @Resource
    private AddressService addressService;

    @ApiOperation("根据id查找地址详情")
    @GetMapping("find/{id}")
    public Result<Address> detail(
            @ApiParam(value = "地址id", required = true)
            @PathVariable Long id) {

        Address address = addressService.getById(id);

        return Result.success(address);
    }


}
