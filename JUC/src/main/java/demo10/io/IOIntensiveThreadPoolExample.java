package demo10.io;

import demo10.MyThreadFactory;
import demo10.RejectedExecutionHandlerFactory;
import demo10.TaskQueue;

import java.util.concurrent.*;

/**
 * io密集型场景任务提交
 * demo1：初次版本
 *  自定义队列：核心线程 -> 最大线程 -> 队列
 *  拒绝策略：队列满了的情况会使用当前submitjob的线程去执行任务（由于是main线程去submitjob，会导致main线程去跑任务，影响main线程的任务执行）
 *
 */
public class IOIntensiveThreadPoolExample {

    public static void main(String[] args) {
        // 获取 CPU 核心数
        int cpuCores = Runtime.getRuntime().availableProcessors();

        // 自定义线程池参数
        int corePoolSize = cpuCores * 2; // 核心线程数（IO 密集型任务可以设置较大）
        int maximumPoolSize = cpuCores * 4; // 最大线程数
        long keepAliveTime = 60L; // 空闲线程存活时间
        TimeUnit unit = TimeUnit.SECONDS; // 时间单位

        // 自定义任务队列
        TaskQueue<Runnable> taskQueue = new TaskQueue<>(corePoolSize * 2); // 队列容量为核心线程数的 2 倍

        // 创建自定义线程池
        ThreadPoolExecutor executor = new ThreadPoolExecutor(
                corePoolSize,
                maximumPoolSize,
                keepAliveTime,
                unit,
                taskQueue,
                new MyThreadFactory("IOIntensiveThreadPool"), // 默认线程工厂 Executors.defaultThreadFactory() | 自定义工厂支持自定义线程池名字
                RejectedExecutionHandlerFactory.newCallerRun("IOIntensiveThreadPool") // 拒绝策略：由提交任务的线程直接执行任务 main线程阻塞住（当已经最大核心线程数已到 & 队列已满场景）
        );

        // 将线程池对象设置到任务队列中
        taskQueue.setExecutor(executor);

        // 提交任务
        for (int i = 0; i < 1000; i++) {
            final int taskId = i;
            executor.submit(() -> {
                System.out.println(Thread.currentThread().getName() + " 正在执行任务 " + taskId + "...");
                try {
                    Thread.sleep(10000); // 模拟 IO 操作（如网络请求或文件读写）10s
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                System.out.println(Thread.currentThread().getName() + " 任务 " + taskId + " 完成！");
            });
        }

        System.out.println("所有任务提交结束！");

        // 关闭线程池
        executor.shutdown();
        try {
            // 等待所有任务完成
            if (!executor.awaitTermination(60, TimeUnit.SECONDS)) {
                executor.shutdownNow(); // 强制关闭
            }
        } catch (InterruptedException e) {
            executor.shutdownNow();
        }
    }
}