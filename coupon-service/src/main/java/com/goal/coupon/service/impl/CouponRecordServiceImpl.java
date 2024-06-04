package com.goal.coupon.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.goal.coupon.domain.po.CouponRecord;
import com.goal.coupon.domain.vo.CouponRecordVO;
import com.goal.coupon.service.CouponRecordService;
import com.goal.coupon.mapper.CouponRecordMapper;
import com.goal.utils.Result;
import com.goal.utils.UserContext;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Map;
import java.util.stream.Collectors;

/**
* @author Goal
* @description 针对表【coupon_record】的数据库操作Service实现
* @createDate 2024-06-02 16:39:50
*/
@Service
public class CouponRecordServiceImpl extends ServiceImpl<CouponRecordMapper, CouponRecord>
    implements CouponRecordService{

    @Resource
    private CouponRecordMapper couponRecordMapper;


    @Override
    public Result pageUserCouponRecord(int page, int size) {

        // 获取当前用户
        Long userId = UserContext.getUser().getId();

        // 1. 封装分页信息
        Page<CouponRecord> pageInfo = new Page<>(page, size);

        Page<CouponRecord> selectPage = couponRecordMapper.selectPage(pageInfo,
                new LambdaQueryWrapper<CouponRecord>()
                        .eq(CouponRecord::getUserId, userId)
                        .orderByDesc(CouponRecord::getCreateTime));

        // 封装分页返回结果
        Map<String, Object> pageMap = Map.of(
                "total_record", selectPage.getTotal(),
                "total_page", selectPage.getPages(),
                "current_data", selectPage.getRecords()
                        .stream().map((this::transferToVO)).collect(Collectors.toList())
        );

        return Result.success(pageMap);
    }

    private CouponRecordVO transferToVO(CouponRecord couponRecord) {
        CouponRecordVO couponRecordVO = new CouponRecordVO();
        BeanUtils.copyProperties(couponRecord, couponRecordVO);
        return couponRecordVO;
    }
}




