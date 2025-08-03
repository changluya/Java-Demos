package com.changlu.springboot.milvus.config;
 
import io.milvus.client.MilvusServiceClient;
import io.milvus.param.ConnectParam;
import io.milvus.v2.client.ConnectConfig;
import io.milvus.v2.client.MilvusClientV2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
 
/**
 * @description  Milvus向量数据库配置
 * @author changlu
 * @date 2025/6/6 21:57
 */
@Configuration
public class MilvusConfig {
 
    @Value("${milvus.host}")
    private String host;
 
    @Value("${milvus.port}")
    private Integer port;
 
    @Bean
    public MilvusClientV2 milvusClientV2() {
        String uri = "http://" + this.host + ":" + this.port.toString();
        return new MilvusClientV2(ConnectConfig.builder().uri(uri).build());
    }
 
    @Bean
    public MilvusServiceClient milvusServiceClient() {
        ConnectParam connectParam = ConnectParam.newBuilder()
                .withHost(host)
                .withPort(port)
                .build();
        return new MilvusServiceClient(connectParam);
    }
}