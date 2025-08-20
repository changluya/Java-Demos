package com.changlu.langchain4jtools.demo03.plugin.http.domain;

import com.changlu.langchain4jtools.demo03.plugin.http.HttpToolExecutor;
import com.changlu.langchain4jtools.demo03.plugin.http.enums.HttpPluginEnums;
import dev.langchain4j.model.chat.request.json.*;

/**
 * HTTP工具参数统一配置类
 */
public class HttpToolParameter {
    private final String methodParamName; // 参数名
    private final String methodParamDescription;     // 参数描述
    private final String mappedName;      // 映射后的真实参数名
    private final int useTypeValue;       // 参数使用类型值（QUERY/BODY/PATH/HEADER）
    private final int dataTypeValue;      // 参数数据类型值（STRING/INTEGER/NUMBER/BOOLEAN）
    private final Object defaultValue;    // 默认值
    private final boolean required;       // 是否必填

    public HttpToolParameter(String methodParamName, String mappedName,
                             int useTypeValue, int dataTypeValue,
                             Object defaultValue, boolean required,
                             String methodParamDescription) {
        this.methodParamName = methodParamName;
        this.mappedName = mappedName;
        this.useTypeValue = useTypeValue;
        this.dataTypeValue = dataTypeValue;
        this.defaultValue = defaultValue;
        this.required = required;
        this.methodParamDescription = methodParamDescription;
    }

    // Getters
    public String getMethodParamName() {
        return methodParamName;
    }

    public String getMappedName() {
        return mappedName;
    }

    public int getUseTypeValue() {
        return useTypeValue;
    }

    public int getDataTypeValue() {
        return dataTypeValue;
    }

    public Object getDefaultValue() {
        return defaultValue;
    }

    public boolean isRequired() {
        return required;
    }

    public String getMethodParamDescription() {
        return methodParamDescription;
    }

    /**
     * 转换为ToolSpecification所需的JsonSchemaElement
     */
    public JsonSchemaElement toJsonSchemaElement() {
        HttpPluginEnums.ParameterType dataType = HttpPluginEnums.ParameterType.fromValue(dataTypeValue);

        switch (dataType) {
            case INTEGER:
                return JsonIntegerSchema.builder()
                        .description(methodParamDescription)
                        .build();
            case NUMBER:
                return JsonNumberSchema.builder()
                        .description(methodParamDescription)
                        .build();
            case BOOLEAN:
                return JsonBooleanSchema.builder()
                        .description(methodParamDescription)
                        .build();
            case STRING:
            default:
                return JsonStringSchema.builder()
                        .description(methodParamDescription)
                        .build();
        }
    }

    /**
     * 转换为HttpToolExecutor所需的ParameterConfig
     */
    public HttpToolExecutor.ParameterConfig toParameterConfig() {
        return new HttpToolExecutor.ParameterConfig(
                mappedName,
                HttpPluginEnums.ParameterUseType.fromValue(useTypeValue),
                defaultValue,
                required
        );
    }
}