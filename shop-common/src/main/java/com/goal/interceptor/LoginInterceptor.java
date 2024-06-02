package com.goal.interceptor;

import com.auth0.jwt.JWT;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.goal.domain.LoginUser;
import com.goal.enums.BizCodeEnum;
import com.goal.exception.BizException;
import com.goal.utils.JwtUtil;
import com.goal.utils.UserContext;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Slf4j
public class LoginInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {

        String accessToken = request.getHeader("token");

        // 解析校验
        DecodedJWT decoded;
        if (StringUtils.isBlank(accessToken) ||
                (decoded = JwtUtil.parseJwtToken(accessToken)) == null) {
            throw new BizException(BizCodeEnum.ACCOUNT_LOGIN_EXPIRED);
        }


        LoginUser loginUser = LoginUser.builder()
                .id(Long.valueOf(decoded.getSubject()))
                .headImg(decoded.getClaim("head_img").asString())
                .name(decoded.getClaim("name").asString())
                .mail(decoded.getClaim("mail").asString())
                .build();

        UserContext.setUser(loginUser);
        // 输出日志
        log.info("请求验证成功：{}", loginUser.getId());
        return true;
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
        HandlerInterceptor.super.postHandle(request, response, handler, modelAndView);
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        UserContext.clearUser();
        HandlerInterceptor.super.afterCompletion(request, response, handler, ex);
    }
}
