package com.tangl.heartbeatai.service.impl;

import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import com.tangl.heartbeatai.dto.ChatWordsRequest;
import com.tangl.heartbeatai.service.AiChatService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

/**
 * 百度文心一言实现类
 */
@Service("wenxinAiChatService")
@Slf4j
public class WenxinAiChatServiceImpl implements AiChatService {

    @Value("${ai.wenxin.api-key}")
    private String apiKey;

    @Value("${ai.wenxin.secret-key}")
    private String secretKey;

    /**
     * 获取文心一言Access Token
     */
    private String getAccessToken() {
        String url = "https://aip.baidubce.com/oauth/2.0/token";
        Map<String, Object> params = new HashMap<>();
        params.put("grant_type", "client_credentials");
        params.put("client_id", apiKey);
        params.put("client_secret", secretKey);

        try (HttpResponse response = HttpRequest.post(url).form(params).execute()) {
            String result = response.body();
            JSONObject json = JSON.parseObject(result);
            return json.getString("access_token");
        } catch (Exception e) {
            log.error("文心一言获取Access Token失败", e);
            return null;
        }
    }

    @Override
    public String generateChatWords(ChatWordsRequest request) {
        String accessToken = getAccessToken();
        if (accessToken == null) {
            return "获取AI服务失败，请稍后再试";
        }

        // 构造Prompt
        String prompt = String.format(
                "你是一个专业的恋爱情感助手，请根据对方的消息：【%s】，按照【%s】的风格生成3条回复话术，每条话术标注沟通逻辑，格式要求：\n" +
                        "1. 话术内容 - 沟通逻辑：xxx\n" +
                        "2. 话术内容 - 沟通逻辑：xxx\n" +
                        "3. 话术内容 - 沟通逻辑：xxx\n" +
                        "要求话术自然、贴合年轻人聊天习惯，避免生硬模板。",
                request.getTargetMessage(), request.getStyle()
        );

        // 调用文心一言API
        String url = "https://aip.baidubce.com/rpc/2.0/ai_custom/v1/wenxinworkshop/chat/completions_pro?access_token=" + accessToken;
        Map<String, Object> body = new HashMap<>();
        body.put("messages", new Object[]{
                new HashMap<String, String>() {{
                    put("role", "user");
                    put("content", prompt);
                }}
        });
        body.put("temperature", 0.7);

        try (HttpResponse response = HttpRequest.post(url)
                .header("Content-Type", "application/json")
                .body(JSON.toJSONString(body))
                .execute()) {
            String result = response.body();
            JSONObject json = JSON.parseObject(result);
            return json.getJSONObject("result").getString("content");
        } catch (Exception e) {
            log.error("文心一言生成话术失败", e);
            return "生成话术失败，请稍后再试";
        }
    }
}