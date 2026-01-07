package com.tangl.heartbeatai.service;

import com.tangl.heartbeatai.dto.ChatWordsRequest;
/**
 * AI话术生成统一接口
 */
public interface AiChatService {
    /**
     * 生成恋爱聊天话术
     *
     * @param request 入参（对方消息、风格、用户ID）
     * @return 生成的话术内容
     */
    String generateChatWords(ChatWordsRequest request);
}