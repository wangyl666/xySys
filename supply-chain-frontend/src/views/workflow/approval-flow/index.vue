<template>
  <div class="approval-flow-container">
    <el-card>
      <template slot="header">
        <div class="header-container">
          <span>审批流配置</span>
          <el-button type="primary" @click="handleAddFlow">
            <i class="el-icon-plus"></i> 新建审批流
          </el-button>
        </div>
      </template>

      <el-table :data="flowList" v-loading="loading" stripe style="width: 100%">
        <el-table-column prop="flowName" label="流程名称" min-width="180" />
        <el-table-column prop="flowCode" label="流程编码" min-width="180" />
        <el-table-column prop="flowType" label="流程类型" min-width="120">
          <template slot-scope="scope">
            <el-tag :type="getFlowTypeTag(scope.row.flowType)">
              {{ getFlowTypeText(scope.row.flowType) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="status" label="状态" width="100">
          <template slot-scope="scope">
            <el-tag :type="scope.row.status === 1 ? 'success' : 'info'">
              {{ scope.row.status === 1 ? '已启用' : '已禁用' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="version" label="版本" width="80" />
        <el-table-column prop="description" label="描述" min-width="200" show-overflow-tooltip />
        <el-table-column prop="createTime" label="创建时间" width="160">
          <template slot-scope="scope">
            {{ formatTime(scope.row.createTime) }}
          </template>
        </el-table-column>
        <el-table-column label="操作" min-width="300" fixed="right">
          <template slot-scope="scope">
            <el-button
              type="text"
              size="small"
              @click="handleEditFlow(scope.row)"
            >
              编辑
            </el-button>
            <el-button
              type="text"
              size="small"
              @click="handleConfigNodes(scope.row)"
            >
              配置节点
            </el-button>
            <el-button
              v-if="scope.row.status !== 1"
              type="text"
              size="small"
              style="color: #67c23a;"
              @click="handleEnableFlow(scope.row)"
            >
              启用
            </el-button>
            <el-button
              v-if="scope.row.status === 1"
              type="text"
              size="small"
              style="color: #e6a23c;"
              @click="handleDisableFlow(scope.row)"
            >
              禁用
            </el-button>
            <el-button
              v-if="scope.row.status !== 1"
              type="text"
              size="small"
              style="color: #f56c6c;"
              @click="handleDeleteFlow(scope.row)"
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
      :title="flowDialogTitle"
      :visible.sync="flowDialogVisible"
      width="600px"
      :close-on-click-modal="false"
    >
      <el-form :model="flowForm" :rules="flowRules" ref="flowForm" label-width="100px">
        <el-form-item label="流程名称" prop="flowName">
          <el-input v-model="flowForm.flowName" placeholder="请输入流程名称" />
        </el-form-item>
        <el-form-item label="流程编码" prop="flowCode">
          <el-input v-model="flowForm.flowCode" placeholder="请输入流程编码" :disabled="flowForm.id" />
        </el-form-item>
        <el-form-item label="流程类型" prop="flowType">
          <el-select v-model="flowForm.flowType" placeholder="请选择流程类型" style="width: 100%;">
            <el-option label="采购订单审批" value="PURCHASE_ORDER" />
            <el-option label="付款审批" value="PAYMENT" />
            <el-option label="报销审批" value="EXPENSE" />
            <el-option label="其他" value="OTHER" />
          </el-select>
        </el-form-item>
        <el-form-item label="描述">
          <el-input
            v-model="flowForm.description"
            type="textarea"
            :rows="3"
            placeholder="请输入描述"
          />
        </el-form-item>
      </el-form>
      <span slot="footer" class="dialog-footer">
        <el-button @click="flowDialogVisible = false">取消</el-button>
        <el-button type="primary" :loading="submitLoading" @click="handleSubmitFlow">
          保存
        </el-button>
      </span>
    </el-dialog>

    <el-dialog
      title="配置审批节点"
      :visible.sync="nodeDialogVisible"
      width="900px"
      :close-on-click-modal="false"
    >
      <div class="node-config-header">
        <span>当前流程：{{ currentFlow.flowName }}</span>
        <el-button type="primary" size="small" @click="handleAddNode">
          <i class="el-icon-plus"></i> 添加审批节点
        </el-button>
      </div>

      <el-timeline v-if="nodeList.length > 0">
        <el-timeline-item
          v-for="(node, index) in nodeList"
          :key="node.id"
          :type="index === 0 ? 'primary' : 'success'"
          :timestamp="'节点 ' + (index + 1)"
          placement="top"
        >
          <el-card shadow="hover">
            <template slot="header">
              <div class="node-header">
                <span class="node-name">{{ node.nodeName }}</span>
                <span class="node-code">({{ node.nodeCode }})</span>
                <div class="node-actions">
                  <el-button type="text" size="small" @click="handleEditNode(node)">
                    编辑
                  </el-button>
                  <el-button type="text" size="small" style="color: #f56c6c;" @click="handleDeleteNode(node)">
                    删除
                  </el-button>
                </div>
              </div>
            </template>
            <el-descriptions :column="3" size="small" border>
              <el-descriptions-item label="节点类型">
                <el-tag size="small">{{ getNodeTypeText(node.nodeType) }}</el-tag>
              </el-descriptions-item>
              <el-descriptions-item label="审批类型">
                <el-tag size="small" type="success">{{ getApprovalTypeText(node.approvalType) }}</el-tag>
              </el-descriptions-item>
              <el-descriptions-item label="排序">
                {{ node.sortOrder }}
              </el-descriptions-item>
              <el-descriptions-item label="条件表达式" :span="3">
                {{ node.conditionExpression || '无' }}
              </el-descriptions-item>
            </el-descriptions>
            <el-divider content-position="left">审批人配置</el-divider>
            <el-table :data="node.assignees || []" size="small" border>
              <el-table-column prop="sortOrder" label="序号" width="80" />
              <el-table-column prop="assigneeType" label="类型" width="100">
                <template slot-scope="scope">
                  <el-tag size="mini">{{ getAssigneeTypeText(scope.row.assigneeType) }}</el-tag>
                </template>
              </el-table-column>
              <el-table-column prop="assigneeName" label="审批人名称" min-width="150" />
              <el-table-column prop="assigneeCode" label="编码" min-width="120" />
            </el-table>
            <el-empty v-if="!node.assignees || node.assignees.length === 0" description="暂无审批人配置" :image-size="60" />
          </el-card>
        </el-timeline-item>
      </el-timeline>

      <el-empty v-if="nodeList.length === 0" description="暂无审批节点，请添加" />
    </el-dialog>

    <el-dialog
      :title="nodeDialogTitle"
      :visible.sync="nodeEditDialogVisible"
      width="700px"
      :close-on-click-modal="false"
    >
      <el-form :model="nodeForm" :rules="nodeRules" ref="nodeForm" label-width="120px">
        <el-form-item label="节点名称" prop="nodeName">
          <el-input v-model="nodeForm.nodeName" placeholder="请输入节点名称" />
        </el-form-item>
        <el-form-item label="节点编码" prop="nodeCode">
          <el-input v-model="nodeForm.nodeCode" placeholder="请输入节点编码（如：node1Task）" />
        </el-form-item>
        <el-form-item label="节点类型" prop="nodeType">
          <el-radio-group v-model="nodeForm.nodeType">
            <el-radio :label="1">审批节点</el-radio>
            <el-radio :label="2">或签节点</el-radio>
            <el-radio :label="3">会签节点</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="排序" prop="sortOrder">
          <el-input-number v-model="nodeForm.sortOrder" :min="1" :max="10" />
        </el-form-item>
        <el-form-item label="条件表达式">
          <el-input
            v-model="nodeForm.conditionExpression"
            type="textarea"
            :rows="2"
            placeholder="可选，如：totalAmount > 10000，为空则始终执行"
          />
          <div class="el-form-item__tip">
            支持的变量：totalAmount（订单金额）、supplierId（供应商ID）等
          </div>
        </el-form-item>
        <el-form-item label="审批人配置">
          <div class="assignee-config">
            <el-button type="primary" size="small" @click="handleAddAssignee">
              <i class="el-icon-plus"></i> 添加审批人
            </el-button>
            <el-table :data="nodeForm.assignees" size="small" border style="margin-top: 10px;">
              <el-table-column prop="sortOrder" label="序号" width="80">
                <template slot-scope="scope">
                  <el-input-number v-model="scope.row.sortOrder" :min="1" :max="10" size="mini" />
                </template>
              </el-table-column>
              <el-table-column prop="assigneeType" label="类型" width="150">
                <template slot-scope="scope">
                  <el-select v-model="scope.row.assigneeType" placeholder="选择类型" size="mini" style="width: 100%;">
                    <el-option label="用户" value="USER" />
                    <el-option label="角色" value="ROLE" />
                    <el-option label="部门" value="DEPARTMENT" />
                  </el-select>
                </template>
              </el-table-column>
              <el-table-column prop="assigneeName" label="名称" min-width="150">
                <template slot-scope="scope">
                  <el-input v-model="scope.row.assigneeName" placeholder="名称" size="mini" />
                </template>
              </el-table-column>
              <el-table-column prop="assigneeCode" label="编码/ID" min-width="120">
                <template slot-scope="scope">
                  <el-input v-model="scope.row.assigneeCode" placeholder="编码或ID" size="mini" />
                </template>
              </el-table-column>
              <el-table-column label="操作" width="80">
                <template slot-scope="scope">
                  <el-button type="text" size="small" style="color: #f56c6c;" @click="handleRemoveAssignee(scope.$index)">
                    删除
                  </el-button>
                </template>
              </el-table-column>
            </el-table>
          </div>
        </el-form-item>
        <el-form-item label="描述">
          <el-input
            v-model="nodeForm.description"
            type="textarea"
            :rows="2"
            placeholder="请输入描述"
          />
        </el-form-item>
      </el-form>
      <span slot="footer" class="dialog-footer">
        <el-button @click="nodeEditDialogVisible = false">取消</el-button>
        <el-button type="primary" :loading="submitLoading" @click="handleSubmitNode">
          保存
        </el-button>
      </span>
    </el-dialog>
  </div>
</template>

<script>
import {
  getApprovalFlowPage,
  createApprovalFlow,
  updateApprovalFlow,
  deleteApprovalFlow,
  enableApprovalFlow,
  disableApprovalFlow,
  getApprovalNodes,
  createApprovalNode,
  updateApprovalNode,
  deleteApprovalNode
} from '@/api/approval-flow'

export default {
  name: 'ApprovalFlow',
  data() {
    const validateFlowCode = (rule, value, callback) => {
      if (!value) {
        callback(new Error('请输入流程编码'))
      } else if (!/^[a-zA-Z0-9_-]+$/.test(value)) {
        callback(new Error('流程编码只能包含字母、数字、下划线和连字符'))
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
      flowList: [],
      flowDialogVisible: false,
      flowDialogTitle: '新建审批流',
      flowForm: {
        id: null,
        flowName: '',
        flowCode: '',
        flowType: '',
        description: ''
      },
      flowRules: {
        flowName: [
          { required: true, message: '请输入流程名称', trigger: 'blur' }
        ],
        flowCode: [
          { required: true, validator: validateFlowCode, trigger: 'blur' }
        ],
        flowType: [
          { required: true, message: '请选择流程类型', trigger: 'change' }
        ]
      },
      nodeDialogVisible: false,
      currentFlow: {},
      nodeList: [],
      nodeEditDialogVisible: false,
      nodeDialogTitle: '新建审批节点',
      nodeForm: {
        id: null,
        flowId: null,
        nodeName: '',
        nodeCode: '',
        nodeType: 1,
        sortOrder: 1,
        approvalType: 'USER',
        conditionExpression: '',
        description: '',
        assignees: []
      },
      nodeRules: {
        nodeName: [
          { required: true, message: '请输入节点名称', trigger: 'blur' }
        ],
        nodeCode: [
          { required: true, message: '请输入节点编码', trigger: 'blur' }
        ]
      }
    }
  },
  created() {
    this.loadFlowList()
  },
  methods: {
    loadFlowList() {
      this.loading = true
      getApprovalFlowPage(this.queryForm)
        .then(res => {
          this.flowList = res.data?.records || []
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
    getFlowTypeTag(type) {
      const tags = {
        PURCHASE_ORDER: 'primary',
        PAYMENT: 'success',
        EXPENSE: 'warning',
        OTHER: 'info'
      }
      return tags[type] || 'info'
    },
    getFlowTypeText(type) {
      const texts = {
        PURCHASE_ORDER: '采购订单审批',
        PAYMENT: '付款审批',
        EXPENSE: '报销审批',
        OTHER: '其他'
      }
      return texts[type] || type
    },
    getNodeTypeText(type) {
      const texts = {
        1: '审批节点',
        2: '或签节点',
        3: '会签节点'
      }
      return texts[type] || type
    },
    getApprovalTypeText(type) {
      const texts = {
        USER: '指定用户',
        ROLE: '指定角色',
        DEPARTMENT: '指定部门'
      }
      return texts[type] || type
    },
    getAssigneeTypeText(type) {
      const texts = {
        USER: '用户',
        ROLE: '角色',
        DEPARTMENT: '部门'
      }
      return texts[type] || type
    },
    handleSizeChange(val) {
      this.queryForm.size = val
      this.loadFlowList()
    },
    handleCurrentChange(val) {
      this.queryForm.current = val
      this.loadFlowList()
    },
    handleAddFlow() {
      this.flowDialogTitle = '新建审批流'
      this.flowForm = {
        id: null,
        flowName: '',
        flowCode: '',
        flowType: '',
        description: ''
      }
      this.flowDialogVisible = true
    },
    handleEditFlow(row) {
      this.flowDialogTitle = '编辑审批流'
      this.flowForm = {
        id: row.id,
        flowName: row.flowName,
        flowCode: row.flowCode,
        flowType: row.flowType,
        description: row.description
      }
      this.flowDialogVisible = true
    },
    handleSubmitFlow() {
      this.$refs.flowForm.validate(valid => {
        if (valid) {
          this.submitLoading = true
          const data = { ...this.flowForm }
          const promise = data.id
            ? updateApprovalFlow(data.id, data)
            : createApprovalFlow(data)
          
          promise
            .then(() => {
              this.$message.success('保存成功')
              this.flowDialogVisible = false
              this.submitLoading = false
              this.loadFlowList()
            })
            .catch(() => {
              this.submitLoading = false
            })
        }
      })
    },
    handleDeleteFlow(row) {
      this.$confirm('确定要删除该审批流吗?', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      })
        .then(() => {
          deleteApprovalFlow(row.id)
            .then(() => {
              this.$message.success('删除成功')
              this.loadFlowList()
            })
        })
        .catch(() => {})
    },
    handleEnableFlow(row) {
      this.$confirm('启用该审批流将禁用同名的其他审批流，确定要启用吗?', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      })
        .then(() => {
          enableApprovalFlow(row.id)
            .then(() => {
              this.$message.success('启用成功')
              this.loadFlowList()
            })
        })
        .catch(() => {})
    },
    handleDisableFlow(row) {
      this.$confirm('确定要禁用该审批流吗?', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      })
        .then(() => {
          disableApprovalFlow(row.id)
            .then(() => {
              this.$message.success('禁用成功')
              this.loadFlowList()
            })
        })
        .catch(() => {})
    },
    handleConfigNodes(row) {
      this.currentFlow = row
      this.loadNodes(row.id)
      this.nodeDialogVisible = true
    },
    loadNodes(flowId) {
      getApprovalNodes(flowId)
        .then(res => {
          this.nodeList = res.data || []
        })
        .catch(() => {
          this.nodeList = []
        })
    },
    handleAddNode() {
      this.nodeDialogTitle = '新建审批节点'
      this.nodeForm = {
        id: null,
        flowId: this.currentFlow.id,
        nodeName: '',
        nodeCode: '',
        nodeType: 1,
        sortOrder: this.nodeList.length + 1,
        approvalType: 'USER',
        conditionExpression: '',
        description: '',
        assignees: []
      }
      this.nodeEditDialogVisible = true
    },
    handleEditNode(node) {
      this.nodeDialogTitle = '编辑审批节点'
      this.nodeForm = {
        id: node.id,
        flowId: this.currentFlow.id,
        nodeName: node.nodeName,
        nodeCode: node.nodeCode,
        nodeType: node.nodeType || 1,
        sortOrder: node.sortOrder,
        approvalType: node.approvalType || 'USER',
        conditionExpression: node.conditionExpression || '',
        description: node.description || '',
        assignees: (node.assignees || []).map(a => ({ ...a }))
      }
      this.nodeEditDialogVisible = true
    },
    handleDeleteNode(node) {
      this.$confirm('确定要删除该审批节点吗?', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      })
        .then(() => {
          deleteApprovalNode(node.id)
            .then(() => {
              this.$message.success('删除成功')
              this.loadNodes(this.currentFlow.id)
            })
        })
        .catch(() => {})
    },
    handleSubmitNode() {
      this.$refs.nodeForm.validate(valid => {
        if (valid) {
          this.submitLoading = true
          const data = { ...this.nodeForm }
          const promise = data.id
            ? updateApprovalNode(data.id, data)
            : createApprovalNode(data)
          
          promise
            .then(() => {
              this.$message.success('保存成功')
              this.nodeEditDialogVisible = false
              this.submitLoading = false
              this.loadNodes(this.currentFlow.id)
            })
            .catch(() => {
              this.submitLoading = false
            })
        }
      })
    },
    handleAddAssignee() {
      this.nodeForm.assignees.push({
        sortOrder: this.nodeForm.assignees.length + 1,
        assigneeType: 'USER',
        assigneeName: '',
        assigneeCode: ''
      })
    },
    handleRemoveAssignee(index) {
      this.nodeForm.assignees.splice(index, 1)
    }
  }
}
</script>

<style lang="scss" scoped>
.approval-flow-container {
  padding: 0;

  .header-container {
    display: flex;
    justify-content: space-between;
    align-items: center;
  }

  .node-config-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
    margin-bottom: 20px;
    font-weight: bold;
  }

  .node-header {
    display: flex;
    align-items: center;

    .node-name {
      font-weight: bold;
      font-size: 14px;
    }

    .node-code {
      color: #909399;
      margin-left: 10px;
      font-size: 12px;
    }

    .node-actions {
      margin-left: auto;
    }
  }

  .assignee-config {
    width: 100%;
  }

  .el-form-item__tip {
    color: #909399;
    font-size: 12px;
    margin-top: 5px;
  }
}
</style>
