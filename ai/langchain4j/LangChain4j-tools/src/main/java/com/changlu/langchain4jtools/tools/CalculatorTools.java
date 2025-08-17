package com.changlu.langchain4jtools.tools;

import dev.langchain4j.agent.tool.P;
import dev.langchain4j.agent.tool.Tool;
import dev.langchain4j.agent.tool.ToolMemoryId;
import org.springframework.stereotype.Component;

/**
 * @description  方式一：bean模式去创建tools，去注入到aiservice
 * @author changlu
 * @date 2025/8/16 17:36
 */
@Component
public class CalculatorTools {

    @Tool(name = "sum", value = "返回两个参数相加之和")
    double sum(
            @P(value="加数1", required = true) Double a,
            @P(value="加数2", required = true) Double b) {
        System.out.println("调用加法运算 ");
        return a + b;
    }

    @Tool(name = "squareRoot", value = "返回给定参数的平方根")
    double squareRoot(Double x) {
        System.out.println("调用平方根运算 ");
        return Math.sqrt(x);
    }

}
