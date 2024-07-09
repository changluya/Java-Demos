package com.changlu.hive;

import org.apache.flink.table.api.EnvironmentSettings;
import org.apache.flink.table.api.TableEnvironment;
import org.apache.flink.table.catalog.hive.HiveCatalog;
import org.apache.hadoop.security.UserGroupInformation;

/**
 * Flink读取Kerberos认证Hive的数据
 */
public class FlinkReadAuthHive {
    public static void main(String[] args) throws Exception {
        //进行kerberos认证
        System.setProperty("java.security.krb5.conf", "E:\\自学历程\\Gitee仓库\\demo-exer\\bigdata\\kerberos\\kerberosAuth\\kerberosAuth\\src\\main\\resources\\krb5.conf");
        String principal = "hive/node1@EXAMPLE.COM";
        String keytabPath = "E:\\自学历程\\Gitee仓库\\demo-exer\\bigdata\\kerberos\\kerberosAuth\\kerberosAuth\\src\\main\\resources\\hive.service.keytab";
        UserGroupInformation.loginUserFromKeytab(principal, keytabPath);

        /**
         * FlinkConnector 读取Hive数据
         */
        EnvironmentSettings settings = EnvironmentSettings.inBatchMode();//批方式读取
        TableEnvironment tableEnv = TableEnvironment.create(settings);

        //自己取得名称
        String name            = "myhive";
        //hive数据库名称
        String defaultDatabase = "default";
        //hive-site.xml位置
        String hiveConfDir     = "E:\\自学历程\\Gitee仓库\\demo-exer\\bigdata\\kerberos\\kerberosAuth\\kerberosAuth\\src\\main\\resources";

        HiveCatalog hive = new HiveCatalog(name, defaultDatabase, hiveConfDir);
        tableEnv.registerCatalog("myhive", hive);

        tableEnv.useCatalog("myhive");

        //读取Hive表数据
        tableEnv.executeSql("select * from person").print();


    }
}
