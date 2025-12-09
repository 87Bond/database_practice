<template>
  <div class="admin-panel">
    <h2>管理员用户管理</h2>
    <p class="tip">通过用户 ID 冻结 / 解冻账号，并创建医生 / 科室管理员 / 系统管理员账号。</p>

    <div class="form">
      <div class="form-item">
        <label>用户ID：</label>
        <input v-model="userId" type="text" placeholder="例如：P2023... / DR0001" class="input">
      </div>
      <div class="btn-group">
        <button class="btn freeze" @click="freezeUser">冻结</button>
        <button class="btn unfreeze" @click="unfreezeUser">解冻</button>
      </div>
      <p v-if="resultMsg" class="result">{{ resultMsg }}</p>
    </div>

    <div class="form create-form">
      <h3>创建医生账号</h3>
      <div class="form-item">
        <label>姓名：</label>
        <input v-model="doctorForm.doctorName" type="text" class="input">
      </div>
      <div class="form-item">
        <label>手机号：</label>
        <input v-model="doctorForm.phone" type="text" class="input">
      </div>
      <div class="form-item">
        <label>密码：</label>
        <input v-model="doctorForm.password" type="password" class="input">
      </div>
      <div class="form-item">
        <label>科室：</label>
        <select v-model="doctorForm.departmentId" class="input">
          <option value="">请选择科室</option>
          <option v-for="d in departments" :key="d.departmentId" :value="d.departmentId">
            {{ d.departmentName }}（{{ d.departmentId }}）
          </option>
        </select>
      </div>
      <div class="form-item">
        <label>职称：</label>
        <input v-model="doctorForm.title" type="text" class="input">
      </div>
      <div class="form-item">
        <label>专长：</label>
        <input v-model="doctorForm.specialty" type="text" class="input">
      </div>
      <div class="form-item">
        <label>办公电话：</label>
        <input v-model="doctorForm.workPhone" type="text" class="input">
      </div>
      <button class="btn create" @click="createDoctor">创建医生</button>
    </div>

    <div class="form create-form">
      <h3>创建科室管理员账号</h3>
      <div class="form-item">
        <label>姓名：</label>
        <input v-model="deptForm.userName" type="text" class="input">
      </div>
      <div class="form-item">
        <label>手机号：</label>
        <input v-model="deptForm.phone" type="text" class="input">
      </div>
      <div class="form-item">
        <label>密码：</label>
        <input v-model="deptForm.password" type="password" class="input">
      </div>
      <div class="form-item">
        <label>科室：</label>
        <select v-model="deptForm.departmentId" class="input">
          <option value="">请选择科室</option>
          <option v-for="d in departments" :key="d.departmentId" :value="d.departmentId">
            {{ d.departmentName }}（{{ d.departmentId }}）
          </option>
        </select>
      </div>
      <div class="form-item">
        <label>办公电话：</label>
        <input v-model="deptForm.workPhone" type="text" class="input">
      </div>
      <button class="btn create" @click="createDeptManager">创建科室管理员</button>
    </div>

    <div class="form create-form">
      <h3>创建系统管理员账号</h3>
      <div class="form-item">
        <label>姓名：</label>
        <input v-model="sysForm.userName" type="text" class="input">
      </div>
      <div class="form-item">
        <label>手机号：</label>
        <input v-model="sysForm.phone" type="text" class="input">
      </div>
      <div class="form-item">
        <label>密码：</label>
        <input v-model="sysForm.password" type="password" class="input">
      </div>
      <div class="form-item">
        <label>办公电话：</label>
        <input v-model="sysForm.workPhone" type="text" class="input">
      </div>
      <button class="btn create" @click="createSystemUser">创建系统管理员</button>
    </div>
  </div>
</template>

<script>
import api from '@/api'

export default {
  data () {
    return {
      userId: '',
      resultMsg: '',
      departments: [],
      doctorForm: {
        doctorName: '',
        phone: '',
        password: '',
        departmentId: '',
        title: '',
        specialty: '',
        workPhone: ''
      },
      deptForm: {
        userName: '',
        phone: '',
        password: '',
        departmentId: '',
        workPhone: ''
      },
      sysForm: {
        userName: '',
        phone: '',
        password: '',
        workPhone: ''
      }
    }
  },
  created () {
    this.loadDepartments()
  },
  methods: {
    async loadDepartments () {
      try {
        const res = await api.get('/departments')
        const body = res.data
        if (body.error_code === 0) {
          this.departments = body.data || []
        }
      } catch (e) {
        console.error(e)
      }
    },
    async freezeUser () {
      if (!this.userId) {
        alert('请先输入用户ID')
        return
      }
      try {
        const res = await api.post(`/users/${this.userId}/freeze`)
        const body = res.data
        this.resultMsg = body.msg || '操作完成'
      } catch (e) {
        console.error(e)
        this.resultMsg = '冻结失败，请检查用户ID或稍后重试'
      }
    },
    async unfreezeUser () {
      if (!this.userId) {
        alert('请先输入用户ID')
        return
      }
      try {
        const res = await api.post(`/users/${this.userId}/unfreeze`)
        const body = res.data
        this.resultMsg = body.msg || '操作完成'
      } catch (e) {
        console.error(e)
        this.resultMsg = '解冻失败，请检查用户ID或稍后重试'
      }
    },
    async createDoctor () {
      const f = this.doctorForm
      if (!f.doctorName || !f.phone || !f.password || !f.departmentId) {
        alert('请至少填写姓名、手机号、密码和科室ID')
        return
      }
      try {
        const res = await api.post('/users/doctors', f)
        const body = res.data
        alert(body.msg || '创建完成')
      } catch (e) {
        console.error(e)
        alert('创建医生失败')
      }
    },
    async createDeptManager () {
      const f = this.deptForm
      if (!f.userName || !f.phone || !f.password || !f.departmentId) {
        alert('请至少填写姓名、手机号、密码和科室ID')
        return
      }
      try {
        const res = await api.post('/users/dept-managers', f)
        const body = res.data
        alert(body.msg || '创建完成')
      } catch (e) {
        console.error(e)
        alert('创建科室管理员失败')
      }
    },
    async createSystemUser () {
      const f = this.sysForm
      if (!f.userName || !f.phone || !f.password) {
        alert('请至少填写姓名、手机号和密码')
        return
      }
      try {
        const res = await api.post('/users/system-users', f)
        const body = res.data
        alert(body.msg || '创建完成')
      } catch (e) {
        console.error(e)
        alert('创建系统管理员失败')
      }
    }
  }
}
</script>

<style scoped>
.admin-panel h2 {
  color: #2f5496;
  margin-bottom: 10px;
}
.tip {
  margin-bottom: 20px;
  color: #666;
}
.form {
  border: 1px solid #e0e0e0;
  border-radius: 8px;
  padding: 15px 20px;
  max-width: 500px;
  margin-bottom: 20px;
}
.form-item {
  display: flex;
  align-items: center;
  margin-bottom: 15px;
}
.form-item label {
  width: 80px;
}
.input {
  flex: 1;
  padding: 6px 10px;
  border: 1px solid #e0e0e0;
  border-radius: 4px;
}
.btn-group {
  display: flex;
  gap: 10px;
  margin-bottom: 10px;
}
.btn {
  padding: 8px 16px;
  border: none;
  border-radius: 4px;
  color: #fff;
  cursor: pointer;
}
.freeze {
  background-color: #d9534f;
}
.unfreeze {
  background-color: #5cb85c;
}
.btn:hover {
  opacity: 0.9;
}
.result {
  color: #333;
}
.create-form h3 {
  margin-bottom: 10px;
  color: #2f5496;
}
</style>
