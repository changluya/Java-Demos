package com.changlu.springbootaichat.assistant;

import dev.langchain4j.service.UserMessage;
import dev.langchain4j.service.spring.AiService;
import dev.langchain4j.service.spring.AiServiceWiringMode;
import reactor.core.publisher.Flux;

@AiService(
        wiringMode = AiServiceWiringMode.EXPLICIT,
//        chatModel = "qwenChatModel",
        streamingChatModel = "streamingChatModel"
)
public interface KnowledgeAgent {
    // 流式返回
    Flux<String> chat(@UserMessage String userMessage);

}