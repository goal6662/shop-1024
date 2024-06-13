package com.goal.user.service.impl;

import com.goal.constant.RedisConstant;
import com.goal.enums.BizCodeEnum;
import com.goal.enums.SendCodeEnum;
import com.goal.user.component.MailService;
import com.goal.user.service.NotifyService;
import com.goal.utils.CheckUtil;
import com.goal.utils.CommonUtil;
import com.goal.utils.Result;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
public class NotifyServiceImpl implements NotifyService {

    @Autowired
    private StringRedisTemplate redisTemplate;

    @Resource
    private MailService mailService;

    private static final String SUBJECT = "SHOP_1024-注册验证码";
    private static final String CONTENT = "您的验证码是: [%s], 有效期5min";

    @Override
    public Result sendCode(SendCodeEnum sendCodeEnum, String to) {

        String registerKey = sendCodeEnum.getCacheKey(to);

        String cacheCode = redisTemplate.opsForValue().get(registerKey);

        // 判断是否在 60s 内重复发送
        if (StringUtils.isNotBlank(cacheCode)) {
            long ttl = Long.parseLong(cacheCode.split("_")[1]);

            long interval = CommonUtil.getCurrentTimeStamp() - ttl;
            if (interval < 1000 * 60) {
                log.warn("重复发送验证码，时间间隔：{}s", interval / 1000);
                return Result.fail(BizCodeEnum.CODE_LIMITED);
            }
        }

        // 邮件验证码
        String code = CommonUtil.getRandomCode(6) + "_" + CommonUtil.getCurrentTimeStamp();

        // 缓存验证码
        String content = String.format(CONTENT, code);
        redisTemplate.opsForValue().set(registerKey, code,
                RedisConstant.USER_REGISTER_KEY_TTL, TimeUnit.MILLISECONDS);

        if (CheckUtil.isEmail(to)) {
            // 发送验证码
            mailService.sendMail(to, SUBJECT, content);
        } else if (CheckUtil.isPhone(to)) {
            // 短信验证码

        }

        return Result.success();
    }

    @Override
    public boolean checkCode(SendCodeEnum sendCodeEnum, String to, String code) {
        String cacheKey = sendCodeEnum.getCacheKey(to);

        String cacheValue = redisTemplate.opsForValue().get(cacheKey).split("_")[0];

        if (StringUtils.isNotBlank(cacheValue) && code.equals(cacheValue)) {
            // 验证码匹配
            redisTemplate.delete(cacheKey);
            return true;
        }

        return false;
    }
}
