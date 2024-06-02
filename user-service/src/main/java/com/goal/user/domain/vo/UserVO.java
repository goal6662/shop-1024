package com.goal.user.domain.vo;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class UserVO {

    private Long id;

    private String name;

    @JsonProperty("head_img")
    private String headImg;

    private String slogan;

    /**
     * 0: 女
     * 1: 男
     */
    private Integer sex;

}
