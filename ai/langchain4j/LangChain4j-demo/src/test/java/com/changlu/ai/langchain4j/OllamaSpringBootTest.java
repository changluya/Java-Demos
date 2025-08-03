package com.changlu.ai.langchain4j;

import dev.langchain4j.model.ollama.OllamaChatModel;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class OllamaSpringBootTest {

    // 注意这里注入的是OllamaChatModel，读取的ollama的配置项
    @Autowired
    private OllamaChatModel ollamaChatModel;

    @Test
    public void test() {
        String message = ollamaChatModel.chat("你是谁？");
        System.out.println(message);
    }


}
