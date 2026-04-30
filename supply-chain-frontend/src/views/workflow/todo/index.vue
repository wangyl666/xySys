<template>
  <div class="todo-container">
    <el-card>
      <template slot="header">
        <span>待办任务</span>
      </template>
      <el-table :data="todoTasks" v-loading="loading" stripe style="width: 100%">
        <el-table-column prop="taskName" label="任务名称" min-width="150" />
        <el-table-column prop="processDefinitionName" label="流程名称" min-width="120" />
        <el-table-column prop="businessKey" label="业务编号" min-width="150" />
        <el-table-column prop="assignee" label="当前审批人" min-width="100">
          <template slot-scope="scope">
            {{ scope.row.assignee || '待认领' }}
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="创建时间" min-width="160">
          <template slot-scope="scope">
            {{ formatTime(scope.row.createTime) }}
          </template>
        </el-table-column>
        <el-table-column label="操作" min-width="250" fixed="right">
          <template slot-scope="scope">
            <el-button
              v-if="!scope.row.assignee"
              type="primary"
              size="small"
              @click="handleClaim(scope.row)"
            >
              认领
            </el-button>
            <el-button
              v-else-if="scope.row.assignee === currentUserId"
              type="success"
              size="small"
              @click="handleApprove(scope.row)"
            >
              审批
            </el-button>
            <el-button
              type="text"
              size="small"
              @click="handleView(scope.row)"
            >
              查看详情
            </el-button>
          </template>
        </el-table-column>
      </el-table>
      <el-empty v-if="todoTasks.length === 0 && !loading" description="暂无待办任务" />
    </el-card>

    <el-dialog
      title="审批任务"
      :visible.sync="approveDialogVisible"
      width="500px"
    >
      <el-form :model="approveForm" label-width="100px">
        <el-form-item label="审批意见">
          <el-input
            v-model="approveForm.comment"
            type="textarea"
            :rows="4"
            placeholder="请输入审批意见"
          />
        </el-form-item>
      </el-form>
      <span slot="footer" class="dialog-footer">
        <el-button @click="approveDialogVisible = false">取消</el-button>
        <el-button type="danger" @click="handleReject">拒绝</el-button>
        <el-button type="primary" @click="handleApproveSubmit">通过</el-button>
      </span>
    </el-dialog>

    <el-dialog
      title="任务详情"
      :visible.sync="detailDialogVisible"
      width="600px"
    >
      <el-descriptions :column="2" border>
        <el-descriptions-item label="任务名称">{{ currentTask.taskName }}</el-descriptions-item>
        <el-descriptions-item label="流程名称">{{ currentTask.processDefinitionName }}</el-descriptions-item>
        <el-descriptions-item label="业务编号">{{ currentTask.businessKey }}</el-descriptions-item>
        <el-descriptions-item label="当前审批人">{{ currentTask.assignee || '待认领' }}</el-descriptions-item>
        <el-descriptions-item label="创建时间" :span="2">
          {{ formatTime(currentTask.createTime) }}
        </el-descriptions-item>
      </el-descriptions>
    </el-dialog>
  </div>
</template>

<script>
import { getTodoTasks, claimTask, approveTask, rejectTask } from '@/api/workflow'
import { mapState } from 'vuex'

export default {
  name: 'TodoTask',
  data() {
    return {
      loading: false,
      todoTasks: [],
      approveDialogVisible: false,
      detailDialogVisible: false,
      currentTask: {},
      approveForm: {
        comment: ''
      }
    }
  },
  computed: {
    ...mapState({
      currentUserId: state => state.user.userId
    })
  },
  created() {
    this.loadTodoTasks()
  },
  methods: {
    loadTodoTasks() {
      this.loading = true
      const userId = this.$store.state.user.userId
      if (!userId) {
        this.loading = false
        return
      }
      getTodoTasks(userId)
        .then(res => {
          this.todoTasks = res.data || []
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
    handleClaim(row) {
      const userId = this.$store.state.user.userId
      this.$confirm('确定要认领该任务吗?', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      })
        .then(() => {
          claimTask(row.taskId, userId)
            .then(() => {
              this.$message.success('认领成功')
              this.loadTodoTasks()
            })
        })
        .catch(() => {})
    },
    handleApprove(row) {
      this.currentTask = row
      this.approveForm.comment = ''
      this.approveDialogVisible = true
    },
    handleView(row) {
      this.currentTask = row
      this.detailDialogVisible = true
    },
    handleApproveSubmit() {
      this.$confirm('确定要通过该审批吗?', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      })
        .then(() => {
          approveTask(this.currentTask.taskId, this.approveForm.comment)
            .then(() => {
              this.$message.success('审批通过')
              this.approveDialogVisible = false
              this.loadTodoTasks()
            })
        })
        .catch(() => {})
    },
    handleReject() {
      this.$confirm('确定要拒绝该审批吗?', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      })
        .then(() => {
          rejectTask(this.currentTask.taskId, this.approveForm.comment)
            .then(() => {
              this.$message.success('已拒绝')
              this.approveDialogVisible = false
              this.loadTodoTasks()
            })
        })
        .catch(() => {})
    }
  }
}
</script>

<style lang="scss" scoped>
.todo-container {
  padding: 0;
}
</style>
