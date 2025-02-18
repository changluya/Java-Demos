package com.changlu.demo1;

/**
 * 成环测试工具
 */
public class CycleDetector {

    private Graph graph;
    private int startNode;

    public CycleDetector(Graph graph, int startNode) {
        this.graph = graph;
        this.startNode = startNode;
    }

    /**
     * 是否出现成环情况
     * @return Y/N
     */
    public boolean hasCycle() {
        boolean[] visited = new boolean[graph.getV()]; // 标记节点是否被访问过
        boolean[] recursionStack = new boolean[graph.getV()]; // 标记节点是否在递归栈中
        if (!visited[startNode]) {
            if (hasCycleUtil(startNode, visited, recursionStack)) {
                return true;
            }
        }
        return false;
    }

    /**
     * 递归逻辑去判断是否出现成环
     * @param v 目标点
     * @param visited 是否真正访问过
     * @param recursionStack 递归栈（用于记录递归过程中，是否有出现重复的点，一旦某条路径访问结束后回退，相应的节点状态也会进行回退）
     * @return Y/N
     */
    private boolean hasCycleUtil(int v, boolean[] visited, boolean[] recursionStack) {
        visited[v] = true;
        // 单独设计一个recursionStack原因是因为，如果某个节点同时被上游两个节点依赖，不能单独只依靠visited来判断出现成环情况
        recursionStack[v] = true;
        // 获取到对应节点v的下层依赖
        for (int neighbor : graph.getAdj()[v]) {
            // 判断是否访问过
            if (!visited[neighbor]) { // 未访问过情况
                if (hasCycleUtil(neighbor, visited, recursionStack)) {
                    return true;
                }
            } else if (recursionStack[neighbor]) {
                // 真正出现成环问题情况
                System.out.printf("出现成环情况！成环点为：%s, 其上游依赖点为：%s \n", neighbor, v);
                return true;
            }
        }
        // 回退某个节点往下找依赖过程中的标识字段
        recursionStack[v] = false;
        return false;
    }

}
