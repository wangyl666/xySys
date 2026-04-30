package com.supplychain.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("sc_purchase_order_item")
public class PurchaseOrderItem extends BaseEntity {

    private Long purchaseOrderId;
    private Long materialId;
    private Integer quantity;
    private BigDecimal unitPrice;
    private BigDecimal amount;
    private String remark;
}
