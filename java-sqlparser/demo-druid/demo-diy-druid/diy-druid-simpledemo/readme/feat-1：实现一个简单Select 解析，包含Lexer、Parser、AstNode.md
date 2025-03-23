[toc]



# 实现目标

初步实现一个最简单的select xxx from xxx的解析功能实现，包含一些基础简单定义的Ast node，词法分析器 Lexer，语法分析器Parser。

- toke定义（枚举类），ast node抽象（各个组件节点），词法分析器 Lexer（涉及到语法扫描）
- 语法分析器Parser 读取解析 -> statement

基础select 的 sql语法支持：

```sql
select id, name from table where age > 20;
```

**主体流程：**

Parser ->  lexer扫描（借助Token类型） ->  Token  -> 构建ast node -> 构建得到statement

# 一、实现词义类型定义 & 词法分析器扫描

![img](https://pictured-bed.oss-cn-beijing.aliyuncs.com/img/2024/202503231259724.png)

## 定义Token类型枚举类：TokenType.java

```java
package com.changlu.enums;

public enum TokenType {
    SELECT("select"),     // SELECT 关键字
    FROM("from"),       // FROM 关键字
    WHERE("where"),      // WHERE 关键字
    IDENTIFIER, // 标识符（如表名、列名，主要为字符串类型）
    NUMBER,     // 数字
    COMMA(","),      // 逗号
    GT(">"),         // 大于号 (>)
    LT("<"),         // 小于号 (<)
    EQ("="),         // 等于号 (=)
    EOF;         // 结束符

    public String value;

    TokenType() {
    }

    TokenType(String value) {
        this.value = value;
    }
}
```

## 定义词法单元：Token.java

```java
package com.changlu.core;

import com.changlu.enums.TokenType;

/**
 * 词法分析器生成的每一个词法单元
 */
public class Token {
    private final TokenType type; // Token类型
    private final String value; // Token值

    public Token(TokenType type, String value) {
        this.type = type;
        this.value = value;
    }

    public TokenType getType() {
        return type;
    }

    public String getValue() {
        return value;
    }

    @Override
    public String toString() {
        return "Token{" +
                "type=" + type +
                ", value='" + value + '\'' +
                '}';
    }
}
```

## 词法分析器：Lexer.java

```java
package com.changlu.core.lexer;

import com.changlu.core.Token;
import com.changlu.enums.TokenType;

/**
 * 词法分析器扫描
 */
public class Lexer {

    private final String input;
    private int pos = 0;

    public Lexer(String input) {
        this.input = input.trim();
    }

    // 获取下一个token
    public Token nextToken() {
        if (pos >= input.length()) {
            return new Token(TokenType.EOF, "");
        }

        // 若是空格符，直接跳过
        char ch = input.charAt(pos);
        if (Character.isWhitespace(ch)) {
            pos++;
            return nextToken();
        }

        // 若是字母，则读取标识符
        if (Character.isLetter(ch)) {
            return readIdentifier();
        }

        // 如果是数字，则读取数字
        if (Character.isDigit(ch)) {
            return readNumber();
        }

        if (ch == ',') {
            pos++;
            return new Token(TokenType.COMMA, TokenType.COMMA.value);
        }

        if (ch == '>') {
            pos++;
            return new Token(TokenType.GT, TokenType.GT.value);
        }

        if (ch == '<') {
            pos++;
            return new Token(TokenType.LT, TokenType.LT.value);
        }

        if (ch == '=') {
            pos++;
            return new Token(TokenType.EQ, TokenType.EQ.value);
        }

        if (ch == ';') {
            pos++;
            return new Token(TokenType.EOF, "");
        }

        throw new RuntimeException("Unexpected character: " + ch);
    }

    // 读取标识符
    public Token readIdentifier() {
        StringBuilder sb = new StringBuilder();
        // 读取一组字符 or 变量
        while (pos < input.length() && Character.isLetterOrDigit(input.charAt(pos))) {
            sb.append(input.charAt(pos));
            pos++;
        }

        String value = sb.toString();
        // select 场景
        if (value.equalsIgnoreCase(TokenType.SELECT.value)) {
            return new Token(TokenType.SELECT, value);
        }
        // from 场景
        if (value.equalsIgnoreCase(TokenType.FROM.value)) {
            return new Token(TokenType.FROM, value);
        }
        // where 场景
        if (value.equalsIgnoreCase(TokenType.WHERE.value)) {
            return new Token(TokenType.WHERE, value);
        }
        return new Token(TokenType.IDENTIFIER, value);
    }

    /**
     * 读取数字
     * @return 数字Token
     */
    public Token readNumber() {
        StringBuilder sb = new StringBuilder();
        while (pos < input.length() && Character.isDigit(input.charAt(pos))) {
            sb.append(input.charAt(pos));
            pos++;
        }
        return new Token(TokenType.NUMBER, sb.toString());
    }

}
```

------

# 二、定义Ast node

## 介绍

抽象类表达式：SQLExpr

- SQLBinaryExpr：二元表达式
- SQLIdentifierExpr：标识符表达式
- SQLNumberExpr：数字表达式

Table数据源：SQLTableSource

![img](https://pictured-bed.oss-cn-beijing.aliyuncs.com/img/2024/202503231259833.png)

## 抽象表达式：SQLExpr.java

```java
package com.changlu.core.ast;

public abstract class SQLExpr {
}
```

## 标识符表达式：SQLIdentifierExpr.java

```java
package com.changlu.core.ast;

// 标识符表达式
public class SQLIdentifierExpr extends SQLExpr{

    private final String name;

    public SQLIdentifierExpr(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        return name;
    }
}
```

## 数字表达式：SQLNumberExpr.java

```java
package com.changlu.core.ast;

/**
 * 数字表达式
 */
public class SQLNumberExpr extends SQLExpr{

    private final String value;

    public SQLNumberExpr(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    @Override
    public String toString() {
        return value;
    }

}
```

## 二元表达式：SQLBinaryExpr.java

```java
package com.changlu.core.ast;

import com.changlu.enums.TokenType;

// 二元表达式
public class SQLBinaryExpr extends SQLExpr{

    private SQLExpr left;
    private final TokenType operator;
    private SQLExpr right;

    // 初始化
    public SQLBinaryExpr(SQLExpr left, TokenType operator, SQLExpr right) {
        this.left = left;
        this.operator = operator;
        this.right = right;
    }

    public SQLExpr getLeft() {
        return left;
    }

    public TokenType getOperator() {
        return operator;
    }

    public SQLExpr getRight() {
        return right;
    }

    @Override
    public String toString() {
        return left + " " + operator.value + " " + right;
    }


}
```

## Table数据源：SQLTableSource.java

```java
package com.changlu.core.ast;

// 表示table数据源
public class SQLTableSource {

    private final String tableName;

    public SQLTableSource(String tableName) {
        this.tableName = tableName;
    }

    public String getTableName() {
        return tableName;
    }

}
```

# 三、定义解析结构statement

目前当前需求是实现select的statement，这里基于二中的多个表达式组件来组成构成一个statement。

![img](https://pictured-bed.oss-cn-beijing.aliyuncs.com/img/2024/202503231259735.png)

## 3.1、Select解析结构：SelectStatement.java

```java
package com.changlu.core.ast.statement;

import com.changlu.core.ast.SQLExpr;
import com.changlu.core.ast.SQLIdentifierExpr;
import com.changlu.core.ast.SQLTableSource;

import java.util.List;

public class SelectStatement {

    private List<SQLExpr> selectList; // select 列表
    private SQLTableSource from; // FROM子句
    private SQLExpr where; // WHERE子句

    public List<SQLExpr> getSelectList() {
        return selectList;
    }

    public void setSelectList(List<SQLExpr> selectList) {
        this.selectList = selectList;
    }

    public SQLTableSource getFrom() {
        return from;
    }

    public void setFrom(SQLTableSource from) {
        this.from = from;
    }

    public SQLExpr getWhere() {
        return where;
    }

    public void setWhere(SQLExpr where) {
        this.where = where;
    }

    @Override
    public String toString() {
        return "SelectStatement{" +
                "selectList=" + selectList +
                ", from=" + from +
                ", where=" + where +
                '}';
    }
}
```



# 四、实现Parser解析工具

目标需求是这个Parser解析工具，能够借助lexer分析器来完成扫描Token，最终转为Statement的整个过程。

![img](https://pictured-bed.oss-cn-beijing.aliyuncs.com/img/2024/202503231259708.png)

## 解析器Paser.java

```java
package com.changlu.core.parser;

import com.changlu.core.ast.*;
import com.changlu.core.ast.statement.SelectStatement;
import com.changlu.core.lexer.Lexer;
import com.changlu.core.Token;
import com.changlu.enums.TokenType;
import com.sun.org.apache.bcel.internal.generic.Select;

import java.util.ArrayList;
import java.util.List;

public class Parser {

    private final Lexer lexer;// 词法分析器
    private Token currentToken; // 当前Token

    public Parser(Lexer lexer) {
        this.lexer = lexer;
        // 获取当前第一个Token标识
        this.currentToken = lexer.nextToken();
    }

    /**
     * 解析整个select 语句
     * @return SelectStatement
     */
    public SelectStatement parse() {
        // 初始化对象
        SelectStatement stmt = new SelectStatement();
        match(TokenType.SELECT); // 匹配 SELECT 关键字
        stmt.setSelectList(parseSelectList()); // 解析select列表
        match(TokenType.FROM);   // 匹配 FROM 关键字
        stmt.setFrom(parseTable()); // 解析表名
        // 针对于where情况可能不会出现
        if (currentToken.getType() == TokenType.WHERE) {
            match(TokenType.WHERE);
            stmt.setWhere(parseExpr());
        }
        return stmt;
    }

    // 解析select列表
    private List<SQLExpr> parseSelectList() {
        List<SQLExpr> selectList = new ArrayList<>();
        selectList.add(parseExpr()); // 解析得到第一个表达式
        while (currentToken.getType() == TokenType.COMMA) {
            match(TokenType.COMMA);
            selectList.add(parseExpr()); // 解析得到下一个表达式
        }
        return selectList;
    }

    // 解析表名
    private SQLTableSource parseTable() {
        String tableName = currentToken.getValue();
        match(TokenType.IDENTIFIER); // 匹配表名
        return new SQLTableSource(tableName);
    }

    // 解析表达式
    private SQLExpr parseExpr() {
        SQLExpr expr = parsePrimary(); // 解析基本表达式
        if (currentToken.getType() == TokenType.GT ||
                currentToken.getType() == TokenType.LT ||
                currentToken.getType() == TokenType.EQ) {
            TokenType operator = currentToken.getType();
            match(operator); // 匹配运算符
            SQLExpr right = parsePrimary(); // 解析右表达式
            expr = new SQLBinaryExpr(expr, operator, right);
        }
        return expr;
    }

    // 解析基本表达式
    private SQLExpr parsePrimary() {
        if (currentToken.getType() == TokenType.IDENTIFIER) {
            SQLExpr expr = new SQLIdentifierExpr(currentToken.getValue());
            match(TokenType.IDENTIFIER);
            return expr;
        }else if (currentToken.getType() == TokenType.NUMBER) {
            SQLExpr expr = new SQLNumberExpr(currentToken.getValue());
            match(TokenType.NUMBER);
            return expr;
        }else {
            // 用于提示当前错误的Token位置
            throw new RuntimeException("Unexpected token: " + currentToken);
        }
    }

    // 匹配当前的 Token 并移动到下一个Token
    private void match(TokenType excepted) {
        if (currentToken.getType() == excepted) {
            currentToken = lexer.nextToken();
        }else {
            throw new RuntimeException("Unexpected token: " + currentToken);
        }
    }

}
```



# 测试验证

![img](https://pictured-bed.oss-cn-beijing.aliyuncs.com/img/2024/202503231259987.png)

Main.java：

```java
package com.changlu;

import com.changlu.core.ast.SQLExpr;
import com.changlu.core.ast.statement.SelectStatement;
import com.changlu.core.lexer.Lexer;
import com.changlu.core.parser.Parser;

public class Main {

    public static void main(String[] args) {
        String sql = "select id, name from table where age > 20;";
        Parser parser = new Parser(new Lexer(sql));
        SelectStatement stmt = parser.parse();

        // 根据解析得到的SelectStatement， 去打印描述信息
        System.out.println("SELECT List:");
        for (SQLExpr expr : stmt.getSelectList()) {
            System.out.println("- " + expr);
        }
        System.out.println("FROM Table: " + stmt.getFrom().getTableName());
        if (stmt.getWhere() != null) {
            System.out.println("WHERE Condition: " + stmt.getWhere());
        }
    }

}
```

![img](https://pictured-bed.oss-cn-beijing.aliyuncs.com/img/2024/202503231259935.png)



------

# 总结

- 通过扩展 `TokenType`、`Lexer` 和 `Parser`，我们实现了对条件表达式（如 `age > 20`）的解析。
- 使用 `SQLBinaryExpr` 表示二元表达式，支持比较运算符（如 `>`、`<`、`=`）。
- 通过 `parsePrimary` 方法解析基本表达式（标识符或数字）。

你可以在此基础上进一步扩展，支持更多运算符（如 `>=`、`<=`、`!=`）和复杂表达式（如 `AND`、`OR`）。如果有进一步的问题，欢迎随时交流！