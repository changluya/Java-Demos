package com.changlu.dingtalk.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class DingTalkResponse {
    private String msgtype;
    private Text text;
    private At at;
    
    public DingTalkResponse(String msgtype, Text text, At at) {
        this.msgtype = msgtype;
        this.text = text;
        this.at = at;
    }
    
    @Data
    public static class Text {
        private String content;
        
        public Text(String content) {
            this.content = content;
        }
    }
    
    @Data
    public static class At {
        private String[] atUserIds;
        
        public At(String[] atUserIds) {
            this.atUserIds = atUserIds;
        }
    }
}