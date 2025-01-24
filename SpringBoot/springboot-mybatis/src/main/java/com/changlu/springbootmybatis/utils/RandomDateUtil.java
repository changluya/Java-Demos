package com.changlu.springbootmybatis.utils;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Random;

public class RandomDateUtil {

    private static final Random random = new Random();

    /**
     * 生成随机日期时间字符串，格式为 yyyyMMddHHmmss
     * 生成的日期时间在传入参数的日期时间的前一年范围内
     *
     * @param endDateTime 截止日期时间（包含）
     * @return 随机日期时间字符串
     */
    public static String generateRandomDateTime(String endDateTime) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");
        LocalDateTime end = LocalDateTime.parse(endDateTime, formatter);

        // 计算一年前的日期时间
        LocalDateTime start = end.minusYears(1);

        // 生成随机的秒数（从一年前到截止日期）
        long startSeconds = start.toEpochSecond(java.time.ZoneOffset.UTC);
        long endSeconds = end.toEpochSecond(java.time.ZoneOffset.UTC);
        long randomSeconds = startSeconds + (long) (random.nextDouble() * (endSeconds - startSeconds));

        LocalDateTime randomDate = LocalDateTime.ofEpochSecond(randomSeconds, 0, java.time.ZoneOffset.UTC);
        return randomDate.format(formatter);
    }

    public static void main(String[] args) {
        System.out.println(RandomDateUtil.generateRandomDateTime("20250114000000"));
    }
}