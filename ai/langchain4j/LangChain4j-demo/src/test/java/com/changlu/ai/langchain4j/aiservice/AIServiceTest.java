package com.changlu.ai.langchain4j.aiservice;

import com.changlu.ai.langchain4j.assistant.Assistant;
import dev.langchain4j.community.model.dashscope.QwenChatModel;
import dev.langchain4j.service.AiServices;
import dev.langchain4j.service.spring.AiService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class AIServiceTest {

    // 方式一：api代码模式创建Assistant代理类，实现aiservice的bean实例创建
    @Autowired
    private QwenChatModel qwenChatModel;

    @Test
    public void testChat() {
        // 创建aiservice，指定大模型 千问
        Assistant assistant = AiServices.create(Assistant.class, qwenChatModel);
        // 调用service的接口
        String answer = assistant.chat("你好呀，你是谁，请介绍下自己？");
        System.out.println(answer);
    }

    // 方式二：直接依赖注入Assistant实现类
    @Autowired
    private Assistant assistant;

    @Test
    public void testChatByAssistant() {
        String answer = assistant.chat("你好呀，你是谁，请再介绍下自己？");
        System.out.println(answer);
    }

}
