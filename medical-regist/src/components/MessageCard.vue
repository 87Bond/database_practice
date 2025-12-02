<template>
  <div class="msg-card">
    <div class="msg-title">{{ title }}</div>
    <div class="msg-content">{{ content }}</div>
    <div class="msg-time">{{ time }}</div>
  </div>
</template>

<script>
export default {
  props: {
    title: String,
    content: String,
    time: String
  }
}
</script>

<style scoped>
.msg-card {
  border: 1px solid #e0e0e0;
  border-radius: 8px;
  padding: 15px;
  margin-bottom: 10px;
}
.msg-title {
  font-weight: bold;
  color: #2f5496;
  margin-bottom: 5px;
}
.msg-content {
  color: #333;
  margin-bottom: 5px;
  font-size: 14px;
}
.msg-time {
  color: #999;
  font-size: 12px;
  text-align: right;
}
</style>