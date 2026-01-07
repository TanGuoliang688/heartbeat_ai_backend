package com.tangl.heartbeatai.common;

import lombok.Data;

/**
 * 全局统一返回格式
 * 所有接口都返回该类型，前端可统一解析code/msg/data
 */
@Data
public class Result<T> {
    // 响应码：200成功 | 400参数错误 | 401未登录 | 403无权限 | 500系统错误
    private int code;
    // 响应信息（成功/失败提示）
    private String msg;
    // 响应数据（泛型，支持任意类型）
    private T data;

    // 手动添加构造方法
    public Result() {
    }

    public Result(int code, String msg, T data) {
        this.code = code;
        this.msg = msg;
        this.data = data;
    }

    // ========== 静态构造方法（简化调用） ==========
    // 成功：无数据
    public static <T> Result<T> success() {
        return new Result<>(200, "操作成功", null);
    }

    // 成功：带数据
    public static <T> Result<T> success(T data) {
        return new Result<>(200, "操作成功", data);
    }

    // 成功：自定义提示+数据
    public static <T> Result<T> success(String msg, T data) {
        return new Result<>(200, msg, data);
    }

    // 失败：默认提示
    public static <T> Result<T> error() {
        return new Result<>(500, "操作失败", null);
    }

    // 失败：自定义提示
    public static <T> Result<T> error(String msg) {
        return new Result<>(500, msg, null);
    }

    // 失败：自定义码+提示
    public static <T> Result<T> error(int code, String msg) {
        return new Result<>(code, msg, null);
    }
}