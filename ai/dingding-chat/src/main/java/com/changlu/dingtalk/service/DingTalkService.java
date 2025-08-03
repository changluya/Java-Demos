package com.changlu.dingtalk.service;

import java.util.Map;

public interface DingTalkService {
    void sendTextMessage(String content, Map<String, Object> atMap);
    void sendMarkdownMessage(String title, String text, Map<String, Object> atMap);
    String processMessage(String content, String senderId);
}