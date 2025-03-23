package com.changlu.core.parser;

import com.changlu.core.ast.*;
import com.changlu.core.ast.statement.SelectStatement;
import com.changlu.core.lexer.Lexer;
import com.changlu.core.Token;
import com.changlu.enums.TokenType;
import com.sun.org.apache.bcel.internal.generic.Select;

import java.util.ArrayList;
import java.util.List;

public class Parser {

    private final Lexer lexer;// 词法分析器
    private Token currentToken; // 当前Token

    public Parser(Lexer lexer) {
        this.lexer = lexer;
        // 获取当前第一个Token标识
        this.currentToken = lexer.nextToken();
    }

    /**
     * 解析整个select 语句
     * @return SelectStatement
     */
    public SelectStatement parse() {
        // 初始化对象
        SelectStatement stmt = new SelectStatement();
        match(TokenType.SELECT); // 匹配 SELECT 关键字
        stmt.setSelectList(parseSelectList()); // 解析select列表
        match(TokenType.FROM);   // 匹配 FROM 关键字
        stmt.setFrom(parseTable()); // 解析表名
        // 针对于where情况可能不会出现
        if (currentToken.getType() == TokenType.WHERE) {
            match(TokenType.WHERE);
            stmt.setWhere(parseExpr());
        }
        return stmt;
    }

    // 解析select列表
    private List<SQLExpr> parseSelectList() {
        List<SQLExpr> selectList = new ArrayList<>();
        selectList.add(parseExpr()); // 解析得到第一个表达式
        while (currentToken.getType() == TokenType.COMMA) {
            match(TokenType.COMMA);
            selectList.add(parseExpr()); // 解析得到下一个表达式
        }
        return selectList;
    }

    // 解析表名
    private SQLTableSource parseTable() {
        String tableName = currentToken.getValue();
        match(TokenType.IDENTIFIER); // 匹配表名
        return new SQLTableSource(tableName);
    }

    // 解析表达式
    private SQLExpr parseExpr() {
        SQLExpr expr = parsePrimary(); // 解析基本表达式
        if (currentToken.getType() == TokenType.GT ||
                currentToken.getType() == TokenType.LT ||
                currentToken.getType() == TokenType.EQ) {
            TokenType operator = currentToken.getType();
            match(operator); // 匹配运算符
            SQLExpr right = parsePrimary(); // 解析右表达式
            expr = new SQLBinaryExpr(expr, operator, right);
        }
        return expr;
    }

    // 解析基本表达式
    private SQLExpr parsePrimary() {
        if (currentToken.getType() == TokenType.IDENTIFIER) {
            SQLExpr expr = new SQLIdentifierExpr(currentToken.getValue());
            match(TokenType.IDENTIFIER);
            return expr;
        }else if (currentToken.getType() == TokenType.NUMBER) {
            SQLExpr expr = new SQLNumberExpr(currentToken.getValue());
            match(TokenType.NUMBER);
            return expr;
        }else {
            // 用于提示当前错误的Token位置
            throw new RuntimeException("Unexpected token: " + currentToken);
        }
    }

    // 匹配当前的 Token 并移动到下一个Token
    private void match(TokenType excepted) {
        if (currentToken.getType() == excepted) {
            currentToken = lexer.nextToken();
        }else {
            throw new RuntimeException("Unexpected token: " + currentToken);
        }
    }

}
