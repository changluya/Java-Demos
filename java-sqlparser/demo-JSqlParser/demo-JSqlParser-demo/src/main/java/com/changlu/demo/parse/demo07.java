package com.changlu.demo.parse;

import net.sf.jsqlparser.expression.operators.relational.EqualsTo;
import net.sf.jsqlparser.schema.Column;
import net.sf.jsqlparser.schema.Table;
import net.sf.jsqlparser.statement.select.Select;
import net.sf.jsqlparser.util.SelectUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * SelectUtils 构建sql
 */
public class demo07 {

    public static final Logger log = LoggerFactory.getLogger(demo03.class);

    public static void main(String[] args) throws Exception{
        // 构建select sql
        Select select = SelectUtils.buildSelectFromTableAndExpressions(new Table("mytable"),
                new Column("a"), new Column("b"));
        // 添加一个字段
        SelectUtils.addExpression(select, new Column("c"));
        // 添加一个条件表达式 id = 1
        final EqualsTo equalsTo = new EqualsTo();
        equalsTo.setLeftExpression(new Column("id"));
        equalsTo.setRightExpression(new Column("1"));
        // 添加一个group by
        SelectUtils.addGroupBy(select, new Column("d"));
        log.info("==> JsqlParser Build SQL: {}", select.toString());
    }

}
