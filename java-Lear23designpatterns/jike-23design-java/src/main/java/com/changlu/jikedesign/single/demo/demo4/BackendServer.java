package com.changlu.jikedesign.single.demo.demo4;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

/**
 * 多例模式实现
 */
public class BackendServer {

    private long serverNo;
    private String serverAddress;

    private static final int SERVER_COUNT = 3;
    private static final Map<Long, BackendServer> serverInstances = new HashMap<>(3);
    // 饿汉式创建服务实例
    static {
        serverInstances.put(1L, new BackendServer(1L, "192.134.172.222:8080"));
        serverInstances.put(2L, new BackendServer(2L, "192.134.172.223:8080"));
        serverInstances.put(3L, new BackendServer(3L, "192.134.172.224:8080"));
    }

    private BackendServer(long serverNo, String serverAddress) {
        this.serverNo = serverNo;
        this.serverAddress = serverAddress;
    }

    public static BackendServer getInstance(long serverNo) {
        return serverInstances.get(serverNo);
    }

    public static BackendServer getRandomServer() {
        Random r = new Random();
        int no = r.nextInt(SERVER_COUNT) + 1;
        return serverInstances.get(no);
    }

    public static void main(String[] args) {
        System.out.println(BackendServer.getInstance(1));
        System.out.println(BackendServer.getInstance(2));
        System.out.println(BackendServer.getInstance(3));
    }

}
