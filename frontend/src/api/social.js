/**
 * Social API module
 *
 * @description Provides API methods for social interaction operations including
 *              comments, likes, and follow relationships.
 *
 * @author MusicShare Team
 * @version 1.0.0
 */

import request from './request'

// ========== Comment APIs ==========

/**
 * Create a new comment
 *
 * @param {Object} data - Comment data
 * @param {string} data.content - Comment content
 * @param {string} data.targetType - Target type (MUSIC, PLAYLIST, COMMENT)
 * @param {number} data.targetId - Target entity ID
 * @param {number} [data.parentCommentId] - Parent comment ID for replies
 * @returns {Promise} Created comment
 */
export function createComment(data) {
  return request({
    url: '/comments',
    method: 'post',
    data
  })
}

/**
 * Update a comment
 *
 * @param {number} id - Comment ID
 * @param {Object} data - Update data
 * @param {string} data.content - Updated comment content
 * @returns {Promise} Updated comment
 */
export function updateComment(id, data) {
  return request({
    url: `/comments/${id}`,
    method: 'put',
    data
  })
}

/**
 * Delete a comment
 *
 * @param {number} id - Comment ID
 * @returns {Promise} Delete result
 */
export function deleteComment(id) {
  return request({
    url: `/comments/${id}`,
    method: 'delete'
  })
}

/**
 * Get comment details by ID
 *
 * @param {number} id - Comment ID
 * @returns {Promise} Comment details
 */
export function getCommentById(id) {
  return request({
    url: `/comments/${id}`,
    method: 'get'
  })
}

/**
 * Get comments by target entity
 *
 * @param {string} targetType - Target type (MUSIC, PLAYLIST, COMMENT)
 * @param {number} targetId - Target entity ID
 * @param {Object} params - Query parameters
 * @param {number} params.page - Page number
 * @param {number} params.size - Page size
 * @param {string} [params.sort] - Sort field and direction
 * @returns {Promise} Page of comments
 */
export function getCommentsByTarget(targetType, targetId, params) {
  return request({
    url: '/comments/target',
    method: 'get',
    params: {
      targetType,
      targetId,
      ...params
    }
  })
}

/**
 * Get replies to a comment
 *
 * @param {number} commentId - Parent comment ID
 * @param {Object} params - Query parameters
 * @param {number} params.page - Page number
 * @param {number} params.size - Page size
 * @param {string} [params.sort] - Sort field and direction
 * @returns {Promise} Page of replies
 */
export function getReplies(commentId, params) {
  return request({
    url: `/comments/${commentId}/replies`,
    method: 'get',
    params
  })
}

/**
 * Get current user's comments
 *
 * @param {Object} params - Query parameters
 * @param {number} params.page - Page number
 * @param {number} params.size - Page size
 * @param {string} [params.sort] - Sort field and direction
 * @returns {Promise} Page of comments
 */
export function getMyComments(params) {
  return request({
    url: '/comments/my',
    method: 'get',
    params
  })
}

// ========== Like APIs ==========

/**
 * Like a target entity
 *
 * @param {string} targetType - Target type (MUSIC, PLAYLIST, COMMENT)
 * @param {number} targetId - Target entity ID
 * @returns {Promise} Like result
 */
export function likeTarget(targetType, targetId) {
  return request({
    url: '/likes',
    method: 'post',
    params: {
      targetType,
      targetId
    }
  })
}

/**
 * Unlike a target entity
 *
 * @param {string} targetType - Target type (MUSIC, PLAYLIST, COMMENT)
 * @param {number} targetId - Target entity ID
 * @returns {Promise} Unlike result
 */
export function unlikeTarget(targetType, targetId) {
  return request({
    url: '/likes',
    method: 'delete',
    params: {
      targetType,
      targetId
    }
  })
}

/**
 * Check if user has liked a target entity
 *
 * @param {string} targetType - Target type (MUSIC, PLAYLIST, COMMENT)
 * @param {number} targetId - Target entity ID
 * @returns {Promise<boolean>} True if liked, false otherwise
 */
export function checkLiked(targetType, targetId) {
  return request({
    url: '/likes/check',
    method: 'get',
    params: {
      targetType,
      targetId
    }
  })
}

/**
 * Get like count for a target entity
 *
 * @param {string} targetType - Target type (MUSIC, PLAYLIST, COMMENT)
 * @param {number} targetId - Target entity ID
 * @returns {Promise<number>} Like count
 */
export function getLikeCount(targetType, targetId) {
  return request({
    url: '/likes/count',
    method: 'get',
    params: {
      targetType,
      targetId
    }
  })
}

/**
 * Get current user's liked items
 *
 * @param {string} targetType - Target type (MUSIC, PLAYLIST, COMMENT)
 * @param {Object} params - Query parameters
 * @param {number} params.page - Page number
 * @param {number} params.size - Page size
 * @param {string} [params.sort] - Sort field and direction
 * @returns {Promise} Page of liked items
 */
export function getMyLikes(targetType, params) {
  return request({
    url: '/likes/my',
    method: 'get',
    params: {
      targetType,
      ...params
    }
  })
}

// ========== Follow APIs ==========

/**
 * Follow a user
 *
 * @param {number} userId - User ID to follow
 * @returns {Promise} Follow result
 */
export function followUser(userId) {
  return request({
    url: `/follows/${userId}`,
    method: 'post'
  })
}

/**
 * Unfollow a user
 *
 * @param {number} userId - User ID to unfollow
 * @returns {Promise} Unfollow result
 */
export function unfollowUser(userId) {
  return request({
    url: `/follows/${userId}`,
    method: 'delete'
  })
}

/**
 * Check if current user is following a user
 *
 * @param {number} userId - User ID to check
 * @returns {Promise<boolean>} True if following, false otherwise
 */
export function checkFollowing(userId) {
  return request({
    url: `/follows/check/${userId}`,
    method: 'get'
  })
}

/**
 * Get user's followers
 *
 * @param {number} userId - User ID
 * @param {Object} params - Query parameters
 * @param {number} params.page - Page number
 * @param {number} params.size - Page size
 * @returns {Promise} Page of followers
 */
export function getFollowers(userId, params) {
  return request({
    url: `/follows/${userId}/followers`,
    method: 'get',
    params
  })
}

/**
 * Get users that a user is following
 *
 * @param {number} userId - User ID
 * @param {Object} params - Query parameters
 * @param {number} params.page - Page number
 * @param {number} params.size - Page size
 * @returns {Promise} Page of following users
 */
export function getFollowing(userId, params) {
  return request({
    url: `/follows/${userId}/following`,
    method: 'get',
    params
  })
}

/**
 * Get follow statistics for a user
 *
 * @param {number} userId - User ID
 * @returns {Promise} Follow statistics (follower count, following count)
 */
export function getFollowStats(userId) {
  return request({
    url: `/follows/${userId}/stats`,
    method: 'get'
  })
}
