package com.changlu.ai.rag.example.milvus.config;

import com.alibaba.cloud.ai.dashscope.chat.DashScopeChatModel;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

@Configuration
public class ChatModelConfig {

    // 默认优先选择百炼大模型
    @Bean
    @Primary
    public ChatModel primaryChatModel(DashScopeChatModel dashScopeChatModel) {
        return dashScopeChatModel;
    }
}