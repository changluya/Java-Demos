package com.changlu.demo.building;

import net.sf.jsqlparser.expression.StringValue;
import net.sf.jsqlparser.parser.CCJSqlParserUtil;
import net.sf.jsqlparser.schema.Column;
import net.sf.jsqlparser.statement.select.Select;
import net.sf.jsqlparser.util.deparser.ExpressionDeParser;
import net.sf.jsqlparser.util.deparser.SelectDeParser;

/**
 * 替换字符串值
 * 背景：有些人想要发布一些SQL，但是想要打乱所有具体的值。这里有一个小例子来说明如何实现这一点。简而言之，访问者扫描整个树，找到所有StringValues并将当前值替换为String。
 * 这段代码的核心是使用 JSQLParser 库将 SQL 语句解析为抽象语法树，然后通过自定义的访问者模式遍历树中的节点，将所有字符串值替换为 "XXXX"，最后输出修改后的 SQL 语句。
 */
public class Demo03 {

    public static void main(String[] args) throws Exception{
        String sql ="SELECT NAME, ADDRESS, COL1 FROM USER WHERE SSN IN ('11111111111111', '22222222222222');";
        try {
            // 使用 CCJSqlParserUtil 类的 parse 方法将 SQL 字符串解析为 Select 对象。
            // 该方法会将 SQL 语句解析为一个抽象语法树（AST），方便后续对其进行操作。
            Select select = (Select) CCJSqlParserUtil.parse(sql);

            // 创建一个 StringBuilder 对象，用于存储最终修改后的 SQL 语句。
            // StringBuilder 是一个可变的字符序列，适合动态拼接字符串。
            StringBuilder buffer = new StringBuilder();

            // 创建一个 ExpressionDeParser 对象，它是 JSQLParser 中的一个解析器，用于处理表达式。重写其 visit 方法来实现对字符串值的替换。
            ExpressionDeParser expressionDeParser = new ExpressionDeParser() {
                // 重写 visit 方法，当访问到 StringValue 对象时会调用该方法。 StringValue 对象代表 SQL 语句中的字符串值。
                @Override
                public void visit(StringValue stringValue) {
                    // 当访问到字符串值时，将 "XXXX" 追加到 buffer 中，而不是原来的字符串值。这样就实现了将所有字符串值替换为 "XXXX" 的功能。
                    this.getBuffer().append("?");
                }

                // 字段名值
                @Override
                public void visit(Column tableColumn) {
                    this.getBuffer().append("?");
                }
            };

            // 创建一个 SelectDeParser 对象，它是 JSQLParser 中用于解析 SELECT 语句的解析器。
            //          构造函数接受两个参数：ExpressionDeParser 对象和 StringBuilder 对象。
            // ExpressionDeParser 用于处理表达式，buffer 用于存储最终的 SQL 语句。
            SelectDeParser deparser = new SelectDeParser(expressionDeParser, buffer);

            // 将 SelectDeParser 对象设置为 ExpressionDeParser 的 SelectVisitor。
            // 这样在解析过程中，ExpressionDeParser 会将 SELECT 语句的处理委托给 SelectDeParser。
            expressionDeParser.setSelectVisitor(deparser);
            // ExpressionDeParser 在处理表达式时会将结果存储到该缓冲区中。
            expressionDeParser.setBuffer(buffer);

            // 调用 select 对象的 getSelectBody 方法获取 SELECT 语句的主体部分。调用 accept 方法，将 SelectDeParser 对象作为访问者传入。
            // 这会触发 SelectDeParser 对 SELECT 语句的解析和处理，同时 ExpressionDeParser 会处理其中的表达式。
            select.getSelectBody().accept(deparser);

            System.out.println(buffer);
        }catch (Exception ex) {
            ex.printStackTrace();
        }

    }

}
