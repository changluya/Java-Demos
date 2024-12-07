package com.changlu.jikedesign.single.demo.demo3;

/**
 * 解决方式1：先init，后getInstance
 */
public class Singleton {

    private static Singleton instance = null;
    private final int paramA;
    private final int paramB;

    private Singleton(int paramA, int paramB) {
        this.paramA = paramA;
        this.paramB = paramB;
    }

    // 不能够直接获取到instance，需要调用init之后才可以
    public static Singleton getInstance() {
        if (instance == null) {
            throw new RuntimeException("Run init() first.");
        }
        return instance;
    }

    // init初始化操作的时候进行单例的创建
    public synchronized static Singleton init(int paramA, int paramB) {
        if (instance != null) {
            throw new RuntimeException("Singleton has been created!");
        }
        instance = new Singleton(paramA, paramB);
        return instance;
    }

    public static void main(String[] args) {
//        System.out.println(Singleton.getInstance());
        Singleton.init(10, 50);
        System.out.println(Singleton.getInstance());
        System.out.println(Singleton.getInstance());
        System.out.println(Singleton.getInstance());
    }

}
