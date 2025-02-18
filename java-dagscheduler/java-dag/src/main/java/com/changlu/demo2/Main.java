package com.changlu.demo2;

public class Main {

    public static void main(String[] args) {
        // 创建 DAG
        DAG dag = new DAG();

        // 添加任务节点
        TaskNode task1 = new TaskNode("a-1", "Shell Task a-1", "Shell");
        TaskNode task2 = new TaskNode("b-2", "Shell Task b-2", "Shell");
        TaskNode task3 = new TaskNode("c-3", "Shell Task c-3", "Shell");
        TaskNode task4 = new TaskNode("d-4", "Shell Task d-4", "Shell");
        TaskNode task5 = new TaskNode("e-5", "Shell Task e-5", "Shell");
        TaskNode task6 = new TaskNode("f-6", "Shell Task f-6", "Shell");
        TaskNode task7 = new TaskNode("g-7", "Shell Task g-7", "Shell");
        TaskNode task8 = new TaskNode("h-8", "Shell Task h-8", "Shell");
        TaskNode task9 = new TaskNode("i-9", "Shell Task i-9", "Shell");

        // 初始化任务节点
        dag.addNode(task1.getTaskId(), task1);
        dag.addNode(task2.getTaskId(), task2);
        dag.addNode(task3.getTaskId(), task3);
        dag.addNode(task4.getTaskId(), task4);
        dag.addNode(task5.getTaskId(), task5);
        dag.addNode(task6.getTaskId(), task6);
        dag.addNode(task7.getTaskId(), task7);
        dag.addNode(task8.getTaskId(), task8);
        dag.addNode(task9.getTaskId(), task9);

        // a-1  -> b-2、c-3
        dag.addEdge(task1.getTaskId(), task2.getTaskId());
        dag.addEdge(task1.getTaskId(), task3.getTaskId());

        // b-2  -> d-4、e-5
        dag.addEdge(task2.getTaskId(), task4.getTaskId());
        dag.addEdge(task2.getTaskId(), task5.getTaskId());

        // c-3  -> e-5、f-6
        dag.addEdge(task3.getTaskId(), task5.getTaskId());
        dag.addEdge(task3.getTaskId(), task6.getTaskId());

        // d-4  -> g-7
        dag.addEdge(task4.getTaskId(), task7.getTaskId());
        // e-5  -> g-7
        dag.addEdge(task5.getTaskId(), task7.getTaskId());
        // f-6  -> h-8
        dag.addEdge(task6.getTaskId(), task8.getTaskId());
        // g-7  -> i-9
        dag.addEdge(task7.getTaskId(), task9.getTaskId());

        // 成环依赖
        dag.addEdge(task9.getTaskId(), task1.getTaskId());

        // 成环工具检测器，开启成环分析
        CycleDetector cycleDetector = new CycleDetector(dag, true);
        if (cycleDetector.isCycle()) {
            throw new RuntimeException("当前dag出现成环情况！");
        }

        // 调度执行
        DAGScheduler scheduler = new DAGScheduler(dag);
        scheduler.execute();
    }

}
