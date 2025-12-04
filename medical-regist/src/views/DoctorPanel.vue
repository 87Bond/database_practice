<template>
  <div class="doctor-panel">
    <h2>医生工作台</h2>
    <p class="tip">本页面用于医生查看当天挂号患者，并向患者发送系统消息。</p>

    <div class="reg-section">
      <h3>挂号患者列表</h3>
      <div class="reg-filter">
        <label>就诊日期：</label>
        <input v-model="regDate" type="date" class="input">
        <button class="btn small" @click="loadRegistrations">刷新</button>
      </div>
      <table class="reg-table" v-if="registrations.length">
        <thead>
          <tr>
            <th>挂号号</th>
            <th>患者ID</th>
            <th>患者姓名</th>
            <th>日期</th>
            <th>时段</th>
            <th>状态</th>
            <th>支付状态</th>
            <th>操作</th>
          </tr>
        </thead>
        <tbody>
          <tr v-for="reg in registrations" :key="reg.regId">
            <td>{{ reg.regId }}</td>
            <td>{{ reg.patientId }}</td>
            <td>{{ reg.patientName }}</td>
            <td>{{ reg.regDate }}</td>
            <td>{{ reg.regTimeSlot }}</td>
            <td>{{ reg.regStatus }}</td>
            <td>{{ reg.payStatus }}</td>
            <td>
              <button class="btn small" @click="fillTarget(reg.patientId)">写消息</button>
              <button class="btn small" @click="openRecordForm(reg)">写病历</button>
            </td>
          </tr>
        </tbody>
      </table>
      <div v-else class="empty-tip">当前日期暂无挂号记录。</div>
    </div>

    <div class="form">
      <div class="form-item">
        <label>目标用户ID：</label>
        <input v-model="targetUserId" type="text" placeholder="请输入患者的 UserID" class="input">
      </div>
      <div class="form-item">
        <label>标题：</label>
        <input v-model="title" type="text" placeholder="请输入消息标题" class="input">
      </div>
      <div class="form-item">
        <label>内容：</label>
        <textarea v-model="content" rows="4" placeholder="请输入消息内容" class="textarea"></textarea>
      </div>
      <button class="btn" @click="sendMessage">发送消息</button>
    </div>

    <div v-if="showRecordForm" class="form">
      <h3>录入诊疗记录</h3>
      <div class="form-item">
        <label>挂号号：</label>
        <span>{{ recordForm.regId }}</span>
      </div>
      <div class="form-item">
        <label>患者：</label>
        <span>{{ recordForm.patientName }}（{{ recordForm.patientId }}）</span>
      </div>
      <div class="form-item">
        <label>诊断：</label>
        <textarea v-model="recordForm.diagnosis" rows="3" class="textarea" />
      </div>
      <div class="form-item">
        <label>治疗方案：</label>
        <textarea v-model="recordForm.treatmentPlan" rows="3" class="textarea" />
      </div>
      <div class="form-item">
        <label>医嘱：</label>
        <textarea v-model="recordForm.advice" rows="3" class="textarea" />
      </div>
      <button class="btn" @click="saveRecord">保存病历</button>
      <button class="btn small" style="margin-left: 10px;" @click="showRecordForm = false">关闭</button>
    </div>

    <h3 class="sub-title">已发送消息（发件箱）</h3>
    <div class="msg-list">
      <MessageCard
        v-for="msg in outbox"
        :key="msg.messageId"
        :title="msg.title"
        :content="msg.content"
        :time="formatTime(msg.createTime)"
      />
      <div v-if="!outbox.length" class="empty-tip">暂无已发送消息</div>
    </div>
  </div>
</template>

<script>
import api from '@/api'
import MessageCard from '@/components/MessageCard'

export default {
  components: {
    MessageCard
  },
  data () {
    return {
      regDate: '',
      registrations: [],
      targetUserId: '',
      title: '',
      content: '',
      outbox: [],
      showRecordForm: false,
      recordForm: {
        regId: '',
        patientId: '',
        patientName: '',
        diagnosis: '',
        treatmentPlan: '',
        advice: ''
      }
    }
  },
  created () {
    const today = new Date()
    this.regDate = today.toISOString().slice(0, 10)
    this.loadRegistrations()
    this.loadOutbox()
  },
  methods: {
    async loadRegistrations () {
      try {
        const res = await api.get('/doctor/registrations', {
          params: { date: this.regDate || null }
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
    fillTarget (patientId) {
      this.targetUserId = patientId
    },
    openRecordForm (reg) {
      this.recordForm = {
        regId: reg.regId,
        patientId: reg.patientId,
        patientName: reg.patientName,
        diagnosis: '',
        treatmentPlan: '',
        advice: ''
      }
      this.showRecordForm = true
    },
    async sendMessage () {
      if (!this.targetUserId || !this.title || !this.content) {
        alert('请完整填写目标用户、标题和内容')
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
          this.loadOutbox()
        } else {
          alert(body.msg || '发送失败')
        }
      } catch (e) {
        console.error(e)
        alert('发送请求失败，请稍后重试')
      }
    },
    async loadOutbox () {
      try {
        const res = await api.get('/messages', {
          params: { box: 'outbox' }
        })
        const body = res.data
        if (body.error_code === 0) {
          this.outbox = body.data || []
        }
      } catch (e) {
        console.error(e)
      }
    },
    async saveRecord () {
      if (!this.recordForm.regId || !this.recordForm.diagnosis) {
        alert('请至少填写挂号号和诊断')
        return
      }
      try {
        const res = await api.post('/medical-records', {
          regId: this.recordForm.regId,
          diagnosis: this.recordForm.diagnosis,
          treatmentPlan: this.recordForm.treatmentPlan,
          advice: this.recordForm.advice
        })
        const body = res.data
        if (body.error_code === 0) {
          alert('病历保存成功')
          this.showRecordForm = false
        } else {
          alert(body.msg || '保存失败')
        }
      } catch (e) {
        console.error(e)
        alert('保存请求失败，请稍后重试')
      }
    },
    formatTime (t) {
      if (!t) return ''
      return String(t).replace('T', ' ').substring(0, 16)
    }
  }
}
</script>

<style scoped>
.doctor-panel h2 {
  color: #2f5496;
  margin-bottom: 10px;
}
.tip {
  margin-bottom: 20px;
  color: #666;
}
.reg-section {
  margin-bottom: 25px;
}
.reg-filter {
  display: flex;
  align-items: center;
  margin-bottom: 10px;
}
.reg-filter label {
  margin-right: 10px;
}
.reg-table {
  width: 100%;
  border-collapse: collapse;
  font-size: 14px;
}
.reg-table th,
.reg-table td {
  border: 1px solid #e0e0e0;
  padding: 6px 8px;
  text-align: left;
}
.form {
  border: 1px solid #e0e0e0;
  border-radius: 8px;
  padding: 15px 20px;
  margin-bottom: 25px;
}
.form-item {
  display: flex;
  align-items: center;
  margin-bottom: 12px;
}
.form-item label {
  width: 100px;
}
.input {
  flex: 1;
  padding: 6px 10px;
  border: 1px solid #e0e0e0;
  border-radius: 4px;
}
.textarea {
  flex: 1;
  padding: 6px 10px;
  border: 1px solid #e0e0e0;
  border-radius: 4px;
  resize: vertical;
}
.btn {
  margin-top: 10px;
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
.sub-title {
  margin-bottom: 10px;
}
.msg-list {
  margin-top: 10px;
}
.empty-tip {
  color: #777;
}
</style>
