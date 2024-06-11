package com.goal.coupon.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.goal.coupon.common.RedisConstant;
import com.goal.coupon.domain.dto.CouponNewUserDTO;
import com.goal.enums.coupon.CouponCategoryEnum;
import com.goal.enums.coupon.CouponRecordStatusEnum;
import com.goal.enums.coupon.CouponStatusEnum;
import com.goal.coupon.domain.po.Coupon;
import com.goal.coupon.domain.po.CouponRecord;
import com.goal.coupon.domain.vo.CouponVO;
import com.goal.coupon.mapper.CouponMapper;
import com.goal.coupon.mapper.CouponRecordMapper;
import com.goal.coupon.service.CouponService;
import com.goal.domain.LoginUser;
import com.goal.enums.BizCodeEnum;
import com.goal.exception.BizException;
import com.goal.utils.Result;
import com.goal.utils.UserContext;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
* @author Goal
* @description 针对表【coupon】的数据库操作Service实现
* @createDate 2024-06-02 16:39:50
*/
@Slf4j
@Service
public class CouponServiceImpl extends ServiceImpl<CouponMapper, Coupon>
    implements CouponService{

    @Resource
    private CouponMapper couponMapper;

    @Resource
    private CouponRecordMapper couponRecordMapper;

    @Autowired
    private StringRedisTemplate redisTemplate;

    @Resource
    private RedissonClient redissonClient;

    @Override
    public Result pageCoupon(int page, int size) {

        Page<Coupon> pageInfo = new Page<>(page, size);

        // 查询分页数据
        Page<Coupon> selectPage = couponMapper.selectPage(pageInfo,
                new LambdaQueryWrapper<Coupon>()
                        .eq(Coupon::getPublish, CouponStatusEnum.PUBLISH.name())   // 优惠券状态
                        .eq(Coupon::getCategory, CouponCategoryEnum.PROMOTION.name())    // 优惠券类型
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

    /**
     * 领取优惠券
     *  1. 判断优惠券是否存在
     *  2. 判断优惠券是否可以领取：优惠券有效期、状态、裤子、限制规则
     *  3. 扣减库存
     *  4. 保存领券记录
     * @param couponId 优惠券id
     * @param couponCategoryEnum 优惠券类型
     * @return
     */
    @Transactional(rollbackFor = Exception.class,   // 异常回滚
            propagation = Propagation.REQUIRED,     // 传播行为
            isolation = Isolation.REPEATABLE_READ   // 隔离级别
    )
    @Override
    public Result addCoupon(long couponId, CouponCategoryEnum couponCategoryEnum) {

        String lockKey = RedisConstant.COUPON_LOCK_KEY + couponId;      // 对优惠券加锁，减少锁的粒度

        RLock rLock = redissonClient.getLock(lockKey);
        rLock.lock();
        log.info("领券接口加锁成功：{}", Thread.currentThread().getId());
        try {
            // 1. 判断优惠券是否存在且可用，已判断: 处于发布状态、优惠券类型，处于有效期内，库存大于0
            Coupon coupon = couponMapper.getAvailableCouponById(couponId, couponCategoryEnum.name());
            if (coupon == null) {
                return Result.fail(BizCodeEnum.COUPON_UNAVAILABLE);
            }
            LoginUser loginUser = UserContext.getUser();
            Long userId = loginUser.getId();
            // 2. 用户是否有该优惠券的领取资格
            this.checkCoupon(coupon, userId);    // 无法领取。抛出异常
            CouponRecord couponRecord = new CouponRecord();
            BeanUtils.copyProperties(coupon, couponRecord);

            couponRecord.setCreateTime(new Date());
            couponRecord.setUseState(CouponRecordStatusEnum.NEW.name());
            couponRecord.setUserId(userId);
            couponRecord.setUserName(loginUser.getName());
            couponRecord.setCouponId(couponId);
            couponRecord.setId(null);       // 不能使用优惠券的id

            // 涉及多个数据库
            // 3. 扣减库存
            int rows = couponMapper.reduceStock(couponId);
            if (rows == 1) {
                // 4. 插入优惠券记录
                // 分布式锁，对用户可领取次数进行校验
                couponRecordMapper.insert(couponRecord);
            } else {
                log.warn("优惠券发放失败: {}, 用户: {}", coupon, loginUser);
                throw new BizException(BizCodeEnum.COUPON_NO_STOCK);
            }
            return Result.success();
        } finally {
            // 解锁
            rLock.unlock();
        }
    }

    /**
     * 新用户注册时并未登录
     *  1. 该接口不应该被拦截
     *  2. 调用时传入userId
     *  3. 设置在UserContext后调用领取方法
     *  4. 调用前需要判断用户是否为新用户
     * @param couponNewUserDTO
     * @return
     */
    @Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
    @Override
    public Result initNewUserCoupon(CouponNewUserDTO couponNewUserDTO) {
        LoginUser loginUser = new LoginUser();
        loginUser.setId(couponNewUserDTO.getUserId());
        loginUser.setName(couponNewUserDTO.getName());

        UserContext.setUser(loginUser);

        // 查询新用户有哪些优惠券可以领取：类别为新用户
        List<Coupon> couponList = couponMapper.listCouponByCategory(CouponCategoryEnum.NEW_USER.name());

        for (Coupon coupon : couponList) {
            // 幂等操作，调用需要加锁
            this.addCoupon(coupon.getId(), CouponCategoryEnum.NEW_USER);
        }


        return Result.success();
    }

    /**
     * 校验用户是否可用领取优惠券
     * @param coupon 优惠券
     * @param userId 用户ID
     */
    private void checkCoupon(Coupon coupon, Long userId) {

        // 1. 查询用户领取了多少张当前优惠券
        int recordCount = couponRecordMapper.getUserClaimCount(userId, coupon.getId());
        if (recordCount >= coupon.getUserLimit()) {
            throw new BizException(BizCodeEnum.COUPON_OUT_OF_LIMIT);
        }

    }

    /**
     * 转换为 VO
     * @param coupon
     * @return
     */
    private CouponVO transferToVO(Coupon coupon) {
        CouponVO couponVO = new CouponVO();
        BeanUtils.copyProperties(coupon, couponVO);
        return couponVO;
    }
}




