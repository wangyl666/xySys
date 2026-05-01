<template>
  <div class="my-process-container">
    <el-card>
      <template slot="header">
        <span>我的流程</span>
      </template>
      <el-tabs v-model="activeTab" @tab-click="handleTabClick">
        <el-tab-pane label="我发起的" name="myStarted">
          <el-table :data="myStartedList" v-loading="myStartedLoading" stripe style="width: 100%" border>
            <el-table-column prop="processDefinitionName" label="流程名称" min-width="150" />
            <el-table-column prop="businessKey" label="业务编号" min-width="180" />
            <el-table-column prop="startTime" label="发起时间" min-width="160">
              <template slot-scope="scope">
                {{ formatTime(scope.row.startTime) }}
              </template>
            </el-table-column>
            <el-table-column prop="endTime" label="结束时间" min-width="160">
              <template slot-scope="scope">
                {{ scope.row.endTime ? formatTime(scope.row.endTime) : '-' }}
              </template>
            </el-table-column>
            <el-table-column prop="status" label="状态" width="100">
              <template slot-scope="scope">
                <el-tag :type="scope.row.status === 'running' ? 'warning' : 'info'">
                  {{ scope.row.status === 'running' ? '运行中' : '已结束' }}
                </el-tag>
              </template>
            </el-table-column>
            <el-table-column prop="result" label="结果" width="100">
              <template slot-scope="scope">
                <el-tag v-if="scope.row.status === 'ended'" :type="getResultType(scope.row.result)">
                  {{ getResultText(scope.row.result) }}
                </el-tag>
                <span v-else>-</span>
              </template>
            </el-table-column>
            <el-table-column label="操作" width="200">
              <template slot-scope="scope">
                <el-button type="text" size="small" @click="handleViewProcess(scope.row)">
                  查看流程
                </el-button>
                <el-button type="text" size="small" @click="handleViewHistory(scope.row)">
                  审批历史
                </el-button>
              </template>
            </el-table-column>
          </el-table>
          <el-empty v-if="myStartedList.length === 0 && !myStartedLoading" description="暂无我发起的流程" />
        </el-tab-pane>
        <el-tab-pane label="我参与的" name="myInvolved">
          <el-table :data="myInvolvedList" v-loading="myInvolvedLoading" stripe style="width: 100%" border>
            <el-table-column prop="processDefinitionName" label="流程名称" min-width="150" />
            <el-table-column prop="businessKey" label="业务编号" min-width="180" />
            <el-table-column prop="taskName" label="我处理的任务" min-width="150" />
            <el-table-column prop="startTime" label="流程发起时间" min-width="160">
              <template slot-scope="scope">
                {{ formatTime(scope.row.startTime) }}
              </template>
            </el-table-column>
            <el-table-column prop="handleTime" label="我处理的时间" min-width="160">
              <template slot-scope="scope">
                {{ formatTime(scope.row.handleTime) }}
              </template>
            </el-table-column>
            <el-table-column prop="result" label="我的处理结果" width="100">
              <template slot-scope="scope">
                <el-tag :type="getResultType(scope.row.result)">
                  {{ getResultText(scope.row.result) }}
                </el-tag>
              </template>
            </el-table-column>
            <el-table-column label="操作" width="150">
              <template slot-scope="scope">
                <el-button type="text" size="small" @click="handleViewHistory(scope.row)">
                  查看详情
                </el-button>
              </template>
            </el-table-column>
          </el-table>
          <el-empty v-if="myInvolvedList.length === 0 && !myInvolvedLoading" description="暂无我参与的流程" />
        </el-tab-pane>
      </el-tabs>
    </el-card>

    <el-dialog
      title="流程详情"
      :visible.sync="processDetailVisible"
      width="700px"
    >
      <el-descriptions :column="2" border>
        <el-descriptions-item label="流程实例ID">{{ currentProcess.processInstanceId }}</el-descriptions-item>
        <el-descriptions-item label="流程名称">{{ currentProcess.processDefinitionName }}</el-descriptions-item>
        <el-descriptions-item label="业务编号">{{ currentProcess.businessKey }}</el-descriptions-item>
        <el-descriptions-item label="状态">
          <el-tag :type="currentProcess.status === 'running' ? 'warning' : 'info'">
            {{ currentProcess.status === 'running' ? '运行中' : '已结束' }}
          </el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="发起时间" :span="2">
          {{ formatTime(currentProcess.startTime) }}
        </el-descriptions-item>
        <el-descriptions-item label="结束时间" :span="2" v-if="currentProcess.endTime">
          {{ formatTime(currentProcess.endTime) }}
        </el-descriptions-item>
      </el-descriptions>

      <el-divider content-position="left">当前活动节点</el-divider>
      <el-timeline v-if="activeActivities.length > 0">
        <el-timeline-item
          v-for="(activity, index) in activeActivities"
          :key="index"
          type="primary"
          :timestamp="formatTime(activity.startTime)"
        >
          <h4>{{ activity.activityName }}</h4>
          <p>类型: {{ getActivityTypeText(activity.activityType) }}</p>
          <p v-if="activity.assignee">审批人: {{ activity.assignee }}</p>
        </el-timeline-item>
      </el-timeline>
      <el-empty v-else description="当前没有活动节点" :image-size="60" />
    </el-dialog>

    <el-dialog
      title="审批历史"
      :visible.sync="historyDialogVisible"
      width="700px"
    >
      <el-table :data="historyTasks" stripe border>
        <el-table-column prop="taskName" label="任务名称" min-width="150" />
        <el-table-column prop="assignee" label="审批人" min-width="120">
          <template slot-scope="scope">
            {{ scope.row.assignee || '待认领' }}
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="创建时间" min-width="160">
          <template slot-scope="scope">
            {{ formatTime(scope.row.createTime) }}
          </template>
        </el-table-column>
        <el-table-column prop="endTime" label="结束时间" min-width="160">
          <template slot-scope="scope">
            {{ scope.row.endTime ? formatTime(scope.row.endTime) : '-' }}
          </template>
        </el-table-column>
        <el-table-column prop="deleteReason" label="处理结果" min-width="120">
          <template slot-scope="scope">
            <el-tag v-if="scope.row.endTime" :type="getDeleteReasonType(scope.row.deleteReason)">
              {{ getDeleteReasonText(scope.row.deleteReason) }}
            </el-tag>
            <el-tag v-else type="warning">处理中</el-tag>
          </template>
        </el-table-column>
      </el-table>
      <el-empty v-if="historyTasks.length === 0" description="暂无审批历史" />
    </el-dialog>
  </div>
</template>

<script>
import { mapState } from 'vuex'
import {
  getMyStartedProcesses,
  getMyInvolvedProcesses,
  getHistoryTasks,
  getActiveActivities
} from '@/api/workflow'

export default {
  name: 'MyProcess',
  data() {
    return {
      activeTab: 'myStarted',
      myStartedList: [],
      myInvolvedList: [],
      myStartedLoading: false,
      myInvolvedLoading: false,
      processDetailVisible: false,
      historyDialogVisible: false,
      currentProcess: {},
      activeActivities: [],
      historyTasks: []
    }
  },
  computed: {
    ...mapState({
      currentUserId: state => state.user.userId
    })
  },
  methods: {
    formatTime(time) {
      if (!time) return ''
      return new Date(time).toLocaleString()
    },
    getResultType(result) {
      const types = {
        approved: 'success',
        rejected: 'danger',
        completed: 'info'
      }
      return types[result] || 'info'
    },
    getResultText(result) {
      const texts = {
        approved: '已通过',
        rejected: '已拒绝',
        completed: '已完成'
      }
      return texts[result] || result
    },
    getActivityTypeText(type) {
      const texts = {
        startEvent: '开始事件',
        endEvent: '结束事件',
        userTask: '用户任务',
        exclusiveGateway: '排他网关',
        sequenceFlow: '顺序流'
      }
      return texts[type] || type
    },
    getDeleteReasonType(reason) {
      if (!reason) return 'info'
      if (reason.includes('approved') || reason.includes('通过')) return 'success'
      if (reason.includes('rejected') || reason.includes('拒绝')) return 'danger'
      return 'info'
    },
    getDeleteReasonText(reason) {
      if (!reason) return '未处理'
      if (reason.includes('approved')) return '已通过'
      if (reason.includes('rejected')) return '已拒绝'
      return reason
    },
    handleTabClick() {
      if (this.activeTab === 'myStarted') {
        this.loadMyStartedProcesses()
      } else {
        this.loadMyInvolvedProcesses()
      }
    },
    loadMyStartedProcesses() {
      const userId = this.currentUserId
      if (!userId) return

      this.myStartedLoading = true
      getMyStartedProcesses(userId)
        .then(res => {
          this.myStartedList = res.data || []
          this.myStartedLoading = false
        })
        .catch(() => {
          this.myStartedLoading = false
        })
    },
    loadMyInvolvedProcesses() {
      const userId = this.currentUserId
      if (!userId) return

      this.myInvolvedLoading = true
      getMyInvolvedProcesses(userId)
        .then(res => {
          this.myInvolvedList = res.data || []
          this.myInvolvedLoading = false
        })
        .catch(() => {
          this.myInvolvedLoading = false
        })
    },
    handleViewProcess(row) {
      this.currentProcess = { ...row }
      this.activeActivities = []
      
      if (row.status === 'running' && row.processInstanceId) {
        getActiveActivities(row.processInstanceId)
          .then(res => {
            this.activeActivities = res.data || []
          })
          .catch(() => {
            this.activeActivities = []
          })
      }
      
      this.processDetailVisible = true
    },
    handleViewHistory(row) {
      this.historyTasks = []
      
      if (row.processInstanceId) {
        getHistoryTasks(row.processInstanceId)
          .then(res => {
            this.historyTasks = res.data || []
          })
          .catch(() => {
            this.historyTasks = []
          })
      }
      
      this.historyDialogVisible = true
    }
  },
  created() {
    this.loadMyStartedProcesses()
  }
}
</script>

<style lang="scss" scoped>
.my-process-container {
  padding: 0;
}
</style>
