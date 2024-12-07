package com.changlu.jikedesign.single.demo.demo4;

import java.util.concurrent.ConcurrentHashMap;

/**
 * 多实例案例
 */
public class Logger {

    // 并发容器
    private static final ConcurrentHashMap<String, Logger> instances = new ConcurrentHashMap<>();

    private Logger(){}

    // 根据loggerName来区分不同的Logger实例
    public static Logger getInstance(String loggerName) {
        instances.putIfAbsent(loggerName, new Logger());
        return instances.get(loggerName);
    }

    public void log() {
    }

    public static void main(String[] args) {
        System.out.println(Logger.getInstance("User.class"));
        System.out.println(Logger.getInstance("User.class"));
        System.out.println(Logger.getInstance("Car.class"));
        System.out.println(Logger.getInstance("Student.class"));
    }

}
