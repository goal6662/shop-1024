package com.goal.user.controller;

import com.baomidou.mybatisplus.extension.api.R;
import com.goal.enums.BizCodeEnum;
import com.goal.user.domain.dto.UserRegisterDTO;
import com.goal.user.service.FileService;
import com.goal.user.service.UserService;
import com.goal.utils.Result;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;

@Api("用户模块")
@RestController
@RequestMapping("api/${app.config.api.version}/user")
public class UserController {

    @Resource
    private FileService fileService;

    @Resource
    private UserService userService;

    /**
     * 上传文件：默认最大大小为 1MB
     * @param file 头像文件
     * @return
     */
    @ApiOperation("用户头像上传")
    @PostMapping("upload")
    public Result<String> uploadUserImg(
            @ApiParam(value = "文件上传")
            @RequestPart("file") MultipartFile file) {

        String url = fileService.uploadFile(file);
        if (url != null) {
            return Result.success(url);
        }

        return Result.fail(BizCodeEnum.FILE_UPLOAD_ERROR);
    }


    /**
     * 接收JSON格式的数据
     * @param registerDTO
     * @return
     */
    @PostMapping("register")
    @ApiOperation("用户注册")
    public Result register(
            @ApiParam("用户注册对象")
            @RequestBody UserRegisterDTO registerDTO) {
        return userService.register(registerDTO);
    }
}
