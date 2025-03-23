package com.changlu;

import org.apache.calcite.avatica.util.Casing;
import org.apache.calcite.config.Lex;
import org.apache.calcite.sql.SqlNode;
import org.apache.calcite.sql.dialect.OracleSqlDialect;
import org.apache.calcite.sql.parser.SqlParseException;
import org.apache.calcite.sql.parser.SqlParser;
import org.apache.calcite.sql.parser.ddl.CustomSqlParserImpl;

public class Main {
    public static void main(String[] args) throws SqlParseException {
        // 解析配置 - mysql设置
        SqlParser.Config mysqlConfig = SqlParser.configBuilder()
                // 定义解析工厂
                .setParserFactory(CustomSqlParserImpl.FACTORY)
//                .setQuotedCasing(Casing.TO_UPPER)
                .setLex(Lex.MYSQL)
                .build();
        // 创建解析器
        SqlParser parser = SqlParser.create("", mysqlConfig);
        // Sql语句
        String sql = "create function " +
                "hr.custom_function as 'com.github.quxiucheng.calcite.func.CustomFunction' " +
                "method 'eval'  " +
                "property ('a'='2','c'='1') ";
        // 解析sql
        SqlNode sqlNode = parser.parseQuery(sql);

        System.out.println(sqlNode.toSqlString(OracleSqlDialect.DEFAULT));
    }
}