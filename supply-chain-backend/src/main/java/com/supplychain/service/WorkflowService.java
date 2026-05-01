package com.supplychain.service;

import java.util.List;
import java.util.Map;

public interface WorkflowService {

    /**
     * 启动流程实例
     */
    String startProcessInstance(String processDefinitionKey, String businessKey, String initiator, Map<String, Object> variables);

    /**
     * 完成任务
     */
    void completeTask(String taskId, Map<String, Object> variables);

    /**
     * 认领任务
     */
    void claimTask(String taskId, String userId);

    /**
     * 取消认领任务
     */
    void unclaimTask(String taskId);

    /**
     * 委派任务
     */
    void delegateTask(String taskId, String userId);

    /**
     * 转办任务
     */
    void transferTask(String taskId, String userId);

    /**
     * 审批通过
     */
    void approve(String taskId, String comment);

    /**
     * 审批拒绝
     */
    void reject(String taskId, String comment);

    /**
     * 获取待办任务列表
     */
    List<Map<String, Object>> getTodoTasks(String userId);

    /**
     * 获取已办任务列表
     */
    List<Map<String, Object>> getDoneTasks(String userId);

    /**
     * 获取流程实例的历史任务
     */
    List<Map<String, Object>> getHistoryTasks(String processInstanceId);

    /**
     * 获取流程实例的活动节点
     */
    List<Map<String, Object>> getActiveActivities(String processInstanceId);

    /**
     * 挂起流程实例
     */
    void suspendProcessInstance(String processInstanceId);

    /**
     * 激活流程实例
     */
    void activateProcessInstance(String processInstanceId);

    /**
     * 终止流程实例
     */
    void terminateProcessInstance(String processInstanceId, String reason);

    /**
     * 获取我发起的流程实例列表
     */
    List<Map<String, Object>> getMyStartedProcesses(String userId);

    /**
     * 获取我参与的流程实例列表
     */
    List<Map<String, Object>> getMyInvolvedProcesses(String userId);
}
