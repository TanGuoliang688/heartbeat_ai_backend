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

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

/**
 * 讯飞星火免费版实现类（兼容多模型切换）
 */
@Service("xunfeiAiChatService")
@Slf4j
public class XunfeiAiChatServiceImpl implements AiChatService {

    @Value("${ai.xunfei.app-id}")
    private String appId;

    @Value("${ai.xunfei.api-key}")
    private String apiKey;

    @Value("${ai.xunfei.api-secret}")
    private String apiSecret;

    @Override
    public String generateChatWords(ChatWordsRequest request) {
        // 1. 构造 Prompt
        String prompt = String.format(
                "你是一个专业的恋爱情感助手，请根据对方的消息：【%s】，按照【%s】的风格生成3条回复话术，每条话术标注沟通逻辑，格式要求：\n" +
                        "1. 话术内容 - 沟通逻辑：xxx\n" +
                        "2. 话术内容 - 沟通逻辑：xxx\n" +
                        "3. 话术内容 - 沟通逻辑：xxx\n" +
                        "要求话术自然、贴合年轻人聊天习惯，避免生硬模板。",
                request.getTargetMessage(), request.getStyle()
        );

        // 2. 构造讯飞星火请求体
        JSONObject requestBody = new JSONObject();
        requestBody.put("header", new JSONObject() {{
            put("app_id", appId);
            put("uid", "heartbeat_ai_user_" + request.getUserId());
        }});
        requestBody.put("parameter", new JSONObject() {{
            put("chat", new JSONObject() {{
                put("domain", "generalv3.5"); // 免费版模型版本
                put("temperature", 0.7);
                put("max_tokens", 1000);
            }});
        }});
        requestBody.put("payload", new JSONObject() {{
            put("message", new JSONObject() {{
                put("text", new JSONArray() {{
                    add(new JSONObject() {{
                        put("role", "user");
                        put("content", prompt);
                    }});
                }});
            }});
        }});

        // 3. 生成鉴权 URL（讯飞星火特殊鉴权方式）
        String hostUrl = "https://spark-api.xf-yun.com/v3.5/chat/completions";
        String authUrl = getAuthUrl(hostUrl, apiKey, apiSecret);

        try (HttpResponse response = HttpRequest.post(authUrl)
                .header("Content-Type", "application/json")
                .body(requestBody.toJSONString())
                .timeout(15000)
                .execute()) {

            String result = response.body();
            log.info("讯飞星火响应结果：{}", result);
            JSONObject json = JSON.parseObject(result);

            // 4. 解析响应结果
            if ("0".equals(json.getJSONObject("header").getString("code"))) {
                JSONArray textArray = json.getJSONObject("payload")
                        .getJSONObject("choices")
                        .getJSONArray("text");
                return textArray.getJSONObject(0).getString("content");
            } else {
                String errorMsg = json.getJSONObject("header").getString("message");
                log.error("讯飞星火调用失败：{}", errorMsg);
                return getFallbackWords(request.getStyle()); // 降级话术
            }

        } catch (Exception e) {
            log.error("讯飞星火调用异常", e);
            return getFallbackWords(request.getStyle());
        }
    }

    /**
     * 讯飞星火鉴权：生成带签名的 URL
     */
    private String getAuthUrl(String hostUrl, String apiKey, String apiSecret) {
        try {
            String date = new java.text.SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss z", java.util.Locale.US)
                    .format(new java.util.Date());
            String signatureOrigin = "host: " + new java.net.URL(hostUrl).getHost() + "\n" +
                    "date: " + date + "\n" +
                    "POST " + new java.net.URL(hostUrl).getPath() + " HTTP/1.1";

            javax.crypto.Mac mac = javax.crypto.Mac.getInstance("hmacsha256");
            mac.init(new javax.crypto.spec.SecretKeySpec(apiSecret.getBytes(StandardCharsets.UTF_8), "hmacsha256"));
            byte[] signatureBytes = mac.doFinal(signatureOrigin.getBytes(StandardCharsets.UTF_8));
            String signature = Base64.getEncoder().encodeToString(signatureBytes);

            String authorization = String.format("api_key=\"%s\", algorithm=\"hmac-sha256\", headers=\"host date request-line\", signature=\"%s\"", apiKey, signature);
            return hostUrl + "?authorization=" + java.net.URLEncoder.encode(authorization, StandardCharsets.UTF_8) +
                    "&date=" + java.net.URLEncoder.encode(date, StandardCharsets.UTF_8) +
                    "&host=" + java.net.URLEncoder.encode(new java.net.URL(hostUrl).getHost(), StandardCharsets.UTF_8);
        } catch (Exception e) {
            log.error("讯飞星火鉴权失败", e);
            return hostUrl;
        }
    }

    /**
     * 降级话术（和之前保持一致）
     */
    private String getFallbackWords(String style) {
        if ("温柔".equals(style)) {
            return "1. 辛苦了宝，忙完赶紧歇会儿～ - 沟通逻辑：先共情，表达关心\n2. 加班这么晚太不容易了，要不要我给你点杯热饮？ - 沟通逻辑：共情+实际行动\n3. 抱抱～忙完我陪你聊聊呀 - 沟通逻辑：情感安抚+陪伴承诺";
        } else if ("幽默".equals(style)) {
            return "1. 打工人的命也是命！快摸鱼歇会儿～ - 沟通逻辑：轻松调侃，缓解压力\n2. 加班使我快乐（才怪），奖励自己一杯奶茶！ - 沟通逻辑：自嘲+正向引导\n3. 老板是不是没给你开加班费？我替你骂他！ - 沟通逻辑：站队+幽默吐槽";
        } else {
            return "1. 加班好累，别硬扛，该歇就歇 - 沟通逻辑：直接关心，不绕弯子\n2. 我等你忙完，想和你聊聊 - 沟通逻辑：直接表达需求\n3. 需要我帮忙吗？ - 沟通逻辑：直接提供帮助";
        }
    }
}