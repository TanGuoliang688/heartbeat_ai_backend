package com.tangl.heartbeatai.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;


/**
 * 话术生成请求DTO
 * 添加JSR380注解，自动校验参数
 */
@Data
public class ChatWordsRequest {
    /**
     * 对方消息（必填）
     */
    @NotBlank(message = "对方消息不能为空") // 非空且非空白
    private String targetMessage;

    /**
     * 回复风格（默认温柔）
     */
    private String style;

    /**
     * 用户ID（必填，且为正数）
     */
    @NotNull(message = "用户ID不能为空") // 不能为null
    @Positive(message = "用户ID必须为正数") // 必须大于0
    private Long userId; // 建议改为Long（适配数据库bigint）

    // 确保存在该字段
    @NotBlank(message = "输入内容不能为空")
    private String inputContent;
}