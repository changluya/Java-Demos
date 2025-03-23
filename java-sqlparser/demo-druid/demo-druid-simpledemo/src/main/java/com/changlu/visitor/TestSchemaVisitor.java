package com.changlu.visitor;

import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.dialect.mysql.parser.MySqlStatementParser;
import com.alibaba.druid.sql.dialect.mysql.visitor.MySqlSchemaStatVisitor;
import com.alibaba.druid.sql.parser.SQLStatementParser;

/**
 * 验证MySqlSchemaStatVisitor
 */
public class TestSchemaVisitor {

    public static void main(String[] args) {
        SQLStatementParser parser = new MySqlStatementParser("select  name ,id ,select money from user from acct where id =10");
        SQLStatement sqlStatement = parser.parseStatement();
        // 定义visitor
        MySqlSchemaStatVisitor visitor = new MySqlSchemaStatVisitor();
        // 执行visitor访问操作
        sqlStatement.accept(visitor);
        // 获取到最终的字段名、tables、条件分支、db类型
        System.out.println(visitor.getColumns()); //[acct.name, acct.id, user.money]
        System.out.println(visitor.getTables()); //{acct=Select, user=Select}
        System.out.println(visitor.getConditions()); //[acct.id = 10]
        System.out.println(visitor.getDbType());//mysql
    }

}
