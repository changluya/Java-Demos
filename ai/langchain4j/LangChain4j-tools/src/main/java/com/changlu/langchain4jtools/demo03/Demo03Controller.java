package com.changlu.langchain4jtools.demo03;

import com.changlu.langchain4jtools.assistant.AiAssistant;
import com.changlu.langchain4jtools.demo02.AiBotFactory2;
import com.changlu.langchain4jtools.domain.ChatForm;
import dev.langchain4j.model.chat.response.ChatResponse;
import dev.langchain4j.model.chat.response.StreamingChatResponseHandler;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.scheduler.Schedulers;

import java.util.ArrayList;

@Tag(name = "demo03 ai控制器")
@RestController
@RequestMapping("/demo03")
@Slf4j
public class Demo03Controller {

    private AiAssistant instance;

    @Operation(summary = "对话")
    @PostMapping(value = "/chat", produces = "text/event-stream;charset=utf-8")
    public Flux<String> chat(@RequestBody ChatForm chatForm) {
        String message = chatForm.getMessage();
        if (instance == null) {
            instance = AiBotFactory3.buildAiAssistant();
        }
        return Flux.<String>create(emitter -> {  // Explicit type parameter
                    try {
                        if (StringUtils.isEmpty(message)) {
                            emitter.complete();
                            return;
                        }
                        // 特定ai调用
                        instance.chat(message)
                                .subscribe(
                                        response -> {
                                            log.debug("[LLM-Response] 收到大模型响应片段 | length: {}, content: {}", response.length(), response);
                                            emitter.next("final|CONTENT|" + response);
                                        },
                                        error -> {
                                            log.error("[LLM-Error] 大模型调用异常 |  error: {}", error.getMessage(), error);
                                            emitter.error(error);
                                        },
                                        () -> {
                                            log.info("[LLM-Complete] 大模型调用完成 ");
                                            if (!emitter.isCancelled()) {
                                                emitter.complete();
                                            }
                                        }
                                );
                    }catch (Exception e) {
                        log.error("[Chat-Error] 流式对话处理异常 | error: {}", e.getMessage(), e);
                        emitter.error(e);
                    }
                })
                .onBackpressureBuffer(128) // 设置缓冲区大小
                .doOnCancel(() -> log.warn("[Chat-Cancel] 流式对话被取消"))
                .doOnTerminate(() -> log.info("[Chat-End] 流式对话终止)"))
                .subscribeOn(Schedulers.boundedElastic());
    }

}
