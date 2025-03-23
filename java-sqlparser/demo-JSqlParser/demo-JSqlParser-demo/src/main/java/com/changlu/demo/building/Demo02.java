package com.changlu.demo.building;

import net.sf.jsqlparser.JSQLParserException;
import net.sf.jsqlparser.expression.ExpressionVisitor;
import net.sf.jsqlparser.expression.LongValue;
import net.sf.jsqlparser.expression.operators.relational.ExpressionList;
import net.sf.jsqlparser.parser.CCJSqlParserUtil;
import net.sf.jsqlparser.schema.Column;
import net.sf.jsqlparser.statement.insert.Insert;
import net.sf.jsqlparser.statement.select.FromItemVisitor;

/**
 * 扩展简单插入
 */
public class Demo02 {

    public static void main(String[] args) throws JSQLParserException {
        Insert insert = (Insert) CCJSqlParserUtil.parse("insert into mytable (col1) values (1)");
        System.out.println(insert.toString());

        //adding a column
        insert.getColumns().add(new Column("col2"));
        System.out.println(insert);

        //adding another column
        insert.getColumns().add(new Column("col3"));
        System.out.println(insert);

        //adding a value using a visitor
        // tag为jsqlparser-1.2.0 有该实现，当前4.9.0没有
//        insert.getItemsList().accept(new ItemsListVisitor() {
//
//            public void visit(SubSelect subSelect) {
//                throw new UnsupportedOperationException("Not supported yet.");
//            }
//
//            public void visit(ExpressionList expressionList) {
//                expressionList.getExpressions().add(new LongValue(5));
//            }
//
//            public void visit(MultiExpressionList multiExprList) {
//                throw new UnsupportedOperationException("Not supported yet.");
//            }
//        });

    }

}
