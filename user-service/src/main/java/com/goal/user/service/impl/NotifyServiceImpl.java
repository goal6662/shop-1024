package com.goal.user.service.impl;

import com.goal.enums.SendCodeEnum;
import com.goal.user.component.MailService;
import com.goal.user.service.NotifyService;
import com.goal.utils.CheckUtil;
import com.goal.utils.CommonUtil;
import com.goal.utils.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

@Slf4j
@Service
public class NotifyServiceImpl implements NotifyService {

    @Resource
    private MailService mailService;

    private static final String SUBJECT = "SHOP_1024验证码";
    private static final String CONTENT = "您的验证码是: [%s], 有效期60s";

    @Override
    public Result sendCode(SendCodeEnum sendCodeEnum, String to) {

        if (CheckUtil.isEmail(to)) {
            // 邮件验证码
            String content = String.format(CONTENT, CommonUtil.getRandomCode(6));
            mailService.sendMail(to, SUBJECT, content);
        } else if (CheckUtil.isPhone(to)) {
            // 短信验证码
        }

        return Result.success();
    }
}
