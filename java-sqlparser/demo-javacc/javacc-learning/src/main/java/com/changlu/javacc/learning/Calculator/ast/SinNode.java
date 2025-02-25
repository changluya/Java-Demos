package com.changlu.javacc.learning.Calculator.ast;

import com.changlu.javacc.learning.Calculator.visitor.ASTVisitor;

/**
 * 处理余弦值
 */
public class SinNode extends UnaryNode {

  public SinNode(Node node) {
    this.node = node;
  }

  @Override
  public <T> T accept(ASTVisitor<T> visitor) {
    return visitor.visit(this);
  }
}
