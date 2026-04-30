<template>
  <div class="supplier-container">
    <el-card>
      <template slot="header">
        <div class="header-container">
          <span>供应商管理</span>
          <el-button type="primary">
            <i class="el-icon-plus"></i> 新增供应商
          </el-button>
        </div>
      </template>
      <el-form :inline="true" :model="queryForm" class="demo-form-inline">
        <el-form-item label="供应商名称">
          <el-input v-model="queryForm.supplierName" placeholder="请输入供应商名称" clearable />
        </el-form-item>
        <el-form-item>
          <el-button type="primary">查询</el-button>
          <el-button>重置</el-button>
        </el-form-item>
      </el-form>
      <el-table :data="supplierList" style="width: 100%" border>
        <el-table-column prop="supplierCode" label="供应商编码" min-width="120" />
        <el-table-column prop="supplierName" label="供应商名称" min-width="180" />
        <el-table-column prop="contactPerson" label="联系人" min-width="100" />
        <el-table-column prop="phone" label="联系电话" min-width="120" />
        <el-table-column prop="email" label="邮箱" min-width="150" />
        <el-table-column prop="address" label="地址" min-width="200" show-overflow-tooltip />
        <el-table-column prop="status" label="状态" width="80">
          <template slot-scope="scope">
            <el-tag :type="scope.row.status === 1 ? 'success' : 'danger'">
              {{ scope.row.status === 1 ? '启用' : '禁用' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="200">
          <template slot-scope="scope">
            <el-button type="text" size="small">编辑</el-button>
            <el-button type="text" size="small">查看</el-button>
            <el-button type="text" size="small" style="color: #f56c6c;">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>
  </div>
</template>

<script>
export default {
  name: 'Supplier',
  data() {
    return {
      queryForm: {
        supplierName: ''
      },
      supplierList: [
        { 
          supplierCode: 'SUP001', 
          supplierName: '北京供应商有限公司', 
          contactPerson: '张三', 
          phone: '13900139001', 
          email: 'zhangsan@example.com', 
          address: '北京市朝阳区建国路88号',
          status: 1,
          remark: '主要物料供应商'
        },
        { 
          supplierCode: 'SUP002', 
          supplierName: '上海供应链科技有限公司', 
          contactPerson: '李四', 
          phone: '13900139002', 
          email: 'lisi@example.com', 
          address: '上海市浦东新区张江高科技园区',
          status: 1,
          remark: '高端物料供应商'
        },
        { 
          supplierCode: 'SUP003', 
          supplierName: '广州物资贸易有限公司', 
          contactPerson: '王五', 
          phone: '13900139003', 
          email: 'wangwu@example.com', 
          address: '广州市天河区珠江新城',
          status: 1,
          remark: '常规物料供应商'
        }
      ]
    }
  }
}
</script>

<style lang="scss" scoped>
.supplier-container {
  padding: 0;

  .header-container {
    display: flex;
    justify-content: space-between;
    align-items: center;
  }
}
</style>
