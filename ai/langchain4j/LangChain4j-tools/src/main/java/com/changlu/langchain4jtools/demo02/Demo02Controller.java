package com.changlu.langchain4jtools.demo02;

import com.changlu.langchain4jtools.assistant.AiAssistant;
import com.changlu.langchain4jtools.demo01.AiBotFactory;
import com.changlu.langchain4jtools.domain.ChatForm;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

@Tag(name = "demo02 ai控制器")
@RestController
@RequestMapping("/demo02")
public class Demo02Controller {

    private AiAssistant instance;

    @Operation(summary = "对话")
    @PostMapping(value = "/chat", produces = "text/stream;charset=utf-8")
    public Flux<String> chat(@RequestBody ChatForm chatForm) {
        if (instance == null) {
            instance = AiBotFactory2.buildAiAssistant();
        }
        return instance.chat(chatForm.getMessage());
    }

}
