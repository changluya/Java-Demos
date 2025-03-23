package com.changlu;

import com.alibaba.druid.DbType;
import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.ast.statement.SQLUseStatement;
import com.alibaba.druid.sql.parser.ParserException;
import com.alibaba.druid.sql.parser.SQLParserUtils;
import com.alibaba.druid.sql.parser.SQLStatementParser;
import com.alibaba.druid.sql.visitor.SchemaStatVisitor;
import com.alibaba.druid.stat.TableStat;
import com.alibaba.druid.util.JdbcConstants;
import lombok.extern.slf4j.Slf4j;

import java.util.*;

public class Main {

    public static void main(String[] args) throws Exception{
        test2();
//        test3();
    }

    public void test() {
        String sql = "select name, age from t_user left join t_user2 on t_user.id = t_user2.id  where t_user.id = 1;";
        List<SQLStatement> stmtList = SQLUtils.parseStatements(sql, JdbcConstants.HIVE);
        SQLStatement stmt = stmtList.get(0);

        // 构建visitor
        SchemaStatVisitor statVisitor = SQLUtils.createSchemaStatVisitor(JdbcConstants.HIVE);
        stmt.accept(statVisitor);

        System.out.println(statVisitor.getColumns()); // [t_user.name, t_user.age, t_user.id]
        System.out.println(statVisitor.getTables()); // {t_user=Select}
        System.out.println(statVisitor.getConditions()); // [t_user.id = 1]
    }

    /**
     * 处理多段sql & 格式化
     */
    public static void test2() {
        String sql = "SELECT id, name FROM table1 UNION SELECT id, name FROM table2;" +
                "SELECT id, name FROM table3";
        List<SQLStatement> stmtList = SQLUtils.parseStatements(sql, DbType.mysql);
        for (SQLStatement sqlStatement : stmtList) {
            System.out.println(SQLUtils.toSQLString(sqlStatement));
            System.out.println();
        }
    }

    public static void test3() {
        String sql = "SELECT id from changlu where id = ?";
        // 支持解析多段sql
        List<SQLStatement> stmtList = SQLUtils.parseStatements(sql, DbType.mysql);
        System.out.println(stmtList);
    }


}