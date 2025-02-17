package com.changlu.demo2;

import com.changlu.dependency.DependencyUtil;
import com.cronutils.descriptor.CronDescriptor;
import com.cronutils.model.Cron;
import com.cronutils.model.CronType;
import com.cronutils.model.definition.CronDefinition;
import com.cronutils.model.definition.CronDefinitionBuilder;
import com.cronutils.parser.CronParser;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class CronDemo2 {

    private static final CronDefinition CRON_DEFINITION = CronDefinitionBuilder.instanceDefinitionFor(CronType.QUARTZ);

    public static void main(String[] args) throws ParseException {
        // 1、解析cron表达式并创建一个Cron对象
        String cron = "0 0 12 * * ?";
        // 定义输入时间：2023-10-05 15:00:00
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date = sdf.parse("2023-10-05 11:00:00");
        System.out.println("当前时间为：" + date + ",cron表达式为：" + cron);
        // 定义解析器
        CronParser parser = new CronParser(CRON_DEFINITION);
        Cron parentCron = parser.parse(cron);

        // 案例1：获取 cron 表达式的描述
        CronDescriptor descriptor = CronDescriptor.instance(Locale.getDefault());
        String description = descriptor.describe(parentCron);
        System.out.println("Cron expression description: " + description);

        // 案例2：获取 下游任务日期为date，上游任务cron表达式为parentCron  => 上一个执行时间、下一个执行时间
        // 其他格式化方式
//        nextDateTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))
        System.out.println("上一个执行时间：" + DependencyUtil.getPreExecution(date, parentCron));
        System.out.println("下一个执行时间：" + DependencyUtil.getNextExecution(date, parentCron));
    }

}