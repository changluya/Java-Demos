package org.apache.calcite.sql.ddl;

import com.google.common.collect.ImmutableList;
import org.apache.calcite.sql.*;
import org.apache.calcite.sql.parser.SqlParserPos;
import org.apache.calcite.util.NlsString;

import java.util.List;

import static java.util.Objects.requireNonNull;

public class SqlProperty extends SqlCall {

    /**
     * 定义特殊操作符
     */
    protected static final SqlOperator OPERATOR =
            new SqlSpecialOperator("Property", SqlKind.OTHER);

    private SqlNode key;
    private SqlNode value;

    public SqlProperty(SqlParserPos pos, SqlNode key, SqlNode value) {
        super(pos);
        this.key = requireNonNull(key, "Property key is missing");
        this.value = requireNonNull(value, "Property value is missing");
    }

    @Override
    public SqlOperator getOperator() {
        return OPERATOR;
    }

    @Override
    public List<SqlNode> getOperandList() {
        return ImmutableList.of(key, value);
    }

    @Override
    public SqlKind getKind() {
        return SqlKind.OTHER;
    }

    @Override
    public void unparse(SqlWriter writer, int leftPrec, int rightPrec) {
        key.unparse(writer, leftPrec, rightPrec);
        writer.keyword("=");
        value.unparse(writer, leftPrec, rightPrec);
    }

    public SqlNode getKey() {
        return key;
    }

    public SqlNode getValue() {
        return value;
    }

    public String getKeyString() {
        return key.toString();
    }

    public String getValueString() {
        return ((NlsString) SqlLiteral.value(value)).getValue();
    }

}
