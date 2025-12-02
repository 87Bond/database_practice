<template>
  <div class="regist-card">
    <h3>{{ doctorName }}</h3>
    <p class="dept">{{ department }}</p>
    <div class="info">
      <span>职称：{{ title }}</span>
      <span>剩余号源：{{ remainNum }}</span>
    </div>
    <button class="btn">立即挂号</button>
  </div>
</template>

<script>
export default {
  // 接收父组件传的参数
  props: {
    doctorName: String,
    department: String,
    title: String,
    remainNum: Number
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
</style>