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
