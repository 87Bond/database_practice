<template>
  <div id="app">
    <!-- 顶部导航栏 -->
    <nav class="nav-bar">
      <router-link v-if="!isLoggedIn" to="/login-register" class="nav-item">登录/注册</router-link>
      <router-link v-if="isLoggedIn && isPatient" to="/registration" class="nav-item">挂号</router-link>
      <router-link v-if="isLoggedIn" to="/message" class="nav-item">消息</router-link>
      <router-link v-if="isLoggedIn" to="/profile" class="nav-item">个人中心</router-link>
      <router-link v-if="isLoggedIn && isDoctor" to="/doctor-panel" class="nav-item">医生端</router-link>
      <router-link v-if="isLoggedIn && isAdmin" to="/admin-panel" class="nav-item">管理员</router-link>
    </nav>
    <!-- 页面容器（路由匹配的页面会显示在这里） -->
    <div class="page-container">
      <router-view />
    </div>
  </div>
</template>

<script>
export default {
  data () {
    return {
      userInfo: null
    }
  },
  computed: {
    isLoggedIn () {
      return !!this.userInfo
    },
    isPatient () {
      return this.userInfo && this.userInfo.role === 'patient'
    },
    isDoctor () {
      return this.userInfo && this.userInfo.role === 'doctor'
    },
    isAdmin () {
      return this.userInfo && this.userInfo.role === 'admin'
    }
  },
  created () {
    this.refreshUserInfo()
  },
  watch: {
    $route () {
      this.refreshUserInfo()
    }
  },
  methods: {
    refreshUserInfo () {
      const infoStr = localStorage.getItem('userInfo')
      if (infoStr) {
        try {
          this.userInfo = JSON.parse(infoStr)
        } catch (e) {
          this.userInfo = null
        }
      } else {
        this.userInfo = null
      }
    }
  }
}
</script>

<style>
/* 全局样式重置 */
* {
  margin: 0;
  padding: 0;
  box-sizing: border-box;
}
/* 导航栏样式 */
.nav-bar {
  background-color: #2f5496; /* 医疗蓝 */
  display: flex;
  padding: 0 20px;
}
.nav-item {
  color: white;
  text-decoration: none;
  padding: 15px 20px;
  font-size: 16px;
}
.nav-item:hover {
  background-color: #1f3a68;
}
/* 页面容器 */
.page-container {
  padding: 30px;
  max-width: 1200px;
  margin: 0 auto;
}
/* 路由激活样式（当前页导航高亮） */
.router-link-active {
  background-color: #1f3a68;
  font-weight: bold;
}
</style>
