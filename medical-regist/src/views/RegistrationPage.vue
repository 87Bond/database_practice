<template>
  <div class="registration-page">
    <h2>门诊挂号</h2>
    <div class="dept-select">
      <label>选择科室：</label>
      <select v-model="selectedDeptId" class="select">
        <option value="">请选择科室</option>
        <option v-for="dept in departmentList" :key="dept.departmentId" :value="dept.departmentId">
          {{ dept.departmentName }}（{{ dept.location }}）
        </option>
      </select>
    </div>
    <div class="date-select">
      <label>就诊日期：</label>
      <input v-model="selectedDate" type="date" class="select">
    </div>
    <!-- 挂号卡片列表 -->
    <div class="card-list">
      <RegistrationCard 
        v-for="doctor in doctorList"
        :key="doctor.userId"
        :doctorName="doctor.doctorName"
        :department="getDeptName(doctor.departmentId)"
        :title="doctor.title"
        :timeSlots="doctor.availableTimeSlots"
        @register="slot => handleRegister(doctor, slot)"
      />
      <div v-if="doctorList.length === 0" class="empty-tip">
        请先选择科室和日期，将自动加载可预约医生
      </div>
    </div>
  </div>
</template>

<script>
// 导入挂号卡片组件
import RegistrationCard from '@/components/RegistrationCard'
import api from '@/api'

export default {
  components: {
    RegistrationCard
  },
  data() {
    return {
      departmentList: [],
      selectedDeptId: '',
      selectedDate: '',
      doctorList: []
    }
  },
  created () {
    this.loadDepartments()
  },
  watch: {
    selectedDeptId () {
      this.loadDoctors()
    },
    selectedDate () {
      this.loadDoctors()
    }
  },
  methods: {
    async loadDepartments () {
      try {
        const res = await api.get('/departments')
        const body = res.data
        if (body.error_code === 0) {
          this.departmentList = body.data || []
        }
      } catch (e) {
        console.error(e)
      }
    },
    async loadDoctors () {
      if (!this.selectedDeptId || !this.selectedDate) {
        this.doctorList = []
        return
      }
      try {
        const res = await api.get('/doctors', {
          params: {
            departmentId: this.selectedDeptId,
            date: this.selectedDate
          }
        })
        const body = res.data
        if (body.error_code === 0) {
          this.doctorList = body.data || []
        } else {
          this.doctorList = []
        }
      } catch (e) {
        console.error(e)
        this.doctorList = []
      }
    },
    async handleRegister (doctor, timeSlot) {
      if (!this.selectedDate || !this.selectedDeptId) {
        alert('请先选择日期和科室')
        return
      }
      try {
        const res = await api.post('/registrations', {
          doctorId: doctor.userId,
          departmentId: this.selectedDeptId,
          regDate: this.selectedDate,
          regTimeSlot: timeSlot
        })
        const body = res.data
        if (body.error_code === 0) {
          alert('挂号成功：' + body.data.regId)
        } else {
          alert(body.msg || '挂号失败')
        }
      } catch (e) {
        console.error(e)
        alert('挂号请求失败，请稍后重试')
      }
    },
    getDeptName (deptId) {
      const dept = this.departmentList.find(d => d.departmentId === deptId)
      return dept ? dept.departmentName : ''
    }
  }
}
</script>

<style scoped>
.registration-page h2 {
  color: #2f5496;
  margin-bottom: 20px;
}
.dept-select {
  margin-bottom: 20px;
}
.date-select {
  margin-bottom: 20px;
}
.select {
  padding: 8px;
  border: 1px solid #e0e0e0;
  border-radius: 4px;
  margin-left: 10px;
}
.card-list {
  margin-top: 20px;
}
.empty-tip {
  margin-top: 10px;
  color: #777;
}
</style>
