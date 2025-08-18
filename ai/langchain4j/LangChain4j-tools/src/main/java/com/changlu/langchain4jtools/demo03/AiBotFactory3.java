package com.changlu.langchain4jtools.demo03;

import com.changlu.langchain4jtools.assistant.AiAssistant;
import com.changlu.langchain4jtools.env.EnvironmentContext;
import com.changlu.langchain4jtools.tools.CalculatorTools;
import com.changlu.langchain4jtools.util.SpringUtil;
import dev.langchain4j.agent.tool.ToolSpecification;
import dev.langchain4j.community.model.dashscope.QwenStreamingChatModel;
import dev.langchain4j.model.chat.request.json.JsonObjectSchema;
import dev.langchain4j.model.chat.request.json.JsonSchemaElement;
import dev.langchain4j.model.chat.request.json.JsonStringSchema;
import dev.langchain4j.service.AiServices;
import dev.langchain4j.service.tool.DefaultToolExecutor;
import dev.langchain4j.service.tool.ToolExecutor;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @description Ai Bot factory class with enhanced HTTP tool support including default values
 */
public class AiBotFactory3 {

    public static AiAssistant buildAiAssistant() {
        EnvironmentContext env = SpringUtil.getBean(EnvironmentContext.class);

        QwenStreamingChatModel qwenStreamingChatModel = QwenStreamingChatModel.builder()
                .apiKey(env.getDashScopeApiKey())
                .modelName(env.getDashScopeModelName())
                .build();

        // Build tools map
        Map<ToolSpecification, ToolExecutor> tools = new HashMap<>();

        // Add HTTP-based tools
        tools.putAll(buildHttpTools());

        AiAssistant diyAiAssistant = AiServices.builder(AiAssistant.class)
                .streamingChatModel(qwenStreamingChatModel)
                .tools(tools)
                .build();
        return diyAiAssistant;
    }

    /**
     * Build HTTP-based tools configuration
     */
    private static Map<ToolSpecification, ToolExecutor> buildHttpTools() {
        Map<ToolSpecification, ToolExecutor> httpTools = new HashMap<>();

        // Example 1: Serper API tool (POST with JSON body)
        Map<String, String> serperParamMappings = new HashMap<>();
        serperParamMappings.put("query", "q");
        serperParamMappings.put("country", "gl");
        serperParamMappings.put("language", "hl");

        Map<String, String> serperHeaders = new HashMap<>();
        serperHeaders.put("X-API-KEY", "d420dbfcefdd0cf0261ba09f5a91dc4a35933c59");
        serperHeaders.put("Content-Type", "application/json");

        // 默认参数 for Serper API
        Map<String, Object> serperDefaults = new HashMap<>();
        serperDefaults.put("country", "cn"); // Default country code
        serperDefaults.put("language", "zh-CN"); // Default language code

        httpTools.put(
                buildSerperToolSpecification(),
                new HttpToolExecutor(
                        "https://google.serper.dev/search",
                        serperParamMappings,
                        serperHeaders,
                        HttpToolExecutor.HttpMethod.POST,
                        true,
                        serperDefaults
                )
        );

        return httpTools;
    }

    private static ToolSpecification buildSerperToolSpecification() {
        Map<String, JsonSchemaElement> properties = new HashMap<>();
        // 构建初始参数
        properties.put("query", JsonStringSchema.builder()
                .description("Search query")
                .build());
        properties.put("country", JsonStringSchema.builder()
                .description("Country code (e.g., 'cn')")
                .build());
        properties.put("language", JsonStringSchema.builder()
                .description("Language code (e.g., 'zh-CN')")
                .build());

        // 设置必填项
        List<String> required = new ArrayList<>();
        required.add("query"); // Only query is required

        return ToolSpecification.builder()
                .name("searchWeb")
                .description("Search the web using Serper API")
                .parameters(JsonObjectSchema.builder()
                        .addProperties(properties)
                        .required(required)
                        .build())
                .build();
    }
}