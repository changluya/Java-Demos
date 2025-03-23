package com.changlu.demo.parse;

import net.sf.jsqlparser.expression.Expression;
import net.sf.jsqlparser.parser.CCJSqlParserUtil;
import net.sf.jsqlparser.schema.Table;
import net.sf.jsqlparser.statement.Statement;
import net.sf.jsqlparser.statement.delete.Delete;
import net.sf.jsqlparser.statement.insert.Insert;
import net.sf.jsqlparser.statement.select.PlainSelect;
import net.sf.jsqlparser.statement.select.Select;
import net.sf.jsqlparser.statement.update.Update;
import net.sf.jsqlparser.statement.update.UpdateSet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class demo03 {

    public static final Logger log = LoggerFactory.getLogger(demo03.class);

    public static void main(String[] args) throws Exception{
//        selectDemo();
//        insertDemo();
//        updateDemo();
        deleteDemo();
    }

    /**
     * select demo案例
     */
    public static void selectDemo() throws Exception{
        String sql = "SELECT id,name,nickname,age,job,department FROM staff_member WHERE nickname= '刘'";
        // Parse SQL
        Statement statement = CCJSqlParserUtil.parse(sql);
        if(statement instanceof Select){
            Select selectStatement = (Select) statement;
            log.info("==> JsqlParser SQL: {}", selectStatement.toString());
            PlainSelect plainSelect = selectStatement.getPlainSelect();
            log.info("==> FromItem: {}", plainSelect.getFromItem());// 表结构
            log.info("==> SelectItem: {}",plainSelect.getSelectItems());// 字段选项
            log.info("==> Where: {}",plainSelect.getWhere());// where字段
        }
    }

    /**
     * insert案例demo
     */
    public static void insertDemo() throws Exception{
        String sql = "INSERT INTO employees (employee_id, employee_name, department) VALUES (1, 'John Doe', 'Human Resources')";
        // Parse SQL
        Statement statement = CCJSqlParserUtil.parse(sql);
        if (statement instanceof Insert) {
            Insert insertStatement = (Insert) statement;
            log.info("==> JsqlParser SQL: {}", insertStatement.toString());
            log.info("==> Table: {}", insertStatement.getTable());
            log.info("==> Columns: {}", insertStatement.getColumns());
            log.info("==> ItemsList: {}", insertStatement.getValues());
        }
    }

    /**
     * update的demo
     */
    public static void updateDemo() throws Exception{
        String sql = "UPDATE employees SET department = 'Human Resources' WHERE employee_id = 1";
// Parse SQL
        Statement statement = CCJSqlParserUtil.parse(sql);
        if (statement instanceof Update) {
            Update updateStatement = (Update) statement;
            log.info("==> JsqlParser SQL: {}", updateStatement.toString());
            // 更新的目标表
            Table table = updateStatement.getTable();
            log.info("Table Name: {}", table.getName());
            log.info("==> Columns: {}", updateStatement.getColumns());
            // 获取更新项
            List<UpdateSet> updateSets = updateStatement.getUpdateSets();
            for (UpdateSet updateSet : updateSets) {
                for (Expression expression : updateSet.getColumns()) {
                    log.info("==> Expression: {}", expression.toString());
                }
            }
            // 更新的字段
            log.info("==> ItemsList: {}", updateStatement.getExpressions());
            // 更新的where条件
            Expression where = updateStatement.getWhere();
            log.info("==> Where: {}", where.toString());
        }
    }

    /**
     * 删除demo
     */
    public static void deleteDemo() throws Exception{
        String sql = "DELETE FROM table_name WHERE a = 1";
        Statement statement = CCJSqlParserUtil.parse(sql);
        if (statement instanceof Delete) {
            Delete deleteStatement = (Delete) statement;
            // 获取要删除的表
            Table table = deleteStatement.getTable();
            System.out.println("Table Name: " + table.getName());
            // 获取WHERE条件
            Expression where = deleteStatement.getWhere();
            System.out.println("Where Condition: " + where.toString());
        }
    }

}
