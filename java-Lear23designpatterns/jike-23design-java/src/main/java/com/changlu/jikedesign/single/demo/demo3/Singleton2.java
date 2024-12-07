package com.changlu.jikedesign.single.demo.demo3;

/**
 * 解决方式二：getInstance传参，但是可能出现误解
 */
public class Singleton2 {

    private static Singleton2 instance = null;
    private final int paramA;
    private final int paramB;

    private Singleton2(int paramA, int paramB) {
        this.paramA = paramA;
        this.paramB = paramB;
    }

    // 类级别锁
    public synchronized static Singleton2 getInstance(int paramA, int paramB) {
        if (instance == null) {
            instance = new Singleton2(paramA, paramB);
        }
        return instance;
    }

    public static void main(String[] args) {
        // 可能会造成误解，传参数不同，但是创建的对象是同一个
        System.out.println(Singleton2.getInstance(10, 20));
        System.out.println(Singleton2.getInstance(20, 30));
    }

}
