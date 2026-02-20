/**
 * Social interactions store module
 *
 * @description Manages social interaction data including comments, likes, and follows
 *              using Pinia Composition API.
 *
 * @author MusicShare Team
 * @version 1.0.0
 */

import { defineStore } from 'pinia'
import { ref, computed } from 'vue'
import {
  createComment as createCommentApi,
  updateComment as updateCommentApi,
  deleteComment as deleteCommentApi,
  getCommentById as getCommentByIdApi,
  getCommentsByTarget as getCommentsByTargetApi,
  getReplies as getRepliesApi,
  getMyComments as getMyCommentsApi
} from '@/api/social'
import {
  likeTarget as likeTargetApi,
  unlikeTarget as unlikeTargetApi,
  checkLiked as checkLikedApi,
  getLikeCount as getLikeCountApi,
  getMyLikes as getMyLikesApi
} from '@/api/social'
import {
  followUser as followUserApi,
  unfollowUser as unfollowUserApi,
  checkFollowing as checkFollowingApi,
  getFollowers as getFollowersApi,
  getFollowing as getFollowingApi,
  getFollowStats as getFollowStatsApi
} from '@/api/social'
import { ElMessage } from 'element-plus'

export const useSocialStore = defineStore('social', () => {
  // ==================== State ====================

  // Comments: Map by targetKey (targetType:targetId)
  const comments = ref(new Map())

  // Comment statistics: Map by targetKey
  const commentStats = ref(new Map())

  // Liked targets: Set of targetKeys
  const likedTargets = ref(new Set())

  // Following users: Set of userIds
  const followingUsers = ref(new Set())

  // Loading states
  const loading = ref({
    comments: false,
    likes: false,
    follows: false
  })

  // ==================== Getters ====================

  const hasComments = computed(() => comments.value.size > 0)
  const hasLikes = computed(() => likedTargets.value.size > 0)
  const hasFollows = computed(() => followingUsers.value.size > 0)

  // ==================== Helper Functions ====================

  /**
   * Create a composite key for target identification
   *
   * @param {string} targetType - Target type (MUSIC, PLAYLIST, COMMENT)
   * @param {number} targetId - Target entity ID
   * @returns {string} Composite key
   */
  function getTargetKey(targetType, targetId) {
    return `${targetType}:${targetId}`
  }

  // ==================== Comment Actions ====================

  /**
   * Fetch comments for a target entity
   *
   * @param {string} targetType - Target type (MUSIC, PLAYLIST, COMMENT)
   * @param {number} targetId - Target entity ID
   * @param {Object} params - Query parameters
   * @returns {Promise<Object|null>} Page data or null
   */
  async function fetchComments(targetType, targetId, params = { page: 0, size: 20, sort: 'createdAt,desc' }) {
    try {
      loading.value.comments = true
      const response = await getCommentsByTargetApi(targetType, targetId, params)

      if (response.data) {
        const targetKey = getTargetKey(targetType, targetId)

        // Store comments
        comments.value.set(targetKey, response.data.content || [])

        // Store stats
        commentStats.value.set(targetKey, {
          total: response.data.totalElements || 0,
          page: response.data.number || 0,
          totalPages: response.data.totalPages || 0
        })

        return response.data
      }
      return null
    } catch (error) {
      console.error('Fetch comments failed:', error)
      ElMessage.error(error.message || 'Failed to fetch comments')
      return null
    } finally {
      loading.value.comments = false
    }
  }

  /**
   * Create a new comment
   *
   * @param {Object} data - Comment data
   * @param {string} data.content - Comment content
   * @param {string} data.targetType - Target type
   * @param {number} data.targetId - Target entity ID
   * @param {number} [data.parentCommentId] - Parent comment ID for replies
   * @returns {Promise<Object|null>} Created comment or null
   */
  async function createComment(data) {
    try {
      const response = await createCommentApi(data)

      if (response.data) {
        ElMessage.success('Comment posted successfully!')

        // Update local state
        const targetKey = getTargetKey(data.targetType, data.targetId)
        const existingComments = comments.value.get(targetKey) || []

        // Add new comment to the beginning
        comments.value.set(targetKey, [response.data, ...existingComments])

        // Update stats
        const stats = commentStats.value.get(targetKey) || { total: 0 }
        commentStats.value.set(targetKey, {
          ...stats,
          total: stats.total + 1
        })

        return response.data
      }
      return null
    } catch (error) {
      console.error('Create comment failed:', error)
      ElMessage.error(error.message || 'Failed to post comment')
      return null
    }
  }

  /**
   * Update a comment
   *
   * @param {number} id - Comment ID
   * @param {string} content - Updated comment content
   * @returns {Promise<boolean>} Success status
   */
  async function updateComment(id, content) {
    try {
      const response = await updateCommentApi(id, { content })

      if (response.data) {
        ElMessage.success('Comment updated successfully!')

        // Update in local state
        comments.value.forEach((commentList, targetKey) => {
          const index = commentList.findIndex(c => c.id === id)
          if (index !== -1) {
            commentList[index] = response.data
            comments.value.set(targetKey, [...commentList])
          }
        })

        return true
      }
      return false
    } catch (error) {
      console.error('Update comment failed:', error)
      ElMessage.error(error.message || 'Failed to update comment')
      return false
    }
  }

  /**
   * Delete a comment
   *
   * @param {number} id - Comment ID
   * @param {string} targetType - Target type
   * @param {number} targetId - Target entity ID
   * @returns {Promise<boolean>} Success status
   */
  async function deleteComment(id, targetType, targetId) {
    try {
      await deleteCommentApi(id)

      ElMessage.success('Comment deleted successfully!')

      // Remove from local state
      const targetKey = getTargetKey(targetType, targetId)
      const existingComments = comments.value.get(targetKey) || []
      const filteredComments = existingComments.filter(c => c.id !== id)
      comments.value.set(targetKey, filteredComments)

      // Update stats
      const stats = commentStats.value.get(targetKey) || { total: 0 }
      commentStats.value.set(targetKey, {
        ...stats,
        total: Math.max(0, stats.total - 1)
      })

      return true
    } catch (error) {
      console.error('Delete comment failed:', error)
      ElMessage.error(error.message || 'Failed to delete comment')
      return false
    }
  }

  /**
   * Load replies for a comment
   *
   * @param {number} commentId - Parent comment ID
   * @param {Object} params - Query parameters
   * @returns {Promise<Object|null>} Page of replies or null
   */
  async function loadReplies(commentId, params = { page: 0, size: 10, sort: 'createdAt,asc' }) {
    try {
      loading.value.comments = true
      const response = await getRepliesApi(commentId, params)

      if (response.data) {
        // Store replies using comment as target
        const targetKey = getTargetKey('COMMENT', commentId)
        comments.value.set(targetKey, response.data.content || [])

        commentStats.value.set(targetKey, {
          total: response.data.totalElements || 0,
          page: response.data.number || 0,
          totalPages: response.data.totalPages || 0
        })

        return response.data
      }
      return null
    } catch (error) {
      console.error('Load replies failed:', error)
      ElMessage.error(error.message || 'Failed to load replies')
      return null
    } finally {
      loading.value.comments = false
    }
  }

  /**
   * Get comments for a specific target from local state
   *
   * @param {string} targetType - Target type
   * @param {number} targetId - Target entity ID
   * @returns {Array} List of comments
   */
  function getCommentsByTargetLocal(targetType, targetId) {
    const targetKey = getTargetKey(targetType, targetId)
    return comments.value.get(targetKey) || []
  }

  /**
   * Get comment stats for a specific target from local state
   *
   * @param {string} targetType - Target type
   * @param {number} targetId - Target entity ID
   * @returns {Object} Comment statistics
   */
  function getCommentStatsLocal(targetType, targetId) {
    const targetKey = getTargetKey(targetType, targetId)
    return commentStats.value.get(targetKey) || { total: 0 }
  }

  // ==================== Like Actions ====================

  /**
   * Like a target entity
   *
   * @param {string} targetType - Target type (MUSIC, PLAYLIST, COMMENT)
   * @param {number} targetId - Target entity ID
   * @returns {Promise<boolean>} Success status
   */
  async function likeTarget(targetType, targetId) {
    try {
      loading.value.likes = true

      // Optimistic update
      const targetKey = getTargetKey(targetType, targetId)
      likedTargets.value.add(targetKey)

      await likeTargetApi(targetType, targetId)

      ElMessage.success('Liked successfully!')
      return true
    } catch (error) {
      console.error('Like target failed:', error)

      // Revert optimistic update
      const targetKey = getTargetKey(targetType, targetId)
      likedTargets.value.delete(targetKey)

      ElMessage.error(error.message || 'Failed to like')
      return false
    } finally {
      loading.value.likes = false
    }
  }

  /**
   * Unlike a target entity
   *
   * @param {string} targetType - Target type (MUSIC, PLAYLIST, COMMENT)
   * @param {number} targetId - Target entity ID
   * @returns {Promise<boolean>} Success status
   */
  async function unlikeTarget(targetType, targetId) {
    try {
      loading.value.likes = true

      // Optimistic update
      const targetKey = getTargetKey(targetType, targetId)
      likedTargets.value.delete(targetKey)

      await unlikeTargetApi(targetType, targetId)

      ElMessage.success('Unliked successfully!')
      return true
    } catch (error) {
      console.error('Unlike target failed:', error)

      // Revert optimistic update
      const targetKey = getTargetKey(targetType, targetId)
      likedTargets.value.add(targetKey)

      ElMessage.error(error.message || 'Failed to unlike')
      return false
    } finally {
      loading.value.likes = false
    }
  }

  /**
   * Check if a target is liked
   *
   * @param {string} targetType - Target type
   * @param {number} targetId - Target entity ID
   * @returns {Promise<boolean>} True if liked, false otherwise
   */
  async function checkLiked(targetType, targetId) {
    try {
      const response = await checkLikedApi(targetType, targetId)

      const targetKey = getTargetKey(targetType, targetId)
      if (response.data) {
        likedTargets.value.add(targetKey)
      } else {
        likedTargets.value.delete(targetKey)
      }

      return response.data
    } catch (error) {
      console.error('Check liked failed:', error)
      return false
    }
  }

  /**
   * Check if a target is liked (from local state)
   *
   * @param {string} targetType - Target type
   * @param {number} targetId - Target entity ID
   * @returns {boolean} True if liked, false otherwise
   */
  function isLikedLocal(targetType, targetId) {
    const targetKey = getTargetKey(targetType, targetId)
    return likedTargets.value.has(targetKey)
  }

  /**
   * Refresh like status for multiple targets
   *
   * @param {Array<Object>} targets - Array of {targetType, targetId}
   * @returns {Promise<void>}
   */
  async function refreshLikeStatus(targets) {
    try {
      // Check each target in parallel
      const checks = targets.map(({ targetType, targetId }) =>
        checkLiked(targetType, targetId)
      )
      await Promise.all(checks)
    } catch (error) {
      console.error('Refresh like status failed:', error)
    }
  }

  // ==================== Follow Actions ====================

  /**
   * Follow a user
   *
   * @param {number} userId - User ID to follow
   * @returns {Promise<boolean>} Success status
   */
  async function followUser(userId) {
    try {
      loading.value.follows = true

      // Optimistic update
      followingUsers.value.add(userId)

      await followUserApi(userId)

      ElMessage.success('Followed successfully!')
      return true
    } catch (error) {
      console.error('Follow user failed:', error)

      // Revert optimistic update
      followingUsers.value.delete(userId)

      ElMessage.error(error.message || 'Failed to follow user')
      return false
    } finally {
      loading.value.follows = false
    }
  }

  /**
   * Unfollow a user
   *
   * @param {number} userId - User ID to unfollow
   * @returns {Promise<boolean>} Success status
   */
  async function unfollowUser(userId) {
    try {
      loading.value.follows = true

      // Optimistic update
      followingUsers.value.delete(userId)

      await unfollowUserApi(userId)

      ElMessage.success('Unfollowed successfully!')
      return true
    } catch (error) {
      console.error('Unfollow user failed:', error)

      // Revert optimistic update
      followingUsers.value.add(userId)

      ElMessage.error(error.message || 'Failed to unfollow user')
      return false
    } finally {
      loading.value.follows = false
    }
  }

  /**
   * Check if current user is following a user
   *
   * @param {number} userId - User ID to check
   * @returns {Promise<boolean>} True if following, false otherwise
   */
  async function checkFollowing(userId) {
    try {
      const response = await checkFollowingApi(userId)

      if (response.data) {
        followingUsers.value.add(userId)
      } else {
        followingUsers.value.delete(userId)
      }

      return response.data
    } catch (error) {
      console.error('Check following failed:', error)
      return false
    }
  }

  /**
   * Check if following a user (from local state)
   *
   * @param {number} userId - User ID
   * @returns {boolean} True if following, false otherwise
   */
  function isFollowingLocal(userId) {
    return followingUsers.value.has(userId)
  }

  /**
   * Fetch followers for a user
   *
   * @param {number} userId - User ID
   * @param {Object} params - Query parameters
   * @returns {Promise<Object|null>} Page data or null
   */
  async function fetchFollowers(userId, params = { page: 0, size: 20 }) {
    try {
      loading.value.follows = true
      const response = await getFollowersApi(userId, params)

      if (response.data) {
        return response.data
      }
      return null
    } catch (error) {
      console.error('Fetch followers failed:', error)
      ElMessage.error(error.message || 'Failed to fetch followers')
      return null
    } finally {
      loading.value.follows = false
    }
  }

  /**
   * Fetch users that a user is following
   *
   * @param {number} userId - User ID
   * @param {Object} params - Query parameters
   * @returns {Promise<Object|null>} Page data or null
   */
  async function fetchFollowing(userId, params = { page: 0, size: 20 }) {
    try {
      loading.value.follows = true
      const response = await getFollowingApi(userId, params)

      if (response.data) {
        return response.data
      }
      return null
    } catch (error) {
      console.error('Fetch following failed:', error)
      ElMessage.error(error.message || 'Failed to fetch following')
      return null
    } finally {
      loading.value.follows = false
    }
  }

  /**
   * Fetch follow statistics for a user
   *
   * @param {number} userId - User ID
   * @returns {Promise<Object|null>} Follow stats or null
   */
  async function fetchFollowStats(userId) {
    try {
      const response = await getFollowStatsApi(userId)

      if (response.data) {
        return response.data
      }
      return null
    } catch (error) {
      console.error('Fetch follow stats failed:', error)
      ElMessage.error(error.message || 'Failed to fetch follow stats')
      return null
    }
  }

  // ==================== Utility Actions ====================

  /**
   * Clear all social data
   */
  function clearAll() {
    comments.value.clear()
    commentStats.value.clear()
    likedTargets.value.clear()
    followingUsers.value.clear()
    loading.value = {
      comments: false,
      likes: false,
      follows: false
    }
  }

  /**
   * Clear comments for a specific target
   *
   * @param {string} targetType - Target type
   * @param {number} targetId - Target entity ID
   */
  function clearComments(targetType, targetId) {
    const targetKey = getTargetKey(targetType, targetId)
    comments.value.delete(targetKey)
    commentStats.value.delete(targetKey)
  }

  return {
    // State
    comments,
    commentStats,
    likedTargets,
    followingUsers,
    loading,

    // Getters
    hasComments,
    hasLikes,
    hasFollows,

    // Helpers
    getTargetKey,

    // Comment Actions
    fetchComments,
    createComment,
    updateComment,
    deleteComment,
    loadReplies,
    getCommentsByTargetLocal,
    getCommentStatsLocal,

    // Like Actions
    likeTarget,
    unlikeTarget,
    checkLiked,
    isLikedLocal,
    refreshLikeStatus,

    // Follow Actions
    followUser,
    unfollowUser,
    checkFollowing,
    isFollowingLocal,
    fetchFollowers,
    fetchFollowing,
    fetchFollowStats,

    // Utility
    clearAll,
    clearComments
  }
})
