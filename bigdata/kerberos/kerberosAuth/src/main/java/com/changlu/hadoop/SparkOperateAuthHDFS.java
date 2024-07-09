/**
 * @description TODO
 * @author changlu
 * @date 2024/07/06 20:55
 * @version 1.0
 */
package com.changlu.hadoop;

import org.apache.hadoop.security.UserGroupInformation;
import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaSparkContext;

/**
 * @description  Spark操作kerberos认证的HDFS
 * @author changlu
 * @date 2024-07-06 20:55
 */
public class SparkOperateAuthHDFS {

    public static void main(String[] args) throws Exception {
        //进行kerberos认证
        System.setProperty("java.security.krb5.conf", "E:\\自学历程\\Gitee仓库\\demo-exer\\bigdata\\kerberos\\kerberosAuth\\kerberosAuth\\src\\main\\resources\\krb5.conf");
        String principal = "zhangsan@EXAMPLE.COM";
        String keytabPath = "E:\\自学历程\\Gitee仓库\\demo-exer\\bigdata\\kerberos\\kerberosAuth\\kerberosAuth\\src\\main\\resources\\zhangsan.keytab";
        UserGroupInformation.loginUserFromKeytab(principal, keytabPath);
        //进行spark配置
        SparkConf conf = new SparkConf();
        conf.setMaster("local");
        conf.setAppName("SparkOperateAuthHDFS");
        JavaSparkContext jsc = new JavaSparkContext(conf);
        //读取指定的hdfs文件，进行逐行打印
        jsc.textFile("hdfs://mycluster/wc.txt").foreach(line -> System.out.println(line));
        jsc.stop();
    }

}
