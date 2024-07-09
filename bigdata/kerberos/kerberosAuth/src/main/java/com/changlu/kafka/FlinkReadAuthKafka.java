package com.changlu.kafka;

import org.apache.flink.api.common.eventtime.WatermarkStrategy;
import org.apache.flink.api.common.typeinfo.TypeHint;
import org.apache.flink.api.common.typeinfo.TypeInformation;
import org.apache.flink.api.java.tuple.Tuple2;
import org.apache.flink.connector.kafka.source.KafkaSource;
import org.apache.flink.connector.kafka.source.enumerator.initializer.OffsetsInitializer;
import org.apache.flink.connector.kafka.source.reader.deserializer.KafkaRecordDeserializationSchema;
import org.apache.flink.streaming.api.datastream.DataStreamSource;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.util.Collector;
import org.apache.kafka.clients.consumer.ConsumerRecord;

import java.io.IOException;

/**
 * Flink 读取Kerberos 认证的Kafka数据
 */
public class FlinkReadAuthKafka {
    public static void main(String[] args) throws Exception {
        //准备JAAS配置文件路径
        String kafkaClientJaasFile = "E:\\自学历程\\Gitee仓库\\demo-exer\\bigdata\\kerberos\\kerberosAuth\\kerberosAuth\\src\\main\\resources\\kafka\\kafka_client_jaas.conf";
        // Kerberos配置文件路径
        String krb5FilePath = "E:\\自学历程\\Gitee仓库\\demo-exer\\bigdata\\kerberos\\kerberosAuth\\kerberosAuth\\src\\main\\resources\\krb5.conf";

        System.setProperty("java.security.auth.login.config", kafkaClientJaasFile);
        System.setProperty("java.security.krb5.conf", krb5FilePath);

        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();

        KafkaSource<Tuple2<String, String>> kafkaSource = KafkaSource.<Tuple2<String, String>>builder()
                .setBootstrapServers("node1:9092,node2:9092,node3:9092") //设置Kafka 集群节点
                .setTopics("test") //设置读取的topic
                .setGroupId("my-test-group") //设置消费者组
                //kerberos安全认证
                .setProperty("security.protocol", "SASL_PLAINTEXT")
                .setProperty("sasl.mechanism", "GSSAPI")
                .setProperty("sasl.kerberos.service.name", "kafka")

                .setStartingOffsets(OffsetsInitializer.earliest()) //设置读取数据位置
                .setDeserializer(new KafkaRecordDeserializationSchema<Tuple2<String, String>>() {
                    //设置key ,value 数据获取后如何处理
                    @Override
                    public void deserialize(ConsumerRecord<byte[], byte[]> consumerRecord, Collector<Tuple2<String, String>> collector) throws IOException {
                        String key = null;
                        String value = null;
                        if(consumerRecord.key() != null){
                            key = new String(consumerRecord.key(), "UTF-8");
                        }
                        if(consumerRecord.value() != null){
                            value = new String(consumerRecord.value(), "UTF-8");
                        }
                        collector.collect(Tuple2.of(key, value));
                    }

                    //设置置返回的二元组类型
                    @Override
                    public TypeInformation<Tuple2<String, String>> getProducedType() {
                        return TypeInformation.of(new TypeHint<Tuple2<String, String>>() {
                        });
                    }
                })
                .build();

        DataStreamSource<Tuple2<String, String>> kafkaDS = env.fromSource(kafkaSource, WatermarkStrategy.noWatermarks(), "kafka-source");

        kafkaDS.print();

        env.execute();
    }
}
