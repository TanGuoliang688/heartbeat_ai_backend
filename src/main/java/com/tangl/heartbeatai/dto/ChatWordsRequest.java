package com.tangl.heartbeatai.dto;

import lombok.Data;

/**
 * 话术生成入参DTO
 */
@Data
public class ChatWordsRequest {
    /**
     * 对方的消息内容
     */
    private String targetMessage;
    /**
     * 期望风格：温柔/幽默/直球
     */
    private String style;
    /**
     * 用户ID
     */
    private Long userId;
}