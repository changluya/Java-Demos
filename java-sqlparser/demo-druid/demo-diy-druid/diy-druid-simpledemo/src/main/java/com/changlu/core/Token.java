package com.changlu.core;

import com.changlu.enums.TokenType;

/**
 * 词法分析器生成的每一个词法单元
 */
public class Token {
    private final TokenType type; // Token类型
    private final String value; // Token值

    public Token(TokenType type, String value) {
        this.type = type;
        this.value = value;
    }

    public TokenType getType() {
        return type;
    }

    public String getValue() {
        return value;
    }

    @Override
    public String toString() {
        return "Token{" +
                "type=" + type +
                ", value='" + value + '\'' +
                '}';
    }
}
