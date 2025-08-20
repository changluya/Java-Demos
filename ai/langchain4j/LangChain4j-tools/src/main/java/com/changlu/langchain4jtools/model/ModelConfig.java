package com.changlu.langchain4jtools.model;

import com.changlu.langchain4jtools.env.EnvironmentContext;
import dev.langchain4j.community.model.dashscope.QwenChatModel;
import dev.langchain4j.community.model.dashscope.QwenStreamingChatModel;
import dev.langchain4j.community.model.xinference.XinferenceStreamingChatModel;
import dev.langchain4j.model.chat.StreamingChatModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import dev.langchain4j.model.chat.ChatModel;

@Configuration
public class ModelConfig {

    @Autowired
    private EnvironmentContext env;

    @Bean
    public StreamingChatModel qwenChatModel() {
//        return QwenStreamingChatModel.builder()
//                .apiKey(env.getDashScopeApiKey())
//                .modelName(env.getDashScopeModelName())
//                .build();
        return XinferenceStreamingChatModel.builder()
                .baseUrl(env.getXInferenceBaseUrl())
                .modelName(env.getXInferenceModelName())
                .logRequests(true)
                .logResponses(true)
                .build();
    }

}
