package com.changlu.demo2.task;

import com.changlu.demo2.TaskNode;

// 示例：Shell 任务执行器
public class ShellTaskExecutor implements TaskExecutor {
    @Override
    public void execute(TaskNode taskNode) {
        System.out.println("Executing Shell Task: " + taskNode.getTaskName());
        // 这里可以调用真实的 Shell 脚本
    }
}