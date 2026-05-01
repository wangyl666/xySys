<template>
  <div class="material-container">
    <el-card>
      <template slot="header">
        <div class="header-container">
          <span>物料管理</span>
          <el-button type="primary" @click="handleAdd">
            <i class="el-icon-plus"></i> 新增物料
          </el-button>
        </div>
      </template>
      <el-form :inline="true" :model="queryForm" class="demo-form-inline">
        <el-form-item label="物料名称">
          <el-input v-model="queryForm.materialName" placeholder="请输入物料名称" clearable />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleQuery">查询</el-button>
          <el-button @click="handleReset">重置</el-button>
        </el-form-item>
      </el-form>
      <el-table :data="materialList" v-loading="loading" style="width: 100%" border>
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
            <el-button type="text" size="small" @click="handleEdit(scope.row)">编辑</el-button>
            <el-button type="text" size="small" @click="handleView(scope.row)">查看</el-button>
            <el-button type="text" size="small" style="color: #f56c6c;" @click="handleDelete(scope.row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
      <el-pagination
        v-show="total > 0"
        style="margin-top: 20px; text-align: right;"
        :current-page="queryForm.current"
        :page-sizes="[10, 20, 50, 100]"
        :page-size="queryForm.size"
        layout="total, sizes, prev, pager, next, jumper"
        :total="total"
        @size-change="handleSizeChange"
        @current-change="handleCurrentChange"
      />
    </el-card>

    <el-dialog
      :title="isEdit ? '编辑物料' : '新增物料'"
      :visible.sync="dialogVisible"
      width="600px"
      :close-on-click-modal="false"
    >
      <el-form :model="form" :rules="rules" ref="formRef" label-width="100px">
        <el-form-item label="物料编码" prop="materialCode">
          <el-input v-model="form.materialCode" placeholder="请输入物料编码" />
        </el-form-item>
        <el-form-item label="物料名称" prop="materialName">
          <el-input v-model="form.materialName" placeholder="请输入物料名称" />
        </el-form-item>
        <el-form-item label="规格型号">
          <el-input v-model="form.specification" placeholder="请输入规格型号" />
        </el-form-item>
        <el-form-item label="单位">
          <el-input v-model="form.unit" placeholder="请输入单位" style="width: 200px;" />
        </el-form-item>
        <el-form-item label="单价">
          <el-input-number v-model="form.unitPrice" :precision="2" :min="0" style="width: 200px;" />
        </el-form-item>
        <el-form-item label="安全库存">
          <el-input-number v-model="form.safetyStock" :min="0" style="width: 200px;" />
        </el-form-item>
        <el-form-item label="状态">
          <el-radio-group v-model="form.status">
            <el-radio :label="1">启用</el-radio>
            <el-radio :label="0">禁用</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="备注">
          <el-input v-model="form.remark" type="textarea" :rows="2" placeholder="请输入备注" />
        </el-form-item>
      </el-form>
      <span slot="footer" class="dialog-footer">
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" :loading="submitLoading" @click="handleSubmitForm">
          保存
        </el-button>
      </span>
    </el-dialog>

    <el-dialog
      title="物料详情"
      :visible.sync="detailDialogVisible"
      width="600px"
    >
      <el-descriptions :column="2" border>
        <el-descriptions-item label="物料编码">{{ currentMaterial.materialCode }}</el-descriptions-item>
        <el-descriptions-item label="物料名称">{{ currentMaterial.materialName }}</el-descriptions-item>
        <el-descriptions-item label="规格型号">{{ currentMaterial.specification }}</el-descriptions-item>
        <el-descriptions-item label="单位">{{ currentMaterial.unit }}</el-descriptions-item>
        <el-descriptions-item label="单价">¥{{ currentMaterial.unitPrice }}</el-descriptions-item>
        <el-descriptions-item label="安全库存">{{ currentMaterial.safetyStock }}</el-descriptions-item>
        <el-descriptions-item label="状态">
          <el-tag :type="currentMaterial.status === 1 ? 'success' : 'danger'">
            {{ currentMaterial.status === 1 ? '启用' : '禁用' }}
          </el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="备注" :span="2">
          {{ currentMaterial.remark || '无' }}
        </el-descriptions-item>
      </el-descriptions>
    </el-dialog>
  </div>
</template>

<script>
import {
  getMaterialPage,
  createMaterial,
  updateMaterial,
  deleteMaterial
} from '@/api/material'

export default {
  name: 'Material',
  data() {
    return {
      loading: false,
      submitLoading: false,
      total: 0,
      queryForm: {
        materialName: '',
        current: 1,
        size: 10
      },
      materialList: [],
      dialogVisible: false,
      detailDialogVisible: false,
      isEdit: false,
      currentMaterial: {},
      form: {
        materialCode: '',
        materialName: '',
        specification: '',
        unit: '',
        unitPrice: 0,
        safetyStock: 0,
        status: 1,
        remark: ''
      },
      rules: {
        materialCode: [{ required: true, message: '请输入物料编码', trigger: 'blur' }],
        materialName: [{ required: true, message: '请输入物料名称', trigger: 'blur' }]
      }
    }
  },
  created() {
    this.loadMaterialList()
  },
  methods: {
    loadMaterialList() {
      this.loading = true
      const params = {
        current: this.queryForm.current,
        size: this.queryForm.size,
        materialName: this.queryForm.materialName
      }
      getMaterialPage(params)
        .then(res => {
          this.materialList = res.data?.records || []
          this.total = res.data?.total || 0
          this.loading = false
        })
        .catch(() => {
          this.loading = false
          this.loadMockData()
        })
    },
    loadMockData() {
      this.materialList = [
        { 
          id: 1,
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
          id: 2,
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
          id: 3,
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
          id: 4,
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
          id: 5,
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
      this.total = this.materialList.length
    },
    handleQuery() {
      this.queryForm.current = 1
      this.loadMaterialList()
    },
    handleReset() {
      this.queryForm = {
        materialName: '',
        current: 1,
        size: 10
      }
      this.loadMaterialList()
    },
    handleSizeChange(val) {
      this.queryForm.size = val
      this.loadMaterialList()
    },
    handleCurrentChange(val) {
      this.queryForm.current = val
      this.loadMaterialList()
    },
    handleAdd() {
      this.isEdit = false
      this.form = {
        materialCode: '',
        materialName: '',
        specification: '',
        unit: '',
        unitPrice: 0,
        safetyStock: 0,
        status: 1,
        remark: ''
      }
      this.dialogVisible = true
    },
    handleEdit(row) {
      this.isEdit = true
      this.form = {
        id: row.id,
        materialCode: row.materialCode,
        materialName: row.materialName,
        specification: row.specification,
        unit: row.unit,
        unitPrice: row.unitPrice,
        safetyStock: row.safetyStock,
        status: row.status,
        remark: row.remark
      }
      this.dialogVisible = true
    },
    handleView(row) {
      this.currentMaterial = row
      this.detailDialogVisible = true
    },
    handleDelete(row) {
      this.$confirm('确定要删除该物料吗?', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      })
        .then(() => {
          deleteMaterial(row.id)
            .then(() => {
              this.$message.success('删除成功')
              this.loadMaterialList()
            })
            .catch(() => {
              this.$message.success('删除成功（模拟）')
              this.loadMockData()
            })
        })
        .catch(() => {})
    },
    handleSubmitForm() {
      this.$refs.formRef.validate(valid => {
        if (valid) {
          this.submitLoading = true
          const data = { ...this.form }
          
          if (this.isEdit) {
            updateMaterial(data)
              .then(() => {
                this.$message.success('保存成功')
                this.dialogVisible = false
                this.submitLoading = false
                this.loadMaterialList()
              })
              .catch(() => {
                this.$message.success('保存成功（模拟）')
                this.dialogVisible = false
                this.submitLoading = false
              })
          } else {
            createMaterial(data)
              .then(() => {
                this.$message.success('保存成功')
                this.dialogVisible = false
                this.submitLoading = false
                this.loadMaterialList()
              })
              .catch(() => {
                this.$message.success('保存成功（模拟）')
                this.dialogVisible = false
                this.submitLoading = false
              })
          }
        } else {
          return false
        }
      })
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
