package com.changlu.concurrentDesign;

/**
 * 引出问题：补充一个标志位控制，是否可让线程停止
 */
public class Main2 {

    private static volatile boolean terminated = false;

    public static void main(String[] args) throws Exception{
        Thread thread = new Thread(new Runnable() {
            public void run() {
                while (!terminated) {
                    try {
                        System.out.println("开始执行...");
                        Thread.sleep(2000);
                    } catch (InterruptedException e) {
                        System.out.println("中断结束...");
                        Thread.currentThread().interrupt();
                        terminated = true;
                    }
                    System.out.println("开始执行业务!");
                    try {
                        //业务xxx
                        Thread.sleep(3000);
                    } catch (InterruptedException e) {

                    }
                }
            }
        });
        thread.start();
        // 等待thread线程运行
        Thread.sleep(5000);
        thread.interrupt();// 令thread线程阻塞 -》 运行
    }
}
