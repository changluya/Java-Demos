package com.changlu.ai.langchain4j.assistant;

import dev.langchain4j.service.MemoryId;
import dev.langchain4j.service.SystemMessage;
import dev.langchain4j.service.UserMessage;
import dev.langchain4j.service.V;
import dev.langchain4j.service.spring.AiService;
import dev.langchain4j.service.spring.AiServiceWiringMode;

// chatMemoryProvider：用于编写lambda表达式，可实现根据memoryId去实现扩展
@AiService(
        wiringMode = AiServiceWiringMode.EXPLICIT,
        chatModel = "qwenChatModel",
        chatMemory = "chatMemory",
        chatMemoryProvider = "chatMemoryProvider",
        tools = "calculatorTools" //配置tools
)
public interface SeparateChatAssistant {

    /**
     * 分离聊天记录
     * @param memoryId 聊天id
     * @param userMessage 用户消息
     * @return ai回答结果
     */
//    @SystemMessage("你是我的好朋友，请用东北话回答问题。")
    // 系统提示词从文件读取
    @SystemMessage(fromResource = "my-prompt-template.txt")
    @UserMessage("请给我语句中添加一些表情符号。 {{userMessage}}") //若是只有一个参数，{{it}}表示这里客户的入参；若是有多个参数需要补充@V
    String chat(@MemoryId int memoryId, @V("userMessage") String userMessage);

}
