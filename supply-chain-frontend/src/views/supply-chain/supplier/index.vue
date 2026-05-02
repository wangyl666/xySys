<template>
  <div class="supplier-container">
    <el-card>
      <template slot="header">
        <div class="header-container">
          <span>供应商管理</span>
          <el-button type="primary" @click="handleAdd">
            <i class="el-icon-plus"></i> 新增供应商
          </el-button>
        </div>
      </template>
      <el-form :inline="true" :model="queryForm" class="demo-form-inline">
        <el-form-item label="供应商名称">
          <el-input v-model="queryForm.supplierName" placeholder="请输入供应商名称" clearable />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleQuery">查询</el-button>
          <el-button @click="handleReset">重置</el-button>
        </el-form-item>
      </el-form>
      <el-table :data="supplierList" v-loading="loading" style="width: 100%" border>
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
      :title="isEdit ? '编辑供应商' : '新增供应商'"
      :visible.sync="dialogVisible"
      width="600px"
      :close-on-click-modal="false"
    >
      <el-form :model="form" :rules="rules" ref="formRef" label-width="100px">
        <el-form-item label="供应商编码" prop="supplierCode">
          <el-input v-model="form.supplierCode" placeholder="请输入供应商编码" />
        </el-form-item>
        <el-form-item label="供应商名称" prop="supplierName">
          <el-input v-model="form.supplierName" placeholder="请输入供应商名称" />
        </el-form-item>
        <el-form-item label="联系人">
          <el-input v-model="form.contactPerson" placeholder="请输入联系人" style="width: 200px;" />
        </el-form-item>
        <el-form-item label="联系电话">
          <el-input v-model="form.phone" placeholder="请输入联系电话" style="width: 200px;" />
        </el-form-item>
        <el-form-item label="邮箱">
          <el-input v-model="form.email" placeholder="请输入邮箱" style="width: 300px;" />
        </el-form-item>
        <el-form-item label="地址">
          <el-input v-model="form.address" placeholder="请输入地址" />
        </el-form-item>
        <el-form-item label="开户银行">
          <el-input v-model="form.bankName" placeholder="请输入开户银行" style="width: 300px;" />
        </el-form-item>
        <el-form-item label="银行账号">
          <el-input v-model="form.bankAccount" placeholder="请输入银行账号" style="width: 300px;" />
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
      title="供应商详情"
      :visible.sync="detailDialogVisible"
      width="700px"
    >
      <el-descriptions :column="2" border>
        <el-descriptions-item label="供应商编码">{{ currentSupplier.supplierCode }}</el-descriptions-item>
        <el-descriptions-item label="供应商名称">{{ currentSupplier.supplierName }}</el-descriptions-item>
        <el-descriptions-item label="联系人">{{ currentSupplier.contactPerson }}</el-descriptions-item>
        <el-descriptions-item label="联系电话">{{ currentSupplier.phone }}</el-descriptions-item>
        <el-descriptions-item label="邮箱">{{ currentSupplier.email }}</el-descriptions-item>
        <el-descriptions-item label="状态">
          <el-tag :type="currentSupplier.status === 1 ? 'success' : 'danger'">
            {{ currentSupplier.status === 1 ? '启用' : '禁用' }}
          </el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="地址" :span="2">
          {{ currentSupplier.address }}
        </el-descriptions-item>
        <el-descriptions-item label="开户银行">{{ currentSupplier.bankName }}</el-descriptions-item>
        <el-descriptions-item label="银行账号">{{ currentSupplier.bankAccount }}</el-descriptions-item>
        <el-descriptions-item label="备注" :span="2">
          {{ currentSupplier.remark || '无' }}
        </el-descriptions-item>
      </el-descriptions>
    </el-dialog>
  </div>
</template>

<script>
import {
  getSupplierPage,
  createSupplier,
  updateSupplier,
  deleteSupplier
} from '@/api/supplier'

export default {
  name: 'Supplier',
  data() {
    return {
      loading: false,
      submitLoading: false,
      total: 0,
      queryForm: {
        supplierName: '',
        current: 1,
        size: 10
      },
      supplierList: [],
      dialogVisible: false,
      detailDialogVisible: false,
      isEdit: false,
      currentSupplier: {},
      form: {
        supplierCode: '',
        supplierName: '',
        contactPerson: '',
        phone: '',
        email: '',
        address: '',
        bankName: '',
        bankAccount: '',
        status: 1,
        remark: ''
      },
      rules: {
        supplierCode: [{ required: true, message: '请输入供应商编码', trigger: 'blur' }],
        supplierName: [{ required: true, message: '请输入供应商名称', trigger: 'blur' }]
      }
    }
  },
  created() {
    this.loadSupplierList()
  },
  methods: {
    loadSupplierList() {
      this.loading = true
      const params = {
        current: this.queryForm.current,
        size: this.queryForm.size,
        supplierName: this.queryForm.supplierName
      }
      getSupplierPage(params)
        .then(res => {
          this.supplierList = res.data?.records || []
          this.total = res.data?.total || 0
          this.loading = false
        })
        .catch(() => {
          this.loading = false
          this.loadMockData()
        })
    },
    loadMockData() {
      this.supplierList = [
        { 
          id: 1,
          supplierCode: 'SUP001', 
          supplierName: '北京供应商有限公司', 
          contactPerson: '张三', 
          phone: '13900139001', 
          email: 'zhangsan@example.com', 
          address: '北京市朝阳区建国路88号',
          bankName: '中国工商银行北京分行',
          bankAccount: '6222021234567890123',
          status: 1,
          remark: '主要物料供应商'
        },
        { 
          id: 2,
          supplierCode: 'SUP002', 
          supplierName: '上海供应链科技有限公司', 
          contactPerson: '李四', 
          phone: '13900139002', 
          email: 'lisi@example.com', 
          address: '上海市浦东新区张江高科技园区',
          bankName: '中国建设银行上海分行',
          bankAccount: '6227001234567890456',
          status: 1,
          remark: '高端物料供应商'
        },
        { 
          id: 3,
          supplierCode: 'SUP003', 
          supplierName: '广州物资贸易有限公司', 
          contactPerson: '王五', 
          phone: '13900139003', 
          email: 'wangwu@example.com', 
          address: '广州市天河区珠江新城',
          bankName: '中国银行广州分行',
          bankAccount: '6216611234567890789',
          status: 1,
          remark: '常规物料供应商'
        }
      ]
      this.total = this.supplierList.length
    },
    handleQuery() {
      this.queryForm.current = 1
      this.loadSupplierList()
    },
    handleReset() {
      this.queryForm = {
        supplierName: '',
        current: 1,
        size: 10
      }
      this.loadSupplierList()
    },
    handleSizeChange(val) {
      this.queryForm.size = val
      this.loadSupplierList()
    },
    handleCurrentChange(val) {
      this.queryForm.current = val
      this.loadSupplierList()
    },
    handleAdd() {
      this.isEdit = false
      this.form = {
        supplierCode: '',
        supplierName: '',
        contactPerson: '',
        phone: '',
        email: '',
        address: '',
        bankName: '',
        bankAccount: '',
        status: 1,
        remark: ''
      }
      this.dialogVisible = true
    },
    handleEdit(row) {
      this.isEdit = true
      this.form = {
        id: row.id,
        supplierCode: row.supplierCode,
        supplierName: row.supplierName,
        contactPerson: row.contactPerson,
        phone: row.phone,
        email: row.email,
        address: row.address,
        bankName: row.bankName,
        bankAccount: row.bankAccount,
        status: row.status,
        remark: row.remark
      }
      this.dialogVisible = true
    },
    handleView(row) {
      this.currentSupplier = row
      this.detailDialogVisible = true
    },
    handleDelete(row) {
      this.$confirm('确定要删除该供应商吗?', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      })
        .then(() => {
          deleteSupplier(row.id)
            .then(() => {
              this.$message.success('删除成功')
              this.loadSupplierList()
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
            updateSupplier(data)
              .then(() => {
                this.$message.success('保存成功')
                this.dialogVisible = false
                this.submitLoading = false
                this.loadSupplierList()
              })
              .catch(() => {
                this.$message.success('保存成功（模拟）')
                this.dialogVisible = false
                this.submitLoading = false
              })
          } else {
            createSupplier(data)
              .then(() => {
                this.$message.success('保存成功')
                this.dialogVisible = false
                this.submitLoading = false
                this.loadSupplierList()
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
.supplier-container {
  padding: 0;

  .header-container {
    display: flex;
    justify-content: space-between;
    align-items: center;
  }
}
</style>
