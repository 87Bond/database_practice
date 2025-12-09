import axios from 'axios'

// 使用环境变量，方便开发/生产切换：
// - 开发（npm run serve）默认走 devServer 代理：/api
// - 生产（npm run build）可在 .env.production 设置 VUE_APP_API_BASE=http://localhost:8080/api
const api = axios.create({
  baseURL: '/api'
})

api.interceptors.request.use((config) => {
  const token = localStorage.getItem('token')
  if (token) {
    config.headers['X-User-Id'] = token
  }
  return config
})

export default api
