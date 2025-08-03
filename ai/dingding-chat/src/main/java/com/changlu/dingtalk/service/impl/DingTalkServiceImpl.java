package com.changlu.dingtalk.service.impl;

import com.changlu.dingtalk.service.DingTalkService;
import com.changlu.dingtalk.util.SignatureUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Service
public class DingTalkServiceImpl implements DingTalkService {

    @Value("${dingtalk.webhook.url}")
    private String webhookUrl;

    @Value("${dingtalk.secret}")
    private String secret;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public void sendTextMessage(String content, Map<String, Object> atMap) {

        try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
            long timestamp = System.currentTimeMillis();
            String sign = SignatureUtil.generateSignature(timestamp, secret);

            String url = webhookUrl + "&timestamp=" + timestamp + "&sign=" + sign;

            Map<String, Object> message = new HashMap<>();
            message.put("msgtype", "text");

            Map<String, String> text = new HashMap<>();
            text.put("content", content);
            message.put("text", text);

            if (!CollectionUtils.isEmpty(atMap)) {
                message.put("at", atMap);
            }

            HttpPost httpPost = new HttpPost(url);
            httpPost.addHeader("Content-Type", "application/json");
            httpPost.setEntity(new StringEntity(objectMapper.writeValueAsString(message), StandardCharsets.UTF_8));

            HttpResponse response = httpClient.execute(httpPost);
            log.info("Message sent. Response: {}", response.getStatusLine().getStatusCode());
        } catch (Exception e) {
            log.error("Failed to send message", e);
        }
    }

    @Override
    public void sendMarkdownMessage(String title, String text, Map<String, Object> atMap) {
        try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
            long timestamp = System.currentTimeMillis();
            String sign = SignatureUtil.generateSignature(timestamp, secret);

            String url = webhookUrl + "&timestamp=" + timestamp + "&sign=" + sign;

            Map<String, Object> message = new HashMap<>();
            message.put("msgtype", "markdown");

            Map<String, String> markdown = new HashMap<>();
            markdown.put("title", title);
            markdown.put("text", text);
            message.put("markdown", markdown);

            if (!CollectionUtils.isEmpty(atMap)) {
                message.put("at", atMap);
            }

            HttpPost httpPost = new HttpPost(url);
            httpPost.addHeader("Content-Type", "application/json");
            httpPost.setEntity(new StringEntity(objectMapper.writeValueAsString(message), StandardCharsets.UTF_8));

            httpClient.execute(httpPost);
        } catch (Exception e) {
            log.error("Failed to send markdown message", e);
        }
    }

    @Override
    public String processMessage(String content, String senderId) {
        // 在实际应用中，这里会调用AI服务
        // 这里简单模拟AI回复
        return "你好！我是AI助手。您说: \"" + content + "\"";
    }
}