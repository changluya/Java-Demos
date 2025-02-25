/* Generated By:JavaCC: Do not edit this line. Calculator.java */
package com.changlu.javacc.learning.Calculator.parser;

import com.changlu.javacc.learning.Calculator.ast.*;

public class Calculator implements CalculatorConstants {

    public Node parse() throws ParseException {
        return expr();
    }

// 解析器描述
// expr() 方法：处理加法和减法操作。
// 第一个{} 定义临时变量
// 第二个{} 编写解析到指定语法情况转换任务元素值赋值给临时变量
  final public Node expr() throws ParseException {
    Node left;  // 左侧子节点
    Node right;
    left = term();
    label_1:
    while (true) {
      if (jj_2_1(2)) {
        ;
      } else {
        break label_1;
      }
      if (jj_2_2(2)) {
        jj_consume_token(10);
        right = expr();
          {if (true) return new ExprNode(left, right, Operator.PLUS);}
      } else if (jj_2_3(2)) {
        jj_consume_token(11);
        right = expr();
          {if (true) return new ExprNode(left, right, Operator.MINUS);}
      } else {
        jj_consume_token(-1);
        throw new ParseException();
      }
    }
      {if (true) return left;}
    throw new Error("Missing return statement in function");
  }

// term() 方法：处理乘法和除法操作。
  final public Node term() throws ParseException {
    Node left; // 左侧子节点
    Node right;
    left = primary();
    label_2:
    while (true) {
      if (jj_2_4(2)) {
        ;
      } else {
        break label_2;
      }
      if (jj_2_5(2)) {
        jj_consume_token(12);
        right = term();
          {if (true) return new TermNode(left, right, Operator.MUL);}
      } else if (jj_2_6(2)) {
        jj_consume_token(13);
        right = term();
          {if (true) return new TermNode(left, right, Operator.DIV);}
      } else {
        jj_consume_token(-1);
        throw new ParseException();
      }
    }
      {if (true) return left;}
    throw new Error("Missing return statement in function");
  }

// primary解析方法：处理基本元素，如数字、括号内的表达式、负号、阶乘和三角函数。
//        解析数学表达式中的基本元素（如数字、括号内的表达式、以及一些函数调用）
  final public Node primary() throws ParseException {
    Token t;
    Token p;
    Node n;
    if (jj_2_7(2147483647)) {
      // 确保接下来的输入是NUMBER后跟"!"
          t = jj_consume_token(NUMBER);
      jj_consume_token(14);
        String value = t.image; // 获取Token的字符串表示
        double number = Double.parseDouble(value);  // 将字符串转换为double
        {if (true) return new FactorialNode(new ValueNode(number));}  // 返回一个新的阶乘节点

    } else if (jj_2_8(2)) {
      // 情况2：单独的数字
          t = jj_consume_token(NUMBER);
        double number = Double.parseDouble(t.image);  // 将NUMBER类型的Token转换为double
        {if (true) return new ValueNode(number);}  // 返回一个值节点

    } else if (jj_2_9(2147483647)) {
      jj_consume_token(15);
      n = expr();
      jj_consume_token(16);
      jj_consume_token(14);
        {if (true) return new FactorialNode(n);}  // 返回一个新的阶乘节点

    } else if (jj_2_10(2)) {
      jj_consume_token(10);
      n = primary();
        {if (true) return n;}  // 直接返回子节点

    } else if (jj_2_11(2)) {
      jj_consume_token(11);
      n = primary();
        n.setSign(-1);  // 设置节点的符号为-1
        {if (true) return n;}       // 返回修改后的节点

    } else if (jj_2_12(2)) {
      jj_consume_token(15);
      n = expr();
      jj_consume_token(16);
        {if (true) return n;}  // 返回括号内的表达式节点

    } else if (jj_2_13(2)) {
      jj_consume_token(SIN);
      jj_consume_token(15);
      n = expr();
      jj_consume_token(16);
        {if (true) return new SinNode(n);}  // 返回一个新的正弦节点

    } else if (jj_2_14(2)) {
      jj_consume_token(COS);
      jj_consume_token(15);
      n = expr();
      jj_consume_token(16);
        {if (true) return new CosNode(n);}  // 返回一个新的余弦节点

    } else if (jj_2_15(2)) {
      jj_consume_token(TAN);
      jj_consume_token(15);
      n = expr();
      jj_consume_token(16);
        {if (true) return new TanNode(n);}  // 返回一个新的正切节点

    } else {
      jj_consume_token(-1);
      throw new ParseException();
    }
    throw new Error("Missing return statement in function");
  }

  private boolean jj_2_1(int xla) {
    jj_la = xla; jj_lastpos = jj_scanpos = token;
    try { return !jj_3_1(); }
    catch(LookaheadSuccess ls) { return true; }
    finally { jj_save(0, xla); }
  }

  private boolean jj_2_2(int xla) {
    jj_la = xla; jj_lastpos = jj_scanpos = token;
    try { return !jj_3_2(); }
    catch(LookaheadSuccess ls) { return true; }
    finally { jj_save(1, xla); }
  }

  private boolean jj_2_3(int xla) {
    jj_la = xla; jj_lastpos = jj_scanpos = token;
    try { return !jj_3_3(); }
    catch(LookaheadSuccess ls) { return true; }
    finally { jj_save(2, xla); }
  }

  private boolean jj_2_4(int xla) {
    jj_la = xla; jj_lastpos = jj_scanpos = token;
    try { return !jj_3_4(); }
    catch(LookaheadSuccess ls) { return true; }
    finally { jj_save(3, xla); }
  }

  private boolean jj_2_5(int xla) {
    jj_la = xla; jj_lastpos = jj_scanpos = token;
    try { return !jj_3_5(); }
    catch(LookaheadSuccess ls) { return true; }
    finally { jj_save(4, xla); }
  }

  private boolean jj_2_6(int xla) {
    jj_la = xla; jj_lastpos = jj_scanpos = token;
    try { return !jj_3_6(); }
    catch(LookaheadSuccess ls) { return true; }
    finally { jj_save(5, xla); }
  }

  private boolean jj_2_7(int xla) {
    jj_la = xla; jj_lastpos = jj_scanpos = token;
    try { return !jj_3_7(); }
    catch(LookaheadSuccess ls) { return true; }
    finally { jj_save(6, xla); }
  }

  private boolean jj_2_8(int xla) {
    jj_la = xla; jj_lastpos = jj_scanpos = token;
    try { return !jj_3_8(); }
    catch(LookaheadSuccess ls) { return true; }
    finally { jj_save(7, xla); }
  }

  private boolean jj_2_9(int xla) {
    jj_la = xla; jj_lastpos = jj_scanpos = token;
    try { return !jj_3_9(); }
    catch(LookaheadSuccess ls) { return true; }
    finally { jj_save(8, xla); }
  }

  private boolean jj_2_10(int xla) {
    jj_la = xla; jj_lastpos = jj_scanpos = token;
    try { return !jj_3_10(); }
    catch(LookaheadSuccess ls) { return true; }
    finally { jj_save(9, xla); }
  }

  private boolean jj_2_11(int xla) {
    jj_la = xla; jj_lastpos = jj_scanpos = token;
    try { return !jj_3_11(); }
    catch(LookaheadSuccess ls) { return true; }
    finally { jj_save(10, xla); }
  }

  private boolean jj_2_12(int xla) {
    jj_la = xla; jj_lastpos = jj_scanpos = token;
    try { return !jj_3_12(); }
    catch(LookaheadSuccess ls) { return true; }
    finally { jj_save(11, xla); }
  }

  private boolean jj_2_13(int xla) {
    jj_la = xla; jj_lastpos = jj_scanpos = token;
    try { return !jj_3_13(); }
    catch(LookaheadSuccess ls) { return true; }
    finally { jj_save(12, xla); }
  }

  private boolean jj_2_14(int xla) {
    jj_la = xla; jj_lastpos = jj_scanpos = token;
    try { return !jj_3_14(); }
    catch(LookaheadSuccess ls) { return true; }
    finally { jj_save(13, xla); }
  }

  private boolean jj_2_15(int xla) {
    jj_la = xla; jj_lastpos = jj_scanpos = token;
    try { return !jj_3_15(); }
    catch(LookaheadSuccess ls) { return true; }
    finally { jj_save(14, xla); }
  }

  private boolean jj_3_6() {
    if (jj_scan_token(13)) return true;
    if (jj_3R_4()) return true;
    return false;
  }

  private boolean jj_3_5() {
    if (jj_scan_token(12)) return true;
    if (jj_3R_4()) return true;
    return false;
  }

  private boolean jj_3_4() {
    Token xsp;
    xsp = jj_scanpos;
    if (jj_3_5()) {
    jj_scanpos = xsp;
    if (jj_3_6()) return true;
    }
    return false;
  }

  private boolean jj_3_11() {
    if (jj_scan_token(11)) return true;
    if (jj_3R_5()) return true;
    return false;
  }

  private boolean jj_3_9() {
    if (jj_scan_token(15)) return true;
    if (jj_3R_3()) return true;
    if (jj_scan_token(16)) return true;
    if (jj_scan_token(14)) return true;
    return false;
  }

  private boolean jj_3R_4() {
    if (jj_3R_5()) return true;
    Token xsp;
    while (true) {
      xsp = jj_scanpos;
      if (jj_3_4()) { jj_scanpos = xsp; break; }
    }
    return false;
  }

  private boolean jj_3_10() {
    if (jj_scan_token(10)) return true;
    if (jj_3R_5()) return true;
    return false;
  }

  private boolean jj_3R_7() {
    if (jj_scan_token(15)) return true;
    if (jj_3R_3()) return true;
    if (jj_scan_token(16)) return true;
    if (jj_scan_token(14)) return true;
    return false;
  }

  private boolean jj_3_3() {
    if (jj_scan_token(11)) return true;
    if (jj_3R_3()) return true;
    return false;
  }

  private boolean jj_3_2() {
    if (jj_scan_token(10)) return true;
    if (jj_3R_3()) return true;
    return false;
  }

  private boolean jj_3_1() {
    Token xsp;
    xsp = jj_scanpos;
    if (jj_3_2()) {
    jj_scanpos = xsp;
    if (jj_3_3()) return true;
    }
    return false;
  }

  private boolean jj_3_7() {
    if (jj_scan_token(NUMBER)) return true;
    if (jj_scan_token(14)) return true;
    return false;
  }

  private boolean jj_3_8() {
    if (jj_scan_token(NUMBER)) return true;
    return false;
  }

  private boolean jj_3R_3() {
    if (jj_3R_4()) return true;
    Token xsp;
    while (true) {
      xsp = jj_scanpos;
      if (jj_3_1()) { jj_scanpos = xsp; break; }
    }
    return false;
  }

  private boolean jj_3_15() {
    if (jj_scan_token(TAN)) return true;
    if (jj_scan_token(15)) return true;
    if (jj_3R_3()) return true;
    if (jj_scan_token(16)) return true;
    return false;
  }

  private boolean jj_3_14() {
    if (jj_scan_token(COS)) return true;
    if (jj_scan_token(15)) return true;
    if (jj_3R_3()) return true;
    if (jj_scan_token(16)) return true;
    return false;
  }

  private boolean jj_3R_6() {
    if (jj_scan_token(NUMBER)) return true;
    if (jj_scan_token(14)) return true;
    return false;
  }

  private boolean jj_3R_5() {
    Token xsp;
    xsp = jj_scanpos;
    if (jj_3R_6()) {
    jj_scanpos = xsp;
    if (jj_3_8()) {
    jj_scanpos = xsp;
    if (jj_3R_7()) {
    jj_scanpos = xsp;
    if (jj_3_10()) {
    jj_scanpos = xsp;
    if (jj_3_11()) {
    jj_scanpos = xsp;
    if (jj_3_12()) {
    jj_scanpos = xsp;
    if (jj_3_13()) {
    jj_scanpos = xsp;
    if (jj_3_14()) {
    jj_scanpos = xsp;
    if (jj_3_15()) return true;
    }
    }
    }
    }
    }
    }
    }
    }
    return false;
  }

  private boolean jj_3_13() {
    if (jj_scan_token(SIN)) return true;
    if (jj_scan_token(15)) return true;
    if (jj_3R_3()) return true;
    if (jj_scan_token(16)) return true;
    return false;
  }

  private boolean jj_3_12() {
    if (jj_scan_token(15)) return true;
    if (jj_3R_3()) return true;
    if (jj_scan_token(16)) return true;
    return false;
  }

  /** Generated Token Manager. */
  public CalculatorTokenManager token_source;
  SimpleCharStream jj_input_stream;
  /** Current token. */
  public Token token;
  /** Next token. */
  public Token jj_nt;
  private int jj_ntk;
  private Token jj_scanpos, jj_lastpos;
  private int jj_la;
  private int jj_gen;
  final private int[] jj_la1 = new int[0];
  static private int[] jj_la1_0;
  static {
      jj_la1_init_0();
   }
   private static void jj_la1_init_0() {
      jj_la1_0 = new int[] {};
   }
  final private JJCalls[] jj_2_rtns = new JJCalls[15];
  private boolean jj_rescan = false;
  private int jj_gc = 0;

  /** Constructor with InputStream. */
  public Calculator(java.io.InputStream stream) {
     this(stream, null);
  }
  /** Constructor with InputStream and supplied encoding */
  public Calculator(java.io.InputStream stream, String encoding) {
    try { jj_input_stream = new SimpleCharStream(stream, encoding, 1, 1); } catch(java.io.UnsupportedEncodingException e) { throw new RuntimeException(e); }
    token_source = new CalculatorTokenManager(jj_input_stream);
    token = new Token();
    jj_ntk = -1;
    jj_gen = 0;
    for (int i = 0; i < 0; i++) jj_la1[i] = -1;
    for (int i = 0; i < jj_2_rtns.length; i++) jj_2_rtns[i] = new JJCalls();
  }

  /** Reinitialise. */
  public void ReInit(java.io.InputStream stream) {
     ReInit(stream, null);
  }
  /** Reinitialise. */
  public void ReInit(java.io.InputStream stream, String encoding) {
    try { jj_input_stream.ReInit(stream, encoding, 1, 1); } catch(java.io.UnsupportedEncodingException e) { throw new RuntimeException(e); }
    token_source.ReInit(jj_input_stream);
    token = new Token();
    jj_ntk = -1;
    jj_gen = 0;
    for (int i = 0; i < 0; i++) jj_la1[i] = -1;
    for (int i = 0; i < jj_2_rtns.length; i++) jj_2_rtns[i] = new JJCalls();
  }

  /** Constructor. */
  public Calculator(java.io.Reader stream) {
    jj_input_stream = new SimpleCharStream(stream, 1, 1);
    token_source = new CalculatorTokenManager(jj_input_stream);
    token = new Token();
    jj_ntk = -1;
    jj_gen = 0;
    for (int i = 0; i < 0; i++) jj_la1[i] = -1;
    for (int i = 0; i < jj_2_rtns.length; i++) jj_2_rtns[i] = new JJCalls();
  }

  /** Reinitialise. */
  public void ReInit(java.io.Reader stream) {
    jj_input_stream.ReInit(stream, 1, 1);
    token_source.ReInit(jj_input_stream);
    token = new Token();
    jj_ntk = -1;
    jj_gen = 0;
    for (int i = 0; i < 0; i++) jj_la1[i] = -1;
    for (int i = 0; i < jj_2_rtns.length; i++) jj_2_rtns[i] = new JJCalls();
  }

  /** Constructor with generated Token Manager. */
  public Calculator(CalculatorTokenManager tm) {
    token_source = tm;
    token = new Token();
    jj_ntk = -1;
    jj_gen = 0;
    for (int i = 0; i < 0; i++) jj_la1[i] = -1;
    for (int i = 0; i < jj_2_rtns.length; i++) jj_2_rtns[i] = new JJCalls();
  }

  /** Reinitialise. */
  public void ReInit(CalculatorTokenManager tm) {
    token_source = tm;
    token = new Token();
    jj_ntk = -1;
    jj_gen = 0;
    for (int i = 0; i < 0; i++) jj_la1[i] = -1;
    for (int i = 0; i < jj_2_rtns.length; i++) jj_2_rtns[i] = new JJCalls();
  }

  private Token jj_consume_token(int kind) throws ParseException {
    Token oldToken;
    if ((oldToken = token).next != null) token = token.next;
    else token = token.next = token_source.getNextToken();
    jj_ntk = -1;
    if (token.kind == kind) {
      jj_gen++;
      if (++jj_gc > 100) {
        jj_gc = 0;
        for (int i = 0; i < jj_2_rtns.length; i++) {
          JJCalls c = jj_2_rtns[i];
          while (c != null) {
            if (c.gen < jj_gen) c.first = null;
            c = c.next;
          }
        }
      }
      return token;
    }
    token = oldToken;
    jj_kind = kind;
    throw generateParseException();
  }

  static private final class LookaheadSuccess extends Error { }
  final private LookaheadSuccess jj_ls = new LookaheadSuccess();
  private boolean jj_scan_token(int kind) {
    if (jj_scanpos == jj_lastpos) {
      jj_la--;
      if (jj_scanpos.next == null) {
        jj_lastpos = jj_scanpos = jj_scanpos.next = token_source.getNextToken();
      } else {
        jj_lastpos = jj_scanpos = jj_scanpos.next;
      }
    } else {
      jj_scanpos = jj_scanpos.next;
    }
    if (jj_rescan) {
      int i = 0; Token tok = token;
      while (tok != null && tok != jj_scanpos) { i++; tok = tok.next; }
      if (tok != null) jj_add_error_token(kind, i);
    }
    if (jj_scanpos.kind != kind) return true;
    if (jj_la == 0 && jj_scanpos == jj_lastpos) throw jj_ls;
    return false;
  }


/** Get the next Token. */
  final public Token getNextToken() {
    if (token.next != null) token = token.next;
    else token = token.next = token_source.getNextToken();
    jj_ntk = -1;
    jj_gen++;
    return token;
  }

/** Get the specific Token. */
  final public Token getToken(int index) {
    Token t = token;
    for (int i = 0; i < index; i++) {
      if (t.next != null) t = t.next;
      else t = t.next = token_source.getNextToken();
    }
    return t;
  }

  private int jj_ntk() {
    if ((jj_nt=token.next) == null)
      return (jj_ntk = (token.next=token_source.getNextToken()).kind);
    else
      return (jj_ntk = jj_nt.kind);
  }

  private java.util.List<int[]> jj_expentries = new java.util.ArrayList<int[]>();
  private int[] jj_expentry;
  private int jj_kind = -1;
  private int[] jj_lasttokens = new int[100];
  private int jj_endpos;

  private void jj_add_error_token(int kind, int pos) {
    if (pos >= 100) return;
    if (pos == jj_endpos + 1) {
      jj_lasttokens[jj_endpos++] = kind;
    } else if (jj_endpos != 0) {
      jj_expentry = new int[jj_endpos];
      for (int i = 0; i < jj_endpos; i++) {
        jj_expentry[i] = jj_lasttokens[i];
      }
      jj_entries_loop: for (java.util.Iterator<?> it = jj_expentries.iterator(); it.hasNext();) {
        int[] oldentry = (int[])(it.next());
        if (oldentry.length == jj_expentry.length) {
          for (int i = 0; i < jj_expentry.length; i++) {
            if (oldentry[i] != jj_expentry[i]) {
              continue jj_entries_loop;
            }
          }
          jj_expentries.add(jj_expentry);
          break jj_entries_loop;
        }
      }
      if (pos != 0) jj_lasttokens[(jj_endpos = pos) - 1] = kind;
    }
  }

  /** Generate ParseException. */
  public ParseException generateParseException() {
    jj_expentries.clear();
    boolean[] la1tokens = new boolean[17];
    if (jj_kind >= 0) {
      la1tokens[jj_kind] = true;
      jj_kind = -1;
    }
    for (int i = 0; i < 0; i++) {
      if (jj_la1[i] == jj_gen) {
        for (int j = 0; j < 32; j++) {
          if ((jj_la1_0[i] & (1<<j)) != 0) {
            la1tokens[j] = true;
          }
        }
      }
    }
    for (int i = 0; i < 17; i++) {
      if (la1tokens[i]) {
        jj_expentry = new int[1];
        jj_expentry[0] = i;
        jj_expentries.add(jj_expentry);
      }
    }
    jj_endpos = 0;
    jj_rescan_token();
    jj_add_error_token(0, 0);
    int[][] exptokseq = new int[jj_expentries.size()][];
    for (int i = 0; i < jj_expentries.size(); i++) {
      exptokseq[i] = jj_expentries.get(i);
    }
    return new ParseException(token, exptokseq, tokenImage);
  }

  /** Enable tracing. */
  final public void enable_tracing() {
  }

  /** Disable tracing. */
  final public void disable_tracing() {
  }

  private void jj_rescan_token() {
    jj_rescan = true;
    for (int i = 0; i < 15; i++) {
    try {
      JJCalls p = jj_2_rtns[i];
      do {
        if (p.gen > jj_gen) {
          jj_la = p.arg; jj_lastpos = jj_scanpos = p.first;
          switch (i) {
            case 0: jj_3_1(); break;
            case 1: jj_3_2(); break;
            case 2: jj_3_3(); break;
            case 3: jj_3_4(); break;
            case 4: jj_3_5(); break;
            case 5: jj_3_6(); break;
            case 6: jj_3_7(); break;
            case 7: jj_3_8(); break;
            case 8: jj_3_9(); break;
            case 9: jj_3_10(); break;
            case 10: jj_3_11(); break;
            case 11: jj_3_12(); break;
            case 12: jj_3_13(); break;
            case 13: jj_3_14(); break;
            case 14: jj_3_15(); break;
          }
        }
        p = p.next;
      } while (p != null);
      } catch(LookaheadSuccess ls) { }
    }
    jj_rescan = false;
  }

  private void jj_save(int index, int xla) {
    JJCalls p = jj_2_rtns[index];
    while (p.gen > jj_gen) {
      if (p.next == null) { p = p.next = new JJCalls(); break; }
      p = p.next;
    }
    p.gen = jj_gen + xla - jj_la; p.first = token; p.arg = xla;
  }

  static final class JJCalls {
    int gen;
    Token first;
    int arg;
    JJCalls next;
  }

}
