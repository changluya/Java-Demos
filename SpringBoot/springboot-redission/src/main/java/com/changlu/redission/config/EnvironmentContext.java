package com.changlu.redission.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

@Component
public class EnvironmentContext {

    @Autowired
    private Environment environment;

    public String getRedisSentinel() {
        return environment.getProperty("spring.redis.sentinel.nodes", "");
    }

    public int getRedisDB() {
        return Integer.parseInt(environment.getProperty("spring.redis.database", "1"));
    }

    public String getRedisUrl() {
        return environment.getProperty("spring.redis.host", "127.0.0.1");
    }

    public String getRedisPassword() {
        return environment.getProperty("spring.redis.password");
    }

    public int getRedisPort() {
        return Integer.parseInt(environment.getProperty("spring.redis.port", "6379"));
    }
}
