package com.changlu.demo.parse;

import net.sf.jsqlparser.parser.CCJSqlParserUtil;
import net.sf.jsqlparser.statement.Statement;
import net.sf.jsqlparser.statement.select.Select;
import net.sf.jsqlparser.util.AddAliasesVisitor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class demo05 {

    public static final Logger log = LoggerFactory.getLogger(demo03.class);

    public static void main(String[] args) throws Exception{
        String sql = "SELECT id,name,nickname,age,job,department FROM staff_member WHERE nickname= '刘'";
        // Parse SQL
        Statement statement = CCJSqlParserUtil.parse(sql);
        if(statement instanceof Select){
            Select selectStatement = (Select) statement;
            // 借助使用自定义visitor来实现补充别名
            final AddAliasesVisitor instance = new AddAliasesVisitor();
            instance.setPrefix("tt");
            selectStatement.accept(instance);
            log.info("==> JSqlParser finalSQL: {}", selectStatement);
        }

    }
}
