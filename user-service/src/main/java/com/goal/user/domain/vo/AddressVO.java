package com.goal.user.domain.vo;

import lombok.Data;

import java.util.Date;

@Data
public class AddressVO {

    private Long id;

    /**
     * 用户id
     */
    private Long userId;

    /**
     * 是否默认收货地址：0->否；1->是
     */
    private Integer defaultStatus;

    /**
     * 收发货人姓名
     */
    private String receiveName;

    /**
     * 收货人电话
     */
    private String phone;

    /**
     * 省/直辖市
     */
    private String province;

    /**
     * 市
     */
    private String city;

    /**
     * 区
     */
    private String region;

    /**
     * 详细地址
     */
    private String detailAddress;

    /**
     *
     */
    private Date createTime;

}
