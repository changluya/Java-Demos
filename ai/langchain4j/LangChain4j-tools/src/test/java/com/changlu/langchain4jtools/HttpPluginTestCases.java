package com.changlu.langchain4jtools;

import com.changlu.langchain4jtools.demo03.plugin.http.HttpToolExecutor;
import com.changlu.langchain4jtools.demo03.plugin.http.domain.HttpPlugin;
import com.changlu.langchain4jtools.demo03.plugin.http.domain.HttpPluginMethod;
import com.changlu.langchain4jtools.demo03.plugin.http.domain.HttpToolParameter;
import com.changlu.langchain4jtools.demo03.plugin.http.enums.HttpPluginEnums;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

// 测试用例配置类
public class HttpPluginTestCases {
    
    // GET 请求 - 查询参数 + 路径参数
    public static HttpPlugin createGetTestCase() {
        List<HttpToolParameter> parameters = Arrays.asList(
                new HttpToolParameter("userId", "userId",
                        HttpPluginEnums.ParameterUseType.PATH.getValue(), HttpPluginEnums.ParameterType.STRING.getValue(),
                        null, true, "用户ID"),
                new HttpToolParameter("page", "page",
                        HttpPluginEnums.ParameterUseType.QUERY.getValue(), HttpPluginEnums.ParameterType.INTEGER.getValue(),
                        1, false, "页码"),
                new HttpToolParameter("size", "size",
                        HttpPluginEnums.ParameterUseType.QUERY.getValue(), HttpPluginEnums.ParameterType.INTEGER.getValue(),
                        10, false, "每页大小"),
                new HttpToolParameter("authToken", "Authorization",
                        HttpPluginEnums.ParameterUseType.HEADER.getValue(), HttpPluginEnums.ParameterType.STRING.getValue(),
                        null, true, "认证令牌")
        );
        
        HttpPluginMethod method = HttpPluginMethod.builder()
            .methodName("getUserInfo")
            .methodDescription("获取用户信息")
            .httpMethodType(HttpPluginEnums.HttpMethod.GET.getValue())
            .uri("/users/{userId}")
            .parameters(parameters)
            .build();

        return HttpPlugin.builder()
            .baseUrl("http://localhost:8999/api")
            .staticHeaders(Map.of("Content-Type", "application/json"))
            .pluginMethods(List.of(method))
            .build();
    }

    // POST 请求 - Body参数 + Header参数
    public static HttpPlugin createPostTestCase() {
// POST 请求示例
        List<HttpToolParameter> parameters = Arrays.asList(
                new HttpToolParameter("userData", "userData",
                        HttpPluginEnums.ParameterUseType.BODY.getValue(), HttpPluginEnums.ParameterType.STRING.getValue(),
                        null, true, "用户数据"),
                new HttpToolParameter("age", "age",
                        HttpPluginEnums.ParameterUseType.BODY.getValue(), HttpPluginEnums.ParameterType.INTEGER.getValue(),
                        null, false, "用户年龄"),
                new HttpToolParameter("score", "score",
                        HttpPluginEnums.ParameterUseType.BODY.getValue(), HttpPluginEnums.ParameterType.NUMBER.getValue(),
                        null, false, "用户评分")
        );
        
        HttpPluginMethod method = HttpPluginMethod.builder()
            .methodName("createUser")
            .methodDescription("创建用户")
            .httpMethodType(HttpPluginEnums.HttpMethod.POST.getValue())
            .uri("/users")
            .parameters(parameters)
            .build();

        return HttpPlugin.builder()
            .baseUrl("http://localhost:8999/api")
            .staticHeaders(Map.of("X-API-Version", "v1"))
            .pluginMethods(List.of(method))
            .build();
    }

    // PUT 请求 - Body参数 + 路径参数
    public static HttpPlugin createPutTestCase() {
        List<HttpToolParameter> parameters = Arrays.asList(
            new HttpToolParameter("userId", "userId", 
                HttpPluginEnums.ParameterUseType.PATH.getValue(), HttpPluginEnums.ParameterType.STRING.getValue(), null, true, "用户ID"),
            new HttpToolParameter("userData", "userData", 
                HttpPluginEnums.ParameterUseType.BODY.getValue(), HttpPluginEnums.ParameterType.STRING.getValue(),null, true, "用户更新数据"),
            new HttpToolParameter("ifMatch", "If-Match", 
                HttpPluginEnums.ParameterUseType.HEADER.getValue(), HttpPluginEnums.ParameterType.STRING.getValue(),null, false, "ETag验证")
        );

        HttpPluginMethod method = HttpPluginMethod.builder()
            .methodName("updateUser")
            .methodDescription("更新用户信息")
            .httpMethodType(HttpPluginEnums.HttpMethod.PUT.getValue())
            .uri("/users/{userId}")
            .parameters(parameters)
            .build();

        return HttpPlugin.builder()
            .baseUrl("http://localhost:8999/api")
            .staticHeaders(Map.of("Content-Type", "application/json"))
            .pluginMethods(List.of(method))
            .build();
    }

    // DELETE 请求 - 路径参数 + Query参数
    public static HttpPlugin createDeleteTestCase() {
        List<HttpToolParameter> parameters = Arrays.asList(
            new HttpToolParameter("userId", "userId", 
                HttpPluginEnums.ParameterUseType.PATH.getValue(), HttpPluginEnums.ParameterType.STRING.getValue(),null, true, "用户ID"),
            new HttpToolParameter("force", "force", 
                HttpPluginEnums.ParameterUseType.QUERY.getValue(), HttpPluginEnums.ParameterType.STRING.getValue(),"false", false, "强制删除"),
            new HttpToolParameter("authToken", "Authorization", 
                HttpPluginEnums.ParameterUseType.HEADER.getValue(), HttpPluginEnums.ParameterType.STRING.getValue(),null, true, "认证令牌")
        );

        HttpPluginMethod method = HttpPluginMethod.builder()
            .methodName("deleteUser")
            .methodDescription("删除用户")
            .httpMethodType(HttpPluginEnums.HttpMethod.DELETE.getValue())
            .uri("/users/{userId}")
            .parameters(parameters)
            .build();

        return HttpPlugin.builder()
            .baseUrl("http://localhost:8999/api")
            .staticHeaders(Map.of("X-API-Version", "v1"))
            .pluginMethods(List.of(method))
            .build();
    }

    // PATCH 请求 - Body参数 + 路径参数 + Header参数
    public static HttpPlugin createPatchTestCase() {
        List<HttpToolParameter> parameters = Arrays.asList(
            new HttpToolParameter("userId", "userId", 
                HttpPluginEnums.ParameterUseType.PATH.getValue(), HttpPluginEnums.ParameterType.STRING.getValue(),null, true, "用户ID"),
            new HttpToolParameter("patchData", "patchData", 
                HttpPluginEnums.ParameterUseType.BODY.getValue(), HttpPluginEnums.ParameterType.STRING.getValue(),null, true, "部分更新数据"),
            new HttpToolParameter("contentType", "Content-Type", 
                HttpPluginEnums.ParameterUseType.HEADER.getValue(), HttpPluginEnums.ParameterType.STRING.getValue(),"application/json-patch+json", false, "内容类型")
        );

        HttpPluginMethod method = HttpPluginMethod.builder()
            .methodName("patchUser")
            .methodDescription("部分更新用户信息")
            .httpMethodType(HttpPluginEnums.HttpMethod.PATCH.getValue())
            .uri("/users/{userId}")
            .parameters(parameters)
            .build();

        return HttpPlugin.builder()
            .baseUrl("http://localhost:8999/api")
            .staticHeaders(Map.of("X-API-Version", "v1"))
            .pluginMethods(List.of(method))
            .build();
    }
}