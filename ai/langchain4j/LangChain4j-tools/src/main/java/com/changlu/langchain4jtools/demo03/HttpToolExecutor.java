package com.changlu.langchain4jtools.demo03;

import dev.langchain4j.agent.tool.ToolExecutionRequest;
import dev.langchain4j.service.tool.ToolExecutor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import java.io.IOException;
import java.util.Map;

/**
 * A ToolExecutor that executes tools via HTTP requests with URL parameters
 */
public class HttpToolExecutor implements ToolExecutor {

    private final OkHttpClient httpClient;
    private final String baseUrl;
    private final Map<String, String> parameterMappings;

    /**
     * @param baseUrl The base URL for the HTTP endpoint
     * @param parameterMappings Maps tool parameter names to HTTP parameter names
     */
    public HttpToolExecutor(String baseUrl, Map<String, String> parameterMappings) {
        this.httpClient = new OkHttpClient();
        this.baseUrl = baseUrl;
        this.parameterMappings = parameterMappings;
    }

    @Override
    public String execute(ToolExecutionRequest toolExecutionRequest, Object memoryId) {
        Map<String, Object> arguments = ToolExecutionRequestUtil.argumentsAsMap(toolExecutionRequest.arguments());
        
        // Build URL with parameters
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
        
        Request request = new Request.Builder()
                .url(urlBuilder.toString())
                .build();
        
        try (Response response = httpClient.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                return "HTTP request failed with code: " + response.code();
            }
            return response.body() != null ? response.body().string() : "Empty response";
        } catch (IOException e) {
            return "HTTP request failed: " + e.getMessage();
        }
    }
}