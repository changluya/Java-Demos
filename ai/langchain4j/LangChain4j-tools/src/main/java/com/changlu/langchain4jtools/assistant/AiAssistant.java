package com.changlu.langchain4jtools.assistant;

import dev.langchain4j.service.spring.AiService;
import dev.langchain4j.service.spring.AiServiceWiringMode;
import reactor.core.publisher.Flux;

@AiService(
        wiringMode = AiServiceWiringMode.EXPLICIT,
        streamingChatModel = "qwenChatModel",
        tools = "calculatorTools" //配置tools
)
public interface AiAssistant {

    Flux<String> chat(String userMessage);

}