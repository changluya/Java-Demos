package com.changlu.demo.parse;

import net.sf.jsqlparser.JSQLParserException;
import net.sf.jsqlparser.parser.CCJSqlParserUtil;
import net.sf.jsqlparser.statement.Statement;
import net.sf.jsqlparser.statement.delete.Delete;
import net.sf.jsqlparser.statement.insert.Insert;
import net.sf.jsqlparser.statement.select.Select;
import net.sf.jsqlparser.statement.update.Update;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 分析不同sql类型
 */
public class demo02 {

    public static final Logger log = LoggerFactory.getLogger(demo02.class);

    public static void main(String[] args) throws JSQLParserException {
        String sql = "SELECT id,name,nickname,age,job,department FROM staff_member WHERE nickname= '刘'";
        // Parse SQL
        Statement statement = CCJSqlParserUtil.parse(sql);
        if(statement instanceof Select){
            Select selectStatement = (Select) statement;
            log.info("Select==> JsqlParser SQL: {}", selectStatement.toString());
        }
        if(statement instanceof Insert){
            Insert insertStatement = (Insert) statement;
            log.info("Insert==> JsqlParser SQL: {}", insertStatement.toString());
        }
        if(statement instanceof Update){
            Update updateStatement = (Update) statement;
            log.info("Update==> JsqlParser SQL: {}", updateStatement.toString());
        }
        if (statement instanceof Delete) {
            Delete deleteStatement = (Delete) statement;
            log.info("Delete==> JsqlParser SQL: {}", statement.toString());
        }

    }
}