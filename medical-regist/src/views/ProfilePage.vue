<template>
  <div class="profile-page">
    <h2>个人中心</h2>
    <div class="profile-info">
      <div class="avatar">
        <div class="avatar-circle">{{ avatarText }}</div>
      </div>
      <div class="user-info">
        <p class="name">用户名：{{ userInfo.username }}</p>
        <p class="phone">用户ID：{{ userInfo.userId }}</p>
        <p class="id-card">角色：{{ userInfo.role }}</p>
      </div>
    </div>
    <div class="profile-menu">
      <div class="menu-item" @click="showRegistrations">我的挂号记录</div>
      <div class="menu-item" @click="showMedicalRecords">我的就诊记录</div>
      <div class="menu-item" @click="changePassword">修改密码</div>
      <div class="menu-item" @click="logout">退出登录</div>
    </div>
    <div class="balance-panel" v-if="isPatient">
      <p>账户余额：¥ {{ (balance / 100).toFixed(2) }}</p>
      <div class="recharge">
        <input v-model.number="rechargeAmount" type="number" min="0" step="0.01" placeholder="充值金额（元）" />
        <button @click="recharge">充值</button>
      </div>
    </div>
    <div class="reg-list" v-if="registrations.length">
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
            <td>¥ {{ (3000 / 100).toFixed(2) }}</td>
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
    <div class="record-list" v-if="medicalRecords.length">
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
.avatar {
  margin-right: 20px;
}
.avatar-circle {
  width: 80px;
  height: 80px;
  border-radius: 50%;
  background-color: #2f5496;
  color: #fff;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 32px;
  font-weight: bold;
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
.reg-list {
  margin-top: 20px;
}
.reg-list h3 {
  margin-bottom: 10px;
}
.balance-panel {
  margin-top: 20px;
  margin-bottom: 10px;
}
.balance-panel .recharge {
  margin-top: 8px;
}
.balance-panel input {
  padding: 6px 10px;
  border-radius: 4px;
  border: 1px solid #e0e0e0;
  margin-right: 8px;
}
.balance-panel button {
  padding: 6px 12px;
}
.table {
  width: 100%;
  border-collapse: collapse;
  font-size: 14px;
}
.table th,
.table td {
  border: 1px solid #e0e0e0;
  padding: 6px 8px;
  text-align: left;
}
.table button {
  margin-right: 6px;
}
</style>
