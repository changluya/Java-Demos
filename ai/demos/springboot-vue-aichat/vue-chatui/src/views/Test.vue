<template>
  <el-container class="ai-chat-container">
    <el-main>
      <el-card class="response-card">
        <markdown-renderer 
          :content="responseContent" 
          :is-typing="isTyping" 
        />
      </el-card>
    </el-main>
  </el-container>
</template>

<script>
import MarkdownRenderer from '@/components/MarkdownRenderer.vue'

export default {
  components: { MarkdownRenderer },
  data() {
    return {
      responseContent: '',
      isTyping: true,
      fullResponse: '```markdown#背景游记：探索云南的神秘之旅```markdown#背景游记：探索云南的神秘之旅识云南旅行始于对云南的好奇。作为中国西南的边陲省份，云南以其多样的民族```markdown#背景游记：探索云南的神秘之旅识云南旅行始于对云南的好奇。作为中国西南的边陲省份，云南以其多样的民族文化和壮丽的自然风光闻名。## 穿越古城首先抵达丽江古城，青石板markdown#背景游记：探索云南的神秘之旅识云南旅行始于对云南的好奇。作为中国西南的边陲省份，云南以其多样的民族文化和壮丽的自然风光闻名。## 穿越古城首先抵达丽江古城，青石板路和纳西族建筑让人仿佛穿越回古代。## 高原之行前往香格里云南的神秘之旅识云南旅行始于对云南的好奇。作为中国西南的边陲省份，云南以其多样的民族文化和壮丽的自然风光闻名。## 穿越古城首先抵达丽江古城，青石板路和纳西族建筑让人仿佛穿越回古代。## 高原之行前往香格里拉，高原的清新空气和藏式寺庙令人心旷神怡。## 民族风情的好奇。作为中国西南的边陲省份，云南以其多样的民族文化和壮丽的自然风光闻名。## 穿越古城首先抵达丽江古城，青石板路和纳西族建筑让人仿佛穿越回古代。## 高原之行前往香格里拉，高原的清新空气和藏式寺庙令人心旷神怡。## 民族风情大理，白族的三道茶和扎染工艺展现了独特的民族文化。## 回程感悟自然风光闻名。## 穿越古城首先抵达丽江古城，青石板路和纳西族建筑让人仿佛穿越回古代。## 高原之行前往香格里拉，高原的清新空气和藏式寺庙令人心旷神怡。## 民族风情大理，白族的三道茶和扎染工艺展现了独特的民族文化。## 回程感悟短，却让我深刻感受到云南的多样与美丽，留下了难忘的回忆。',
//       fullResponse: `# AI 回答示例

// ## 代码示例
// \`\`\`javascript
// // Vue 组件示例
// export default {
//   data() {
//     return {
//       message: 'Hello AI!'
//     }
//   },
//   mounted() {
//     console.log(this.message)
//   }
// }
// \`\`\`

// ## 功能列表
// - ✅ Markdown 渲染
// - ✨ 代码高亮
// - 💬 模拟 AI 对话效果

// > 提示：这是模拟的 AI 回答内容
// `
    }
  },
  mounted() {
    this.simulateTyping()
  },
  methods: {
    cleanStreamedMarkdown(streamedText) {
      if (!streamedText) return ''
      
      // Step 1: Remove duplicate content by keeping only the last occurrence
      let cleanText = streamedText
      
      // Step 2: Remove any markdown code block markers that might be malformed
      cleanText = cleanText.replace(/```markdown/g, '').replace(/```/g, '')
      
      // Step 3: Fix headers that might be broken across streams
      cleanText = cleanText.replace(/([^#\n]|^)(#+)([^#\s\n])/g, '$1$2 $3')
      cleanText = cleanText.replace(/(#+)\s+/g, '$1 ')
      
      // Step 4: Remove duplicate sections by finding the last complete version
      const sections = {}
      const headerRegex = /(^|\n)(#+ .+?)(?=\n#|$)/gs
      let match
      
      while ((match = headerRegex.exec(cleanText))) {
        const header = match[2]
        const contentStart = match.index + match[1].length
        const nextMatch = headerRegex.exec(cleanText)
        const contentEnd = nextMatch ? nextMatch.index : cleanText.length
        const content = cleanText.slice(contentStart + header.length, contentEnd).trim()
        
        // Keep the last version of each section
        sections[header] = content
      }
      
      // Step 5: Rebuild the markdown from the cleaned sections
      let result = ''
      for (const [header, content] of Object.entries(sections)) {
        result += `${header}\n\n${content}\n\n`
      }
      
      // Step 6: Remove any remaining duplicate lines
      const lines = result.split('\n')
      const uniqueLines = []
      const seenLines = new Set()
      
      for (const line of lines) {
        const trimmed = line.trim()
        if (!seenLines.has(trimmed)) {
          seenLines.add(trimmed)
          uniqueLines.push(line)
        }
      }
      
      return uniqueLines.join('\n').trim()
    },
    simulateTyping() {
    //   console.log("clean =>", this.cleanStreamedMarkdown(this.fullResponse))
      let index = 0
      const typingInterval = setInterval(() => {
        if (index <= this.fullResponse.length) {
          this.responseContent = this.fullResponse.substring(0, index)
          index++
        } else {
          clearInterval(typingInterval)
          this.isTyping = false
        }
      }, 20)
    }
  }
}
</script>

<style scoped>
.ai-chat-container {
  height: 100vh;
  background-color: #f0f2f5;
  padding: 20px;
}

.response-card {
  border-radius: 12px;
  transition: all 0.3s;
}

.response-card:hover {
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.15);
}
</style>