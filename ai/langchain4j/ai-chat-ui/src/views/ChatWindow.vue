<template>
  <div class="app-layout">
    <div class="sidebar">
      <div class="logo-section">
        <img src="@/assets/logo.png" alt="智能助手" width="40" height="40" />
        <span class="logo-text">智能助手</span>
      </div>
      <el-button class="new-chat-button" @click="newChat">
        <i class="fa-solid fa-plus"></i>
        &nbsp;新会话
      </el-button>
      <div class="chat-history">
        <h3>会话历史</h3>
        <el-scrollbar style="height: 70vh;">
          <div class="history-item" v-for="(item, index) in chatHistory" :key="index" @click="loadChat(item.id)">
            <i class="fa-solid fa-comment-dots"></i>
            <span>{{ item.title || '未命名会话' }}</span>
          </div>
        </el-scrollbar>
      </div>
    </div>
    <div class="main-content">
      <div class="chat-container">
        <!-- 使用flex容器包装消息列表和输入框，保持它们宽度一致 -->
        <div class="message-input-wrapper" :class="{'input-at-bottom': messages.length > 0}">
          <!-- 修改后的欢迎界面 -->
          <div class="welcome-container" v-if="showWelcome && messages.length == 0">
            <div class="welcome-content">
              <div class="welcome-header">
                <img src="@/assets/logo.png" alt="智能助手" class="welcome-logo" />
                <h1 class="welcome-title">我是 智能助手，很高兴见到你！</h1>
              </div>
              <p class="welcome-description">我可以帮你检索语雀、禅道等内容，请把你的问题发给我吧～</p>
            </div>
          </div>

          <div class="message-list"  ref="messaggListRef" v-show="messages.length > 0">
            <div
                v-for="(message, index) in messages"
                :key="index"
                :class="
                message.isUser ? 'message user-message' : 'message bot-message'
              "
            >
              <!-- 会话图标 -->
              <div v-if="!message.isUser" class="message-avatar">
                <img src="@/assets/logo.png" alt="数栈知识库小智" class="bot-logo" />
              </div>

              <div :class="!message.isUser ? 'message-content': 'message-user-content'">
                <!-- 会话内容 -->
                <div v-if="!message.isUser && message.steps && message.steps.length > 0">
                  <div class="step-container" style="height: auto;font-size: 10px;">
                    <div class="search-thinking-container">
                      <img
                          src="@/assets/images/search.png"
                          :class="message.stepsFinished ? 'auto-pulse-img' : 'auto-pulse-img-keyframes'"
                          style="width: 25px; height: 25px;"
                          alt="搜索"
                      />
                      <span :class="message.stepsFinished ? 'thinking-text' : 'thinking-text-keyframes'">
                          {{message.stepsFinished ? '已完成检索' : '正在搜索中...' }}
                        </span>
                    </div>
                    <!-- active属性：表示当前是第几个步骤 -->
                    <el-steps direction="vertical" :active="message.activeStep" :space="90" finish-status="success">
                      <el-step
                          v-for="(step, index) in message.steps"
                          :key="step.type"
                          :title="step.title"
                      >
                        <template #description>
                          <div class="step-content">
                            <!-- 关键词标签容器 -->
                            <div class="keyword-tags" v-if="step.keywords && step.keywords.length > 0">
                              <div class="tags-container">
                                <!-- 修改关键字标签部分 -->
                                <span
                                    class="keyword-tag"
                                    v-for="(keyword, i) in step.keywords"
                                    :key="i"
                                    @click.stop="handleKeywordClick(keyword.urls)"
                                    :style="{ cursor: keyword.urls?.length ? 'pointer' : 'default' }"
                                >
                                    <i class="el-icon-search"></i>
                                    {{ keyword.key }}
                                  </span>
                              </div>
                            </div>
                            <div class="step-description">{{ step.content }}</div>
                          </div>
                        </template>
                      </el-step>
                    </el-steps>
                  </div>
                </div>

                <div
                    class="loading-dots"
                    v-if="!message.isUser && !message.stepsFinished"
                >
                  <span class="dot"></span>
                  <span class="dot"></span>
                  <span class="dot"></span>
                </div>
                <div v-if="message.isUser" v-html="message.content"></div>
                <!-- AI回答部分 - 替换为TypingMarkdownRenderer -->
                <TypingMarkdownRenderer
                    class="markdown-renderer"
                    v-if="!message.isUser && !message.isThinking && message.content"
                    :content="message.content"
                    :typing-speed="typingSpeed"
                    :should-type="!message.isHistory"
                    @typing-complete="onMessageTypingComplete(index)"
                />
              </div>
            </div>
          </div>

          <div class="inputBox">
            <div class="input-area">
              <el-input
                  v-model="inputMessage"
                  placeholder="给数栈小智发送消息"
                  type="textarea"
                  :autosize="{ minRows: 2, maxRows: 1000 }"
                  @keydown.native="handleKeyCode($event)"
                  class="custom-no-border"
              ></el-input>
              <div class="send-area">
                <!-- <el-switch
                    v-model="isThinkingMode"
                    active-text="深度思考"
                    inactive-text=""
                    active-color="#13ce66"
                    inactive-color="#ff4949"
                    class="thinking-switch"
                /> -->
                <el-button
                    @click="sendMessage"
                    :disabled="isSending"
                    type="primary"
                    circle
                    class="send-btn"
                    size="mini"
                >
                  <i class="el-icon-top"></i>
                </el-button>
              </div>
            </div>
          </div>
        </div>
      </div>
      <!-- 新增底部固定提示 - 移动到main-content内部，确保在右侧区域 -->
      <div class="footer-notice">
        内容由 AI 生成，请仔细甄别
      </div>
    </div>
    <!-- 在模板末尾添加右侧模板 -->
    <keyword-drawer
        :urls="currentUrls"
        :visible="drawerVisible"
        @update:visible="drawerVisible = $event"
    />
  </div>
</template>

<script setup>
import { onMounted, ref, watch, reactive } from 'vue'
import { v4 as uuidv4 } from 'uuid'
import MarkdownIt from 'markdown-it'
import TypingMarkdownRenderer from '@/components/TypingMarkdownRenderer.vue' // 引入新组件
import KeywordDrawer from '@/components/KeywordDrawer.vue'
import { chatStream } from '@/api/chatApi'

// 右侧关键字列表
const drawerVisible = ref(false)
const currentUrls = ref([])

// 新增：定义消息缓冲区和当前消息索引
const messageBuffer = ref('') // 用于累积流式数据
const currentBotMessageIndex = ref(-1) // 记录当前机器人消息在messages数组中的索引
const typingSpeed = ref(30) // 打字速度

const messaggListRef = ref()
const isSending = ref(false)
const uuid = ref('')
const inputMessage = ref('')
const messages = ref([])
const chatHistory = ref([])
const md = ref(new MarkdownIt())

const isThinkingMode = ref(false);

// 思考
const thinkBuffer = ref(''); // 累积思考内容
const isCollectingThink = ref(false); // 是否在收集思考内容
const hasThinkContent = ref(false); // 当前消息是否包含思考内容

const showWelcome = ref(true)

onMounted(() => {
  initUUID() // 初始化UUID
  loadChatHistory()
// 修改watch部分
  watch(
    messages,
    (newVal) => {
      showWelcome.value = newVal.length === 0
    },
    { immediate: true }
  )
  // 注释掉原有的hello()调用，避免自动发送消息
  // hello() // 保留初次页面渲染时的chat接口调用
  // console.log("import.meta.env.VITE_API_URL=>", import.meta.env.VITE_API_URL)
})

// 新增：防抖函数
const debounce = (func, wait) => {
  let timeout
  return function(...args) {
    clearTimeout(timeout)
    timeout = setTimeout(() => {
      func.apply(this, args)
    }, wait)
  }
}

// 修改后的scrollToBottom函数
const scrollToBottom = debounce(() => {
  if (messaggListRef.value) {
    // 使用平滑滚动
    messaggListRef.value.scrollTo({
      top: messaggListRef.value.scrollHeight,
      behavior: 'smooth'
    })
  }
}, 100) // 100ms防抖延迟

const hello = () => {
  // sendRequest('你好，检索语雀、知识库，禅道')
  // sendRequest('你好')
}

const sendMessage = () => {
  if (inputMessage.value.trim()) {
    sendRequest(inputMessage.value.trim())
    inputMessage.value = ''
  }
}

const sendRequest = (message) => {
  // 重置思考相关状态
  thinkBuffer.value = '';
  isCollectingThink.value = false;
  hasThinkContent.value = false;

  isSending.value = true
  messageBuffer.value = ''
  currentBotMessageIndex.value = messages.value.length

  const userMsg = {
    id: Date.now(),
    isUser: true,
    content: message,
    isTyping: false,
    isThinking: false,
  }
  
  messages.value.push(userMsg)

  // 修改：初始化机器人消息时不预设任何步骤
  const botMsg = {
    id: Date.now() + 1,
    isUser: false,
    content: '', 
    fullContent: '',
    steps: [], // 初始为空数组，根据实际返回数据动态添加
    activeStep: 0,
    isTyping: true,
    stepsFinished: false,
    isThinking: true
  }
  messages.value.push(botMsg)
  scrollToBottom()

  // 聊天
  chatStream(
      uuid.value,
      message,
      isThinkingMode.value ? 1 : 0,
      (e) => {
        const fullText = e.event.target.responseText
        console.log("fullText=>", fullText)
        let newText = fullText.substring(messages.value.at(-1).fullContent.length)
        const lines = newText.split('\n');

        // 累积更新内容，减少DOM操作
        let accumulatedContent = ''

        lines.forEach(line => {
          if (!line.trim()) return;

          let isNotHasLine = !line.includes('|');
          let [stepType, contentType, ...contentArr] = line.split('|');
          stepType = removeDataPrefix(stepType);
          let content = contentArr.join('|');

          // if (content.trim() === '```' || content.trim() === '```markdown' || content.trim() === 'markdown'){
          //   return;
          // }
          if (content.trim() === '```markdown' || content.trim() === 'markdown'){
            return;
          }

          const currentBotMsg = messages.value.at(-1);

          // 修改：动态处理步骤类型
          if (stepType === 'knowledge' || stepType === 'zentao') {
            // 检查是否已存在该步骤
            let step = currentBotMsg.steps.find(s => s.type === stepType);

            if (!step) {
              // 如果步骤不存在，则创建新步骤
              step = {
                title: stepType === 'knowledge' ? '检索语雀知识库' : '检索禅道',
                content: '检索中...',
                type: stepType,
                keywords: []
              };
              currentBotMsg.steps.push(step);
            }

            // 结束条件
            if (content.endsWith("end")) {
              currentBotMsg.activeStep += 1;
              // 检查是否所有步骤都已完成
              const hasKnowledge = currentBotMsg.steps.some(s => s.type === 'knowledge');
              const hasZentao = currentBotMsg.steps.some(s => s.type === 'zentao');

              // 修改：只有当所有存在的步骤都完成时才标记为完成
              const knowledgeFinished = !hasKnowledge || currentBotMsg.steps.find(s => s.type === 'knowledge').content !== '检索中...';
              const zentaoFinished = !hasZentao || currentBotMsg.steps.find(s => s.type === 'zentao').content !== '检索中...';

              currentBotMsg.stepsFinished = knowledgeFinished && zentaoFinished;
              currentBotMsg.isThinking = !currentBotMsg.stepsFinished;
            } else {
              updateStep(currentBotMsg, stepType, content);
            }
          }
          else if (stepType === 'final') {
            // 第一次进入时的严格条件检测
            if (!isCollectingThink.value &&
                (content.includes("<") ||
                    content.includes("<t") ||
                    content.includes("<th"))) {
              console.log("enter think...")
              thinkBuffer.value += content;
              isCollectingThink.value = true;
              hasThinkContent.value = true;

              currentBotMsg.isTyping = true;
              return;
            }

            // 正在收集思考内容
            if (isCollectingThink.value) {
              thinkBuffer.value += content;

              console.log("thinkBuffer.value=>", thinkBuffer.value)
              // 检测到完整<think>标签时开始转换
              if (thinkBuffer.value.includes("<think>")) {
                console.log("正式开始转换=》", thinkBuffer.value)
                // 移除<think>标签并转换为Markdown引用
                const processedContent = thinkBuffer.value
                    .replace("<think>", "")
                    .replace("</think>", "")
                    .split('\n\n')
                    .map(line => `> ${line}`)
                    .join('\n');

                // 追加到正式内容
                currentBotMsg.content = processedContent;
                console.log("转换后=》", currentBotMsg.content)

                // 检测是否结束思考块
                if (thinkBuffer.value.includes("</think>")) {
                  isCollectingThink.value = false;
                  thinkBuffer.value = '';
                }

                // 只在内容变化较大时才触发滚动
                // 累积内容而不是立即更新
                accumulatedContent += line + '\n'
                if (accumulatedContent.length > 100) {
                  scrollToBottom()
                }
                return;
              }
            }

            console.log("结束思考模式阶段")

            // 普通AI回复内容
            currentBotMsg.content += content;
            // 可能思考模式先触发了，所以这里需要check下
            if (!currentBotMsg.isThinking) {
              currentBotMsg.isTyping = true;
            }

            // 修改：如果没有其他步骤，直接标记为完成
            if (currentBotMsg.steps.length === 0) {
              currentBotMsg.stepsFinished = true;
              currentBotMsg.isThinking = false;
            }
          }

          // 处理data: 场景 需要换行
          if (currentBotMsg.stepsFinished && isNotHasLine) {
            const handleLine = removeDataPrefix(line);
            // 是否在收集思考
            if (isCollectingThink.value) {
              console.log("思考中出现换行场景...")
              thinkBuffer.value += "\n\n" + handleLine;
            }else {
              currentBotMsg.content += "\n\n" + handleLine;
            }
            currentBotMsg.isTyping = true;
          }

          // 累积内容而不是立即更新
          accumulatedContent += line + '\n'
        })

        messages.value.at(-1).fullContent += newText;

        // 只在内容变化较大时才触发滚动
        if (accumulatedContent.length > 100) {
          scrollToBottom()
        }
      },
  ).then(() => {
    messages.value.at(-1).isTyping = false;
    isSending.value = false;
    saveChatHistory();
  })
  .catch((error) => {
    if (error.code === 'ECONNABORTED') {
      messages.value.at(-1).content = '请求超时，请尝试重新发送';
    }
    console.error('流式错误:', error);
    messages.value.at(-1).isTyping = false;
    messages.value.at(-1).isThinking = false;
    isSending.value = false;
  });

}

// 更新步骤状态
const updateStep = (message, stepType, content, status) => {
  const step = message.steps.find(s => s.type === stepType);
  if (step) {
    console.log("updateStep => content:", content)
    // 处理matchKeywords格式
    if (content.includes('matchKeyAndUrls=>')) {
      try {
        const jsonStr = content.replace('matchKeyAndUrls=>', '').trim();
        const matchData = JSON.parse(jsonStr);
        // 示范：{"keyword":"生命周期","urls":[{"title":"xxx","description":"xxx","url":"xxx"},{"title":"xxx","description":"xxx","url":"xxx"}]}
        step.keywords.push({
          key: matchData.keyword,
          urls: matchData.urls,
          clickable: true // 标记为可点击
        })
        console.log("type:", stepType, ", keywords:", step.keywords)
      } catch (e) {
        console.error('解析matchKeywords失败:', e);
      }
    } else {
      step.content = content;
    }
  }
}

function removeDataPrefix(str) {
  if (str.startsWith("data:")) {
    return str.slice(5); // 从第6个字符开始截取字符串（索引为5）
  }
  return str; // 如果不以"data:"开头，直接返回原字符串
}

// 打字完成回调
const onMessageTypingComplete = (index) => {
  messages.value[index].isTyping = false
}

// 修改后的initUUID函数
const initUUID = () => {
  // 生成新的UUID
  uuid.value = uuidToNumber(uuidv4())
  localStorage.setItem('user_uuid', uuid.value)

  // 清空当前会话消息
  messages.value = []
}

const uuidToNumber = (uuid) => {
  let number = 0
  for (let i = 0; i < uuid.length && i < 6; i++) {
    const hexValue = uuid[i]
    number = number * 16 + (parseInt(hexValue, 16) || 0)
  }
  return number % 1000000
}

// 修改后的newChat函数
const newChat = () => {
  // 生成新的UUID并初始化
  initUUID()
  
  // 清空消息并显示欢迎界面
  messages.value = []
  showWelcome.value = true
  // 深入思考回退
  isThinkingMode.value = false
  console.log('newChat think： ', isThinkingMode)
  
  // 更新会话历史
  saveChatHistory()
}

// 会话历史管理
const loadChatHistory = () => {
  const history = localStorage.getItem('chat_history')
  if (history) {
    chatHistory.value = JSON.parse(history)
  }
}

const saveChatHistory = () => {
  if (messages.value.length === 0) return
  
  // 获取当前会话ID
  const currentChatId = uuid.value
  
  // 从消息中提取标题（前30个字符）
  const title = messages.value[0]?.content?.substring(0, 30) || '新会话'
  
  // 检查是否已存在该会话
  const existingIndex = chatHistory.value.findIndex(item => item.id === currentChatId)
  
  if (existingIndex !== -1) {
    // 更新现有会话
    chatHistory.value[existingIndex] = {
      id: currentChatId,
      title,
      lastUpdated: new Date().toISOString(),
      messages: messages.value
    }
  } else {
    // 添加新会话
    chatHistory.value.unshift({
      id: currentChatId,
      title,
      lastUpdated: new Date().toISOString(),
      messages: messages.value
    })
  }
  
  // 限制历史记录数量
  if (chatHistory.value.length > 20) {
    chatHistory.value = chatHistory.value.slice(0, 20)
  }
  
  // 保存到本地存储
  localStorage.setItem('chat_history', JSON.stringify(chatHistory.value))
}

// 修改loadChat函数
const loadChat = (chatId) => {
  const chat = chatHistory.value.find(item => item.id === chatId)
  if (chat) {
    // 为历史消息添加isHistory标志
    messages.value = chat.messages.map(msg => ({
      ...msg,
      isHistory: true
    }))
    uuid.value = chatId
    scrollToBottom()
    // 加载历史聊天时隐藏欢迎界面
    showWelcome.value = false
  }
}


// 键盘回车事件
const handleKeyCode = (event) => {
  console.log("event=>", event)
  if (event.keyCode == 13) {
    if (!event.metaKey) {
      event.preventDefault();
      sendMessage();
    } else {
      this.messageTxt = this.messageTxt + '\n';
    }
  }
}

// 关键字点击
const handleKeywordClick = (urls) => {
  console.log('点击关键词时的 urls:', urls);
  if (urls && urls.length > 0) {
    const validUrls = urls.filter(url => url.title && url.description && url.url).map(url => ({
      ...url,
      date: url.date // Ensure date is passed through
    }));
    if (validUrls.length > 0) {
      currentUrls.value = validUrls;
      drawerVisible.value = true;
    } else {
      console.error('传递的 urls 数组中没有有效的链接数据');
    }
  }
}


</script>

<style scoped>
/* 全局样式 */
* {
  box-sizing: border-box;
}

body {
  font-family: -apple-system, BlinkMacSystemFont, "Segoe UI", Roboto, "Helvetica Neue", Arial, sans-serif;
}

.app-layout {
  display: flex;
  height: 100vh;
  overflow: hidden;
}

/* 侧边栏样式 */
.sidebar {
  width: 300px;
  background-color: #f8f9fa;
  border-right: 1px solid #e9ecef;
  display: flex;
  flex-direction: column;
  overflow: hidden;
}

.welcome-container {
  display: flex;
  justify-content: center;
  align-items: center;
  width: 100%;
  min-height: 15vh;
  margin-top: -180px;
}

.welcome-content {
  display: flex;
  flex-direction: column;
  align-items: center;
  max-width: 800px;
  text-align: center;
  padding: 20px;
}

.welcome-header {
  display: flex;
  align-items: center;
  margin-bottom: 16px;
}

.welcome-logo {
  width: 60px;
  height: 60px;
  border-radius: 50%;
  object-fit: cover;
  margin-right: 20px;
}

.welcome-title {
  font-size: 28px;
  font-weight: 600;
  color: #333;
  line-height: 1.4;
  margin: 0;
}

.welcome-description {
  font-size: 16px;
  color: #666;
  line-height: 1.6;
  max-width: 600px;
  margin-top: 8px;
}

.logo-section {
  display: flex;
  align-items: center;
  padding: 20px;
  border-bottom: 1px solid #e9ecef;
}

.logo-section img {
  width: 40px;
  height: 40px;
  margin-right: 10px;
}

.logo-text {
  font-size: 18px;
  font-weight: 600;
  color: #333;
}

.new-chat-button {
  margin: 15px;
}

.chat-history {
  flex: 1;
  padding: 10px;
  overflow: hidden;
}

.chat-history h3 {
  font-size: 14px;
  color: #6c757d;
  margin: 10px 0 5px 5px;
}

.history-item {
  display: flex;
  align-items: center;
  padding: 8px 10px;
  border-radius: 4px;
  cursor: pointer;
  transition: background-color 0.2s;
  font-size: 14px;
  color: #333;
}

.history-item:hover {
  background-color: #e9ecef;
}

.history-item i {
  margin-right: 10px;
  color: #6c757d;
}

/* 主内容区样式 */
.main-content {
  flex: 1;
  display: flex;
  flex-direction: column;
  background-color: #ffffff;
  position: relative; /* 新增：为主内容区添加相对定位 */
}

.chat-container {
  display: flex;
  flex-direction: column;
  height: 100%;
  padding: 0px 30px;
}

/* 修改后的底部提示样式 - 现在位于右侧内容区底部 */
.footer-notice {
  position: absolute;
  bottom: 10px;
  left: 0;
  width: 100%;
  text-align: center;
  color: #888;
  font-size: 14px;
  padding: 8px 0;
  z-index: 100;
  background-color: rgba(255, 255, 255, 0.8);
}

/* 新增：包装消息列表和输入框的容器 */
.message-input-wrapper {
  display: flex;
  flex-direction: column;
  align-items: center;
  width: 100%;
  min-height: 100%;
}

.message-input-wrapper:not(.input-at-bottom) {
  justify-content: center;
}

.message-input-wrapper.input-at-bottom {
  justify-content: space-between;
}

.message-list {
  flex: 1;
  overflow-y: auto;
  padding: 10px;
  margin-bottom: 15px;
  border-radius: 8px;
  background-color: #ffffff;
  /* 使消息列表宽度与输入框一致 */
  width: 65%;
}

.message {
  display: flex;
  margin-bottom: 15px;
  margin-top: 30px
}

.message-avatar {
  width: 36px;
  height: 36px;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 18px;
  flex-shrink: 0;
  overflow: hidden; /* 确保图片不会超出容器 */
}

.bot-logo {
  width: 100%;
  height: 100%;
  object-fit: cover; /* 使图片适应容器 */
}

.user-message {
  align-self: flex-end;
  flex-direction: row-reverse;
}

.user-message .message-avatar {
  background-color: #007bff;
  color: white;
  margin-left: 10px;
}

.bot-message {
  align-self: flex-start;
}

.bot-message .message-avatar {
  margin-right: 10px;
}

.message-user-content {
  background-color: #f0f6fe;
  padding: 16px 16px;
  border-radius: 8px;
  box-shadow: 0 1px 3px rgba(0,0,0,0.1);
  line-height: 1.6;
  max-width: 100%;
}

.step-container {
  border-radius: 12px;
  line-height: 1.6;
  border: .5px solid rgba(0, 0, 0, .13);
  max-width: 100%;
  padding: 16px;
  /* 新增固定宽度和溢出处理 */
  width: 600px; /* 或设置具体像素值如 width: 500px; */
  max-width: 100%;
  overflow-x: auto; /* 水平溢出时显示滚动条 */
  word-break: break-word; /* 允许单词内换行 */
}

.message-content {
  background-color: white;
  padding: 0px 16px 16px 16px;
  border-radius: 8px;
  line-height: 1.6;
  max-width: 100%;
  width: 600px;
}

.user-message .message-content {
  background-color: #e7f5ff;
}

.loading-dots {
  display: flex;
  margin-top: 18px;
}

.dot {
  width: 6px;
  height: 6px;
  background-color: #6c757d;
  border-radius: 50%;
  margin-right: 4px;
  animation: pulse 1.2s infinite ease-in-out both;
}

.dot:nth-child(2) {
  animation-delay: -0.4s;
}

.dot:nth-child(3) {
  animation-delay: -0.2s;
}

@keyframes pulse {
  0%, 80%, 100% { transform: scale(0.6); opacity: 0.4; }
  40% { transform: scale(1); opacity: 1; }
}


.inputBox {
  display: flex;
  align-items: center;
  flex-direction: column;
  width: 100%;
  margin-bottom: 50px; /* 为底部提示留出空间 */
}

/* 输入区域样式 */
.input-area {
  display: flex;
  flex-direction: column;
  border-radius: 8px;
  background-color: rgb(243, 244, 246);
  box-shadow: 0 1px 3px rgba(0,0,0,0.1);
  padding: 15px 10px 15px 10px;
  width: 65%;
  border-radius: 24px;
  margin-bottom: 15px;
}

.send-area {
  display: flex;
  justify-content: flex-end; /* 改为右对齐 */
  align-items: center;
  margin-top: 10px;
  padding: 0 10px;
}


.thinking-switch {
  margin: 0; /* 移除默认margin */
}

.send-btn {
  margin: 0;
  padding: 0;
  width: 40px;
  height: 40px;
}

/* 调整图标大小和位置 */
.send-btn i {
  font-size: 18px;
}

/* 关键词标签样式 */
.step-content {
  padding: 5px 0;
}

.step-description {
  margin-top: 10px;
  margin-bottom: 8px;
  color: #555;
}

.keyword-tags {
  margin-top: 8px;
}

.tag-label {
  font-size: 12px;
  color: #6c757d;
  margin-right: 5px;
}

.tags-container {
  display: flex;
  flex-wrap: wrap;
  gap: 4px;
  margin-top: 4px;
}

.keyword-tag {
  padding: 3px 8px;
  background-color: #f0f6fe;
  color: #007bffcc;
  border-radius: 4px;
  font-size: 12px;
  white-space: nowrap;
}

.markdown-renderer{
  margin-top: 10px;
}

/* 容器：让图标和文字水平对齐 */
.search-thinking-container {
  display: flex;
  align-items: center; /* 垂直居中 */
  gap: 10px; /* 图标和文字之间的间距 */
  margin-bottom: 10px;
}

/* search图标 缩小放大效果 */
.auto-pulse-img-keyframes {
  width: 25px;
  height: 25px;
  margin-bottom: 10px;
  animation: fast-search-pulse 1.2s infinite;
}
.auto-pulse-img {
  width: 25px;
  height: 25px;
  margin-bottom: 10px;
}

/* 文字动画同步为0.6秒周期 */
.thinking-text-keyframes {
  font-size: 16px;
  font-weight: 500;
  color: #333;
  animation: text-blink 1.2s infinite ease-in-out;
}
.thinking-text {
  font-size: 16px;
  font-weight: 500;
  color: #333;
}

.el-switch {
  margin-left: 10px;
}

.el-switch__label {
  color: #606266;
  font-size: 14px;
}

.el-switch__label.is-active {
  color: #13ce66;
}

/* 确保引用块样式清晰 */
.markdown-renderer blockquote {
  border-left: 3px solid #d1d5db;
  padding: 0.5rem 1rem;
  margin: 0.75rem 0;
  background-color: #f9fafb;
  color: #4b5563;
}

/* 空行保持最小高度 */
.markdown-renderer blockquote p:empty::after {
  content: " ";
  display: inline-block;
}

.keyword-tag {
  padding: 3px 8px;
  background-color: #f0f6fe;
  color: #007bffcc;
  border-radius: 4px;
  font-size: 12px;
  white-space: nowrap;
  transition: all 0.2s;
}

.keyword-tag:hover {
  background-color: #e0ecff;
  color: #007bff;
}


/* 新增：用户消息内容样式，确保换行正常 */
.user-message-content {
  background-color: #f0f6fe;
  padding: 16px 16px;
  border-radius: 8px;
  box-shadow: 0 1px 3px rgba(0,0,0,0.1);
  line-height: 1.6;
  max-width: 100%;
  word-break: break-word; /* 强制换行 */
}

::v-deep .custom-no-border .el-textarea__inner {
  /* 移除边框 */
  border: none;
  box-shadow: none;
  background-color: rgb(243, 244, 246);
  /* 移除调整大小控制柄 */
  resize: none;
  max-height: 450px;
  word-break: break-all; /* 允许单词内换行 */
}

@keyframes text-blink {
  0%, 100% { opacity: 0.7; }
  50% { opacity: 1; }
}

@keyframes fast-search-pulse {
  0%, 100% {
    transform: scale(1);
  }
  20% {
    transform: scale(1.3);
  }
  40% {
    transform: scale(0.9);
  }
  60% {
    transform: scale(1.2);
  }
  80% {
    transform: scale(1);
  }
}

/* 响应式设计 - 优化版 */
@media (max-width: 1200px) {
  .message-list, .input-area {
    width: 75%;
  }
  .step-container {
    width: 100%;
  }
}

@media (max-width: 992px) {
  .message-list, .input-area {
    width: 85%;
  }
  .step-container {
    width: 100%;
  }
}

@media (max-width: 768px) {
  .welcome-header {
    flex-direction: column;
    text-align: center;
  }
  
  .welcome-logo {
    margin-right: 0;
    margin-bottom: 15px;
  }
  
  .welcome-title {
    font-size: 22px;
  }
  
  .welcome-description {
    font-size: 14px;
  }

  .app-layout {
    flex-direction: column;
  }
  
  .sidebar {
    width: 100%;
    height: auto;
    flex-direction: row;
    flex-wrap: wrap;
    padding: 10px;
  }
  
  .logo-section {
    flex: 1;
    padding: 0;
    border-bottom: none;
  }
  
  .new-chat-button {
    width: auto;
    margin: 0 10px;
  }
  
  .chat-history {
    display: none;
  }
  
  .main-content {
    padding: 0;
  }
  
  .chat-container {
    padding: 10px;
  }
  
  .message-list, .input-area {
    width: 95%;
  }

  .send-btn {
    width: 36px;
    height: 36px;
  }

  .send-btn i {
    font-size: 16px;
  }
  
  .message {
    max-width: 95%;
  }
  
  .ai-response {
    margin-left: 0;
  }

  .footer-notice {
    position: fixed;
    bottom: 5px;
    left: 0;
    width: 100%;
    font-size: 12px;
    background-color: rgba(255, 255, 255, 0.95);
  }
  
  .inputBox {
    margin-bottom: 30px;
  }

  .step-container, .message-content {
    width: 100%; /* 小屏幕下占满宽度 */
  }
}

/* 超小屏幕优化 */
@media (max-width: 576px) {
  .welcome-logo {
    width: 50px;
    height: 50px;
  }
  
  .welcome-title {
    font-size: 20px;
  }
  
  .welcome-description {
    font-size: 13px;
  }

  .message-list, .input-area {
    width: 100%;
  }
  
  .step-container, .message-content, .message-user-content {
    padding: 10px;
  }
  
  .markdown-renderer {
    font-size: 14px;
  }
  
  .keyword-tag {
    padding: 2px 6px;
    font-size: 11px;
  }

  .footer-notice {
    font-size: 12px;
    bottom: 5px;
  }
  
  .inputBox {
    margin-bottom: 30px;
  }

  .step-container {
    width: 100%;
  }
}
</style>    