package com.changlu.ast;

import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.sql.ast.SQLExpr;
import com.alibaba.druid.sql.ast.SQLLimit;
import com.alibaba.druid.sql.ast.SQLOrderBy;
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.ast.expr.SQLBinaryOpExpr;
import com.alibaba.druid.sql.ast.expr.SQLBinaryOperator;
import com.alibaba.druid.sql.ast.expr.SQLInSubQueryExpr;
import com.alibaba.druid.sql.ast.statement.*;
import com.alibaba.druid.util.JdbcConstants;

import java.util.List;

/**
 * 解析astnode树
 */
public class Demo1 {

    public static void main(String[] args) {
        String sql = "select id from (select id, name from test) as tmp where id in (select id from user where id = 1)";
        enhanceSelectSql(sql);
    }

    /**
     * 处理select sql 场景
     */
    public static void enhanceSelectSql(String sql) {
        // 解析 只选择第一条sql情况
        List<SQLStatement> statements = SQLUtils.parseStatements(sql, JdbcConstants.MYSQL);
        SQLStatement sqlStatement = statements.get(0);

        // 只考虑查询语句
        SQLSelectStatement sqlSelectStatement = (SQLSelectStatement) sqlStatement;
        SQLSelectQuery sqlSelectQuery = sqlSelectStatement.getSelect().getQuery();

        // 查询条件包含两种情况
        if (sqlSelectQuery instanceof SQLSelectQueryBlock) {
            SQLSelectQueryBlock sqlSelectQueryBlock = (SQLSelectQueryBlock) sqlSelectQuery;
            dealSQLSelectQueryBlock(sqlSelectQueryBlock);
        // union all 的查询条件
        }else if (sqlSelectQuery instanceof SQLUnionQuery) {
            SQLUnionQuery sqlUnionQuery = (SQLUnionQuery) sqlSelectQuery;
            dealSQLUnionQuery(sqlUnionQuery);
        }

    }

    /**
     * 针对SQLSelectQueryBlock 进行处理
     * @param sqlSelectQueryBlock
     */
    public static void dealSQLSelectQueryBlock(SQLSelectQueryBlock sqlSelectQueryBlock) {
        // 获取字段列表
        List<SQLSelectItem> selectItems = sqlSelectQueryBlock.getSelectList();
        selectItems.forEach(x -> {
            // 处理---------------------
        });

        // 获取表
        SQLTableSource table = sqlSelectQueryBlock.getFrom();
        // 普通单表
        if (table instanceof SQLExprTableSource) {
            // 处理---------------------
            // join多表
        } else if (table instanceof SQLJoinTableSource) {
            // 处理---------------------
            // 子查询作为表
        } else if (table instanceof SQLSubqueryTableSource) {
            // 处理---------------------
            SQLSubqueryTableSource sqlSubqueryTableSource = (SQLSubqueryTableSource) table;
            SQLSelect select = sqlSubqueryTableSource.getSelect();
            // 处理
        }

        // 获取where条件
        SQLExpr where = sqlSelectQueryBlock.getWhere();
        // 如果是二元表达式
        if (where instanceof SQLBinaryOpExpr) {
            SQLBinaryOpExpr   sqlBinaryOpExpr = (SQLBinaryOpExpr) where;
            SQLExpr           left            = sqlBinaryOpExpr.getLeft();
            SQLBinaryOperator operator        = sqlBinaryOpExpr.getOperator();
            SQLExpr           right           = sqlBinaryOpExpr.getRight();
            // 处理---------------------
            // 如果是子查询
        } else if (where instanceof SQLInSubQueryExpr) {
            SQLInSubQueryExpr sqlInSubQueryExpr = (SQLInSubQueryExpr) where;
            // 处理---------------------
        }

        // 获取分组
        SQLSelectGroupByClause groupBy = sqlSelectQueryBlock.getGroupBy();
        // 处理---------------------
        // 获取排序
        SQLOrderBy orderBy = sqlSelectQueryBlock.getOrderBy();
        // 处理---------------------
        // 获取分页
        SQLLimit limit = sqlSelectQueryBlock.getLimit();
        // 处理---------------------

    }

    public static void dealSQLUnionQuery(SQLUnionQuery sqlUnionQuery) {

    }


}
