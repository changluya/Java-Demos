package com.changlu.javacc.learning.Calculator.visitor;

import com.changlu.javacc.learning.Calculator.ast.*;

public interface ASTVisitor<T> {

    T visit(ExprNode node);

    T visit(TermNode node);

    T visit(SinNode node);

    T visit(CosNode node);

    T visit(TanNode node);

    T visit(FactorialNode node);

    T visit(ValueNode node);

}
