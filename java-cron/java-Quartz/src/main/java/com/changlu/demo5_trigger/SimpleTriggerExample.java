package com.changlu.demo5_trigger;

import com.changlu.demo1.MyJob;
import org.quartz.*;
import org.quartz.impl.StdSchedulerFactory;
import org.quartz.impl.matchers.GroupMatcher;

import static org.quartz.TriggerBuilder.newTrigger;
import static org.quartz.JobBuilder.newJob;

public class SimpleTriggerExample {
    public static void main(String[] args) throws SchedulerException, InterruptedException {
        // 创建Scheduler实例
        Scheduler scheduler = StdSchedulerFactory.getDefaultScheduler();

        // 定义JobDetail
        JobDetail jobDetail = newJob(MyJob.class)
                .withIdentity("myJob", "group1")
                .build();

        // 创建SimpleTrigger
        SimpleTrigger simpleTrigger = newTrigger()
                .withIdentity("myTrigger", "group1")
                .startNow() // 立即开始
                .withSchedule(SimpleScheduleBuilder.simpleSchedule()
                        .withIntervalInSeconds(10) // 每10秒执行一次
                        .withRepeatCount(5)) // 重复5次
                .build();

        // 将Job和Trigger绑定到Scheduler
        scheduler.start();
        scheduler.scheduleJob(jobDetail, simpleTrigger);

        // 等待任务执行完成
        Thread.sleep(60000);

        // 关闭Scheduler
        scheduler.shutdown(true);
    }
}