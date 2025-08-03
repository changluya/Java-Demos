package com.changlu.spring.ai.chat.controller;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.model.Generation;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.chat.prompt.SystemPromptTemplate;
import org.springframework.ai.openai.OpenAiChatOptions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
public class DeepSeekController {

    @Autowired
    private ChatModel chatModel;

    @GetMapping
    public String chat(@RequestParam("msg")String msg) {
        return chatModel.call(msg);
    }

    @GetMapping("/openai")
    public String openai(@RequestParam("msg")String msg) {
        ChatResponse call = chatModel.call(
                new Prompt(
                        msg,
                        OpenAiChatOptions.builder()
                                //可以更换成其他大模型，如Anthropic3ChatOptions亚马逊
                                .model("deepseek-chat")
                                .temperature(0.8)
                                .build()
                )
        );
        return call.getResult().getOutput().getContent();
    }

    @GetMapping("/prompt")
    public String prompt(@RequestParam("name")
                         String name,
                         @RequestParam("voice")
                         String voice){
        String userText= """
            给我推荐北京的至少三种美食
            """;
        UserMessage userMessage = new UserMessage(userText);
        String systemText= """
            你是一个美食咨询助手，可以帮助人们查询美食信息。
            你的名字是{name},
            你应该用你的名字和{voice}的饮食习惯回复用户的请求。
            """;
        SystemPromptTemplate systemPromptTemplate = new SystemPromptTemplate(systemText);
        //替换占位符
        Message systemMessage = systemPromptTemplate
                .createMessage(Map.of("name", name, "voice", voice));
        Prompt prompt = new Prompt(List.of(userMessage, systemMessage));
        List<Generation> results = chatModel.call(prompt).getResults();
        return results.stream().map(x->x.getOutput().getContent()).collect(Collectors.joining(""));
    }

    @GetMapping(value = "/chat1", produces = MediaType.APPLICATION_STREAM_JSON_VALUE)
    public String ragJsonText(@RequestParam(value = "userMessage") String userMessage){
        return ChatClient.builder(chatModel)
                .build()
                .prompt()
                .system("""
                    您是算术计算器的代理。
                    您能够支持加法运算、乘法运算等操作，其余功能将在后续版本中添加，如果用户问的问题不支持请告知详情。
                    在提供加法运算、乘法运算等操作之前，您必须从用户处获取如下信息：两个数字，运算类型。
                    请调用自定义函数执行加法运算、乘法运算。
                    请讲中文。
                    """)
                .user(userMessage)
                .functions("addOperation", "mulOperation")
                .call()
                .content();
    }

}