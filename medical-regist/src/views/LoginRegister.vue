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
        <input v-model="loginId" type="tel" placeholder="请输入手机号或账号" class="input">
      </div>
      <div class="form-item">
        <label>密码：</label>
        <input v-model="loginPassword" type="password" placeholder="请输入密码" class="input">
      </div>
      <button class="submit-btn" @click="handleLogin">登录</button>
    </div>
    <!-- 注册表单 -->
    <div class="form-container" v-else>
      <div class="form-item">
        <label>姓名：</label>
        <input v-model="registerForm.patientName" type="text" placeholder="请输入姓名" class="input">
      </div>
      <div class="form-item">
        <label>性别：</label>
        <select v-model="registerForm.gender" class="input">
          <option value="">请选择</option>
          <option value="男">男</option>
          <option value="女">女</option>
        </select>
      </div>
      <div class="form-item">
        <label>年龄：</label>
        <input v-model.number="registerForm.age" type="number" min="0" placeholder="请输入年龄" class="input">
      </div>
      <div class="form-item">
        <label>手机号：</label>
        <input v-model="registerForm.phone" type="tel" placeholder="请输入手机号" class="input">
      </div>
      <div class="form-item">
        <label>密码：</label>
        <input v-model="registerForm.password" type="password" placeholder="请设置密码" class="input">
      </div>
      <div class="form-item">
        <label>身份证号：</label>
        <input v-model="registerForm.idCard" type="text" placeholder="请输入身份证号" class="input">
      </div>
      <button class="submit-btn" @click="handleRegister">注册</button>
    </div>
  </div>
</template>

<script>
import api from '@/api'

export default {
  data() {
    return {
      isLogin: true, // 默认显示登录
      loginId: '',
      loginPassword: '',
      registerForm: {
        patientName: '',
        gender: '',
        age: null,
        phone: '',
        password: '',
        idCard: ''
      }
    }
  },
  methods: {
    async handleLogin () {
      if (!this.loginId || !this.loginPassword) {
        alert('请填写账号和密码')
        return
      }
      try {
        const res = await api.post('/auth/login', {
          id: this.loginId,
          password: this.loginPassword
        })
        const body = res.data
        if (body.error_code !== 0) {
          alert(body.msg || '登录失败')
          return
        }
        const data = body.data
        localStorage.setItem('token', data.token)
        localStorage.setItem('userInfo', JSON.stringify(data))
        // 登录后统一跳到个人中心
        this.$router.push('/profile')
      } catch (e) {
        console.error(e)
        alert('登录请求失败，请稍后重试')
      }
    },
    async handleRegister () {
      const f = this.registerForm
      if (!f.patientName || !f.gender || !f.age || !f.phone || !f.password || !f.idCard) {
        alert('请完整填写注册信息')
        return
      }
      try {
        const res = await api.post('/auth/patient/register', f)
        const body = res.data
        if (body.error_code !== 0) {
          alert(body.msg || '注册失败')
          return
        }
        alert('注册成功，请使用手机号登录')
        this.isLogin = true
        this.loginId = f.phone
      } catch (e) {
        console.error(e)
        alert('注册请求失败，请稍后重试')
      }
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
