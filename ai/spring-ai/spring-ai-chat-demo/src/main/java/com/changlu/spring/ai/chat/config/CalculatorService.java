package com.changlu.spring.ai.chat.config;

import com.fasterxml.jackson.annotation.JsonClassDescription;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyDescription;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Description;

import java.util.function.Function;

@Configuration
@JsonClassDescription("计算器业务类实现")
public class CalculatorService {

    public record AddOperation(
            @JsonProperty(required = true)
            @JsonPropertyDescription("第一个数字")
            int a,
            @JsonProperty(required = true)
            @JsonPropertyDescription("第二个数字")
            int b) {

    }

    public record MulOperation(
            @JsonProperty(required = true)
            @JsonPropertyDescription("第一个乘数")
            int m,
            @JsonProperty(required = true)
            @JsonPropertyDescription("第二个乘数")
            int n) {

    }

    @Bean
    @Description("加法运算，计算两个整数的和")
    public Function<AddOperation, Integer> addOperation() {
        return request -> {
            return request.a + request.b;
        };
    }

    @Bean
    @Description("乘法运算")
    public Function<MulOperation, Integer> mulOperation() {
        return request -> {
            return request.m * request.n;
        };
    }
}