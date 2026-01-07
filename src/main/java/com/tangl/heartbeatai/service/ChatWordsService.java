package com.tangl.heartbeatai.service;

import com.tangl.heartbeatai.dto.ChatWordsRequest;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;


/**
 * 业务层：调用统一AI接口（无需关心具体模型）
 */
@Service
public class ChatWordsService {

    // 注入动态选择的AI实现类
    @Resource(name = "aiChatService")
    private AiChatService aiChatService;

    /**
     * 生成话术（统一入口，模型切换无感知）
     */
    public String generateChatWords(ChatWordsRequest request) {
        return aiChatService.generateChatWords(request);
    }
}