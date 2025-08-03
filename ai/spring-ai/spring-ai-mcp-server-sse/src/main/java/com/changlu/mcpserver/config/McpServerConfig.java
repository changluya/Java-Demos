package com.changlu.mcpserver.config;

import com.changlu.mcpserver.tools.UserService;
import org.springframework.ai.tool.ToolCallbackProvider;
import org.springframework.ai.tool.method.MethodToolCallbackProvider;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class McpServerConfig {

    @Bean
    public ToolCallbackProvider weatherTools(UserService userService) {
        return MethodToolCallbackProvider.builder().toolObjects(userService).build();
    }

}
