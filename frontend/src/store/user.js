/**
 * User store module
 *
 * @description Manages user authentication state, profile data, and
 *              related operations using Pinia.
 *
 * @author MusicShare Team
 * @version 1.0.0
 */

import { defineStore } from 'pinia'
import { ref, computed } from 'vue'
import { login as loginApi, logout as logoutApi, getUserProfile } from '@/api/user'
import { ElMessage } from 'element-plus'

export const useUserStore = defineStore('user', () => {
  // State
  const token = ref(localStorage.getItem('token') || '')
  const userInfo = ref(JSON.parse(localStorage.getItem('userInfo') || 'null'))

  // Getters
  const isLoggedIn = computed(() => !!token.value)
  const userId = computed(() => userInfo.value?.id)
  const username = computed(() => userInfo.value?.username)
  const avatar = computed(() => userInfo.value?.avatar)
  const role = computed(() => userInfo.value?.role)
  const isMusician = computed(() => role.value === 'MUSICIAN' || role.value === 'ADMIN')
  const isAdmin = computed(() => role.value === 'ADMIN')

  /**
   * User login action
   *
   * @param {Object} credentials - Login credentials
   * @param {string} credentials.username - Username or email
   * @param {string} credentials.password - Password
   * @returns {Promise<boolean>} Login success status
   */
  async function login(credentials) {
    try {
      const response = await loginApi(credentials)

      // Store token and user info
      token.value = response.token
      userInfo.value = response.userInfo

      localStorage.setItem('token', response.token)
      localStorage.setItem('userInfo', JSON.stringify(response.userInfo))

      ElMessage.success('Login successful')
      return true
    } catch (error) {
      console.error('Login failed:', error)
      return false
    }
  }

  /**
   * User logout action
   *
   * @returns {Promise<void>}
   */
  async function logout() {
    try {
      await logoutApi()
    } catch (error) {
      console.error('Logout API error:', error)
    } finally {
      // Clear local data
      token.value = ''
      userInfo.value = null
      localStorage.removeItem('token')
      localStorage.removeItem('userInfo')

      ElMessage.success('Logout successful')
    }
  }

  /**
   * Fetch current user profile
   *
   * @returns {Promise<void>}
   */
  async function fetchUserProfile() {
    try {
      const data = await getUserProfile()
      userInfo.value = data
      localStorage.setItem('userInfo', JSON.stringify(data))
    } catch (error) {
      console.error('Failed to fetch user profile:', error)
    }
  }

  /**
   * Update local user info
   *
   * @param {Object} newInfo - New user information
   */
  function updateUserInfo(newInfo) {
    userInfo.value = { ...userInfo.value, ...newInfo }
    localStorage.setItem('userInfo', JSON.stringify(userInfo.value))
  }

  return {
    // State
    token,
    userInfo,

    // Getters
    isLoggedIn,
    userId,
    username,
    avatar,
    role,
    isMusician,
    isAdmin,

    // Actions
    login,
    logout,
    fetchUserProfile,
    updateUserInfo
  }
})
