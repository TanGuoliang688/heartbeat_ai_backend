package com.tangl.heartbeatai.service.impl;

import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import com.tangl.heartbeatai.dto.ChatWordsRequest;
import com.tangl.heartbeatai.service.AiChatService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

/**
 * DeepSeek实现类
 */
@Service("deepSeekAiChatService")
@Slf4j
public class DeepSeekAiChatServiceImpl implements AiChatService {

    @Value("${ai.deepseek.api-key}")
    private String apiKey;

    @Value("${ai.deepseek.base-url}")
    private String baseUrl;

    @Value("${ai.deepseek.model-name}")
    private String modelName;

    @Override
    public String generateChatWords(ChatWordsRequest request) {
        // 构造Prompt
        String prompt = String.format(
                "你是一个专业的恋爱情感助手，请根据对方的消息：【%s】，按照【%s】的风格生成3条回复话术，每条话术标注沟通逻辑，格式要求：\n" +
                        "1. 话术内容 - 沟通逻辑：xxx\n" +
                        "2. 话术内容 - 沟通逻辑：xxx\n" +
                        "3. 话术内容 - 沟通逻辑：xxx\n" +
                        "要求话术自然、贴合年轻人聊天习惯，避免生硬模板。",
                request.getTargetMessage(), request.getStyle()
        );

        // 构造DeepSeek请求体
        JSONObject requestBody = new JSONObject();
        requestBody.put("model", modelName);
        JSONArray messages = new JSONArray();
        JSONObject message = new JSONObject();
        message.put("role", "user");
        message.put("content", prompt);
        messages.add(message);
        requestBody.put("messages", messages);
        requestBody.put("temperature", 0.7);
        requestBody.put("max_tokens", 1000);

        // 调用DeepSeek API
        String url = baseUrl + "/chat/completions";
        try (HttpResponse response = HttpRequest.post(url)
                .header("Content-Type", "application/json")
                .header("Authorization", "Bearer " + apiKey)
                .body(requestBody.toJSONString())
                .timeout(10000)
                .execute()) {

            String result = response.body();
            log.info("DeepSeek响应结果：{}", result);
            JSONObject json = JSON.parseObject(result);

            if (json.containsKey("choices")) {
                JSONArray choices = json.getJSONArray("choices");
                JSONObject choice = choices.getJSONObject(0);
                return choice.getJSONObject("message").getString("content");
            } else {
                log.error("DeepSeek调用失败，响应：{}", result);
                return "生成话术失败，请稍后再试";
            }

        } catch (Exception e) {
            log.error("DeepSeek生成话术异常", e);
            return "生成话术失败，请稍后再试";
        }
    }
}