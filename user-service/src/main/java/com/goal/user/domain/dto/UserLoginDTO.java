package com.goal.user.domain.dto;

import io.swagger.annotations.ApiModel;
import lombok.Data;

@Data
@ApiModel("登录对象")
public class UserLoginDTO {

    private String mail;

    private String pwd;

}
