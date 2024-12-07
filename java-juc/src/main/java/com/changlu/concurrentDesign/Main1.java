package com.changlu.concurrentDesign;

/**
 * 引出问题：while循环中任务如何停止，此时会出现停不下来情况
 */
public class Main1 {
    public static void main(String[] args) throws Exception{
        Thread thread = new Thread(new Runnable() {
            public void run() {
                while (true) {
                    try {
                        System.out.println("开始执行...");
                        Thread.sleep(10000);
                    } catch (InterruptedException e) {
                        System.out.println("中断结束...");
                    }
                }
            }
        });
        thread.start();
        // 等待thread线程运行
        Thread.sleep(2000);
        thread.interrupt();// 令thread线程阻塞 -》 运行
    }
}
