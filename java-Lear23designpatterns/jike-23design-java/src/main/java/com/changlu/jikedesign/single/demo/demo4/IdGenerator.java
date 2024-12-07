package com.changlu.jikedesign.single.demo.demo4;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

/**
 * 线程单例创建
 */
public class IdGenerator {

    private AtomicLong id = new AtomicLong(0);
    private static final ConcurrentHashMap<Long, IdGenerator> instances = new ConcurrentHashMap<>();

    private IdGenerator(){}

    // 实现线程间的单例
    public static IdGenerator getInstance() {
        long curThreadId = Thread.currentThread().getId();
        instances.putIfAbsent(curThreadId, new IdGenerator());
        return instances.get(curThreadId);
    }

    public long getId() {
        return id.incrementAndGet();
    }

    public static void main(String[] args) {

        new Thread(new Runnable() {
            @Override
            public void run() {
                System.out.println("thread1：" + IdGenerator.getInstance().getId());
                System.out.println("thread1：" + IdGenerator.getInstance().getId());
            }
        }).start();
        new Thread(new Runnable() {
            @Override
            public void run() {
                System.out.println("thread2：" + IdGenerator.getInstance().getId());
                System.out.println("thread2：" + IdGenerator.getInstance().getId());
            }
        }).start();
    }

}
