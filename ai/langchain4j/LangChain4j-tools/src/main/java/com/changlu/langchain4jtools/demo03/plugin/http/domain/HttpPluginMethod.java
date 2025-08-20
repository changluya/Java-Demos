package com.changlu.langchain4jtools.demo03.plugin.http.domain;

import lombok.Builder;
import lombok.Data;

import java.util.List;

/**
 * @description  插件方法
 * @author changlu
 * @date 2025/8/19 01:06
 */
@Data
@Builder
public class HttpPluginMethod {

    // 方法名称
    private String methodName;
    // 方法描述
    private String methodDescription;

    // 请求方法类型Code
    private Integer httpMethodType;
    // 请求资源点 例如：https://baidu.com/news，其中uri就是/news
    private String uri;

    // 请求参数集合
    private List<HttpToolParameter> parameters;

}
