-- 审批流模板表
CREATE TABLE IF NOT EXISTS sc_approval_flow (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '主键ID',
    flow_name VARCHAR(100) NOT NULL COMMENT '流程名称',
    flow_code VARCHAR(100) NOT NULL COMMENT '流程编码',
    flow_type VARCHAR(50) COMMENT '流程类型',
    description VARCHAR(500) COMMENT '描述',
    status INT DEFAULT 0 COMMENT '状态：0-禁用，1-启用',
    version INT DEFAULT 1 COMMENT '版本号',
    create_by BIGINT COMMENT '创建人',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_by BIGINT COMMENT '更新人',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    deleted INT DEFAULT 0 COMMENT '逻辑删除：0-未删除，1-已删除',
    UNIQUE KEY uk_flow_code (flow_code)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='审批流模板表';

-- 审批节点表
CREATE TABLE IF NOT EXISTS sc_approval_node (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '主键ID',
    flow_id BIGINT NOT NULL COMMENT '审批流ID',
    node_code VARCHAR(100) NOT NULL COMMENT '节点编码',
    node_name VARCHAR(100) NOT NULL COMMENT '节点名称',
    node_type INT DEFAULT 1 COMMENT '节点类型：1-审批节点，2-或签节点，3-会签节点',
    sort_order INT DEFAULT 1 COMMENT '排序',
    approval_type VARCHAR(50) COMMENT '审批类型：USER-指定用户，ROLE-指定角色，DEPARTMENT-指定部门',
    approval_count INT DEFAULT 1 COMMENT '审批人数（会签时使用）',
    condition_expression VARCHAR(500) COMMENT '条件表达式',
    description VARCHAR(500) COMMENT '描述',
    create_by BIGINT COMMENT '创建人',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_by BIGINT COMMENT '更新人',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    deleted INT DEFAULT 0 COMMENT '逻辑删除：0-未删除，1-已删除',
    KEY idx_flow_id (flow_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='审批节点表';

-- 审批人配置表
CREATE TABLE IF NOT EXISTS sc_approval_node_assignee (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '主键ID',
    node_id BIGINT NOT NULL COMMENT '审批节点ID',
    flow_id BIGINT NOT NULL COMMENT '审批流ID',
    assignee_type VARCHAR(50) NOT NULL COMMENT '审批人类型：USER-用户，ROLE-角色，DEPARTMENT-部门',
    assignee_id BIGINT COMMENT '审批人ID',
    assignee_name VARCHAR(100) COMMENT '审批人名称',
    assignee_code VARCHAR(100) COMMENT '审批人编码',
    sort_order INT DEFAULT 1 COMMENT '排序',
    create_by BIGINT COMMENT '创建人',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_by BIGINT COMMENT '更新人',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    deleted INT DEFAULT 0 COMMENT '逻辑删除：0-未删除，1-已删除',
    KEY idx_node_id (node_id),
    KEY idx_flow_id (flow_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='审批人配置表';

-- 初始化采购订单审批流模板
INSERT INTO sc_approval_flow (flow_name, flow_code, flow_type, description, status, version, create_by, create_time)
VALUES ('采购订单审批流程', 'purchase-order-approval', 'PURCHASE_ORDER', '采购订单提交后的审批流程', 0, 1, 1, NOW());

-- 示例：添加审批节点（需要根据实际需求配置）
-- INSERT INTO sc_approval_node (flow_id, node_code, node_name, node_type, sort_order, approval_type, description)
-- VALUES (1, 'node1Task', '部门经理审批', 1, 1, 'ROLE', '金额<=10000时由部门经理审批');

-- INSERT INTO sc_approval_node (flow_id, node_code, node_name, node_type, sort_order, approval_type, condition_expression, description)
-- VALUES (1, 'node2Task', '财务审批', 1, 2, 'ROLE', 'totalAmount > 10000', '金额>10000时由财务审批');
