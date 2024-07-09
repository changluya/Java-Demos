package com.changlu;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

public class DBUtil {
    static final HikariDataSource DS;
    static {
        HikariConfig config = new HikariConfig();
        config.setAutoCommit(true);//设置自动提交
        config.setDataSourceClassName("com.kingbase8.ds.KBSimpleDataSource");//设置数据源class
        config.setUsername("SYSTEM");
        config.setPassword("SYSTEM");
        config.addDataSourceProperty("serverName", "172.16.82.106");
        config.addDataSourceProperty("portNumber", 54321);
        config.addDataSourceProperty("databaseName", "TEST");
        DS = new HikariDataSource(config);
    }
    public static void close() {
        DS.close();
    }
}
