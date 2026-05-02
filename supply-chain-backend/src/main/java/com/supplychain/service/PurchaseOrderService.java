package com.supplychain.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.supplychain.entity.PurchaseOrder;
import com.supplychain.entity.PurchaseOrderItem;

import java.util.List;

public interface PurchaseOrderService extends IService<PurchaseOrder> {

    /**
     * 分页查询采购订单
     */
    Page<PurchaseOrder> pageQuery(Page<PurchaseOrder> page, PurchaseOrder query);

    /**
     * 创建采购订单
     */
    PurchaseOrder createOrder(PurchaseOrder order, List<PurchaseOrderItem> items);

    /**
     * 更新采购订单
     */
    PurchaseOrder updateOrder(PurchaseOrder order, List<PurchaseOrderItem> items);

    /**
     * 提交采购订单（启动审批流程）
     */
    String submitOrder(Long orderId);
    
    /**
     * 提交采购订单（指定审批流配置ID）
     */
    String submitOrderWithFlowConfig(Long orderId, Long flowConfigId);

    /**
     * 审批采购订单
     */
    void approveOrder(String taskId, String comment, boolean approved);

    /**
     * 获取采购订单详情
     */
    PurchaseOrder getOrderDetail(Long orderId);

    /**
     * 获取采购订单明细
     */
    List<PurchaseOrderItem> getOrderItems(Long orderId);

    /**
     * 删除采购订单
     */
    void deleteOrder(Long orderId);

    /**
     * 生成订单编号
     */
    String generateOrderNo();
}
