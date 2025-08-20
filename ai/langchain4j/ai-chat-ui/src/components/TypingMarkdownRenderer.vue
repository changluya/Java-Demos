<template>
  <div>
    <markdown-renderer
        :content="displayContent"
        :is-typing="isTyping"
    />
  </div>
</template>

<script>
import MarkdownRenderer from '@/components/MarkdownRenderer.vue'

export default {
  components: { MarkdownRenderer },
  props: {
    content: {
      type: String,
      default: ''
    },
    typingSpeed: {
      type: Number,
      default: 20 // 每多少毫秒显示一个字符
    }
  },
  data() {
    return {
      displayContent: '',
      isTyping: false,
      typingInterval: null,
      lastContent: '',
      currentIndex: 0
    }
  },
  watch: {
    content: {
      handler(newContent) {
        this.handleContentChange(newContent)
      },
      deep: true
    }
  },
  mounted() {
    if (this.content) {
      this.startTyping(this.content)
    }
  },
  beforeDestroy() {
    this.stopTyping()
  },
  methods: {
    sanitizeMarkdown(content) {
      // 修复常见的Markdown格式问题
      return content
        .replace(/([^`])````/g, '$1```') // 修复多余的反引号
        .replace(/^#+\s+/gm, '\n$&') // 确保标题前有换行
        .replace(/(\n```)([^\n])/g, '$1\n$2'); // 确保代码块后有换行
    },
    handleContentChange(newContent) {
      newContent = this.sanitizeMarkdown(newContent);
      // 如果内容相同，不做处理
      if (newContent === this.lastContent) return

      // 如果正在打字，停止当前打字效果
      if (this.isTyping) {
        this.stopTyping()
      }

      // 检查新内容是否是在旧内容基础上增加的
      if (newContent.startsWith(this.lastContent)) {
        // 流式更新：继续在已有内容后打字
        this.currentIndex = this.lastContent.length
        this.startTyping(newContent, true)
      } else {
        // 全新内容：从头开始打字
        this.currentIndex = 0
        this.startTyping(newContent)
      }
    },
    startTyping(content, isContinued = false) {
      this.lastContent = content
      this.isTyping = true

      // 如果是继续打字，保留当前显示内容
      if (!isContinued) {
        this.displayContent = ''
        this.currentIndex = 0
      }

      // 立即显示第一个字符，然后设置定时器
      if (this.currentIndex === 0 && content.length > 0) {
        this.displayContent = content[0]
        this.currentIndex = 1
      }

      this.typingInterval = setInterval(() => {
        if (this.currentIndex < content.length) {
          this.displayContent = content.substring(0, this.currentIndex + 1)
          this.currentIndex++
        } else {
          this.stopTyping()
        }
      }, this.typingSpeed)
    },
    stopTyping() {
      clearInterval(this.typingInterval)
      this.isTyping = false
      this.$emit('typing-complete')
    }
  }
}
</script>

<style scoped>
/* 可以添加一些组件特定的样式 */
</style>
