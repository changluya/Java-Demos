package com.changlu.demo1;

/**
 * 主函数，用于测试拓扑排序。
 */
public class Main {

    public static void main(String args[]) {
        // 创建一个图
        Graph g = new Graph(10);
        // 1 -> 2,3
        g.addEdge(1, 2);
        g.addEdge(1, 3);
        // 2 -> 4,5
        g.addEdge(2, 4);
        g.addEdge(2, 5);
        // 3 -> 5,6
        g.addEdge(3, 5);
        g.addEdge(3, 6);
        // 4 -> 7
        g.addEdge(4, 7);
        // 5 -> 7
        g.addEdge(5, 7);
        // 6 -> 8
        g.addEdge(6,8);
        // 7 -> 9
        g.addEdge(7, 9);

        // 成环场景案例
//        g.addEdge(9, 1);
        // 添加完边之后，校验是否出现成环问题
        CycleDetector cycleDetector = new CycleDetector(g, 1);
        if (cycleDetector.hasCycle()) {
            System.out.println("dag出现成环问题！请重新编辑");
            return;
        }

        // 创建拓扑排序对象，并指定起始节点为1
        TopologicalSort ts = new TopologicalSort(g, 1);

        System.out.println("拓扑排序结果:");
        ts.topologicalSort(); // 执行拓扑排序并打印结果
    }
}