package com.changlu.redission.config;

import io.micrometer.core.instrument.util.StringUtils;
import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.redisson.config.SingleServerConfig;
import org.redisson.config.TransportMode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class CacheConfig {

    @Autowired
    private EnvironmentContext environmentContext;

    @Bean
    public RedissonClient redissonClient(){
        Config config = new Config();
        config.setTransportMode(TransportMode.NIO);
        String redisPassword = getRedisPassword();
        int redisDB = environmentContext.getRedisDB();
        // 单节点服务器
        SingleServerConfig singleServerConfig = config.useSingleServer();
        singleServerConfig.setAddress(getRedissonAddress());
        singleServerConfig.setDatabase(redisDB);
        if (StringUtils.isNotBlank(redisPassword)) {
            singleServerConfig.setPassword(redisPassword);
        }
        return Redisson.create(config);
    }

    private String getRedissonAddress() {
        return "redis://" + environmentContext.getRedisUrl() + ":" + environmentContext.getRedisPort();
    }


    public String getRedisPassword() {
        String redisPassword;
        try {
            redisPassword = environmentContext.getRedisPassword();
        } catch (Exception e) {
            redisPassword = environmentContext.getRedisPassword();
        }
        return redisPassword;
    }

}
