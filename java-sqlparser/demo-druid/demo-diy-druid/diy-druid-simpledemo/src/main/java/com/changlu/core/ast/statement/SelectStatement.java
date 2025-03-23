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
