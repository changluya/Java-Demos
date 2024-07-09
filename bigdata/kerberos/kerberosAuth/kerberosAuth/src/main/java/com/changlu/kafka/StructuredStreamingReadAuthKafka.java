package com.changlu.kafka;

import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.SparkSession;
import org.apache.spark.sql.streaming.StreamingQuery;
import org.apache.spark.sql.streaming.StreamingQueryException;
import java.util.concurrent.TimeoutException;

/**
 * StructuredStreaming 读取Kerberos 认证的Kafka数据
 */
public class StructuredStreamingReadAuthKafka {
    public static void main(String[] args) throws TimeoutException, StreamingQueryException {
        //准备JAAS配置文件路径
        String kafkaClientJaasFile = "E:\\自学历程\\Gitee仓库\\demo-exer\\bigdata\\kerberos\\kerberosAuth\\kerberosAuth\\src\\main\\resources\\kafka\\kafka_client_jaas.conf";
        // Kerberos配置文件路径
        String krb5FilePath = "E:\\自学历程\\Gitee仓库\\demo-exer\\bigdata\\kerberos\\kerberosAuth\\kerberosAuth\\src\\main\\resources\\krb5.conf";

        System.setProperty("java.security.auth.login.config", kafkaClientJaasFile);
        System.setProperty("java.security.krb5.conf", krb5FilePath);

        //1.创建对象
        SparkSession spark = SparkSession.builder()
                .master("local")
                .appName("kafka source")
                .config("spark.sql.shuffle.partitions", 1)
                .getOrCreate();

        spark.sparkContext().setLogLevel("Error");

        //2.读取kafka 数据
        Dataset<Row> df = spark.readStream()
                .format("kafka")
                .option("kafka.bootstrap.servers", "node1:9092,node2:9092,node3:9092")
                .option("subscribe", "test")
                .option("startingOffsets", "earliest")
                //kerberos安全认证
                .option("kafka.security.protocol", "SASL_PLAINTEXT")
                .option("kafka.sasl.mechanism", "GSSAPI")
                .option("kafka.sasl.kerberos.service.name", "kafka")
                .load();

        Dataset<Row> result = df.selectExpr("cast (key as string)", "cast (value as string)");

        StreamingQuery query = result.writeStream()
                .format("console")
                .start();

        query.awaitTermination();
    }
}
