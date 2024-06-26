package com.goal.user.controller;

import com.goal.constant.RedisConstant;
import com.goal.enums.BizCodeEnum;
import com.goal.enums.SendCodeEnum;
import com.goal.user.service.NotifyService;
import com.goal.utils.CommonUtil;
import com.goal.utils.Result;
import com.google.code.kaptcha.Producer;
import io.swagger.annotations.Api;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.imageio.ImageIO;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

/**
 * 验证码
 */
@Slf4j
@Api("验证码服务")
@RestController
@RequestMapping("api/${app.config.api.version}/user")
public class NotifyController {

    @Resource
    private Producer captchaProducer;

    @Resource(name = "stringRedisTemplate")
    private StringRedisTemplate redisTemplate;

    @Resource
    private NotifyService notifyService;

    @RequestMapping("captcha")
    public void getCaptcha(HttpServletRequest request, HttpServletResponse response) {

        // 获取验证码文本
        String kaptchaProducerText = captchaProducer.createText();
        log.info("图形验证码：{}", kaptchaProducerText);

        // 缓存验证码
        redisTemplate.opsForValue().set(getCaptchaKey(request), kaptchaProducerText,
                RedisConstant.USER_CAPTCHA_KEY_TTL, TimeUnit.MILLISECONDS);

        // 根据文本生成图形验证码
        BufferedImage bufferedImage = captchaProducer.createImage(kaptchaProducerText);
        try (ServletOutputStream outputStream = response.getOutputStream()){
            // 写回图形验证码
            ImageIO.write(bufferedImage, "jpg", outputStream);
            outputStream.flush();
        } catch (IOException e) {
            log.error("获取图形验证码异常：{}", e.getMessage());
        }
    }

    /**
     * Redis验证码的key
     */
    private String getCaptchaKey(HttpServletRequest request) {

        String ipAddr = CommonUtil.getIpAddr(request);
        String userAgent = request.getHeader("User-Agent");

        String key = RedisConstant.USER_CAPTCHA_KEY + CommonUtil.MD5(ipAddr + userAgent);
        log.info("User-Agent={}", userAgent);
        log.info("IP={}", ipAddr);
        log.info("缓存验证码, key：{}", key);

        return key;
    }


    /**
     * 要求：1、60s内不可重复发送 2、缓存发送的验证码
     * @param to 注册邮箱
     * @param captcha 图形验证码
     */
    @GetMapping("send_code")
    public Result sendRegisterCode(@RequestParam(value = "to") String to,
                                   @RequestParam(value = "captcha") String captcha,
                                   HttpServletRequest request) {

        String key = getCaptchaKey(request);

        // 获取缓存验证码
        String cacheCaptcha = redisTemplate.opsForValue().get(key);

        // 判断图形验证码是否一致
        if (StringUtils.isNoneBlank(captcha, cacheCaptcha) && captcha.equals(cacheCaptcha)) {
            // 成功
            // 删除图形验证码缓存
            redisTemplate.delete(key);

            // 发送验证码到邮箱
            return notifyService.sendCode(SendCodeEnum.USER_REGISTER, to);

        } else {
            return Result.fail(BizCodeEnum.CODE_CAPTCHA_ERROR);
        }

    }

}
