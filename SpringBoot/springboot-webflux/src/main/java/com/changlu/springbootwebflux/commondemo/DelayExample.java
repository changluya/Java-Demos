package com.changlu.springbootwebflux.commondemo;

import reactor.core.publisher.Flux;
import java.time.Duration;
import java.time.LocalTime;

public class DelayExample {
    public static void main(String[] args) throws InterruptedException {
        // 生成1、2、3三个ID的数据流
        Flux.range(1, 3)
                // 打印当前元素和处理时间（延迟前）
                .doOnNext(id -> System.out.println(
                    "准备处理ID=" + id + " | 时间=" + LocalTime.now() + " | 线程=" + Thread.currentThread().getName()
                ))
                // 每个元素延迟1秒
                .delayElements(Duration.ofSeconds(1))
                // 打印延迟后的元素和时间（延迟后）
                .doOnNext(id -> System.out.println(
                    "延迟后处理ID=" + id + " | 时间=" + LocalTime.now() + " | 线程=" + Thread.currentThread().getName()
                ))
                // 订阅触发执行
                .subscribe();

        // 主线程等待5秒，避免程序提前结束
        Thread.sleep(5000);
    }
}
