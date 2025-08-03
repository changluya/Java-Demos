package com.changlu.springboot.milvus;

import org.junit.jupiter.api.Test;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.ollama.OllamaChatModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class ChatModelTest {

//    @Autowired
//    private OllamaChatModel ollamaChatModel;

    @Autowired
    private ChatClient chatClient;

    @Test
    public void test() {
//        System.out.println(ollamaChatModel.call("你好"));
    }

    @Test
    public void test02() {
        System.out.println(chatClient.prompt()
                .user("你好")
                .call().content());
    }

}
