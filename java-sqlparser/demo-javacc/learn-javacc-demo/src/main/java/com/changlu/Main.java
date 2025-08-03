package com.changlu;

import com.changlu.demo01.Adder;
import com.changlu.demo01.ParseException;

public class Main {
    public static void main(String[] args) throws ParseException {
        // 解析表达式 输出最终结果
        long res = Adder.evaluate("1 + 2");
        System.out.println(res);
    }
}