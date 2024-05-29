package com.goal.user.controller;

import com.goal.user.common.RedisConstants;
import com.goal.utils.CommonUtil;
import com.google.code.kaptcha.Producer;
import io.swagger.annotations.Api;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.bind.annotation.RequestMapping;
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
public class ValidateController {

    @Resource
    private Producer captchaProducer;

    @Resource(name = "stringRedisTemplate")
    private StringRedisTemplate redisTemplate;

    @RequestMapping("captcha")
    public void getCaptcha(HttpServletRequest request, HttpServletResponse response) {

        // 获取验证码文本
        String kaptchaProducerText = captchaProducer.createText();
        log.info("图形验证码：{}", kaptchaProducerText);

        // 缓存验证码
        redisTemplate.opsForValue().set(getCaptchaKey(request), kaptchaProducerText,
                RedisConstants.USER_CAPTCHA_KEY_TTL, TimeUnit.MILLISECONDS);

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
     * @param request
     * @return
     */
    private String getCaptchaKey(HttpServletRequest request) {

        String ipAddr = CommonUtil.getIpAddr(request);
        String userAgent = request.getHeader("User-Agent");

        String key = RedisConstants.USER_CAPTCHA_KEY + CommonUtil.MD5(ipAddr + userAgent);
        log.info("User-Agent={}", userAgent);
        log.info("IP={}", ipAddr);
        log.info("缓存验证码, key：{}", key);

        return key;
    }

}
