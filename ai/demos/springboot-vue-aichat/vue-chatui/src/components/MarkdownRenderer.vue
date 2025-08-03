<template>
  <div class="markdown-body">
    <div v-html="compiledMarkdown" />
    <span v-if="isTyping" class="typing-cursor"></span>
  </div>
</template>

<script>
import MarkdownIt from 'markdown-it'
import hljs from 'highlight.js'
import 'github-markdown-css/github-markdown.css'
import 'highlight.js/styles/github.css'

export default {
  props: {
    content: {
      type: String,
      required: true
    },
    isTyping: {
      type: Boolean,
      default: false
    }
  },
  data() {
    return {
      md: new MarkdownIt({
        html: true,
        linkify: true,
        typographer: true,
        highlight: (str, lang) => {
          if (lang && hljs.getLanguage(lang)) {
            try {
              return `<pre class="hljs"><code>${hljs.highlight(str, { 
                language: lang, 
                ignoreIllegals: true 
              }).value}</code></pre>`
            } catch (__) {}
          }
          return `<pre class="hljs"><code>${this.md.utils.escapeHtml(str)}</code></pre>`
        }
      })
    }
  },
  computed: {
    compiledMarkdown() {
      return this.md.render(this.content)
    }
  }
}
</script>

<style>
.markdown-body {
  box-sizing: border-box;
  min-width: 200px;
  max-width: 100%;
  /* padding: 20px; */
  background-color: #ffffff;
  border-radius: 8px;
  /* box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1); */
}

.hljs {
  padding: 1em;
  border-radius: 6px;
  font-size: 14px;
}

@keyframes blink {
  50% { opacity: 0; }
}

.typing-cursor {
  display: inline-block;
  width: 8px;
  height: 1.2em;
  background: #333;
  margin-left: 2px;
  animation: blink 1s step-end infinite;
  vertical-align: middle;
}
</style>