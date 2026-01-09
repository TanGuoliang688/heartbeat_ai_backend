package com.tangl.heartbeatai.config;

import com.tangl.heartbeatai.interceptor.LoginInterceptor;
import jakarta.annotation.Resource;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;


@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    @Resource
    private LoginInterceptor loginInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        // 1. 必须登录的接口（无有效Token直接401）
        registry.addInterceptor(loginInterceptor)
                .addPathPatterns("/api/user/**") // 需登录的核心接口
                .excludePathPatterns("/api/user/login", "/api/user/register"); // 登录/注册接口放行

        // 2. 允许匿名的接口（无Token设userId=0放行）
        registry.addInterceptor(loginInterceptor)
                .addPathPatterns("/api/chat/**");
    }
}