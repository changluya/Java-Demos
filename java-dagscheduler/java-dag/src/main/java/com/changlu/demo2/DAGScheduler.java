package com.changlu.demo2;

import com.changlu.demo2.task.ShellTaskExecutor;
import com.changlu.demo2.task.TaskExecutor;

import java.util.*;

public class DAGScheduler {

    private DAG dag;
    // 存储任务类型 -> 任务执行器
    private Map<String, TaskExecutor> executors = new HashMap<>();

    public DAGScheduler(DAG dag) {
        this.dag = dag;
        // 注册任务执行器
        executors.put("Shell", new ShellTaskExecutor());
    }

    // 拓扑排序（检测依赖并生成执行顺序）
    public List<String> topologicalSort() {
        // dag图的顶点集合
        Map<String, List<String>> edges = dag.getEdges();

        // 入度map，目的为找到入度为0的顶点
        Map<String, Integer> inDegree = new HashMap<>();
        // 初始化入度
        for (String node : dag.getNodes().keySet()) {
            inDegree.put(node, 0);
        }
        // 入度累加
        for (List<String> deps : edges.values()) {
            for (String dep : deps) {
                inDegree.put(dep, inDegree.get(dep) + 1);
            }
        }

        // 底层实现原理：队列实现拓扑排序
        // 拓扑排序的起点
        Queue<String> queue = new LinkedList<>();
        // 入度为0的表示顶点，收集顶点到队列中
        for (String taskId : inDegree.keySet()) {
            if (inDegree.get(taskId) == 0) {
                queue.offer(taskId);
            }
        }
        // 成环校验：入度为0的情况 表示出现成环问题
        if (queue.isEmpty()) {
            throw new RuntimeException("出现成环问题，请及时排查解决！");
        }

        // 拓扑排序结果集
        List<String> result = new ArrayList<>();
        while (!queue.isEmpty()) {
            String taskId = queue.poll();
            result.add(taskId);
            // 处理当前节点所关联的依赖边
            for (String depTaskId : edges.get(taskId)) {
                // 依赖去重逻辑
                inDegree.put(depTaskId, inDegree.get(depTaskId) - 1);
                if (inDegree.get(depTaskId) == 0) {
                    queue.offer(depTaskId);
                }
            }
        }
        return result;
    }

    // 执行dag任务
    public void execute() {
        // 计算出dag图的拓扑排序执行路径
        List<String> orderTaskIds = this.topologicalSort();
        // 按照拓扑排序顺序的节点执行任务
        System.out.println("规划的拓扑排序顺序为：" + orderTaskIds);
        for (String taskId : orderTaskIds) {
            TaskNode taskNode = dag.getNodes().get(taskId);
            // 匹配对应的任务执行器
            TaskExecutor taskExecutor = executors.get(taskNode.getTaskType());
            if (taskExecutor != null) {
                taskExecutor.execute(taskNode);
            }else {
                System.out.println("No executor found for task type: " + taskNode.getTaskType());
                throw new RuntimeException(String.format("No executor found for task type: %s, taskId: %s", taskNode.getTaskType(), taskNode.getTaskId()));
            }
        }
    }

}
