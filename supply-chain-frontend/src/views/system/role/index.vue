<template>
  <div class="role-container">
    <el-card>
      <template slot="header">
        <div class="header-container">
          <span>角色管理</span>
          <el-button type="primary">
            <i class="el-icon-plus"></i> 新增角色
          </el-button>
        </div>
      </template>
      <el-table :data="roleList" style="width: 100%" border>
        <el-table-column prop="id" label="ID" width="80" />
        <el-table-column prop="roleName" label="角色名称" min-width="150" />
        <el-table-column prop="roleCode" label="角色编码" min-width="150" />
        <el-table-column prop="sort" label="排序" width="80" />
        <el-table-column prop="status" label="状态" width="100">
          <template slot-scope="scope">
            <el-tag :type="scope.row.status === 1 ? 'success' : 'danger'">
              {{ scope.row.status === 1 ? '启用' : '禁用' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="remark" label="备注" min-width="200" />
        <el-table-column label="操作" width="200">
          <template slot-scope="scope">
            <el-button type="text" size="small">编辑</el-button>
            <el-button type="text" size="small">权限</el-button>
            <el-button type="text" size="small" style="color: #f56c6c;">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>
  </div>
</template>

<script>
export default {
  name: 'Role',
  data() {
    return {
      roleList: [
        { id: 1, roleName: '超级管理员', roleCode: 'SUPER_ADMIN', sort: 1, status: 1, remark: '系统超级管理员，拥有所有权限' },
        { id: 2, roleName: '采购经理', roleCode: 'PURCHASE_MANAGER', sort: 2, status: 1, remark: '采购部门经理' },
        { id: 3, roleName: '采购专员', roleCode: 'PURCHASE_STAFF', sort: 3, status: 1, remark: '采购部门专员' },
        { id: 4, roleName: '财务专员', roleCode: 'FINANCE_STAFF', sort: 4, status: 1, remark: '财务部门专员' }
      ]
    }
  }
}
</script>

<style lang="scss" scoped>
.role-container {
  padding: 0;

  .header-container {
    display: flex;
    justify-content: space-between;
    align-items: center;
  }
}
</style>
