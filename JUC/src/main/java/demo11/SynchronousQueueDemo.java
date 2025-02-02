package demo11;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.SynchronousQueue;

/**
 * @apiNote SynchronousQueue示例
 **/
public class SynchronousQueueDemo {
    public static void main(String[] args) throws InterruptedException {
        // 1. 创建SynchronousQueue队列
        BlockingQueue<Integer> synchronousQueue = new SynchronousQueue<>();

        // 2. 启动一个线程，往队列中放3个元素
        new Thread(() -> {
            try {
                System.out.println(Thread.currentThread().getName() + " 入队列 1");
                synchronousQueue.put(1);
                Thread.sleep(1);
                System.out.println(Thread.currentThread().getName() + " 入队列 2");
                synchronousQueue.put(2);
                Thread.sleep(1);
                System.out.println(Thread.currentThread().getName() + " 入队列 3");
                synchronousQueue.put(3);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }).start();

        // 3. 等待1000毫秒
        Thread.sleep(1000L);

        // 4. 再启动一个线程，从队列中取出3个元素
        new Thread(() -> {
            try {
                System.out.println(Thread.currentThread().getName() + " 出队列 " + synchronousQueue.take());
                Thread.sleep(1);
                System.out.println(Thread.currentThread().getName() + " 出队列 " + synchronousQueue.take());
                Thread.sleep(1);
                System.out.println(Thread.currentThread().getName() + " 出队列 " + synchronousQueue.take());
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }).start();
    }

}