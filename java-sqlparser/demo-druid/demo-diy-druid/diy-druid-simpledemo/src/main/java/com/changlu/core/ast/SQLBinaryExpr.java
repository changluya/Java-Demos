package com.changlu.core.ast;

import com.changlu.enums.TokenType;

// 二元表达式
public class SQLBinaryExpr extends SQLExpr{

    private SQLExpr left;
    private final TokenType operator;
    private SQLExpr right;

    // 初始化
    public SQLBinaryExpr(SQLExpr left, TokenType operator, SQLExpr right) {
        this.left = left;
        this.operator = operator;
        this.right = right;
    }

    public SQLExpr getLeft() {
        return left;
    }

    public TokenType getOperator() {
        return operator;
    }

    public SQLExpr getRight() {
        return right;
    }

    @Override
    public String toString() {
        return left + " " + operator.value + " " + right;
    }


}
