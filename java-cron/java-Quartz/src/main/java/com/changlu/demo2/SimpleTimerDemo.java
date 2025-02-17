package com.changlu.demo2;

import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

public class SimpleTimerDemo {
    public static void main(String[] args) {
        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                System.out.println("任务执行了，时间：" + new Date());
            }
        }, 0, 10000); // 延迟 0 毫秒，每隔 10 秒执行一次
    }
}