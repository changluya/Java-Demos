package com.changlu.demo3;

import org.quartz.*;
import org.quartz.impl.StdSchedulerFactory;

import java.util.Date;

public class QuartzDatabaseStoreDemo {
    public static void main(String[] args) {
        try {
            // 1. 加载 quartz.properties 配置文件
            StdSchedulerFactory factory = new StdSchedulerFactory("quartz.properties");
            Scheduler scheduler = factory.getScheduler();

            // 2. 定义 JobKey
            JobKey jobKey = new JobKey("myJob", "group1");

            // 3. 检查 Job 是否已经存在
            if (scheduler.checkExists(jobKey)) {
                System.out.println("Job 已存在，删除旧 Job...");
                scheduler.deleteJob(jobKey); // 删除旧的 Job
            }

            // 4. 创建新的 Job
            JobDetail job = JobBuilder.newJob(MyJob.class)
                    .withIdentity(jobKey)
                    .build();

            // 5. 定义 Trigger
            Trigger trigger = TriggerBuilder.newTrigger()
                    .withIdentity("myTrigger", "group1")
                    .startNow()
                    .withSchedule(SimpleScheduleBuilder.simpleSchedule()
                            .withIntervalInSeconds(10) // 每 10 秒执行一次
                            .repeatForever()) // 无限重复
                    .build();

            // 6. 调度任务
            scheduler.scheduleJob(job, trigger);

            // 7. 启动调度器
            scheduler.start();

            // 8. 主线程休眠 60 秒，观察任务执行
            Thread.sleep(60000);

            // 9. 关闭调度器
            scheduler.shutdown();
        } catch (SchedulerException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    // 定义 Job 类
    public static class MyJob implements Job {
        @Override
        public void execute(JobExecutionContext context) {
            System.out.println("任务开始执行，时间：" + new Date());
            try {
                Thread.sleep(30 * 1000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            System.out.println("任务执行结束，时间：" + new Date());
        }
    }
}