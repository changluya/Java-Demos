package com.changlu.concurrentDesign;

/**
 * 测试interrupt将阻塞线程唤醒
 */
public class Main {
    public static void main(String[] args) throws InterruptedException {
        Thread thread = new Thread(new Runnable() {
            public void run() {
                try {
                    System.out.println("开始执行...");
                    Thread.sleep(10000);
                } catch (InterruptedException e) {
                    System.out.println("中断结束...");
                    throw new RuntimeException(e);
                }
            }
        });
        thread.start();
        // 等待thread线程运行
        Thread.sleep(1000);
        thread.interrupt();// 令thread线程阻塞 -》 运行
    }
}
