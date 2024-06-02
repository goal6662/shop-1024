package com.goal.user.controller;

import com.baomidou.mybatisplus.extension.api.R;
import com.goal.domain.RefreshableToken;
import com.goal.enums.BizCodeEnum;
import com.goal.user.domain.dto.UserLoginDTO;
import com.goal.user.domain.dto.UserRegisterDTO;
import com.goal.user.domain.vo.UserVO;
import com.goal.user.service.FileService;
import com.goal.user.service.UserService;
import com.goal.utils.Result;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.util.Map;

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
     * @param registerDTO  使用json接收数据，后续数据变更仅需修改实体类
     * @return
     */
    @ApiOperation("用户注册")
    @PostMapping("register")
    public Result register(
            @ApiParam("用户注册对象")
            @RequestBody UserRegisterDTO registerDTO) {
        return userService.register(registerDTO);
    }


    @ApiOperation("用户登录")
    @PostMapping("login")
    public Result<String> login(
            @ApiParam("用户登录对象")
            @RequestBody UserLoginDTO loginDTO
            ) {
        return userService.login(loginDTO);
    }


    @ApiOperation("查看个人信息详情")
    @GetMapping("detail")
    public Result<UserVO> detail() {
        return userService.findUserDetail();
    }

    @ApiOperation("自动刷新token")
    @PostMapping("refresh_token")
    public Result<RefreshableToken> refreshToken(
            @ApiParam("刷新token")
            @RequestBody RefreshableToken token
            ) {
        return userService.refreshToken(token);
    }

}
