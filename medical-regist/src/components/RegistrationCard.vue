<template>
  <div class="regist-card">
    <h3>{{ doctorName }}</h3>
    <p class="dept">{{ department }}</p>
    <div class="info">
      <span>职称：{{ title }}</span>
      <span v-if="timeSlots && timeSlots.length">可约时段：</span>
    </div>
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
    <button
      class="btn"
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
  border: 1px solid #e0e0e0;
  border-radius: 8px;
  padding: 20px;
  margin-bottom: 15px;
  box-shadow: 0 2px 5px rgba(0,0,0,0.1);
}
.regist-card h3 {
  color: #2f5496;
  margin-bottom: 8px;
}
.dept {
  color: #666;
  margin-bottom: 10px;
}
.info {
  display: flex;
  justify-content: space-between;
  margin-bottom: 15px;
  color: #333;
}
.slot-select {
  margin-bottom: 10px;
}
.slot-input {
  width: 100%;
  padding: 6px 10px;
  border-radius: 4px;
  border: 1px solid #e0e0e0;
}
.btn {
  background-color: #2f5496;
  color: white;
  border: none;
  padding: 8px 15px;
  border-radius: 4px;
  cursor: pointer;
}
.btn:hover {
  background-color: #1f3a68;
}
.btn.disabled {
  background-color: #cccccc;
  cursor: not-allowed;
}
</style>
