<template>
  <div class="material-container">
    <el-card>
      <template slot="header">
        <div class="header-container">
          <span>物料管理</span>
          <el-button type="primary">
            <i class="el-icon-plus"></i> 新增物料
          </el-button>
        </div>
      </template>
      <el-form :inline="true" :model="queryForm" class="demo-form-inline">
        <el-form-item label="物料名称">
          <el-input v-model="queryForm.materialName" placeholder="请输入物料名称" clearable />
        </el-form-item>
        <el-form-item>
          <el-button type="primary">查询</el-button>
          <el-button>重置</el-button>
        </el-form-item>
      </el-form>
      <el-table :data="materialList" style="width: 100%" border>
        <el-table-column prop="materialCode" label="物料编码" min-width="120" />
        <el-table-column prop="materialName" label="物料名称" min-width="150" />
        <el-table-column prop="specification" label="规格型号" min-width="150" />
        <el-table-column prop="unit" label="单位" width="80" />
        <el-table-column prop="unitPrice" label="单价" width="120">
          <template slot-scope="scope">
            ¥{{ scope.row.unitPrice }}
          </template>
        </el-table-column>
        <el-table-column prop="safetyStock" label="安全库存" width="100" />
        <el-table-column prop="status" label="状态" width="80">
          <template slot-scope="scope">
            <el-tag :type="scope.row.status === 1 ? 'success' : 'danger'">
              {{ scope.row.status === 1 ? '启用' : '禁用' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="remark" label="备注" min-width="150" show-overflow-tooltip />
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
  name: 'Material',
  data() {
    return {
      queryForm: {
        materialName: ''
      },
      materialList: [
        { 
          materialCode: 'MAT001', 
          materialName: 'STM32F103芯片', 
          specification: 'STM32F103ZET6', 
          unit: '个', 
          unitPrice: 25.50,
          safetyStock: 100,
          status: 1,
          remark: '主控芯片'
        },
        { 
          materialCode: 'MAT002', 
          materialName: '10K电阻', 
          specification: '0805 10K ±5%', 
          unit: '个', 
          unitPrice: 0.05,
          safetyStock: 1000,
          status: 1,
          remark: '贴片电阻'
        },
        { 
          materialCode: 'MAT003', 
          materialName: '104电容', 
          specification: '0805 104 50V', 
          unit: '个', 
          unitPrice: 0.10,
          safetyStock: 500,
          status: 1,
          remark: '贴片电容'
        },
        { 
          materialCode: 'MAT004', 
          materialName: '激光打印机', 
          specification: 'HP M403dn', 
          unit: '台', 
          unitPrice: 2500.00,
          safetyStock: 5,
          status: 1,
          remark: '办公打印机'
        },
        { 
          materialCode: 'MAT005', 
          materialName: '工业电机', 
          specification: 'Y132S-4 5.5KW', 
          unit: '台', 
          unitPrice: 8500.00,
          safetyStock: 10,
          status: 1,
          remark: '三相异步电机'
        }
      ]
    }
  }
}
</script>

<style lang="scss" scoped>
.material-container {
  padding: 0;

  .header-container {
    display: flex;
    justify-content: space-between;
    align-items: center;
  }
}
</style>
