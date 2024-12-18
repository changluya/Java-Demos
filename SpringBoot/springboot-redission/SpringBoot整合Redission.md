[toc]



Redission集成到springboot是有两种场景的，第一个场景是针对单台节点，第二个场景是针对多台节点。

当前配置是单台节点

# Redission详细配置步骤

![image-20241218170628525](https://pictured-bed.oss-cn-beijing.aliyuncs.com/img/2024/202412181706247.png)。

## pom依赖

```xml
    <dependencies>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-web</artifactId>
        </dependency>
        <dependency>
            <groupId>org.redisson</groupId>
            <artifactId>redisson-spring-boot-starter</artifactId>
            <version>3.17.7</version>
        </dependency>

        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-test</artifactId>
            <scope>test</scope>
        </dependency>

        <dependency>
            <groupId>junit</groupId>
            <artifactId>junit</artifactId>
            <version>4.13.2</version>
            <scope>test</scope>
        </dependency>
    </dependencies>
```

## application.yaml

```yaml
server:
  port: 8055
spring:
  redis:
    host: 127.0.0.1
    port: 6379
    database: 1
    password: 123456
# 直接配置参数
#redisson:
#  codec: org.redisson.codec.JsonJacksonCodec
#  threads: 4
#  netty:
#    threads: 4
#  single-server-config:
#    address: "redis://localhost:6379"
#    password: 123456
#    database: 0
```

## 配置类

### CacheConfig

```java
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
```

### EnvironmentContext

```java
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
```

## RedissionController

```java
package com.changlu.redission.controller;

import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@RestController
@RequestMapping("/redission")
public class RedissionController {

    @Autowired
    private RedissonClient redissonClient;

    @GetMapping("/key/{key}")
    public Map<String, String> redission(@PathVariable("key")String key) {
        RLock rLock = redissonClient.getLock(key);
        try {
            boolean lock = rLock.tryLock(10, 20, TimeUnit.SECONDS);
            System.out.println("lock: " + lock);
            if (lock) {
                //业务
                Thread.sleep(1000 * 10);
            }
        } catch (Throwable e) {
            e.printStackTrace();
        } finally {
            if (rLock.isLocked() && rLock.isHeldByCurrentThread()) {
                rLock.unlock();
            }
            System.out.println("解锁");
        }
        return new HashMap<>();
    }

}
```

## 单测

```java
@SpringBootTest(classes = SpringbootRedissionApplication.class)
@RunWith(SpringRunner.class)
public class TestApplication {

    @Autowired
    ApplicationContext context;

    // redisson客户端
    @Autowired
    RedissonClient redissonClient;

    // 测试分布式锁
    @Test
    public void terst1() throws InterruptedException {
        RLock lock = redissonClient.getLock("anyLock");

        new Thread(() -> {
            lock.lock();

            try {
                System.out.println(Thread.currentThread().getName() + ":\t 获得锁");
                Thread.sleep(3000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            } finally {
                System.out.println(Thread.currentThread().getName() + ":\t 释放锁");
                lock.unlock();
            }
        }).start();


        new Thread(() -> {
            lock.lock();
            try {
                System.out.println(Thread.currentThread().getName() + ":\t 获得锁");
                Thread.sleep(3000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            } finally {
                System.out.println(Thread.currentThread().getName() + ":\t 释放锁");
                lock.unlock();
            }
        }).start();

        Thread.sleep(100000);
    }
}
```





















---

整理者：长路 时间：2024.12.18



