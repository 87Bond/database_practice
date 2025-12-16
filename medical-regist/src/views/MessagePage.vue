<template>
  <div class="message-page">
    <div class="page-header">
      <div>
        <p class="eyebrow">消息中心</p>
        <h2>和医生、患者或管理员高效沟通</h2>
      </div>
      <div class="pill">支持会话 + 系统通知</div>
    </div>

    <div class="message-layout">
      <!-- 左侧会话列表 -->
      <div class="conversation-sidebar card">
        <div
          v-for="conv in contactList"
          :key="conv.userId"
          :class="['conv-item', conv.userId === selectedContactId ? 'active' : '']"
          @click="selectContact(conv.userId)"
        >
          <div class="conv-avatar">
            {{ (conv.displayName || conv.userId || '').charAt(0) || '用' }}
          </div>
          <div class="conv-main">
            <div class="conv-header">
              <span class="conv-name">
                {{ conv.displayName }}
              </span>
              <span class="conv-time">{{ formatTime(conv.lastTime) }}</span>
            </div>
            <div class="conv-preview">
              {{ conv.lastTitle || conv.lastContent || '点击开始沟通' }}
            </div>
          </div>
        </div>
        <div v-if="!contactList.length" class="empty-tip sidebar-empty">
          暂无可联系对象
        </div>
      </div>

      <!-- 右侧聊天 + 系统消息 -->
      <div class="message-main">
        <div v-if="canSend" class="send-box card">
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

        <div v-if="canSend" class="chat-box card">
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
              <div class="send-actions">
                <span class="pill ghost">{{ isPatient ? '患者端' : '工作端' }}</span>
              </div>
            </div>
            <div class="form-item">
              <label>标题</label>
              <input v-model="title" type="text" class="input" placeholder="一句话概述你的诉求或提醒">
            </div>
            <div class="form-item">
              <label>内容</label>
              <textarea v-model="content" rows="3" class="textarea" placeholder="输入想要发送的详细内容"></textarea>
            </div>
            <div class="send-footer">
              <div class="muted">发送后将在下方出现最新记录</div>
              <button class="btn" @click="sendMessage">发送</button>
            </div>
          </div>

          <div class="chat-box card">
            <div class="chat-title-row">
              <div>
                <p class="eyebrow small">历史对话</p>
                <h3 class="chat-title">{{ currentContactName }}</h3>
              </div>
            </div>
            <div v-if="!conversation.length" class="empty-tip">
              暂无历史消息
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
            </div>
          </div>
        </div>

        <div class="msg-list card">
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
    selectContact (id) {
      this.selectedContactId = id
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
      const c = this.contactList.find(d => d.userId === this.selectedContactId)
      return c ? c.displayName : ''
    },
    contactList () {
      const map = {}
      const list = []
      this.contactOptions.forEach(c => {
        const item = {
          userId: c.userId,
          displayName: c.displayName,
          lastTitle: c.lastTitle,
          lastContent: c.lastContent,
          lastTime: c.lastTime
        }
        map[c.userId] = item
        list.push(item)
      })
      this.conversations.forEach(conv => {
        const id = conv.contactUserId
        if (map[id]) {
          map[id].lastTitle = conv.lastTitle || map[id].lastTitle
          map[id].lastContent = conv.lastContent || map[id].lastContent
          map[id].lastTime = conv.lastTime || map[id].lastTime
        } else {
          list.push({
            userId: id,
            displayName: this.formatDisplayName(conv.contactUserName, id),
            lastTitle: conv.lastTitle,
            lastContent: conv.lastContent,
            lastTime: conv.lastTime
          })
        }
      })
      return list
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
.page-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 12px;
  gap: 12px;
}

.eyebrow {
  color: var(--primary-dark);
  font-weight: 700;
  letter-spacing: 0.4px;
  margin-bottom: 6px;
}

.page-header h2 {
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

.message-layout {
  display: grid;
  grid-template-columns: 280px 1fr;
  gap: 18px;
}

.conversation-sidebar {
  max-height: 520px;
  overflow-y: auto;
  padding: 4px 0;
}

.conv-item {
  display: flex;
  padding: 12px 16px;
  cursor: pointer;
  border-radius: 12px;
  margin: 6px 10px;
  transition: all 0.2s ease;
}

.conv-item.active,
.conv-item:hover {
  background: linear-gradient(90deg, rgba(59, 110, 227, 0.12), rgba(90, 216, 255, 0.08));
  box-shadow: 0 10px 22px rgba(59, 110, 227, 0.12);
}

.conv-avatar {
  width: 40px;
  height: 40px;
  border-radius: 50%;
  background: linear-gradient(135deg, var(--primary), var(--primary-dark));
  color: #fff;
  display: flex;
  align-items: center;
  justify-content: center;
  margin-right: 12px;
  font-size: 16px;
  font-weight: 700;
}

.conv-main {
  flex: 1;
}

.conv-header {
  display: flex;
  justify-content: space-between;
  margin-bottom: 4px;
  font-size: 13px;
  color: var(--text-main);
}

.conv-name {
  font-weight: 700;
}

.conv-time {
  color: var(--text-muted);
  font-size: 12px;
}

.conv-preview {
  font-size: 13px;
  color: var(--text-muted);
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

.message-main {
  display: grid;
  gap: 16px;
}

.send-box {
  display: grid;
  gap: 12px;
}

.form-item {
  display: grid;
  gap: 6px;
}

.form-item label {
  color: var(--text-muted);
  font-weight: 600;
}

.input,
.select,
.textarea {
  width: 100%;
  padding: 10px 12px;
  border: 1px solid rgba(59, 110, 227, 0.18);
  border-radius: 10px;
  background: #f8faff;
}

.textarea {
  resize: vertical;
}

.btn {
  justify-self: flex-start;
  padding: 10px 16px;
  border-radius: 10px;
  background: linear-gradient(135deg, var(--primary), var(--primary-dark));
  color: #fff;
  border: none;
  cursor: pointer;
  box-shadow: 0 8px 20px rgba(59, 110, 227, 0.3);
}

.btn:hover {
  transform: translateY(-1px);
  box-shadow: 0 12px 24px rgba(39, 76, 159, 0.35);
}

.chat-box {
  display: grid;
  gap: 10px;
}

.chat-title {
  margin-bottom: 2px;
}

.chat-list {
  max-height: 260px;
  overflow-y: auto;
  display: grid;
  gap: 12px;
  padding-right: 6px;
}

.chat-item {
  display: flex;
  flex-direction: column;
  gap: 6px;
}

.chat-item.from-me {
  align-items: flex-end;
}

.chat-item.from-other {
  align-items: flex-start;
}

.chat-meta {
  font-size: 12px;
  color: var(--text-muted);
  display: flex;
  gap: 8px;
}

.chat-title-text {
  font-weight: 700;
  color: var(--primary-dark);
}

.chat-content {
  max-width: 70%;
  background: #f1f5ff;
  border-radius: 12px;
  padding: 10px 12px;
  font-size: 14px;
  line-height: 1.5;
  box-shadow: 0 6px 14px rgba(59, 110, 227, 0.14);
}

.chat-item.from-me .chat-content {
  background: linear-gradient(135deg, var(--primary), var(--primary-dark));
  color: #fff;
  box-shadow: 0 8px 18px rgba(39, 76, 159, 0.22);
}

.msg-list h3 {
  margin-bottom: 8px;
}

.empty-tip {
  margin-top: 8px;
  color: var(--text-muted);
}

.sidebar-empty {
  padding: 10px;
  color: var(--text-muted);
}
</style>
