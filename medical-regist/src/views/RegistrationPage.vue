<template>
  <div class="registration-page">
    <div class="header">
      <div>
        <p class="eyebrow">门诊挂号</p>
        <h2>选择科室与日期，快速预约心仪专家</h2>
      </div>
      <div class="pill">今日号源实时更新</div>
    </div>

    <div class="filter card">
      <div class="filter-group">
        <label>选择科室</label>
        <select v-model="selectedDeptId" class="select">
          <option value="">请选择科室</option>
          <option v-for="dept in departmentList" :key="dept.departmentId" :value="dept.departmentId">
            {{ dept.departmentName }}（{{ dept.location }}）
          </option>
        </select>
      </div>
      <div class="filter-group">
        <label>就诊日期</label>
        <input v-model="selectedDate" type="date" class="select">
      </div>
      <div class="filter-hint">选择后自动为你展示对应科室可预约医生</div>
    </div>
    <!-- 挂号卡片列表 -->
    <div class="card-list">
      <RegistrationCard
        v-for="doctor in doctorList"
        :key="doctor.userId"
        :doctorName="doctor.doctorName"
        :department="getDeptName(doctor.departmentId)"
        :title="doctor.title"
        :timeSlots="doctor.availableSlots"
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
      if (!timeSlot || !timeSlot.slotId) {
        alert('请选择有效的就诊时段')
        return
      }
      try {
        const res = await api.post('/registrations', {
          doctorId: doctor.userId,
          departmentId: this.selectedDeptId,
          regDate: this.selectedDate,
          slotId: timeSlot.slotId
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
.registration-page {
  display: grid;
  gap: 18px;
}

.header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  gap: 12px;
}

.eyebrow {
  color: var(--primary-dark);
  font-weight: 700;
  letter-spacing: 0.4px;
  margin-bottom: 6px;
}

.header h2 {
  font-size: 22px;
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

.filter {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(240px, 1fr));
  gap: 16px;
  align-items: flex-end;
}

.filter-group {
  display: grid;
  gap: 8px;
}

.filter-group label {
  color: var(--text-muted);
  font-weight: 600;
}

.select {
  padding: 12px;
  border: 1px solid rgba(59, 110, 227, 0.2);
  border-radius: 12px;
  background: #f8faff;
  transition: border-color 0.2s ease, box-shadow 0.2s ease;
}

.select:focus {
  outline: none;
  border-color: var(--primary);
  box-shadow: 0 0 0 3px rgba(59, 110, 227, 0.2);
}

.filter-hint {
  color: var(--text-muted);
  font-size: 13px;
}

.card-list {
  margin-top: 4px;
  display: grid;
  gap: 16px;
}

.empty-tip {
  margin-top: 10px;
  color: var(--text-muted);
}
</style>
