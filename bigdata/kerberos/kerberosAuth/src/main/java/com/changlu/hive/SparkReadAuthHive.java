package com.changlu.hive;

import org.apache.hadoop.security.UserGroupInformation;
import org.apache.spark.sql.SparkSession;

import java.io.IOException;

/**
 * Spark 读取Kerberos认证Hive的数据
 */
public class SparkReadAuthHive {
    public static void main(String[] args) throws IOException {
        //进行kerberos认证
        System.setProperty("java.security.krb5.conf", "E:\\自学历程\\Gitee仓库\\demo-exer\\bigdata\\kerberos\\kerberosAuth\\kerberosAuth\\src\\main\\resources\\krb5.conf");
        String principal = "hive/node1@EXAMPLE.COM";
        String keytabPath = "E:\\自学历程\\Gitee仓库\\demo-exer\\bigdata\\kerberos\\kerberosAuth\\kerberosAuth\\src\\main\\resources\\hive.service.keytab";
        UserGroupInformation.loginUserFromKeytab(principal, keytabPath);
        //spark进行hive配置，注意hive很多配置服务地址信息在hive-site.xml配置，所以这个配置需要传上来
        SparkSession spark = SparkSession.builder().appName("SparkReadAuthHive")
                .master("local")
//                .config("hive.metastore.uris", "thrift://node1:9083")
                .enableHiveSupport()
                .getOrCreate();
        spark.sql("select * from person").show();
        spark.stop();

    }
}
