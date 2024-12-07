package com.changlu.jikedesign.single;

public class Demo4 {

    private Demo4(){}

    // 静态内部类
    public static class SingleHolder {
        private static final Demo4 instance = new Demo4();
    }

    // 获取到实例
    public static Demo4 getInstance() {
        return SingleHolder.instance;
    }

    public static void main(String[] args) {
        System.out.println(Demo4.getInstance());
    }

}
