package com.changlu.dingtalk.service;

public interface AIService {
    String getAIResponse(String query);
    String getAIResponseWithContext(String query, String context);
}