/**
 * Genre API module
 *
 * @description Provides API methods for music genre operations.
 *
 * @author MusicShare Team
 * @version 1.0.0
 */

import request from './request'

/**
 * Get all genres
 *
 * @returns {Promise} List of all genres
 */
export function getAllGenres() {
  return request({
    url: '/genres',
    method: 'get'
  })
}

/**
 * Get popular genres ordered by music count
 *
 * @returns {Promise} List of popular genres
 */
export function getPopularGenres() {
  return request({
    url: '/genres/popular',
    method: 'get'
  })
}

/**
 * Get genre by ID
 *
 * @param {number} id - Genre ID
 * @returns {Promise} Genre details
 */
export function getGenreById(id) {
  return request({
    url: `/genres/${id}`,
    method: 'get'
  })
}
