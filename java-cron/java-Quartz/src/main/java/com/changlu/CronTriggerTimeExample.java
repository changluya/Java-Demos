package com.changlu;

import org.quartz.CronExpression;

import java.text.ParseException;
import java.util.Date;

public class CronTriggerTimeExample {

    public static void main(String[] args) {
        // Cron 表达式
        String cronExpression = "0 0 0 15,L * ?";

        try {
            // 解析 Cron 表达式
            CronExpression cron = new CronExpression(cronExpression);

            // 获取当前时间
            Date currentDate = new Date();

            // 计算下一次触发时间
            Date nextTriggerTime = cron.getNextValidTimeAfter(currentDate);

            // 输出结果
            System.out.println("当前时间: " + currentDate);
            System.out.println("下一次触发时间: " + nextTriggerTime);

            // 如果需要获取更多触发时间，可以循环计算
            System.out.println("\n未来 5 次触发时间：");
            for (int i = 0; i < 5; i++) {
                nextTriggerTime = cron.getNextValidTimeAfter(nextTriggerTime);
                System.out.println(nextTriggerTime);
            }

        } catch (ParseException e) {
            System.err.println("Cron 表达式解析失败: " + e.getMessage());
        }
    }
}