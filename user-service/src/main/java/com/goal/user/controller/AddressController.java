package com.goal.user.controller;

import com.goal.user.domain.dto.AddressAddDTO;
import com.goal.user.domain.vo.AddressVO;
import com.goal.user.service.AddressService;
import com.goal.utils.Result;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

@Api("收货地址服务")
@RestController
@RequestMapping("api/${app.config.api.version}/address")
public class AddressController {

    @Resource
    private AddressService addressService;

    @ApiOperation("根据id查找地址详情")
    @GetMapping("find/{id}")
    public Result<AddressVO> detail(
            @ApiParam(value = "地址id", required = true)
            @PathVariable Long id) {
        return addressService.getDetailById(id);
    }

    @ApiOperation("查询用户所有地址信息")
    @GetMapping("/list")
    public Result<List<AddressVO>> findAllAddress() {
        return addressService.findUserAllAddress();
    }

    @ApiOperation("新增收货地址")
    @PostMapping("add")
    public Result add(
            @ApiParam("地址信息")
            @RequestBody AddressAddDTO addressAddDTO
            ) {
        return addressService.add(addressAddDTO);
    }


    @ApiOperation("删除地址")
    @DeleteMapping("del/{id}")
    public Result delete(
            @ApiParam("地址ID")
            @PathVariable Long id
            ) {
        return addressService.deleteById(id);
    }


}
