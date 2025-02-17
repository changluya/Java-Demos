package com.changlu.demo1;

import java.util.Stack;

/**
 * 该类负责对有向图进行拓扑排序。
 */
public class TopologicalSort {

    private Graph graph;
    private int startNode;

    /**
     * 构造函数，初始化拓扑排序。
     *
     * @param graph 要进行拓扑排序的图
     * @param startNode 拓扑排序的起始节点
     */
    public TopologicalSort(Graph graph, int startNode) {
        this.graph = graph;
        this.startNode = startNode;
    }

    /**
     * 拓扑排序的递归辅助函数。
     *
     * @param v 当前顶点
     * @param visited 记录顶点是否被访问过的数组
     * @param stack 用于存储拓扑排序结果的栈（递归到最后一个任务，依次入栈）
     */
    private void topologicalSortUtil(int v, boolean visited[], Stack<Integer> stack) {
        // 标记当前节点为已访问
        visited[v] = true;

        // 递归访问所有邻接节点
        for (Integer i : graph.getAdj()[v]) {
            if (!visited[i])
                topologicalSortUtil(i, visited, stack);
        }

        // 将当前节点压入栈中
        stack.push(v);
    }

    /**
     * 执行拓扑排序，并打印排序结果。
     */
    public void topologicalSort() {
        Stack<Integer> stack = new Stack<>(); // 用于存储拓扑排序结果的栈
        boolean visited[] = new boolean[graph.getV()]; // 记录顶点是否被访问过的数组
        for (int i = 0; i < graph.getV(); i++)
            visited[i] = false; // 初始化所有顶点为未访问

        // 对每个未访问的节点调用递归辅助函数
        // 优先从指定的起始节点开始访问
        if (!visited[startNode])
            topologicalSortUtil(startNode, visited, stack);

        // 打印栈中的内容，即拓扑排序结果
        while (!stack.isEmpty())
            System.out.print(stack.pop() + " ");
    }
}