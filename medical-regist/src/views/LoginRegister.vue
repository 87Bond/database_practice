<template>
  <div class="login-register">
    <div class="hero-panel card">
      <div>
        <div class="hero-tag">欢迎使用智慧医疗</div>
        <h1>一站式预约与就诊管理</h1>
        <p class="sub">便捷挂号 · 消息沟通 · 个人中心全流程体验</p>
        <div class="hero-stats">
          <div class="stat">
            <div class="stat-number">24/7</div>
            <div class="stat-label">随时预约</div>
          </div>
          <div class="stat">
            <div class="stat-number">多角色</div>
            <div class="stat-label">患者 / 医生 / 管理员</div>
          </div>
          <div class="stat">
            <div class="stat-number">双模式</div>
            <div class="stat-label">日间 / 夜间护眼</div>
          </div>
        </div>
      </div>
      <div class="hero-badge">安心就医 · 灵动体验</div>
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
  grid-template-columns: 1.1fr 1fr;
  gap: 24px;
  align-items: stretch;
}

.hero-panel {
  background: radial-gradient(circle at 20% 30%, rgba(90, 216, 255, 0.18), transparent 32%),
    radial-gradient(circle at 70% 20%, rgba(59, 110, 227, 0.22), transparent 38%),
    linear-gradient(135deg, color-mix(in srgb, var(--primary) 20%, transparent), color-mix(in srgb, var(--card) 90%, transparent));
  border: 1px solid rgba(59, 110, 227, 0.18);
  padding: 28px 32px;
  border-radius: 20px;
  display: grid;
  gap: 16px;
}

.hero-tag {
  display: inline-flex;
  align-items: center;
  gap: 6px;
  padding: 8px 14px;
  border-radius: 999px;
  background: rgba(59, 110, 227, 0.12);
  color: var(--primary-dark);
  font-weight: 700;
}

.hero-panel h1 {
  font-size: 30px;
  line-height: 1.3;
  margin-bottom: 6px;
}

.hero-panel .sub {
  color: var(--text-muted);
}

.hero-stats {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(140px, 1fr));
  gap: 12px;
  margin-top: 4px;
}

.stat {
  background: color-mix(in srgb, var(--card) 92%, transparent);
  border: 1px solid rgba(59, 110, 227, 0.12);
  border-radius: 14px;
  padding: 12px;
  box-shadow: 0 12px 24px rgba(59, 110, 227, 0.12);
}

.stat-number {
  font-size: 18px;
  font-weight: 800;
  color: var(--primary-dark);
}

.stat-label {
  color: var(--text-muted);
  margin-top: 4px;
}

.hero-badge {
  background: color-mix(in srgb, var(--card) 96%, transparent);
  border-radius: 12px;
  padding: 12px 16px;
  color: var(--primary-dark);
  box-shadow: var(--shadow);
  font-weight: 700;
  width: fit-content;
}

.auth-card {
  max-width: 680px;
  width: 100%;
  margin: auto;
}

.tab-header {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  margin-bottom: 20px;
  background: color-mix(in srgb, var(--card) 88%, transparent);
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
  background: color-mix(in srgb, var(--card) 98%, transparent);
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
  background: color-mix(in srgb, var(--card) 92%, transparent);
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

@media (max-width: 960px) {
  .login-register {
    grid-template-columns: 1fr;
  }

  .hero-panel {
    order: 2;
  }

  .auth-card {
    order: 1;
  }
}

@media (max-width: 640px) {
  .hero-panel h1 {
    font-size: 24px;
  }

  .hero-panel {
    padding: 20px;
  }
}
</style>
