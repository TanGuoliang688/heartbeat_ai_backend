package com.tangl.heartbeatai.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

/**
 * 用户注册DTO（接收前端注册参数，含JSR380参数校验）
 * 作用：
 * 1. 仅接收注册所需字段（手机号/密码/确认密码），避免接收冗余字段；
 * 2. 前端参数校验（格式/非空），减少Service层校验逻辑；
 * 3. 与Entity隔离，避免直接暴露数据库字段。
 */
@Data
public class UserRegisterDTO {
    /**
     * 手机号（11位数字，仅允许13/14/15/16/17/18/19开头）
     * NotBlank：非空校验
     * Pattern：格式正则校验
     */
    @NotBlank(message = "手机号不能为空")
    @Pattern(regexp = "^1[3-9]\\d{9}$", message = "手机号格式错误（需11位有效手机号）")
    private String phone;

    /**
     * 密码（6-20位，必须包含字母+数字，避免弱密码）
     */
    @NotBlank(message = "密码不能为空")
    @Pattern(regexp = "^(?=.*[a-zA-Z])(?=.*\\d).{6,20}$", message = "密码需6-20位，且包含字母和数字")
    private String password;

    /**
     * 确认密码（与密码一致）
     */
    @NotBlank(message = "确认密码不能为空")
    private String confirmPassword;
}