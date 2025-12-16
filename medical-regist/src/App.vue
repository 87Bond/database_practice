<template>
  <div id="app">
    <!-- 顶部导航栏 -->
    <nav class="nav-bar">
      <div class="brand">
        <span class="brand-icon">医+</span>
        <span class="brand-text">智慧医疗</span>
      </div>
      <div class="nav-links">
        <router-link v-if="!isLoggedIn" to="/login-register" class="nav-item">登录 / 注册</router-link>
        <router-link v-if="isLoggedIn && isPatient" to="/registration" class="nav-item">挂号</router-link>
        <router-link v-if="isLoggedIn" to="/message" class="nav-item">消息</router-link>
        <router-link v-if="isLoggedIn" to="/profile" class="nav-item">个人中心</router-link>
        <router-link v-if="isLoggedIn && isDoctor" to="/doctor-panel" class="nav-item">医生端</router-link>
        <router-link v-if="isLoggedIn && isDeptManager" to="/dept-panel" class="nav-item">科室端</router-link>
        <router-link v-if="isLoggedIn && isAdmin" to="/admin-panel" class="nav-item">系统管理</router-link>
      </div>
    </nav>
    <!-- 页面容器（路由匹配的页面会显示在这里） -->
    <div class="page-shell">
      <div class="page-container">
        <router-view />
      </div>
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
    isDeptManager () {
      return this.userInfo && this.userInfo.role === 'dept_manager'
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

:root {
  --primary: #3b6ee3;
  --primary-dark: #274c9f;
  --accent: #5ad8ff;
  --bg: #f6f8fb;
  --card: #ffffff;
  --text-main: #1f2d3d;
  --text-muted: #6b7b8c;
  --shadow: 0 12px 30px rgba(38, 74, 164, 0.15);
  font-family: 'Inter', 'PingFang SC', 'Helvetica Neue', Arial, sans-serif;
  background: var(--bg);
  color: var(--text-main);
}

body {
  background: radial-gradient(circle at 10% 20%, rgba(90, 216, 255, 0.25), transparent 25%),
    radial-gradient(circle at 90% 10%, rgba(59, 110, 227, 0.22), transparent 25%),
    radial-gradient(circle at 80% 80%, rgba(39, 76, 159, 0.18), transparent 23%),
    var(--bg);
  min-height: 100vh;
}

#app {
  min-height: 100vh;
}

/* 导航栏样式 */
.nav-bar {
  position: sticky;
  top: 0;
  z-index: 10;
  background: linear-gradient(90deg, #2f59d6, #1c3f9b);
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 12px 26px;
  box-shadow: 0 10px 25px rgba(20, 48, 122, 0.18);
}

.brand {
  display: flex;
  align-items: center;
  gap: 10px;
  color: #fff;
}

.brand-icon {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  width: 36px;
  height: 36px;
  border-radius: 12px;
  background: rgba(255, 255, 255, 0.15);
  backdrop-filter: blur(6px);
  font-weight: 700;
  letter-spacing: 1px;
}

.brand-text {
  font-size: 18px;
  font-weight: 700;
  letter-spacing: 0.5px;
}

.nav-links {
  display: flex;
  align-items: center;
  gap: 6px;
}

.nav-item {
  color: white;
  text-decoration: none;
  padding: 10px 14px;
  font-size: 15px;
  border-radius: 10px;
  transition: all 0.2s ease;
  font-weight: 500;
}

.nav-item:hover {
  background-color: rgba(255, 255, 255, 0.16);
  box-shadow: inset 0 0 0 1px rgba(255, 255, 255, 0.2);
}

/* 页面容器 */
.page-shell {
  padding: 26px 18px 36px;
}

.page-container {
  padding: 28px;
  max-width: 1200px;
  margin: 0 auto;
  background: rgba(255, 255, 255, 0.9);
  border-radius: 18px;
  box-shadow: var(--shadow);
  backdrop-filter: blur(6px);
}

/* 常用 UI 辅助类 */
.card {
  background: var(--card);
  border-radius: 14px;
  box-shadow: var(--shadow);
  padding: 18px 20px;
  border: 1px solid rgba(59, 110, 227, 0.08);
}

.pill {
  display: inline-flex;
  align-items: center;
  gap: 6px;
  padding: 9px 14px;
  border-radius: 999px;
  background: rgba(59, 110, 227, 0.08);
  color: var(--primary-dark);
  border: 1px solid rgba(59, 110, 227, 0.18);
  font-weight: 600;
}

.pill::before {
  content: '';
  display: inline-block;
  width: 8px;
  height: 8px;
  border-radius: 999px;
  background: linear-gradient(135deg, var(--accent), var(--primary));
  box-shadow: 0 4px 10px rgba(59, 110, 227, 0.3);
}

.muted {
  color: var(--text-muted);
}

.section-title {
  font-size: 20px;
  font-weight: 700;
  color: var(--primary-dark);
  margin-bottom: 16px;
  display: flex;
  align-items: center;
  gap: 8px;
}

.section-title::before {
  content: '';
  width: 8px;
  height: 20px;
  background: linear-gradient(180deg, var(--accent), var(--primary));
  border-radius: 6px;
  box-shadow: 0 4px 10px rgba(59, 110, 227, 0.25);
}

.primary-btn {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  padding: 10px 16px;
  border-radius: 12px;
  background: linear-gradient(135deg, var(--primary), #274c9f);
  color: #fff;
  border: none;
  cursor: pointer;
  font-weight: 600;
  box-shadow: 0 8px 20px rgba(59, 110, 227, 0.3);
  transition: transform 0.15s ease, box-shadow 0.2s ease;
}

.primary-btn:hover {
  transform: translateY(-1px);
  box-shadow: 0 12px 24px rgba(39, 76, 159, 0.35);
}

.table-block {
  overflow: hidden;
  border-radius: 14px;
  box-shadow: var(--shadow);
  border: 1px solid rgba(59, 110, 227, 0.12);
}

.table-block table,
table {
  width: 100%;
  border-collapse: collapse;
  font-size: 14px;
  background: #fff;
}

.table-block th,
.table-block td,
table th,
table td {
  padding: 12px 14px;
  border-bottom: 1px solid rgba(59, 110, 227, 0.08);
  text-align: left;
}

.table-block th,
table th {
  background: linear-gradient(135deg, rgba(59, 110, 227, 0.08), rgba(90, 216, 255, 0.14));
  color: var(--text-muted);
  font-weight: 700;
  letter-spacing: 0.3px;
}

.table-block tbody tr:nth-child(odd),
table tbody tr:nth-child(odd) {
  background: #f8faff;
}

.table-block tbody tr:hover,
table tbody tr:hover {
  background: rgba(59, 110, 227, 0.06);
}

.empty-tip {
  padding: 18px;
  border-radius: 12px;
  background: rgba(59, 110, 227, 0.06);
  color: var(--text-muted);
  text-align: center;
}

input,
select,
textarea {
  font-family: inherit;
}

/* 路由激活样式（当前页导航高亮） */
.router-link-active {
  background-color: rgba(255, 255, 255, 0.16);
  font-weight: 700;
}
</style>
