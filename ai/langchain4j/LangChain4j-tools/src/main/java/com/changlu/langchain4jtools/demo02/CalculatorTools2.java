package com.changlu.langchain4jtools.demo02;

import dev.langchain4j.agent.tool.P;
import dev.langchain4j.agent.tool.Tool;
import org.springframework.stereotype.Component;

/**
 * @description 自定义计算器
 * @author changlu
 * @date 2025/8/16 17:36
 */
public class CalculatorTools2 {

    public double sum(Double a, Double b) {
        System.out.println("调用加法运算 ");
        return a + b;
    }

    public double squareRoot(Double x) {
        System.out.println("调用平方根运算 ");
        return Math.sqrt(x);
    }

}
