package com.changlu.ai.langchain4j.rag;

import dev.langchain4j.data.message.UserMessage;
import dev.langchain4j.model.embedding.onnx.HuggingFaceTokenizer;
import org.junit.jupiter.api.Test;

public class TokenCalculateTest {

    @Test
    public void testTokenCount() {
        String text = "这是一个示例文本，用于测试 token 长度的计算。";
        UserMessage userMessage = UserMessage.userMessage(text);
        //计算 token 长度
        //QwenTokenizer tokenizer = new QwenTokenizer(System.getenv("DASH_SCOPE_API_KEY"), "qwen-max");
        HuggingFaceTokenizer tokenizer = new HuggingFaceTokenizer();
        int count = tokenizer.estimateTokenCountInMessage(userMessage);
        System.out.println("token长度：" + count);
    }

}
