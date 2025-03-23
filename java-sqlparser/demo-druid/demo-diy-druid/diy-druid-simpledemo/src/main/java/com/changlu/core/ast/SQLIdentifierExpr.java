package com.changlu.core.ast;

// 标识符表达式
public class SQLIdentifierExpr extends SQLExpr{

    private final String name;

    public SQLIdentifierExpr(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        return name;
    }
}
