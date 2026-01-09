package com.tangl.heartbeatai.interceptor;

import com.alibaba.fastjson2.JSON;
import com.tangl.heartbeatai.common.Result;
import com.tangl.heartbeatai.util.JwtUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import java.io.PrintWriter;

/**
 * 登录拦截器（适配匿名用户ID=0）
 * 核心逻辑：
 * 1. 无有效Token → 设匿名用户ID=0并放行；
 * 2. Token无效/过期 → 返回401；
 * 3. Token有效 → 解析用户ID并传递至后续接口。
 */
@Slf4j
@Component
public class LoginInterceptor implements HandlerInterceptor {

    // 常量提取：避免硬编码
    private static final String AUTH_HEADER = "Authorization";
    private static final String BEARER_PREFIX = "Bearer ";
    private static final String USER_ID_ATTR = "userId";
    private static final Long ANONYMOUS_USER_ID = 0L;
    private static final int TOKEN_START_INDEX = 7; // Bearer 后开始截取的索引

    private final JwtUtil jwtUtil;

    // 构造器注入（Spring推荐，避免@Autowired空指针）
    public LoginInterceptor(JwtUtil jwtUtil) {
        this.jwtUtil = jwtUtil;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        // 1. 获取并解析Authorization请求头
        String authHeader = request.getHeader(AUTH_HEADER);
        if (authHeader == null || !authHeader.startsWith(BEARER_PREFIX)) {
            // 匿名用户：设置userId=0并放行，记录DEBUG日志
            log.debug("请求头无有效Authorization，视为匿名用户访问，URI:{}", request.getRequestURI());
            request.setAttribute(USER_ID_ATTR, ANONYMOUS_USER_ID);
            return true;
        }

        // 2. 截取Token并校验非空
        String token = authHeader.substring(TOKEN_START_INDEX).trim();
        if (token.isEmpty()) {
            log.warn("Authorization头格式错误，Token为空，URI:{}", request.getRequestURI());
            returnErrorResponse(response, Result.error(401, "token格式错误，请重新登录"));
            return false;
        }

        // 3. 校验Token有效性
        if (jwtUtil == null) {
            log.error("JwtUtil注入失败，无法校验Token");
            returnErrorResponse(response, Result.error(500, "服务器内部错误"));
            return false;
        }
        if (!jwtUtil.validateToken(token)) {
            log.warn("Token无效/过期，URI:{}", request.getRequestURI());
            returnErrorResponse(response, Result.error(401, "token已过期，请重新登录"));
            return false;
        }

        // 4. 解析用户ID并传递
        Long userId = jwtUtil.getUserIdFromToken(token);
        if (userId == null) {
            log.warn("Token解析用户ID失败，URI:{}", request.getRequestURI());
            returnErrorResponse(response, Result.error(401, "token解析失败，请重新登录"));
            return false;
        }
        request.setAttribute(USER_ID_ATTR, userId);
        log.debug("Token校验通过，用户ID:{}，URI:{}", userId, request.getRequestURI());

        return true;
    }

    /**
     * 统一返回JSON格式的错误响应
     *
     * @param response 响应对象
     * @param result   错误结果体
     */
    private void returnErrorResponse(HttpServletResponse response, Result<?> result) {
        // 基础响应配置：必设，避免异常时状态码丢失
        response.setCharacterEncoding("UTF-8");
        response.setContentType("application/json; charset=utf-8");
        response.setStatus(HttpStatus.UNAUTHORIZED.value());

        // 写入响应体：简化异常处理，优先保证基础响应
        try (PrintWriter writer = response.getWriter()) {
            writer.write(JSON.toJSONString(result));
            writer.flush();
        } catch (Exception e) {
            log.error("写入错误响应失败", e);
            // 兜底：若JSON写入失败，返回简单文本
            try {
                response.getWriter().write("认证失败，请重新登录");
            } catch (Exception ex) {
                log.error("兜底响应写入失败", ex);
            }
        }
    }
}