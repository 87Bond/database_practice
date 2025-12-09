<template>
  <div class="dept-panel">
    <h2>科室管理员工作台</h2>
    <p class="tip">
      本页面用于维护本科室医生的号源，并查看本科室所有医生的挂号记录，同时可以向医生和患者发送系统消息。
    </p>

    <!-- 医生列表与加入医生 -->
    <section class="block">
      <h3>本科室医生列表</h3>
      <div class="assign-bar">
        <label>加入已有医生到本科室：</label>
        <input v-model="assignDoctorId" type="text" class="input" placeholder="请输入医生的 UserID，例如 DR0001">
        <button class="btn small" @click="assignDoctor">加入本科室</button>
      </div>
      <table class="table" v-if="doctors.length">
        <thead>
          <tr>
            <th>医生ID</th>
            <th>姓名</th>
            <th>职称</th>
          </tr>
        </thead>
        <tbody>
          <tr v-for="d in doctors" :key="d.userId">
            <td>{{ d.userId }}</td>
            <td>{{ d.doctorName }}</td>
            <td>{{ d.title }}</td>
          </tr>
        </tbody>
      </table>
      <div v-else class="empty-tip">当前科室暂无医生，请先创建或加入医生账号。</div>
    </section>

    <!-- 号源维护 -->
    <section class="block">
      <h3>分配号源</h3>
      <div class="form-row">
        <label>医生：</label>
        <select v-model="slotForm.doctorId" class="input">
          <option value="">请选择医生</option>
          <option v-for="d in doctors" :key="d.userId" :value="d.userId">
            {{ d.doctorName }}（{{ d.userId }}）
          </option>
        </select>
      </div>
      <div class="form-row">
        <label>日期：</label>
        <input v-model="slotForm.slotDate" type="date" class="input">
      </div>
      <div class="form-row">
        <label>开始时间：</label>
        <input v-model="slotForm.startTime" type="time" class="input">
      </div>
      <div class="form-row">
        <label>结束时间：</label>
        <input v-model="slotForm.endTime" type="time" class="input">
      </div>
      <div class="form-row">
        <label>号源数量：</label>
        <input v-model.number="slotForm.capacity" type="number" min="1" class="input">
      </div>
      <div class="form-row">
        <label>备注：</label>
        <input v-model="slotForm.note" type="text" class="input" placeholder="可选，例如 上午门诊">
      </div>
      <button class="btn" @click="createSlot">创建号源</button>

      <h3 style="margin-top: 24px;">号源列表</h3>
      <div class="form-row">
        <label>查看日期：</label>
        <input v-model="slotQueryDate" type="date" class="input">
        <button class="btn small" @click="loadSlots">刷新</button>
      </div>
      <table class="table" v-if="slots.length">
        <thead>
          <tr>
            <th>医生</th>
            <th>日期</th>
            <th>时间段</th>
            <th>容量</th>
            <th>已预约</th>
            <th>状态</th>
            <th>备注</th>
          </tr>
        </thead>
        <tbody>
          <tr v-for="s in slots" :key="s.slotId">
            <td>{{ s.doctorName }}（{{ s.doctorId }}）</td>
            <td>{{ s.slotDate }}</td>
            <td>{{ formatSlotTime(s) }}</td>
            <td>{{ s.capacity }}</td>
            <td>{{ s.bookedCount }}</td>
            <td>{{ s.status }}</td>
            <td>{{ s.note }}</td>
          </tr>
        </tbody>
      </table>
      <div v-else class="empty-tip">当前日期暂无号源。</div>
    </section>

    <!-- 挂号记录 -->
    <section class="block">
      <h3>本科室挂号记录</h3>
      <div class="form-row">
        <label>日期：</label>
        <input v-model="regQueryDate" type="date" class="input">
        <button class="btn small" @click="loadRegistrations">刷新</button>
      </div>
      <table class="table" v-if="registrations.length">
        <thead>
          <tr>
            <th>挂号号</th>
            <th>患者ID</th>
            <th>患者姓名</th>
            <th>医生</th>
            <th>日期</th>
            <th>时段</th>
            <th>状态</th>
            <th>支付状态</th>
            <th>发送消息</th>
          </tr>
        </thead>
        <tbody>
          <tr v-for="r in registrations" :key="r.regId">
            <td>{{ r.regId }}</td>
            <td>{{ r.patientId }}</td>
            <td>{{ r.patientName }}</td>
            <td>{{ r.doctorName }}（{{ r.doctorId }}）</td>
            <td>{{ r.regDate }}</td>
            <td>{{ r.regTimeSlot }}</td>
            <td>{{ r.regStatus }}</td>
            <td>{{ r.payStatus }}</td>
            <td>
              <button class="btn small" @click="fillTarget(r.patientId)">给患者</button>
              <button class="btn small" style="margin-left:6px;" @click="fillTarget(r.doctorId)">给医生</button>
            </td>
          </tr>
        </tbody>
      </table>
      <div v-else class="empty-tip">暂无挂号记录。</div>
    </section>

    <!-- 简单消息发送区 -->
    <section class="block">
      <h3>发送系统消息</h3>
      <div class="form-row">
        <label>目标用户ID：</label>
        <input v-model="targetUserId" type="text" class="input" placeholder="例如患者或医生的 UserID">
      </div>
      <div class="form-row">
        <label>标题：</label>
        <input v-model="title" type="text" class="input">
      </div>
      <div class="form-row">
        <label>内容：</label>
        <textarea v-model="content" rows="3" class="textarea" />
      </div>
      <button class="btn" @click="sendMessage">发送消息</button>
    </section>
  </div>
</template>

<script>
import api from '@/api'

export default {
  data () {
    return {
      doctors: [],
      assignDoctorId: '',
      slotForm: {
        doctorId: '',
        slotDate: '',
        startTime: '',
        endTime: '',
        capacity: 1,
        note: ''
      },
      slotQueryDate: '',
      slots: [],
      regQueryDate: '',
      registrations: [],
      targetUserId: '',
      title: '',
      content: ''
    }
  },
  created () {
    const today = new Date().toISOString().slice(0, 10)
    this.slotQueryDate = today
    this.regQueryDate = today
    this.loadDoctors()
    this.loadSlots()
    this.loadRegistrations()
  },
  methods: {
    async loadDoctors () {
      try {
        const res = await api.get('/dept/doctors')
        const body = res.data
        if (body.error_code === 0) {
          this.doctors = body.data || []
        } else {
          this.doctors = []
        }
      } catch (e) {
        console.error(e)
        this.doctors = []
      }
    },
    async assignDoctor () {
      if (!this.assignDoctorId) {
        alert('请先输入要加入的医生ID')
        return
      }
      try {
        const res = await api.post(`/dept/doctors/${this.assignDoctorId}/assign`)
        const body = res.data
        alert(body.msg || '操作完成')
        this.assignDoctorId = ''
        this.loadDoctors()
      } catch (e) {
        console.error(e)
        alert('操作失败，请稍后重试')
      }
    },
    async createSlot () {
      const f = this.slotForm
      if (!f.doctorId || !f.slotDate || !f.startTime || !f.endTime || !f.capacity) {
        alert('请完整填写医生、日期、起止时间和号源数量')
        return
      }
      try {
        const res = await api.post('/dept/slots', f)
        const body = res.data
        alert(body.msg || '创建成功')
        this.loadSlots()
      } catch (e) {
        console.error(e)
        alert('创建号源失败，请稍后重试')
      }
    },
    async loadSlots () {
      try {
        const res = await api.get('/dept/slots', {
          params: {
            date: this.slotQueryDate || null
          }
        })
        const body = res.data
        if (body.error_code === 0) {
          this.slots = body.data || []
        } else {
          this.slots = []
        }
      } catch (e) {
        console.error(e)
        this.slots = []
      }
    },
    async loadRegistrations () {
      try {
        const res = await api.get('/dept/registrations', {
          params: {
            date: this.regQueryDate || null
          }
        })
        const body = res.data
        if (body.error_code === 0) {
          this.registrations = body.data || []
        } else {
          this.registrations = []
        }
      } catch (e) {
        console.error(e)
        this.registrations = []
      }
    },
    formatSlotTime (s) {
      if (!s) return ''
      const start = s.startTime ? String(s.startTime).substring(0, 5) : ''
      const end = s.endTime ? String(s.endTime).substring(0, 5) : ''
      return start && end ? `${start}-${end}` : ''
    },
    fillTarget (userId) {
      this.targetUserId = userId
    },
    async sendMessage () {
      if (!this.targetUserId || !this.title || !this.content) {
        alert('请填写目标用户、标题和内容')
        return
      }
      try {
        const res = await api.post('/messages', {
          targetUserId: this.targetUserId,
          title: this.title,
          content: this.content
        })
        const body = res.data
        if (body.error_code === 0) {
          alert('发送成功')
          this.title = ''
          this.content = ''
        } else {
          alert(body.msg || '发送失败')
        }
      } catch (e) {
        console.error(e)
        alert('发送请求失败，请稍后重试')
      }
    }
  }
}
</script>

<style scoped>
.dept-panel h2 {
  color: #2f5496;
  margin-bottom: 10px;
}
.tip {
  margin-bottom: 20px;
  color: #666;
}
.block {
  border: 1px solid #e0e0e0;
  border-radius: 8px;
  padding: 15px 20px;
  margin-bottom: 20px;
}
.block h3 {
  margin-bottom: 10px;
  color: #2f5496;
}
.assign-bar {
  display: flex;
  align-items: center;
  margin-bottom: 10px;
}
.assign-bar label {
  margin-right: 8px;
}
.form-row {
  display: flex;
  align-items: center;
  margin-bottom: 10px;
}
.form-row label {
  width: 90px;
}
.input,
.textarea {
  flex: 1;
  padding: 6px 10px;
  border: 1px solid #e0e0e0;
  border-radius: 4px;
}
.textarea {
  resize: vertical;
}
.table {
  width: 100%;
  border-collapse: collapse;
  font-size: 14px;
  margin-top: 10px;
}
.table th,
.table td {
  border: 1px solid #e0e0e0;
  padding: 6px 8px;
  text-align: left;
}
.btn {
  background-color: #2f5496;
  color: #fff;
  border: none;
  padding: 8px 16px;
  border-radius: 4px;
  cursor: pointer;
}
.btn.small {
  padding: 4px 10px;
  font-size: 12px;
}
.btn:hover {
  background-color: #1f3a68;
}
.empty-tip {
  margin-top: 8px;
  color: #777;
}
</style>

