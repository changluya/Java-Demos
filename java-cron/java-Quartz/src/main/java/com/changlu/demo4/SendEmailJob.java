package com.changlu.demo4;

import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

public class SendEmailJob implements Job {
    @Override
    public void execute(JobExecutionContext context) throws JobExecutionException {
        // 从 JobDataMap 中获取参数
        String toEmail = context.getJobDetail().getJobDataMap().getString("toEmail");
        String subject = context.getJobDetail().getJobDataMap().getString("subject");
        String content = context.getJobDetail().getJobDataMap().getString("content");

        // 发送邮件（这里用打印日志模拟）
        System.out.println("发送邮件给：" + toEmail);
        System.out.println("邮件主题：" + subject);
        System.out.println("邮件内容：" + content);
    }
}