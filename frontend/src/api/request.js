/**
 * Axios HTTP client configuration
 *
 * @description Provides a configured Axios instance with interceptors for
 *              request/response handling, authentication, and error management.
 *
 * @author MusicShare Team
 * @version 1.0.0
 */

import axios from 'axios'
import { ElMessage } from 'element-plus'

// Create axios instance
const request = axios.create({
  baseURL: import.meta.env.VITE_API_BASE_URL || '/api',
  timeout: 15000,
  headers: {
    'Content-Type': 'application/json;charset=UTF-8'
  }
})

// Request interceptor
request.interceptors.request.use(
  (config) => {
    // Add authentication token if exists
    const token = localStorage.getItem('token')
    if (token) {
      config.headers.Authorization = `Bearer ${token}`
    }

    // Log request in development
    if (import.meta.env.DEV) {
      console.log('[Request]', config.method.toUpperCase(), config.url, config.data || config.params)
    }

    return config
  },
  (error) => {
    console.error('[Request Error]', error)
    return Promise.reject(error)
  }
)

// Response interceptor
request.interceptors.response.use(
  (response) => {
    const { data } = response

    // Log response in development
    if (import.meta.env.DEV) {
      console.log('[Response]', response.config.url, data)
    }

    // Handle custom response format: { code, message, data }
    if (data.code !== undefined) {
      if (data.code === 200 || data.code === 0) {
        return data.data
      } else {
        // Business error
        ElMessage.error(data.message || 'Request failed')
        return Promise.reject(new Error(data.message || 'Error'))
      }
    }

    // Return raw data if no custom format
    return data
  },
  (error) => {
    console.error('[Response Error]', error)

    // Handle HTTP errors
    if (error.response) {
      const { status, data } = error.response

      switch (status) {
        case 401:
          // Unauthorized - clear token and redirect to login
          localStorage.removeItem('token')
          localStorage.removeItem('userInfo')
          ElMessage.error('Please login first')
          window.location.href = '/login'
          break

        case 403:
          ElMessage.error('Access denied')
          break

        case 404:
          ElMessage.error('Resource not found')
          break

        case 500:
          ElMessage.error(data.message || 'Server error')
          break

        default:
          ElMessage.error(data.message || `Error: ${status}`)
      }
    } else if (error.request) {
      // Request made but no response received
      ElMessage.error('Network error, please check your connection')
    } else {
      // Error in request configuration
      ElMessage.error(error.message || 'Request failed')
    }

    return Promise.reject(error)
  }
)

export default request
