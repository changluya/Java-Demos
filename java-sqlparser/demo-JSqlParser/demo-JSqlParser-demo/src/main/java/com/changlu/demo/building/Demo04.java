package com.changlu.demo.building;

import net.sf.jsqlparser.JSQLParserException;
import net.sf.jsqlparser.expression.LongValue;
import net.sf.jsqlparser.expression.StringValue;
import net.sf.jsqlparser.parser.CCJSqlParserUtil;
import net.sf.jsqlparser.statement.Statement;
import net.sf.jsqlparser.util.deparser.ExpressionDeParser;
import net.sf.jsqlparser.util.deparser.SelectDeParser;
import net.sf.jsqlparser.util.deparser.StatementDeParser;

public class Demo04 {

    // 自定义访问者类型 替换String字段值 & Long类型值
    static class ReplaceColumnAndLongValues extends ExpressionDeParser {

        @Override
        public void visit(StringValue stringValue) {
            this.getBuffer().append("?");
        }

        @Override
        public void visit(LongValue longValue) {
            this.getBuffer().append("?");
        }
    }

    public static String cleanStatement(String sql) throws JSQLParserException {
        StringBuilder buffer = new StringBuilder();
        ExpressionDeParser expr = new ReplaceColumnAndLongValues();

        // 绑定ExpressionDeParser & StringBuilder
        SelectDeParser selectDeparser = new SelectDeParser(expr, buffer);
        // 表达式解析起设置select访问器
        expr.setSelectVisitor(selectDeparser);
        expr.setBuffer(buffer);

        // 将表达式绑定到stmtDeparser中，后续使用astnode去进行accept操作
        StatementDeParser stmtDeparser = new StatementDeParser(expr, selectDeparser, buffer);

        Statement stmt = CCJSqlParserUtil.parse(sql);

        // 间接执行访问者模式操作
        stmt.accept(stmtDeparser);
        return stmtDeparser.getBuffer().toString();
    }

    public static void main(String[] args) throws JSQLParserException {
        System.out.println(cleanStatement("SELECT 'abc', 5 FROM mytable WHERE col='test'"));
        System.out.println(cleanStatement("UPDATE table1 A SET A.columna = 'XXX' WHERE A.cod_table = 'YYY'"));
        System.out.println(cleanStatement("INSERT INTO example (num, name, address, tel) VALUES (1, 'name', 'test ', '1234-1234')"));
        System.out.println(cleanStatement("DELETE FROM table1 where col=5 and col2=4"));
    }

}
