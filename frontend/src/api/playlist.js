/**
 * Playlist API module
 *
 * @description Provides API methods for playlist-related operations including
 *              creation, management, and music organization.
 *
 * @author MusicShare Team
 * @version 1.0.0
 */

import request from './request'

/**
 * Create a new playlist
 *
 * @param {Object} data - Playlist data
 * @param {string} data.name - Playlist name
 * @param {string} data.description - Playlist description
 * @param {string} data.coverImage - Cover image URL
 * @param {boolean} data.isPublic - Public visibility
 * @returns {Promise} Created playlist
 */
export function createPlaylist(data) {
  return request({
    url: '/playlists',
    method: 'post',
    data
  })
}

/**
 * Get playlist details
 *
 * @param {number} id - Playlist ID
 * @returns {Promise} Playlist details with music list
 */
export function getPlaylistDetail(id) {
  return request({
    url: `/playlists/${id}`,
    method: 'get'
  })
}

/**
 * Update playlist
 *
 * @param {number} id - Playlist ID
 * @param {Object} data - Update data
 * @returns {Promise} Updated playlist
 */
export function updatePlaylist(id, data) {
  return request({
    url: `/playlists/${id}`,
    method: 'put',
    data
  })
}

/**
 * Delete playlist
 *
 * @param {number} id - Playlist ID
 * @returns {Promise} Delete result
 */
export function deletePlaylist(id) {
  return request({
    url: `/playlists/${id}`,
    method: 'delete'
  })
}

/**
 * Get current user's playlists
 *
 * @param {Object} params - Query parameters
 * @param {number} params.page - Page number
 * @param {number} params.size - Page size
 * @returns {Promise} Page of playlists
 */
export function getMyPlaylists(params) {
  return request({
    url: '/playlists/my',
    method: 'get',
    params
  })
}

/**
 * Get public playlists
 *
 * @param {Object} params - Query parameters
 * @param {number} params.page - Page number
 * @param {number} params.size - Page size
 * @returns {Promise} Page of public playlists
 */
export function getPublicPlaylists(params) {
  return request({
    url: '/playlists/public',
    method: 'get',
    params
  })
}

/**
 * Search playlists
 *
 * @param {Object} params - Query parameters
 * @param {string} params.keyword - Search keyword
 * @param {number} params.page - Page number
 * @param {number} params.size - Page size
 * @returns {Promise} Page of matching playlists
 */
export function searchPlaylists(params) {
  return request({
    url: '/playlists/search',
    method: 'get',
    params
  })
}

/**
 * Get popular playlists
 *
 * @param {Object} params - Query parameters
 * @param {number} params.page - Page number
 * @param {number} params.size - Page size
 * @returns {Promise} Page of popular playlists
 */
export function getPopularPlaylists(params) {
  return request({
    url: '/playlists/popular',
    method: 'get',
    params
  })
}

/**
 * Add music to playlist
 *
 * @param {number} playlistId - Playlist ID
 * @param {number} musicId - Music ID
 * @returns {Promise} Add result
 */
export function addMusicToPlaylist(playlistId, musicId) {
  return request({
    url: `/playlists/${playlistId}/music/${musicId}`,
    method: 'post'
  })
}

/**
 * Remove music from playlist
 *
 * @param {number} playlistId - Playlist ID
 * @param {number} musicId - Music ID
 * @returns {Promise} Remove result
 */
export function removeMusicFromPlaylist(playlistId, musicId) {
  return request({
    url: `/playlists/${playlistId}/music/${musicId}`,
    method: 'delete'
  })
}

/**
 * Reorder playlist music
 *
 * @param {number} playlistId - Playlist ID
 * @param {Array<number>} musicIds - Ordered array of music IDs
 * @returns {Promise} Reorder result
 */
export function reorderPlaylistMusic(playlistId, musicIds) {
  return request({
    url: `/playlists/${playlistId}/reorder`,
    method: 'put',
    data: musicIds
  })
}
