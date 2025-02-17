package com.changlu.dependency;

import com.cronutils.model.Cron;
import com.cronutils.model.time.ExecutionTime;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Date;

/**
 * 时间依赖工具类
 */
public class DependencyUtil {

    // 获取基于childDate所依赖的parentCron上一个的时间（含等于）
    public static Date getPreExecution(Date childDate, Cron parentCron) {
        return getExecution(childDate, parentCron, true);
    }

    // 获取基于childDate所依赖的parentCron下一个的时间
    public static Date getNextExecution(Date childDate, Cron parentCron) {
        return getExecution(childDate, parentCron, false);
    }

    /**
     * 获取针对子任务为childDate日期的父任务parentCron表达式的上一个时间（含等于） or 下一个时间
     * @param childDate 子任务执行时间
     * @param parentCron 父任务的cron表达式
     * @param isPre 是否计算上一个时间
     * @return 匹配父任务的上一个时间（含等于） or 下一个时间
     */
    private static Date getExecution(Date childDate, Cron parentCron, boolean isPre) {
        // 1、解析父cron的ExecutionTime
        // 使用给定的Quartz Cron表达式创建一个ExecutionTime对象
        // ExecutionTime是CronUtils库中的一个类，用于计算Cron表达式的执行时间
        ExecutionTime parentExecuteTime = ExecutionTime.forCron(parentCron);

        // 2、计算获取基于childDate的前一个时间 or 后一个时间
        // ZonedDateTime.ofInstant参数：
        //  1、转换为ZonedDateTime，假设 date 是 2023-10-05T15:30:00，转换为 ZonedDateTime 后为 2023-10-05T15:30:00+08:00。
        //  2、Instant 是 Java 8 引入的 java.time 包中的一个类，用于表示时间线上的一个瞬时点。 【ZoneId.systemDefault()：系统的默认时区】
        ZonedDateTime childZonedDateTime = ZonedDateTime
                .ofInstant(childDate.toInstant(), ZoneId.systemDefault());
        ZonedDateTime executionDate = null;
        // 基于date的上一次执行时间（包含=情况）：lastExecution
        // 举例：最靠近的一个时间点，如子任务时间为2023-10-05 12:00:00，父任务cron为 "0 0 12 * * ?"，得到的依赖父任务时间为2023-10-05 12:00:00
        if (isPre) {
            // 子任务当前时间是否满足父任务cron表达式，满足直接作为所依赖的时间
            if (parentExecuteTime.isMatch(childZonedDateTime)) {
                executionDate = childZonedDateTime;
            }else {
                // 基于date的上一次执行时间：lastExecution
                executionDate = parentExecuteTime
                        .lastExecution(childZonedDateTime)
                        .orElse(null);
            }
        }else {
            // 基于date的下一次执行时间：nextExecution
            executionDate = parentExecuteTime
                    .nextExecution(childZonedDateTime)
                    .orElse(null);
        }
        if (executionDate == null) {
            return null;
        }
        // ZonedDateTime 是 Java 8 引入的 java.time 包中的一个类，表示一个带有时区的日期时间。
        return Date.from(executionDate.toInstant());
    }


}
