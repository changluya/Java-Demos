package com.changlu;

import com.alibaba.druid.DbType;
import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.ast.statement.SQLJoinTableSource;
import com.alibaba.druid.sql.ast.statement.SQLSelectItem;
import com.alibaba.druid.sql.dialect.oracle.ast.stmt.OracleSelectQueryBlock;
import com.alibaba.druid.sql.dialect.oracle.ast.stmt.OracleSelectTableReference;
import com.alibaba.druid.sql.dialect.oracle.visitor.OracleASTVisitorAdapter;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

class OracleTablesVisitor extends OracleASTVisitorAdapter {

    private Map<String, String> tables;

    OracleTablesVisitor(Map<String, String> tables) {
        this.tables = tables;
    }

    @Override
    public boolean visit(OracleSelectTableReference x) {
        String tableName = x.getTableName();
        tables.put(x.getAlias(), tableName);
        return super.visit(x);
    }
}

public class Main2 {

    public static void main(String[] args) {
        Map<String, String> tables = new HashMap<>();
        OracleTablesVisitor oracleTablesVisitor = new OracleTablesVisitor(tables);
        String sql = "SELECT \n" +
                "    e.employee_id AS \"员工ID\",\n" +
                "    e.first_name AS \"名字\",\n" +
                "    e.last_name AS \"姓氏\",\n" +
                "    d.department_name AS \"部门名称\"\n" +
                "FROM \n" +
                "    employees e\n" +
                "LEFT JOIN \n" +
                "    departments d\n" +
                "ON \n" +
                "    e.department_id = d.department_id;";
        // 支持解析多段sql
        List<SQLStatement> stmtList = SQLUtils.parseStatements(sql, DbType.oracle);
        SQLStatement sqlStatement = stmtList.get(0);
        sqlStatement.accept(oracleTablesVisitor);
        System.out.println(tables);
    }

}
