package com.changlu.demo.parse;

import net.sf.jsqlparser.JSQLParserException;
import net.sf.jsqlparser.parser.CCJSqlParserUtil;
import net.sf.jsqlparser.statement.Statement;
import net.sf.jsqlparser.statement.select.Select;

/**
 * 解析sql小demo
 */
public class demo01 {

    public static void main(String[] args) throws JSQLParserException {
        String sql = "SELECT id,name,nickname,age,job,department FROM staff_member WHERE nickname= '刘'";
        // Parse SQL
        Statement statement = CCJSqlParserUtil.parse(sql);
        Select selectStatement = (Select) statement;
        System.out.println("JsqlParser SQL" + selectStatement.toString());
    }

}
