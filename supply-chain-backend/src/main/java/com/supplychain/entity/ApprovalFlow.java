package com.supplychain.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("sc_approval_flow")
public class ApprovalFlow extends BaseEntity {

    private String flowName;
    private String flowCode;
    private String flowType;
    private String processKey;
    private String description;
    private Integer status;
    private Integer version;
}
