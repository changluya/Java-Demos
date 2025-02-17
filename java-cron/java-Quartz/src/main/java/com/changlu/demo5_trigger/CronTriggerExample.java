package com.changlu.demo5_trigger;

import com.changlu.demo1.MyJob;
import org.quartz.*;
import org.quartz.impl.StdSchedulerFactory;
import org.quartz.impl.matchers.GroupMatcher;

import static org.quartz.TriggerBuilder.newTrigger;
import static org.quartz.JobBuilder.newJob;

public class CronTriggerExample {
    public static void main(String[] args) throws Exception {
        // 创建Scheduler实例
        Scheduler scheduler = StdSchedulerFactory.getDefaultScheduler();

        // 定义JobDetail
        JobDetail jobDetail = newJob(MyJob.class)
                .withIdentity("myJob", "group1")
                .build();

        // 创建CronTrigger
        CronTrigger cronTrigger = newTrigger()
                .withIdentity("myCronTrigger", "group1")
//                .startNow()
                .withSchedule(
                        CronScheduleBuilder.cronSchedule("*/10 * * * * ?") // 每10s执行一次
                                .withMisfireHandlingInstructionFireAndProceed() // 如果任务错过执行时间，则立即执行。
                )
                .build();

        // 将Job和Trigger绑定到Scheduler
        scheduler.start();
        scheduler.scheduleJob(jobDetail, cronTrigger);

        // 等待任务执行完成
        Thread.sleep(60000);

        // 关闭Scheduler
        scheduler.shutdown(true);
    }
}