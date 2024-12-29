package com.changlu.redission.controller;

import com.changlu.redission.component.CLRedissionLock;
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
import java.util.concurrent.atomic.AtomicBoolean;

@RestController
@RequestMapping("/redission")
public class RedissionController {

    @Autowired
    private RedissonClient redissonClient;

    @Autowired
    private CLRedissionLock clRedissionLock;

    /**
     * 使用模板式开解锁 无任务参数
     * @param key
     * @return
     */
    @GetMapping("/key2/{key}")
    public Map<String, String> redission2(@PathVariable("key")String key) {
        AtomicBoolean flag = new AtomicBoolean(true);
        clRedissionLock.execWithLockWithWatchDog(key, 0, TimeUnit.SECONDS,
                ()-> {
                    System.out.println("进入业务方法...");
                    try {
                        // 业务方法20s
                        Thread.sleep(1000 * 20);
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                    System.out.println("结束业务方法...");
                },
                (e)-> {throw new RuntimeException("业务方法抛出异常！");},
                () -> {
//                    throw new RuntimeException("获取锁失败！");
                    flag.set(false);// 没有抢到锁
                },
                false);
        HashMap<String, String> result = new HashMap<>();
        result.put("flag", flag.toString());
        return result;
    }

    /**
     * 手动调用redissonClient方式
     * @param key
     * @return
     */
    @GetMapping("/key/{key}")
    public Map<String, String> redission(@PathVariable("key")String key) {
        RLock rLock = redissonClient.getLock(key);
        try {
            boolean lock = rLock.tryLock(0, -1, TimeUnit.SECONDS);
            System.out.println("lock: " + lock);
            if (lock) {
                //业务
                Thread.sleep(1000 * 3000);
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
