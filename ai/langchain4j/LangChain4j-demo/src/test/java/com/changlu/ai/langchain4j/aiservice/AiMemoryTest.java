package com.changlu.ai.langchain4j.aiservice;

import com.changlu.ai.langchain4j.assistant.Assistant;
import com.changlu.ai.langchain4j.assistant.MemoryChatAssistant;
import com.changlu.ai.langchain4j.assistant.SeparateChatAssistant;
import dev.langchain4j.community.model.dashscope.QwenChatModel;
import dev.langchain4j.data.message.AiMessage;
import dev.langchain4j.data.message.UserMessage;
import dev.langchain4j.memory.chat.MessageWindowChatMemory;
import dev.langchain4j.model.chat.response.ChatResponse;
import dev.langchain4j.service.AiServices;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Arrays;

@SpringBootTest
public class AiMemoryTest {

    @Autowired
    private Assistant assistant;

    /**
     * 测试验证是否普通chat对话有记忆
     * @param
     * @return void
     */
    @Test
    public void test01_memory() {
        String answer1 = assistant.chat("我是小明");
        System.out.println(answer1);

        String answer2 = assistant.chat("你知道我叫什么吗？");
        System.out.println(answer2);
    }

    // 注入千问模型
    @Autowired
    private QwenChatModel qwenChatModel;

    /**
     * 简单实现聊天记忆
     * @param
     * @return void
     */
    @Test
    public void test02_memory() {
        // 第一轮对话
        System.out.println("第一轮对话：");
        UserMessage userMessage1 = UserMessage.userMessage("我是长路");
        ChatResponse chatResponse1 = qwenChatModel.chat(userMessage1);
        AiMessage aiMessage1 = chatResponse1.aiMessage();// 获取到ai message信息
        System.out.println(aiMessage1);
        System.out.println();

        // 第二轮对话
        System.out.println("第二轮对话：");
        UserMessage userMessage2 = UserMessage.userMessage("你知道我是谁吗？");
        // 将第一次用户对话 & ai回应消息 & 用户第二次对话信息 都发送给ai
        ChatResponse chatResponse2 = qwenChatModel.chat(Arrays.asList(userMessage1, aiMessage1, userMessage2));
        AiMessage aiMessage2 = chatResponse2.aiMessage();
        System.out.println(aiMessage2);
    }

    /**
     * 基于MessageWindowChatMemory实现类来实现聊天记忆
     * @param
     * @return void
     */
    @Test
    public void test03_memory() {
        // 创建chatMemory(基于内存实现的)
        MessageWindowChatMemory chatMemory = MessageWindowChatMemory.withMaxMessages(10);

        // 创建AiService
        Assistant assistant = AiServices.builder(Assistant.class)
                .chatLanguageModel(qwenChatModel)
                .chatMemory(chatMemory)
                .build();

        String answer1 = assistant.chat("我是长路");
        System.out.println(answer1);
        String answer2 = assistant.chat("你知道我是谁吗？");
        System.out.println(answer2);
        String answer3 = assistant.chat("我想知道你是谁？并且使用江苏话跟我说我是谁？");
        System.out.println(answer3);
    }

    @Autowired
    private MemoryChatAssistant memoryChatAssistant;
    /**
     * AiService注入形式实现memory记忆
     * @param
     * @return void
     */
    @Test
    public void test04_memory() {

        String answer1 = memoryChatAssistant.chat("我是长路");
        System.out.println(answer1);
        System.out.println("=====");
        String answer2 = memoryChatAssistant.chat("你知道我是谁吗？");
        System.out.println(answer2);
        System.out.println("=====");
        String answer3 = memoryChatAssistant.chat("我想知道你是谁？并且使用江苏话跟我说我是谁？");
        System.out.println(answer3);
    }

    @Autowired
    private SeparateChatAssistant separateChatAssistant;
    /**
     * 实现用户隔离聊天记录
     * @param
     * @return void
     */
    @Test
    public void test05_memory() {
          String answer1 = separateChatAssistant.chat(1, "你知道我是谁吗？");
        System.out.println(answer1);
//        // 用户1：memoryId=1
//        String answer1 = separateChatAssistant.chat(1, "我是长路");
//        System.out.println(answer1);
//        System.out.println("=====1");
//        String answer2 = separateChatAssistant.chat(1, "你知道我是谁吗？");
//        System.out.println(answer2);
//        // 用户2：memoryId=2
//        System.out.println("=====2");
//        String answer3 = separateChatAssistant.chat(2, "我想知道你是谁？并且使用江苏话跟我说我是谁？");
//        System.out.println(answer3);
//        System.out.println("=====3");
//
//        // 再次测试用户1
//        String answer4 = separateChatAssistant.chat(1, "我叫什么名字？");
//        System.out.println(answer4);
//        System.out.println("=====4");
    }

}
