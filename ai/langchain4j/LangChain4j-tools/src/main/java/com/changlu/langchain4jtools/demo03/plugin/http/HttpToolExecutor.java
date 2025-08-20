package com.changlu.langchain4jtools.demo03.plugin.http;

import com.changlu.langchain4jtools.demo03.plugin.http.enums.HttpPluginEnums;
import dev.langchain4j.agent.tool.ToolExecutionRequest;
import dev.langchain4j.service.tool.ToolExecutor;
import lombok.extern.slf4j.Slf4j;
import okhttp3.*;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * HTTP 工具执行器，支持多种参数类型：
 * - Query 参数（URL查询参数）
 * - Body 参数（JSON格式请求体）
 * - Path 参数（URL路径参数）
 * - Header 参数（请求头参数）
 * 支持参数映射和默认值配置
 */
/**
 * HTTP 工具执行器，支持智能判断请求体类型：
 * 1. 当存在BODY类型参数时，自动使用JSON请求体
 * 2. GET/DELETE方法强制使用查询参数
 * 3. 其他方法(POST/PUT/PATCH)根据参数类型自动选择
 */
@Slf4j
public class HttpToolExecutor implements ToolExecutor {

    private final OkHttpClient httpClient;
    private final String baseUrl;
    private final Map<String, ParameterConfig> parameterConfigs;
    private final Map<String, String> staticHeaders;
    private final HttpPluginEnums.HttpMethod method;

    /**
     * 参数配置类
     */
    public static class ParameterConfig {
        public final String mappedName;  // 映射后的参数名
        public final HttpPluginEnums.ParameterUseType type;  // 参数类型
        public final Object defaultValue; // 默认值
        public final boolean required;    // 是否必填

        public ParameterConfig(String mappedName, HttpPluginEnums.ParameterUseType type, Object defaultValue, boolean required) {
            this.mappedName = mappedName;
            this.type = type;
            this.defaultValue = defaultValue;
            this.required = required;
        }
    }


    /**
     * 构造函数
     * @param baseUrl 基础URL
     * @param method HTTP方法
     * @param staticHeaders 静态请求头（固定值）
     * @param parameterConfigs 参数配置映射（参数名 -> 参数配置）
     */
    public HttpToolExecutor(String baseUrl,
                            int methodValue, // 使用数值表示方法
                            Map<String, String> staticHeaders,
                            Map<String, ParameterConfig> parameterConfigs) {
        this.httpClient = new OkHttpClient();
        this.baseUrl = baseUrl;
        this.method = HttpPluginEnums.HttpMethod.fromValue(methodValue); // 转换为枚举
        this.staticHeaders = staticHeaders != null ? staticHeaders : new HashMap<>();
        this.parameterConfigs = parameterConfigs != null ? parameterConfigs : new HashMap<>();
    }

    @Override
    public String execute(ToolExecutionRequest toolExecutionRequest, Object memoryId) {
        long startTime = System.currentTimeMillis();

        // 将工具执行请求的参数转换为Map
        Map<String, Object> arguments = ToolExecutionRequestUtil.argumentsAsMap(toolExecutionRequest.arguments());

        // 处理参数：应用默认值并验证必填参数
        Map<String, Object> finalArguments = processArguments(arguments);

        // 构建包含路径参数的URL
        String url = buildUrlWithPathParams(finalArguments);

        Request.Builder requestBuilder = new Request.Builder().url(url);

        // 添加静态请求头【处理初始插件配置参数】
        staticHeaders.forEach(requestBuilder::addHeader);

        // 处理动态请求头参数【类型为HEADER会封装到请求头中】
        addHeaderParameters(requestBuilder, finalArguments);

        // 智能判断请求体类型并处理参数
        RequestBody requestBody = processParameters(requestBuilder, finalArguments);

        // 记录请求详情
        logRequestDetails(url, requestBuilder, requestBody, finalArguments);

        try (Response response = httpClient.newCall(requestBuilder.build()).execute()) {
            ResponseBody body = response.body();
            String responseContent = body != null ? body.string() : "Empty response";

            long endTime = System.currentTimeMillis();
            long duration = endTime - startTime;

            // 记录响应详情
            logResponseDetails(response.code(), responseContent, duration);

            if (!response.isSuccessful()) {
                return "HTTP请求失败，状态码: " + response.code();
            }
            return responseContent;
        } catch (IOException e) {
            long endTime = System.currentTimeMillis();
            log.error("HTTP请求执行失败，耗时 {} 毫秒", (endTime - startTime), e);
            return "HTTP请求失败: " + e.getMessage();
        }
    }

    /**
     * 智能处理参数并返回请求体（可能为null）
     */
    private RequestBody processParameters(Request.Builder requestBuilder, Map<String, Object> arguments) {
        // 判断是否需要使用JSON请求体
        boolean useJsonBody = shouldUseJsonBody(arguments);

        if (useJsonBody) {
            // 处理JSON请求体参数
            Map<String, Object> bodyParams = new HashMap<>();
            for (Map.Entry<String, ParameterConfig> entry : parameterConfigs.entrySet()) {
                if (entry.getValue().type == HttpPluginEnums.ParameterUseType.BODY && arguments.containsKey(entry.getKey())) {
                    bodyParams.put(entry.getValue().mappedName, arguments.get(entry.getKey()));
                }
            }
            String json = ToolExecutionRequestUtil.toJson(bodyParams);
            RequestBody requestBody = RequestBody.create(json, MediaType.parse("application/json"));
            requestBuilder.method(method.name(), requestBody);
            return requestBody;
        } else {
            // 处理查询参数
            HttpUrl.Builder urlBuilder = HttpUrl.parse(requestBuilder.build().url().toString()).newBuilder();
            for (Map.Entry<String, ParameterConfig> entry : parameterConfigs.entrySet()) {
                if (entry.getValue().type == HttpPluginEnums.ParameterUseType.QUERY && arguments.containsKey(entry.getKey())) {
                    urlBuilder.addQueryParameter(
                            entry.getValue().mappedName,
                            arguments.get(entry.getKey()).toString()
                    );
                }
            }
            requestBuilder.url(urlBuilder.build());
            requestBuilder.method(method.name(), null);
            return null;
        }
    }

    /**
     * 智能判断是否使用JSON请求体
     */
    private boolean shouldUseJsonBody(Map<String, Object> arguments) {
        // GET/DELETE方法强制使用查询参数
        if (method == HttpPluginEnums.HttpMethod.GET || method == HttpPluginEnums.HttpMethod.DELETE) {
            return false;
        }

        // 检查是否存在BODY类型参数
        boolean hasBodyParams = parameterConfigs.values().stream()
                .anyMatch(config -> config.type == HttpPluginEnums.ParameterUseType.BODY &&
                        (arguments.containsKey(config.mappedName) || config.defaultValue != null));

        // POST/PUT/PATCH方法且存在BODY参数时使用JSON请求体
        return hasBodyParams;
    }

    // 以下方法保持不变（与之前版本相同）
    private Map<String, Object> processArguments(Map<String, Object> providedArguments) {
        Map<String, Object> processed = new HashMap<>();
        for (Map.Entry<String, ParameterConfig> entry : parameterConfigs.entrySet()) {
            String paramName = entry.getKey();
            ParameterConfig config = entry.getValue();
            if (providedArguments.containsKey(paramName)) {
                processed.put(paramName, providedArguments.get(paramName));
            } else if (config.defaultValue != null) {
                processed.put(paramName, config.defaultValue);
            } else if (config.required) {
                throw new IllegalArgumentException("缺少必填参数 '" + paramName + "'");
            }
        }
        return processed;
    }

    private String buildUrlWithPathParams(Map<String, Object> arguments) {
        String url = baseUrl;
        for (Map.Entry<String, ParameterConfig> entry : parameterConfigs.entrySet()) {
            if (entry.getValue().type == HttpPluginEnums.ParameterUseType.PATH) {
                String paramName = entry.getKey();
                String placeholder = "{" + entry.getValue().mappedName + "}";
                if (arguments.containsKey(paramName)) {
                    url = url.replace(placeholder, arguments.get(paramName).toString());
                }
            }
        }
        return url;
    }

    private void addHeaderParameters(Request.Builder requestBuilder, Map<String, Object> arguments) {
        for (Map.Entry<String, ParameterConfig> entry : parameterConfigs.entrySet()) {
            if (entry.getValue().type == HttpPluginEnums.ParameterUseType.HEADER && arguments.containsKey(entry.getKey())) {
                requestBuilder.addHeader(
                        entry.getValue().mappedName,
                        arguments.get(entry.getKey()).toString()
                );
            }
        }
    }

    private void logRequestDetails(String url, Request.Builder requestBuilder,
                                   RequestBody requestBody, Map<String, Object> arguments) {
        log.info("=== HTTP 请求详情 ===");
        log.info("URL: {}", url);
        log.info("方法: {}", method);
        Request request = requestBuilder.build();
        log.info("请求头: {}", request.headers().toMultimap());
        Map<HttpPluginEnums.ParameterUseType, Map<String, Object>> paramsByType = arguments.entrySet().stream()
                .collect(Collectors.groupingBy(
                        e -> parameterConfigs.get(e.getKey()).type,
                        Collectors.toMap(
                                e -> parameterConfigs.get(e.getKey()).mappedName,
                                Map.Entry::getValue
                        )
                ));
        paramsByType.forEach((type, params) ->
                log.info("{} 参数: {}", type, params)
        );
        if (requestBody != null) {
            log.info("请求体: {}", requestBody.toString());
        }
        log.info("=====================");
    }

    private void logResponseDetails(int statusCode, String responseContent, long durationMs) {


        log.info("=== HTTP 响应详情 ===");
        log.info("状态码: {}", statusCode);
        log.info("耗时: {} 毫秒", durationMs);
        String truncatedResponse = responseContent.length() > 1000
                ? responseContent.substring(0, 1000) + "...[截断]"
                : responseContent;
        log.info("响应体: {}", truncatedResponse);
        log.info("=====================");
    }
}