package com.changlu.langchain4jtools.demo01;

import com.changlu.langchain4jtools.assistant.AiAssistant;
import com.changlu.langchain4jtools.env.EnvironmentContext;
import com.changlu.langchain4jtools.tools.CalculatorTools;
import com.changlu.langchain4jtools.util.SpringUtil;
import dev.langchain4j.community.model.dashscope.QwenStreamingChatModel;
import dev.langchain4j.service.AiServices;
/**
 * @description  Ai Bot工厂类
 * @author changlu
 * @date 2025/8/17 00:21
 */
public class AiBotFactory {

    // 快速构建一个Aiservice
    public static AiAssistant buildAiAssistant() {
        EnvironmentContext env = SpringUtil.getBean(EnvironmentContext.class);
        // 创建model
        QwenStreamingChatModel qwenStreamingChatModel = QwenStreamingChatModel.builder()
                .apiKey(env.getDashScopeApiKey())
                .modelName(env.getDashScopeModelName())
                .build();

        // 使用langchain4j提供的代理类快速实现一个ai bot
        AiAssistant diyAiAssistant = AiServices.builder(AiAssistant.class)
                .streamingChatModel(qwenStreamingChatModel)
                .tools(new CalculatorTools())
                .build();
        return diyAiAssistant;
    }

}
