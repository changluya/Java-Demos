package com.changlu.javacc.learning.Calculator.ast;

import com.changlu.javacc.learning.Calculator.visitor.ASTVisitor;

public class CosNode extends UnaryNode {

  public CosNode(Node node) {
    this.node = node;
  }

  @Override
  public <T> T accept(ASTVisitor<T> visitor) {
    return visitor.visit(this);
  }
}
