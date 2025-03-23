// 通过@header指定package信息的antlr4语法示例
grammar Hello; // 1、定义文法的名字

//2、java package
@header {
package com.changlu.hello;
}

s  : 'hello' ID ;            // 3、匹配关键字hello和标志符
ID : [a-z]+ ;                // 标志符由小写字母组成
WS : [ \t\r\n]+ -> skip ;    // 4、跳过空格、制表符、回车符和换行符