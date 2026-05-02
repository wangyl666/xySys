package com.supplychain.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("sc_approval_node_assignee")
public class ApprovalNodeAssignee extends BaseEntity {

    private Long nodeId;
    private Long flowId;
    private String assigneeType;
    private Long assigneeId;
    private String assigneeName;
    private String assigneeCode;
    private Integer sortOrder;
}
