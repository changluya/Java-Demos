package com.changlu.mcp.client.controller;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

@RestController
public class ChatController {

    @Autowired
    private ChatClient chatClient;

    // 流式响应
    @GetMapping(value = "/chat/stream",produces="text/html;charset=UTF-8")
    public Flux<String> chatStream(@RequestParam(value = "msg") String message) {
        return chatClient.prompt().user(message).stream().content();
    }

}
