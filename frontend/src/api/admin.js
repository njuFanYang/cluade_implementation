/**
 * Admin API module
 *
 * @description Provides API methods for admin operations including dashboard
 *              statistics, user management, and music approval workflow.
 *
 * @author MusicShare Team
 * @version 1.0.0
 */

import request from './request'

// ==================== Dashboard ====================

/**
 * Get dashboard statistics
 *
 * @returns {Promise} Dashboard statistics
 */
export function getDashboardStats() {
  return request({
    url: '/admin/dashboard',
    method: 'get'
  })
}

// ==================== User Management ====================

/**
 * Get all users with search and filter
 *
 * @param {Object} params - Query parameters
 * @param {string} params.keyword - Search keyword for username or email
 * @param {boolean} params.enabled - Filter by enabled status
 * @param {number} params.page - Page number
 * @param {number} params.size - Page size
 * @returns {Promise} Page of users
 */
export function getAllUsers(params) {
  return request({
    url: '/admin/users',
    method: 'get',
    params
  })
}

/**
 * Get user details by ID
 *
 * @param {number} userId - User ID
 * @returns {Promise} User details
 */
export function getUserDetails(userId) {
  return request({
    url: `/admin/users/${userId}`,
    method: 'get'
  })
}

/**
 * Update user status (enable/disable)
 *
 * @param {number} userId - User ID
 * @param {Object} data - Status data
 * @param {boolean} data.enabled - Whether to enable the user
 * @param {boolean} data.locked - Whether to lock the user
 * @returns {Promise} Update result
 */
export function updateUserStatus(userId, data) {
  return request({
    url: `/admin/users/${userId}/status`,
    method: 'put',
    data
  })
}

/**
 * Manage user role (add or remove)
 *
 * @param {Object} data - Role management data
 * @param {number} data.userId - User ID
 * @param {string} data.roleName - Role name (ROLE_USER, ROLE_MUSICIAN, ROLE_ADMIN)
 * @param {string} data.action - Action (ADD or REMOVE)
 * @returns {Promise} Update result
 */
export function manageUserRole(data) {
  return request({
    url: '/admin/users/roles',
    method: 'post',
    data
  })
}

/**
 * Delete user by ID
 *
 * @param {number} userId - User ID
 * @returns {Promise} Delete result
 */
export function deleteUser(userId) {
  return request({
    url: `/admin/users/${userId}`,
    method: 'delete'
  })
}

// ==================== Music Approval ====================

/**
 * Get pending music for approval
 *
 * @param {Object} params - Query parameters
 * @param {number} params.page - Page number
 * @param {number} params.size - Page size
 * @returns {Promise} Page of pending music
 */
export function getPendingMusic(params) {
  return request({
    url: '/admin/music/pending',
    method: 'get',
    params
  })
}

/**
 * Get all music by status
 *
 * @param {Object} params - Query parameters
 * @param {string} params.status - Music status (PENDING, APPROVED, REJECTED)
 * @param {number} params.page - Page number
 * @param {number} params.size - Page size
 * @returns {Promise} Page of music
 */
export function getAllMusicAdmin(params) {
  return request({
    url: '/admin/music',
    method: 'get',
    params
  })
}

/**
 * Approve music by ID
 *
 * @param {number} musicId - Music ID
 * @returns {Promise} Approval result
 */
export function approveMusic(musicId) {
  return request({
    url: `/admin/music/${musicId}/approve`,
    method: 'put'
  })
}

/**
 * Reject music by ID with optional reason
 *
 * @param {number} musicId - Music ID
 * @param {string} reason - Rejection reason (optional)
 * @returns {Promise} Rejection result
 */
export function rejectMusic(musicId, reason) {
  return request({
    url: `/admin/music/${musicId}/reject`,
    method: 'put',
    params: { reason }
  })
}
