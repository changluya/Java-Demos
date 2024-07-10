package com.changlu;

import java.sql.Timestamp;

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
        long tt = System.currentTimeMillis();
        Timestamp timestamp = new Timestamp(tt);
        System.out.println(timestamp.getTime());
        System.out.println(tt);
    }
}
enum Student {
    CHANGLU;
}
