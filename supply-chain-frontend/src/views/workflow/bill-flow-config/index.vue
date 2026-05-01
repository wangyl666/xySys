<template>
  <div class="bill-flow-config-container">
    <el-card>
      <template slot="header">
        <div class="header-container">
          <span>单据审批流配置</span>
          <el-button type="primary" @click="handleAdd">
            <i class="el-icon-plus"></i> 新增配置
          </el-button>
        </div>
      </template>

      <el-form :inline="true" :model="searchForm" class="search-form">
        <el-form-item label="单据类型">
          <el-select
            v-model="searchForm.billTypeCode"
            placeholder="全部"
            clearable
            style="width: 180px"
            @change="handleSearch"
          >
            <el-option
              v-for="item in billTypeList"
              :key="item.billTypeCode"
              :label="item.billTypeName"
              :value="item.billTypeCode"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="状态">
          <el-select
            v-model="searchForm.status"
            placeholder="全部"
            clearable
            style="width: 120px"
            @change="handleSearch"
          >
            <el-option label="启用" :value="1" />
            <el-option label="禁用" :value="0" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleSearch">查询</el-button>
          <el-button @click="handleReset">重置</el-button>
        </el-form-item>
      </el-form>

      <el-table :data="list" v-loading="loading" stripe style="width: 100%">
        <el-table-column prop="billTypeCode" label="单据类型编码" min-width="150" />
        <el-table-column prop="billTypeName" label="单据类型名称" min-width="120">
          <template slot-scope="scope">
            <el-tag size="small" type="primary">{{ getBillTypeName(scope.row.billTypeCode) }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="flowCode" label="审批流编码" min-width="180" />
        <el-table-column prop="flowName" label="审批流名称" min-width="150">
          <template slot-scope="scope">
            <el-tag size="small" type="success">{{ getFlowName(scope.row.flowCode) }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="isDefault" label="是否默认" width="100">
          <template slot-scope="scope">
            <el-tag v-if="scope.row.isDefault === 1" type="warning" size="small">默认</el-tag>
            <span v-else>-</span>
          </template>
        </el-table-column>
        <el-table-column prop="status" label="状态" width="100">
          <template slot-scope="scope">
            <el-tag :type="scope.row.status === 1 ? 'success' : 'info'" size="small">
              {{ scope.row.status === 1 ? '已启用' : '已禁用' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="description" label="描述" min-width="150" show-overflow-tooltip />
        <el-table-column prop="createTime" label="创建时间" width="160">
          <template slot-scope="scope">
            {{ formatTime(scope.row.createTime) }}
          </template>
        </el-table-column>
        <el-table-column label="操作" min-width="320" fixed="right">
          <template slot-scope="scope">
            <el-button type="text" size="small" @click="handleEdit(scope.row)">
              编辑
            </el-button>
            <el-button
              v-if="scope.row.isDefault !== 1 && scope.row.status === 1"
              type="text"
              size="small"
              style="color: #e6a23c;"
              @click="handleSetDefault(scope.row)"
            >
              设为默认
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
      width="550px"
      :close-on-click-modal="false"
    >
      <el-form :model="form" :rules="rules" ref="form" label-width="100px">
        <el-form-item label="单据类型" prop="billTypeCode">
          <el-select
            v-model="form.billTypeCode"
            placeholder="请选择单据类型"
            style="width: 100%;"
            :disabled="form.id"
          >
            <el-option
              v-for="item in billTypeList"
              :key="item.billTypeCode"
              :label="item.billTypeName"
              :value="item.billTypeCode"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="审批流" prop="flowCode">
          <el-select
            v-model="form.flowCode"
            placeholder="请选择审批流"
            style="width: 100%;"
          >
            <el-option
              v-for="item in flowList"
              :key="item.flowCode"
              :label="item.flowName + ' (' + item.flowCode + ')'"
              :value="item.flowCode"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="是否默认" prop="isDefault">
          <el-radio-group v-model="form.isDefault">
            <el-radio :label="1">是</el-radio>
            <el-radio :label="0">否</el-radio>
          </el-radio-group>
          <el-divider-content style="margin-left: 10px; color: #909399; font-size: 12px;">
            设置为默认后，该单据类型提交时自动使用此审批流
          </el-divider-content>
        </el-form-item>
        <el-form-item label="状态" prop="status">
          <el-radio-group v-model="form.status">
            <el-radio :label="1">启用</el-radio>
            <el-radio :label="0">禁用</el-radio>
          </el-radio-group>
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
  getBillFlowConfigPage,
  createBillFlowConfig,
  updateBillFlowConfig,
  deleteBillFlowConfig,
  enableBillFlowConfig,
  disableBillFlowConfig,
  setDefaultBillFlowConfig
} from '@/api/bill-flow-config'
import { getBillTypeList } from '@/api/bill-type'
import { getApprovalFlowPage } from '@/api/approval-flow'

export default {
  name: 'BillFlowConfig',
  data() {
    return {
      loading: false,
      submitLoading: false,
      total: 0,
      queryForm: {
        current: 1,
        size: 10
      },
      searchForm: {
        billTypeCode: '',
        status: null
      },
      list: [],
      billTypeList: [],
      flowList: [],
      dialogVisible: false,
      dialogTitle: '新增配置',
      form: {
        id: null,
        billTypeCode: '',
        flowCode: '',
        isDefault: 0,
        status: 1,
        description: ''
      },
      rules: {
        billTypeCode: [
          { required: true, message: '请选择单据类型', trigger: 'change' }
        ],
        flowCode: [
          { required: true, message: '请选择审批流', trigger: 'change' }
        ]
      }
    }
  },
  created() {
    this.loadBillTypeList()
    this.loadFlowList()
    this.loadList()
  },
  methods: {
    loadList() {
      this.loading = true
      const params = {
        ...this.queryForm,
        ...this.searchForm
      }
      if (params.status === null) {
        delete params.status
      }
      getBillFlowConfigPage(params)
        .then(res => {
          this.list = res.data?.records || []
          this.total = res.data?.total || 0
          this.loading = false
        })
        .catch(() => {
          this.loading = false
        })
    },
    loadBillTypeList() {
      getBillTypeList()
        .then(res => {
          this.billTypeList = res.data || []
        })
        .catch(() => {
          this.billTypeList = []
        })
    },
    loadFlowList() {
      getApprovalFlowPage({ current: 1, size: 1000 })
        .then(res => {
          this.flowList = res.data?.records || []
        })
        .catch(() => {
          this.flowList = []
        })
    },
    formatTime(time) {
      if (!time) return ''
      return new Date(time).toLocaleString()
    },
    getBillTypeName(code) {
      const item = this.billTypeList.find(t => t.billTypeCode === code)
      return item ? item.billTypeName : code
    },
    getFlowName(code) {
      const item = this.flowList.find(f => f.flowCode === code)
      return item ? item.flowName : code
    },
    handleSearch() {
      this.queryForm.current = 1
      this.loadList()
    },
    handleReset() {
      this.searchForm = {
        billTypeCode: '',
        status: null
      }
      this.handleSearch()
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
      this.dialogTitle = '新增配置'
      this.form = {
        id: null,
        billTypeCode: '',
        flowCode: '',
        isDefault: 0,
        status: 1,
        description: ''
      }
      this.dialogVisible = true
    },
    handleEdit(row) {
      this.dialogTitle = '编辑配置'
      this.form = {
        id: row.id,
        billTypeCode: row.billTypeCode,
        flowCode: row.flowCode,
        isDefault: row.isDefault,
        status: row.status,
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
            ? updateBillFlowConfig(data.id, data)
            : createBillFlowConfig(data)
          
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
      this.$confirm('确定要删除该配置吗?', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      })
        .then(() => {
          deleteBillFlowConfig(row.id)
            .then(() => {
              this.$message.success('删除成功')
              this.loadList()
            })
        })
        .catch(() => {})
    },
    handleSetDefault(row) {
      this.$confirm('确定要将此配置设为默认吗?', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      })
        .then(() => {
          setDefaultBillFlowConfig(row.id)
            .then(() => {
              this.$message.success('设置成功')
              this.loadList()
            })
        })
        .catch(() => {})
    },
    handleEnable(row) {
      this.$confirm('确定要启用该配置吗?', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      })
        .then(() => {
          enableBillFlowConfig(row.id)
            .then(() => {
              this.$message.success('启用成功')
              this.loadList()
            })
        })
        .catch(() => {})
    },
    handleDisable(row) {
      this.$confirm('确定要禁用该配置吗?', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      })
        .then(() => {
          disableBillFlowConfig(row.id)
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
.bill-flow-config-container {
  padding: 0;

  .header-container {
    display: flex;
    justify-content: space-between;
    align-items: center;
  }

  .search-form {
    margin-bottom: 20px;
  }
}
</style>
