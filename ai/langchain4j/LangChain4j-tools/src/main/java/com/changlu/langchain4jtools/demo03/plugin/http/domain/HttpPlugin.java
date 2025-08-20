package com.changlu.langchain4jtools.demo03.plugin.http.domain;

import lombok.Builder;
import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
@Builder
public class HttpPlugin {

    // 基础服务名称
    private String baseUrl;

    // 公共请求头
    private Map<String, String> staticHeaders;

    // 包含多个插件方法
    List<HttpPluginMethod> pluginMethods;

}
