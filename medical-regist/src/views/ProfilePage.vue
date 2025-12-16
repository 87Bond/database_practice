<template>
  <div class="profile-page">
    <div class="header">
      <div>
        <p class="eyebrow">个人中心</p>
        <h2>管理账户、预约与就诊记录</h2>
      </div>
      <div class="pill" v-if="isPatient">余额实时刷新</div>
    </div>
    <div class="profile-info card">
      <div class="avatar">
        <div class="avatar-circle">{{ avatarText }}</div>
      </div>
      <div class="user-info">
        <p class="name">用户名：{{ userInfo.username }}</p>
        <p class="phone">用户ID：{{ userInfo.userId }}</p>
        <p class="id-card">角色：{{ userInfo.role }}</p>
      </div>
    </div>
    <div class="profile-menu card">
      <div class="menu-item" @click="showRegistrations">我的挂号记录</div>
      <div class="menu-item" @click="showMedicalRecords">我的就诊记录</div>
      <div class="menu-item" @click="changePassword">修改密码</div>
      <div class="menu-item" @click="logout">退出登录</div>
    </div>
    <div class="balance-panel card" v-if="isPatient">
      <p>账户余额：¥ {{ (balance / 100).toFixed(2) }}</p>
      <div class="recharge">
        <input v-model.number="rechargeAmount" type="number" min="0" step="0.01" placeholder="充值金额（元）" />
        <button @click="recharge">充值</button>
      </div>
    </div>
    <div class="reg-list card" v-if="registrations.length">
      <h3>挂号记录</h3>
      <table class="table">
        <thead>
          <tr>
            <th>日期</th>
            <th>时段</th>
            <th>科室</th>
            <th>地点</th>
            <th>医生</th>
            <th>费用</th>
            <th>状态</th>
            <th>操作</th>
          </tr>
        </thead>
        <tbody>
          <tr v-for="item in registrations" :key="item.regId">
            <td>{{ item.regDate }}</td>
            <td>{{ formatSlot(item.regTimeSlot) }}</td>
            <td>{{ item.departmentName || item.departmentId }}</td>
            <td>{{ item.departmentLocation || '-' }}</td>
            <td>{{ item.doctorName || item.doctorId }}</td>
            <td>¥ {{ ((item.regFee || 0) / 100).toFixed(2) }}</td>
            <td>{{ item.regStatus }} / {{ item.payStatus }}</td>
            <td>
              <button
                v-if="item.regStatus === 'Booked' && item.payStatus === 'Unpaid'"
                @click="pay(item)"
              >
                支付
              </button>
              <button
                v-if="item.regStatus === 'Booked'"
                @click="cancel(item)"
              >
                取消
              </button>
            </td>
          </tr>
        </tbody>
      </table>
    </div>
    <div class="record-list card" v-if="medicalRecords.length">
      <h3>就诊记录</h3>
      <table class="table">
        <thead>
          <tr>
            <th>日期</th>
            <th>时段</th>
            <th>科室</th>
            <th>地点</th>
            <th>{{ isPatient ? '医生' : '患者' }}</th>
            <th>诊断</th>
            <th>医嘱</th>
          </tr>
        </thead>
        <tbody>
          <tr v-for="item in medicalRecords" :key="item.recordId">
            <td>{{ item.regDate }}</td>
            <td>{{ formatSlot(item.regTimeSlot) }}</td>
            <td>{{ item.departmentName || item.departmentId }}</td>
            <td>{{ item.departmentLocation || '-' }}</td>
            <td>{{ isPatient ? item.doctorName : item.patientName }}</td>
            <td>{{ item.diagnosis }}</td>
            <td>{{ item.advice }}</td>
          </tr>
        </tbody>
      </table>
    </div>
  </div>
</template>

<script>
import api from '@/api'

export default {
  data () {
    return {
      userInfo: {
        username: '',
        userId: '',
        role: ''
      },
      registrations: [],
      medicalRecords: [],
      balance: 0,
      rechargeAmount: null
    }
  },
  computed: {
    avatarText () {
      if (this.userInfo && this.userInfo.username) {
        return this.userInfo.username.charAt(0)
      }
      if (this.userInfo.role === 'doctor') {
        return '医'
      }
      if (this.userInfo.role === 'admin') {
        return '管'
      }
      if (this.userInfo.role === 'dept_manager') {
        return '科'
      }
      return '用'
    },
    isPatient () {
      return this.userInfo.role === 'patient'
    }
  },
  created () {
    const infoStr = localStorage.getItem('userInfo')
    if (infoStr) {
      this.userInfo = JSON.parse(infoStr)
    }
    this.loadProfile()
  },
  methods: {
    async loadProfile () {
      if (!this.isPatient) return
      try {
        const res = await api.get('/patient/me')
        const body = res.data
        if (body.error_code === 0 && body.data) {
          this.balance = body.data.balance || 0
        }
      } catch (e) {
        console.error(e)
      }
    },
    async showRegistrations () {
      try {
        // 患者查看自己的挂号记录；医生查看自己的门诊挂号列表
        if (this.isPatient) {
          const res = await api.get('/registrations')
          const body = res.data
          if (body.error_code === 0) {
            this.registrations = body.data || []
          }
        } else if (this.userInfo.role === 'doctor') {
          const res = await api.get('/doctor/registrations')
          const body = res.data
          if (body.error_code === 0) {
            this.registrations = body.data || []
          }
        } else {
          this.registrations = []
        }
      } catch (e) {
        console.error(e)
        this.registrations = []
      }
    },
    async showMedicalRecords () {
      try {
        if (this.isPatient) {
          const res = await api.get('/medical-records')
          const body = res.data
          if (body.error_code === 0) {
            this.medicalRecords = body.data || []
          }
        } else if (this.userInfo.role === 'doctor') {
          const res = await api.get('/medical-records/doctor')
          const body = res.data
          if (body.error_code === 0) {
            this.medicalRecords = body.data || []
          }
        } else {
          this.medicalRecords = []
        }
      } catch (e) {
        console.error(e)
        this.medicalRecords = []
      }
    },
    async pay (item) {
      if (!confirm('确认支付该挂号费用？')) return
      try {
        const res = await api.post(`/registrations/${item.regId}/pay`, null, {
          params: { payChannel: 'Balance' }
        })
        const body = res.data
        if (body.error_code === 0) {
          alert('支付成功')
          this.showRegistrations()
          this.loadProfile()
        } else {
          alert(body.msg || '支付失败')
        }
      } catch (e) {
        console.error(e)
        alert('支付请求失败')
      }
    },
    async cancel (item) {
      if (!confirm('确认取消该挂号？已支付的将扣除一定手续费。')) return
      try {
        const res = await api.post(`/registrations/${item.regId}/cancel`)
        const body = res.data
        if (body.error_code === 0) {
          alert('取消成功')
          this.showRegistrations()
          this.loadProfile()
        } else {
          alert(body.msg || '取消失败')
        }
      } catch (e) {
        console.error(e)
        alert('取消请求失败')
      }
    },
    async recharge () {
      if (!this.isPatient) return
      if (!this.rechargeAmount || this.rechargeAmount <= 0) {
        alert('请输入大于 0 的充值金额')
        return
      }
      try {
        const res = await api.post('/patient/recharge', {
          amount: this.rechargeAmount
        })
        const body = res.data
        if (body.error_code === 0) {
          alert('充值成功')
          this.rechargeAmount = null
          this.loadProfile()
        } else {
          alert(body.msg || '充值失败')
        }
      } catch (e) {
        console.error(e)
        alert('充值请求失败')
      }
    },
    async changePassword () {
      const oldPwd = window.prompt('请输入原密码')
      if (!oldPwd) return
      const newPwd = window.prompt('请输入新密码')
      if (!newPwd) return
      try {
        const res = await api.post('/auth/change-password', {
          oldPassword: oldPwd,
          newPassword: newPwd
        })
        const body = res.data
        alert(body.msg || '修改完成')
      } catch (e) {
        console.error(e)
        alert('修改密码失败')
      }
    },
    logout () {
      localStorage.removeItem('token')
      localStorage.removeItem('userInfo')
      this.$router.push('/login-register')
    },
    formatSlot (slot) {
      const map = {
        'AM-1': '上午 09:00-10:00',
        'AM-2': '上午 10:00-11:00',
        'PM-1': '下午 14:00-15:00',
        'PM-2': '下午 15:00-16:00'
      }
      return map[slot] || slot
    }
  }
}
</script>

<style scoped>
.header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 16px;
}

.eyebrow {
  color: var(--primary-dark);
  font-weight: 700;
  letter-spacing: 0.4px;
  margin-bottom: 6px;
}

.header h2 {
  color: var(--text-main);
}

.pill {
  background: rgba(59, 110, 227, 0.08);
  color: var(--primary-dark);
  padding: 10px 14px;
  border-radius: 999px;
  font-weight: 600;
  border: 1px solid rgba(59, 110, 227, 0.2);
}

.profile-info {
  display: flex;
  align-items: center;
  gap: 20px;
}

.avatar-circle {
  width: 90px;
  height: 90px;
  border-radius: 50%;
  background: linear-gradient(135deg, var(--primary), var(--primary-dark));
  color: #fff;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 34px;
  font-weight: 800;
  box-shadow: 0 10px 20px rgba(59, 110, 227, 0.26);
}

.user-info p {
  margin-bottom: 6px;
  color: var(--text-main);
}

.profile-menu {
  margin-top: 16px;
}

.menu-item {
  padding: 15px 20px;
  border-bottom: 1px solid rgba(59, 110, 227, 0.08);
  cursor: pointer;
  transition: background 0.2s ease, transform 0.15s ease;
}

.menu-item:last-child {
  border-bottom: none;
}

.menu-item:hover {
  background: rgba(59, 110, 227, 0.06);
  transform: translateY(-1px);
}

.balance-panel {
  margin-top: 14px;
  display: grid;
  gap: 8px;
}

.balance-panel .recharge {
  display: flex;
  gap: 8px;
}

.balance-panel input {
  flex: 1;
  padding: 10px 12px;
  border-radius: 10px;
  border: 1px solid rgba(59, 110, 227, 0.18);
  background: #f8faff;
}

.balance-panel button {
  padding: 10px 14px;
  border-radius: 10px;
  background: linear-gradient(135deg, var(--primary), var(--primary-dark));
  color: #fff;
  border: none;
  cursor: pointer;
  box-shadow: 0 8px 20px rgba(59, 110, 227, 0.3);
}

.reg-list,
.record-list {
  margin-top: 16px;
}

.reg-list h3,
.record-list h3 {
  margin-bottom: 10px;
}

.table {
  width: 100%;
  border-collapse: collapse;
  font-size: 14px;
}

.table th,
.table td {
  border: 1px solid rgba(59, 110, 227, 0.12);
  padding: 8px 10px;
  text-align: left;
}

.table th {
  background: #f8faff;
  color: var(--text-muted);
  font-weight: 700;
}

.table button {
  margin-right: 6px;
  padding: 6px 10px;
  border-radius: 8px;
  border: none;
  background: var(--primary);
  color: #fff;
  cursor: pointer;
}
</style>
