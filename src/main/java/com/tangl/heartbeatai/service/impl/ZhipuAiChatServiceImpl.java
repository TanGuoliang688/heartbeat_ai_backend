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
 * 智谱AI（GLM）免费版实现类（兼容多模型切换架构）
 */
@Service("zhipuAiChatService")
@Slf4j
public class ZhipuAiChatServiceImpl implements AiChatService {

    @Value("${ai.zhipu.api-key}")
    private String apiKey;

    // 智谱AI免费版接口地址（固定）
    private static final String API_URL = "https://open.bigmodel.cn/api/paas/v4/chat/completions";

    @Override
    public String generateChatWords(ChatWordsRequest request) {
        // 1. 构造Prompt提示词（和之前保持一致）
        String prompt = String.format(
                "你是一个专业的恋爱情感助手，请根据对方的消息：【%s】，按照【%s】的风格生成3条回复话术，每条话术标注沟通逻辑，格式要求：\n" +
                        "1. 话术内容 - 沟通逻辑：xxx\n" +
                        "2. 话术内容 - 沟通逻辑：xxx\n" +
                        "3. 话术内容 - 沟通逻辑：xxx\n" +
                        "要求话术自然、贴合年轻人聊天习惯，避免生硬模板。",
                request.getTargetMessage(), request.getStyle()
        );

        // 2. 构造智谱AI请求体（标准OpenAI格式，和DeepSeek兼容）
        JSONObject requestBody = new JSONObject();
        // 免费版模型名称：glm-3-turbo（必填）
        requestBody.put("model", "glm-3-turbo");
        // 消息列表
        JSONArray messages = new JSONArray();
        JSONObject message = new JSONObject();
        message.put("role", "user");
        message.put("content", prompt);
        messages.add(message);
        requestBody.put("messages", messages);
        // 可选参数：控制随机性和长度
        requestBody.put("temperature", 0.7);
        requestBody.put("max_tokens", 1000);

        try (HttpResponse response = HttpRequest.post(API_URL)
                .header("Content-Type", "application/json")
                .header("Authorization", "Bearer " + apiKey) // 鉴权方式：Bearer + api-key
                .body(requestBody.toJSONString())
                .timeout(15000) // 超时时间15秒
                .execute()) {

            String result = response.body();
            log.info("智谱AI响应结果：{}", result);
            JSONObject json = JSON.parseObject(result);

            // 3. 解析响应结果
            // 错误处理：智谱AI的错误格式
            if (json.containsKey("error")) {
                String errorMsg = json.getJSONObject("error").getString("message");
                log.error("智谱AI调用失败：{}", errorMsg);
                return getFallbackWords(request.getStyle()); // 降级话术
            }

            // 正常解析
            if (json.containsKey("choices")) {
                JSONArray choices = json.getJSONArray("choices");
                JSONObject choice = choices.getJSONObject(0);
                return choice.getJSONObject("message").getString("content");
            } else {
                log.error("智谱AI响应格式异常：{}", result);
                return getFallbackWords(request.getStyle());
            }

        } catch (Exception e) {
            log.error("智谱AI调用异常", e);
            return getFallbackWords(request.getStyle());
        }
    }

    /**
     * 降级话术（和其他模型保持一致，保证用户体验）
     */
    private String getFallbackWords(String style) {
        if ("温柔".equals(style)) {
            return "1. 辛苦了宝，忙完赶紧歇会儿～ - 沟通逻辑：先共情，表达关心\n2. 加班这么晚太不容易了，要不要我给你点杯热饮？ - 沟通逻辑：共情+实际行动\n3. 抱抱～忙完我陪你聊聊呀 - 沟通逻辑：情感安抚+陪伴承诺";
        } else if ("幽默".equals(style)) {
            return "1. 打工人的命也是命！快摸鱼歇会儿～ - 沟通逻辑：轻松调侃，缓解压力\n2. 加班使我快乐（才怪），奖励自己一杯奶茶！ - 沟通逻辑：自嘲+正向引导\n3. 老板是不是没给你开加班费？我替你骂他！ - 沟通逻辑：站队+幽默吐槽";
        } else if ("直球".equals(style)) {
            return "1. 加班好累，别硬扛，该歇就歇 - 沟通逻辑：直接关心，不绕弯子\n2. 我等你忙完，想和你聊聊 - 沟通逻辑：直接表达需求\n3. 需要我帮忙吗？ - 沟通逻辑：直接提供帮助";
        } else {
            return "1. 辛苦了，注意休息～ - 沟通逻辑：基础关心\n2. 忙完记得吃饭哦 - 沟通逻辑：日常关怀\n3. 有我在呢～ - 沟通逻辑：情感支持";
        }
    }
}