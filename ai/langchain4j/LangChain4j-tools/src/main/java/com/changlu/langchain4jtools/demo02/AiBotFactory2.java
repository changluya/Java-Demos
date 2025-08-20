package com.changlu.langchain4jtools.demo02;

import com.changlu.langchain4jtools.assistant.AiAssistant;
import com.changlu.langchain4jtools.env.EnvironmentContext;
import com.changlu.langchain4jtools.tools.CalculatorTools;
import com.changlu.langchain4jtools.util.SpringUtil;
import dev.langchain4j.agent.tool.ToolSpecification;
import dev.langchain4j.community.model.dashscope.QwenStreamingChatModel;
import dev.langchain4j.model.chat.request.json.JsonBooleanSchema;
import dev.langchain4j.model.chat.request.json.JsonNumberSchema;
import dev.langchain4j.model.chat.request.json.JsonObjectSchema;
import dev.langchain4j.model.chat.request.json.JsonSchemaElement;
import dev.langchain4j.service.AiServices;
import dev.langchain4j.service.tool.DefaultToolExecutor;
import dev.langchain4j.service.tool.ToolExecutor;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @description  Ai Bot工厂类
 * @author changlu
 * @date 2025/8/17 00:21
 */
public class AiBotFactory2 {

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
                // 方式一：注入带@Tool注解形式
//                .tools(new CalculatorTools())
                // 方式二：手动去构建ToolSpecification
                .tools(buildDiyTools())
                .tools()
                .build();
        return diyAiAssistant;
    }

    /**
     * 模拟langchain4j底层核心封装逻辑 方法描述 & 方法执行器
     * @param
     * @return Map<ToolSpecification,ToolExecutor>
     * @author changlu
     * @createDate 2025/8/17 21:04
     */
    public static Map<ToolSpecification, ToolExecutor> buildDiyTools() {
        Map<ToolSpecification, ToolExecutor> tools = new HashMap<>();

        // 构建一个CalculatorTools2
        CalculatorTools2 calculatorTools = new CalculatorTools2();
        try {
            // 第一个方法封装 sum 两数之和
            ToolSpecification sumSpec = buildSumToolSpecification();
            Method sumMethod = CalculatorTools2.class.getDeclaredMethod("sum", Double.class, Double.class);
            tools.put(sumSpec, new DefaultToolExecutor(calculatorTools, sumMethod));

            // 第二个方法封装 平方根
            ToolSpecification sqrtSpec = buildSqrtToolSpecification();
            Method sqrtMethod = CalculatorTools2.class.getDeclaredMethod("squareRoot", Double.class);
            tools.put(sqrtSpec, new DefaultToolExecutor(calculatorTools, sqrtMethod));
        } catch (NoSuchMethodException e) {
            throw new RuntimeException(e);
        }
        return tools;
    }

    private static ToolSpecification buildSqrtToolSpecification() {
        // 定义参数属性
        JsonObjectSchema sqrtJsonObjectSchema = buildSqrtJsonObjectSchema();
        ToolSpecification sqrtSpec = ToolSpecification.builder()
                .name("squareRoot")
                .description("返回给定参数的平方根")
                .parameters(sqrtJsonObjectSchema)
                .build();
        return sqrtSpec;
    }

    private static JsonObjectSchema buildSqrtJsonObjectSchema() {
        // 定义的参数 & 类型
        String param1 = "arg0";
        Map<String, JsonSchemaElement> properties = new HashMap<>();
        properties.put(param1, JsonNumberSchema.builder().description("需要计算平方根的数字").build());

        // 必要的参数
        List<String> required = new ArrayList<>();
        required.add(param1);

        JsonObjectSchema jsonObjectSchema = JsonObjectSchema.builder()
                .addProperties(properties)
                .required(required)
                .definitions(null)
                .build();
        return jsonObjectSchema;
    }

    private static ToolSpecification buildSumToolSpecification() {
        // 定义参数属性
        JsonObjectSchema sumJsonObjectSchema = buildSumJsonObjectSchema();
        ToolSpecification sumSpec = ToolSpecification.builder()
                .name("sum")
                .description("返回两个参数相加之和")
                .parameters(sumJsonObjectSchema)
                .build();
        return sumSpec;
    }

    /**
     * 构建方法元数据信息
     * @param
     * @return JsonObjectSchema
     * @author changlu
     * @createDate 2025/8/17 21:06
     */
    private static JsonObjectSchema buildSumJsonObjectSchema() {
        // 定义的参数 & 类型
        // 底层是根据method.getParameters()获取到的名字，所以这里直接使用arg0、arg1
        String param1 = "arg0";
        String param2 = "arg1";
        Map<String, JsonSchemaElement> properties = new HashMap<>();
        properties.put(param1, JsonNumberSchema.builder().description("加数1").build());
        properties.put(param2, JsonNumberSchema.builder().description("加数2").build());

        // 必要的参数
        List<String> required = new ArrayList<>();
        required.add(param1);
        required.add(param2);

        JsonObjectSchema jsonObjectSchema = JsonObjectSchema.builder()
                .addProperties(properties)
                .required(required)
                .definitions(null)
                .build();
        return jsonObjectSchema;
    }



}
