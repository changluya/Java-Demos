import axios from 'axios'

const service = axios.create({
    baseURL: import.meta.env.VITE_API_URL,
    timeout: 30000 // 默认超时改为30秒
})

// 流式请求专用实例（无响应拦截器）
const streamService = axios.create({
    baseURL: import.meta.env.VITE_API_URL,
    timeout: 300000
})


// 请求拦截器
service.interceptors.request.use(config => {
    // 为流式请求单独设置超时
    if (config.responseType === 'stream') {
        config.timeout = 30000 // 流式请求30秒超时
    }
    return config
}, error => {
    return Promise.reject(error)
})

// response 拦截器
service.interceptors.response.use(
  response => {
    // 在这里处理返回数据const { data } = response
    if (response.data.code !== 200) {
      console.error('Error:', data.message)
        console.error("data=>", response.data)
      return Promise.reject(newError(data.message || 'Error'))
    } else {
      return response.data
    }
  },
  error => {
    console.log(error)
    return Promise.reject(error)
  }
)
export { service as default, streamService }