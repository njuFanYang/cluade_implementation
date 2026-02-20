/**
 * Admin store module
 *
 * @description Manages admin operations state including dashboard statistics,
 *              user management, and music approval workflow.
 *
 * @author MusicShare Team
 * @version 1.0.0
 */

import { defineStore } from 'pinia'
import { ref } from 'vue'
import {
  getDashboardStats as getDashboardStatsApi,
  getAllUsers as getAllUsersApi,
  getUserDetails as getUserDetailsApi,
  updateUserStatus as updateUserStatusApi,
  manageUserRole as manageUserRoleApi,
  deleteUser as deleteUserApi,
  getPendingMusic as getPendingMusicApi,
  getAllMusicAdmin as getAllMusicAdminApi,
  approveMusic as approveMusicApi,
  rejectMusic as rejectMusicApi
} from '@/api/admin'
import { ElMessage } from 'element-plus'

export const useAdminStore = defineStore('admin', () => {
  // State
  const dashboardStats = ref(null)
  const users = ref({ content: [], totalElements: 0 })
  const musicList = ref({ content: [], totalElements: 0 })
  const loading = ref(false)

  /**
   * Fetch dashboard statistics
   *
   * @returns {Promise<Object>} Dashboard statistics
   */
  async function fetchDashboardStats() {
    try {
      loading.value = true
      const response = await getDashboardStatsApi()
      dashboardStats.value = response.data
      return dashboardStats.value
    } catch (error) {
      console.error('Failed to fetch dashboard stats:', error)
      ElMessage.error(error.message || 'Failed to load dashboard statistics')
      throw error
    } finally {
      loading.value = false
    }
  }

  /**
   * Fetch all users with search and filter
   *
   * @param {Object} params - Query parameters
   * @returns {Promise<Object>} Page of users
   */
  async function fetchUsers(params = {}) {
    try {
      loading.value = true
      const response = await getAllUsersApi(params)
      users.value = response.data
      return users.value
    } catch (error) {
      console.error('Failed to fetch users:', error)
      ElMessage.error(error.message || 'Failed to load users')
      throw error
    } finally {
      loading.value = false
    }
  }

  /**
   * Fetch user details by ID
   *
   * @param {number} userId - User ID
   * @returns {Promise<Object>} User details
   */
  async function fetchUserDetails(userId) {
    try {
      loading.value = true
      const response = await getUserDetailsApi(userId)
      return response.data
    } catch (error) {
      console.error('Failed to fetch user details:', error)
      ElMessage.error(error.message || 'Failed to load user details')
      throw error
    } finally {
      loading.value = false
    }
  }

  /**
   * Update user status (enable/disable)
   *
   * @param {number} userId - User ID
   * @param {Object} data - Status data
   * @returns {Promise<void>}
   */
  async function updateUserStatus(userId, data) {
    try {
      loading.value = true
      await updateUserStatusApi(userId, data)
      ElMessage.success('User status updated successfully')
    } catch (error) {
      console.error('Failed to update user status:', error)
      ElMessage.error(error.message || 'Failed to update user status')
      throw error
    } finally {
      loading.value = false
    }
  }

  /**
   * Manage user role (add or remove)
   *
   * @param {Object} data - Role management data
   * @returns {Promise<void>}
   */
  async function manageUserRole(data) {
    try {
      loading.value = true
      await manageUserRoleApi(data)
      ElMessage.success('User role updated successfully')
    } catch (error) {
      console.error('Failed to manage user role:', error)
      ElMessage.error(error.message || 'Failed to update user role')
      throw error
    } finally {
      loading.value = false
    }
  }

  /**
   * Delete user by ID
   *
   * @param {number} userId - User ID
   * @returns {Promise<void>}
   */
  async function deleteUser(userId) {
    try {
      loading.value = true
      await deleteUserApi(userId)
      ElMessage.success('User deleted successfully')
    } catch (error) {
      console.error('Failed to delete user:', error)
      ElMessage.error(error.message || 'Failed to delete user')
      throw error
    } finally {
      loading.value = false
    }
  }

  /**
   * Fetch pending music for approval
   *
   * @param {Object} params - Query parameters
   * @returns {Promise<Object>} Page of pending music
   */
  async function fetchPendingMusic(params = {}) {
    try {
      loading.value = true
      const response = await getPendingMusicApi(params)
      musicList.value = response.data
      return musicList.value
    } catch (error) {
      console.error('Failed to fetch pending music:', error)
      ElMessage.error(error.message || 'Failed to load pending music')
      throw error
    } finally {
      loading.value = false
    }
  }

  /**
   * Fetch music by status
   *
   * @param {Object} params - Query parameters
   * @returns {Promise<Object>} Page of music
   */
  async function fetchMusicByStatus(params = {}) {
    try {
      loading.value = true
      const response = await getAllMusicAdminApi(params)
      musicList.value = response.data
      return musicList.value
    } catch (error) {
      console.error('Failed to fetch music:', error)
      ElMessage.error(error.message || 'Failed to load music')
      throw error
    } finally {
      loading.value = false
    }
  }

  /**
   * Approve music by ID
   *
   * @param {number} musicId - Music ID
   * @returns {Promise<void>}
   */
  async function approveMusic(musicId) {
    try {
      loading.value = true
      await approveMusicApi(musicId)
      ElMessage.success('Music approved successfully')
    } catch (error) {
      console.error('Failed to approve music:', error)
      ElMessage.error(error.message || 'Failed to approve music')
      throw error
    } finally {
      loading.value = false
    }
  }

  /**
   * Reject music by ID with optional reason
   *
   * @param {number} musicId - Music ID
   * @param {string} reason - Rejection reason
   * @returns {Promise<void>}
   */
  async function rejectMusic(musicId, reason) {
    try {
      loading.value = true
      await rejectMusicApi(musicId, reason)
      ElMessage.success('Music rejected successfully')
    } catch (error) {
      console.error('Failed to reject music:', error)
      ElMessage.error(error.message || 'Failed to reject music')
      throw error
    } finally {
      loading.value = false
    }
  }

  return {
    // State
    dashboardStats,
    users,
    musicList,
    loading,

    // Actions
    fetchDashboardStats,
    fetchUsers,
    fetchUserDetails,
    updateUserStatus,
    manageUserRole,
    deleteUser,
    fetchPendingMusic,
    fetchMusicByStatus,
    approveMusic,
    rejectMusic
  }
})
