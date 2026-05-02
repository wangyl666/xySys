package com.supplychain.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.supplychain.common.Result;
import com.supplychain.entity.PurchaseOrder;
import com.supplychain.entity.PurchaseOrderItem;
import com.supplychain.service.PurchaseOrderService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@Api(tags = "采购订单管理")
@RestController
@RequestMapping("/api/purchase-order")
@RequiredArgsConstructor
public class PurchaseOrderController {

    private final PurchaseOrderService purchaseOrderService;

    @ApiOperation("分页查询采购订单")
    @GetMapping("/page")
    public Result<Page<PurchaseOrder>> pageQuery(
            @ApiParam("当前页") @RequestParam(defaultValue = "1") Long current,
            @ApiParam("每页大小") @RequestParam(defaultValue = "10") Long size,
            PurchaseOrder query) {
        Page<PurchaseOrder> page = new Page<>(current, size);
        Page<PurchaseOrder> result = purchaseOrderService.pageQuery(page, query);
        return Result.success(result);
    }

    @ApiOperation("获取采购订单详情")
    @GetMapping("/{id}")
    public Result<PurchaseOrder> getById(
            @ApiParam("订单ID") @PathVariable Long id) {
        PurchaseOrder order = purchaseOrderService.getOrderDetail(id);
        return Result.success(order);
    }

    @ApiOperation("获取采购订单明细")
    @GetMapping("/{id}/items")
    public Result<List<PurchaseOrderItem>> getOrderItems(
            @ApiParam("订单ID") @PathVariable Long id) {
        List<PurchaseOrderItem> items = purchaseOrderService.getOrderItems(id);
        return Result.success(items);
    }

    @ApiOperation("创建采购订单")
    @PostMapping
    public Result<PurchaseOrder> create(
            @RequestBody Map<String, Object> request) {
        PurchaseOrder order = convertToPurchaseOrder(request);
        @SuppressWarnings("unchecked")
        List<Map<String, Object>> itemsMap = (List<Map<String, Object>>) request.get("items");
        List<PurchaseOrderItem> items = convertToPurchaseOrderItemList(itemsMap);
        
        PurchaseOrder result = purchaseOrderService.createOrder(order, items);
        return Result.success(result);
    }

    @ApiOperation("更新采购订单")
    @PutMapping("/{id}")
    public Result<PurchaseOrder> update(
            @ApiParam("订单ID") @PathVariable Long id,
            @RequestBody Map<String, Object> request) {
        PurchaseOrder order = convertToPurchaseOrder(request);
        order.setId(id);
        
        @SuppressWarnings("unchecked")
        List<Map<String, Object>> itemsMap = (List<Map<String, Object>>) request.get("items");
        List<PurchaseOrderItem> items = convertToPurchaseOrderItemList(itemsMap);
        
        PurchaseOrder result = purchaseOrderService.updateOrder(order, items);
        return Result.success(result);
    }

    @ApiOperation("提交采购订单")
    @PostMapping("/{id}/submit")
    public Result<String> submit(
            @ApiParam("订单ID") @PathVariable Long id,
            @ApiParam("审批流配置ID（可选，不传则使用默认配置）") 
            @RequestParam(required = false) Long flowConfigId) {
        String processInstanceId;
        if (flowConfigId != null) {
            processInstanceId = purchaseOrderService.submitOrderWithFlowConfig(id, flowConfigId);
        } else {
            processInstanceId = purchaseOrderService.submitOrder(id);
        }
        return Result.success(processInstanceId);
    }

    @ApiOperation("审批采购订单")
    @PostMapping("/approve")
    public Result<Void> approve(
            @ApiParam("任务ID") @RequestParam String taskId,
            @ApiParam("审批意见") @RequestParam(required = false) String comment,
            @ApiParam("是否通过") @RequestParam Boolean approved) {
        purchaseOrderService.approveOrder(taskId, comment, approved);
        return Result.success();
    }

    @ApiOperation("删除采购订单")
    @DeleteMapping("/{id}")
    public Result<Void> delete(
            @ApiParam("订单ID") @PathVariable Long id) {
        purchaseOrderService.deleteOrder(id);
        return Result.success();
    }

    private PurchaseOrder convertToPurchaseOrder(Map<String, Object> map) {
        PurchaseOrder order = new PurchaseOrder();
        if (map.get("supplierId") != null) {
            order.setSupplierId(Long.parseLong(map.get("supplierId").toString()));
        }
        if (map.get("expectedDeliveryDate") != null) {
            String dateStr = map.get("expectedDeliveryDate").toString();
            java.time.LocalDateTime dateTime = parseDateTime(dateStr);
            order.setExpectedDeliveryDate(dateTime);
        }
        if (map.get("remark") != null) {
            order.setRemark(map.get("remark").toString());
        }
        return order;
    }

    private java.time.LocalDateTime parseDateTime(String dateStr) {
        if (dateStr == null || dateStr.isEmpty()) {
            return null;
        }
        try {
            if (dateStr.endsWith("Z") || dateStr.contains("+") || dateStr.contains("-") && dateStr.indexOf('-') != 4) {
                return java.time.ZonedDateTime.parse(dateStr, java.time.format.DateTimeFormatter.ISO_DATE_TIME)
                        .withZoneSameInstant(java.time.ZoneId.systemDefault())
                        .toLocalDateTime();
            }
            return java.time.LocalDateTime.parse(dateStr);
        } catch (Exception e) {
            throw new RuntimeException("日期格式解析失败: " + dateStr, e);
        }
    }

    private List<PurchaseOrderItem> convertToPurchaseOrderItemList(List<Map<String, Object>> itemsMap) {
        if (itemsMap == null) {
            return java.util.Collections.emptyList();
        }
        List<PurchaseOrderItem> items = new java.util.ArrayList<>();
        for (Map<String, Object> itemMap : itemsMap) {
            PurchaseOrderItem item = new PurchaseOrderItem();
            if (itemMap.get("materialId") != null) {
                item.setMaterialId(Long.parseLong(itemMap.get("materialId").toString()));
            }
            if (itemMap.get("quantity") != null) {
                item.setQuantity(Integer.parseInt(itemMap.get("quantity").toString()));
            }
            if (itemMap.get("unitPrice") != null) {
                item.setUnitPrice(new java.math.BigDecimal(itemMap.get("unitPrice").toString()));
            }
            if (itemMap.get("remark") != null) {
                item.setRemark(itemMap.get("remark").toString());
            }
            items.add(item);
        }
        return items;
    }
}
