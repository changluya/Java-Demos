package com.changlu.redission.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import javax.sql.DataSource;

@Configuration
public class TransactionConfig {
    @Autowired
    private DataSourceProperties dataSourceProperties;
    
    @Bean
    public DataSource dataSource() {
        // 配置数据源
        return dataSourceProperties.initializeDataSourceBuilder().build();
    }
    
    @Bean
    public DataSourceTransactionManager transactionManager() {
        // 配置事务管理器
        return new DataSourceTransactionManager(dataSource());
    }
}