package com.changlu.demo.building;

import net.sf.jsqlparser.JSQLParserException;
import net.sf.jsqlparser.schema.Column;
import net.sf.jsqlparser.schema.Table;
import net.sf.jsqlparser.statement.select.Select;
import net.sf.jsqlparser.util.SelectUtils;

/**
 * 创建一个简单的select
 */
public class Demo01 {

    public static void main(String[] args) throws JSQLParserException {
        // SELECT * FROM mytable
        Select select = SelectUtils.buildSelectFromTable(new Table("mytable"));
        System.out.println(select);
        // select包含select * from mytable。
        select = SelectUtils.buildSelectFromTableAndExpressions(new Table("mytable"), new Column("a"), new Column("b"));
        System.out.println(select);
        // 或者更简单，如果你不想构建正确的表达式树，你可以提供简单的文本表达式，它将被解析并包含在你的选择中。
        // 示范：SELECT a + b, test FROM mytable
        select = SelectUtils.buildSelectFromTableAndExpressions(new Table("mytable"), "a+b", "test");
        System.out.println(select);
    }

}
