package com.tangl.heartbeatai.common;

import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.BindException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * 全局异常处理器
 * 捕获所有Controller层的异常，统一返回Result格式，避免前端收到500页面
 */
@RestControllerAdvice // 全局拦截Controller的异常
@Slf4j
public class GlobalExceptionHandler {

    /**
     * 捕获所有运行时异常（最常用）
     */
    @ExceptionHandler(RuntimeException.class)
    public Result<String> handleRuntimeException(RuntimeException e) {
        log.error("运行时异常", e); // 打印完整异常栈，方便排查
        return Result.error("系统异常，请稍后再试：" + e.getMessage());
    }

    /**
     * 捕获空指针异常（单独处理，更精准）
     */
    @ExceptionHandler(NullPointerException.class)
    public Result<String> handleNullPointerException(NullPointerException e) {
        log.error("空指针异常", e);
        return Result.error(500, "数据异常，请检查输入内容");
    }

    /**
     * 捕获所有通用异常（兜底）
     */
    @ExceptionHandler(Exception.class)
    public Result<String> handleException(Exception e) {
        log.error("通用异常", e);
        return Result.error(500, "服务器内部错误");
    }

    // 在GlobalExceptionHandler中新增方法
    /**
     * 捕获参数校验异常（@Validated注解触发）
     */
    @ExceptionHandler(BindException.class)
    public Result<String> handleBindException(BindException e) {
        // 获取第一个校验错误的字段和提示
        FieldError fieldError = e.getBindingResult().getFieldError();
        String msg = fieldError != null ? fieldError.getDefaultMessage() : "参数格式错误";
        log.warn("参数校验失败：{}", msg);
        return Result.error(400, msg);
    }
}