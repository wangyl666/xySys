<template>
  <div class="user-container">
    <el-card>
      <template slot="header">
        <div class="header-container">
          <span>用户管理</span>
          <el-button type="primary">
            <i class="el-icon-plus"></i> 新增用户
          </el-button>
        </div>
      </template>
      <el-table :data="userList" style="width: 100%" border>
        <el-table-column prop="id" label="ID" width="80" />
        <el-table-column prop="username" label="用户名" min-width="120" />
        <el-table-column prop="nickname" label="昵称" min-width="120" />
        <el-table-column prop="email" label="邮箱" min-width="150" />
        <el-table-column prop="phone" label="手机号" min-width="120" />
        <el-table-column prop="status" label="状态" width="100">
          <template slot-scope="scope">
            <el-tag :type="scope.row.status === 1 ? 'success' : 'danger'">
              {{ scope.row.status === 1 ? '启用' : '禁用' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="200">
          <template slot-scope="scope">
            <el-button type="text" size="small">编辑</el-button>
            <el-button type="text" size="small" style="color: #f56c6c;">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
      <el-pagination
        style="margin-top: 20px; text-align: right;"
        :current-page="1"
        :page-sizes="[10, 20, 50]"
        :page-size="10"
        layout="total, sizes, prev, pager, next, jumper"
        :total="0"
      />
    </el-card>
  </div>
</template>

<script>
export default {
  name: 'User',
  data() {
    return {
      userList: [
        { id: 1, username: 'admin', nickname: '系统管理员', email: 'admin@example.com', phone: '13800138000', status: 1 },
        { id: 2, username: 'manager', nickname: '采购经理', email: 'manager@example.com', phone: '13800138001', status: 1 },
        { id: 3, username: 'staff', nickname: '采购专员', email: 'staff@example.com', phone: '13800138002', status: 1 },
        { id: 4, username: 'finance', nickname: '财务专员', email: 'finance@example.com', phone: '13800138003', status: 1 }
      ]
    }
  }
}
</script>

<style lang="scss" scoped>
.user-container {
  padding: 0;

  .header-container {
    display: flex;
    justify-content: space-between;
    align-items: center;
  }
}
</style>
