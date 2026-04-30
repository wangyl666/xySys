-- 创建数据库
CREATE DATABASE IF NOT EXISTS supply_chain DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

USE supply_chain;

-- 系统角色表
CREATE TABLE IF NOT EXISTS sys_role (
    id BIGINT(20) NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    role_name VARCHAR(50) NOT NULL COMMENT '角色名称',
    role_code VARCHAR(50) NOT NULL COMMENT '角色编码',
    sort INT(11) DEFAULT 0 COMMENT '排序',
    status INT(1) DEFAULT 1 COMMENT '状态：1-启用 0-禁用',
    remark VARCHAR(500) DEFAULT NULL COMMENT '备注',
    create_by BIGINT(20) DEFAULT NULL COMMENT '创建人',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_by BIGINT(20) DEFAULT NULL COMMENT '更新人',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    deleted INT(1) DEFAULT 0 COMMENT '逻辑删除：0-未删除 1-已删除',
    PRIMARY KEY (id),
    UNIQUE KEY uk_role_code (role_code)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='系统角色表';

-- 系统用户表
CREATE TABLE IF NOT EXISTS sys_user (
    id BIGINT(20) NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    username VARCHAR(50) NOT NULL COMMENT '用户名',
    password VARCHAR(200) NOT NULL COMMENT '密码',
    nickname VARCHAR(50) DEFAULT NULL COMMENT '昵称',
    avatar VARCHAR(500) DEFAULT NULL COMMENT '头像',
    email VARCHAR(100) DEFAULT NULL COMMENT '邮箱',
    phone VARCHAR(20) DEFAULT NULL COMMENT '手机号',
    role_id BIGINT(20) DEFAULT NULL COMMENT '角色ID',
    dept_id BIGINT(20) DEFAULT NULL COMMENT '部门ID',
    status INT(1) DEFAULT 1 COMMENT '状态：1-启用 0-禁用',
    remark VARCHAR(500) DEFAULT NULL COMMENT '备注',
    create_by BIGINT(20) DEFAULT NULL COMMENT '创建人',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_by BIGINT(20) DEFAULT NULL COMMENT '更新人',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    deleted INT(1) DEFAULT 0 COMMENT '逻辑删除：0-未删除 1-已删除',
    PRIMARY KEY (id),
    UNIQUE KEY uk_username (username)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='系统用户表';

-- 供应商表
CREATE TABLE IF NOT EXISTS sc_supplier (
    id BIGINT(20) NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    supplier_code VARCHAR(50) NOT NULL COMMENT '供应商编码',
    supplier_name VARCHAR(100) NOT NULL COMMENT '供应商名称',
    contact_person VARCHAR(50) DEFAULT NULL COMMENT '联系人',
    phone VARCHAR(20) DEFAULT NULL COMMENT '联系电话',
    email VARCHAR(100) DEFAULT NULL COMMENT '邮箱',
    address VARCHAR(200) DEFAULT NULL COMMENT '地址',
    bank_name VARCHAR(100) DEFAULT NULL COMMENT '开户银行',
    bank_account VARCHAR(50) DEFAULT NULL COMMENT '银行账号',
    status INT(1) DEFAULT 1 COMMENT '状态：1-启用 0-禁用',
    remark VARCHAR(500) DEFAULT NULL COMMENT '备注',
    create_by BIGINT(20) DEFAULT NULL COMMENT '创建人',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_by BIGINT(20) DEFAULT NULL COMMENT '更新人',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    deleted INT(1) DEFAULT 0 COMMENT '逻辑删除：0-未删除 1-已删除',
    PRIMARY KEY (id),
    UNIQUE KEY uk_supplier_code (supplier_code)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='供应商表';

-- 物料分类表
CREATE TABLE IF NOT EXISTS sc_material_category (
    id BIGINT(20) NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    category_code VARCHAR(50) NOT NULL COMMENT '分类编码',
    category_name VARCHAR(100) NOT NULL COMMENT '分类名称',
    parent_id BIGINT(20) DEFAULT 0 COMMENT '父分类ID',
    sort INT(11) DEFAULT 0 COMMENT '排序',
    status INT(1) DEFAULT 1 COMMENT '状态：1-启用 0-禁用',
    remark VARCHAR(500) DEFAULT NULL COMMENT '备注',
    create_by BIGINT(20) DEFAULT NULL COMMENT '创建人',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_by BIGINT(20) DEFAULT NULL COMMENT '更新人',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    deleted INT(1) DEFAULT 0 COMMENT '逻辑删除：0-未删除 1-已删除',
    PRIMARY KEY (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='物料分类表';

-- 物料表
CREATE TABLE IF NOT EXISTS sc_material (
    id BIGINT(20) NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    material_code VARCHAR(50) NOT NULL COMMENT '物料编码',
    material_name VARCHAR(100) NOT NULL COMMENT '物料名称',
    specification VARCHAR(200) DEFAULT NULL COMMENT '规格型号',
    unit VARCHAR(20) DEFAULT NULL COMMENT '计量单位',
    category_id BIGINT(20) DEFAULT NULL COMMENT '分类ID',
    supplier_id BIGINT(20) DEFAULT NULL COMMENT '默认供应商ID',
    unit_price DECIMAL(18,2) DEFAULT 0.00 COMMENT '单价',
    safety_stock INT(11) DEFAULT 0 COMMENT '安全库存',
    status INT(1) DEFAULT 1 COMMENT '状态：1-启用 0-禁用',
    remark VARCHAR(500) DEFAULT NULL COMMENT '备注',
    create_by BIGINT(20) DEFAULT NULL COMMENT '创建人',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_by BIGINT(20) DEFAULT NULL COMMENT '更新人',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    deleted INT(1) DEFAULT 0 COMMENT '逻辑删除：0-未删除 1-已删除',
    PRIMARY KEY (id),
    UNIQUE KEY uk_material_code (material_code)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='物料表';

-- 采购订单表
CREATE TABLE IF NOT EXISTS sc_purchase_order (
    id BIGINT(20) NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    order_no VARCHAR(50) NOT NULL COMMENT '订单编号',
    supplier_id BIGINT(20) NOT NULL COMMENT '供应商ID',
    order_date DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '订单日期',
    expected_delivery_date DATETIME DEFAULT NULL COMMENT '预计交货日期',
    total_amount DECIMAL(18,2) DEFAULT 0.00 COMMENT '订单总金额',
    order_status VARCHAR(20) DEFAULT 'DRAFT' COMMENT '订单状态：DRAFT-草稿 SUBMITTED-已提交 APPROVING-审批中 APPROVED-已审批 REJECTED-已拒绝',
    approval_status VARCHAR(20) DEFAULT 'PENDING' COMMENT '审批状态：PENDING-待审批 APPROVED-已审批 REJECTED-已拒绝',
    process_instance_id VARCHAR(100) DEFAULT NULL COMMENT '流程实例ID',
    remark VARCHAR(500) DEFAULT NULL COMMENT '备注',
    create_by BIGINT(20) DEFAULT NULL COMMENT '创建人',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_by BIGINT(20) DEFAULT NULL COMMENT '更新人',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    deleted INT(1) DEFAULT 0 COMMENT '逻辑删除：0-未删除 1-已删除',
    PRIMARY KEY (id),
    UNIQUE KEY uk_order_no (order_no)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='采购订单表';

-- 采购订单明细表
CREATE TABLE IF NOT EXISTS sc_purchase_order_item (
    id BIGINT(20) NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    purchase_order_id BIGINT(20) NOT NULL COMMENT '采购订单ID',
    material_id BIGINT(20) NOT NULL COMMENT '物料ID',
    quantity INT(11) NOT NULL COMMENT '数量',
    unit_price DECIMAL(18,2) DEFAULT 0.00 COMMENT '单价',
    amount DECIMAL(18,2) DEFAULT 0.00 COMMENT '金额',
    remark VARCHAR(500) DEFAULT NULL COMMENT '备注',
    create_by BIGINT(20) DEFAULT NULL COMMENT '创建人',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_by BIGINT(20) DEFAULT NULL COMMENT '更新人',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    deleted INT(1) DEFAULT 0 COMMENT '逻辑删除：0-未删除 1-已删除',
    PRIMARY KEY (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='采购订单明细表';

-- 初始化角色数据
INSERT INTO sys_role (role_name, role_code, sort, status, remark) VALUES
('超级管理员', 'SUPER_ADMIN', 1, 1, '系统超级管理员，拥有所有权限'),
('采购经理', 'PURCHASE_MANAGER', 2, 1, '采购部门经理'),
('采购专员', 'PURCHASE_STAFF', 3, 1, '采购部门专员'),
('财务专员', 'FINANCE_STAFF', 4, 1, '财务部门专员');

-- 初始化用户数据（密码为123456，使用BCrypt加密）
INSERT INTO sys_user (username, password, nickname, phone, role_id, status, remark) VALUES
('admin', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iAt6Z5E', '系统管理员', '13800138000', 1, 1, '系统管理员账号'),
('manager', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iAt6Z5E', '采购经理', '13800138001', 2, 1, '采购经理账号'),
('staff', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iAt6Z5E', '采购专员', '13800138002', 3, 1, '采购专员账号'),
('finance', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iAt6Z5E', '财务专员', '13800138003', 4, 1, '财务专员账号');

-- 初始化供应商数据
INSERT INTO sc_supplier (supplier_code, supplier_name, contact_person, phone, email, address, status, remark) VALUES
('SUP001', '北京供应商有限公司', '张三', '13900139001', 'zhangsan@example.com', '北京市朝阳区建国路88号', 1, '主要物料供应商'),
('SUP002', '上海供应链科技有限公司', '李四', '13900139002', 'lisi@example.com', '上海市浦东新区张江高科技园区', 1, '高端物料供应商'),
('SUP003', '广州物资贸易有限公司', '王五', '13900139003', 'wangwu@example.com', '广州市天河区珠江新城', 1, '常规物料供应商');

-- 初始化物料分类数据
INSERT INTO sc_material_category (category_code, category_name, parent_id, sort, status, remark) VALUES
('CAT001', '电子元器件', 0, 1, 1, '电子元器件类物料'),
('CAT002', '机械设备', 0, 2, 1, '机械设备类物料'),
('CAT003', '办公用品', 0, 3, 1, '办公用品类物料'),
('CAT001001', '集成电路', 1, 1, 1, '集成电路类物料'),
('CAT001002', '被动元件', 1, 2, 1, '被动元件类物料');

-- 初始化物料数据
INSERT INTO sc_material (material_code, material_name, specification, unit, category_id, supplier_id, unit_price, safety_stock, status, remark) VALUES
('MAT001', 'STM32F103芯片', 'STM32F103ZET6', '个', 4, 1, 25.50, 100, 1, '主控芯片'),
('MAT002', '10K电阻', '0805 10K ±5%', '个', 5, 1, 0.05, 1000, 1, '贴片电阻'),
('MAT003', '104电容', '0805 104 50V', '个', 5, 2, 0.10, 500, 1, '贴片电容'),
('MAT004', '激光打印机', 'HP M403dn', '台', 3, 3, 2500.00, 5, 1, '办公打印机'),
('MAT005', '工业电机', 'Y132S-4 5.5KW', '台', 2, 2, 8500.00, 10, 1, '三相异步电机');
