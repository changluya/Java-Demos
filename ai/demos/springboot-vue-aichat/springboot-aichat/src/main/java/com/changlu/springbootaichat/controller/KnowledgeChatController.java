package com.changlu.springbootaichat.controller;

import com.changlu.springbootaichat.assistant.KnowledgeAgent;
import com.changlu.springbootaichat.vo.ChatForm;
import dev.langchain4j.model.chat.ChatModel;
import dev.langchain4j.model.chat.StreamingChatModel;
import dev.langchain4j.model.chat.response.ChatResponse;
import dev.langchain4j.model.chat.response.StreamingChatResponseHandler;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.scheduler.Schedulers;

@Tag(name = "知识库agent")
@RestController
@RequestMapping("/knowledge")
public class KnowledgeChatController {

    @Autowired
    private KnowledgeAgent knowledgeAgent;

    @Autowired
    private StreamingChatModel streamingChatModel;

    @Autowired
    private ChatModel chatModel;

    @Operation(summary = "对话")
    @GetMapping(value = "/chat")
    public String chat(@RequestParam("msg")String msg) {
        return chatModel.chat(msg);
    }

    @Operation(summary = "对话")
    @PostMapping(value = "/chatAgent", produces = "text/stream;charset=utf-8")
    public Flux<String> chat(@RequestBody ChatForm chatForm) {
        return knowledgeAgent.chat(chatForm.getMessage());
    }

    @Operation(summary = "流式对话（含思考过程）")
    @PostMapping(value = "/chatStream", produces = "text/event-stream;charset=utf-8") // sse标准格式
    public Flux<String> chatStream(@RequestBody ChatForm chatForm) {

        return Flux.<String>create(emitter -> {  // Explicit type parameter
//            sleep(1500);
//            // 阶段1：知识库检索
//            String knowledgeResult = "检索到知识库文档《XXX系统使用手册》中关于XX功能的说明：...";
//            emitter.next("knowledge|CONTENT|" + knowledgeResult);
//            sleep(1500);
//            emitter.next("knowledge|CONTENT|==> end");
//            sleep(1500);
//
//            // 阶段2：禅道检索
//            String zentaoResult = "禅道系统中未发现与当前问题相关的历史Bug记录";
//            emitter.next("zentao|CONTENT|" + zentaoResult);
//            sleep(1500);
//            emitter.next("zentao|CONTENT|==> end");
//            sleep(1500);
//
//            // 阶段3：思考过程
//            String analysisResult = "综合知识库信息，需要从技术实现、业务逻辑、用户场景三个维度进行解答...\n\n";
//            emitter.next("thinking|CONTENT|" + analysisResult);

            // 阶段4：大模型流式回答
            streamingChatModel.chat(chatForm.getMessage(), new StreamingChatResponseHandler() {
                @Override
                public void onPartialResponse(String partialResponse) {
                    emitter.next("final|CONTENT|" + partialResponse);
                }

                @Override
                public void onCompleteResponse(ChatResponse completeResponse) {
                    emitter.complete();
                }

                @Override
                public void onError(Throwable error) {
                    emitter.error(error);
                }
            });
        }).subscribeOn(Schedulers.boundedElastic());
    }

    // 模拟延时的工具方法（非阻塞当前线程）
    private void sleep(long millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }


}