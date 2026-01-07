package com.tangl.heartbeatai.config;


import com.tangl.heartbeatai.service.AiChatService;
import com.tangl.heartbeatai.service.impl.DeepSeekAiChatServiceImpl;
import com.tangl.heartbeatai.service.impl.WenxinAiChatServiceImpl;
import com.tangl.heartbeatai.service.impl.XunfeiAiChatServiceImpl;
import com.tangl.heartbeatai.service.impl.ZhipuAiChatServiceImpl;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * AI模型配置类：支持文心一言/DeepSeek/讯飞星火/智谱AI切换
 */
@Configuration
public class AiChatConfig {

    @Value("${ai.provider}")
    private String aiProvider;

    @Bean("aiChatService")
    public AiChatService aiChatService() {
        if ("wenxin".equals(aiProvider)) {
            return new WenxinAiChatServiceImpl();
        } else if ("deepseek".equals(aiProvider)) {
            return new DeepSeekAiChatServiceImpl();
        } else if ("xunfei".equals(aiProvider)) {
            return new XunfeiAiChatServiceImpl();
        } else if ("zhipu".equals(aiProvider)) { // 新增智谱AI分支
            return new ZhipuAiChatServiceImpl();
        } else {
            // 默认使用智谱AI免费版
            return new ZhipuAiChatServiceImpl();
        }
    }
}