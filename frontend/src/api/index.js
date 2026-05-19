import axios from 'axios'
import { ElMessage } from 'element-plus'
import router from '@/router'

const request = axios.create({
  baseURL: '/api',
  timeout: 10000
})

request.interceptors.request.use(
  (config) => {
    const token = localStorage.getItem('satoken')
    if (token) {
      config.headers['satoken'] = token
    }
    return config
  },
  (error) => {
    return Promise.reject(error)
  }
)

request.interceptors.response.use(
  (response) => {
    const res = response.data
    if (res.code === 200) {
      return res.data
    } else if (res.code === 401) {
      localStorage.removeItem('satoken')
      localStorage.removeItem('userInfo')
      ElMessage.error('登录已过期，请重新登录')
      router.push('/login')
      return Promise.reject(new Error(res.message))
    } else {
      ElMessage.error(res.message || '请求失败')
      return Promise.reject(new Error(res.message))
    }
  },
  (error) => {
    ElMessage.error(error.message || '网络错误')
    return Promise.reject(error)
  }
)

export const login = (data) => {
  return request.post('/user/login', data)
}

export const logout = () => {
  return request.post('/user/logout')
}

export const getUserInfo = () => {
  return request.get('/user/info')
}

export const getRoomList = () => {
  return request.get('/room/list')
}

export const getTodayBookings = () => {
  return request.get('/booking/today')
}

export const getTodayBookingsByRoom = (roomId) => {
  return request.get(`/booking/today/room/${roomId}`)
}

export const createBooking = (data) => {
  return request.post('/booking', data)
}

export const cancelBooking = (id) => {
  return request.post(`/booking/cancel/${id}`)
}

export const getMyBookings = () => {
  return request.get('/booking/my')
}

export const getPendingBookings = () => {
  return request.get('/booking/pending')
}

export const approveBooking = (data) => {
  return request.post('/approval', data)
}

export default request
