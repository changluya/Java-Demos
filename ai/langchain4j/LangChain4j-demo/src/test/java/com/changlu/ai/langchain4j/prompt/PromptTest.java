package com.changlu.ai.langchain4j.prompt;

import com.changlu.ai.langchain4j.assistant.SeparateChatAssistant;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class PromptTest {

    @Autowired
    private SeparateChatAssistant separateChatAssistant;

    // 系统提示词测试
    @Test
    public void test01_systemPrompt() {
        String answer = separateChatAssistant.chat(3, "我是长路，你好呀，请跟我用英语说good morning，并告诉我今天几号");
        System.out.println(answer);
    }

    // 用户提示词测试
    @Test
    public void test02_userPrompt() {
        String answer = separateChatAssistant.chat(4, "请告诉我今年是哪个生肖年");
        System.out.println(answer);
        System.out.println("==========");
        String answer2 = separateChatAssistant.chat(4, "我的幸运年是今年吗？今天是几号？");
        System.out.println(answer2);
    }

}
