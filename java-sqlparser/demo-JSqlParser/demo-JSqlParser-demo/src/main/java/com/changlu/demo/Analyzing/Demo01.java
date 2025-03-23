package com.changlu.demo.Analyzing;

import net.sf.jsqlparser.JSQLParserException;
import net.sf.jsqlparser.expression.Expression;
import net.sf.jsqlparser.parser.CCJSqlParserUtil;
import net.sf.jsqlparser.statement.select.PlainSelect;
import net.sf.jsqlparser.statement.select.Select;
import net.sf.jsqlparser.statement.select.SelectItem;
import net.sf.jsqlparser.statement.select.SelectItemVisitorAdapter;

import java.util.HashMap;
import java.util.Map;

public class Demo01 {

    public static void main(String[] args) throws JSQLParserException {
        Select stmt = (Select) CCJSqlParserUtil.parse("SELECT col1 AS a, col2 AS b, col3 AS c FROM table WHERE col1 = 10 AND col2 = 20 AND col3 = 30");

        Map<String, Expression> map = new HashMap<>();
        for (SelectItem selectItem : ((PlainSelect)stmt.getSelectBody()).getSelectItems()) {
            selectItem.accept(new SelectItemVisitorAdapter() {

                // select item选项
                @Override
                public void visit(SelectItem item) {
                    map.put(item.getAlias().getName(), item.getExpression());
                }

            });
        }

        System.out.println("map " + map);
    }

}
