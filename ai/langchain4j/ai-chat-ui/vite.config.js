import { defineConfig } from 'vite'
import path from 'path'
import vue2 from '@vitejs/plugin-vue2'

// https://vitejs.dev/config/
export default defineConfig({
base: './', // 打包的静态资源引用路径
  plugins: [vue2()], // 放插件用的
  resolve: {
    alias: {
        // 设置路径
        '~': path.resolve(__dirname, './'),
        // 设置别名
        '@': path.resolve(__dirname, './src')
      },
      extensions: ['.js', '.jsx', '.ts', '.tsx', '.json', '.vue'],
  },
  // vite 相关配置
  // 配置服务端口地址
  server: {
    port: 81,
    host: true,
    open: true, 
  },
})