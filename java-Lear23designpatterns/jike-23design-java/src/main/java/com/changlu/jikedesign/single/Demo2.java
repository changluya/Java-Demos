package com.changlu.jikedesign.single;

// 懒汉式
public class Demo2 {

    private static Demo2 instance;

    private Demo2(){}

    // class作为锁
    public static synchronized Demo2 getInstance() {
        if (instance == null) {
            instance = new Demo2();
        }
        return instance;
    }

    public static void main(String[] args) {
        System.out.println(Demo2.getInstance());
    }

}
