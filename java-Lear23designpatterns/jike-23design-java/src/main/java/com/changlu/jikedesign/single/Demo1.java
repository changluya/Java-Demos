package com.changlu.jikedesign.single;

// 饿汉式
public class Demo1 {

    private static final Demo1 instance = new Demo1();

    private Demo1() {}

    public static Demo1 getInstance() {
        return instance;
    }

}
