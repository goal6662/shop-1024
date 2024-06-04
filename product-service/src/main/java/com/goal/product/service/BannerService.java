package com.goal.product.service;

import com.goal.product.domain.po.Banner;
import com.baomidou.mybatisplus.extension.service.IService;
import com.goal.product.domain.vo.BannerVO;
import com.goal.utils.Result;

import java.util.List;

/**
* @author Goal
* @description 针对表【banner】的数据库操作Service
* @createDate 2024-06-04 19:12:59
*/
public interface BannerService extends IService<Banner> {

    /**
     * 查询所有轮播图
     * @return
     */
    Result<List<BannerVO>> listAll();
}
