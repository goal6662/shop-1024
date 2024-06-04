package com.goal.product.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.goal.product.domain.po.Banner;
import com.goal.product.domain.vo.BannerVO;
import com.goal.product.service.BannerService;
import com.goal.product.mapper.BannerMapper;
import com.goal.utils.Result;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.stream.Collectors;

/**
* @author Goal
* @description 针对表【banner】的数据库操作Service实现
* @createDate 2024-06-04 19:12:59
*/
@Service
public class BannerServiceImpl extends ServiceImpl<BannerMapper, Banner>
    implements BannerService {

    @Resource
    private BannerMapper bannerMapper;

    @Override
    public Result<List<BannerVO>> listAll() {
        List<Banner> bannerList = bannerMapper.selectList(new LambdaQueryWrapper<Banner>()
                .orderByAsc(Banner::getWeight));

        List<BannerVO> bannerVOList = bannerList.stream().map((banner -> {
            BannerVO bannerVO = new BannerVO();
            BeanUtils.copyProperties(banner, bannerVO);
            return bannerVO;
        })).collect(Collectors.toList());


        return Result.success(bannerVOList);
    }
}




