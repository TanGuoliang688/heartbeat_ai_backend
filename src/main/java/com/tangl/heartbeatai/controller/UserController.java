package com.tangl.heartbeatai.controller;

import com.tangl.heartbeatai.common.Result;
import com.tangl.heartbeatai.dto.UserLoginDTO;
import com.tangl.heartbeatai.dto.UserRegisterDTO;
import com.tangl.heartbeatai.service.TbUserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 用户控制器（登录/注册）
 * 关键：@Validated 注解触发DTO的参数校验
 */
@Slf4j
@RestController
@RequestMapping("/api/user")
public class UserController {

    @Autowired
    private TbUserService userService;

    /**
     * 用户注册
     *
     * @Validated：触发UserRegisterDTO的参数校验，失败则抛出MethodArgumentNotValidException
     * @RequestBody：接收JSON格式参数，映射到DTO
     */
    @PostMapping("/register")
    public Result<Void> register(@Validated @RequestBody UserRegisterDTO dto) {
        log.info("用户注册请求：手机号={}", dto.getPhone());
        return userService.register(dto);
    }

    /**
     * 用户登录
     */
    @PostMapping("/login")
    public Result<String> login(@Validated @RequestBody UserLoginDTO dto) {
        log.info("用户登录请求：手机号={}", dto.getPhone());
        return userService.login(dto);
    }

    @PostMapping("/logout")
    public Result<Void> logout() {
        return userService.logout();
    }
}