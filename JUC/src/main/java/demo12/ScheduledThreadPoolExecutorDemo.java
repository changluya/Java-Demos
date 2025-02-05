package demo12;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class ScheduledThreadPoolExecutorDemo {
    public static void main(String[] args) {
        test01();
//        test02();
    }

    // scheduleAtFixedRate 方法：第三个参数是period，表示每间隔period时间执行一次任务（如果period为2s，但是前一个任务为3s，此时会在前一个任务执行完后再执行）
    public static void test01() {
        ScheduledExecutorService executor = Executors.newScheduledThreadPool(2);
        executor.scheduleAtFixedRate(() -> {
            LocalDateTime startTime = LocalDateTime.now();
            System.out.println("Task started at: " + startTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")) + ", thread: " + Thread.currentThread().getName());
            try {
                Thread.sleep(3000); // 模拟任务耗时3秒
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            LocalDateTime endTime = LocalDateTime.now();
            System.out.println("Task finished at: " + endTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
        }, 0, 1, TimeUnit.SECONDS);
    }

    // scheduleWithFixedDelay：第三个参数为delay，指的是上一个任务执行完之后间隔delay来执行任务
    public static void test02() {
        ScheduledExecutorService executor = Executors.newScheduledThreadPool(1);
        executor.scheduleWithFixedDelay(() -> {
            LocalDateTime startTime = LocalDateTime.now();
            System.out.println("Task started at: " + startTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")) + ", thread: " + Thread.currentThread().getName());
            try {
                Thread.sleep(3000); // 模拟任务耗时3秒
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            LocalDateTime endTime = LocalDateTime.now();
            System.out.println("Task finished at: " + endTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
        }, 0, 2, TimeUnit.SECONDS);
    }
}
