package org.apache.calcite.sql.ddl;

import org.apache.calcite.sql.*;
import org.apache.calcite.sql.parser.SqlParserPos;

import java.util.List;

/**
 语法如下：
 # 创建函数关键字
 create function
 # 函数名
 hr.custom_function
 # as关键字
 as
 # 类名称
 'com.github.quxiucheng.calcite.func.CustomFunction'
 # 可选 方法名称
 method 'eval'
 # 可选 备注信息
 comment 'comment'
 # 可选 附件变量
 property ('a'='b','c'='1')

 介绍：
 SqlCreateFunction 类用于表示 SQL 中创建函数（CREATE FUNCTION）的语句。
 它继承自 SqlCall 类，并实现了特定于 CREATE FUNCTION 语句的行为。

 */
public class SqlCreateFunction extends SqlCall {

    // 函数名节点
    private SqlNode functionName;
    // Java 类名，包含实现该函数的类
    private String className;
    // 方法名，如果函数是通过某个具体方法实现的，则指定该方法名
    private String methodName;
    // 注释，用于描述该函数的用途或其它信息
    private String comment;
    // 属性列表，通常用于指定函数的其他配置选项
    private SqlNodeList properties;

    public SqlCreateFunction(SqlParserPos pos, SqlNode functionName, String className, String methodName, String comment, SqlNodeList properties) {
        super(pos);
        this.functionName = functionName;
        this.className = className;
        this.methodName = methodName;
        this.comment = comment;
        this.properties = properties;
    }

    public SqlNode getFunctionName() {
        return functionName;
    }

    public String getClassName() {
        return className;
    }

    public String getMethodName() {
        return methodName;
    }

    public SqlNodeList getProperties() {
        return properties;
    }

    public String getComment() {
        return comment;
    }

    @Override
    public SqlOperator getOperator() {
        return null;
    }

    @Override
    public List<SqlNode> getOperandList() {
        return null;
    }

    /**
     * 获取 SQL 语句的类型。
     * 返回 SqlKind.OTHER_DDL，表示这是一个其他类型的 DDL 语句。
     *
     * @return SqlKind.OTHER_DDL
     */
    @Override
    public SqlKind getKind() {
        return SqlKind.OTHER_DDL;
    }

    /**
     * 将 SQL 创建函数的语句解析为字符串。
     * 该方法会根据提供的参数生成对应的 SQL 语句。
     *
     * @param writer   SQL 写入器
     * @param leftPrec 左操作数优先级
     * @param rightPrec 右操作数优先级
     */
    @Override
    public void unparse(SqlWriter writer, int leftPrec, int rightPrec) {
        writer.keyword("CREATE");
        writer.keyword("FUNCTION");
        functionName.unparse(writer, leftPrec, rightPrec);
        writer.keyword("AS");
        writer.print("'" + className + "'");
        if (methodName != null) {
            writer.newlineAndIndent();
            writer.keyword("METHOD");
            writer.print("'" + methodName + "'");
        }
        if (properties != null) {
            writer.newlineAndIndent();
            writer.keyword("PROPERTY");
            SqlWriter.Frame propertyFrame = writer.startList("(", ")");
            for (SqlNode property : properties) {
                writer.sep(",", false);
                writer.newlineAndIndent();
                writer.print("  ");
                property.unparse(writer, leftPrec, rightPrec);
            }
            writer.newlineAndIndent();
            writer.endList(propertyFrame);
        }
        if (comment != null) {
            writer.newlineAndIndent();
            writer.keyword("COMMENT");
            writer.print("'" + comment + "'");
        }
    }
}
