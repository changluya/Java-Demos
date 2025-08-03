package com.changlu;

import com.changlu.grammer.starrocks.StarRocksLexer;
import com.changlu.grammer.starrocks.StarRocksParser;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CodePointCharStream;
import org.antlr.v4.runtime.CommonTokenStream;
//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
public class Main {
    public static void main(String[] args) {
        String expr = "select * from changlu";
        // 1、将字符串转换成流
        CodePointCharStream stream = CharStreams.fromString(expr);
        // 2、实例化词法分析器 进行词法分析
        // 词法分析器初始化
        StarRocksLexer lexer = new StarRocksLexer(stream);
        // 词法分析
        CommonTokenStream tokens = new CommonTokenStream(lexer);
        // 3、实例化语法分析器
        StarRocksParser parser = new StarRocksParser(tokens);
        // 语法分析并转为抽象语法树
        StarRocksParser.SqlStatementsContext sqlStatementsContext = parser.sqlStatements();
        System.out.println(sqlStatementsContext);
    }
}