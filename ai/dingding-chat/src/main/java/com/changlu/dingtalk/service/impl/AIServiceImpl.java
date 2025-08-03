package com.changlu.dingtalk.service.impl;

import com.changlu.dingtalk.service.AIService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class AIServiceImpl implements AIService {

    @Value("${ai.service.url}")
    private String aiServiceUrl;

    private final RestTemplate restTemplate = new RestTemplate();

    @Override
    public String getAIResponse(String query) {
        // 在实际应用中，这里会调用真正的AI服务API
        // 这里返回模拟响应
        return "AI回复: " + query;
        
        // 实际调用代码示例:
        // Map<String, String> request = new HashMap<>();
        // request.put("query", query);
        // return restTemplate.postForObject(aiServiceUrl, request, String.class);
    }

    @Override
    public String getAIResponseWithContext(String query, String context) {
        // 在实际应用中，这里会调用真正的AI服务API，并传递上下文
        return "AI回复(带上下文): " + query + " [上下文: " + context + "]";
    }
}