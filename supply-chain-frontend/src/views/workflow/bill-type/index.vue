<template>
  <div class="bill-type-container">
    <el-card>
      <template slot="header">
        <div class="header-container">
          <span>单据类型管理</span>
          <el-button type="primary" @click="handleAdd">
            <i class="el-icon-plus"></i> 新增
          </el-button>
        </div>
      </template>

      <el-table :data="list" v-loading="loading" stripe style="width: 100%">
        <el-table-column prop="billTypeCode" label="单据类型编码" min-width="180" />
        <el-table-column prop="billTypeName" label="单据类型名称" min-width="150" />
        <el-table-column prop="moduleName" label="所属模块" min-width="120">
          <template slot-scope="scope">
            <el-tag size="small">{{ getModuleNameText(scope.row.moduleName) }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="sortOrder" label="排序" width="80" />
        <el-table-column prop="status" label="状态" width="100">
          <template slot-scope="scope">
            <el-tag :type="scope.row.status === 1 ? 'success' : 'info'" size="small">
              {{ scope.row.status === 1 ? '已启用' : '已禁用' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="description" label="描述" min-width="200" show-overflow-tooltip />
        <el-table-column prop="createTime" label="创建时间" width="160">
          <template slot-scope="scope">
            {{ formatTime(scope.row.createTime) }}
          </template>
        </el-table-column>
        <el-table-column label="操作" min-width="250" fixed="right">
          <template slot-scope="scope">
            <el-button type="text" size="small" @click="handleEdit(scope.row)">
              编辑
            </el-button>
            <el-button
              v-if="scope.row.status !== 1"
              type="text"
              size="small"
              style="color: #67c23a;"
              @click="handleEnable(scope.row)"
            >
              启用
            </el-button>
            <el-button
              v-if="scope.row.status === 1"
              type="text"
              size="small"
              style="color: #e6a23c;"
              @click="handleDisable(scope.row)"
            >
              禁用
            </el-button>
            <el-button
              type="text"
              size="small"
              style="color: #f56c6c;"
              @click="handleDelete(scope.row)"
            >
              删除
            </el-button>
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
      :title="dialogTitle"
      :visible.sync="dialogVisible"
      width="500px"
      :close-on-click-modal="false"
    >
      <el-form :model="form" :rules="rules" ref="form" label-width="100px">
        <el-form-item label="单据类型编码" prop="billTypeCode">
          <el-input v-model="form.billTypeCode" placeholder="请输入单据类型编码" :disabled="form.id" />
        </el-form-item>
        <el-form-item label="单据类型名称" prop="billTypeName">
          <el-input v-model="form.billTypeName" placeholder="请输入单据类型名称" />
        </el-form-item>
        <el-form-item label="所属模块" prop="moduleName">
          <el-select v-model="form.moduleName" placeholder="请选择所属模块" style="width: 100%;">
            <el-option label="供应链管理" value="supply_chain" />
            <el-option label="系统管理" value="system" />
            <el-option label="财务管理" value="finance" />
            <el-option label="其他" value="other" />
          </el-select>
        </el-form-item>
        <el-form-item label="排序" prop="sortOrder">
          <el-input-number v-model="form.sortOrder" :min="1" :max="100" />
        </el-form-item>
        <el-form-item label="描述">
          <el-input
            v-model="form.description"
            type="textarea"
            :rows="3"
            placeholder="请输入描述"
          />
        </el-form-item>
      </el-form>
      <span slot="footer" class="dialog-footer">
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" :loading="submitLoading" @click="handleSubmit">
          保存
        </el-button>
      </span>
    </el-dialog>
  </div>
</template>

<script>
import {
  getBillTypePage,
  createBillType,
  updateBillType,
  deleteBillType,
  enableBillType,
  disableBillType
} from '@/api/bill-type'

export default {
  name: 'BillType',
  data() {
    const validateBillTypeCode = (rule, value, callback) => {
      if (!value) {
        callback(new Error('请输入单据类型编码'))
      } else if (!/^[a-z_]+$/.test(value)) {
        callback(new Error('单据类型编码只能包含小写字母和下划线'))
      } else {
        callback()
      }
    }

    return {
      loading: false,
      submitLoading: false,
      total: 0,
      queryForm: {
        current: 1,
        size: 10
      },
      list: [],
      dialogVisible: false,
      dialogTitle: '新增单据类型',
      form: {
        id: null,
        billTypeCode: '',
        billTypeName: '',
        moduleName: 'supply_chain',
        sortOrder: 1,
        description: ''
      },
      rules: {
        billTypeCode: [
          { required: true, validator: validateBillTypeCode, trigger: 'blur' }
        ],
        billTypeName: [
          { required: true, message: '请输入单据类型名称', trigger: 'blur' }
        ],
        moduleName: [
          { required: true, message: '请选择所属模块', trigger: 'change' }
        ]
      }
    }
  },
  created() {
    this.loadList()
  },
  methods: {
    loadList() {
      this.loading = true
      getBillTypePage(this.queryForm)
        .then(res => {
          this.list = res.data?.records || []
          this.total = res.data?.total || 0
          this.loading = false
        })
        .catch(() => {
          this.loading = false
        })
    },
    formatTime(time) {
      if (!time) return ''
      return new Date(time).toLocaleString()
    },
    getModuleNameText(name) {
      const texts = {
        supply_chain: '供应链管理',
        system: '系统管理',
        finance: '财务管理',
        other: '其他'
      }
      return texts[name] || name
    },
    handleSizeChange(val) {
      this.queryForm.size = val
      this.loadList()
    },
    handleCurrentChange(val) {
      this.queryForm.current = val
      this.loadList()
    },
    handleAdd() {
      this.dialogTitle = '新增单据类型'
      this.form = {
        id: null,
        billTypeCode: '',
        billTypeName: '',
        moduleName: 'supply_chain',
        sortOrder: 1,
        description: ''
      }
      this.dialogVisible = true
    },
    handleEdit(row) {
      this.dialogTitle = '编辑单据类型'
      this.form = {
        id: row.id,
        billTypeCode: row.billTypeCode,
        billTypeName: row.billTypeName,
        moduleName: row.moduleName,
        sortOrder: row.sortOrder,
        description: row.description
      }
      this.dialogVisible = true
    },
    handleSubmit() {
      this.$refs.form.validate(valid => {
        if (valid) {
          this.submitLoading = true
          const data = { ...this.form }
          const promise = data.id
            ? updateBillType(data.id, data)
            : createBillType(data)
          
          promise
            .then(() => {
              this.$message.success('保存成功')
              this.dialogVisible = false
              this.submitLoading = false
              this.loadList()
            })
            .catch(() => {
              this.submitLoading = false
            })
        }
      })
    },
    handleDelete(row) {
      this.$confirm('确定要删除该单据类型吗?', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      })
        .then(() => {
          deleteBillType(row.id)
            .then(() => {
              this.$message.success('删除成功')
              this.loadList()
            })
        })
        .catch(() => {})
    },
    handleEnable(row) {
      this.$confirm('确定要启用该单据类型吗?', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      })
        .then(() => {
          enableBillType(row.id)
            .then(() => {
              this.$message.success('启用成功')
              this.loadList()
            })
        })
        .catch(() => {})
    },
    handleDisable(row) {
      this.$confirm('确定要禁用该单据类型吗?', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      })
        .then(() => {
          disableBillType(row.id)
            .then(() => {
              this.$message.success('禁用成功')
              this.loadList()
            })
        })
        .catch(() => {})
    }
  }
}
</script>

<style lang="scss" scoped>
.bill-type-container {
  padding: 0;

  .header-container {
    display: flex;
    justify-content: space-between;
    align-items: center;
  }
}
</style>
