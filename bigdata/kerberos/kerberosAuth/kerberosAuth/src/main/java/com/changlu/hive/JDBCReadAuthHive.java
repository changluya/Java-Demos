package com.changlu.hive;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.security.UserGroupInformation;

import java.io.IOException;
import java.sql.*;

/**
 * 通过JDBC方式读取Kerberos认证Hive的数据
 */
public class JDBCReadAuthHive {
    // Kerberos主体
    static final String principal = "hive/node1@EXAMPLE.COM";

    // Kerberos配置文件路径
    static final String krb5FilePath = "E:\\自学历程\\Gitee仓库\\demo-exer\\bigdata\\kerberos\\kerberosAuth\\kerberosAuth\\src\\main\\resources\\krb5.conf";

    // Keytab文件路径
    static final String keytabFilePath = "E:\\自学历程\\Gitee仓库\\demo-exer\\bigdata\\kerberos\\kerberosAuth\\kerberosAuth\\src\\main\\resources\\hive.service.keytab";

    public static void main(String[] args) throws SQLException, ClassNotFoundException, IOException {
        // 1.加载Kerberos配置文件
        System.setProperty("java.security.krb5.conf", krb5FilePath);
        // 2.设置Kerberos认证
        Configuration configuration = new Configuration();
        configuration.set("hadoop.security.authentication", "kerberos");
        UserGroupInformation.setConfiguration(configuration);
        UserGroupInformation.loginUserFromKeytab(principal, keytabFilePath);
        // 3.JDBC连接字符串
        String jdbcURL = "jdbc:hive2://node1:10000/default;principal=hive/node1@EXAMPLE.COM";
        Class.forName("org.apache.hive.jdbc.HiveDriver");
        try {
            // 4.创建Hive连接
            Connection connection = DriverManager.getConnection(jdbcURL, "", "");
            // 5.执行Hive查询
            Statement statement = connection.createStatement();
            ResultSet rs = statement.executeQuery("SELECT id,name,age FROM person");
            // 6.处理查询结果
            while (rs.next()) {
                System.out.println(rs.getInt(1) + "," +
                        rs.getString(2)+ "," +
                        rs.getInt(3)) ;
            }
            // 7.关闭连接
            rs.close();
            statement.close();
            connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }

    }
}
