package com.goal.user.domain.dto;

import lombok.Data;

@Data
public class UserRegisterDTO {

    private String name;

    private String pwd;

    private String headImg;

    private String slogan;

    /**
     * 性别
     *  0：女
     *  1：男
     */
    private Integer sex;

    private String mail;

    private String code;

}
