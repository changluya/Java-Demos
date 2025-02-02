package demo10.cpu;

import demo10.MyThreadFactory;
import demo10.RejectedExecutionHandlerFactory;
import demo10.TaskQueue;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * cpu密集型场景任务提交
 *  自定义队列：核心线程 -> 最大线程 -> 队列
 *  自定义拒绝策略：自定义采用执行阻塞队列的put操作来实现任务阻塞入队，而非直接使用调用者线程来直接跑任务
 *  非影响主线程执行流程：批次1000个任务统一在一个线程中去进行处理，与主流程main线程隔离
 *
 */
@Slf4j
public class CPUThreadPoolExample {

    public static void main(String[] args) {
        // 获取 CPU 核心数
        int cpuCores = Runtime.getRuntime().availableProcessors();

        // 自定义线程池参数
        int corePoolSize = cpuCores + 1; // 核心线程数 cpu核心数+1
        int maximumPoolSize = corePoolSize; // 最大线程数 cpu核心数+1
        long keepAliveTime = 60L; // 空闲线程存活时间
        TimeUnit unit = TimeUnit.SECONDS; // 时间单位
        // 自定义任务队列 核心线程 -> 最大核心线程数 -> 队列
        TaskQueue<Runnable> taskQueue = new TaskQueue<>(500); // 队列容量为核心线程数的 2 倍
        // 创建自定义线程池
        ThreadPoolExecutor executor = new ThreadPoolExecutor(
                corePoolSize,
                maximumPoolSize,
                keepAliveTime,
                unit,
                taskQueue,
                new MyThreadFactory("IOIntensiveThreadPool"), // 默认线程工厂 Executors.defaultThreadFactory() | 自定义工厂支持自定义线程池名字
                RejectedExecutionHandlerFactory.blockCallerPolicy("IOIntensiveThreadPool")
        );
        // 将线程池对象设置到任务队列中
        taskQueue.setExecutor(executor);

        // 统计任务的执行数量
        int jobNums = 1000000;
        final AtomicInteger count = new AtomicInteger(0);

        // 记录任务开始时间
        long startTime = System.currentTimeMillis();
        // 单独开一个线程（后续可改为线程池 核心、最大就1个场景）去完成整个任务提交处理
        // 如果submitjob阻塞，仅仅只会影响该thread线程
        new Thread(() -> {
            CountDownLatch latch = new CountDownLatch(jobNums);
            // 模拟1000个任务 （可改造为queue队列形式去在这个线程中去消费）
            for (int i = 0; i < jobNums; i++) {
                final int taskId = i;
                executor.submit(() -> {
                    // CPU计算
                    int sum = 0;
                    for (int j = 0; j < 100000; j++) {
                        sum += j;
                    }
                    System.out.println(Thread.currentThread().getName() + " 任务 " + taskId + " 完成！sum = " + sum);
                    count.incrementAndGet(); // 原子操作，+1 并返回新值
                    latch.countDown();
                });
            }
            System.out.println("所有任务提交完成！");
            // 关闭线程池，等待任务全部执行完毕
            try {
                latch.await();
                System.out.println("所有任务执行结束！");
                // 记录任务结束时间
                long endTime = System.currentTimeMillis();
                // 计算任务执行时间
                long duration = endTime - startTime;
                System.out.println("任务执行总耗时: " + duration + " 毫秒");
            } catch (InterruptedException e) {
                e.printStackTrace();
                throw new RuntimeException(e);
            }finally {
                executor.shutdown();
            }
        }).start();

        try {
            // 等待所有任务完成
            if (!executor.awaitTermination(60, TimeUnit.SECONDS)) {
                executor.shutdownNow(); // 强制关闭
            }
            System.out.println("执行完任务数统计：" + count.get());
        } catch (InterruptedException e) {
            executor.shutdownNow();
        }
    }
}