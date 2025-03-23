package com.changlu.core.lexer;

import com.changlu.core.Token;
import com.changlu.enums.TokenType;

/**
 * 词法分析器扫描
 */
public class Lexer {

    private final String input;
    private int pos = 0;

    public Lexer(String input) {
        this.input = input.trim();
    }

    // 获取下一个token
    public Token nextToken() {
        if (pos >= input.length()) {
            return new Token(TokenType.EOF, "");
        }

        // 若是空格符，直接跳过
        char ch = input.charAt(pos);
        if (Character.isWhitespace(ch)) {
            pos++;
            return nextToken();
        }

        // 若是字母，则读取标识符
        if (Character.isLetter(ch)) {
            return readIdentifier();
        }

        // 如果是数字，则读取数字
        if (Character.isDigit(ch)) {
            return readNumber();
        }

        if (ch == ',') {
            pos++;
            return new Token(TokenType.COMMA, TokenType.COMMA.value);
        }

        if (ch == '>') {
            pos++;
            return new Token(TokenType.GT, TokenType.GT.value);
        }

        if (ch == '<') {
            pos++;
            return new Token(TokenType.LT, TokenType.LT.value);
        }

        if (ch == '=') {
            pos++;
            return new Token(TokenType.EQ, TokenType.EQ.value);
        }

        if (ch == ';') {
            pos++;
            return new Token(TokenType.EOF, "");
        }

        throw new RuntimeException("Unexpected character: " + ch);
    }

    // 读取标识符
    public Token readIdentifier() {
        StringBuilder sb = new StringBuilder();
        // 读取一组字符 or 变量
        while (pos < input.length() && Character.isLetterOrDigit(input.charAt(pos))) {
            sb.append(input.charAt(pos));
            pos++;
        }

        String value = sb.toString();
        // select 场景
        if (value.equalsIgnoreCase(TokenType.SELECT.value)) {
            return new Token(TokenType.SELECT, value);
        }
        // from 场景
        if (value.equalsIgnoreCase(TokenType.FROM.value)) {
            return new Token(TokenType.FROM, value);
        }
        // where 场景
        if (value.equalsIgnoreCase(TokenType.WHERE.value)) {
            return new Token(TokenType.WHERE, value);
        }
        return new Token(TokenType.IDENTIFIER, value);
    }

    /**
     * 读取数字
     * @return 数字Token
     */
    public Token readNumber() {
        StringBuilder sb = new StringBuilder();
        while (pos < input.length() && Character.isDigit(input.charAt(pos))) {
            sb.append(input.charAt(pos));
            pos++;
        }
        return new Token(TokenType.NUMBER, sb.toString());
    }

}
