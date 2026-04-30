package com.supplychain.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("sc_material")
public class Material extends BaseEntity {

    private String materialCode;
    private String materialName;
    private String specification;
    private String unit;
    private Long categoryId;
    private Long supplierId;
    private BigDecimal unitPrice;
    private Integer safetyStock;
    private Integer status;
    private String remark;
}
