<template>
  <div class="message-page">
    <h2>系统消息</h2>
    <div class="msg-list">
      <MessageCard 
        v-for="(item, index) in msgList" 
        :key="index"
        :title="item.title"
        :content="item.content"
        :time="item.time"
      />
    </div>
  </div>
</template>

<script>
import MessageCard from '@/components/MessageCard'

export default {
  components: {
    MessageCard
  },
  data() {
    return {
      msgList: [
        { title: '挂号成功通知', content: '您已成功挂取张三医生的内科号，就诊时间：2025-12-05 上午9:00', time: '2025-12-02 10:30' },
        { title: '就诊提醒', content: '您的就诊时间为明天上午9点，请提前30分钟到医院取号', time: '2025-12-02 09:15' },
        { title: '系统维护通知', content: '本系统将于2025-12-10 23:00-00:00进行维护，期间暂停挂号服务', time: '2025-12-01 18:00' }
      ]
    }
  }
}
</script>

<style scoped>
.message-page h2 {
  color: #2f5496;
  margin-bottom: 20px;
}
.msg-list {
  margin-top: 20px;
}
</style>