package com.changlu.javacc.learning.Calculator.ast;

import com.changlu.javacc.learning.Calculator.visitor.ASTVisitor;

/**
 * 阶乘Node
 */
public class FactorialNode extends UnaryNode {

  public FactorialNode(Node node) {
    this.node = node;
  }

  @Override
  public String toString() {
    if (node instanceof ValueNode) {
      return (sign == 1 ? "" : "-") + node + "!";
    } else {
      return (sign == 1 ? "" : "-") + "(" + node + ")" + "!";
    }
  }

  @Override
  public <T> T accept(ASTVisitor<T> visitor) {
    return visitor.visit(this);
  }
}
