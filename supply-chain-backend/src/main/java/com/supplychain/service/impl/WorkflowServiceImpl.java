package com.supplychain.service.impl;

import com.supplychain.service.WorkflowService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.flowable.engine.*;
import org.flowable.engine.history.HistoricActivityInstance;
import org.flowable.engine.history.HistoricProcessInstance;
import org.flowable.engine.repository.ProcessDefinition;
import org.flowable.engine.runtime.ActivityInstance;
import org.flowable.engine.runtime.Execution;
import org.flowable.engine.runtime.ProcessInstance;
import org.flowable.task.api.Task;
import org.flowable.task.api.history.HistoricTaskInstance;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class WorkflowServiceImpl implements WorkflowService {

    private final RuntimeService runtimeService;
    private final TaskService taskService;
    private final HistoryService historyService;
    private final RepositoryService repositoryService;
    private final ManagementService managementService;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public String startProcessInstance(String processDefinitionKey, String businessKey, Map<String, Object> variables) {
        log.info("启动流程实例: processDefinitionKey={}, businessKey={}", processDefinitionKey, businessKey);
        ProcessInstance processInstance = runtimeService.startProcessInstanceByKey(processDefinitionKey, businessKey, variables);
        log.info("流程实例启动成功: processInstanceId={}", processInstance.getId());
        return processInstance.getId();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void completeTask(String taskId, Map<String, Object> variables) {
        log.info("完成任务: taskId={}", taskId);
        taskService.complete(taskId, variables);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void claimTask(String taskId, String userId) {
        log.info("认领任务: taskId={}, userId={}", taskId, userId);
        taskService.claim(taskId, userId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void unclaimTask(String taskId) {
        log.info("取消认领任务: taskId={}", taskId);
        taskService.unclaim(taskId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void delegateTask(String taskId, String userId) {
        log.info("委派任务: taskId={}, userId={}", taskId, userId);
        taskService.delegateTask(taskId, userId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void transferTask(String taskId, String userId) {
        log.info("转办任务: taskId={}, userId={}", taskId, userId);
        taskService.setAssignee(taskId, userId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void approve(String taskId, String comment) {
        log.info("审批通过: taskId={}", taskId);
        Map<String, Object> variables = new HashMap<>();
        variables.put("approved", true);
        variables.put("comment", comment);
        taskService.complete(taskId, variables);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void reject(String taskId, String comment) {
        log.info("审批拒绝: taskId={}", taskId);
        Map<String, Object> variables = new HashMap<>();
        variables.put("approved", false);
        variables.put("comment", comment);
        taskService.complete(taskId, variables);
    }

    @Override
    public List<Map<String, Object>> getTodoTasks(String userId) {
        log.info("获取待办任务列表: userId={}", userId);
        List<Task> tasks = taskService.createTaskQuery()
                .taskCandidateOrAssigned(userId)
                .orderByTaskCreateTime()
                .desc()
                .list();

        return convertToTaskMapList(tasks);
    }

    @Override
    public List<Map<String, Object>> getDoneTasks(String userId) {
        log.info("获取已办任务列表: userId={}", userId);
        List<HistoricTaskInstance> historicTasks = historyService.createHistoricTaskInstanceQuery()
                .taskAssignee(userId)
                .finished()
                .orderByHistoricTaskInstanceEndTime()
                .desc()
                .list();

        return convertToHistoricTaskMapList(historicTasks);
    }

    @Override
    public List<Map<String, Object>> getHistoryTasks(String processInstanceId) {
        log.info("获取流程实例历史任务: processInstanceId={}", processInstanceId);
        List<HistoricTaskInstance> historicTasks = historyService.createHistoricTaskInstanceQuery()
                .processInstanceId(processInstanceId)
                .orderByHistoricTaskInstanceStartTime()
                .asc()
                .list();

        return convertToHistoricTaskMapList(historicTasks);
    }

    @Override
    public List<Map<String, Object>> getActiveActivities(String processInstanceId) {
        log.info("获取流程实例活动节点: processInstanceId={}", processInstanceId);
        List<ActivityInstance> activityInstances = runtimeService.createActivityInstanceQuery()
                .processInstanceId(processInstanceId)
                .list();

        List<Map<String, Object>> result = new ArrayList<>();
        for (ActivityInstance activity : activityInstances) {
            Map<String, Object> map = new HashMap<>();
            map.put("activityId", activity.getActivityId());
            map.put("activityName", activity.getActivityName());
            map.put("activityType", activity.getActivityType());
            map.put("startTime", activity.getStartTime());
            map.put("assignee", activity.getAssignee());
            result.add(map);
        }
        return result;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void suspendProcessInstance(String processInstanceId) {
        log.info("挂起流程实例: processInstanceId={}", processInstanceId);
        runtimeService.suspendProcessInstanceById(processInstanceId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void activateProcessInstance(String processInstanceId) {
        log.info("激活流程实例: processInstanceId={}", processInstanceId);
        runtimeService.activateProcessInstanceById(processInstanceId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void terminateProcessInstance(String processInstanceId, String reason) {
        log.info("终止流程实例: processInstanceId={}, reason={}", processInstanceId, reason);
        runtimeService.deleteProcessInstance(processInstanceId, reason);
    }

    private List<Map<String, Object>> convertToTaskMapList(List<Task> tasks) {
        List<Map<String, Object>> result = new ArrayList<>();
        for (Task task : tasks) {
            Map<String, Object> map = new HashMap<>();
            map.put("taskId", task.getId());
            map.put("taskName", task.getName());
            map.put("taskDefinitionKey", task.getTaskDefinitionKey());
            map.put("processInstanceId", task.getProcessInstanceId());
            map.put("processDefinitionId", task.getProcessDefinitionId());
            map.put("assignee", task.getAssignee());
            map.put("owner", task.getOwner());
            map.put("createTime", task.getCreateTime());
            map.put("dueDate", task.getDueDate());
            map.put("priority", task.getPriority());
            map.put("description", task.getDescription());

            ProcessInstance processInstance = runtimeService.createProcessInstanceQuery()
                    .processInstanceId(task.getProcessInstanceId())
                    .singleResult();
            if (processInstance != null) {
                map.put("businessKey", processInstance.getBusinessKey());
            }

            ProcessDefinition processDefinition = repositoryService.createProcessDefinitionQuery()
                    .processDefinitionId(task.getProcessDefinitionId())
                    .singleResult();
            if (processDefinition != null) {
                map.put("processDefinitionName", processDefinition.getName());
                map.put("processDefinitionKey", processDefinition.getKey());
            }

            result.add(map);
        }
        return result;
    }

    private List<Map<String, Object>> convertToHistoricTaskMapList(List<HistoricTaskInstance> historicTasks) {
        List<Map<String, Object>> result = new ArrayList<>();
        for (HistoricTaskInstance task : historicTasks) {
            Map<String, Object> map = new HashMap<>();
            map.put("taskId", task.getId());
            map.put("taskName", task.getName());
            map.put("taskDefinitionKey", task.getTaskDefinitionKey());
            map.put("processInstanceId", task.getProcessInstanceId());
            map.put("processDefinitionId", task.getProcessDefinitionId());
            map.put("assignee", task.getAssignee());
            map.put("owner", task.getOwner());
            map.put("createTime", task.getCreateTime());
            map.put("endTime", task.getEndTime());
            map.put("dueDate", task.getDueDate());
            map.put("priority", task.getPriority());
            map.put("description", task.getDescription());
            map.put("deleteReason", task.getDeleteReason());
            map.put("durationInMillis", task.getDurationInMillis());

            HistoricProcessInstance processInstance = historyService.createHistoricProcessInstanceQuery()
                    .processInstanceId(task.getProcessInstanceId())
                    .singleResult();
            if (processInstance != null) {
                map.put("businessKey", processInstance.getBusinessKey());
            }

            ProcessDefinition processDefinition = repositoryService.createProcessDefinitionQuery()
                    .processDefinitionId(task.getProcessDefinitionId())
                    .singleResult();
            if (processDefinition != null) {
                map.put("processDefinitionName", processDefinition.getName());
                map.put("processDefinitionKey", processDefinition.getKey());
            }

            result.add(map);
        }
        return result;
    }
}
