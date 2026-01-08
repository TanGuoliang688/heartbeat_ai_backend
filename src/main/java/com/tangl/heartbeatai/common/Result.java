package com.tangl.heartbeatai.common;

import lombok.Data;

/**
 * 全局统一响应结果封装类
 * 阶段2：完善成功响应、通用失败、默认响应等能力
 */
@Data
public class Result<T> {
    // ========== 通用状态码常量 ==========
    /**
     * 成功状态码
     */
    public static final int SUCCESS_CODE = 200;
    /**
     * 通用失败状态码
     */
    public static final int ERROR_CODE = 500;
    /**
     * 通用成功提示
     */
    public static final String SUCCESS_MSG = "操作成功";
    /**
     * 通用失败提示
     */
    public static final String ERROR_MSG = "操作失败";

    // ========== 响应核心字段 ==========
    /**
     * 响应状态码
     */
    private int code;
    /**
     * 响应提示信息
     */
    private String msg;
    /**
     * 响应数据体
     */
    private T data;

    // ========== 私有构造器（禁止外部直接实例化） ==========
    private Result(int code, String msg, T data) {
        this.code = code;
        this.msg = msg;
        this.data = data;
    }

    // ========== 阶段1已实现：自定义失败响应 ==========
    public static <T> Result<T> error(int code, String msg) {
        return new Result<>(code, msg, null);
    }

    // ========== 阶段2新增：通用响应方法 ==========

    /**
     * 通用成功响应（无数据）
     */
    public static <T> Result<T> success() {
        return new Result<>(SUCCESS_CODE, SUCCESS_MSG, null);
    }

    /**
     * 成功响应（带自定义提示）
     */
    public static <T> Result<T> success(String msg) {
        return new Result<>(SUCCESS_CODE, msg, null);
    }

    /**
     * 成功响应（带自定义数据）
     */
    public static <T> Result<T> success(T data) {
        return new Result<>(SUCCESS_CODE, SUCCESS_MSG, data);
    }

    /**
     * 成功响应（自定义提示+数据）
     */
    public static <T> Result<T> success(String msg, T data) {
        return new Result<>(SUCCESS_CODE, msg, data);
    }

    /**
     * 通用失败响应（默认500+通用提示）
     */
    public static <T> Result<T> error() {
        return new Result<>(ERROR_CODE, ERROR_MSG, null);
    }

    /**
     * 失败响应（仅自定义提示，默认500码）
     */
    public static <T> Result<T> error(String msg) {
        return new Result<>(ERROR_CODE, msg, null);
    }
}