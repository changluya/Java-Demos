package demo11;

import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 线程池单线程场景（核心数、最大线程数1场景）
 * 可实现公平效果（无论是否设置公平参数）
 * 适合场景：任务按照submitjob去依次提交任务 & 去除线程池同样也可实现，可见下面处理方式
 */
public class SynchronousQueueFairAndNotFairPoolDemo2 {
    public static void main(String[] args) throws InterruptedException {
        ThreadPoolExecutor executor = new ThreadPoolExecutor(1, 1,
                60L, TimeUnit.SECONDS,
                new SynchronousQueue<Runnable>(), // 填写false、true在核心、最大线程数为1 1情况下效果一致
                new RejectedExecutionHandler() {
                    @Override
                    public void rejectedExecution(Runnable r, ThreadPoolExecutor executor) {
                        try {
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
        //设计提交任务
        new Thread(()->{
            // 可改为queue队列
            int i = 0;
            while (i < jobNum) {
                int finalI = i;
                // 处理方式一：submitjob的线程（非主线程）会进入到阻塞当中（保证按照顺序来执行）
                executor.submit(()->{
                    System.out.println("CPU执行任务：" + finalI + ", 计数 =>" + count.incrementAndGet());
                    countDownLatch.countDown();
                });
                // or 处理方式二：思考：是否如果原本就在新建线程中，是否无需使用线程池去submitjob提交？因为原本当前就是阻塞进行的
//                System.out.println("执行任务：" + finalI);

                i++;
            }
        }).start();

        // 7. 等待1000毫秒
        System.out.println("main主线程开始干活");
        countDownLatch.await();
        executor.shutdown();
        System.out.println("任务全部完成");
    }
}
