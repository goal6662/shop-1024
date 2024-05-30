package com.goal.user.component;

public interface MailService {

    /**
     * 发送邮件
     * @param to 接收邮箱
     * @param subject 邮件主题
     * @param content 邮件内容
     */
    void sendMail(String to, String subject, String content);
}
