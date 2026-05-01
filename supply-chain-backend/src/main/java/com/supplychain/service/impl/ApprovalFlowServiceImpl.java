package com.supplychain.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.supplychain.entity.ApprovalFlow;
import com.supplychain.entity.ApprovalNode;
import com.supplychain.entity.ApprovalNodeAssignee;
import com.supplychain.exception.BusinessException;
import com.supplychain.mapper.ApprovalFlowMapper;
import com.supplychain.mapper.ApprovalNodeAssigneeMapper;
import com.supplychain.mapper.ApprovalNodeMapper;
import com.supplychain.service.ApprovalFlowService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class ApprovalFlowServiceImpl extends ServiceImpl<ApprovalFlowMapper, ApprovalFlow> implements ApprovalFlowService {

    private final ApprovalNodeMapper nodeMapper;
    private final ApprovalNodeAssigneeMapper assigneeMapper;

    @Override
    public Page<ApprovalFlow> pageQuery(Page<ApprovalFlow> page, ApprovalFlow query) {
        LambdaQueryWrapper<ApprovalFlow> wrapper = new LambdaQueryWrapper<>();
        if (query != null) {
            if (StringUtils.hasText(query.getFlowName())) {
                wrapper.like(ApprovalFlow::getFlowName, query.getFlowName());
            }
            if (StringUtils.hasText(query.getFlowCode())) {
                wrapper.eq(ApprovalFlow::getFlowCode, query.getFlowCode());
            }
            if (StringUtils.hasText(query.getFlowType())) {
                wrapper.eq(ApprovalFlow::getFlowType, query.getFlowType());
            }
            if (query.getStatus() != null) {
                wrapper.eq(ApprovalFlow::getStatus, query.getStatus());
            }
        }
        wrapper.orderByDesc(ApprovalFlow::getCreateTime);
        return this.page(page, wrapper);
    }

    @Override
    public ApprovalFlow getDetail(Long flowId) {
        return this.getById(flowId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public ApprovalFlow createFlow(ApprovalFlow flow, List<ApprovalNode> nodes) {
        log.info("创建审批流: flow={}, nodes={}", flow, nodes);
        
        LambdaQueryWrapper<ApprovalFlow> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ApprovalFlow::getFlowCode, flow.getFlowCode());
        if (this.count(wrapper) > 0) {
            throw new BusinessException("流程编码已存在");
        }
        
        flow.setStatus(0);
        flow.setVersion(1);
        this.save(flow);
        
        if (!CollectionUtils.isEmpty(nodes)) {
            for (int i = 0; i < nodes.size(); i++) {
                ApprovalNode node = nodes.get(i);
                node.setFlowId(flow.getId());
                node.setSortOrder(i + 1);
                nodeMapper.insert(node);
            }
        }
        
        log.info("审批流创建成功: flowId={}", flow.getId());
        return flow;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public ApprovalFlow updateFlow(ApprovalFlow flow, List<ApprovalNode> nodes) {
        log.info("更新审批流: flowId={}", flow.getId());
        
        ApprovalFlow existFlow = this.getById(flow.getId());
        if (existFlow == null) {
            throw new BusinessException("审批流不存在");
        }
        
        if (existFlow.getStatus() == 1) {
            throw new BusinessException("已启用的审批流不能修改");
        }
        
        LambdaQueryWrapper<ApprovalFlow> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ApprovalFlow::getFlowCode, flow.getFlowCode());
        wrapper.ne(ApprovalFlow::getId, flow.getId());
        if (this.count(wrapper) > 0) {
            throw new BusinessException("流程编码已存在");
        }
        
        flow.setVersion(existFlow.getVersion() + 1);
        this.updateById(flow);
        
        if (nodes != null) {
            nodeMapper.delete(new LambdaQueryWrapper<ApprovalNode>()
                    .eq(ApprovalNode::getFlowId, flow.getId()));
            assigneeMapper.delete(new LambdaQueryWrapper<ApprovalNodeAssignee>()
                    .eq(ApprovalNodeAssignee::getFlowId, flow.getId()));
            
            for (int i = 0; i < nodes.size(); i++) {
                ApprovalNode node = nodes.get(i);
                node.setFlowId(flow.getId());
                node.setSortOrder(i + 1);
                nodeMapper.insert(node);
            }
        }
        
        log.info("审批流更新成功: flowId={}", flow.getId());
        return flow;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteFlow(Long flowId) {
        log.info("删除审批流: flowId={}", flowId);
        
        ApprovalFlow flow = this.getById(flowId);
        if (flow == null) {
            throw new BusinessException("审批流不存在");
        }
        
        if (flow.getStatus() == 1) {
            throw new BusinessException("已启用的审批流不能删除");
        }
        
        assigneeMapper.delete(new LambdaQueryWrapper<ApprovalNodeAssignee>()
                .eq(ApprovalNodeAssignee::getFlowId, flowId));
        nodeMapper.delete(new LambdaQueryWrapper<ApprovalNode>()
                .eq(ApprovalNode::getFlowId, flowId));
        this.removeById(flowId);
        
        log.info("审批流删除成功: flowId={}", flowId);
    }

    @Override
    public List<ApprovalNode> getNodesByFlowId(Long flowId) {
        return nodeMapper.selectList(
                new LambdaQueryWrapper<ApprovalNode>()
                        .eq(ApprovalNode::getFlowId, flowId)
                        .orderByAsc(ApprovalNode::getSortOrder)
        );
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public ApprovalNode createNode(ApprovalNode node, List<ApprovalNodeAssignee> assignees) {
        log.info("创建审批节点: node={}", node);
        
        Integer maxSort = nodeMapper.selectOne(
                new LambdaQueryWrapper<ApprovalNode>()
                        .eq(ApprovalNode::getFlowId, node.getFlowId())
                        .orderByDesc(ApprovalNode::getSortOrder)
                        .last("LIMIT 1")
        ) != null ? nodeMapper.selectOne(
                new LambdaQueryWrapper<ApprovalNode>()
                        .eq(ApprovalNode::getFlowId, node.getFlowId())
                        .orderByDesc(ApprovalNode::getSortOrder)
                        .last("LIMIT 1")
        ).getSortOrder() : 0;
        
        node.setSortOrder(maxSort + 1);
        nodeMapper.insert(node);
        
        if (!CollectionUtils.isEmpty(assignees)) {
            for (int i = 0; i < assignees.size(); i++) {
                ApprovalNodeAssignee assignee = assignees.get(i);
                assignee.setNodeId(node.getId());
                assignee.setFlowId(node.getFlowId());
                assignee.setSortOrder(i + 1);
                assigneeMapper.insert(assignee);
            }
        }
        
        log.info("审批节点创建成功: nodeId={}", node.getId());
        return node;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public ApprovalNode updateNode(ApprovalNode node, List<ApprovalNodeAssignee> assignees) {
        log.info("更新审批节点: nodeId={}", node.getId());
        
        ApprovalNode existNode = nodeMapper.selectById(node.getId());
        if (existNode == null) {
            throw new BusinessException("审批节点不存在");
        }
        
        nodeMapper.updateById(node);
        
        if (assignees != null) {
            assigneeMapper.delete(new LambdaQueryWrapper<ApprovalNodeAssignee>()
                    .eq(ApprovalNodeAssignee::getNodeId, node.getId()));
            
            for (int i = 0; i < assignees.size(); i++) {
                ApprovalNodeAssignee assignee = assignees.get(i);
                assignee.setNodeId(node.getId());
                assignee.setFlowId(existNode.getFlowId());
                assignee.setSortOrder(i + 1);
                assigneeMapper.insert(assignee);
            }
        }
        
        log.info("审批节点更新成功: nodeId={}", node.getId());
        return node;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteNode(Long nodeId) {
        log.info("删除审批节点: nodeId={}", nodeId);
        
        ApprovalNode node = nodeMapper.selectById(nodeId);
        if (node == null) {
            throw new BusinessException("审批节点不存在");
        }
        
        assigneeMapper.delete(new LambdaQueryWrapper<ApprovalNodeAssignee>()
                .eq(ApprovalNodeAssignee::getNodeId, nodeId));
        nodeMapper.deleteById(nodeId);
        
        log.info("审批节点删除成功: nodeId={}", nodeId);
    }

    @Override
    public List<ApprovalNodeAssignee> getAssigneesByNodeId(Long nodeId) {
        return assigneeMapper.selectList(
                new LambdaQueryWrapper<ApprovalNodeAssignee>()
                        .eq(ApprovalNodeAssignee::getNodeId, nodeId)
                        .orderByAsc(ApprovalNodeAssignee::getSortOrder)
        );
    }

    @Override
    public Map<String, Object> getFlowConfigByCode(String flowCode) {
        LambdaQueryWrapper<ApprovalFlow> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ApprovalFlow::getFlowCode, flowCode);
        wrapper.eq(ApprovalFlow::getStatus, 1);
        wrapper.orderByDesc(ApprovalFlow::getVersion);
        wrapper.last("LIMIT 1");
        
        ApprovalFlow flow = this.getOne(wrapper);
        if (flow == null) {
            return null;
        }
        
        Map<String, Object> result = new HashMap<>();
        result.put("flow", flow);
        result.put("processKey", StringUtils.hasText(flow.getProcessKey()) ? flow.getProcessKey() : flow.getFlowCode());
        
        List<ApprovalNode> nodes = getNodesByFlowId(flow.getId());
        List<Map<String, Object>> nodeList = new ArrayList<>();
        
        for (ApprovalNode node : nodes) {
            Map<String, Object> nodeMap = new HashMap<>();
            nodeMap.put("node", node);
            
            List<ApprovalNodeAssignee> assignees = getAssigneesByNodeId(node.getId());
            nodeMap.put("assignees", assignees);
            
            nodeList.add(nodeMap);
        }
        
        result.put("nodes", nodeList);
        return result;
    }

    @Override
    public Map<String, Object> getFlowConfigForProcess(String flowCode, Map<String, Object> variables) {
        Map<String, Object> flowConfig = getFlowConfigByCode(flowCode);
        if (flowConfig == null) {
            return null;
        }
        
        Map<String, Object> result = new HashMap<>();
        result.put("flow", flowConfig.get("flow"));
        result.put("processKey", flowConfig.get("processKey"));
        
        @SuppressWarnings("unchecked")
        List<Map<String, Object>> nodes = (List<Map<String, Object>>) flowConfig.get("nodes");
        List<Map<String, Object>> filteredNodes = new ArrayList<>();
        
        for (Map<String, Object> nodeMap : nodes) {
            ApprovalNode node = (ApprovalNode) nodeMap.get("node");
            
            if (StringUtils.hasText(node.getConditionExpression())) {
                if (!evaluateCondition(node.getConditionExpression(), variables)) {
                    continue;
                }
            }
            
            @SuppressWarnings("unchecked")
            List<ApprovalNodeAssignee> assignees = (List<ApprovalNodeAssignee>) nodeMap.get("assignees");
            List<String> assigneeUserIds = new ArrayList<>();
            List<String> assigneeGroupIds = new ArrayList<>();
            
            for (ApprovalNodeAssignee assignee : assignees) {
                if ("USER".equals(assignee.getAssigneeType())) {
                    assigneeUserIds.add(String.valueOf(assignee.getAssigneeId()));
                } else if ("ROLE".equals(assignee.getAssigneeType())) {
                    assigneeGroupIds.add(assignee.getAssigneeCode());
                } else if ("DEPARTMENT".equals(assignee.getAssigneeType())) {
                    assigneeGroupIds.add(assignee.getAssigneeCode());
                }
            }
            
            Map<String, Object> filteredNode = new HashMap<>();
            filteredNode.put("node", node);
            filteredNode.put("assignees", assignees);
            filteredNode.put("assigneeUserIds", assigneeUserIds);
            filteredNode.put("assigneeGroupIds", assigneeGroupIds);
            
            filteredNodes.add(filteredNode);
        }
        
        result.put("nodes", filteredNodes);
        return result;
    }

    private boolean evaluateCondition(String expression, Map<String, Object> variables) {
        if (variables == null || variables.isEmpty()) {
            return true;
        }
        
        try {
            if (expression.contains("totalAmount")) {
                BigDecimal totalAmount = null;
                if (variables.get("totalAmount") instanceof BigDecimal) {
                    totalAmount = (BigDecimal) variables.get("totalAmount");
                } else if (variables.get("totalAmount") != null) {
                    totalAmount = new BigDecimal(variables.get("totalAmount").toString());
                }
                
                if (totalAmount != null) {
                    if (expression.contains("<=")) {
                        String[] parts = expression.split("<=");
                        if (parts.length == 2) {
                            BigDecimal threshold = new BigDecimal(parts[1].trim());
                            return totalAmount.compareTo(threshold) <= 0;
                        }
                    } else if (expression.contains(">")) {
                        String[] parts = expression.split(">");
                        if (parts.length == 2) {
                            BigDecimal threshold = new BigDecimal(parts[1].trim());
                            return totalAmount.compareTo(threshold) > 0;
                        }
                    }
                }
            }
        } catch (Exception e) {
            log.warn("条件表达式解析失败: expression={}, error={}", expression, e.getMessage());
        }
        
        return true;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void enableFlow(Long flowId) {
        log.info("启用审批流: flowId={}", flowId);
        
        ApprovalFlow flow = this.getById(flowId);
        if (flow == null) {
            throw new BusinessException("审批流不存在");
        }
        
        LambdaQueryWrapper<ApprovalNode> nodeWrapper = new LambdaQueryWrapper<>();
        nodeWrapper.eq(ApprovalNode::getFlowId, flowId);
        if (nodeMapper.selectCount(nodeWrapper) == 0) {
            throw new BusinessException("审批流没有配置节点，无法启用");
        }
        
        LambdaQueryWrapper<ApprovalFlow> updateWrapper = new LambdaQueryWrapper<>();
        updateWrapper.eq(ApprovalFlow::getFlowCode, flow.getFlowCode());
        updateWrapper.eq(ApprovalFlow::getStatus, 1);
        
        List<ApprovalFlow> enabledFlows = this.list(updateWrapper);
        for (ApprovalFlow enabledFlow : enabledFlows) {
            enabledFlow.setStatus(0);
            this.updateById(enabledFlow);
        }
        
        flow.setStatus(1);
        this.updateById(flow);
        
        log.info("审批流启用成功: flowId={}", flowId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void disableFlow(Long flowId) {
        log.info("禁用审批流: flowId={}", flowId);
        
        ApprovalFlow flow = this.getById(flowId);
        if (flow == null) {
            throw new BusinessException("审批流不存在");
        }
        
        flow.setStatus(0);
        this.updateById(flow);
        
        log.info("审批流禁用成功: flowId={}", flowId);
    }
}
