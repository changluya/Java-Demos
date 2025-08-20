package com.changlu.langchain4jtools.demo03;

import dev.langchain4j.agent.tool.ToolExecutionRequest;
import dev.langchain4j.service.tool.ToolExecutor;
import lombok.extern.slf4j.Slf4j;
import okhttp3.*;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * A ToolExecutor that executes tools via HTTP requests with support for:
 * - Configurable headers
 * - Query parameters
 * - JSON body
 * - Parameter mappings
 * - Default parameter values
 * - Detailed request/response logging
 */
@Slf4j
public class HttpToolExecutor implements ToolExecutor {

    private final OkHttpClient httpClient;
    private final String baseUrl;
    private final Map<String, String> parameterMappings;
    private final Map<String, String> headers;
    private final HttpMethod method;
    private final boolean useJsonBody;
    private final Map<String, Object> defaultParameters;

    /**
     * @param baseUrl The base URL for the HTTP endpoint
     * @param parameterMappings Maps tool parameter names to HTTP parameter names
     * @param headers Map of HTTP headers to include in requests
     * @param method HTTP method (GET, POST, etc.)
     * @param useJsonBody Whether to send parameters as JSON body (true) or URL query (false)
     * @param defaultParameters Map of default parameter values
     */
    public HttpToolExecutor(String baseUrl,
                            Map<String, String> parameterMappings,
                            Map<String, String> headers,
                            HttpMethod method,
                            boolean useJsonBody,
                            Map<String, Object> defaultParameters) {
        this.httpClient = new OkHttpClient();
        this.baseUrl = baseUrl;
        this.parameterMappings = parameterMappings != null ? parameterMappings : new HashMap<>();
        this.headers = headers != null ? headers : new HashMap<>();
        this.method = method != null ? method : HttpMethod.GET;
        this.useJsonBody = useJsonBody;
        this.defaultParameters = defaultParameters != null ? defaultParameters : new HashMap<>();
    }

    @Override
    public String execute(ToolExecutionRequest toolExecutionRequest, Object memoryId) {
        long startTime = System.currentTimeMillis();  // 记录开始时间

        Map<String, Object> arguments = ToolExecutionRequestUtil.argumentsAsMap(toolExecutionRequest.arguments());

        // Apply default values for missing parameters
        Map<String, Object> finalArguments = applyDefaults(arguments);

        // Build URL and log request details
        String requestUrl = buildUrl(finalArguments);
        logRequestDetails(requestUrl, finalArguments);

        Request.Builder requestBuilder = new Request.Builder()
                .url(requestUrl);

        // Add headers
        headers.forEach(requestBuilder::addHeader);

        // Set request method and body if needed
        RequestBody requestBody = null;
        if (useJsonBody && (method == HttpMethod.POST || method == HttpMethod.PUT || method == HttpMethod.PATCH)) {
            requestBody = createJsonBody(finalArguments);
            requestBuilder.method(method.name(), requestBody);
        } else {
            requestBuilder.method(method.name(), null);
        }

        try (Response response = httpClient.newCall(requestBuilder.build()).execute()) {
            // Process response
            ResponseBody body = response.body();
            String responseContent = body != null ? body.string() : "Empty response";

            long endTime = System.currentTimeMillis();  // 记录结束时间
            long duration = endTime - startTime;  // 计算耗时

            // Log response details with duration
            logResponseDetails(response.code(), responseContent, duration);

            if (!response.isSuccessful()) {
                return "HTTP request failed with code: " + response.code();
            }
            return responseContent;
        } catch (IOException e) {
            long endTime = System.currentTimeMillis();
            log.error("HTTP request execution failed after {} ms", (endTime - startTime), e);
            return "HTTP request failed: " + e.getMessage();
        }
    }

    private void logRequestDetails(String url, Map<String, Object> parameters) {
        log.info("=== HTTP Request Details ===");
        log.info("URL: {}", url);
        log.info("Method: {}", method);
        log.info("Headers: {}", headers);

        if (useJsonBody && (method == HttpMethod.POST || method == HttpMethod.PUT || method == HttpMethod.PATCH)) {
            log.info("Request Body (JSON): {}", ToolExecutionRequestUtil.toJson(parameters));
        } else {
            log.info("Query Parameters: {}", parameters);
        }
        log.info("===========================");
    }

    private void logResponseDetails(int statusCode, String responseContent, long durationMs) {
        log.info("=== HTTP Response Details ===");
        log.info("Status Code: {}", statusCode);
        log.info("Duration: {} ms", durationMs);

        // Truncate very long responses for better readability
        String truncatedResponse = responseContent.length() > 1000
                ? responseContent.substring(0, 1000) + "...[TRUNCATED]"
                : responseContent;

        log.info("Response Body: {}", truncatedResponse);
        log.info("============================");
    }

    private Map<String, Object> applyDefaults(Map<String, Object> providedArguments) {
        Map<String, Object> result = new HashMap<>(defaultParameters);
        result.putAll(providedArguments); // Provided arguments override defaults
        return result;
    }

    private String buildUrl(Map<String, Object> arguments) {
        if (useJsonBody && !method.equals(HttpMethod.GET)) {
            return baseUrl; // No query params for JSON body requests
        }

        StringBuilder urlBuilder = new StringBuilder(baseUrl);
        if (!arguments.isEmpty()) {
            urlBuilder.append("?");

            boolean first = true;
            for (Map.Entry<String, Object> entry : arguments.entrySet()) {
                String toolParamName = entry.getKey();
                String httpParamName = parameterMappings.getOrDefault(toolParamName, toolParamName);

                if (!first) {
                    urlBuilder.append("&");
                }
                first = false;

                urlBuilder.append(httpParamName)
                        .append("=")
                        .append(entry.getValue().toString());
            }
        }
        return urlBuilder.toString();
    }

    private RequestBody createJsonBody(Map<String, Object> arguments) {
        Map<String, Object> mappedParams = new HashMap<>();
        for (Map.Entry<String, Object> entry : arguments.entrySet()) {
            String toolParamName = entry.getKey();
            String httpParamName = parameterMappings.getOrDefault(toolParamName, toolParamName);
            mappedParams.put(httpParamName, entry.getValue());
        }

        String json = ToolExecutionRequestUtil.toJson(mappedParams);
        return RequestBody.create(json, MediaType.parse("application/json"));
    }

    public enum HttpMethod {
        GET, POST, PUT, DELETE, PATCH
    }
}