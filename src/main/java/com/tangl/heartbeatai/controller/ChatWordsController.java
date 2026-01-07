package com.tangl.heartbeatai.controller;


import com.tangl.heartbeatai.common.Result;
import com.tangl.heartbeatai.dto.ChatWordsRequest;
import com.tangl.heartbeatai.service.ChatWordsService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j; // 先加日志注解（后续步骤会用）
import org.springframework.util.StringUtils;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/chat")
@Tag(name = "聊天话术接口", description = "生成恋爱聊天话术")
@Slf4j // 新增：日志注解
public class ChatWordsController {

    private final ChatWordsService chatWordsService;

    public ChatWordsController(ChatWordsService chatWordsService) {
        this.chatWordsService = chatWordsService;
    }

    @PostMapping("/generate-words")
    @Operation(summary = "生成聊天话术", description = "根据对方消息和风格生成恋爱话术")
    // 关键修改：添加@Validated注解，启用参数校验
    public Result<String> generateChatWords(@Validated @RequestBody ChatWordsRequest request) {
        // 1. 日志：打印入参
        log.info("生成话术请求参数：targetMessage={}, style={}, userId={}",
                request.getTargetMessage(), request.getStyle(), request.getUserId());

        // 2. 移除所有手动if校验，改为默认值设置
        if (!StringUtils.hasText(request.getStyle())) {
            request.setStyle("温柔");
            log.info("风格为空，默认设置为：温柔");
        }

        try {
            String result = chatWordsService.generateChatWords(request);
            log.info("生成话术成功，结果长度：{}", result.length());
            return Result.success("话术生成成功", result);
        } catch (Exception e) {
            log.error("生成话术失败", e);
            return Result.error("生成话术失败：" + e.getMessage());
        }
    }
}