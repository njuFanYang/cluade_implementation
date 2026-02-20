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
import {
  register as registerApi,
  login as loginApi,
  logout as logoutApi,
  getUserProfile,
  updateProfile as updateProfileApi,
  changePassword as changePasswordApi
} from '@/api/user'
import { ElMessage } from 'element-plus'

export const useUserStore = defineStore('user', () => {
  // State
  const token = ref(localStorage.getItem('token') || '')
  const userInfo = ref(JSON.parse(localStorage.getItem('userInfo') || 'null'))

  // Getters
  const isLoggedIn = computed(() => !!token.value)
  const userId = computed(() => userInfo.value?.id)
  const username = computed(() => userInfo.value?.username)
  const email = computed(() => userInfo.value?.email)
  const avatar = computed(() => userInfo.value?.avatar)
  const nickname = computed(() => userInfo.value?.nickname || userInfo.value?.username)
  const bio = computed(() => userInfo.value?.bio)
  const roles = computed(() => userInfo.value?.roles || [])
  const isMusician = computed(() => roles.value.includes('ROLE_MUSICIAN') || roles.value.includes('ROLE_ADMIN'))
  const isAdmin = computed(() => roles.value.includes('ROLE_ADMIN'))
  const followersCount = computed(() => userInfo.value?.followersCount || 0)
  const followingCount = computed(() => userInfo.value?.followingCount || 0)
  const musicCount = computed(() => userInfo.value?.musicCount || 0)
  const playlistCount = computed(() => userInfo.value?.playlistCount || 0)

  /**
   * User registration action
   *
   * @param {Object} credentials - Registration credentials
   * @param {string} credentials.username - Username
   * @param {string} credentials.email - Email
   * @param {string} credentials.password - Password
   * @param {string} credentials.confirmPassword - Confirm password
   * @param {string} credentials.nickname - Nickname (optional)
   * @returns {Promise<boolean>} Registration success status
   */
  async function register(credentials) {
    try {
      const response = await registerApi(credentials)

      ElMessage.success('Registration successful! Please login.')
      return true
    } catch (error) {
      console.error('Registration failed:', error)
      ElMessage.error(error.message || 'Registration failed')
      return false
    }
  }

  /**
   * User login action
   *
   * @param {Object} credentials - Login credentials
   * @param {string} credentials.usernameOrEmail - Username or email
   * @param {string} credentials.password - Password
   * @param {boolean} credentials.rememberMe - Remember me flag
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

      return true
    } catch (error) {
      console.error('Login failed:', error)
      ElMessage.error(error.message || 'Login failed')
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
      throw error
    }
  }

  /**
   * Update user profile
   *
   * @param {Object} data - Profile data to update
   * @returns {Promise<boolean>} Update success status
   */
  async function updateProfile(data) {
    try {
      const response = await updateProfileApi(data)
      userInfo.value = response
      localStorage.setItem('userInfo', JSON.stringify(response))

      ElMessage.success('Profile updated successfully')
      return true
    } catch (error) {
      console.error('Failed to update profile:', error)
      ElMessage.error(error.message || 'Failed to update profile')
      return false
    }
  }

  /**
   * Change password
   *
   * @param {Object} data - Password change data
   * @param {string} data.oldPassword - Old password
   * @param {string} data.newPassword - New password
   * @param {string} data.confirmPassword - Confirm password
   * @returns {Promise<boolean>} Change success status
   */
  async function changePassword(data) {
    try {
      await changePasswordApi(data)

      ElMessage.success('Password changed successfully')
      return true
    } catch (error) {
      console.error('Failed to change password:', error)
      ElMessage.error(error.message || 'Failed to change password')
      return false
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
    email,
    avatar,
    nickname,
    bio,
    roles,
    isMusician,
    isAdmin,
    followersCount,
    followingCount,
    musicCount,
    playlistCount,

    // Actions
    register,
    login,
    logout,
    fetchUserProfile,
    updateProfile,
    changePassword,
    updateUserInfo
  }
})
