package org.apache.calcite.sql.ddl;

import org.apache.calcite.sql.*;
import org.apache.calcite.sql.parser.SqlParserPos;

import java.util.List;

/**
 * SqlCreateFunction 类表示一个 SQL 创建函数的语句。
 * 它继承自 SqlCall，表示这是一个 SQL 调用语句。
 */
public class SqlCreateFunction extends SqlCall {

    // 成员变量，表示函数的名称，类型为 SqlNode
    private SqlNode functionName;

    // 成员变量，表示函数的 Java 类名
    private String className;

    // 成员变量，表示函数的属性列表，类型为 SqlNodeList
    private SqlNodeList properties;

    // 成员变量，表示函数的方法名
    private String methodName;

    // 成员变量，表示函数的注释
    private String comment;

    /**
     * 构造函数，用于创建一个 SqlCreateFunction 对象。
     *
     * @param pos          SQL 解析器位置
     * @param functionName 函数的名称
     * @param className    函数的 Java 类名
     * @param methodName   函数的方法名
     * @param comment      函数的注释
     * @param properties   函数的属性列表
     */
    public SqlCreateFunction(SqlParserPos pos,
                             SqlNode functionName, String className, String methodName, String comment,
                             SqlNodeList properties) {

        super(pos); // 调用父类 SqlCall 的构造函数
        this.functionName = functionName; // 初始化函数名称
        this.className = className; // 初始化类名
        this.properties = properties; // 初始化属性列表
        this.methodName = methodName; // 初始化方法名
        this.comment = comment; // 初始化注释
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
