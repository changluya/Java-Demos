package com.changlu.demo2;

import java.util.Map;

/**
 * 任务node节点
 */
public class TaskNode {

    private String taskId; // 任务唯一标识
    private String taskName; // 任务名称
    private String taskType; // 任务类型（Shell、SQL 等）
    private Map<String, Object> params; // 任务参数

    public TaskNode() {
    }

    public TaskNode(String taskId, String taskName, String taskType) {
        this.taskId = taskId;
        this.taskName = taskName;
        this.taskType = taskType;
    }

    public String getTaskId() {
        return taskId;
    }

    public void setTaskId(String taskId) {
        this.taskId = taskId;
    }

    public String getTaskName() {
        return taskName;
    }

    public void setTaskName(String taskName) {
        this.taskName = taskName;
    }

    public String getTaskType() {
        return taskType;
    }

    public void setTaskType(String taskType) {
        this.taskType = taskType;
    }

    public Map<String, Object> getParams() {
        return params;
    }

    public void setParams(Map<String, Object> params) {
        this.params = params;
    }
}
