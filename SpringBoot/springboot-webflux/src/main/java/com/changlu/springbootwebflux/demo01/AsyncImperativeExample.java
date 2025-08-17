package com.changlu.springbootwebflux.demo01;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

/**
 * @description  命令式实现
 * @author changlu
 * @date 2025/8/12 19:16
 */
public class AsyncImperativeExample {
    public static void main(String[] args) {
        // 模拟异步获取用户（用Future模拟异步）
        CompletableFuture<User> user1 = CompletableFuture.supplyAsync(() -> fetchUser(1));
        CompletableFuture<User> user2 = CompletableFuture.supplyAsync(() -> fetchUser(2));
        CompletableFuture<User> user3 = CompletableFuture.supplyAsync(() -> fetchUser(3));

        // 等待所有异步结果（阻塞！）
        List<User> users = new ArrayList<>();
        try {
            users.add(user1.get()); // 阻塞等待
            users.add(user2.get());
            users.add(user3.get());
        } catch (Exception e) {
            e.printStackTrace();
        }

        // 过滤+打印
        for (User user : users) {
            if (user.age >= 18) {
                System.out.println(user.name);
            }
        }
    }

    // 模拟网络请求获取用户
    static User fetchUser(int id) {
        try { Thread.sleep(1000); } catch (InterruptedException e) {} // 模拟延迟
        return new User(id, "User" + id, 15 + id); // 年龄：16,17,18
    }

    static class User {
        int id;
        String name;
        int age;
        // 构造器省略


        public User(int id, String name, int age) {
            this.id = id;
            this.name = name;
            this.age = age;
        }
    }
}