package com.changlu.spring.ai.chat.config;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.document.Document;
import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.ai.vectorstore.SimpleVectorStore;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class RagConfig {

    @Bean
    ChatClient chatClient(ChatClient.Builder builder) {
        return builder.defaultSystem("你将作为一名Java开发语言的专家，对于用户的使用需求作出解答")
                .build();
    }

    @Bean
    VectorStore vectorStore(EmbeddingModel embeddingModel) {
        SimpleVectorStore simpleVectorStore = SimpleVectorStore.builder(embeddingModel)
                .build();

        // 生成一个说明的文档
        List<Document> documents = List.of(
                new Document("产品说明:名称：Java开发语言\n" +
                        "产品描述：Java是一种面向对象开发语言。\n" +
                        "特性：\n" +
                        "1. 封装\n" +
                        "2. 继承\n" +
                        "3. 多态\n"));
        simpleVectorStore.add(documents);
        return simpleVectorStore;
    }

}