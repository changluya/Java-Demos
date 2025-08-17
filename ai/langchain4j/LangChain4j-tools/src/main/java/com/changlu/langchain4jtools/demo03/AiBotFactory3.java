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
 * @description Ai Bot factory class with HTTP tool support
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
        
        // Example: Weather API tool
        Map<String, String> weatherParamMappings = new HashMap<>();
        weatherParamMappings.put("location", "q"); // Map tool param "location" to HTTP param "q"
        
        httpTools.put(
            buildWeatherToolSpecification(),
            new HttpToolExecutor("https://api.weatherapi.com/v1/current.json", weatherParamMappings)
        );
        
        // Add more HTTP tools as needed...
        return httpTools;
    }

    private static ToolSpecification buildWeatherToolSpecification() {
        Map<String, JsonSchemaElement> properties = new HashMap<>();
        properties.put("location", JsonStringSchema.builder().description("The city and region or ZIP code for weather lookup").build());
        
        List<String> required = new ArrayList<>();
        required.add("location");
        
        return ToolSpecification.builder()
                .name("getCurrentWeather")
                .description("Get the current weather for a location")
                .parameters(JsonObjectSchema.builder()
                        .addProperties(properties)
                        .required(required)
                        .build())
                .build();
    }

}