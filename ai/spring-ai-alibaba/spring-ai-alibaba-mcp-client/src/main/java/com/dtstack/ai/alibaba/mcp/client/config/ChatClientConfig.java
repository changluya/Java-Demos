package com.dtstack.ai.alibaba.mcp.client.config;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.tool.ToolCallbackProvider;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ChatClientConfig {

    @Bean
    public ChatClient chatClient(ChatClient.Builder chatClientBuilder, ToolCallbackProvider tools) {
        ChatClient chatClient = chatClientBuilder.defaultToolCallbacks(tools)
                .defaultSystem("你是气象专家，请用专家术语简洁快速的描述客户的问题，回答的时候带一点调皮与emoji，最终转换为html输出")
                .build();
        return chatClient;
    }

}
