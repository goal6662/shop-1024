package com.goal.user.service;

import com.goal.enums.SendCodeEnum;
import com.goal.utils.Result;

public interface NotifyService {

    /**
     * 发送邮件
     * @param sendCodeEnum 邮件类型
     * @param to 接收邮箱
     * @return 发送结果
     */
    Result sendCode(SendCodeEnum sendCodeEnum, String to);
}
