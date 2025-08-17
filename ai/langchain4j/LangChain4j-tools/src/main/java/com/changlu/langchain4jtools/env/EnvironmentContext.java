package com.changlu.langchain4jtools.env;

import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import java.time.Duration;

@Component
@Data
public class EnvironmentContext {

    @Autowired
    private Environment environment;

    // dashscope
    public String getDashScopeApiKey() {
        return environment.getRequiredProperty("langchain4j.community.dashscope.chat-model.api-key");
    }

    public String getDashScopeModelName() {
        return environment.getRequiredProperty("langchain4j.community.dashscope.chat-model.model-name");
    }
}