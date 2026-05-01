package com.supplychain.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("sc_bill_flow_config")
public class BillFlowConfig extends BaseEntity {

    private Long billTypeId;
    private String billTypeCode;
    private Long flowId;
    private String flowCode;
    private Integer status;
    private Integer isDefault;
    private String description;
}
