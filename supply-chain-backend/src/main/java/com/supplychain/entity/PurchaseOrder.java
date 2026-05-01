package com.supplychain.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("sc_purchase_order")
public class PurchaseOrder extends BaseEntity {

    private String orderNo;
    private Long supplierId;
    private LocalDateTime orderDate;
    private LocalDateTime expectedDeliveryDate;
    private BigDecimal totalAmount;
    private String orderStatus;
    private String approvalStatus;
    private String processInstanceId;
    private Long flowId;
    private String flowCode;
    private String remark;
}
