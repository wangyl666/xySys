package com.supplychain.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.supplychain.entity.ApprovalFlow;
import com.supplychain.entity.ApprovalNode;
import com.supplychain.entity.ApprovalNodeAssignee;

import java.util.List;
import java.util.Map;

public interface ApprovalFlowService extends IService<ApprovalFlow> {

    Page<ApprovalFlow> pageQuery(Page<ApprovalFlow> page, ApprovalFlow query);

    ApprovalFlow getDetail(Long flowId);

    ApprovalFlow createFlow(ApprovalFlow flow, List<ApprovalNode> nodes);

    ApprovalFlow updateFlow(ApprovalFlow flow, List<ApprovalNode> nodes);

    void deleteFlow(Long flowId);

    List<ApprovalNode> getNodesByFlowId(Long flowId);

    ApprovalNode createNode(ApprovalNode node, List<ApprovalNodeAssignee> assignees);

    ApprovalNode updateNode(ApprovalNode node, List<ApprovalNodeAssignee> assignees);

    void deleteNode(Long nodeId);

    List<ApprovalNodeAssignee> getAssigneesByNodeId(Long nodeId);

    Map<String, Object> getFlowConfigByCode(String flowCode);

    Map<String, Object> getFlowConfigForProcess(String flowCode, Map<String, Object> variables);

    void enableFlow(Long flowId);

    void disableFlow(Long flowId);
}
