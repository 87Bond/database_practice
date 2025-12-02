<template>
  <div id="app">
    <!-- 顶部导航栏 -->
    <nav class="nav-bar">
      <router-link to="/login-register" class="nav-item">登录/注册</router-link>
      <router-link to="/registration" class="nav-item">挂号</router-link>
      <router-link to="/message" class="nav-item">消息</router-link>
      <router-link to="/profile" class="nav-item">个人中心</router-link>
    </nav>
    <!-- 页面容器（路由匹配的页面会显示在这里） -->
    <div class="page-container">
      <router-view />
    </div>
  </div>
</template>

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