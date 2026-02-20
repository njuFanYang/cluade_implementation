/**
 * User API module
 *
 * @description Provides API methods for user-related operations including
 *              authentication, profile management, and user queries.
 *
 * @author MusicShare Team
 * @version 1.0.0
 */

import request from './request'

/**
 * User registration
 *
 * @param {Object} data - Registration data
 * @param {string} data.username - Username
 * @param {string} data.email - Email address
 * @param {string} data.password - Password
 * @returns {Promise} Registration result
 */
export function register(data) {
  return request({
    url: '/auth/register',
    method: 'post',
    data
  })
}

/**
 * User login
 *
 * @param {Object} data - Login credentials
 * @param {string} data.username - Username or email
 * @param {string} data.password - Password
 * @returns {Promise} Login result with token
 */
export function login(data) {
  return request({
    url: '/auth/login',
    method: 'post',
    data
  })
}

/**
 * User logout
 *
 * @returns {Promise} Logout result
 */
export function logout() {
  return request({
    url: '/auth/logout',
    method: 'post'
  })
}

/**
 * Get current user profile
 *
 * @returns {Promise<Object>} User profile data
 */
export function getUserProfile() {
  return request({
    url: '/users/profile',
    method: 'get'
  })
}

/**
 * Update user profile
 *
 * @param {Object} data - Profile data to update
 * @returns {Promise} Update result
 */
export function updateProfile(data) {
  return request({
    url: '/users/profile',
    method: 'put',
    data
  })
}

/**
 * Change password
 *
 * @param {Object} data - Password change data
 * @param {string} data.oldPassword - Current password
 * @param {string} data.newPassword - New password
 * @returns {Promise} Change result
 */
export function changePassword(data) {
  return request({
    url: '/users/password',
    method: 'put',
    data
  })
}

/**
 * Get user by ID
 *
 * @param {number} userId - User ID
 * @returns {Promise<Object>} User data
 */
export function getUserById(userId) {
  return request({
    url: `/users/${userId}`,
    method: 'get'
  })
}

/**
 * Upload avatar
 *
 * @param {FormData} formData - Form data containing avatar file
 * @returns {Promise<string>} Avatar URL
 */
export function uploadAvatar(formData) {
  return request({
    url: '/users/avatar',
    method: 'post',
    data: formData,
    headers: {
      'Content-Type': 'multipart/form-data'
    }
  })
}
