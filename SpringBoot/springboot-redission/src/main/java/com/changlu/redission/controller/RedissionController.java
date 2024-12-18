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
