<template>
  <div class="regist-card card">
    <div class="card-header">
      <div>
        <div class="doc-name">{{ doctorName }}</div>
        <p class="dept">{{ department }}</p>
      </div>
      <span class="badge">{{ title }}</span>
    </div>
    <div class="info">
      <span class="label">可约时段</span>
      <div class="slot-select" v-if="timeSlots && timeSlots.length">
        <select v-model="selectedSlot" class="slot-input">
          <option disabled value="">请选择时段</option>
          <option
            v-for="slot in timeSlots"
            :key="slot.slotId"
            :value="slot.slotId"
          >
            {{ formatSlot(slot) }}
          </option>
        </select>
      </div>
      <div v-else class="no-slot">暂无号源，试试其他日期</div>
    </div>
    <button
      class="primary-btn action-btn"
      :class="{ disabled: !timeSlots || !timeSlots.length }"
      :disabled="!timeSlots || !timeSlots.length"
      @click="handleRegister"
    >
      {{ timeSlots && timeSlots.length ? '立即挂号' : '暂无号源' }}
    </button>
  </div>
</template>

<script>
export default {
  // 接收父组件传的参数
  props: {
    doctorName: String,
    department: String,
    title: String,
    timeSlots: {
      type: Array,
      default: () => []
    }
  },
  data () {
    return {
      selectedSlot: ''
    }
  },
  methods: {
    formatSlot (slot) {
      // 后端已经返回了人类可读的 label 和起止时间
      if (!slot) return ''
      if (slot.label) return slot.label + `（剩余 ${slot.remain}）`
      if (slot.startTime && slot.endTime) {
        return `${slot.startTime}-${slot.endTime}（剩余 ${slot.remain}）`
      }
      return `剩余 ${slot.remain}`
    },
    handleRegister () {
      if (!this.timeSlots || !this.timeSlots.length) {
        alert('该日期暂无可预约号源，请尝试更换日期或科室')
        return
      }
      if (!this.selectedSlot) {
        alert('请先选择就诊时段')
        return
      }
      const chosen = this.timeSlots.find(s => s.slotId === this.selectedSlot)
      this.$emit('register', chosen)
    }
  }
}
</script>

<style scoped>
.regist-card {
  display: grid;
  gap: 12px;
}

.card-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 12px;
}

.doc-name {
  font-size: 18px;
  font-weight: 700;
  color: var(--primary-dark);
}

.dept {
  color: var(--text-muted);
  margin-top: 4px;
}

.badge {
  padding: 8px 12px;
  border-radius: 10px;
  background: rgba(59, 110, 227, 0.1);
  color: var(--primary-dark);
  font-weight: 600;
  border: 1px solid rgba(59, 110, 227, 0.25);
}

.info {
  display: grid;
  gap: 8px;
}

.label {
  color: var(--text-muted);
  font-weight: 600;
}

.slot-select {
  margin-bottom: 6px;
}

.slot-input {
  width: 100%;
  padding: 10px 12px;
  border-radius: 10px;
  border: 1px solid rgba(59, 110, 227, 0.2);
  background: #f8faff;
}

.slot-input:focus {
  outline: none;
  border-color: var(--primary);
  box-shadow: 0 0 0 3px rgba(59, 110, 227, 0.18);
}

.no-slot {
  color: var(--text-muted);
}

.action-btn {
  justify-self: flex-start;
  min-width: 140px;
}

.action-btn.disabled {
  background: #cdd5e4;
  box-shadow: none;
  cursor: not-allowed;
}
</style>
