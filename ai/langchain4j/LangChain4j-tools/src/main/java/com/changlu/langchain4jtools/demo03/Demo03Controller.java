package com.changlu.langchain4jtools.demo03;

import com.changlu.langchain4jtools.assistant.AiAssistant;
import com.changlu.langchain4jtools.demo02.AiBotFactory2;
import com.changlu.langchain4jtools.domain.ChatForm;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

@Tag(name = "demo03 ai控制器")
@RestController
@RequestMapping("/demo03")
public class Demo03Controller {

    private AiAssistant instance;

    @Operation(summary = "对话")
    @PostMapping(value = "/chat", produces = "text/stream;charset=utf-8")
    public Flux<String> chat(@RequestBody ChatForm chatForm) {
        if (instance == null) {
            instance = AiBotFactory3.buildAiAssistant();
        }
        return instance.chat(chatForm.getMessage());
    }

}
