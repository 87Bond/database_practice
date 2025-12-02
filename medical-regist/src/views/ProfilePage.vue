<template>
  <div class="profile-page">
    <h2>个人中心</h2>
    <div class="profile-info">
      <div class="avatar">
        <img src="https://via.placeholder.com/80" alt="头像">
      </div>
      <div class="user-info">
        <p class="name">用户名：张三</p>
        <p class="phone">手机号：13800138000</p>
        <p class="id-card">就诊卡号：123456789012345678</p>
      </div>
    </div>
    <div class="profile-menu">
      <div class="menu-item">我的挂号记录</div>
      <div class="menu-item">我的就诊记录</div>
      <div class="menu-item">修改密码</div>
      <div class="menu-item">退出登录</div>
    </div>
  </div>
</template>

<style scoped>
.profile-page h2 {
  color: #2f5496;
  margin-bottom: 20px;
}
.profile-info {
  display: flex;
  align-items: center;
  margin-bottom: 30px;
  padding-bottom: 20px;
  border-bottom: 1px solid #e0e0e0;
}
.avatar img {
  border-radius: 50%;
  margin-right: 20px;
}
.user-info p {
  margin-bottom: 8px;
  color: #333;
}
.profile-menu {
  border: 1px solid #e0e0e0;
  border-radius: 8px;
}
.menu-item {
  padding: 15px 20px;
  border-bottom: 1px solid #e0e0e0;
  cursor: pointer;
}
.menu-item:last-child {
  border-bottom: none;
}
.menu-item:hover {
  background-color: #f5f5f5;
}
</style>