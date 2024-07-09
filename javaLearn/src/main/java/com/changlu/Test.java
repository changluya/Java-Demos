package com.changlu;

/**
 * @Description:
        * @Author: changlu
        * @Date: 10:19 PM
        */

class Son {
    public Long num;

    public Son(){

    }
}
public class Test {
    public static void main(String[] args) {
//        System.out.println(Long.compare(id, 10));
        Son son = new Son();
        Long num = son.num;
        System.out.println(num);
//        System.out.println("10".equals(id.toString()));
    }
}
enum Student {
    CHANGLU;
}
