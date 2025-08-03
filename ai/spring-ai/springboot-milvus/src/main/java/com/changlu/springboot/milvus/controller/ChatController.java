package com.changlu.springboot.milvus.controller;

import com.github.xiaoymin.knife4j.core.util.StrUtil;
import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.memory.InMemoryChatMemory;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

import java.util.UUID;

import static org.springframework.ai.chat.client.advisor.AbstractChatMemoryAdvisor.CHAT_MEMORY_CONVERSATION_ID_KEY;
import static org.springframework.ai.chat.client.advisor.AbstractChatMemoryAdvisor.CHAT_MEMORY_RETRIEVE_SIZE_KEY;

/**
 * @description  聊天记录实现
 * @author changlu
 * @date 2025/6/7 18:31
 */
@RestController
@RequestMapping("/chat")
@OpenAPIDefinition(info = @Info(title = "Chat API", version = "1.0", description = "API for chat operations"))
@Tag(name = "聊天记录实现", description = "聊天记录实现")
@Slf4j
public class ChatController {

    @Autowired
    private ChatClient chatClient;

    @Operation(summary = "普通聊天")
    @GetMapping("/ai/generate")
    public ResponseEntity<String> generate(@RequestParam(value = "message", defaultValue = "讲个笑话") String message) {
        return ResponseEntity.ok(
                chatClient.prompt()
                        .user(message)
                        .call().content()
        );
    }

    @Operation(summary = "流式回答聊天")
    @GetMapping(value = "/ai/generateStream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
//    @GetMapping(value = "/chat/stream",produces="text/html;charset=UTF-8")
    public Flux<ChatResponse> generateStream(@RequestParam(value = "message", defaultValue = "讲个笑话") String message) {
        Assert.notNull(message, "message不能为空");
        log.info("generateStream ...");
        return chatClient.prompt()
                .user(message)
                .stream().chatResponse();
    }

}
