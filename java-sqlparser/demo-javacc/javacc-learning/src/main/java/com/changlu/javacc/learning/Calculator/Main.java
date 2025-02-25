package com.changlu.javacc.learning.Calculator;

import com.changlu.javacc.learning.Calculator.ast.Node;
import com.changlu.javacc.learning.Calculator.parser.Calculator;
import com.changlu.javacc.learning.Calculator.visitor.CalculateVisitor;
import com.changlu.javacc.learning.Calculator.visitor.DumpVisitor;

public class Main {

    public static void main(String[] args) throws Exception{
        Calculator calculator = new Calculator(System.in);
        // 解析为抽象语法树
        Node node = calculator.parse();
        System.out.println(node);

        // 计算表达式值
        CalculateVisitor calculateVisitor = new CalculateVisitor();
        System.out.println(calculateVisitor.calculate(node));

        // 采用visitor模式遍历抽象语法树
        DumpVisitor dumpVisitor = new DumpVisitor();
        dumpVisitor.dump(node);
    }

}
