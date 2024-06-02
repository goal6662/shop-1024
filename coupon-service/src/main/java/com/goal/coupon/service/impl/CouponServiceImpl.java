package com.goal.coupon.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.goal.coupon.domain.enums.CouponCategoryEnum;
import com.goal.coupon.domain.enums.CouponStatusEnum;
import com.goal.coupon.domain.po.Coupon;
import com.goal.coupon.domain.vo.CouponVO;
import com.goal.coupon.service.CouponService;
import com.goal.coupon.mapper.CouponMapper;
import com.goal.utils.Result;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
* @author Goal
* @description 针对表【coupon】的数据库操作Service实现
* @createDate 2024-06-02 16:39:50
*/
@Service
public class CouponServiceImpl extends ServiceImpl<CouponMapper, Coupon>
    implements CouponService{

    @Resource
    private CouponMapper couponMapper;

    @Override
    public Result pageCoupon(int page, int size) {

        Page<Coupon> pageInfo = new Page<>(page, size);

        // 查询分页数据
        Page<Coupon> selectPage = couponMapper.selectPage(pageInfo,
                new LambdaQueryWrapper<Coupon>()
                        .eq(Coupon::getPublish, CouponStatusEnum.PUBLISH)   // 优惠券状态
                        .eq(Coupon::getCategory, CouponCategoryEnum.PROMOTION)    // 优惠券类型
                        .orderByDesc(Coupon::getCreateTime));   // 后发布的在前

        // 封装分页返回结果
        Map<String, Object> pageMap = Map.of(
                "total_record", selectPage.getTotal(),
                "total_page", selectPage.getPages(),
                "current_data", selectPage.getRecords()
                        .stream().map((this::transferToVO)).collect(Collectors.toList())
        );

        return Result.success(pageMap);
    }

    private CouponVO transferToVO(Coupon coupon) {
        CouponVO couponVO = new CouponVO();
        BeanUtils.copyProperties(coupon, couponVO);
        return couponVO;
    }
}




