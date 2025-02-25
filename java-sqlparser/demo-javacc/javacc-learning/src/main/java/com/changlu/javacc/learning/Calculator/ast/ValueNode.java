package com.changlu.javacc.learning.Calculator.ast;

import com.changlu.javacc.learning.Calculator.visitor.ASTVisitor;

/**
 * 单纯的数字Node节点（整数 or 小数）
 */
public class ValueNode extends Node{

    protected double value;

    public ValueNode(double value) {
        this.value = value;
    }

    public double getValue() {
        return value;
    }

    @Override
    public <T> T accept(ASTVisitor<T> visitor) {
        return visitor.visit(this);
    }

    @Override
    public String toString() {
        return (sign == 1 ? "" : "-") + value;
    }

}
