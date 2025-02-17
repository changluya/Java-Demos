package com.changlu.demo2;

import org.quartz.*;
import org.quartz.impl.StdSchedulerFactory;

import java.util.Date;

public class QuartzMemoryStoreDemo {
    public static void main(String[] args) {
        try {
            // 1. 加载 quartz.properties 配置文件
            StdSchedulerFactory factory = new StdSchedulerFactory("quartz.properties");
            Scheduler scheduler = factory.getScheduler();

            // 2. 定义 Job
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

            // 4. 调度任务
            scheduler.scheduleJob(job, trigger);

            // 5. 启动调度器
            scheduler.start();

            // 6. 主线程休眠 60 秒，观察任务执行
            Thread.sleep(60000);

            // 7. 关闭调度器
            scheduler.shutdown();
        } catch (SchedulerException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    // 定义 Job 类
    public static class MyJob implements Job {
        @Override
        public void execute(JobExecutionContext context) {
            System.out.println("任务执行了，时间：" + new Date());
        }
    }
}