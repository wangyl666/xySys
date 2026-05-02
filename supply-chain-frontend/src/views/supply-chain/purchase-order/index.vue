<template>
  <div class="purchase-order-container">
    <el-card>
      <template slot="header">
        <div class="header-container">
          <span>采购订单管理</span>
          <el-button type="primary" @click="handleAdd">
            <i class="el-icon-plus"></i> 新建订单
          </el-button>
        </div>
      </template>

      <el-form :inline="true" :model="queryForm" class="demo-form-inline">
        <el-form-item label="订单编号">
          <el-input v-model="queryForm.orderNo" placeholder="请输入订单编号" clearable />
        </el-form-item>
        <el-form-item label="订单状态">
          <el-select v-model="queryForm.orderStatus" placeholder="请选择订单状态" clearable>
            <el-option label="草稿" value="DRAFT" />
            <el-option label="已提交" value="SUBMITTED" />
            <el-option label="审批中" value="APPROVING" />
            <el-option label="已审批" value="APPROVED" />
            <el-option label="已拒绝" value="REJECTED" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleQuery">查询</el-button>
          <el-button @click="handleReset">重置</el-button>
        </el-form-item>
      </el-form>

      <el-table :data="orderList" v-loading="loading" stripe style="width: 100%">
        <el-table-column prop="orderNo" label="订单编号" min-width="180" />
        <el-table-column prop="supplierId" label="供应商" min-width="150">
          <template slot-scope="scope">
            {{ getSupplierName(scope.row.supplierId) }}
          </template>
        </el-table-column>
        <el-table-column prop="totalAmount" label="订单金额" min-width="120">
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
        <el-table-column prop="flowCode" label="审批流" min-width="150">
          <template slot-scope="scope">
            <template v-if="scope.row.flowCode">
              <el-tag size="small" type="primary">{{ scope.row.flowCode }}</el-tag>
              <div style="margin-top: 4px; font-size: 12px; color: #909399;">
                {{ getFlowName(scope.row.flowCode) }}
              </div>
            </template>
            <span v-else style="color: #909399;">-</span>
          </template>
        </el-table-column>
        <el-table-column prop="orderDate" label="订单日期" min-width="160">
          <template slot-scope="scope">
            {{ formatTime(scope.row.orderDate) }}
          </template>
        </el-table-column>
        <el-table-column label="操作" min-width="250" fixed="right">
          <template slot-scope="scope">
            <el-button
              type="text"
              size="small"
              @click="handleView(scope.row)"
            >
              查看
            </el-button>
            <el-button
              v-if="scope.row.orderStatus === 'DRAFT'"
              type="text"
              size="small"
              @click="handleEdit(scope.row)"
            >
              编辑
            </el-button>
            <el-button
              v-if="scope.row.orderStatus === 'DRAFT'"
              type="text"
              size="small"
              @click="handleSubmit(scope.row)"
            >
              提交
            </el-button>
            <el-button
              v-if="scope.row.orderStatus === 'DRAFT'"
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
      title="新建采购订单"
      :visible.sync="dialogVisible"
      width="900px"
      :close-on-click-modal="false"
    >
      <el-form :model="orderForm" :rules="orderRules" ref="orderForm" label-width="120px">
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="供应商" prop="supplierId">
              <el-select
                v-model="orderForm.supplierId"
                placeholder="请选择供应商"
                style="width: 100%;"
                filterable
              >
                <el-option
                  v-for="item in supplierList"
                  :key="item.id"
                  :label="item.supplierName"
                  :value="item.id"
                />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="预计交货日期" prop="expectedDeliveryDate">
              <el-date-picker
                v-model="orderForm.expectedDeliveryDate"
                type="datetime"
                placeholder="选择预计交货日期"
                style="width: 100%;"
              />
            </el-form-item>
          </el-col>
        </el-row>
        <el-form-item label="备注">
          <el-input
            v-model="orderForm.remark"
            type="textarea"
            :rows="2"
            placeholder="请输入备注"
          />
        </el-form-item>
        <el-form-item label="订单明细">
          <el-table :data="orderForm.items" style="width: 100%" border>
            <el-table-column prop="materialName" label="物料名称" width="200">
              <template slot-scope="scope">
                <el-select
                  v-model="scope.row.materialId"
                  placeholder="选择物料"
                  @change="handleMaterialChange(scope.$index, scope.row.materialId)"
                  filterable
                  style="width: 100%;"
                >
                  <el-option
                    v-for="item in materialList"
                    :key="item.id"
                    :label="item.materialName"
                    :value="item.id"
                  />
                </el-select>
              </template>
            </el-table-column>
            <el-table-column prop="specification" label="规格型号" width="150">
              <template slot-scope="scope">
                {{ getMaterialField(scope.row.materialId, 'specification') }}
              </template>
            </el-table-column>
            <el-table-column prop="unit" label="单位" width="80">
              <template slot-scope="scope">
                {{ getMaterialField(scope.row.materialId, 'unit') }}
              </template>
            </el-table-column>
            <el-table-column prop="unitPrice" label="单价" width="120">
              <template slot-scope="scope">
                <el-input-number
                  v-model="scope.row.unitPrice"
                  :precision="2"
                  :step="0.01"
                  :min="0"
                  style="width: 100%;"
                />
              </template>
            </el-table-column>
            <el-table-column prop="quantity" label="数量" width="100">
              <template slot-scope="scope">
                <el-input-number
                  v-model="scope.row.quantity"
                  :min="1"
                  style="width: 100%;"
                />
              </template>
            </el-table-column>
            <el-table-column prop="amount" label="金额" width="120">
              <template slot-scope="scope">
                ¥{{ (scope.row.unitPrice * scope.row.quantity).toFixed(2) }}
              </template>
            </el-table-column>
            <el-table-column label="操作" width="80">
              <template slot-scope="scope">
                <el-button
                  type="text"
                  size="small"
                  style="color: #f56c6c;"
                  @click="handleRemoveItem(scope.$index)"
                >
                  删除
                </el-button>
              </template>
            </el-table-column>
          </el-table>
          <el-button style="margin-top: 10px;" type="primary" size="small" @click="handleAddItem">
            <i class="el-icon-plus"></i> 添加物料
          </el-button>
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
      title="订单详情"
      :visible.sync="detailDialogVisible"
      width="800px"
    >
      <el-descriptions :column="2" border>
        <el-descriptions-item label="订单编号">{{ currentOrder.orderNo }}</el-descriptions-item>
        <el-descriptions-item label="供应商">{{ getSupplierName(currentOrder.supplierId) }}</el-descriptions-item>
        <el-descriptions-item label="订单金额">¥{{ currentOrder.totalAmount }}</el-descriptions-item>
        <el-descriptions-item label="订单状态">
          <el-tag :type="getStatusType(currentOrder.orderStatus)">
            {{ getStatusText(currentOrder.orderStatus) }}
          </el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="审批状态">
          <el-tag :type="getApprovalType(currentOrder.approvalStatus)">
            {{ getApprovalText(currentOrder.approvalStatus) }}
          </el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="订单日期">{{ formatTime(currentOrder.orderDate) }}</el-descriptions-item>
        <el-descriptions-item label="预计交货日期" :span="2">
          {{ formatTime(currentOrder.expectedDeliveryDate) }}
        </el-descriptions-item>
        <el-descriptions-item label="备注" :span="2">
          {{ currentOrder.remark || '无' }}
        </el-descriptions-item>
      </el-descriptions>

      <el-divider content-position="left">订单明细</el-divider>
      <el-table :data="currentOrderItems" border>
        <el-table-column prop="materialName" label="物料名称" min-width="150" />
        <el-table-column prop="specification" label="规格型号" min-width="120" />
        <el-table-column prop="unit" label="单位" width="80" />
        <el-table-column prop="unitPrice" label="单价" width="100">
          <template slot-scope="scope">
            ¥{{ scope.row.unitPrice }}
          </template>
        </el-table-column>
        <el-table-column prop="quantity" label="数量" width="80" />
        <el-table-column prop="amount" label="金额" width="120">
          <template slot-scope="scope">
            ¥{{ scope.row.amount }}
          </template>
        </el-table-column>
      </el-table>
    </el-dialog>

    <el-dialog
      title="选择审批流"
      :visible.sync="selectFlowDialogVisible"
      width="600px"
      :close-on-click-modal="false"
    >
      <div class="select-flow-info">
        <el-alert title="请选择审批流程" type="info" :closable="false" show-icon>
          <template slot="default">
            <p>订单编号：<strong>{{ currentSubmitOrder?.orderNo }}</strong></p>
            <p>订单金额：<strong>¥{{ currentSubmitOrder?.totalAmount }}</strong></p>
          </template>
        </el-alert>
      </div>

      <el-divider content-position="left">可用审批流</el-divider>

      <el-table 
        :data="flowConfigList" 
        v-loading="flowConfigList.length === 0" 
        stripe
        highlight-current-row
        @current-row-key="id"
        @row-click="(row) => { selectedFlowConfigId = row.id }"
      >
        <el-table-column label="选择" width="60">
          <template slot-scope="scope">
            <el-radio 
              v-model="selectedFlowConfigId" 
              :label="scope.row.id"
              @change="() => {}"
            />
          </template>
        </el-table-column>
        <el-table-column prop="flowCode" label="审批流编码" min-width="150" />
        <el-table-column prop="flowName" label="审批流名称" min-width="150">
          <template slot-scope="scope">
            <el-tag size="small" type="success">{{ getFlowName(scope.row.flowCode) }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="isDefault" label="默认" width="70">
          <template slot-scope="scope">
            <el-tag v-if="scope.row.isDefault === 1" type="warning" size="small">默认</el-tag>
            <span v-else>-</span>
          </template>
        </el-table-column>
        <el-table-column prop="description" label="描述" min-width="120" show-overflow-tooltip />
      </el-table>

      <el-empty v-if="flowConfigList.length === 0" description="暂无可用的审批流配置" />

      <span slot="footer" class="dialog-footer">
        <el-button @click="handleCancelSelectFlow">取消</el-button>
        <el-button type="primary" :loading="submitLoading" @click="handleConfirmSubmit">
          确认提交
        </el-button>
      </span>
    </el-dialog>
  </div>
</template>

<script>
import {
  getPurchaseOrderPage,
  createPurchaseOrder,
  submitPurchaseOrder,
  submitPurchaseOrderWithFlowConfig,
  deletePurchaseOrder,
  getPurchaseOrderItems
} from '@/api/purchase-order'
import { getBillFlowConfigListByBillTypeCode } from '@/api/bill-flow-config'
import { getApprovalFlowPage } from '@/api/approval-flow'

export default {
  name: 'PurchaseOrder',
  data() {
    const validateItems = (rule, value, callback) => {
      if (!value || value.length === 0) {
        callback(new Error('请添加至少一条订单明细'))
      } else {
        const hasInvalid = value.some(item => !item.materialId || !item.quantity || !item.unitPrice)
        if (hasInvalid) {
          callback(new Error('请完善订单明细信息'))
        } else {
          callback()
        }
      }
    }
    return {
      loading: false,
      submitLoading: false,
      total: 0,
      queryForm: {
        orderNo: '',
        orderStatus: '',
        current: 1,
        size: 10
      },
      orderList: [],
      supplierList: [
        { id: 1, supplierName: '北京供应商有限公司' },
        { id: 2, supplierName: '上海供应链科技有限公司' },
        { id: 3, supplierName: '广州物资贸易有限公司' }
      ],
      materialList: [
        { id: 1, materialName: 'STM32F103芯片', specification: 'STM32F103ZET6', unit: '个', unitPrice: 25.50 },
        { id: 2, materialName: '10K电阻', specification: '0805 10K ±5%', unit: '个', unitPrice: 0.05 },
        { id: 3, materialName: '104电容', specification: '0805 104 50V', unit: '个', unitPrice: 0.10 },
        { id: 4, materialName: '激光打印机', specification: 'HP M403dn', unit: '台', unitPrice: 2500.00 },
        { id: 5, materialName: '工业电机', specification: 'Y132S-4 5.5KW', unit: '台', unitPrice: 8500.00 }
      ],
      flowConfigList: [],
      flowList: [],
      selectedFlowConfigId: null,
      selectFlowDialogVisible: false,
      currentSubmitOrder: null,
      dialogVisible: false,
      detailDialogVisible: false,
      currentOrder: {},
      currentOrderItems: [],
      orderForm: {
        supplierId: null,
        expectedDeliveryDate: '',
        remark: '',
        items: []
      },
      orderRules: {
        supplierId: [{ required: true, message: '请选择供应商', trigger: 'change' }],
        items: [{ required: true, validator: validateItems, trigger: 'change' }]
      }
    }
  },
  created() {
    this.loadOrderList()
    this.loadFlowConfigList()
    this.loadFlowList()
  },
  methods: {
    loadFlowConfigList() {
      getBillFlowConfigListByBillTypeCode('purchase_order')
        .then(res => {
          this.flowConfigList = res.data || []
          const defaultConfig = this.flowConfigList.find(c => c.isDefault === 1)
          if (defaultConfig) {
            this.selectedFlowConfigId = defaultConfig.id
          }
        })
        .catch(() => {
          this.flowConfigList = []
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
    getFlowName(flowCode) {
      const flow = this.flowList.find(f => f.flowCode === flowCode)
      return flow ? flow.flowName : flowCode
    },
    loadOrderList() {
      this.loading = true
      const params = {
        current: this.queryForm.current,
        size: this.queryForm.size,
        orderNo: this.queryForm.orderNo,
        orderStatus: this.queryForm.orderStatus
      }
      getPurchaseOrderPage(params)
        .then(res => {
          this.orderList = res.data?.records || []
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
    },
    getSupplierName(id) {
      const supplier = this.supplierList.find(s => s.id === id)
      return supplier ? supplier.supplierName : id
    },
    getMaterialField(id, field) {
      const material = this.materialList.find(m => m.id === id)
      return material ? material[field] : ''
    },
    handleQuery() {
      this.queryForm.current = 1
      this.loadOrderList()
    },
    handleReset() {
      this.queryForm = {
        orderNo: '',
        orderStatus: '',
        current: 1,
        size: 10
      }
      this.loadOrderList()
    },
    handleSizeChange(val) {
      this.queryForm.size = val
      this.loadOrderList()
    },
    handleCurrentChange(val) {
      this.queryForm.current = val
      this.loadOrderList()
    },
    handleAdd() {
      this.orderForm = {
        supplierId: null,
        expectedDeliveryDate: '',
        remark: '',
        items: [
          { materialId: null, unitPrice: 0, quantity: 1 }
        ]
      }
      this.dialogVisible = true
    },
    handleEdit(row) {
      this.$message.info('编辑功能需要先查询订单明细')
    },
    handleView(row) {
      this.currentOrder = row
      getPurchaseOrderItems(row.id).then(res => {
        this.currentOrderItems = res.data || []
      }).catch(() => {
        this.currentOrderItems = []
      })
      this.detailDialogVisible = true
    },
    handleSubmit(row) {
      this.currentSubmitOrder = row
      const defaultConfig = this.flowConfigList.find(c => c.isDefault === 1)
      this.selectedFlowConfigId = defaultConfig ? defaultConfig.id : null
      this.selectFlowDialogVisible = true
    },
    handleConfirmSubmit() {
      this.selectFlowDialogVisible = false
      
      const orderId = this.currentSubmitOrder.id
      const flowConfigId = this.selectedFlowConfigId
      
      this.submitLoading = true
      
      const promise = flowConfigId 
        ? submitPurchaseOrderWithFlowConfig(orderId, flowConfigId)
        : submitPurchaseOrder(orderId)
      
      promise
        .then(() => {
          this.$message.success('提交成功')
          this.loadOrderList()
          this.submitLoading = false
          this.currentSubmitOrder = null
        })
        .catch(() => {
          this.submitLoading = false
        })
    },
    handleCancelSelectFlow() {
      this.selectFlowDialogVisible = false
      this.currentSubmitOrder = null
    },
    handleDelete(row) {
      this.$confirm('确定要删除该订单吗?', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      })
        .then(() => {
          deletePurchaseOrder(row.id)
            .then(() => {
              this.$message.success('删除成功')
              this.loadOrderList()
            })
        })
        .catch(() => {})
    },
    handleAddItem() {
      this.orderForm.items.push({
        materialId: null,
        unitPrice: 0,
        quantity: 1
      })
    },
    handleRemoveItem(index) {
      this.orderForm.items.splice(index, 1)
    },
    handleMaterialChange(index, materialId) {
      const material = this.materialList.find(m => m.id === materialId)
      if (material) {
        this.orderForm.items[index].unitPrice = material.unitPrice
      }
    },
    handleSubmitForm() {
      this.$refs.orderForm.validate(valid => {
        if (valid) {
          this.submitLoading = true
          const data = {
            supplierId: this.orderForm.supplierId,
            expectedDeliveryDate: this.orderForm.expectedDeliveryDate,
            remark: this.orderForm.remark,
            items: this.orderForm.items
          }
          createPurchaseOrder(data)
            .then(() => {
              this.$message.success('保存成功')
              this.dialogVisible = false
              this.submitLoading = false
              this.loadOrderList()
            })
            .catch(() => {
              this.submitLoading = false
            })
        } else {
          return false
        }
      })
    }
  }
}
</script>

<style lang="scss" scoped>
.purchase-order-container {
  padding: 0;

  .header-container {
    display: flex;
    justify-content: space-between;
    align-items: center;
  }
}

.select-flow-info {
  margin-bottom: 15px;

  p {
    margin: 8px 0;
    font-size: 14px;
  }
}
</style>
