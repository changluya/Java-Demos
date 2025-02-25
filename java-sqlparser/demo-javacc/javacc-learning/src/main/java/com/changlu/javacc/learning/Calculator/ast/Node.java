package com.changlu.javacc.learning.Calculator.ast;

import com.changlu.javacc.learning.Calculator.visitor.ASTVisitor;

/**
 * 抽象节点Node
 */
public abstract class Node {

    protected int sign = 1;

    public int getSign() {
        return sign;
    }

    public void setSign(int sign) {
        this.sign = sign;
    }

    public abstract <T> T accept(ASTVisitor<T> visitor);
}
