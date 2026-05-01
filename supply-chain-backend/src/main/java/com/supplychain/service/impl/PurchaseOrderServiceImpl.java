package com.supplychain.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.supplychain.entity.ApprovalNode;
import com.supplychain.entity.ApprovalNodeAssignee;
import com.supplychain.entity.PurchaseOrder;
import com.supplychain.entity.PurchaseOrderItem;
import com.supplychain.exception.BusinessException;
import com.supplychain.mapper.PurchaseOrderItemMapper;
import com.supplychain.mapper.PurchaseOrderMapper;
import com.supplychain.service.ApprovalFlowService;
import com.supplychain.service.BillFlowConfigService;
import com.supplychain.service.PurchaseOrderService;
import com.supplychain.service.WorkflowService;
import com.supplychain.util.SecurityUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class PurchaseOrderServiceImpl extends ServiceImpl<PurchaseOrderMapper, PurchaseOrder> implements PurchaseOrderService {

    private final PurchaseOrderItemMapper orderItemMapper;
    private final WorkflowService workflowService;
    private final ApprovalFlowService approvalFlowService;
    private final BillFlowConfigService billFlowConfigService;
    
    private static final String BILL_TYPE_CODE = "purchase_order";

    @Override
    public Page<PurchaseOrder> pageQuery(Page<PurchaseOrder> page, PurchaseOrder query) {
        LambdaQueryWrapper<PurchaseOrder> wrapper = new LambdaQueryWrapper<>();
        if (query != null) {
            if (StringUtils.hasText(query.getOrderNo())) {
                wrapper.like(PurchaseOrder::getOrderNo, query.getOrderNo());
            }
            if (query.getSupplierId() != null) {
                wrapper.eq(PurchaseOrder::getSupplierId, query.getSupplierId());
            }
            if (StringUtils.hasText(query.getOrderStatus())) {
                wrapper.eq(PurchaseOrder::getOrderStatus, query.getOrderStatus());
            }
            if (StringUtils.hasText(query.getApprovalStatus())) {
                wrapper.eq(PurchaseOrder::getApprovalStatus, query.getApprovalStatus());
            }
        }
        
        if (!SecurityUtils.isAdmin()) {
            Long currentUserId = SecurityUtils.getCurrentUserId();
            if (currentUserId != null) {
                wrapper.eq(PurchaseOrder::getCreateBy, currentUserId);
            }
        }
        
        wrapper.orderByDesc(PurchaseOrder::getCreateTime);
        return this.page(page, wrapper);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public PurchaseOrder createOrder(PurchaseOrder order, List<PurchaseOrderItem> items) {
        log.info("创建采购订单: order={}, items={}", order, items);
        
        if (CollectionUtils.isEmpty(items)) {
            throw new BusinessException("采购订单明细不能为空");
        }

        order.setOrderNo(generateOrderNo());
        order.setOrderStatus("DRAFT");
        order.setApprovalStatus("PENDING");

        BigDecimal totalAmount = BigDecimal.ZERO;
        for (PurchaseOrderItem item : items) {
            BigDecimal amount = item.getUnitPrice().multiply(BigDecimal.valueOf(item.getQuantity()));
            item.setAmount(amount);
            totalAmount = totalAmount.add(amount);
        }
        order.setTotalAmount(totalAmount);

        this.save(order);

        for (PurchaseOrderItem item : items) {
            item.setPurchaseOrderId(order.getId());
            orderItemMapper.insert(item);
        }

        log.info("采购订单创建成功: orderId={}", order.getId());
        return order;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public PurchaseOrder updateOrder(PurchaseOrder order, List<PurchaseOrderItem> items) {
        log.info("更新采购订单: orderId={}", order.getId());

        PurchaseOrder existOrder = this.getById(order.getId());
        if (existOrder == null) {
            throw new BusinessException("采购订单不存在");
        }

        if (!"DRAFT".equals(existOrder.getOrderStatus())) {
            throw new BusinessException("只能修改草稿状态的订单");
        }

        orderItemMapper.delete(new LambdaQueryWrapper<PurchaseOrderItem>()
                .eq(PurchaseOrderItem::getPurchaseOrderId, order.getId()));

        if (!CollectionUtils.isEmpty(items)) {
            BigDecimal totalAmount = BigDecimal.ZERO;
            for (PurchaseOrderItem item : items) {
                BigDecimal amount = item.getUnitPrice().multiply(BigDecimal.valueOf(item.getQuantity()));
                item.setAmount(amount);
                totalAmount = totalAmount.add(amount);
                item.setPurchaseOrderId(order.getId());
                orderItemMapper.insert(item);
            }
            order.setTotalAmount(totalAmount);
        }

        this.updateById(order);
        return order;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public String submitOrder(Long orderId) {
        log.info("提交采购订单: orderId={}", orderId);

        PurchaseOrder order = this.getById(orderId);
        if (order == null) {
            throw new BusinessException("采购订单不存在");
        }

        if (!"DRAFT".equals(order.getOrderStatus())) {
            throw new BusinessException("订单已提交或已审批");
        }

        Map<String, Object> variables = new HashMap<>();
        variables.put("orderId", orderId);
        variables.put("orderNo", order.getOrderNo());
        variables.put("supplierId", order.getSupplierId());
        variables.put("totalAmount", order.getTotalAmount());
        variables.put("initiator", order.getCreateBy());
        
        Map<String, Object> flowConfig = billFlowConfigService.getFlowConfigByBillTypeCode(
                BILL_TYPE_CODE, 
                variables
        );
        
        if (flowConfig != null) {
            @SuppressWarnings("unchecked")
            List<Map<String, Object>> nodes = (List<Map<String, Object>>) flowConfig.get("nodes");
            if (nodes != null) {
                for (int i = 0; i < nodes.size() && i < 3; i++) {
                    Map<String, Object> nodeConfig = nodes.get(i);
                    int nodeIndex = i + 1;
                    String nodeKey = "node" + nodeIndex;
                    
                    variables.put(nodeKey + "_enabled", true);
                    
                    @SuppressWarnings("unchecked")
                    List<String> assigneeUserIds = (List<String>) nodeConfig.get("assigneeUserIds");
                    if (assigneeUserIds != null && !assigneeUserIds.isEmpty()) {
                        if (assigneeUserIds.size() == 1) {
                            variables.put(nodeKey + "Task_assignee", assigneeUserIds.get(0));
                        } else {
                            variables.put(nodeKey + "Task_candidateUsers", assigneeUserIds);
                        }
                    }
                    
                    @SuppressWarnings("unchecked")
                    List<String> assigneeGroupIds = (List<String>) nodeConfig.get("assigneeGroupIds");
                    if (assigneeGroupIds != null && !assigneeGroupIds.isEmpty()) {
                        variables.put(nodeKey + "Task_candidateGroups", assigneeGroupIds);
                    }
                }
                
                for (int i = nodes.size(); i < 3; i++) {
                    int nodeIndex = i + 1;
                    variables.put("node" + nodeIndex + "_enabled", false);
                }
            } else {
                variables.put("node1_enabled", false);
                variables.put("node2_enabled", false);
                variables.put("node3_enabled", false);
            }
        } else {
            log.warn("未找到采购订单审批流配置，使用默认流程");
            variables.put("node1_enabled", false);
            variables.put("node2_enabled", false);
            variables.put("node3_enabled", false);
        }

        String flowCode = "purchase-order-approval";
        if (flowConfig != null && flowConfig.get("flow") != null) {
            Object flowObj = flowConfig.get("flow");
            if (flowObj instanceof Map) {
                @SuppressWarnings("unchecked")
                Map<String, Object> flowMap = (Map<String, Object>) flowObj;
                if (flowMap.get("flowCode") != null) {
                    flowCode = flowMap.get("flowCode").toString();
                }
            } else if (flowObj instanceof com.supplychain.entity.ApprovalFlow) {
                com.supplychain.entity.ApprovalFlow flow = (com.supplychain.entity.ApprovalFlow) flowObj;
                flowCode = flow.getFlowCode();
            }
        }
        
        log.info("启动审批流程，流程编码: {}, 流程变量: {}", flowCode, variables);
        
        String initiator = order.getCreateBy() != null ? String.valueOf(order.getCreateBy()) : null;
        String processInstanceId = workflowService.startProcessInstance(
                flowCode,
                order.getOrderNo(),
                initiator,
                variables
        );

        order.setOrderStatus("SUBMITTED");
        order.setApprovalStatus("APPROVING");
        order.setProcessInstanceId(processInstanceId);
        this.updateById(order);

        log.info("采购订单提交成功: orderId={}, processInstanceId={}", orderId, processInstanceId);
        return processInstanceId;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void approveOrder(String taskId, String comment, boolean approved) {
        log.info("审批采购订单: taskId={}, approved={}", taskId, approved);

        if (approved) {
            workflowService.approve(taskId, comment);
        } else {
            workflowService.reject(taskId, comment);
        }
    }

    @Override
    public PurchaseOrder getOrderDetail(Long orderId) {
        return this.getById(orderId);
    }

    @Override
    public List<PurchaseOrderItem> getOrderItems(Long orderId) {
        return orderItemMapper.selectList(
                new LambdaQueryWrapper<PurchaseOrderItem>()
                        .eq(PurchaseOrderItem::getPurchaseOrderId, orderId)
        );
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteOrder(Long orderId) {
        log.info("删除采购订单: orderId={}", orderId);

        PurchaseOrder order = this.getById(orderId);
        if (order == null) {
            throw new BusinessException("采购订单不存在");
        }

        if (!"DRAFT".equals(order.getOrderStatus())) {
            throw new BusinessException("只能删除草稿状态的订单");
        }

        orderItemMapper.delete(new LambdaQueryWrapper<PurchaseOrderItem>()
                .eq(PurchaseOrderItem::getPurchaseOrderId, orderId));

        this.removeById(orderId);
        log.info("采购订单删除成功: orderId={}", orderId);
    }

    @Override
    public String generateOrderNo() {
        String dateStr = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        LambdaQueryWrapper<PurchaseOrder> wrapper = new LambdaQueryWrapper<>();
        wrapper.likeRight(PurchaseOrder::getOrderNo, "PO" + dateStr);
        wrapper.orderByDesc(PurchaseOrder::getOrderNo);
        wrapper.last("LIMIT 1");
        
        PurchaseOrder lastOrder = this.getOne(wrapper);
        int sequence = 1;
        
        if (lastOrder != null && StringUtils.hasText(lastOrder.getOrderNo())) {
            String lastNo = lastOrder.getOrderNo();
            String seqStr = lastNo.substring(lastNo.length() - 4);
            sequence = Integer.parseInt(seqStr) + 1;
        }
        
        return String.format("PO%s%04d", dateStr, sequence);
    }
}
