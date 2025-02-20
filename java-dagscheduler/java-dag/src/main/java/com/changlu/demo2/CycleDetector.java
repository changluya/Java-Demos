package com.changlu.demo2;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

/**
 * DAG成环检测器
 */
public class CycleDetector {

    private static final Logger log = LoggerFactory.getLogger(CycleDetector.class);

    private DAG dag;
    private boolean isAnalysis = false;
    private String analysisMsg = "";

    public CycleDetector(DAG dag) {
        this.dag = dag;
    }

    public CycleDetector(DAG dag, boolean isAnalysis) {
        this.dag = dag;
        this.isAnalysis = isAnalysis;
    }

    /**
     * 是否存在出现成环情况
     */
    public boolean isCycle() {
        // 入度map，目的为找到入度为0的顶点
        Map<String, Integer> inDegree = getInDegreeMap();

        // 校验是否出现成环问题。
        // 成环情况：无入度为0的节点数
        int inDegreeZeroNodeNums = 0;
        for (Integer value : inDegree.values()) {
            if (value == 0) inDegreeZeroNodeNums ++;
        }
        boolean isCycle = inDegreeZeroNodeNums == 0;

        // 出现成环情况 & 开启分析
        if (isCycle && isAnalysis) {
            doProcessAnalysis();
        }

        return isCycle;
    }

    /**
     * 分析成环原因，递归实现
     * 时间复杂度：O(n * n)、空间复杂度O(n)
     */
    private void doProcessAnalysis() {
        log.info("分析参数已开启，已检测到成环情况，开始分析成环原因...");
        // 得到dag图的顶点
        List<String> headTaskIds = new ArrayList<>();
        // 方案1：筛选得到入度为0的顶点（成环情况，无法筛选）
        // 入度map，目的为找到入度为0的顶点
//        Map<String, Integer> inDegreeMap = this.getInDegreeMap();
//        for (String taskId : inDegreeMap.keySet()) {
//            if (inDegreeMap.get(taskId) == 0) {
//                headTaskIds.add(taskId);
//            }
//        }
        // 方案2：筛选全部的顶点 这里原因导致后续时间复杂度：O(n * n)，如果能够找到顶点，时间复杂度就是O(n)
        headTaskIds.addAll(this.dag.getNodes().keySet());

        // 初始化访问map、递归map
        Map<String, Boolean> visited = new HashMap<>();
        Map<String, Map.Entry<Boolean, Long>> recursionStack = new HashMap<>();
        for (String taskId : this.dag.getNodes().keySet()) {
            visited.put(taskId, false);
            recursionStack.put(taskId, this.getEntry(false));
        }
        // 开始从顶点（顶点可能有点多个场景）开始进行递归处理
        for (String headTaskId : headTaskIds) {
            boolean isCycle = processAnalysisUtil(headTaskId, visited, recursionStack);
            // 如果检测过程中已经确认有环了，核心原因已分析：this.analysisMsg
            // 此时可以提前结束分析
            if (isCycle) {
                return;
            }
        }
        log.info("分析成环原因已结束");
    }

    /**
     * 递归成环路径分析工具
     * @param taskId 任务id
     * @param visited 访问过节点
     * @param recursionStack 递归访问
     */
    private boolean processAnalysisUtil(String taskId, Map<String, Boolean> visited, Map<String, Map.Entry<Boolean, Long>> recursionStack) {
        visited.put(taskId, true);
        // 单独设计一个recursionStack原因是因为，如果某个节点同时被上游两个节点依赖，不能单独只依靠visited来判断出现成环情况
        recursionStack.put(taskId, this.getEntry(true));
        List<String> depTaskIds = this.dag.getEdges().get(taskId);
        for (String depTaskId : depTaskIds) {
            // 判断是否访问过
            if (!visited.get(depTaskId)) { // 未访问过情况
                if (processAnalysisUtil(depTaskId, visited, recursionStack)) {
                    return true;
                }
            }else if (recursionStack.get(depTaskId).getKey()){ // 递归路径中访问过情况
                // 注：无法直接分析得到某个点，因为一旦不是从顶点往下遍历就会出现错误预测，this.analysisMsg = String.format("出现成环情况！成环点为：%s, 其上游依赖点为：%s", depTaskId, taskId);
                // 这里确认成环路径 根据添加的时间戳顺序来进行排序
                Map<String, Map.Entry<Boolean, Long>> sortedMap = recursionStack.entrySet()
                        .stream()
                        // 过滤掉 key 为 false 的情况
                        .filter(entry -> entry.getValue().getKey())
                        .sorted(Map.Entry.comparingByValue((entry1, entry2) -> {
                            Long timeStamp1 = entry1.getValue();
                            Long timeStamp2 = entry2.getValue();
                            return timeStamp1.compareTo(timeStamp2);
                        }))
                        .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (e1, e2) -> e1, LinkedHashMap::new));
                // 将排序后的键拼接成字符串
                String analysisCyclePath = sortedMap.keySet()
                        .stream()
                        .map(key -> "[" + key + "]")
                        .collect(Collectors.joining(" -> "));
                analysisCyclePath = analysisCyclePath + " -> " + "[" + depTaskId + "]";
                this.analysisMsg = String.format("成环路径为 => %s, 起始扫描起点 => %s", analysisCyclePath, "[" + depTaskId + "]");
                log.info(this.analysisMsg);
                return true;
            }
        }
        // 回退某个节点往下找依赖过程中的标识字段
        recursionStack.put(taskId, this.getEntry(false));
        return false;
    }

    // -------------------辅助封装方法-------------------

    private Map.Entry<Boolean, Long> getEntry(Boolean key) {
        if (key) {
            // 自定义TimestampGenerator.generateUniqueTimestamp原因为：可能会出现设置时间戳一致的情况，导致依赖路径无法确认
            return getMapEntry(key, TimestampGenerator.generateUniqueTimestamp());
        }else {
            return getMapEntry(key, -1L);
        }
    }

    /**
     * 创建一个 Map.Entry 对象。
     * @param key 键
     * @param value 值
     * @param <T> 键的类型
     * @param <R> 值的类型
     * @return 返回一个 Map.Entry 对象
     */
    private  <T, R> Map.Entry<T, R> getMapEntry(T key, R value) {
        return new AbstractMap.SimpleEntry<>(key, value);
    }

    /**
     * 获取点入度map
     * @return 入度结果集
     */
    private Map<String, Integer> getInDegreeMap() {
        Map<String, Integer> inDegree = new HashMap<>();
        // 初始化入度
        for (String node : dag.getNodes().keySet()) {
            inDegree.put(node, 0);
        }
        // 入度累加
        for (List<String> deps : dag.getEdges().values()) {
            for (String dep : deps) {
                inDegree.put(dep, inDegree.get(dep) + 1);
            }
        }
        return inDegree;
    }

    /**
     * 获取成环分析结果
     * @return 成环分析结果
     */
    public String getAnalysisMsg() {
        return analysisMsg;
    }

}

class TimestampGenerator {
    private static final AtomicLong counter = new AtomicLong(0);

    /**
     * 生成一个唯一的长整型时间戳
     * 简易版单机
     * @return 唯一的时间戳
     */
    public static long generateUniqueTimestamp() {
        return System.currentTimeMillis() * 1000 + counter.getAndIncrement();
    }
}
