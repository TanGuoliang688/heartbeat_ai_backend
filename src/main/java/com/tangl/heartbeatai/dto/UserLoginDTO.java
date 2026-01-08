package com.tangl.heartbeatai.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

/**
 * 用户登录DTO（接收前端登录参数）
 * 核心：仅保留登录必要字段，校验规则与注册一致
 */
@Data
public class UserLoginDTO {
    /**
     * 手机号（规则与注册一致）
     */
    @NotBlank(message = "手机号不能为空")
    @Pattern(regexp = "^1[3-9]\\d{9}$", message = "手机号格式错误（需11位有效手机号）")
    private String phone;

    /**
     * 密码（仅做非空校验，格式校验在注册时已完成）
     */
    @NotBlank(message = "密码不能为空")
    private String password;
}