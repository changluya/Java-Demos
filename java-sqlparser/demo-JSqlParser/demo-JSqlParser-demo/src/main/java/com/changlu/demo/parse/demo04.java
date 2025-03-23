package com.changlu.demo.parse;

import net.sf.jsqlparser.parser.CCJSqlParserUtil;
import net.sf.jsqlparser.statement.Statement;
import net.sf.jsqlparser.statement.select.Select;
import net.sf.jsqlparser.util.TablesNamesFinder;

import java.util.Set;


/**
 * 从SQL语句中提取表名
 */
public class demo04 {
    public static void main(String[] args) throws Exception{
//        String sql = "SELECT * FROM MY_TABLE1";
//        String sql = "SELECT * FROM t1 left join t2 on t1.a = t2.a";
        String sql = "SELECT * FROM t1 union all select * from t2";
        Statement statement = CCJSqlParserUtil.parse(sql);
        Select selectStatement = (Select) statement;
        // 表名查找器
        TablesNamesFinder tablesNamesFinder = new TablesNamesFinder();
        Set<String> tables = tablesNamesFinder.getTables(statement);
        System.out.println(tables);
    }
}
