/**
 * Music API module
 *
 * @description Provides API methods for music-related operations including
 *              upload, playback, search, and music management.
 *
 * @author MusicShare Team
 * @version 1.0.0
 */

import request from './request'

/**
 * Upload music file with metadata
 *
 * @param {FormData} formData - Form data containing file and metadata
 * @returns {Promise} Upload result with music details
 */
export function uploadMusic(formData) {
  return request({
    url: '/music/upload',
    method: 'post',
    data: formData,
    headers: {
      'Content-Type': 'multipart/form-data'
    },
    timeout: 300000 // 5 minutes for large files
  })
}

/**
 * Get music details by ID
 *
 * @param {number} id - Music ID
 * @returns {Promise} Music details
 */
export function getMusicById(id) {
  return request({
    url: `/music/${id}`,
    method: 'get'
  })
}

/**
 * Update music metadata
 *
 * @param {number} id - Music ID
 * @param {Object} data - Update data
 * @returns {Promise} Updated music details
 */
export function updateMusic(id, data) {
  return request({
    url: `/music/${id}`,
    method: 'put',
    data
  })
}

/**
 * Delete music
 *
 * @param {number} id - Music ID
 * @returns {Promise} Delete result
 */
export function deleteMusic(id) {
  return request({
    url: `/music/${id}`,
    method: 'delete'
  })
}

/**
 * Get current user's uploaded music
 *
 * @param {Object} params - Query parameters
 * @param {number} params.page - Page number (0-indexed)
 * @param {number} params.size - Page size
 * @param {string} params.sort - Sort field and direction
 * @returns {Promise} Page of music
 */
export function getMyMusic(params) {
  return request({
    url: '/music/my',
    method: 'get',
    params
  })
}

/**
 * Search music by keyword
 *
 * @param {Object} params - Query parameters
 * @param {string} params.keyword - Search keyword
 * @param {number} params.page - Page number
 * @param {number} params.size - Page size
 * @returns {Promise} Page of matching music
 */
export function searchMusic(params) {
  return request({
    url: '/music/search',
    method: 'get',
    params
  })
}

/**
 * Get popular music
 *
 * @param {Object} params - Query parameters
 * @param {number} params.page - Page number
 * @param {number} params.size - Page size
 * @returns {Promise} Page of popular music
 */
export function getPopularMusic(params) {
  return request({
    url: '/music/popular',
    method: 'get',
    params
  })
}

/**
 * Get recent music
 *
 * @param {Object} params - Query parameters
 * @param {number} params.page - Page number
 * @param {number} params.size - Page size
 * @returns {Promise} Page of recent music
 */
export function getRecentMusic(params) {
  return request({
    url: '/music/recent',
    method: 'get',
    params
  })
}

/**
 * Get music by genre
 *
 * @param {number} genreId - Genre ID
 * @param {Object} params - Query parameters
 * @param {number} params.page - Page number
 * @param {number} params.size - Page size
 * @returns {Promise} Page of music in genre
 */
export function getMusicByGenre(genreId, params) {
  return request({
    url: `/music/genre/${genreId}`,
    method: 'get',
    params
  })
}

/**
 * Get all public music
 *
 * @param {Object} params - Query parameters
 * @param {number} params.page - Page number
 * @param {number} params.size - Page size
 * @param {string} params.sort - Sort field and direction
 * @returns {Promise} Page of public music
 */
export function getAllPublicMusic(params) {
  return request({
    url: '/music',
    method: 'get',
    params
  })
}

/**
 * Get music stream URL
 *
 * @param {number} id - Music ID
 * @returns {string} Stream URL
 */
export function getMusicStreamUrl(id) {
  // Return the full URL for audio element
  const baseURL = import.meta.env.VITE_API_BASE_URL || 'http://localhost:8081/api'
  return `${baseURL}/music/${id}/stream`
}

/**
 * Record music play
 *
 * @param {number} id - Music ID
 * @returns {Promise} Record result
 */
export function recordPlay(id) {
  return request({
    url: `/music/${id}/play`,
    method: 'post'
  })
}
