package com.changlu.demo1;

import com.cronutils.model.Cron;
import com.cronutils.model.CronType;
import com.cronutils.model.definition.CronDefinition;
import com.cronutils.model.definition.CronDefinitionBuilder;
import com.cronutils.model.time.ExecutionTime;
import com.cronutils.parser.CronParser;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Date;

public class CronDemo1 {

    private static final CronDefinition CRON_DEFINITION = CronDefinitionBuilder.instanceDefinitionFor(CronType.QUARTZ);

    public static void main(String[] args) throws ParseException {
        // 1、解析cron表达式并创建一个Cron对象
        String cron = "0 0 12 * * ?";
        // 定义输入时间：2023-10-05 15:00:00
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date = sdf.parse("2023-10-05 15:00:00");
        System.out.println("当前时间为：" + date + ",cron表达式为：" + cron);
        // 定义解析器
        CronParser parser = new CronParser(CRON_DEFINITION);
        Cron quartzCron = parser.parse(cron);

        // 2、计算指定日期（date）的上一个、下一个执行时间
        // 使用给定的Quartz Cron表达式创建一个ExecutionTime对象
        // ExecutionTime是CronUtils库中的一个类，用于计算Cron表达式的执行时间
        ExecutionTime executionTime = ExecutionTime.forCron(quartzCron);
        // 参数：
        //  1、转换为ZonedDateTime，假设 date 是 2023-10-05T15:30:00，转换为 ZonedDateTime 后为 2023-10-05T15:30:00+08:00。
        //  2、Instant 是 Java 8 引入的 java.time 包中的一个类，用于表示时间线上的一个瞬时点。
        // 最近执行时间lastExecution，executionTime本身带有cron表达式,lastExecution获取最近执行时间
        // ZoneId.systemDefault()：系统的默认时区
        // 基于date的上一次执行时间：lastExecution
        ZonedDateTime lastDateTime = executionTime.lastExecution(ZonedDateTime.ofInstant(date.toInstant(), ZoneId.systemDefault())).orElse(null);
        // 基于date的下一次执行时间：nextExecution
        ZonedDateTime nextDateTime = executionTime.nextExecution(ZonedDateTime.ofInstant(date.toInstant(), ZoneId.systemDefault())).orElse(null);

        if (lastDateTime == null || nextDateTime == null) {
            return;
        }
        // ZonedDateTime 是 Java 8 引入的 java.time 包中的一个类，表示一个带有时区的日期时间。
        Date firstExecuteTime = Date.from(lastDateTime.toInstant());
        Date secondExecuteTime = Date.from(nextDateTime.toInstant());
        // 其他格式化方式
//        nextDateTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))
        System.out.println("上一个执行时间：" + firstExecuteTime);
        System.out.println("下一个执行时间：" + secondExecuteTime);
    }
}