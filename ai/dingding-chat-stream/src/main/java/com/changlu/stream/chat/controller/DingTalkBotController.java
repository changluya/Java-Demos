package com.changlu.stream.chat.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

@RestController
public class DingTalkBotController {

    // 钉钉Stream模式配置参数（从钉钉后台获取）
    private static final String CLIENT_ID = "your_client_id";
    private static final String CLIENT_SECRET = "your_client_secret";
    private static final String BOT_APP_KEY = "dingvaxy13w4mfqzi4mm";
    private static final String BOT_APP_SECRET = "dmkLhKG1OV3-lpa5bKrfk4LolreEFkA-pRoviOe-G4liJJWqZB1Ffgb0_LePA1Kj";
    
    // 钉钉API地址
    private static final String TOKEN_URL = "https://api.dingtalk.com/v1.0/oauth2/accessToken";
    private static final String STREAM_URL = "https://api.dingtalk.com/v1.0/robot/oToMessages/batchSend";
    
    private final RestTemplate restTemplate = new RestTemplate();
    private final ObjectMapper objectMapper = new ObjectMapper();
    
    // 缓存accessToken
    private String accessToken;
    private long tokenExpireTime;

    /**
     * 钉钉消息推送入口
     */
    @PostMapping("/dingtalk/webhook")
    public ResponseEntity<Map<String, Object>> handleDingTalkMessage(@RequestBody Map<String, Object> payload) {
        try {
            // 1. 验证请求签名（生产环境必须实现）
            // verifySignature(request);
            
            // 2. 解析消息内容
            String message = parseMessage(payload);
            String senderId = (String) ((Map<?, ?>) payload.get("senderId")).get("staffId");
            String conversationId = (String) payload.get("conversationId");
            
            // 3. 处理业务逻辑（示例：简单回声）
            String reply = "收到你的消息: " + message;
            
            // 4. 构造回复消息
            Map<String, Object> replyMsg = buildReplyMessage(reply, conversationId, senderId);
            
            // 5. 发送回复
            sendReply(replyMsg);
            
            return ResponseEntity.ok(Collections.singletonMap("status", "success"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Collections.singletonMap("error", e.getMessage()));
        }
    }
    
    /**
     * 解析消息内容
     */
    private String parseMessage(Map<String, Object> payload) {
        if ("text".equals(payload.get("msgtype"))) {
            return (String) ((Map<?, ?>) payload.get("text")).get("content");
        }
        return "不支持的消息类型";
    }
    
    /**
     * 构建回复消息
     */
    private Map<String, Object> buildReplyMessage(String content, String conversationId, String receiverId) {
        Map<String, Object> msg = new HashMap<>();
        msg.put("msgKey", "sampleText");
        msg.put("msgParam", "{\"content\":\"" + content + "\"}");
        
        Map<String, Object> receiver = new HashMap<>();
        receiver.put("appUserId", receiverId);
        receiver.put("conversationType", "1"); // 1:单聊 2:群聊
        
        Map<String, Object> conversation = new HashMap<>();
        conversation.put("conversationId", conversationId);
        conversation.put("openConversationId", conversationId);
        
        msg.put("receiverList", Collections.singletonList(receiver));
        msg.put("robotCode", BOT_APP_KEY);
        msg.put("conversation", conversation);
        return msg;
    }
    
    /**
     * 发送回复消息
     */
    private void sendReply(Map<String, Object> message) throws Exception {
        // 获取AccessToken
        if (accessToken == null || System.currentTimeMillis() > tokenExpireTime) {
            refreshAccessToken();
        }
        
        // 设置请求头
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(accessToken);
        
        // 发送请求
        HttpEntity<Map<String, Object>> request = new HttpEntity<>(message, headers);
        ResponseEntity<String> response = restTemplate.postForEntity(STREAM_URL, request, String.class);
        
        // 处理响应
        if (!response.getStatusCode().is2xxSuccessful()) {
            throw new RuntimeException("消息发送失败: " + response.getBody());
        }
    }
    
    /**
     * 刷新AccessToken
     */
    private void refreshAccessToken() throws Exception {
        Map<String, String> tokenRequest = new HashMap<>();
        tokenRequest.put("appKey", BOT_APP_KEY);
        tokenRequest.put("appSecret", BOT_APP_SECRET);
        
        HttpEntity<Map<String, String>> request = new HttpEntity<>(tokenRequest);
        ResponseEntity<String> response = restTemplate.postForEntity(TOKEN_URL, request, String.class);
        
        if (response.getStatusCode().is2xxSuccessful()) {
            JsonNode root = objectMapper.readTree(response.getBody());
            accessToken = root.path("accessToken").asText();
            long expiresIn = root.path("expireIn").asLong() * 1000; // 转换为毫秒
            tokenExpireTime = System.currentTimeMillis() + expiresIn - 60000; // 提前1分钟过期
        } else {
            throw new RuntimeException("获取AccessToken失败: " + response.getBody());
        }
    }
    
    /**
     * 验证签名（生产环境必须实现）
     */
    /*
    private void verifySignature(HttpServletRequest request) {
        // 从header获取时间戳和签名
        String timestamp = request.getHeader("Timestamp");
        String sign = request.getHeader("Sign");
        
        // 使用ClientSecret计算签名
        String stringToSign = timestamp + "\n" + CLIENT_SECRET;
        String computedSign = HmacSHA256.sign(stringToSign, CLIENT_SECRET);
        
        if (!computedSign.equals(sign)) {
            throw new SecurityException("签名验证失败");
        }
    }
    */
}