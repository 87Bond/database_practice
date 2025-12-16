<template>
  <div class="login-register">
    <div class="auth-hero">
      <div>
        <p class="eyebrow">欢迎使用智慧医疗</p>
        <h2>一站式预约与就诊管理</h2>
        <p class="sub">便捷挂号 / 消息沟通 / 个人中心全流程体验</p>
      </div>
      <div class="hero-badge">安心就医 · 轻松每一天</div>
      <div class="hero-meta">
        <span class="pill">智能分诊 · 即时沟通</span>
        <span class="pill ghost">数据安全 · 便捷高效</span>
      </div>
    </div>
    <div class="auth-card card">
      <div class="tab-header">
        <div class="tab-item" :class="{ active: isLogin }" @click="isLogin = true">登录</div>
        <div class="tab-item" :class="{ active: !isLogin }" @click="isLogin = false">注册</div>
      </div>
      <!-- 登录表单 -->
      <div class="form-container" v-if="isLogin">
        <div class="form-item">
          <label>手机号</label>
          <input v-model="loginId" type="tel" placeholder="请输入手机号或账号" class="input">
        </div>
        <div class="form-item">
          <label>密码</label>
          <input v-model="loginPassword" type="password" placeholder="请输入密码" class="input">
        </div>
        <button class="primary-btn submit-btn" @click="handleLogin">立即登录</button>
      </div>
      <!-- 注册表单 -->
      <div class="form-container" v-else>
        <div class="form-grid">
          <div class="form-item">
            <label>姓名</label>
            <input v-model="registerForm.patientName" type="text" placeholder="请输入姓名" class="input">
          </div>
          <div class="form-item">
            <label>性别</label>
            <select v-model="registerForm.gender" class="input">
              <option value="">请选择</option>
              <option value="男">男</option>
              <option value="女">女</option>
            </select>
          </div>
          <div class="form-item">
            <label>年龄</label>
            <input v-model.number="registerForm.age" type="number" min="0" placeholder="请输入年龄" class="input">
          </div>
          <div class="form-item">
            <label>手机号</label>
            <input v-model="registerForm.phone" type="tel" placeholder="请输入手机号" class="input">
          </div>
          <div class="form-item">
            <label>密码</label>
            <input v-model="registerForm.password" type="password" placeholder="请设置密码" class="input">
          </div>
          <div class="form-item">
            <label>身份证号</label>
            <input v-model="registerForm.idCard" type="text" placeholder="请输入身份证号" class="input">
          </div>
        </div>
        <button class="primary-btn submit-btn" @click="handleRegister">完成注册</button>
      </div>
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
  display: grid;
  gap: 22px;
  grid-template-columns: minmax(420px, 1.1fr) 420px;
  align-items: stretch;
}

.auth-hero {
  padding: 28px 30px;
  border-radius: 18px;
  background: linear-gradient(135deg, rgba(59, 110, 227, 0.16), rgba(90, 216, 255, 0.1));
  border: 1px solid rgba(59, 110, 227, 0.18);
  box-shadow: 0 14px 34px rgba(59, 110, 227, 0.18);
  display: grid;
  gap: 14px;
  align-content: center;
}

.eyebrow {
  color: var(--primary-dark);
  font-weight: 700;
  letter-spacing: 0.5px;
  margin-bottom: 6px;
}

.auth-hero h2 {
  font-size: 24px;
  margin-bottom: 6px;
}

.auth-hero .sub {
  color: var(--text-muted);
}

.hero-badge {
  background: #fff;
  border-radius: 12px;
  padding: 12px 16px;
  color: var(--primary-dark);
  box-shadow: var(--shadow);
  font-weight: 700;
}

.hero-meta {
  display: flex;
  flex-wrap: wrap;
  gap: 10px;
}

.auth-card {
  max-width: 100%;
}

.tab-header {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  margin-bottom: 20px;
  background: #f4f6fb;
  border-radius: 12px;
  padding: 6px;
}

.tab-item {
  text-align: center;
  padding: 10px;
  cursor: pointer;
  border-radius: 10px;
  color: var(--text-muted);
  transition: all 0.2s ease;
  font-weight: 600;
}

.tab-item.active {
  background: #fff;
  color: var(--primary-dark);
  box-shadow: 0 8px 18px rgba(59, 110, 227, 0.18);
}

.form-container {
  display: grid;
  gap: 14px;
}

.form-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(220px, 1fr));
  gap: 14px 16px;
}

.form-item {
  display: grid;
  gap: 6px;
  color: var(--text-main);
}

.input {
  width: 100%;
  padding: 10px 12px;
  border: 1px solid rgba(59, 110, 227, 0.18);
  border-radius: 10px;
  background: var(--field-bg);
  transition: border-color 0.2s ease, box-shadow 0.2s ease;
}

.input:focus {
  outline: none;
  border-color: var(--primary);
  box-shadow: 0 0 0 3px rgba(59, 110, 227, 0.2);
}

.submit-btn {
  width: 100%;
  margin-top: 4px;
}

@media (max-width: 1024px) {
  .login-register {
    grid-template-columns: 1fr;
  }
}
</style>
