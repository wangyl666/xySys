package com.supplychain.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("sc_bill_type")
public class BillType extends BaseEntity {

    private String billTypeCode;
    private String billTypeName;
    private String description;
    private Integer status;
    private Integer sortOrder;
    private String moduleName;
}
