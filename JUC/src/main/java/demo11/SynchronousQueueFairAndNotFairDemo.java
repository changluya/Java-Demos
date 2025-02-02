package demo11;

import java.util.Arrays;
import java.util.concurrent.SynchronousQueue;

/**
 * 公平 & 非公平 案例测试
 * 设置初始化的SynchronousQueue参数即可
 */
public class SynchronousQueueFairAndNotFairDemo {
    public static void main(String[] args) throws InterruptedException {
        // 1. 创建SynchronousQueue队列，可设置是否公平
        SynchronousQueue<Integer> synchronousQueue = new SynchronousQueue<>(true);

        // 放置三个元素 放置过程中各自等待500ms
        for (int i = 0; i < 10; i++) {
            // 2. 启动一个线程，往队列中放1个元素
            int finalI = i;
            new Thread(() -> {
                try {
                    System.out.println(Thread.currentThread().getName() + "开始入队列" + finalI);
                    synchronousQueue.put(finalI);
//                    System.out.println(Thread.currentThread().getName() + " 入队列" + finalI + "成功");
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }).start();
            Thread.sleep(500);
        }
        // 3. 等待1000毫秒
//        Thread.sleep(1000L);

        // 取元素的时候各自间隔500ms
        int[] arr = new int[10];
        for (int i = 0; i < 10; i++) {
            // 4. 启动一个线程，往队列中放1个元素
            int finalI = i;
            new Thread(() -> {
                try {
                    arr[finalI] = synchronousQueue.take();
                    System.out.println(Thread.currentThread().getName() + " 出队列 " + arr[finalI]);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }).start();

            Thread.sleep(500);
        }
        System.out.println(Arrays.toString(arr));
        // 7. 等待1000毫秒
        Thread.sleep(1000L);
    }
}
