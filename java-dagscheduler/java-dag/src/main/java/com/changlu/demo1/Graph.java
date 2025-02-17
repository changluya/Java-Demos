package com.changlu.demo1;

import java.util.LinkedList;

/**
 * 该类表示一个有向图，使用邻接表进行存储。
 */
public class Graph {

    private int V; // 顶点数（节点数量）
    private LinkedList<Integer> adj[]; // 邻接表

    /**
     * 构造函数，初始化图。
     *
     * @param v 图的顶点数
     */
    public Graph(int v) {
        this.V = v;
        this.adj = new LinkedList[v]; // 初始化邻接表
        for (int i = 0; i < v; i++) {
            this.adj[i] = new LinkedList<>(); // 为每个顶点创建一个空的邻接表
        }
    }

    /**
     * 添加一条从顶点 v 到顶点 w 的边。
     *
     * @param v 边的起始顶点
     * @param w 边的目标顶点
     */
    public void addEdge(int v, int w) {
        this.adj[v].add(w); // 将顶点 w 添加到顶点 v 的邻接表中
    }

    /**
     * 获取图的顶点数。
     *
     * @return 图的顶点数
     */
    public int getV() {
        return V;
    }

    /**
     * 获取图的邻接表。
     *
     * @return 图的邻接表
     */
    public LinkedList<Integer>[] getAdj() {
        return adj;
    }
}