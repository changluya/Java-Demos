package com.changlu.demo2;

import java.util.Date;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class SimpleScheduledExecutorDemo {
    public static void main(String[] args) {
        ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
        scheduler.scheduleAtFixedRate(() -> {
            System.out.println("任务执行了，时间：" + new Date());
        }, 0, 10, TimeUnit.SECONDS); // 延迟 0 秒，每隔 10 秒执行一次
    }
}