package com.changlu.demo4;

import org.quartz.JobBuilder;
import org.quartz.JobDetail;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.Trigger;
import org.quartz.TriggerBuilder;
import org.quartz.impl.StdSchedulerFactory;

import static org.quartz.SimpleScheduleBuilder.simpleSchedule;

public class EmailScheduler {
    public static void main(String[] args) {
        try {
            // 1. 创建 Scheduler
            Scheduler scheduler = StdSchedulerFactory.getDefaultScheduler();

            // 2. 定义 JobDetail
            JobDetail job = JobBuilder.newJob(SendEmailJob.class)
                    .withIdentity("sendEmailJob", "emailGroup") // 设置 Job 的名称和组
                    .usingJobData("toEmail", "user@example.com") // 设置参数
                    .usingJobData("subject", "测试邮件")
                    .usingJobData("content", "这是一封测试邮件，请忽略。")
                    .build();

            // 3. 定义 Trigger
            Trigger trigger = TriggerBuilder.newTrigger()
                    .withIdentity("emailTrigger", "emailGroup") // 设置 Trigger 的名称和组
                    .startNow() // 立即启动
                    .withSchedule(simpleSchedule()
                            .withIntervalInSeconds(10) // 每隔 10 秒执行一次
                            .repeatForever()) // 无限重复
                    .build();

            // 4. 调度任务
            scheduler.scheduleJob(job, trigger);

            // 5. 启动 Scheduler
            scheduler.start();

            // 6. 主线程休眠 60 秒，观察任务执行
            Thread.sleep(60000);

            // 7. 关闭 Scheduler
            scheduler.shutdown();
        } catch (SchedulerException | InterruptedException e) {
            e.printStackTrace();
        }
    }
}