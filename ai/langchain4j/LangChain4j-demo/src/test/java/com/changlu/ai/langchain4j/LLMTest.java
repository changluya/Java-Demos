package com.changlu.ai.langchain4j;

import dev.langchain4j.model.openai.OpenAiChatModel;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * @description  start chat with openai gpt
 * @author changlu
 * @date 2025/5/18 02:25
 */
@SpringBootTest
public class LLMTest {

    @Test
    public void testGPTDemo() {
        // 初始化模型
        OpenAiChatModel model = OpenAiChatModel.builder()
                .baseUrl("http://langchain4j.dev/demo/openai/v1") //设置模型api地址（如果apiKey="demo"，则可省略baseUrl的配置），新版本不能忽视
                .apiKey("demo")  //设置模型apiKey
                .modelName("gpt-4o-mini") //设置模型名称
                .build();

        // 回答的结果
        String answer = model.chat("你好");

        System.out.println(answer);
    }

}
