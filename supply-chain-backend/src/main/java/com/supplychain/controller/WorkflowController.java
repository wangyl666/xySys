package com.supplychain.controller;

import com.supplychain.common.Result;
import com.supplychain.service.WorkflowService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@Api(tags = "工作流管理")
@RestController
@RequestMapping("/api/workflow")
@RequiredArgsConstructor
public class WorkflowController {

    private final WorkflowService workflowService;

    @ApiOperation("获取待办任务列表")
    @GetMapping("/todo/{userId}")
    public Result<List<Map<String, Object>>> getTodoTasks(
            @ApiParam("用户ID") @PathVariable String userId) {
        List<Map<String, Object>> tasks = workflowService.getTodoTasks(userId);
        return Result.success(tasks);
    }

    @ApiOperation("获取已办任务列表")
    @GetMapping("/done/{userId}")
    public Result<List<Map<String, Object>>> getDoneTasks(
            @ApiParam("用户ID") @PathVariable String userId) {
        List<Map<String, Object>> tasks = workflowService.getDoneTasks(userId);
        return Result.success(tasks);
    }

    @ApiOperation("认领任务")
    @PostMapping("/claim/{taskId}/{userId}")
    public Result<Void> claimTask(
            @ApiParam("任务ID") @PathVariable String taskId,
            @ApiParam("用户ID") @PathVariable String userId) {
        workflowService.claimTask(taskId, userId);
        return Result.success();
    }

    @ApiOperation("取消认领任务")
    @PostMapping("/unclaim/{taskId}")
    public Result<Void> unclaimTask(
            @ApiParam("任务ID") @PathVariable String taskId) {
        workflowService.unclaimTask(taskId);
        return Result.success();
    }

    @ApiOperation("审批通过")
    @PostMapping("/approve/{taskId}")
    public Result<Void> approve(
            @ApiParam("任务ID") @PathVariable String taskId,
            @ApiParam("审批意见") @RequestParam(required = false) String comment) {
        workflowService.approve(taskId, comment);
        return Result.success();
    }

    @ApiOperation("审批拒绝")
    @PostMapping("/reject/{taskId}")
    public Result<Void> reject(
            @ApiParam("任务ID") @PathVariable String taskId,
            @ApiParam("审批意见") @RequestParam(required = false) String comment) {
        workflowService.reject(taskId, comment);
        return Result.success();
    }

    @ApiOperation("完成任务")
    @PostMapping("/complete/{taskId}")
    public Result<Void> completeTask(
            @ApiParam("任务ID") @PathVariable String taskId,
            @RequestBody(required = false) Map<String, Object> variables) {
        workflowService.completeTask(taskId, variables);
        return Result.success();
    }

    @ApiOperation("转办任务")
    @PostMapping("/transfer/{taskId}/{userId}")
    public Result<Void> transferTask(
            @ApiParam("任务ID") @PathVariable String taskId,
            @ApiParam("目标用户ID") @PathVariable String userId) {
        workflowService.transferTask(taskId, userId);
        return Result.success();
    }

    @ApiOperation("委派任务")
    @PostMapping("/delegate/{taskId}/{userId}")
    public Result<Void> delegateTask(
            @ApiParam("任务ID") @PathVariable String taskId,
            @ApiParam("目标用户ID") @PathVariable String userId) {
        workflowService.delegateTask(taskId, userId);
        return Result.success();
    }

    @ApiOperation("获取流程历史任务")
    @GetMapping("/history/{processInstanceId}")
    public Result<List<Map<String, Object>>> getHistoryTasks(
            @ApiParam("流程实例ID") @PathVariable String processInstanceId) {
        List<Map<String, Object>> tasks = workflowService.getHistoryTasks(processInstanceId);
        return Result.success(tasks);
    }

    @ApiOperation("获取流程活动节点")
    @GetMapping("/activities/{processInstanceId}")
    public Result<List<Map<String, Object>>> getActiveActivities(
            @ApiParam("流程实例ID") @PathVariable String processInstanceId) {
        List<Map<String, Object>> activities = workflowService.getActiveActivities(processInstanceId);
        return Result.success(activities);
    }

    @ApiOperation("挂起流程实例")
    @PostMapping("/suspend/{processInstanceId}")
    public Result<Void> suspendProcessInstance(
            @ApiParam("流程实例ID") @PathVariable String processInstanceId) {
        workflowService.suspendProcessInstance(processInstanceId);
        return Result.success();
    }

    @ApiOperation("激活流程实例")
    @PostMapping("/activate/{processInstanceId}")
    public Result<Void> activateProcessInstance(
            @ApiParam("流程实例ID") @PathVariable String processInstanceId) {
        workflowService.activateProcessInstance(processInstanceId);
        return Result.success();
    }

    @ApiOperation("终止流程实例")
    @PostMapping("/terminate/{processInstanceId}")
    public Result<Void> terminateProcessInstance(
            @ApiParam("流程实例ID") @PathVariable String processInstanceId,
            @ApiParam("终止原因") @RequestParam(required = false) String reason) {
        workflowService.terminateProcessInstance(processInstanceId, reason);
        return Result.success();
    }
}
