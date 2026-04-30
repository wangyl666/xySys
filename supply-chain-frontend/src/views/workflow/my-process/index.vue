<template>
  <div class="my-process-container">
    <el-card>
      <template slot="header">
        <span>我的流程</span>
      </template>
      <el-tabs v-model="activeTab">
        <el-tab-pane label="我发起的" name="myStarted">
          <el-table :data="myStartedList" style="width: 100%" border>
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
            <el-table-column label="操作" width="150">
              <template slot-scope="scope">
                <el-button type="text" size="small">查看流程</el-button>
                <el-button type="text" size="small">查看详情</el-button>
              </template>
            </el-table-column>
          </el-table>
          <el-empty v-if="myStartedList.length === 0" description="暂无我发起的流程" />
        </el-tab-pane>
        <el-tab-pane label="我参与的" name="myInvolved">
          <el-table :data="myInvolvedList" style="width: 100%" border>
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
            <el-table-column label="操作" width="100">
              <template slot-scope="scope">
                <el-button type="text" size="small">查看详情</el-button>
              </template>
            </el-table-column>
          </el-table>
          <el-empty v-if="myInvolvedList.length === 0" description="暂无我参与的流程" />
        </el-tab-pane>
      </el-tabs>
    </el-card>
  </div>
</template>

<script>
export default {
  name: 'MyProcess',
  data() {
    return {
      activeTab: 'myStarted',
      myStartedList: [],
      myInvolvedList: []
    }
  },
  methods: {
    formatTime(time) {
      if (!time) return ''
      return new Date(time).toLocaleString()
    },
    getResultType(result) {
      const types = {
        approved: 'success',
        rejected: 'danger'
      }
      return types[result] || 'info'
    },
    getResultText(result) {
      const texts = {
        approved: '已通过',
        rejected: '已拒绝'
      }
      return texts[result] || result
    }
  }
}
</script>

<style lang="scss" scoped>
.my-process-container {
  padding: 0;
}
</style>
