package com.changlu.kafka;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;

import java.time.Duration;
import java.util.Arrays;
import java.util.Properties;
import java.util.UUID;

/**
 *  Java API 操作Kerbros认证的Kafka
 *  使用 JAAS 来进行 Kerberos 认证
 *  注意：kafka_client_jaas.conf文件中的keytab文件路径需要使用双斜杠或者反单斜杠
 */
public class OperateAuthKafka {
    public static void main(String[] args) {
        //准备JAAS配置文件路径
        String kafkaClientJaasFile = "E:\\自学历程\\Gitee仓库\\demo-exer\\bigdata\\kerberos\\kerberosAuth\\kerberosAuth\\src\\main\\resources\\kafka\\kafka_client_jaas.conf";
        // Kerberos配置文件路径
        String krb5FilePath = "E:\\自学历程\\Gitee仓库\\demo-exer\\bigdata\\kerberos\\kerberosAuth\\kerberosAuth\\src\\main\\resources\\krb5.conf";

        System.setProperty("java.security.auth.login.config", kafkaClientJaasFile);
        System.setProperty("java.security.krb5.conf", krb5FilePath);

        Properties props = new Properties();
        props.setProperty("bootstrap.servers", "node1:9092,node2:9092,node3:9092");
        props.setProperty("key.serializer", "org.apache.kafka.common.serialization.StringSerializer");
        props.setProperty("value.serializer", "org.apache.kafka.common.serialization.StringSerializer");
        //kerberos安全认证
        props.setProperty("security.protocol", "SASL_PLAINTEXT");
        props.setProperty("sasl.mechanism", "GSSAPI");
        props.setProperty("sasl.kerberos.service.name", "kafka");

        //向Kafka topic中发送消息
        KafkaProducer<String, String> kafkaProducer = new KafkaProducer<>(props);
        kafkaProducer.send(new ProducerRecord<>("test", "100"));
        kafkaProducer.send(new ProducerRecord<>("test", "200"));
        kafkaProducer.send(new ProducerRecord<>("test", "300"));
        kafkaProducer.send(new ProducerRecord<>("test", "400"));
        kafkaProducer.send(new ProducerRecord<>("test", "500"));

        kafkaProducer.close();
        System.out.println("消息发送成功");

        /**
         * 从Kafka topic中消费消息
         */
        props.setProperty("group.id", "test"+ UUID.randomUUID());
        //设置消费的位置，earliest表示从头开始消费，latest表示从最新的位置开始消费
        props.setProperty("auto.offset.reset", "earliest");
        props.setProperty("key.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
        props.setProperty("value.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
        KafkaConsumer<String, String> kafkaConsumer = new KafkaConsumer<>(props);
        kafkaConsumer.subscribe(Arrays.asList("test"));
        while (true) {
            // 拉取数据
            ConsumerRecords<String, String> consumerRecords = kafkaConsumer.poll(Duration.ofMillis(100));
            for (ConsumerRecord<String, String> record : consumerRecords) {
                // 获取数据对应的分区号
                int partition = record.partition();
                // 对应数据值
                String value = record.value();
                //对应数据的偏移量
                long lastoffset = record.offset();
                //对应数据发送的key
                String key = record.key();

                System.out.println("数据的key为:"+ key +
                        ",数据的value为:" + value +
                        ",数据的offset为:"+ lastoffset +
                        ",数据的分区为:"+ partition);
            }
        }

    }
}
