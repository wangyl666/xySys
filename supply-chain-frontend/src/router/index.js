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
        meta: { title: '首页', icon: 'el-icon-s-home' }
      }
    ]
  },
  {
    path: '/system',
    component: Layout,
    redirect: '/system/user',
    name: 'System',
    meta: { title: '系统管理', icon: 'el-icon-s-tools' },
    children: [
      {
        path: 'user',
        name: 'User',
        component: () => import('@/views/system/user/index.vue'),
        meta: { title: '用户管理', icon: 'el-icon-user-solid' }
      },
      {
        path: 'role',
        name: 'Role',
        component: () => import('@/views/system/role/index.vue'),
        meta: { title: '角色管理', icon: 'el-icon-s-custom' }
      }
    ]
  },
  {
    path: '/supply-chain',
    component: Layout,
    redirect: '/supply-chain/supplier',
    name: 'SupplyChain',
    meta: { title: '供应链管理', icon: 'el-icon-s-goods' },
    children: [
      {
        path: 'supplier',
        name: 'Supplier',
        component: () => import('@/views/supply-chain/supplier/index.vue'),
        meta: { title: '供应商管理', icon: 'el-icon-office-building' }
      },
      {
        path: 'material',
        name: 'Material',
        component: () => import('@/views/supply-chain/material/index.vue'),
        meta: { title: '物料管理', icon: 'el-icon-box' }
      },
      {
        path: 'purchase-order',
        name: 'PurchaseOrder',
        component: () => import('@/views/supply-chain/purchase-order/index.vue'),
        meta: { title: '采购订单', icon: 'el-icon-document' }
      }
    ]
  },
  {
    path: '/workflow',
    component: Layout,
    redirect: '/workflow/todo',
    name: 'Workflow',
    meta: { title: '工作流管理', icon: 'el-icon-s-claim' },
    children: [
      {
        path: 'todo',
        name: 'TodoTask',
        component: () => import('@/views/workflow/todo/index.vue'),
        meta: { title: '待办任务', icon: 'el-icon-message-solid' }
      },
      {
        path: 'done',
        name: 'DoneTask',
        component: () => import('@/views/workflow/done/index.vue'),
        meta: { title: '已办任务', icon: 'el-icon-s-order' }
      },
      {
        path: 'my-process',
        name: 'MyProcess',
        component: () => import('@/views/workflow/my-process/index.vue'),
        meta: { title: '我的流程', icon: 'el-icon-s-promotion' }
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
