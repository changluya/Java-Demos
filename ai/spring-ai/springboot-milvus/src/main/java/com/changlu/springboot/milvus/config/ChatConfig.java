package com.changlu.springboot.milvus.config;

import com.changlu.springboot.milvus.ai.MilvusVectorStore;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.QuestionAnswerAdvisor;
//import org.springframework.ai.ollama.OllamaChatModel;
import org.springframework.ai.ollama.OllamaChatModel;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
 
/**
 * @description  AI聊天配置
 * @author changlu
 * @date 2025/6/7 18:24
 */
@Configuration
public class ChatConfig {

    @Bean
    public ChatClient chatClient(OllamaChatModel ollamaChatModel, MilvusVectorStore milvusVectorStore) {
        return ChatClient.builder(ollamaChatModel)
                .defaultSystem("你是一个RAG知识库问答机器人，致力于解决用户提出的问题，并给出详细的解决方案")
                .defaultAdvisors(new QuestionAnswerAdvisor(milvusVectorStore))
                .build();
    }

//    @Bean
//    public ChatClient chatClient(DashScopeChatModel dashScopeChatModel, MilvusVectorStore milvusVectorStore) {
//        return ChatClient.builder(dashScopeChatModel)
//                .defaultSystem("你是一个RAG知识库问答机器人，致力于解决用户提出的问题，并给出详细的解决方案")
////                .defaultAdvisors(new QuestionAnswerAdvisor(milvusVectorStore))
//                .build();
//    }
 
}