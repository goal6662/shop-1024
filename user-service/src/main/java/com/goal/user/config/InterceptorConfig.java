package com.goal.user.config;

import com.goal.interceptor.LoginInterceptor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Slf4j
@Configuration
public class InterceptorConfig implements WebMvcConfigurer {

    @Bean
    public LoginInterceptor loginInterceptor() {
        return new LoginInterceptor();
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {

        registry.addInterceptor(loginInterceptor())
                .addPathPatterns("/api/*/user/**", "/api/*/address/**")
                .excludePathPatterns(
                        "/api/*/user/send_code",
                        "/api/*/user/captcha",
                        "/api/*/user/register",
                        "/api/*/user/login",
                        "/api/*/user/upload");



    }
}
