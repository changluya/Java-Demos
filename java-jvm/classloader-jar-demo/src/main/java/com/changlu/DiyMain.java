package com.changlu;

public class DiyMain {

    static {
        System.out.println("classloader-jar-demo DiyMain class static 初始化！");
    }

    public DiyMain() {
        System.out.println("classloader-jar-demo 构造器初始化");
    }

    public static void main(String[] args) {
        System.out.println("123456");
    }

}
