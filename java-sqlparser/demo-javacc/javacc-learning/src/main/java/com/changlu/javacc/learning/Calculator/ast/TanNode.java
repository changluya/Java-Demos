package com.changlu.javacc.learning.Calculator.ast;

import com.changlu.javacc.learning.Calculator.visitor.ASTVisitor;

/**
 * 处理tan函数
 */
public class TanNode extends UnaryNode {

  public TanNode(Node node) {
    this.node = node;
  }

  @Override
  public <T> T accept(ASTVisitor<T> visitor) {
    return visitor.visit(this);
  }
}
