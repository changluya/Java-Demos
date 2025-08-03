package com.changlu.springbootaichat.config;

import dev.langchain4j.community.model.dashscope.QwenChatModel;
import dev.langchain4j.community.model.dashscope.QwenStreamingChatModel;
import dev.langchain4j.model.chat.ChatModel;
import dev.langchain4j.model.chat.StreamingChatModel;
import dev.langchain4j.model.ollama.OllamaChatModel;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AiConfig {


    // dashscope
    @Value("${langchain4j.community.dashscope.chat-model.api-key}")
    private String dashScopeApiKey;
    @Value("${langchain4j.community.dashscope.chat-model.model-name}")
    private String dashScopeModelName;

    @Bean
    public ChatModel chatModel() {
        ChatModel chatModel = QwenChatModel.builder()
                .apiKey(dashScopeApiKey)
                .modelName(dashScopeModelName)
                .build();
        return chatModel;
    }

    @Bean
    public StreamingChatModel streamingChatModel() {
        // ollama
//        OllamaStreamingChatModel ollamaStreamingChatModel = OllamaStreamingChatModel.builder()
//                .baseUrl(ollamaUrl)
//                .modelName(streamModelName)
//                .logRequests(true)
//                .logResponses(true)
//                .build();
//        return ollamaStreamingChatModel;
        // dashscope
        QwenStreamingChatModel qwenStreamingChatModel = QwenStreamingChatModel.builder()
                .apiKey(dashScopeApiKey)
                .modelName(dashScopeModelName)
                .build();
        return qwenStreamingChatModel;
    }
}
