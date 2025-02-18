package com.changlu.demo2;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * dag：有向无环图
 * 模拟数组+链表  => map+集合
 */
public class DAG {

    private Map<String, TaskNode> nodes = new HashMap<>();// 节点集合 模拟的节点数组
    private Map<String, List<String>> edges = new HashMap<>(); // 边集合（任务依赖关系） 模拟的数组+链表

    /**
     * 添加节点
     * @param taskId 任务id
     * @param taskNode 任务节点
     */
    public void addNode(String taskId, TaskNode taskNode) {
        nodes.put(taskId, taskNode);
        edges.put(taskId, new ArrayList<>());
    }

    /**
     * 添加边
     * @param fromTaskId 来源任务id
     * @param toTaskId 目标任务id
     */
    public void addEdge(String fromTaskId, String toTaskId) {
        edges.get(fromTaskId).add(toTaskId);
    }

    // 获取所有节点
    public Map<String, TaskNode> getNodes() {
        return nodes;
    }

    // 获取所有边
    public Map<String, List<String>> getEdges() {
        return edges;
    }

}
