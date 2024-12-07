package com.changlu.jikedesign.single.demo.demo4;

import java.util.concurrent.atomic.AtomicLong;

/**
 * 需求：为了保证任何时刻，在进程间都只有一份对象存在，一个进程在获取到对象之后，需要对对
 * 象加锁，避免其他进程再将其获取。在进程使用完这个对象之后，还需要显式地将对象从内
 * 存中删除，并且释放对对象的加锁。
 */
public class IdGenerator2 {

    private AtomicLong id = new AtomicLong(0);
    private static IdGenerator2 instance;
    // 全局锁
    private static DistributedLock lock = new DistributedLock();
    // db存储器
    private static SharedObjectStorage storage = SharedObjectStorage.getInstance();

    private IdGenerator2() {}

    // 获取唯一实例
    // 1、自己本身保证上的是类级锁  2、真正创建时进行全局上锁
    public synchronized static IdGenerator2 getInstance() {
        if (instance == null) {
            // 全局上锁
            lock.lock();
            instance = storage.load(IdGenerator.class);
        }
        return instance;
    }

    // 释放资源
    // 1、本身当前为对象级别锁（已经是唯一了就可以使用对象级别的） 2、操作任务结束后，可以释放掉全局锁
    public synchronized void freeInstance() {
        storage.save(this, IdGenerator2.class);// 保存数据
        instance = null;// 初始化设置为空
        // 全局解锁
        lock.unlock();
    }

    public long getId() {
        return id.incrementAndGet();
    }

    // 静态内部类单独设计
    public static class SingleHolder {
        private static final IdGenerator2 instance = new IdGenerator2();
        public static IdGenerator2 getInstance() {
            return instance;
        }
    }

    public static void main(String[] args) {
        // 多集群场景下，仅仅只有一个实例存在一台服务器中（同一时间）
        // 获取资源
        IdGenerator2 instance = IdGenerator2.getInstance();
        System.out.println(instance.getId());
        // 释放资源
        instance.freeInstance();
    }

}

// 模拟redis操作类（分布式锁）
class DistributedLock {
    public void lock(){}

    public void unlock(){}
}

// 模拟db操作类
class SharedObjectStorage {
    private static SharedObjectStorage instance = new SharedObjectStorage();
    private SharedObjectStorage(){}
    public static SharedObjectStorage getInstance() {
        return instance;
    }

    // 模拟方法，实际是真实创建IdGenerator2对象
    public IdGenerator2 load(Class clazz) {
        return IdGenerator2.SingleHolder.getInstance();
    }

    // 模拟方法，保存方法
    public void save(IdGenerator2 instance, Class clazz) {
    }
}