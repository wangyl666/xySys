package com.supplychain.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.supplychain.common.Result;
import com.supplychain.entity.ApprovalFlow;
import com.supplychain.entity.ApprovalNode;
import com.supplychain.entity.ApprovalNodeAssignee;
import com.supplychain.service.ApprovalFlowService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@Api(tags = "审批流模板管理")
@RestController
@RequestMapping("/api/approval-flow")
@RequiredArgsConstructor
public class ApprovalFlowController {

    private final ApprovalFlowService approvalFlowService;

    @ApiOperation("分页查询审批流模板")
    @GetMapping("/page")
    public Result<Page<ApprovalFlow>> pageQuery(
            @ApiParam("当前页") @RequestParam(defaultValue = "1") Long current,
            @ApiParam("每页大小") @RequestParam(defaultValue = "10") Long size,
            ApprovalFlow query) {
        Page<ApprovalFlow> page = new Page<>(current, size);
        Page<ApprovalFlow> result = approvalFlowService.pageQuery(page, query);
        return Result.success(result);
    }

    @ApiOperation("获取审批流详情")
    @GetMapping("/{id}")
    public Result<Map<String, Object>> getById(
            @ApiParam("审批流ID") @PathVariable Long id) {
        ApprovalFlow flow = approvalFlowService.getDetail(id);
        if (flow == null) {
            return Result.error("审批流不存在");
        }
        
        List<ApprovalNode> nodes = approvalFlowService.getNodesByFlowId(id);
        for (ApprovalNode node : nodes) {
            List<ApprovalNodeAssignee> assignees = approvalFlowService.getAssigneesByNodeId(node.getId());
            node.setAssignees(assignees);
        }
        
        Map<String, Object> result = new java.util.HashMap<>();
        result.put("flow", flow);
        result.put("nodes", nodes);
        return Result.success(result);
    }

    @ApiOperation("创建审批流模板")
    @PostMapping
    public Result<ApprovalFlow> create(@RequestBody Map<String, Object> request) {
        ApprovalFlow flow = convertToApprovalFlow(request);
        @SuppressWarnings("unchecked")
        List<Map<String, Object>> nodesMap = (List<Map<String, Object>>) request.get("nodes");
        List<ApprovalNode> nodes = convertToApprovalNodeList(nodesMap);
        
        ApprovalFlow result = approvalFlowService.createFlow(flow, nodes);
        return Result.success(result);
    }

    @ApiOperation("更新审批流模板")
    @PutMapping("/{id}")
    public Result<ApprovalFlow> update(
            @ApiParam("审批流ID") @PathVariable Long id,
            @RequestBody Map<String, Object> request) {
        ApprovalFlow flow = convertToApprovalFlow(request);
        flow.setId(id);
        
        @SuppressWarnings("unchecked")
        List<Map<String, Object>> nodesMap = (List<Map<String, Object>>) request.get("nodes");
        List<ApprovalNode> nodes = convertToApprovalNodeList(nodesMap);
        
        ApprovalFlow result = approvalFlowService.updateFlow(flow, nodes);
        return Result.success(result);
    }

    @ApiOperation("删除审批流模板")
    @DeleteMapping("/{id}")
    public Result<Void> delete(
            @ApiParam("审批流ID") @PathVariable Long id) {
        approvalFlowService.deleteFlow(id);
        return Result.success();
    }

    @ApiOperation("启用审批流")
    @PostMapping("/{id}/enable")
    public Result<Void> enable(
            @ApiParam("审批流ID") @PathVariable Long id) {
        approvalFlowService.enableFlow(id);
        return Result.success();
    }

    @ApiOperation("禁用审批流")
    @PostMapping("/{id}/disable")
    public Result<Void> disable(
            @ApiParam("审批流ID") @PathVariable Long id) {
        approvalFlowService.disableFlow(id);
        return Result.success();
    }

    @ApiOperation("获取审批流节点列表")
    @GetMapping("/{flowId}/nodes")
    public Result<List<ApprovalNode>> getNodes(
            @ApiParam("审批流ID") @PathVariable Long flowId) {
        List<ApprovalNode> nodes = approvalFlowService.getNodesByFlowId(flowId);
        for (ApprovalNode node : nodes) {
            List<ApprovalNodeAssignee> assignees = approvalFlowService.getAssigneesByNodeId(node.getId());
            node.setAssignees(assignees);
        }
        return Result.success(nodes);
    }

    @ApiOperation("创建审批节点")
    @PostMapping("/node")
    public Result<ApprovalNode> createNode(@RequestBody Map<String, Object> request) {
        ApprovalNode node = convertToApprovalNode(request);
        @SuppressWarnings("unchecked")
        List<Map<String, Object>> assigneesMap = (List<Map<String, Object>>) request.get("assignees");
        List<ApprovalNodeAssignee> assignees = convertToApprovalNodeAssigneeList(assigneesMap);
        
        ApprovalNode result = approvalFlowService.createNode(node, assignees);
        return Result.success(result);
    }

    @ApiOperation("更新审批节点")
    @PutMapping("/node/{id}")
    public Result<ApprovalNode> updateNode(
            @ApiParam("节点ID") @PathVariable Long id,
            @RequestBody Map<String, Object> request) {
        ApprovalNode node = convertToApprovalNode(request);
        node.setId(id);
        
        @SuppressWarnings("unchecked")
        List<Map<String, Object>> assigneesMap = (List<Map<String, Object>>) request.get("assignees");
        List<ApprovalNodeAssignee> assignees = convertToApprovalNodeAssigneeList(assigneesMap);
        
        ApprovalNode result = approvalFlowService.updateNode(node, assignees);
        return Result.success(result);
    }

    @ApiOperation("删除审批节点")
    @DeleteMapping("/node/{id}")
    public Result<Void> deleteNode(
            @ApiParam("节点ID") @PathVariable Long id) {
        approvalFlowService.deleteNode(id);
        return Result.success();
    }

    @ApiOperation("根据流程编码获取流程配置")
    @GetMapping("/config/{flowCode}")
    public Result<Map<String, Object>> getFlowConfig(
            @ApiParam("流程编码") @PathVariable String flowCode) {
        Map<String, Object> config = approvalFlowService.getFlowConfigByCode(flowCode);
        if (config == null) {
            return Result.error("未找到启用的审批流配置");
        }
        return Result.success(config);
    }

    private ApprovalFlow convertToApprovalFlow(Map<String, Object> map) {
        ApprovalFlow flow = new ApprovalFlow();
        if (map.get("flowName") != null) {
            flow.setFlowName(map.get("flowName").toString());
        }
        if (map.get("flowCode") != null) {
            flow.setFlowCode(map.get("flowCode").toString());
        }
        if (map.get("flowType") != null) {
            flow.setFlowType(map.get("flowType").toString());
        }
        if (map.get("description") != null) {
            flow.setDescription(map.get("description").toString());
        }
        if (map.get("status") != null) {
            flow.setStatus(Integer.parseInt(map.get("status").toString()));
        }
        return flow;
    }

    private List<ApprovalNode> convertToApprovalNodeList(List<Map<String, Object>> nodesMap) {
        if (nodesMap == null) {
            return java.util.Collections.emptyList();
        }
        List<ApprovalNode> nodes = new java.util.ArrayList<>();
        for (Map<String, Object> nodeMap : nodesMap) {
            nodes.add(convertToApprovalNode(nodeMap));
        }
        return nodes;
    }

    private ApprovalNode convertToApprovalNode(Map<String, Object> map) {
        ApprovalNode node = new ApprovalNode();
        if (map.get("id") != null) {
            node.setId(Long.parseLong(map.get("id").toString()));
        }
        if (map.get("flowId") != null) {
            node.setFlowId(Long.parseLong(map.get("flowId").toString()));
        }
        if (map.get("nodeCode") != null) {
            node.setNodeCode(map.get("nodeCode").toString());
        }
        if (map.get("nodeName") != null) {
            node.setNodeName(map.get("nodeName").toString());
        }
        if (map.get("nodeType") != null) {
            node.setNodeType(Integer.parseInt(map.get("nodeType").toString()));
        }
        if (map.get("sortOrder") != null) {
            node.setSortOrder(Integer.parseInt(map.get("sortOrder").toString()));
        }
        if (map.get("approvalType") != null) {
            node.setApprovalType(map.get("approvalType").toString());
        }
        if (map.get("approvalCount") != null) {
            node.setApprovalCount(Integer.parseInt(map.get("approvalCount").toString()));
        }
        if (map.get("conditionExpression") != null) {
            node.setConditionExpression(map.get("conditionExpression").toString());
        }
        if (map.get("description") != null) {
            node.setDescription(map.get("description").toString());
        }
        
        @SuppressWarnings("unchecked")
        List<Map<String, Object>> assigneesMap = (List<Map<String, Object>>) map.get("assignees");
        if (assigneesMap != null) {
            List<ApprovalNodeAssignee> assignees = new java.util.ArrayList<>();
            for (Map<String, Object> assigneeMap : assigneesMap) {
                assignees.add(convertToApprovalNodeAssignee(assigneeMap));
            }
            node.setAssignees(assignees);
        }
        
        return node;
    }

    private List<ApprovalNodeAssignee> convertToApprovalNodeAssigneeList(List<Map<String, Object>> assigneesMap) {
        if (assigneesMap == null) {
            return java.util.Collections.emptyList();
        }
        List<ApprovalNodeAssignee> assignees = new java.util.ArrayList<>();
        for (Map<String, Object> assigneeMap : assigneesMap) {
            assignees.add(convertToApprovalNodeAssignee(assigneeMap));
        }
        return assignees;
    }

    private ApprovalNodeAssignee convertToApprovalNodeAssignee(Map<String, Object> map) {
        ApprovalNodeAssignee assignee = new ApprovalNodeAssignee();
        if (map.get("id") != null) {
            assignee.setId(Long.parseLong(map.get("id").toString()));
        }
        if (map.get("nodeId") != null) {
            assignee.setNodeId(Long.parseLong(map.get("nodeId").toString()));
        }
        if (map.get("flowId") != null) {
            assignee.setFlowId(Long.parseLong(map.get("flowId").toString()));
        }
        if (map.get("assigneeType") != null) {
            assignee.setAssigneeType(map.get("assigneeType").toString());
        }
        if (map.get("assigneeId") != null) {
            assignee.setAssigneeId(Long.parseLong(map.get("assigneeId").toString()));
        }
        if (map.get("assigneeName") != null) {
            assignee.setAssigneeName(map.get("assigneeName").toString());
        }
        if (map.get("assigneeCode") != null) {
            assignee.setAssigneeCode(map.get("assigneeCode").toString());
        }
        if (map.get("sortOrder") != null) {
            assignee.setSortOrder(Integer.parseInt(map.get("sortOrder").toString()));
        }
        return assignee;
    }
}
