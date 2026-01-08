package com.tangl.heartbeatai.util;

import eu.bitwalker.useragentutils.UserAgent;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import java.util.HashMap;
import java.util.Map;

/**
 * 日志工具类（重构核心：统一IP/设备解析，规范日志格式）
 */
@Slf4j
public class LogUtils {

    /**
     * 获取当前请求的HttpServletRequest对象
     */
    private static HttpServletRequest getRequest() {
        try {
            return ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
        } catch (Exception e) {
            log.warn("获取请求对象失败，可能为非Web环境");
            return null;
        }
    }

    /**
     * 获取用户真实IP（优先取X-Forwarded-For，兼容代理场景）
     */
    public static String getRealIp() {
        HttpServletRequest request = getRequest();
        if (request == null) {
            return "unknown";
        }
        String ip = request.getHeader("X-Forwarded-For");
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("Proxy-Client-IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("WL-Proxy-Client-IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }
        // 多代理场景下，取第一个非unknown的IP
        if (ip != null && ip.contains(",")) {
            ip = ip.split(",")[0].trim();
        }
        // 过滤本地回环地址
        return "127.0.0.1".equals(ip) || "0:0:0:0:0:0:0:1".equals(ip) ? "local" : ip;
    }

    /**
     * 解析设备信息（浏览器/系统/设备类型）
     */
    public static String parseDeviceInfo() {
        HttpServletRequest request = getRequest();
        if (request == null) {
            return "unknown";
        }
        // 获取User-Agent请求头
        String userAgentStr = request.getHeader("User-Agent");
        if (userAgentStr == null || userAgentStr.isEmpty()) {
            return "unknown";
        }
        UserAgent userAgent = UserAgent.parseUserAgentString(userAgentStr);
        // 拼接设备信息：浏览器类型 + 系统类型 + 设备类型
        String browser = userAgent.getBrowser().getName();
        String os = userAgent.getOperatingSystem().getName();
        String device = userAgent.getOperatingSystem().getDeviceType().getName();
        return String.format("%s | %s | %s", browser, os, device);
    }

    /**
     * 格式化操作内容为JSON字符串（请求参数+响应结果）
     * @param operationType 操作类型
     * @param requestParam 请求参数（DTO对象）
     * @param responseData 响应结果
     * @param isSuccess 操作是否成功
     * @return 标准化JSON字符串
     */
    public static String formatOperationContent(String operationType, Object requestParam, Object responseData, boolean isSuccess) {
        Map<String, Object> contentMap = new HashMap<>();
        contentMap.put("operationType", operationType);
        contentMap.put("requestParam", requestParam);
        contentMap.put("responseData", responseData);
        contentMap.put("isSuccess", isSuccess);
        contentMap.put("timestamp", System.currentTimeMillis());
        // 使用FastJSON格式化（确保引入fastjson依赖）
        return com.alibaba.fastjson2.JSON.toJSONString(contentMap);
    }
}
    