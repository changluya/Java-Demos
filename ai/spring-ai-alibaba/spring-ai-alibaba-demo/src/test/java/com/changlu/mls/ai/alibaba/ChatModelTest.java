package com.changlu.mls.ai.alibaba;

import com.alibaba.cloud.ai.dashscope.chat.DashScopeChatOptions;
import com.changlu.mls.ai.alibaba.controller.HelloworldController;
import org.junit.jupiter.api.Test;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.prompt.ChatOptions;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.*;

@SpringBootTest
public class ChatModelTest {
    @Autowired
    private ChatModel chatModel;

    // 快速测试chatmodel 默认模型为：qwen-plus
    @Test
    public void testModel_01() {
        // 记录开始时间
        long startTime = System.currentTimeMillis();

        ChatResponse call = chatModel.call(new Prompt("你是什么大模型？"));
        String res = call.getResult().getOutput().getText();
        System.out.println(res);

        // 计算耗时
        long endTime = System.currentTimeMillis();
        long duration = endTime - startTime;

        // 打印耗时（单位：毫秒）
        System.out.println("请求耗时：" + duration + "ms");
    }

    // 单测2：测试每个模型耗费的时间
    @Test
    public void testModel_02_options() {
        // 定义测试的模型列表
        // 思考模型（这里暂时不引入）：qwen3-235b-a22b、qwen3-30b-a3b、deepseek-v3、baichuan2-turbo
        List<String> models = Arrays.asList(
                "qwen-plus-latest", "qwen-turbo-latest", "qwen-turbo-2025-04-28",
                "deepseek-r1");

        // 创建表格标题
        System.out.println("+-----------------------------+--------------+");
        System.out.println("| 模型名称                     | 耗时(ms)     |");
        System.out.println("+-----------------------------+--------------+");

        // 用于存储结果的列表
        List<Object[]> results = new ArrayList<>();

        for (String modelName : models) {

            ChatOptions options = DashScopeChatOptions.builder()
                    .withTemperature(0.7)
                    .withModel(modelName)
                    .build();
//            Prompt prompt = new Prompt("/no_think" +
//                    "基于以下数据，哪个模型的响应速度最快？只需给出模型名称，不要解释。\"" +
//                    "模型响应速度对比数据（2025年测试）：\n" +
//                    "- qwen-turbo-latest: 平均延迟 320ms，支持 1000 TPM\n" +
//                    "- deepseek-v3: 平均延迟 280ms，支持 1500 TPM  \n" +
//                    "- baichuan2-turbo: 平均延迟 350ms，支持 800 TPM\n" +
//                    "- qwen-plus-latest: 平均延迟 450ms，支持 1200 TPM", options);

            Prompt prompt = new Prompt("你是谁？/no_think", options);

            // 记录开始时间
            long startTime = System.currentTimeMillis();
            ChatResponse response = chatModel.call(prompt);
            String content = response.getResult().getOutput().getText();

            // 计算耗时
            long endTime = System.currentTimeMillis();
            long duration = endTime - startTime;

            // 存储结果
            results.add(new Object[]{modelName, duration});

//            System.out.println(String.format("model: %s, content: %s\n", modelName, content));
        }

        // 打印结果表格
        for (Object[] result : results) {
            System.out.printf("| %-27s | %-12d |\n", result[0], result[1]);
        }
        System.out.println("+-----------------------------+--------------+");
    }
}
