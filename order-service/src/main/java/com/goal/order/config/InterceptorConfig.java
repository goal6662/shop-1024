package com.goal.order.config;

import com.goal.interceptor.LoginInterceptor;
import feign.RequestInterceptor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import javax.servlet.http.HttpServletRequest;

@Configuration
public class InterceptorConfig implements WebMvcConfigurer {

    @Bean
    public LoginInterceptor loginInterceptor() {
        return new LoginInterceptor();
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(loginInterceptor())
                .addPathPatterns("/api/*/order/**")
                .excludePathPatterns(
                        "/api/*/callback/**",   // 回调接口
                        "/api/*/order/query_state/*"     // 定时任务：查询订单状态
                );
    }


    @Bean
    public RequestInterceptor userInfoRequestInterceptor() {
        return requestTemplate -> {

            // 从请求上下文获取请求
            // 一个请求对应一个线程
            ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder
                    .getRequestAttributes();

            // 获取设置token
            if (attributes != null) {
                HttpServletRequest request = attributes.getRequest();
                String token = request.getHeader("token");
                requestTemplate.header("token", token);
            }

        };
    }

}
