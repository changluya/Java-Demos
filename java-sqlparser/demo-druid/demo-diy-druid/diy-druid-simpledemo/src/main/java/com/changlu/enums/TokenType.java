package com.changlu.enums;

public enum TokenType {
    SELECT("select"),     // SELECT 关键字
    FROM("from"),       // FROM 关键字
    WHERE("where"),      // WHERE 关键字
    IDENTIFIER, // 标识符（如表名、列名，主要为字符串类型）
    NUMBER,     // 数字
    COMMA(","),      // 逗号
    GT(">"),         // 大于号 (>)
    LT("<"),         // 小于号 (<)
    EQ("="),         // 等于号 (=)
    EOF;         // 结束符

    public String value;

    TokenType() {
    }

    TokenType(String value) {
        this.value = value;
    }
}