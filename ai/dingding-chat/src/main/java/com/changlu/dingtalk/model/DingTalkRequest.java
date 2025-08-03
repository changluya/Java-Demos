package com.changlu.dingtalk.model;

import lombok.Data;

@Data
public class DingTalkRequest {
    private String msgtype;
    private Text text;
    private String chatbotUserId;
    private String senderId;
    private String conversationId;
    
    @Data
    public static class Text {
        private String content;
    }
}