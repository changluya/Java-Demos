package com.changlu.core.ast;

// 表示table数据源
public class SQLTableSource {

    private final String tableName;

    public SQLTableSource(String tableName) {
        this.tableName = tableName;
    }

    public String getTableName() {
        return tableName;
    }

}
