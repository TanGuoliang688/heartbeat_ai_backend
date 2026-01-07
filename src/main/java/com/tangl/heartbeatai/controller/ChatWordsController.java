package com.tangl.heartbeatai.controller;


import com.tangl.heartbeatai.dto.ChatWordsRequest;
import com.tangl.heartbeatai.service.ChatWordsService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 聊天话术接口
 */
@RestController
@RequestMapping("/api/chat")
@Tag(name = "聊天话术接口", description = "生成恋爱聊天话术")
public class ChatWordsController {

    private final ChatWordsService chatWordsService;

    public ChatWordsController(ChatWordsService chatWordsService) {
        this.chatWordsService = chatWordsService;
    }

    @PostMapping("/generate-words")
    @Operation(summary = "生成聊天话术", description = "根据对方消息和风格生成恋爱话术")
    public String generateChatWords(@RequestBody ChatWordsRequest request) {
        return chatWordsService.generateChatWords(request);
    }
}