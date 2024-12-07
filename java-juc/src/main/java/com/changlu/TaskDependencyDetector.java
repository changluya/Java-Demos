package com.changlu;

import cn.hutool.crypto.digest.DigestUtil;
import com.google.common.base.Joiner;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

public class TaskDependencyDetector extends ArrayList<Set<String>> {
    private Set<String> keySet = new HashSet<>(); // 存储任务集合的MD5值

    // 压栈方法，用于检测循环依赖
    public Boolean push(Set<String> jobKeys) {
        // 将任务键连接成一个字符串
        String key = Joiner.on("").join(jobKeys);
        // 计算字符串的MD5值
        String md5String = DigestUtil.md5Hex(key); // 这里简化使用hashCode代替MD5

        // 检查是否已经处理过这个任务集合
        if (!keySet.contains(md5String)) {
            // 如果没有处理过，添加到集合中
            keySet.add(md5String);
            // 假设add方法将任务集合添加到某个队列或栈中
            return this.add(new HashSet<>(jobKeys));
        } else {
            // 如果已经处理过，返回false
            return Boolean.FALSE;
        }
    }

    public static void main(String[] args) {
        TaskDependencyDetector detector = new TaskDependencyDetector();

        // 尝试添加任务A的依赖
        Set<String> dependenciesA = new HashSet<>();
        dependenciesA.add("B");
        System.out.println("Can add A's dependencies? " + detector.push(dependenciesA));

        // 尝试添加任务B的依赖
        Set<String> dependenciesB = new HashSet<>();
        dependenciesB.add("B");
        System.out.println("Can add B's dependencies? " + detector.push(dependenciesB));

        // 尝试添加任务C的依赖，这将尝试形成一个循环依赖
        Set<String> dependenciesC = new HashSet<>();
        dependenciesC.add("A");
        System.out.println("Can add C's dependencies? " + detector.push(dependenciesC));
    }
}