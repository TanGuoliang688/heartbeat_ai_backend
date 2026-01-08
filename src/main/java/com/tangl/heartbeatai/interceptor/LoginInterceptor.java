package com.tangl.heartbeatai.interceptor;

import com.alibaba.fastjson2.JSON;
import com.tangl.heartbeatai.common.Result;
import com.tangl.heartbeatai.util.JwtUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import java.io.PrintWriter;

/**
 * 登录拦截器（适配匿名用户ID=0）
 */
@Slf4j
@Component
public class LoginInterceptor implements HandlerInterceptor {

    @Autowired
    private JwtUtil jwtUtil;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        // 1. 获取token（请求头：Authorization，格式：Bearer {token}）
        String authHeader = request.getHeader("Authorization");
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            // 无token，设置匿名用户ID=0
            request.setAttribute("userId", 0L);
            return true; // 放行，不拦截（改为匿名访问）
        }

        String token = authHeader.substring(7); // 截取Bearer后的token

        // 2. 校验token有效性
        if (!jwtUtil.validateToken(token)) {
            // token无效/过期，返回401
            returnErrorResponse(response, Result.error(401, "token已过期，请重新登录"));
            return false;
        }

        // 3. 解析用户ID，存入request属性
        Long userId = jwtUtil.getUserIdFromToken(token);
        request.setAttribute("userId", userId);

        return true;
    }

    /**
     * 返回错误响应（JSON格式）
     */
    private void returnErrorResponse(HttpServletResponse response, Result<?> result) {
        response.setCharacterEncoding("UTF-8");
        response.setContentType("application/json; charset=utf-8");
        response.setStatus(HttpStatus.UNAUTHORIZED.value());
        try (PrintWriter writer = response.getWriter()) {
            writer.write(JSON.toJSONString(result));
        } catch (Exception e) {
            log.error("返回错误响应失败", e);
        }
    }
}