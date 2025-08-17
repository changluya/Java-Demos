package com.changlu.springbootwebflux.demo01;

import reactor.core.publisher.Flux;
import java.time.Duration;

public class AsyncReactiveExample {
    public static void main(String[] args) throws InterruptedException {
        // 1. 生成异步数据流（3个用户，每个延迟1秒）
        Flux<User> userFlux = Flux.range(1, 3)
                .delayElements(Duration.ofSeconds(1)) // 每个元素异步延迟1秒（非阻塞）
                .map(id -> fetchUser(id)); // 异步获取用户

        // 2. 过滤年龄>18的用户（仅定义规则）
        Flux<User> adultFlux = userFlux.filter(user -> user.age >= 18);

        // 3. 订阅时触发整个流程（非阻塞）
        adultFlux.subscribe(user -> System.out.println(user.name));

        // 主线程等待（避免程序提前退出）
        Thread.sleep(4000);
    }

    // 同上：模拟网络请求获取用户
    static User fetchUser(int id) {
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