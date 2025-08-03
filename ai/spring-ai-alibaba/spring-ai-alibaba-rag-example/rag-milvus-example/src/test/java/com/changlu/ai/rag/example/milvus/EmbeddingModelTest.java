package com.changlu.ai.rag.example.milvus;

import org.junit.jupiter.api.Test;
import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.ai.embedding.EmbeddingRequest;
import org.springframework.ai.embedding.EmbeddingResponse;
import org.springframework.ai.ollama.api.OllamaOptions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Arrays;
import java.util.List;

@SpringBootTest
public class EmbeddingModelTest {

    @Autowired
    private EmbeddingModel embeddingModel;  // 应指向 Ollama 的 bge-m3

    @Test
    public void testEmbeddingModel() {
        EmbeddingResponse response = embeddingModel.call(
                new EmbeddingRequest(List.of("Hello World", "World is big and salvation is near"),
                        OllamaOptions.builder()
                                .model("bge-m3").truncate(false).build()));
        System.out.println("向量维度: " + Arrays.toString(response.getResults().get(0).getOutput()));
        System.out.println("向量维度: " + Arrays.toString(response.getResults().get(1).getOutput()));
    }

}
