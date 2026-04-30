<template>
  <div class="dashboard-container">
    <el-row :gutter="20">
      <el-col :span="6">
        <el-card class="box-card">
          <div class="card-content">
            <div class="card-icon">
              <i class="el-icon-shopping-cart-2"></i>
            </div>
            <div class="card-info">
              <div class="card-value">{{ statistics.pendingOrders }}</div>
              <div class="card-label">待审批订单</div>
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card class="box-card">
          <div class="card-content">
            <div class="card-icon" style="background-color: #67c23a;">
              <i class="el-icon-goods"></i>
            </div>
            <div class="card-info">
              <div class="card-value">{{ statistics.approvedOrders }}</div>
              <div class="card-label">已审批订单</div>
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card class="box-card">
          <div class="card-content">
            <div class="card-icon" style="background-color: #e6a23c;">
              <i class="el-icon-office-building"></i>
            </div>
            <div class="card-info">
              <div class="card-value">{{ statistics.supplierCount }}</div>
              <div class="card-label">供应商数量</div>
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card class="box-card">
          <div class="card-content">
            <div class="card-icon" style="background-color: #f56c6c;">
              <i class="el-icon-box"></i>
            </div>
            <div class="card-info">
              <div class="card-value">{{ statistics.materialCount }}</div>
              <div class="card-label">物料数量</div>
            </div>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <el-row :gutter="20" style="margin-top: 20px;">
      <el-col :span="12">
        <el-card>
          <template slot="header">
            <span>待办任务</span>
            <el-button type="text" style="float: right;" @click="$router.push('/workflow/todo')">
              查看全部 <i class="el-icon-arrow-right"></i>
            </el-button>
          </template>
          <el-table :data="todoTasks" style="width: 100%" size="small">
            <el-table-column prop="taskName" label="任务名称" min-width="150" />
            <el-table-column prop="processDefinitionName" label="流程名称" min-width="120" />
            <el-table-column prop="createTime" label="创建时间" min-width="160">
              <template slot-scope="scope">
                {{ formatTime(scope.row.createTime) }}
              </template>
            </el-table-column>
            <el-table-column label="操作" width="100">
              <template slot-scope="scope">
                <el-button type="text" size="small" @click="handleTask(scope.row)">
                  处理
                </el-button>
              </template>
            </el-table-column>
          </el-table>
          <el-empty v-if="todoTasks.length === 0" description="暂无待办任务" />
        </el-card>
      </el-col>
      <el-col :span="12">
        <el-card>
          <template slot="header">
            <span>最近采购订单</span>
            <el-button type="text" style="float: right;" @click="$router.push('/supply-chain/purchase-order')">
              查看全部 <i class="el-icon-arrow-right"></i>
            </el-button>
          </template>
          <el-table :data="recentOrders" style="width: 100%" size="small">
            <el-table-column prop="orderNo" label="订单编号" min-width="150" />
            <el-table-column prop="totalAmount" label="订单金额" min-width="100">
              <template slot-scope="scope">
                ¥{{ scope.row.totalAmount }}
              </template>
            </el-table-column>
            <el-table-column prop="orderStatus" label="订单状态" min-width="100">
              <template slot-scope="scope">
                <el-tag :type="getStatusType(scope.row.orderStatus)">
                  {{ getStatusText(scope.row.orderStatus) }}
                </el-tag>
              </template>
            </el-table-column>
            <el-table-column prop="approvalStatus" label="审批状态" min-width="100">
              <template slot-scope="scope">
                <el-tag :type="getApprovalType(scope.row.approvalStatus)">
                  {{ getApprovalText(scope.row.approvalStatus) }}
                </el-tag>
              </template>
            </el-table-column>
          </el-table>
          <el-empty v-if="recentOrders.length === 0" description="暂无采购订单" />
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script>
import { getTodoTasks } from '@/api/workflow'

export default {
  name: 'Dashboard',
  data() {
    return {
      statistics: {
        pendingOrders: 0,
        approvedOrders: 0,
        supplierCount: 0,
        materialCount: 0
      },
      todoTasks: [],
      recentOrders: []
    }
  },
  created() {
    this.loadData()
  },
  methods: {
    loadData() {
      const userId = this.$store.state.user.userId
      if (userId) {
        getTodoTasks(userId).then(res => {
          this.todoTasks = res.data || []
          this.statistics.pendingOrders = this.todoTasks.filter(t => t.processDefinitionKey === 'purchase-order-approval').length
        })
      }
    },
    formatTime(time) {
      if (!time) return ''
      return new Date(time).toLocaleString()
    },
    handleTask(row) {
      this.$router.push('/workflow/todo')
    },
    getStatusType(status) {
      const types = {
        DRAFT: 'info',
        SUBMITTED: 'warning',
        APPROVING: 'warning',
        APPROVED: 'success',
        REJECTED: 'danger'
      }
      return types[status] || 'info'
    },
    getStatusText(status) {
      const texts = {
        DRAFT: '草稿',
        SUBMITTED: '已提交',
        APPROVING: '审批中',
        APPROVED: '已完成',
        REJECTED: '已拒绝'
      }
      return texts[status] || status
    },
    getApprovalType(status) {
      const types = {
        PENDING: 'warning',
        APPROVING: 'warning',
        APPROVED: 'success',
        REJECTED: 'danger'
      }
      return types[status] || 'info'
    },
    getApprovalText(status) {
      const texts = {
        PENDING: '待审批',
        APPROVING: '审批中',
        APPROVED: '已通过',
        REJECTED: '已拒绝'
      }
      return texts[status] || status
    }
  }
}
</script>

<style lang="scss" scoped>
.dashboard-container {
  padding: 0;
}

.box-card {
  .card-content {
    display: flex;
    align-items: center;

    .card-icon {
      width: 60px;
      height: 60px;
      border-radius: 8px;
      background-color: #409eff;
      display: flex;
      align-items: center;
      justify-content: center;
      margin-right: 20px;

      i {
        font-size: 30px;
        color: #fff;
      }
    }

    .card-info {
      .card-value {
        font-size: 28px;
        font-weight: bold;
        color: #333;
      }

      .card-label {
        font-size: 14px;
        color: #999;
        margin-top: 5px;
      }
    }
  }
}
</style>
