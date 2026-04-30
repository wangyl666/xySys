<template>
  <div class="app-wrapper">
    <aside class="app-aside" :class="{ 'is-collapsed': !sidebar.opened }">
      <div class="logo">
        <h2 v-if="sidebar.opened">供应链管理系统</h2>
        <h2 v-else>SCM</h2>
      </div>
      <el-menu
        :default-active="activeMenu"
        :collapse="!sidebar.opened"
        :unique-opened="true"
        :router="true"
        background-color="#304156"
        text-color="#bfcbd9"
        active-text-color="#409EFF"
      >
        <sidebar-item v-for="route in permission_routes" :key="route.path" :item="route" :base-path="route.path" />
      </el-menu>
    </aside>

    <div class="app-main-container">
      <header class="app-header">
        <div class="header-left">
          <span class="collapse-btn" @click="toggleSideBar">
            <i :class="sidebar.opened ? 'el-icon-s-fold' : 'el-icon-s-unfold'"></i>
          </span>
          <el-breadcrumb separator="/">
            <el-breadcrumb-item v-for="item in $route.matched" :key="item.path">
              {{ item.meta.title }}
            </el-breadcrumb-item>
          </el-breadcrumb>
        </div>
        <div class="header-right">
          <el-dropdown @command="handleCommand">
            <span class="user-info">
              <el-avatar :size="32" icon="el-icon-user-solid"></el-avatar>
              <span class="username">{{ username }}</span>
              <i class="el-icon-arrow-down el-icon--right"></i>
            </span>
            <el-dropdown-menu slot="dropdown">
              <el-dropdown-item command="profile">个人中心</el-dropdown-item>
              <el-dropdown-item command="logout" divided>退出登录</el-dropdown-item>
            </el-dropdown-menu>
          </el-dropdown>
        </div>
      </header>

      <main class="app-main">
        <transition name="fade-transform" mode="out-in">
          <router-view />
        </transition>
      </main>
    </div>
  </div>
</template>

<script>
import { mapState, mapActions } from 'vuex'
import SidebarItem from './components/SidebarItem.vue'
import router from '@/router'

export default {
  name: 'Layout',
  components: { SidebarItem },
  computed: {
    ...mapState({
      sidebar: state => state.app.sidebar,
      device: state => state.app.device,
      username: state => state.user.name,
      roles: state => state.user.roles
    }),
    permission_routes() {
      const filterRoutes = (routes, roles) => {
        const res = []
        routes.forEach(route => {
          const tmp = { ...route }
          if (hasPermission(roles, tmp)) {
            if (tmp.children) {
              tmp.children = filterRoutes(tmp.children, roles)
            }
            res.push(tmp)
          }
        })
        return res
      }

      const hasPermission = (roles, route) => {
        if (route.meta && route.meta.roles) {
          return roles.some(role => route.meta.roles.includes(role))
        }
        return true
      }

      const accessedRoutes = filterRoutes(router.options.routes, this.roles)
      return accessedRoutes.filter(route => route.meta?.hidden !== true)
    },
    activeMenu() {
      const route = this.$route
      const { meta, path } = route
      if (meta.activeMenu) {
        return meta.activeMenu
      }
      return path
    }
  },
  methods: {
    ...mapActions({
      toggleSideBar: 'app/toggleSideBar',
      logout: 'user/logout'
    }),
    handleCommand(command) {
      if (command === 'logout') {
        this.$confirm('确定要退出登录吗?', '提示', {
          confirmButtonText: '确定',
          cancelButtonText: '取消',
          type: 'warning'
        }).then(() => {
          this.logout().then(() => {
            this.$router.push(`/login?redirect=${this.$route.fullPath}`)
            this.$message.success('退出成功')
          })
        }).catch(() => {})
      }
    }
  }
}
</script>

<style lang="scss" scoped>
.app-wrapper {
  display: flex;
  height: 100vh;
  width: 100%;
}

.app-aside {
  width: 210px;
  background-color: #304156;
  transition: width 0.3s;
  overflow: hidden;
  flex-shrink: 0;

  &.is-collapsed {
    width: 64px;
  }

  .logo {
    height: 50px;
    display: flex;
    align-items: center;
    justify-content: center;
    background-color: #2b3a4a;

    h2 {
      color: #fff;
      font-size: 18px;
      font-weight: 600;
      margin: 0;
      white-space: nowrap;
    }
  }

  ::v-deep .el-menu {
    border-right: none;
    width: 100%;
  }
}

.app-main-container {
  flex: 1;
  display: flex;
  flex-direction: column;
  overflow: hidden;
}

.app-header {
  height: 50px;
  background-color: #fff;
  box-shadow: 0 1px 4px rgba(0, 21, 41, 0.08);
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 0 20px;
  flex-shrink: 0;

  .header-left {
    display: flex;
    align-items: center;

    .collapse-btn {
      font-size: 20px;
      cursor: pointer;
      margin-right: 15px;
      padding: 5px;
      transition: background-color 0.3s;

      &:hover {
        background-color: #f5f7fa;
      }
    }
  }

  .header-right {
    display: flex;
    align-items: center;

    .user-info {
      display: flex;
      align-items: center;
      cursor: pointer;

      .username {
        margin: 0 8px;
        color: #606266;
      }
    }
  }
}

.app-main {
  flex: 1;
  padding: 20px;
  overflow-y: auto;
  background-color: #f5f7fa;
}

.fade-transform-leave-active,
.fade-transform-enter-active {
  transition: all 0.5s;
}

.fade-transform-enter {
  opacity: 0;
  transform: translateX(-30px);
}

.fade-transform-leave-to {
  opacity: 0;
  transform: translateX(30px);
}
</style>
