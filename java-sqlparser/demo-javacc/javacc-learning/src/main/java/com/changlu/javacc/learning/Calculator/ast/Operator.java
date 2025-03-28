package com.changlu.javacc.learning.Calculator.ast;

public enum Operator {

    PLUS("+"),
    MINUS("-"),
    MUL("*"),
    DIV("/");

    private final String symbol;

    Operator(String symbol) {
        this.symbol = symbol;
    }

    @Override
    public String toString() {
        return symbol;
    }

}
