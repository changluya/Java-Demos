package com.changlu.springbootwebflux.demo01;

import reactor.core.publisher.Flux;

import java.util.ArrayList;
import java.util.List;

/**
 * @description  传统模式编程 & 响应式编程
 * @author changlu
 * @date 2025/8/12 19:13
 */
public class Demo01 {

    public static void main(String[] args) {
//        test_01();
        test_02();
    }

    // 传统编程方式
    public static void test_01() {
        // 1. 立即生成列表（直接执行）
        List<Integer> list = new ArrayList<>();
        for (int i = 1; i <= 5; i++) {
            list.add(i);
        }

        // 2. 立即计算平方（直接执行）
        List<Integer> squaredList = new ArrayList<>();
        for (int num : list) {
            squaredList.add(num * num);
        }

        // 3. 立即过滤（直接执行）
        List<Integer> filteredList = new ArrayList<>();
        for (int num : squaredList) {
            if (num > 10) {
                filteredList.add(num);
            }
        }

        // 4. 立即打印（直接执行）
        for (int num : filteredList) {
            System.out.println(num);
        }
    }

    // 命令方式编程
    public static void test_02() {
        // 创建一个包含 1 到 5 的 Flux
        Flux<Integer> flux = Flux.range(1, 5);

        // 对每个元素进行平方操作
        Flux<Integer> squaredFlux = flux.map(i -> i * i);

        // 过滤出大于 10 的元素
        Flux<Integer> filteredFlux = squaredFlux.filter(i -> i > 10);

        // 订阅并打印结果
        filteredFlux.subscribe(System.out::println);
    }

}
