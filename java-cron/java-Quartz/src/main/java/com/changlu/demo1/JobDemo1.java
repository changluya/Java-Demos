package com.changlu.demo1;

import org.quartz.*;
import org.quartz.impl.StdSchedulerFactory;

public class JobDemo1 {

    public static void main(String[] args) throws SchedulerException {
        // 1、创建 Scheduler
        Scheduler scheduler = StdSchedulerFactory.getDefaultScheduler();

        // 2. 定义 JobDetail
        JobDetail job = JobBuilder.newJob(MyJob.class)
                .withIdentity("myJob", "group1")
                .build();

        // 3. 定义 Trigger
        Trigger trigger = TriggerBuilder.newTrigger()
                .withIdentity("myTrigger", "group1")
                .startNow()
                .withSchedule(SimpleScheduleBuilder.simpleSchedule()
                        .withIntervalInSeconds(10) // 每 10 秒执行一次
                        .repeatForever()) // 无限重复
                .build();

        // 4、调度任务
        scheduler.scheduleJob(job, trigger);

        // 5. 启动 Scheduler【异步执行】
        scheduler.start();

        // 上面去启动任务，不会影响主线程执行
        System.out.println("main主线程任务执行...");
    }

}
