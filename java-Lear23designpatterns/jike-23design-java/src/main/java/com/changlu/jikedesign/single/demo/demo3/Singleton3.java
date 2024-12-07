package com.changlu.jikedesign.single.demo.demo3;

/**
 * 解决方式3：通过全局类获取参数
 */
public class Singleton3 {

    private int paramA;
    private int paramB;
    private Singleton3 instance = null;

    // 初始化的时候参数通过全局变量获取
    private Singleton3() {
        this.paramA = Config.PARAM_A;
        this.paramB = Config.PARAM_B;
    }

    // 类级别锁
    public synchronized Singleton3 getInstance() {
        if (instance == null) {
            instance = new Singleton3();
        }
        return instance;
    }

}
