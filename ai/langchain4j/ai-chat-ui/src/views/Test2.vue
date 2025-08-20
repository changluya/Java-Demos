<template>
  <div class="app-container">
    <div class="chat-header">
      <h1>AI Chat Assistant</h1>
      <div class="status-indicator" :class="{ 'online': isAIOnline }"></div>
    </div>

    <div class="chat-messages">
      <div v-for="(message, index) in chatHistory" :key="index" class="message">
        <div class="message-avatar">
          <img :src="message.isUser ? userAvatar : aiAvatar" alt="Avatar" />
        </div>
        <div class="message-content">
          <div class="message-bubble" :class="{ 'user-message': message.isUser, 'ai-message': !message.isUser }">
            <typing-markdown-renderer
                v-if="!message.isUser"
                :content="message.content"
                :typing-speed="typingSpeed"
                @typing-complete="onMessageTypingComplete(index)"
            />
            <div v-else class="user-message-content" v-html="md.render(message.content)"></div>
          </div>
          <div class="message-timestamp">
            {{ message.timestamp }}
          </div>
        </div>
      </div>

      <!-- 加载状态指示器 -->
      <div v-if="isAISending" class="typing-indicator">
        <div class="message-avatar">
          <img src="https://picsum.photos/200/200?random=ai" alt="AI Avatar" />
        </div>
        <div class="message-content">
          <div class="message-bubble ai-message">
            <div class="dots-container">
              <span class="dot"></span>
              <span class="dot"></span>
              <span class="dot"></span>
            </div>
          </div>
        </div>
      </div>
    </div>

    <div class="chat-input">
      <el-input
          v-model="userInput"
          placeholder="输入您的问题..."
          @keyup.enter="sendMessage"
          ref="inputRef"
      ></el-input>
      <el-button @click="sendMessage" :disabled="isAISending">发送</el-button>
    </div>

    <div class="chat-controls">
      <el-select v-model="typingSpeed" placeholder="选择打字速度">
        <el-option label="极快" value="5"></el-option>
        <el-option label="快速" value="15"></el-option>
        <el-option label="适中" value="30"></el-option>
        <el-option label="慢速" value="50"></el-option>
        <el-option label="极慢" value="100"></el-option>
      </el-select>
      <el-button @click="clearChat">清空对话</el-button>
    </div>
  </div>
</template>

<script>
import TypingMarkdownRenderer from '@/components/TypingMarkdownRenderer.vue'
import MarkdownIt from 'markdown-it'
import 'github-markdown-css/github-markdown.css'

export default {
  components: { TypingMarkdownRenderer },
  data() {
    return {
      chatHistory: [],
      userInput: '',
      isAISending: false,
      isAIOnline: true,
      typingSpeed: 30,
      md: new MarkdownIt({
        html: true,
        linkify: true,
        typographer: true
      }),
      userAvatar: 'https://picsum.photos/200/200?random=user',
      aiAvatar: 'https://picsum.photos/200/200?random=ai',
      // 模拟AI回复的预设内容
      aiResponses: [
        `# 欢迎使用AI助手

我是您的AI助手，很高兴为您服务。我可以回答各种问题，提供信息和帮助。

### 我能做什么？
- 回答技术问题
- 提供编程帮助
- 解释概念
- 提供建议
- 进行简单的对话

您有什么问题需要帮助吗？`,

        `## 关于Vue.js

Vue.js是一个用于构建用户界面的渐进式JavaScript框架。它采用自底向上增量开发的设计。

### Vue.js的特点
- 易用：已经会了HTML、CSS、JavaScript？即刻阅读指南开始构建应用！
- 灵活：不断繁荣的生态系统，可以在一个库和一套完整框架之间自如伸缩。
- 高效：20kB min+gzip 运行大小、超快虚拟DOM、最省心的优化。

### 基本语法示例
\`\`\`javascript
new Vue({
  el: '#app',
  data: {
    message: 'Hello Vue!'
  }
})
\`\`\``,

        `## 关于Markdown

Markdown是一种轻量级标记语言，它允许人们使用易读易写的纯文本格式编写文档。

### Markdown的优点
- 纯文本，所以兼容性极强，可以用所有文本编辑器打开
- 让你专注于文字而不是排版
- 格式转换方便，Markdown的文本你可以轻松转换为HTML、PDF等
- Markdown的标记语法有极好的可读性

### 基本语法
- **标题**：# 一级标题，## 二级标题，以此类推
- **列表**：使用 - 或 * 开始无序列表，使用数字开始有序列表
- **强调**：*斜体* 或 **粗体**
- **链接**：[链接文字](链接地址)
- **图片**：![图片描述](图片地址)
- **代码**：使用 \` 包裹单行代码，使用 \`\`\` 包裹多行代码`
      ]
    }
  },
  mounted() {
    this.$refs.inputRef.focus()
    // 添加初始欢迎消息
    this.addAIMessage(this.aiResponses[0])
  },
  methods: {
    sendMessage() {
      if (!this.userInput.trim() || this.isAISending) return

      // 添加用户消息到历史记录
      this.addUserMessage(this.userInput)
      this.userInput = ''

      // 模拟AI思考时间
      this.isAISending = true
      setTimeout(() => {
        // 随机选择一个AI回复
        const randomIndex = Math.floor(Math.random() * this.aiResponses.length)
        this.addAIMessage(this.aiResponses[randomIndex])
        this.isAISending = false
      }, 1000 + Math.random() * 2000)

      // 聚焦输入框以便继续输入
      this.$nextTick(() => {
        this.$refs.inputRef.focus()
      })
    },

    addUserMessage(content) {
      this.chatHistory.push({
        content,
        isUser: true,
        timestamp: this.formatTimestamp(new Date())
      })
      this.scrollToBottom()
    },

    addAIMessage(content) {
      this.chatHistory.push({
        content,
        isUser: false,
        timestamp: this.formatTimestamp(new Date()),
        isTypingComplete: false
      })
      this.scrollToBottom()
    },

    onMessageTypingComplete(messageIndex) {
      // 更新消息状态
      this.$set(this.chatHistory[messageIndex], 'isTypingComplete', true)
      this.scrollToBottom()
    },

    formatTimestamp(date) {
      return date.toLocaleTimeString([], { hour: '2-digit', minute: '2-digit' })
    },

    scrollToBottom() {
      this.$nextTick(() => {
        const container = this.$el.querySelector('.chat-messages')
        container.scrollTop = container.scrollHeight
      })
    },

    clearChat() {
      this.chatHistory = []
      // 添加初始欢迎消息
      this.addAIMessage(this.aiResponses[0])
    }
  }
}
</script>

<style scoped>
.app-container {
  display: flex;
  flex-direction: column;
  height: 100vh;
  background-color: #f5f7fa;
}

.chat-header {
  display: flex;
  align-items: center;
  padding: 15px 20px;
  background-color: #409eff;
  color: white;
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1);
}

.status-indicator {
  width: 10px;
  height: 10px;
  border-radius: 50%;
  margin-left: 10px;
  background-color: #67c23a;
  animation: pulse 2s infinite;
}

@keyframes pulse {
  0% { opacity: 0.5; }
  50% { opacity: 1; }
  100% { opacity: 0.5; }
}
