package com.supplychain.listener;

import lombok.extern.slf4j.Slf4j;
import org.flowable.engine.delegate.TaskListener;
import org.flowable.task.service.delegate.DelegateTask;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@Component
public class DynamicApprovalTaskListener implements TaskListener {

    @Override
    public void notify(DelegateTask delegateTask) {
        String taskDefinitionKey = delegateTask.getTaskDefinitionKey();
        log.info("动态审批任务监听器触发: taskDefinitionKey={}", taskDefinitionKey);

        String assigneeVar = taskDefinitionKey + "_assignee";
        String candidateUsersVar = taskDefinitionKey + "_candidateUsers";
        String candidateGroupsVar = taskDefinitionKey + "_candidateGroups";

        String assignee = (String) delegateTask.getVariable(assigneeVar);
        if (assignee != null && !assignee.isEmpty()) {
            delegateTask.setAssignee(assignee);
            log.info("设置任务审批人: taskDefinitionKey={}, assignee={}", taskDefinitionKey, assignee);
            return;
        }

        @SuppressWarnings("unchecked")
        List<String> candidateUsers = (List<String>) delegateTask.getVariable(candidateUsersVar);
        if (candidateUsers != null && !candidateUsers.isEmpty()) {
            for (String user : candidateUsers) {
                delegateTask.addCandidateUser(user);
            }
            log.info("设置任务候选用户: taskDefinitionKey={}, candidateUsers={}", taskDefinitionKey, candidateUsers);
        }

        @SuppressWarnings("unchecked")
        List<String> candidateGroups = (List<String>) delegateTask.getVariable(candidateGroupsVar);
        if (candidateGroups != null && !candidateGroups.isEmpty()) {
            for (String group : candidateGroups) {
                delegateTask.addCandidateGroup(group);
            }
            log.info("设置任务候选组: taskDefinitionKey={}, candidateGroups={}", taskDefinitionKey, candidateGroups);
        }
    }
}
