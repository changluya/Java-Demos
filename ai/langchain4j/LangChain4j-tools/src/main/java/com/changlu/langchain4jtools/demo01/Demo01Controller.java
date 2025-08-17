package com.changlu.langchain4jtools.demo01;

import com.changlu.langchain4jtools.assistant.AiAssistant;
import com.changlu.langchain4jtools.domain.ChatForm;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

@Tag(name = "demo01 ai控制器")
@RestController
@RequestMapping("/demo01")
public class Demo01Controller {

    private AiAssistant instance;

    @Operation(summary = "对话")
    @PostMapping(value = "/chat", produces = "text/stream;charset=utf-8")
    public Flux<String> chat(@RequestBody ChatForm chatForm) {
        if (instance == null) {
            instance = AiBotFactory.buildAiAssistant();
        }
        return instance.chat(chatForm.getMessage());
    }

}
