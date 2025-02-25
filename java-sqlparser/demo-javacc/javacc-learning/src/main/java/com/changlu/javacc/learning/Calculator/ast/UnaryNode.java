package com.changlu.javacc.learning.Calculator.ast;

public abstract class UnaryNode extends Node {

  protected Node node;

  public Node getNode() {
    return node;
  }
}
