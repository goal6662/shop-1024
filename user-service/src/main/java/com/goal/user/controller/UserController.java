package com.goal.user.controller;

import com.goal.enums.BizCodeEnum;
import com.goal.user.service.FileService;
import com.goal.utils.Result;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiParam;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;

@Api("用户模块")
@RestController
@RequestMapping("api/${app.config.api.version}/user")
public class UserController {

    @Resource
    private FileService fileService;

    /**
     * 上传文件：默认最大大小为 1MB
     * @param file 头像文件
     * @return
     */
    @PostMapping("upload")
    public Result uploadUserImg(
            @ApiParam(value = "文件上传")
            @RequestPart("file") MultipartFile file) {

        String url = fileService.uploadFile(file);
        if (url != null) {
            return Result.success(url);
        }

        return Result.fail(BizCodeEnum.FILE_UPLOAD_ERROR);
    }


}
