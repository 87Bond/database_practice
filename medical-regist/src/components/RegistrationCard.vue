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
          :key="slot"
          :value="slot"
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
      // 统一映射为更直观的文字
      const map = {
        'AM-1': '上午 09:00-10:00',
        'AM-2': '上午 10:00-11:00',
        'PM-1': '下午 14:00-15:00',
        'PM-2': '下午 15:00-16:00'
      }
      return map[slot] || slot
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
      this.$emit('register', this.selectedSlot)
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
