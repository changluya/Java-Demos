package com.changlu.langchain4jtools.demo03;

import com.changlu.langchain4jtools.assistant.AiAssistant;
import com.changlu.langchain4jtools.demo03.plugin.http.HttpToolExecutor;
import com.changlu.langchain4jtools.demo03.plugin.http.domain.HttpPlugin;
import com.changlu.langchain4jtools.demo03.plugin.http.domain.HttpPluginMethod;
import com.changlu.langchain4jtools.demo03.plugin.http.domain.HttpToolParameter;
import com.changlu.langchain4jtools.demo03.plugin.http.enums.HttpPluginEnums;
import com.changlu.langchain4jtools.domain.Pair;
import com.changlu.langchain4jtools.env.EnvironmentContext;
import com.changlu.langchain4jtools.util.SpringUtil;
import dev.langchain4j.agent.tool.ToolSpecification;
import dev.langchain4j.community.model.dashscope.QwenStreamingChatModel;
import dev.langchain4j.model.chat.request.json.JsonObjectSchema;
import dev.langchain4j.model.chat.request.json.JsonSchemaElement;
import dev.langchain4j.service.AiServices;
import dev.langchain4j.service.tool.ToolExecutor;

import java.util.*;

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

        AiAssistant diyAiAssistant = AiServices.builder(AiAssistant.class)
                .streamingChatModel(qwenStreamingChatModel)
                .tools(buildHttpTools())
                .build();
        return diyAiAssistant;
    }

    private static Map<ToolSpecification, ToolExecutor> buildHttpTools() {
        // 定义参数配置
        List<HttpToolParameter> parameters = new ArrayList<>();
        parameters.add(new HttpToolParameter(
                "query", "q",
                HttpPluginEnums.ParameterUseType.BODY.getValue(), // 使用枚举值
                HttpPluginEnums.ParameterType.STRING.getValue(),
                null, true, "搜索关键词"));
        parameters.add(new HttpToolParameter(
                "country", "gl",
                HttpPluginEnums.ParameterUseType.BODY.getValue(), // 使用枚举值
                HttpPluginEnums.ParameterType.STRING.getValue(),
                "cn", false, "国家代码（如：'cn'）"));
        parameters.add(new HttpToolParameter(
                "language", "hl",
                HttpPluginEnums.ParameterUseType.BODY.getValue(), // 使用枚举值
                HttpPluginEnums.ParameterType.STRING.getValue(),
                "zh-CN", false, "语言代码（如：'zh-CN'）"));
        // 构建插件方法
        HttpPluginMethod httpPluginMethod = HttpPluginMethod.builder()
                .uri("/search")
                .methodName("searchWeb")
                .methodDescription("使用 Serper API 搜索网络")
                .httpMethodType(HttpPluginEnums.HttpMethod.POST.getValue())
                .parameters(parameters).build();


        // 定义静态请求头
        Map<String, String> staticHeaders = new HashMap<>();
        staticHeaders.put("X-API-KEY", "d420dbfcefdd0cf0261ba09f5a91dc4a35933c59");
        staticHeaders.put("Content-Type", "application/json");

        // 封装http插件，目前这里就一个插件
        HttpPlugin httpPlugin = HttpPlugin.builder()
                .baseUrl("https://google.serper.dev")
                .staticHeaders(staticHeaders)
                .pluginMethods(Arrays.asList(httpPluginMethod))
                .build();


        // -----------------
//        // 封装构建插件
//        HttpPluginMethod searchPlugin = httpPlugin.getPluginMethods().get(0);
//
//        // 转换为HttpToolExecutor需要的配置
//        Map<String, HttpToolExecutor.ParameterConfig> parameterConfigs = new HashMap<>();
//        parameters.forEach(param ->
//                parameterConfigs.put(param.getMethodParamName(), param.toParameterConfig()));
//
//        httpTools.put(
//                buildToolSpecification(searchPlugin.getMethodName(), searchPlugin.getMethodDescription(), parameters),
//                new HttpToolExecutor(
//                        httpPlugin.getBaseUrl() + searchPlugin.getUri(),
//                        searchPlugin.getHttpMethodType(),
//                        httpPlugin.getStaticHeaders(),
//                        parameterConfigs
//                )
//        );
        Map<ToolSpecification, ToolExecutor> httpPluginTools = buildHttpPluginTools(httpPlugin);
        return httpPluginTools;
    }

    public static Map<ToolSpecification, ToolExecutor> buildHttpPluginTools(HttpPlugin httpPlugin) {
        Map<ToolSpecification, ToolExecutor> res = new HashMap<>();

        String baseUrl = httpPlugin.getBaseUrl();
        Map<String, String> staticHeaders = httpPlugin.getStaticHeaders();
        List<HttpPluginMethod> pluginMethods = httpPlugin.getPluginMethods();

        for (HttpPluginMethod httpPluginMethod : pluginMethods) {
            Pair<ToolSpecification, ToolExecutor> pair = buildHttpPluginTool(baseUrl, staticHeaders, httpPluginMethod);
            res.put(pair.getFirst(), pair.getSecond());
        }
        return res;
    }

    private static Pair<ToolSpecification, ToolExecutor> buildHttpPluginTool(String baseUrl, Map<String, String> staticHeaders, HttpPluginMethod httpPluginMethod) {
        String uri = httpPluginMethod.getUri();
        Integer httpMethodType = httpPluginMethod.getHttpMethodType();
        String methodName = httpPluginMethod.getMethodName();
        String methodDescription = httpPluginMethod.getMethodDescription();
        List<HttpToolParameter> parameters = httpPluginMethod.getParameters();

        // 构建toolSpecification
        ToolSpecification toolSpecification = buildToolSpecification(methodName, methodDescription, parameters);
        // 构建HttpToolExecutor
        Map<String, HttpToolExecutor.ParameterConfig> parameterConfigs = new HashMap<>();
        parameters.forEach(param ->
                parameterConfigs.put(param.getMethodParamName(), param.toParameterConfig()));
        HttpToolExecutor httpToolExecutor = new HttpToolExecutor(
                baseUrl + uri,
                httpMethodType,
                staticHeaders,
                parameterConfigs
        );

        return new Pair<>(toolSpecification, httpToolExecutor);
    }


    private static ToolSpecification buildToolSpecification(String methodName, String methodDescription, List<HttpToolParameter> parameters) {
        Map<String, JsonSchemaElement> properties = new HashMap<>();
        List<String> required = new ArrayList<>();

        for (HttpToolParameter param : parameters) {
            properties.put(param.getMethodParamName(), param.toJsonSchemaElement());
            if (param.isRequired()) {
                required.add(param.getMethodParamName());
            }
        }

        return ToolSpecification.builder()
                .name(methodName)
                .description(methodDescription)
                .parameters(JsonObjectSchema.builder()
                        .addProperties(properties)
                        .required(required)
                        .build())
                .build();
    }
}