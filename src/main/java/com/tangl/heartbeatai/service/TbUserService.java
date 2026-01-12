package com.tangl.heartbeatai.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.tangl.heartbeatai.common.Result;
import com.tangl.heartbeatai.dto.UserLoginDTO;
import com.tangl.heartbeatai.dto.UserRegisterDTO;
import com.tangl.heartbeatai.entity.TbUser;

/**
 * 用户Service接口（适配tb_user表）
 * 规范：
 * 1. 继承MyBatis-Plus的IService，获得通用CRUD方法（无需重复编写增删改查）
 * 2. 自定义业务方法（注册/登录），明确接口契约
 */
public interface TbUserService extends IService<TbUser> {

    /**
     * 用户注册
     * @param dto 注册参数
     * @return 统一响应结果
     */
    Result<Void> register(UserRegisterDTO dto);

    /**
     * 用户登录（返回JWT token）
     * @param dto 登录参数
     * @return 包含token的响应结果
     */
    Result<String> login(UserLoginDTO dto);

    /**
     * 用户登出
     */
    Result<Void> logout();

    /**
     * 根据原始手机号查询用户（登录/校验用）
     * @param phone 原始手机号（未脱敏）
     * @return 用户实体
     */
    TbUser getUserByPhone(String phone);
}