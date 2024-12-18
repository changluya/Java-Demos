package com.changlu.test;

import com.changlu.redission.SpringbootRedissionApplication;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.test.context.junit4.SpringRunner;

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