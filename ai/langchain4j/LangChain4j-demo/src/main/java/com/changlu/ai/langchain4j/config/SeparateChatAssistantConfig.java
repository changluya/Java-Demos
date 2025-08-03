package com.changlu.ai.langchain4j.config;

import com.changlu.ai.langchain4j.store.MongoChatMemoryStore;
import dev.langchain4j.memory.chat.ChatMemoryProvider;
import dev.langchain4j.memory.chat.MessageWindowChatMemory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SeparateChatAssistantConfig {

    @Autowired
    private MongoChatMemoryStore mongoChatMemoryStore;

    @Bean
    public ChatMemoryProvider chatMemoryProvider() {
        // 这里封装了一个接口实现，一个memoryId对应一个用户同时去维护一组历史10条记录
        return memoryId -> MessageWindowChatMemory.builder()
                .chatMemoryStore(mongoChatMemoryStore) // 指定持久化消息存储对象
                .id(memoryId)
                .maxMessages(10)
                .build();
    }
    
}
