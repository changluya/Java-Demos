package com.changlu.springbootmybatis;

import com.changlu.springbootmybatis.service.ScheduleJobService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@SpringBootTest
class SpringbootMybatisApplicationTests {

    @Autowired
    private ScheduleJobService scheduleJobService;

    @Test
    void contextLoads() {
        List<String> jobIds = Arrays.asList("afasdfsaddsa1");
//        scheduleJobService.batchRestartScheduleJob(jobIds);
//        scheduleJobService.batchRestartScheduleJob2(jobIds);
        // 编程式
        scheduleJobService.batchRestartScheduleJob3(jobIds);
    }

    @Test
    public void currencyTest() throws InterruptedException {
        List<String> jobIds = new ArrayList<>();
        for (int i = 1; i <= 8000; i++) {
            jobIds.add("afasdfsaddsa" + i);
        }

        long startTime = System.currentTimeMillis(); // 获取开始时间
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                List<String> jobs = jobIds.subList(100, 6000);
                synchronized (ScheduleJobService.class) {
                    scheduleJobService.batchRestartScheduleJob3(jobs);
                }
                System.out.println("thread1 end");
            }
        });
        Thread thread2 = new Thread(new Runnable() {
            @Override
            public void run() {
                List<String> jobs = jobIds.subList(2000, 7000);
                synchronized (ScheduleJobService.class) {
                    scheduleJobService.batchRestartScheduleJob3(jobs);
                }
                System.out.println("thread2 end");
            }
        });
        thread.start();
        thread2.start();
        // 等待任务结束
        try {
            // 等待线程执行完毕
            thread.join();
            thread2.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        long endTime = System.currentTimeMillis(); // 获取结束时间
        long duration = endTime - startTime; // 计算持续时间
        System.out.println("Main thread ends. Total time: " + duration + " ms.");
    }

}
