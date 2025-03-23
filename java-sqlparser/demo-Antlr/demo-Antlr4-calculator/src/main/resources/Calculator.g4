// 带赋值的计算器的语法定义文件
grammar Calculator; // 语法的名字

@header {
    package com.changlu.parser;
}

// EBNF语法表示法
// 程序program是由一系列语句构成的
// +表示1个或多个
program: statement+;

// 语句： 三种描述方式
//   - 表达式 换行符
//   - 赋值语句：标识符 '=' 换行符
//   - 换行符
// 注意：#在这里并不是注释的意思，在antlr中 # 可以给语法规则取名字
statement: expression NEWLINE           # printExpression
         | ID '=' expression NEWLINE    # assign
         | NEWLINE                     # blank
         ;

// 编写表达式
// INT表示整型、ID表示变量名（自定义的）
// op是我们给运算符取的名字
expression : expression op=('*'|'/') expression  # MulDiv
           | expression op=('+'|'-') expression  # AddSub
           | INT                                 # int
           | ID                                  # id
           | '(' expression ')'                  # parens
           ;

// 变量名：一个或者多个大小写字母
ID : [a-zA-Z]+;
// 整型
INT : [0-9]+;
// 换行符 \r?表示0个或者1个回车符
NEWLINE : '\r'? '\n';
// 空白字符
// [ \t]+ 含义如下：
//      [ ]：表示字符集合，匹配方括号内的任意一个字符。
//      （空格）：表示空格字符。
//       \t：表示制表符（Tab 键产生的字符）。
//      [ \t]：表示匹配一个空格字符或一个制表符。
//      +：表示前面的字符集合 [ \t] 可以出现一次或多次。
// -> skip 是 ANTLR 的一个指令，表示当解析器遇到匹配此规则的输入时，会跳过这些字符，不将它们作为 Token 传递给语法分析器。
WS : [ \t]+ -> skip;

//实际上面expression的语法规则中，可以使用MUL、DIV、ADD、SUB 代替，但是为了简洁，可以不使用
MUL: '*';
DIV: '/';
ADD: '+';
SUB: '-';
