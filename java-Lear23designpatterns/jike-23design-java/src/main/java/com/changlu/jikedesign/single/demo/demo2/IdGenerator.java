package com.changlu.jikedesign.single.demo.demo2;

import java.util.concurrent.atomic.AtomicLong;

public class IdGenerator {

    // AtomicLong 是一个Java并发库中提供的一个原子变量类型
    // 将线程不安全需要加锁的复合操作封装为了线程安全的原子操作
    private AtomicLong id = new AtomicLong(0);

    // 饿汉式
    private static final IdGenerator instance = new IdGenerator();

    private IdGenerator() {}

    public static IdGenerator getInstance() {
        return instance;
    }

    public long getId() {
        return id.incrementAndGet();
    }

    public static void main(String[] args) {
        System.out.println(IdGenerator.getInstance().getId());
        System.out.println(IdGenerator.getInstance().getId());
        System.out.println(IdGenerator.getInstance().getId());
        System.out.println(IdGenerator.getInstance().getId());
        System.out.println(IdGenerator.getInstance().getId());
    }

}
