package com.changlu;

import com.changlu.parser.CalculatorBaseVisitor;
import com.changlu.parser.CalculatorLexer;
import com.changlu.parser.CalculatorParser;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CodePointCharStream;
import org.antlr.v4.runtime.CommonTokenStream;

import java.util.HashMap;
import java.util.Map;

// 计算器的结果是整型，所以访问抽象语法树的每个节点的返回值也是整型
public class CalculatorVisitor extends CalculatorBaseVisitor<Integer> {

    // map 用来保存变量和值的变量关系  a = 1  => {"a": 1}
    // key：变量名
    // value：变量名的所对应的值
    Map<String, Integer> memory = new HashMap<>();

    /**
     * 访问变量名 a = expression
     * @param ctx the parse tree
     * @return 返回表达式的值
     */
    @Override
    public Integer visitAssign(CalculatorParser.AssignContext ctx) {
        // 取出=左边的变量名
        String paramName = ctx.ID().getText();
        // 取出=右边的表达式
        // 根据表达式来求值
        Integer value = visit(ctx.expression());
        memory.put(paramName, value);
        return value;
    }

    // expression NEWLINE
    @Override
    public Integer visitPrintExpression(CalculatorParser.PrintExpressionContext ctx) {
        Integer value = visit(ctx.expression());
        // 每针对带有 expression NEWLINE 情况会进行打印
        System.out.println(ctx.expression().getText() + " = " + value);
        return value;
    }

    // 访问到INT表达式情况
    @Override
    public Integer visitInt(CalculatorParser.IntContext ctx) {
        return Integer.parseInt(ctx.INT().getText());
    }

    // 访问到 '(' expression ')'情况
    @Override
    public Integer visitParens(CalculatorParser.ParensContext ctx) {
        // 直接返回'(' expression ')' 中的表达式的值
        return visit(ctx.expression());
    }

    // 访问到 expression op=('*'|'/') expression 情况
    @Override
    public Integer visitMulDiv(CalculatorParser.MulDivContext ctx) {
        Integer leftVal = visit(ctx.expression(0));
        Integer rightVal = visit(ctx.expression(1));
        if (ctx.op.getType() == CalculatorParser.MUL) {
            return leftVal * rightVal;
        }else {
            return leftVal / rightVal;
        }
    }

    // 访问到 expression op=('+'|'-') expression 情况
    @Override
    public Integer visitAddSub(CalculatorParser.AddSubContext ctx) {
        Integer leftVal = visit(ctx.expression(0));
        Integer rightVal = visit(ctx.expression(1));
        if (ctx.op.getType() == CalculatorParser.ADD) {
            return leftVal + rightVal;
        }else {
            return leftVal - rightVal;
        }
    }

    // 访问到 ID 情况 （最终得到求表达式的值）
    @Override
    public Integer visitId(CalculatorParser.IdContext ctx) {
        String id = ctx.ID().getText();
        if (memory.containsKey(id)) return memory.get(id);
        return 0;
    }

    public static void main(String[] args) {
        String expr = "a = 1\n" +
                "b = 2\n" +
                "c = a + b * 2\n" +
                "d = 3 + c\n" +
                "c\n" +
                "d\n";
        // 1、将字符串转换成流
        CodePointCharStream stream = CharStreams.fromString(expr);
        // 2、实例化词法分析器 进行词法分析
        // 词法分析器初始化
        CalculatorLexer lexer = new CalculatorLexer(stream);
        // 词法分析
        CommonTokenStream tokens = new CommonTokenStream(lexer);
        // 3、实例化语法分析器
        CalculatorParser parser = new CalculatorParser(tokens);
        // 语法分析并转为抽象语法树
        CalculatorParser.ProgramContext tree = parser.program();

        // 进行表达式求值
        CalculatorVisitor visitor = new CalculatorVisitor();
        visitor.visit(tree);
    }

}
