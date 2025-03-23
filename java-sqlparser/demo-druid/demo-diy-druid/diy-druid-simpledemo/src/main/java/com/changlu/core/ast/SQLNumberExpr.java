package com.changlu.core.ast;

/**
 * 数字表达式
 */
public class SQLNumberExpr extends SQLExpr{

    private final String value;

    public SQLNumberExpr(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    @Override
    public String toString() {
        return value;
    }

}
