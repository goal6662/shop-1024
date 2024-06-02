package com.goal.user.controller;

import com.baomidou.mybatisplus.extension.api.R;
import com.goal.user.domain.Address;
import com.goal.user.domain.dto.AddressAddDTO;
import com.goal.user.service.AddressService;
import com.goal.utils.Result;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

@Api("收货地址服务")
@RestController
@RequestMapping("api/${app.config.api.version}/address")
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

    @ApiOperation("新增收货地址")
    @PostMapping("add")
    public Result add(
            @ApiParam("地址信息")
            @RequestBody AddressAddDTO addressAddDTO
            ) {
        return addressService.add(addressAddDTO);
    }

}
