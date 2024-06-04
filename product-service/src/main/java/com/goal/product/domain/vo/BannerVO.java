package com.goal.product.domain.vo;

import lombok.Data;

@Data
public class BannerVO {

    private Integer id;

    /**
     * 图片
     */
    private String img;

    /**
     * 跳转地址
     */
    private String url;

    /**
     * 权重
     */
    private Integer weight;

}
