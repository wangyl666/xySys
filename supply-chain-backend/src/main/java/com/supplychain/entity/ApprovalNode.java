package com.supplychain.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("sc_approval_node")
public class ApprovalNode extends BaseEntity {

    private Long flowId;
    private String nodeCode;
    private String nodeName;
    private Integer nodeType;
    private Integer sortOrder;
    private String approvalType;
    private Integer approvalCount;
    private String conditionExpression;
    private String description;

    @TableField(exist = false)
    private List<ApprovalNodeAssignee> assignees;
}
