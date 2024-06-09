package com.goal.coupon.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.goal.coupon.domain.dto.CouponLockDTO;
import com.goal.coupon.domain.enums.CouponRecordStatusEnum;
import com.goal.coupon.domain.enums.CouponTaskStatusEnum;
import com.goal.coupon.domain.po.CouponRecord;
import com.goal.coupon.domain.po.CouponTask;
import com.goal.coupon.domain.vo.CouponRecordVO;
import com.goal.coupon.mapper.CouponTaskMapper;
import com.goal.coupon.service.CouponRecordService;
import com.goal.coupon.mapper.CouponRecordMapper;
import com.goal.enums.BizCodeEnum;
import com.goal.exception.BizException;
import com.goal.utils.Result;
import com.goal.utils.UserContext;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
* @author Goal
* @description 针对表【coupon_record】的数据库操作Service实现
* @createDate 2024-06-02 16:39:50
*/
@Slf4j
@Service
public class CouponRecordServiceImpl extends ServiceImpl<CouponRecordMapper, CouponRecord>
    implements CouponRecordService{

    @Resource
    private CouponRecordMapper couponRecordMapper;

    @Resource
    private CouponTaskMapper couponTaskMapper;


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

    @Override
    public CouponRecordVO findById(long recordId) {
        Long userId = UserContext.getUser().getId();

        LambdaQueryWrapper<CouponRecord> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(CouponRecord::getId, recordId)
                .eq(CouponRecord::getUserId, userId)
                .orderByDesc(CouponRecord::getCreateTime);

        CouponRecord couponRecord = couponRecordMapper.selectOne(wrapper);
        if (couponRecord == null) {
            return null;
        }

        return transferToVO(couponRecord);
    }

    /**
     * 锁定优惠券记录
     * task表插入记录
     * 发送延迟消息
     *    前提：使用的优惠券记录数目 == 锁定的优惠券记录数目 == task任务数目
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result lockCouponRecords(CouponLockDTO couponLockDTO) {

        // 1. 获取信息
        Long userId = UserContext.getUser().getId();
        String orderOutTradeNo = couponLockDTO.getOrderOutTradeNo();
        List<Long> lockCouponRecordIds = couponLockDTO.getLockCouponRecordIds();

        // 2. 锁定优惠券记录
        int updateRow = couponRecordMapper.changeRecordStateBatch(userId, CouponRecordStatusEnum.NEW.name(),
                CouponRecordStatusEnum.USED.name(), lockCouponRecordIds);

        // 3. task表插入记录
        List<CouponTask> couponTaskList = lockCouponRecordIds.stream().map((id) -> {
            CouponTask couponTask = new CouponTask();

            // 赋值
            couponTask.setCouponRecordId(id);
            couponTask.setLockState(CouponTaskStatusEnum.LOCK.name());
            couponTask.setOutTradeNo(orderOutTradeNo);
            couponTask.setCreateTime(new Date());

            return couponTask;
        }).collect(Collectors.toList());
        int insertRows = couponTaskMapper.insertBatch(couponTaskList);
        log.info("锁定优惠券记录：{}", updateRow);
        log.info("新增优惠券记录Task：{}", insertRows);

        // 4. 发送延迟消息
        if (lockCouponRecordIds.size() == updateRow && updateRow == insertRows) {
            // TODO: 2024/6/9 执行正常，发送延迟消息

            return Result.success();
        } else {
            // 抛出异常用于回滚数据
            throw new BizException(BizCodeEnum.COUPON_RECORD_LOCK_FAIL);
        }
    }

    private CouponRecordVO transferToVO(CouponRecord couponRecord) {
        CouponRecordVO couponRecordVO = new CouponRecordVO();
        BeanUtils.copyProperties(couponRecord, couponRecordVO);
        return couponRecordVO;
    }
}




