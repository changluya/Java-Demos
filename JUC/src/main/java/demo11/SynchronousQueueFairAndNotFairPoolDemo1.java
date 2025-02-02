package demo11;

import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 线程池多线程场景（核心数、最大线程数非1场景）
 * 无公平可言原因：线程池底层走的是offer操作，并非是put操作（可见案例2中的demo场景，可实现公平是走的put操作实现）
 * 适合场景：cpu密集型，在多线程场景通过线程池是无法实现按照submit的提交顺序去处理逻辑的（原因如上）。
 */
public class SynchronousQueueFairAndNotFairPoolDemo1 {
    public static void main(String[] args) throws InterruptedException {
        int cpuCores = Runtime.getRuntime().availableProcessors();
        ThreadPoolExecutor executor = new ThreadPoolExecutor(cpuCores + 1, cpuCores + 1,
                60L, TimeUnit.SECONDS,
                new SynchronousQueue<Runnable>(true),
                new RejectedExecutionHandler() {
                    @Override
                    public void rejectedExecution(Runnable r, ThreadPoolExecutor executor) {
                        try {
                            // 阻塞等待
                            executor.getQueue().put(r);
                        } catch (InterruptedException var4) {
                            throw new RejectedExecutionException("Unexpected InterruptedException", var4);
                        }
                    }
                });
        // 倒计数
        int jobNum = 100;
        CountDownLatch countDownLatch = new CountDownLatch(jobNum);
        final AtomicInteger count = new AtomicInteger(0);
        // 记录任务开始时间
        long startTime = System.currentTimeMillis();
        //设计提交任务
        new Thread(()->{
            // 可改为queue队列
            int i = 0;
            while (i < jobNum) {
                int finalI = i;
                // submitjob的线程（非主线程）会进入到阻塞当中（保证按照顺序来执行）
                // 线程池底层使用的是队列的offer、poll
                executor.submit(()->{
                    System.out.println("CPU执行任务：" + finalI + ", 计数 =>" + count.incrementAndGet());
                    countDownLatch.countDown();
                });

                i++;
            }
        }).start();

        System.out.println("main主线程开始干活");
        countDownLatch.await();
        executor.shutdown();
        System.out.println("任务全部完成");

        // 记录任务结束时间
        long endTime = System.currentTimeMillis();
        // 计算任务执行时间
        long duration = endTime - startTime;
        System.out.println("任务执行总耗时: " + duration + " 毫秒");
    }
}
