package com.tangl.heartbeatai.controller;


import com.tangl.heartbeatai.common.Result;
import com.tangl.heartbeatai.dto.ChatWordsRequest;
//import com.tangl.heartbeatai.entity.TbOperationLog;
import com.tangl.heartbeatai.entity.TbOperationLog;
import com.tangl.heartbeatai.enums.OperationTypeEnum;
import com.tangl.heartbeatai.service.ChatWordsService;

import com.tangl.heartbeatai.service.TbOperationLogService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j; // 先加日志注解（后续步骤会用）
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 话术生成控制器（重构：简化日志调用）
 */
@Slf4j
@RestController
@RequestMapping("/api/chat")
public class ChatWordsController {

    @Autowired
    private ChatWordsService chatWordsService;

    @Autowired
    private TbOperationLogService operationLogService;

    @PostMapping("/generate-words")
    public Result<String> generateWords(@Validated @RequestBody ChatWordsRequest request) {
        log.info("接收到话术生成请求：{}", request);
        try {
            // 1. 核心业务逻辑
            String words = chatWordsService.generateChatWords(request);
            Result<String> successResult = Result.success("话术生成成功", words);

            // 2. 保存操作日志（重构后：一行代码完成日志保存）
            operationLogService.saveOperationLog(
                    request.getUserId(),
                    OperationTypeEnum.GENERATE_WORDS,
                    request,
                    successResult,
                    true // 操作成功
            );

            return successResult;
        } catch (Exception e) {
            log.error("话术生成失败", e);
            Result<String> errorResult = Result.error("话术生成失败：" + e.getMessage());

            // 3. 保存失败日志
            operationLogService.saveOperationLog(
                    request.getUserId(),
                    OperationTypeEnum.GENERATE_WORDS,
                    request,
                    errorResult,
                    false // 操作失败
            );

            return errorResult;
        }
    }
}