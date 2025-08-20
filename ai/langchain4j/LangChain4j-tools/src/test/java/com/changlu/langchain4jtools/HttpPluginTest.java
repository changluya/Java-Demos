package com.changlu.langchain4jtools;

import com.changlu.langchain4jtools.demo03.AiBotFactory3;
import com.changlu.langchain4jtools.demo03.plugin.http.ToolExecutionRequestUtil;
import com.changlu.langchain4jtools.demo03.plugin.http.domain.HttpPlugin;
import dev.langchain4j.agent.tool.ToolExecutionRequest;
import dev.langchain4j.agent.tool.ToolSpecification;
import dev.langchain4j.service.tool.ToolExecutor;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class HttpPluginTest {

    @Test
    public void testAllHttpMethods() {
        // 构建所有测试用例
        List<HttpPlugin> testCases = Arrays.asList(
            HttpPluginTestCases.createGetTestCase(),
            HttpPluginTestCases.createPostTestCase(),
            HttpPluginTestCases.createPutTestCase(),
            HttpPluginTestCases.createDeleteTestCase(),
            HttpPluginTestCases.createPatchTestCase()
        );

        for (HttpPlugin plugin : testCases) {
            Map<ToolSpecification, ToolExecutor> tools =
                AiBotFactory3.buildHttpPluginTools(plugin);
            
            // 测试每个插件方法
            for (Map.Entry<ToolSpecification, ToolExecutor> entry : tools.entrySet()) {
                ToolSpecification spec = entry.getKey();
                ToolExecutor executor = entry.getValue();
                
                System.out.println("Testing: " + spec.name());
                
                // 构建测试请求
                ToolExecutionRequest request = ToolExecutionRequest.builder()
                    .name(spec.name())
                    .arguments(buildTestArguments(spec))
                    .build();
                
                try {
                    String result = executor.execute(request, null);
                    System.out.println("Result: " + result);
                } catch (Exception e) {
                    System.out.println("Error: " + e.getMessage());
                }
            }
        }
    }

    private String buildTestArguments(ToolSpecification spec) {
        Map<String, Object> args = new HashMap<>();
        
        // 根据方法名称构建不同的测试参数
        switch (spec.name()) {
            case "getUserInfo":
                args.put("userId", "123");
                args.put("authToken", "Bearer token123");
                break;
            case "createUser":
                args.put("userData", Map.of("name", "John", "email", "john@example.com"));
                args.put("traceId", "trace-123");
                break;
            case "updateUser":
                args.put("userId", "456");
                args.put("userData", Map.of("name", "Updated Name"));
                break;
            case "deleteUser":
                args.put("userId", "789");
                args.put("authToken", "Bearer token456");
                break;
            case "patchUser":
                args.put("userId", "101");
                args.put("patchData", Map.of("op", "replace", "path", "/name", "value", "Patched"));
                break;
        }
        
        return ToolExecutionRequestUtil.toJson(args);
    }
}