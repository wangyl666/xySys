<template>
  <div class="done-container">
    <el-card>
      <template slot="header">
        <span>已办任务</span>
      </template>
      <el-table :data="doneTasks" v-loading="loading" stripe style="width: 100%">
        <el-table-column prop="taskName" label="任务名称" min-width="150" />
        <el-table-column prop="processDefinitionName" label="流程名称" min-width="120" />
        <el-table-column prop="businessKey" label="业务编号" min-width="150" />
        <el-table-column prop="deleteReason" label="处理结果" min-width="100">
          <template slot-scope="scope">
            <el-tag :type="getResultType(scope.row.deleteReason)">
              {{ getResultText(scope.row.deleteReason) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="创建时间" min-width="160">
          <template slot-scope="scope">
            {{ formatTime(scope.row.createTime) }}
          </template>
        </el-table-column>
        <el-table-column prop="endTime" label="完成时间" min-width="160">
          <template slot-scope="scope">
            {{ formatTime(scope.row.endTime) }}
          </template>
        </el-table-column>
        <el-table-column label="时长" min-width="100">
          <template slot-scope="scope">
            {{ formatDuration(scope.row.durationInMillis) }}
          </template>
        </el-table-column>
      </el-table>
      <el-empty v-if="doneTasks.length === 0 && !loading" description="暂无已办任务" />
    </el-card>
  </div>
</template>

<script>
import { getDoneTasks } from '@/api/workflow'

export default {
  name: 'DoneTask',
  data() {
    return {
      loading: false,
      doneTasks: []
    }
  },
  created() {
    this.loadDoneTasks()
  },
  methods: {
    loadDoneTasks() {
      this.loading = true
      const userId = this.$store.state.user.userId
      if (!userId) {
        this.loading = false
        return
      }
      getDoneTasks(userId)
        .then(res => {
          this.doneTasks = res.data || []
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
    formatDuration(millis) {
      if (!millis) return '-'
      const seconds = Math.floor(millis / 1000)
      const minutes = Math.floor(seconds / 60)
      const hours = Math.floor(minutes / 60)
      const days = Math.floor(hours / 24)
      
      if (days > 0) {
        return `${days}天${hours % 24}小时`
      } else if (hours > 0) {
        return `${hours}小时${minutes % 60}分钟`
      } else if (minutes > 0) {
        return `${minutes}分钟`
      } else {
        return `${seconds}秒`
      }
    },
    getResultType(reason) {
      if (reason && reason.toLowerCase().includes('approved')) {
        return 'success'
      } else if (reason && reason.toLowerCase().includes('reject')) {
        return 'danger'
      }
      return 'info'
    },
    getResultText(reason) {
      if (reason && reason.toLowerCase().includes('approved')) {
        return '通过'
      } else if (reason && reason.toLowerCase().includes('reject')) {
        return '拒绝'
      }
      return '完成'
    }
  }
}
</script>

<style lang="scss" scoped>
.done-container {
  padding: 0;
}
</style>
