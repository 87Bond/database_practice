<template>
  <div class="login-register">
    <div class="tab-header">
      <div class="tab-item" :class="{ active: isLogin }" @click="isLogin = true">登录</div>
      <div class="tab-item" :class="{ active: !isLogin }" @click="isLogin = false">注册</div>
    </div>
    <!-- 登录表单 -->
    <div class="form-container" v-if="isLogin">
      <div class="form-item">
        <label>手机号：</label>
        <input type="tel" placeholder="请输入手机号" class="input">
      </div>
      <div class="form-item">
        <label>密码：</label>
        <input type="password" placeholder="请输入密码" class="input">
      </div>
      <button class="submit-btn">登录</button>
    </div>
    <!-- 注册表单 -->
    <div class="form-container" v-else>
      <div class="form-item">
        <label>手机号：</label>
        <input type="tel" placeholder="请输入手机号" class="input">
      </div>
      <div class="form-item">
        <label>验证码：</label>
        <input type="text" placeholder="请输入验证码" class="input">
        <button class="code-btn">获取验证码</button>
      </div>
      <div class="form-item">
        <label>密码：</label>
        <input type="password" placeholder="请设置密码" class="input">
      </div>
      <button class="submit-btn">注册</button>
    </div>
  </div>
</template>

<script>
export default {
  data() {
    return {
      isLogin: true // 默认显示登录
    }
  }
}
</script>

<style scoped>
.login-register {
  max-width: 400px;
  margin: 0 auto;
}
.tab-header {
  display: flex;
  margin-bottom: 20px;
}
.tab-item {
  flex: 1;
  text-align: center;
  padding: 10px;
  border-bottom: 2px solid #e0e0e0;
  cursor: pointer;
}
.tab-item.active {
  border-bottom-color: #2f5496;
  color: #2f5496;
  font-weight: bold;
}
.form-item {
  display: flex;
  align-items: center;
  margin-bottom: 15px;
}
.form-item label {
  width: 80px;
  color: #333;
}
.input {
  flex: 1;
  padding: 8px 10px;
  border: 1px solid #e0e0e0;
  border-radius: 4px;
}
.code-btn {
  margin-left: 10px;
  padding: 8px 15px;
  background-color: #2f5496;
  color: white;
  border: none;
  border-radius: 4px;
  cursor: pointer;
}
.submit-btn {
  width: 100%;
  padding: 10px;
  background-color: #2f5496;
  color: white;
  border: none;
  border-radius: 4px;
  cursor: pointer;
  margin-top: 10px;
}
.submit-btn:hover {
  background-color: #1f3a68;
}
</style>