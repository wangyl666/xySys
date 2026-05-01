import Vue from 'vue'
import VueRouter from 'vue-router'
import Layout from '@/layout/index.vue'

Vue.use(VueRouter)

const routes = [
  {
    path: '/login',
    name: 'Login',
    component: () => import('@/views/login/index.vue'),
    meta: { title: '登录', hidden: true }
  },
  {
    path: '/',
    component: Layout,
    redirect: '/dashboard',
    children: [
      {
        path: 'dashboard',
        name: 'Dashboard',
        component: () => import('@/views/dashboard/index.vue'),
        meta: { title: '首页', icon: 'el-icon-s-home', roles: ['SUPER_ADMIN', 'PURCHASE_MANAGER', 'PURCHASE_STAFF', 'FINANCE_STAFF'] }
      }
    ]
  },
  {
    path: '/system',
    component: Layout,
    redirect: '/system/user',
    name: 'System',
    meta: { title: '系统管理', icon: 'el-icon-s-tools', roles: ['SUPER_ADMIN'] },
    children: [
      {
        path: 'user',
        name: 'User',
        component: () => import('@/views/system/user/index.vue'),
        meta: { title: '用户管理', icon: 'el-icon-user-solid', roles: ['SUPER_ADMIN'] }
      },
      {
        path: 'role',
        name: 'Role',
        component: () => import('@/views/system/role/index.vue'),
        meta: { title: '角色管理', icon: 'el-icon-s-custom', roles: ['SUPER_ADMIN'] }
      }
    ]
  },
  {
    path: '/supply-chain',
    component: Layout,
    redirect: '/supply-chain/supplier',
    name: 'SupplyChain',
    meta: { title: '供应链管理', icon: 'el-icon-s-goods', roles: ['SUPER_ADMIN', 'PURCHASE_MANAGER', 'PURCHASE_STAFF'] },
    children: [
      {
        path: 'supplier',
        name: 'Supplier',
        component: () => import('@/views/supply-chain/supplier/index.vue'),
        meta: { title: '供应商管理', icon: 'el-icon-office-building', roles: ['SUPER_ADMIN', 'PURCHASE_MANAGER', 'PURCHASE_STAFF'] }
      },
      {
        path: 'material',
        name: 'Material',
        component: () => import('@/views/supply-chain/material/index.vue'),
        meta: { title: '物料管理', icon: 'el-icon-box', roles: ['SUPER_ADMIN', 'PURCHASE_MANAGER', 'PURCHASE_STAFF'] }
      },
      {
        path: 'purchase-order',
        name: 'PurchaseOrder',
        component: () => import('@/views/supply-chain/purchase-order/index.vue'),
        meta: { title: '采购订单', icon: 'el-icon-document', roles: ['SUPER_ADMIN', 'PURCHASE_MANAGER', 'PURCHASE_STAFF'] }
      }
    ]
  },
  {
    path: '/workflow',
    component: Layout,
    redirect: '/workflow/todo',
    name: 'Workflow',
    meta: { title: '工作流管理', icon: 'el-icon-s-claim', roles: ['SUPER_ADMIN', 'PURCHASE_MANAGER', 'PURCHASE_STAFF', 'FINANCE_STAFF'] },
    children: [
      {
        path: 'todo',
        name: 'TodoTask',
        component: () => import('@/views/workflow/todo/index.vue'),
        meta: { title: '待办任务', icon: 'el-icon-message-solid', roles: ['SUPER_ADMIN', 'PURCHASE_MANAGER', 'PURCHASE_STAFF', 'FINANCE_STAFF'] }
      },
      {
        path: 'done',
        name: 'DoneTask',
        component: () => import('@/views/workflow/done/index.vue'),
        meta: { title: '已办任务', icon: 'el-icon-s-order', roles: ['SUPER_ADMIN', 'PURCHASE_MANAGER', 'PURCHASE_STAFF', 'FINANCE_STAFF'] }
      },
      {
        path: 'my-process',
        name: 'MyProcess',
        component: () => import('@/views/workflow/my-process/index.vue'),
        meta: { title: '我的流程', icon: 'el-icon-s-promotion', roles: ['SUPER_ADMIN', 'PURCHASE_MANAGER', 'PURCHASE_STAFF', 'FINANCE_STAFF'] }
      },
      {
        path: 'approval-flow',
        name: 'ApprovalFlow',
        component: () => import('@/views/workflow/approval-flow/index.vue'),
        meta: { title: '审批流配置', icon: 'el-icon-setting', roles: ['SUPER_ADMIN'] }
      },
      {
        path: 'bill-type',
        name: 'BillType',
        component: () => import('@/views/workflow/bill-type/index.vue'),
        meta: { title: '单据类型', icon: 'el-icon-tickets', roles: ['SUPER_ADMIN'] }
      },
      {
        path: 'bill-flow-config',
        name: 'BillFlowConfig',
        component: () => import('@/views/workflow/bill-flow-config/index.vue'),
        meta: { title: '单据审批流配置', icon: 'el-icon-connection', roles: ['SUPER_ADMIN'] }
      }
    ]
  }
]

const router = new VueRouter({
  mode: 'history',
  base: process.env.BASE_URL,
  routes
})

export default router
