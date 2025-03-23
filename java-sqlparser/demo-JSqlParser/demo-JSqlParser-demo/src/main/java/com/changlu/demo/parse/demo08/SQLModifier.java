package com.changlu.demo.parse.demo08;

import net.sf.jsqlparser.parser.*;
import net.sf.jsqlparser.statement.Statement;

public class SQLModifier extends CCJSqlParserDefaultVisitor {
    @Override
    public Object visit(SimpleNode node, Object data) {
        Object value = node.jjtGetValue();
        switch (node.getId()) {
            case CCJSqlParserTreeConstants.JJTTABLENAME:
                break;
            case CCJSqlParserTreeConstants.JJTCOLUMN:
                break;
            case CCJSqlParserTreeConstants.JJTFUNCTION:
                break;
            default:
                break;
        }
        System.out.println(node.getId());
        return super.visit(node, data);
    }

    public static void main(String[] args) throws ParseException {
        String originalSql = "select * from user where id = 1";
        CCJSqlParser parser = CCJSqlParserUtil.newParser(originalSql);
        Statement statement = parser.Statement();
        parser.getASTRoot().jjtAccept(new SQLModifier(), null);
    }

}
