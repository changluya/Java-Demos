package com.changlu.simple;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class TimeWheel {

    private final int slotNum; // 槽数
    private final long interval; // 每个槽的时间间隔（毫秒）
    private final List<List<Runnable>> slots; // 槽中的任务列表
    private int currentSlot; // 当前指针位置
    private final ScheduledExecutorService scheduler;// 定时调度器

    public TimeWheel(int slotNum, int interval) {
        this.slotNum = slotNum;
        this.interval = interval;
        // 初始化槽集合
        this.slots = new ArrayList<>(slotNum);
        for (int i = 0; i < slotNum; i++) {
            this.slots.add(new ArrayList<>());
        }
        this.currentSlot = 0;
        this.scheduler = Executors.newScheduledThreadPool(1);
        start();
    }

    // 启动时间轮
    public void start() {
        scheduler.scheduleAtFixedRate(() -> {
            // 当前槽的任务
            List<Runnable> tasks = slots.get(currentSlot);
            for (Runnable task : tasks) {
                task.run();
            }
            // 清空当前的任务
            tasks.clear();

            // 移动指针
            currentSlot = (currentSlot + 1) % slotNum;
        }, interval, interval, TimeUnit.MILLISECONDS);
    }

    /**
     * 添加任务
     * @param task 任务
     * @param delay 延时时间 ms
     */
    public void addTask(Runnable task, long delay) {
        if (delay <= 0) {
            task.run();
            return;
        }
        // 计算目标任务槽
        int targetSlot = (currentSlot + (int)(delay / interval) % slotNum);
        slots.get(targetSlot).add(task);
    }

    /**
     * 停止时间轮
     */
    public void stop() {
        scheduler.shutdown();
    }

    public static void main(String[] args) throws InterruptedException {
        // 创建一个时间轮，8 个槽，每个槽间隔 1 秒
        TimeWheel timeWheel = new TimeWheel(8, 1000);

        // 添加任务
        timeWheel.addTask(() -> System.out.println("Task 1 executed"), 3000); // 3 秒后执行
        timeWheel.addTask(() -> System.out.println("Task 2 executed"), 5000); // 5 秒后执行

        // 等待任务执行
        Thread.sleep(10000);

        // 停止时间轮
        timeWheel.stop();
    }

}
