package com.goal.user.component.impl;

import com.goal.user.component.MailService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class MailServiceImpl implements MailService {
    @Override
    public void sendMail(String to, String subject, String content) {
        log.warn("模拟发送邮件成功：\n" +
                "接收人：{}\n" +
                "主题：{}\n" +
                "内容：{}", to, subject, content);
    }
}
