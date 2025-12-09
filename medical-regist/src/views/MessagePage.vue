<template>
  <div class="message-page">
    <h2>消息中心</h2>

    <div class="message-layout">
      <!-- 左侧会话列表 -->
      <div class="conversation-sidebar">
        <div
          v-for="conv in conversations"
          :key="conv.contactUserId"
          :class="['conv-item', conv.contactUserId === selectedContactId ? 'active' : '']"
          @click="selectedContactId = conv.contactUserId"
        >
          <div class="conv-avatar">
            {{ (conv.contactUserName || conv.contactUserId || '').charAt(0) || '用' }}
          </div>
          <div class="conv-main">
            <div class="conv-header">
              <span class="conv-name">
                {{ formatDisplayName(conv.contactUserName, conv.contactUserId) }}
              </span>
              <span class="conv-time">{{ formatTime(conv.lastTime) }}</span>
            </div>
            <div class="conv-preview">
              {{ conv.lastTitle || conv.lastContent }}
            </div>
          </div>
        </div>
        <div v-if="!conversations.length" class="empty-tip sidebar-empty">
          暂无会话
        </div>
      </div>

      <!-- 右侧聊天 + 系统消息 -->
      <div class="message-main">
        <div v-if="canSend" class="send-box">
          <h3 v-if="isPatient">给就诊医生发消息</h3>
          <h3 v-else-if="isDoctor">给患者 / 管理员发消息</h3>
          <h3 v-else>发送系统消息</h3>
          <div class="form-item">
            <label>选择对象：</label>
            <select v-model="selectedContactId" class="select">
              <option value="">请选择</option>
              <option v-for="c in contactOptions" :key="c.userId" :value="c.userId">
                {{ c.displayName }}
              </option>
            </select>
          </div>
          <div class="form-item">
            <label>标题：</label>
            <input v-model="title" type="text" class="input">
          </div>
          <div class="form-item">
            <label>内容：</label>
            <textarea v-model="content" rows="3" class="textarea"></textarea>
          </div>
          <button class="btn" @click="sendMessage">发送</button>
        </div>

        <div v-if="canSend" class="chat-box">
          <h3 class="chat-title">
            对话记录
            <span v-if="currentContactName">（当前对象：{{ currentContactName }}）</span>
          </h3>
          <div v-if="!selectedContactId" class="empty-tip">
            请选择对象后查看与你之间的消息记录
          </div>
          <div v-else class="chat-list">
            <div
              v-for="msg in conversation"
              :key="msg.messageId"
              :class="['chat-item', msg.createUserId === userInfo.userId ? 'from-me' : 'from-other']"
            >
              <div class="chat-meta">
                <span class="chat-sender">
                  {{ msg.createUserId === userInfo.userId ? '我' : (msg.createUserName || '对方') }}
                </span>
                <span class="chat-time">{{ formatTime(msg.createTime) }}</span>
              </div>
              <div class="chat-title-text" v-if="msg.title">
                {{ msg.title }}
              </div>
              <div class="chat-content">
                {{ msg.content }}
              </div>
            </div>
            <div v-if="conversation.length === 0" class="empty-tip">
              暂无历史消息
            </div>
          </div>
        </div>

        <div class="msg-list">
          <h3>系统消息</h3>
          <MessageCard
            v-for="item in msgList"
            :key="item.messageId"
            :title="item.title"
            :content="item.content"
            :time="formatTime(item.createTime)"
            :subtitle="formatSender(item)"
          />
          <div v-if="msgList.length === 0" class="empty-tip">
            暂无系统消息
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import MessageCard from '@/components/MessageCard'
import api from '@/api'

export default {
  components: {
    MessageCard
  },
  data() {
    return {
      msgList: [],
      userInfo: null,
      contactOptions: [],
      selectedContactId: '',
      title: '',
      content: '',
      conversation: [],
      conversations: []
    }
  },
  created () {
    const infoStr = localStorage.getItem('userInfo')
    if (infoStr) {
      this.userInfo = JSON.parse(infoStr)
    }
    this.loadMessages()
    this.loadContacts()
    this.loadConversations()
  },
  methods: {
    async loadMessages () {
      try {
        const res = await api.get('/messages', {
          params: { box: 'inbox', isValid: true }
        })
        const body = res.data
        if (body.error_code === 0) {
          this.msgList = body.data || []
        }
      } catch (e) {
        console.error(e)
      }
    },
    async loadConversations () {
      try {
        const res = await api.get('/messages/conversations')
        const body = res.data
        if (body.error_code === 0) {
          this.conversations = body.data || []
        }
      } catch (e) {
        console.error(e)
      }
    },
    async loadContacts () {
      // 患者：根据挂号记录列出医生；其他角色：先用会话列表里的联系人
      if (this.isPatient) {
        try {
          const res = await api.get('/registrations')
          const body = res.data
          if (body.error_code === 0) {
            const regs = body.data || []
            const map = {}
            regs.forEach(r => {
              if (!map[r.doctorId]) {
                map[r.doctorId] = {
                  userId: r.doctorId,
                  displayName: this.formatDisplayName(
                    `${r.doctorName || r.doctorId}${r.departmentName ? ' - ' + r.departmentName : ''}`,
                    r.doctorId
                  )
                }
              }
            })
            this.contactOptions = Object.values(map)
          }
        } catch (e) {
          console.error(e)
        }
      } else {
        // 对医生 / 管理员，默认从历史会话里取联系人
        try {
          const res = await api.get('/messages/conversations')
          const body = res.data
          if (body.error_code === 0) {
            const list = body.data || []
            this.contactOptions = list.map(c => ({
              userId: c.contactUserId,
              displayName: this.formatDisplayName(c.contactUserName, c.contactUserId)
            }))
          }
        } catch (e) {
          console.error(e)
        }
      }
    },
    async sendMessage () {
      if (!this.canSend) {
        alert('当前角色暂不支持发送消息')
        return
      }
      if (!this.selectedContactId || !this.title || !this.content) {
        alert('请先选择对象并填写标题和内容')
        return
      }
      try {
        const res = await api.post('/messages', {
          targetUserId: this.selectedContactId,
          title: this.title,
          content: this.content
        })
        const body = res.data
        if (body.error_code === 0) {
          alert('发送成功')
          this.title = ''
          this.content = ''
          this.loadConversation()
          this.loadConversations()
        } else {
          alert(body.msg || '发送失败')
        }
      } catch (e) {
        console.error(e)
        alert('发送请求失败，请稍后重试')
      }
    },
    async loadConversation () {
      if (!this.canSend || !this.selectedContactId) {
        this.conversation = []
        return
      }
      try {
        const res = await api.get('/messages/conversation', {
          params: { targetUserId: this.selectedContactId }
        })
        const body = res.data
        if (body.error_code === 0) {
          this.conversation = body.data || []
        }
      } catch (e) {
        console.error(e)
      }
    },
    formatTime (t) {
      if (!t) return ''
      return String(t).replace('T', ' ').substring(0, 16)
    },
    formatSender (item) {
      const name = item.createUserName || item.createUserId || ''
      const id = item.createUserId || ''
      if (!name && !id) return ''
      return `来自：${name}${id ? '（' + id + '）' : ''}`
    },
    formatDisplayName (name, id) {
      if (!id && !name) return ''
      if (name && id) return `${name}（${id}）`
      return name || id
    }
  },
  computed: {
    isPatient () {
      return this.userInfo && this.userInfo.role === 'patient'
    },
    isDoctor () {
      return this.userInfo && this.userInfo.role === 'doctor'
    },
    canSend () {
      return this.userInfo && ['patient', 'doctor', 'admin', 'dept_manager'].includes(this.userInfo.role)
    },
    currentContactName () {
      const c = this.contactOptions.find(d => d.userId === this.selectedContactId)
      return c ? c.displayName : ''
    }
  },
  watch: {
    selectedContactId () {
      this.loadConversation()
    }
  }
}
</script>

<style scoped>
.message-page h2 {
  color: #2f5496;
  margin-bottom: 20px;
}
.message-layout {
  display: flex;
}
.conversation-sidebar {
  width: 260px;
  border: 1px solid #e0e0e0;
  border-radius: 8px;
  margin-right: 20px;
  max-height: 500px;
  overflow-y: auto;
  background-color: #fafafa;
}
.conv-item {
  display: flex;
  padding: 10px 12px;
  border-bottom: 1px solid #f0f0f0;
  cursor: pointer;
}
.conv-item:last-child {
  border-bottom: none;
}
.conv-item.active {
  background-color: #e6f0ff;
}
.conv-avatar {
  width: 36px;
  height: 36px;
  border-radius: 50%;
  background-color: #2f5496;
  color: #fff;
  display: flex;
  align-items: center;
  justify-content: center;
  margin-right: 10px;
  font-size: 16px;
}
.conv-main {
  flex: 1;
}
.conv-header {
  display: flex;
  justify-content: space-between;
  margin-bottom: 2px;
  font-size: 13px;
}
.conv-name {
  font-weight: bold;
  color: #333;
}
.conv-id {
  font-weight: normal;
  color: #999;
  font-size: 12px;
}
.conv-time {
  color: #999;
  font-size: 12px;
}
.conv-preview {
  font-size: 12px;
  color: #666;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}
.message-main {
  flex: 1;
}
.send-box {
  border: 1px solid #e0e0e0;
  border-radius: 8px;
  padding: 15px 20px;
  margin-bottom: 20px;
}
.form-item {
  display: flex;
  align-items: center;
  margin-bottom: 10px;
}
.form-item label {
  width: 80px;
}
.input,
.select,
.textarea {
  flex: 1;
  padding: 6px 10px;
  border: 1px solid #e0e0e0;
  border-radius: 4px;
}
.textarea {
  resize: vertical;
}
.btn {
  background-color: #2f5496;
  color: #fff;
  border: none;
  padding: 8px 16px;
  border-radius: 4px;
  cursor: pointer;
}
.btn:hover {
  background-color: #1f3a68;
}
.chat-box {
  border: 1px solid #e0e0e0;
  border-radius: 8px;
  padding: 15px 20px;
  margin: 20px 0;
}
.chat-title {
  margin-bottom: 10px;
}
.chat-list {
  max-height: 260px;
  overflow-y: auto;
}
.chat-item {
  margin-bottom: 10px;
  display: flex;
  flex-direction: column;
}
.chat-item.from-me {
  align-items: flex-end;
}
.chat-item.from-other {
  align-items: flex-start;
}
.chat-meta {
  font-size: 12px;
  color: #999;
  margin-bottom: 2px;
}
.chat-sender {
  margin-right: 8px;
}
.chat-title-text {
  font-weight: bold;
  color: #2f5496;
  margin-bottom: 2px;
}
.chat-content {
  max-width: 60%;
  background-color: #f5f5f5;
  border-radius: 6px;
  padding: 6px 10px;
  font-size: 14px;
  line-height: 1.4;
}
.chat-item.from-me .chat-content {
  background-color: #2f5496;
  color: #fff;
}
.msg-list {
  margin-top: 20px;
}
.empty-tip {
  margin-top: 10px;
  color: #777;
}
.sidebar-empty {
  padding: 10px;
}
</style>
