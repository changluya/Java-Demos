package com.changlu.jikedesign.single;

// 双重检测
public class Demo3 {

    // 防止指令重排
    private volatile static Demo3 instance;

    private Demo3(){}

    public static Demo3 getInstance() {
        if (instance == null) {
            synchronized (Demo3.class) { // 类级别的锁
                if (instance == null) {
                    instance = new Demo3();
                }
            }
        }
        return instance;
    }

    public static void main(String[] args) {
        System.out.println(Demo3.getInstance());
    }

}
