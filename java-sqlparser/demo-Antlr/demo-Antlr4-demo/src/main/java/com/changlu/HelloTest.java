package com.changlu;

import com.changlu.hello.HelloLexer;
import com.changlu.hello.HelloParser;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.tree.ParseTree;

public class HelloTest {
    public static void main(String[] args) throws Exception {
//        testStr("hello world");
        // 错误语法
        testStr("hello 123");
    }

    public static void testStr(String str) {
        HelloLexer lexer = new HelloLexer(CharStreams.fromString(str));
        CommonTokenStream tokens = new CommonTokenStream(lexer);
        HelloParser parser = new HelloParser(tokens);
        ParseTree tree = parser.s();
        System.out.println(tree.toStringTree(parser));
    }

}