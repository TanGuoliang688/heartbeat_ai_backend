package com.tangl.heartbeatai.util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;

/**
 * JWT工具类（核心：生成token、解析token、校验token有效性）
 * 适配你的用户体系：
 * 1. token载荷包含userId（关联tb_user.id）
 * 2. 匿名用户默认userId=0
 * 3. 兼容你的拦截器/Service逻辑
 */
@Slf4j
@Component // 交给Spring容器管理，可通过@Autowired注入
public class JwtUtil {
    /**
     * JWT密钥（必须≥32位，生产环境建议配置在环境变量中，避免硬编码）
     * 配置在application.yml中，通过@Value注入
     */
    @Value("${jwt.secret:heartbeatai20260108secretkey1234567890abcdef123}")
    private String secret;

    /**
     * token过期时间：2小时（单位：毫秒），可通过yml配置调整
     */
    @Value("${jwt.expire:7200000}")
    private long expireTime;

    /**
     * 生成JWT token（登录成功后返回给前端）
     * @param userId 用户ID（tb_user.id，匿名用户=0）
     * @param phone 原始手机号（用于载荷存储，非脱敏）
     * @return 加密后的JWT token字符串
     */
    public String generateToken(Long userId, String phone) {
        try {
            // 1. 生成加密密钥（基于HMAC-SHA256算法，密钥必须≥32位）
            SecretKey key = Keys.hmacShaKeyFor(secret.getBytes());

            // 2. 构建token：设置载荷、签发时间、过期时间、签名
            return Jwts.builder()
                    // 自定义载荷（核心：用户ID，用于后续解析）
                    .claim("userId", userId)
                    .claim("phone", phone) // 可选：存储手机号，便于日志排查
                    // 签发时间
                    .setIssuedAt(new Date())
                    // 过期时间（当前时间 + 过期时长）
                    .setExpiration(new Date(System.currentTimeMillis() + expireTime))
                    // 签名加密（防止token被篡改）
                    .signWith(key)
                    // 压缩为字符串
                    .compact();
        } catch (Exception e) {
            log.error("生成JWT token失败", e);
            throw new RuntimeException("生成登录凭证失败，请重试");
        }
    }

    /**
     * 解析token，获取用户ID（拦截器/接口中获取登录用户ID）
     * @param token 前端传递的token（需先截取Bearer前缀）
     * @return 用户ID（匿名用户返回0）
     */
    public Long getUserIdFromToken(String token) {
        try {
            // 解析token获取载荷
            Claims claims = parseToken(token);
            // 从载荷中获取用户ID（Long类型）
            return claims.get("userId", Long.class);
        } catch (Exception e) {
            log.error("解析token获取用户ID失败，返回匿名用户ID=0", e);
            return 0L; // 匹配你的匿名用户规则
        }
    }

    /**
     * 校验token是否有效（未过期、未被篡改）
     * @param token 前端传递的token
     * @return true=有效，false=无效/过期/篡改
     */
    public boolean validateToken(String token) {
        try {
            Claims claims = parseToken(token);
            // 校验是否过期：过期时间 > 当前时间 = 有效
            return !claims.getExpiration().before(new Date());
        } catch (Exception e) {
            log.error("token无效或已过期", e);
            return false;
        }
    }

    /**
     * 私有方法：解析token获取完整载荷（内部复用）
     */
    private Claims parseToken(String token) {
        // 生成和解析必须使用相同的密钥
        SecretKey key = Keys.hmacShaKeyFor(secret.getBytes());
        // 解析token：验证签名 → 检查过期 → 返回载荷
        return Jwts.parserBuilder()
                .setSigningKey(key) // 设置签名密钥
                .build()
                .parseClaimsJws(token) // 解析token（签名无效会抛异常）
                .getBody(); // 获取载荷
    }
}