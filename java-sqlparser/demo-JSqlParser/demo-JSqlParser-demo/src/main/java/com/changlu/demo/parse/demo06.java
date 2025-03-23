package com.changlu.demo.parse;

import net.sf.jsqlparser.expression.LongValue;
import net.sf.jsqlparser.expression.operators.arithmetic.Addition;
import net.sf.jsqlparser.expression.operators.relational.EqualsTo;
import net.sf.jsqlparser.parser.CCJSqlParserUtil;
import net.sf.jsqlparser.schema.Column;
import net.sf.jsqlparser.schema.Table;
import net.sf.jsqlparser.statement.select.Join;
import net.sf.jsqlparser.statement.select.Select;
import net.sf.jsqlparser.util.SelectUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 动态加字段加表达式加条件
 */
public class demo06 {

    public static final Logger log = LoggerFactory.getLogger(demo03.class);

    public static void main(String[] args) throws Exception{
//        test01();
        test02();
    }

    // 增加一个表达式
    public static void test01() throws Exception{
        // 解析得到statement
        // 测试：select a from mytable union all select b from test 不支持  Not supported yet.
        Select select = (Select) CCJSqlParserUtil.parse("select a from mytable");
        // 添加表达式，这里添加一个字段
        SelectUtils.addExpression(select, new Column("b"));
        System.out.println(select);

        // 添加一个字段表达式 5+6
        Addition add = new Addition();
        add.setLeftExpression(new LongValue(5));
        add.setRightExpression(new LongValue(6));
        SelectUtils.addExpression(select, add);
        System.out.println(select);
    }

    /**
     * 增加一个join
     * @throws Exception
     */
    public static void test02() throws Exception{
        Select select = (Select) CCJSqlParserUtil.parse("select a from mytable");
        final EqualsTo equalsTo = new EqualsTo();
        equalsTo.setLeftExpression(new Column("a"));
        equalsTo.setRightExpression(new Column("b"));
        Join addJoin = SelectUtils.addJoin(select, new Table("mytable2"), equalsTo);
        addJoin.setLeft(true);
        // "SELECT a FROM mytable LEFT JOIN mytable2 ON a = b"
        System.out.println(select.toString());
    }

}
