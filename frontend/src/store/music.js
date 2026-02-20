/**
 * Music store module
 *
 * @description Manages music data, genres, playlists and related operations using Pinia.
 *
 * @author MusicShare Team
 * @version 1.0.0
 */

import { defineStore } from 'pinia'
import { ref, computed } from 'vue'
import {
  uploadMusic as uploadMusicApi,
  getMusicById,
  updateMusic as updateMusicApi,
  deleteMusic as deleteMusicApi,
  getMyMusic as getMyMusicApi,
  searchMusic as searchMusicApi,
  getPopularMusic as getPopularMusicApi,
  getRecentMusic as getRecentMusicApi,
  getMusicByGenre as getMusicByGenreApi,
  getAllPublicMusic as getAllPublicMusicApi,
  recordPlay as recordPlayApi
} from '@/api/music'
import {
  createPlaylist as createPlaylistApi,
  getPlaylistDetail as getPlaylistDetailApi,
  updatePlaylist as updatePlaylistApi,
  deletePlaylist as deletePlaylistApi,
  getMyPlaylists as getMyPlaylistsApi,
  getPublicPlaylists as getPublicPlaylistsApi,
  searchPlaylists as searchPlaylistsApi,
  getPopularPlaylists as getPopularPlaylistsApi,
  addMusicToPlaylist as addMusicToPlaylistApi,
  removeMusicFromPlaylist as removeMusicFromPlaylistApi,
  reorderPlaylistMusic as reorderPlaylistMusicApi
} from '@/api/playlist'
import {
  getAllGenres as getAllGenresApi,
  getPopularGenres as getPopularGenresApi,
  getGenreById as getGenreByIdApi
} from '@/api/genre'
import { ElMessage } from 'element-plus'

export const useMusicStore = defineStore('music', () => {
  // State
  const musicList = ref([])
  const currentMusic = ref(null)
  const totalMusic = ref(0)
  const loading = ref(false)

  const genres = ref([])
  const currentGenre = ref(null)

  const playlists = ref([])
  const currentPlaylist = ref(null)
  const totalPlaylists = ref(0)

  // Getters
  const hasMusicList = computed(() => musicList.value.length > 0)
  const hasGenres = computed(() => genres.value.length > 0)
  const hasPlaylists = computed(() => playlists.value.length > 0)

  /**
   * Upload music
   *
   * @param {FormData} formData - Form data with file and metadata
   * @returns {Promise<Object|null>} Uploaded music or null
   */
  async function uploadMusic(formData) {
    try {
      loading.value = true
      const response = await uploadMusicApi(formData)

      if (response.data) {
        ElMessage.success('Music uploaded successfully!')
        return response.data
      }
    } catch (error) {
      console.error('Upload music failed:', error)
      ElMessage.error(error.message || 'Failed to upload music')
      return null
    } finally {
      loading.value = false
    }
  }

  /**
   * Get music by ID
   *
   * @param {number} id - Music ID
   * @returns {Promise<Object|null>} Music details or null
   */
  async function fetchMusicById(id) {
    try {
      loading.value = true
      const response = await getMusicById(id)

      if (response.data) {
        currentMusic.value = response.data
        return response.data
      }
      return null
    } catch (error) {
      console.error('Fetch music failed:', error)
      ElMessage.error(error.message || 'Failed to fetch music')
      return null
    } finally {
      loading.value = false
    }
  }

  /**
   * Update music
   *
   * @param {number} id - Music ID
   * @param {Object} data - Update data
   * @returns {Promise<boolean>} Success status
   */
  async function updateMusic(id, data) {
    try {
      loading.value = true
      const response = await updateMusicApi(id, data)

      if (response.data) {
        ElMessage.success('Music updated successfully!')

        // Update in list if exists
        const index = musicList.value.findIndex(m => m.id === id)
        if (index !== -1) {
          musicList.value[index] = response.data
        }

        // Update current if it's the same
        if (currentMusic.value?.id === id) {
          currentMusic.value = response.data
        }

        return true
      }
      return false
    } catch (error) {
      console.error('Update music failed:', error)
      ElMessage.error(error.message || 'Failed to update music')
      return false
    } finally {
      loading.value = false
    }
  }

  /**
   * Delete music
   *
   * @param {number} id - Music ID
   * @returns {Promise<boolean>} Success status
   */
  async function deleteMusic(id) {
    try {
      loading.value = true
      await deleteMusicApi(id)

      ElMessage.success('Music deleted successfully!')

      // Remove from list
      musicList.value = musicList.value.filter(m => m.id !== id)
      totalMusic.value = Math.max(0, totalMusic.value - 1)

      // Clear current if it's the same
      if (currentMusic.value?.id === id) {
        currentMusic.value = null
      }

      return true
    } catch (error) {
      console.error('Delete music failed:', error)
      ElMessage.error(error.message || 'Failed to delete music')
      return false
    } finally {
      loading.value = false
    }
  }

  /**
   * Get my music
   *
   * @param {Object} params - Query parameters
   * @returns {Promise<Object|null>} Page data or null
   */
  async function fetchMyMusic(params = { page: 0, size: 20, sort: 'createdAt,desc' }) {
    try {
      loading.value = true
      const response = await getMyMusicApi(params)

      if (response.data) {
        musicList.value = response.data.content || []
        totalMusic.value = response.data.totalElements || 0
        return response.data
      }
      return null
    } catch (error) {
      console.error('Fetch my music failed:', error)
      ElMessage.error(error.message || 'Failed to fetch music')
      return null
    } finally {
      loading.value = false
    }
  }

  /**
   * Search music
   *
   * @param {Object} params - Query parameters with keyword
   * @returns {Promise<Object|null>} Page data or null
   */
  async function searchMusic(params) {
    try {
      loading.value = true
      const response = await searchMusicApi(params)

      if (response.data) {
        musicList.value = response.data.content || []
        totalMusic.value = response.data.totalElements || 0
        return response.data
      }
      return null
    } catch (error) {
      console.error('Search music failed:', error)
      ElMessage.error(error.message || 'Failed to search music')
      return null
    } finally {
      loading.value = false
    }
  }

  /**
   * Get popular music
   *
   * @param {Object} params - Query parameters
   * @returns {Promise<Object|null>} Page data or null
   */
  async function fetchPopularMusic(params = { page: 0, size: 20 }) {
    try {
      loading.value = true
      const response = await getPopularMusicApi(params)

      if (response.data) {
        musicList.value = response.data.content || []
        totalMusic.value = response.data.totalElements || 0
        return response.data
      }
      return null
    } catch (error) {
      console.error('Fetch popular music failed:', error)
      ElMessage.error(error.message || 'Failed to fetch popular music')
      return null
    } finally {
      loading.value = false
    }
  }

  /**
   * Get recent music
   *
   * @param {Object} params - Query parameters
   * @returns {Promise<Object|null>} Page data or null
   */
  async function fetchRecentMusic(params = { page: 0, size: 20 }) {
    try {
      loading.value = true
      const response = await getRecentMusicApi(params)

      if (response.data) {
        musicList.value = response.data.content || []
        totalMusic.value = response.data.totalElements || 0
        return response.data
      }
      return null
    } catch (error) {
      console.error('Fetch recent music failed:', error)
      ElMessage.error(error.message || 'Failed to fetch recent music')
      return null
    } finally {
      loading.value = false
    }
  }

  /**
   * Get music by genre
   *
   * @param {number} genreId - Genre ID
   * @param {Object} params - Query parameters
   * @returns {Promise<Object|null>} Page data or null
   */
  async function fetchMusicByGenre(genreId, params = { page: 0, size: 20 }) {
    try {
      loading.value = true
      const response = await getMusicByGenreApi(genreId, params)

      if (response.data) {
        musicList.value = response.data.content || []
        totalMusic.value = response.data.totalElements || 0
        return response.data
      }
      return null
    } catch (error) {
      console.error('Fetch music by genre failed:', error)
      ElMessage.error(error.message || 'Failed to fetch music')
      return null
    } finally {
      loading.value = false
    }
  }

  /**
   * Get all public music
   *
   * @param {Object} params - Query parameters
   * @returns {Promise<Object|null>} Page data or null
   */
  async function fetchAllPublicMusic(params = { page: 0, size: 20, sort: 'createdAt,desc' }) {
    try {
      loading.value = true
      const response = await getAllPublicMusicApi(params)

      if (response.data) {
        musicList.value = response.data.content || []
        totalMusic.value = response.data.totalElements || 0
        return response.data
      }
      return null
    } catch (error) {
      console.error('Fetch public music failed:', error)
      ElMessage.error(error.message || 'Failed to fetch music')
      return null
    } finally {
      loading.value = false
    }
  }

  /**
   * Record music play
   *
   * @param {number} id - Music ID
   */
  async function recordPlay(id) {
    try {
      await recordPlayApi(id)
    } catch (error) {
      console.error('Record play failed:', error)
      // Don't show error message for play recording
    }
  }

  // ==================== Genre Actions ====================

  /**
   * Fetch all genres
   *
   * @returns {Promise<Array>} List of genres
   */
  async function fetchGenres() {
    try {
      const response = await getAllGenresApi()

      if (response.data) {
        genres.value = response.data
        return response.data
      }
      return []
    } catch (error) {
      console.error('Fetch genres failed:', error)
      ElMessage.error(error.message || 'Failed to fetch genres')
      return []
    }
  }

  /**
   * Fetch popular genres
   *
   * @returns {Promise<Array>} List of popular genres
   */
  async function fetchPopularGenres() {
    try {
      const response = await getPopularGenresApi()

      if (response.data) {
        genres.value = response.data
        return response.data
      }
      return []
    } catch (error) {
      console.error('Fetch popular genres failed:', error)
      ElMessage.error(error.message || 'Failed to fetch genres')
      return []
    }
  }

  /**
   * Fetch genre by ID
   *
   * @param {number} id - Genre ID
   * @returns {Promise<Object|null>} Genre data or null
   */
  async function fetchGenreById(id) {
    try {
      const response = await getGenreByIdApi(id)

      if (response.data) {
        currentGenre.value = response.data
        return response.data
      }
      return null
    } catch (error) {
      console.error('Fetch genre failed:', error)
      ElMessage.error(error.message || 'Failed to fetch genre')
      return null
    }
  }

  // ==================== Playlist Actions ====================

  /**
   * Create playlist
   *
   * @param {Object} data - Playlist data
   * @returns {Promise<Object|null>} Created playlist or null
   */
  async function createPlaylist(data) {
    try {
      const response = await createPlaylistApi(data)

      if (response.data) {
        ElMessage.success('Playlist created successfully!')
        playlists.value.unshift(response.data)
        totalPlaylists.value++
        return response.data
      }
      return null
    } catch (error) {
      console.error('Create playlist failed:', error)
      ElMessage.error(error.message || 'Failed to create playlist')
      return null
    }
  }

  /**
   * Get playlist detail
   *
   * @param {number} id - Playlist ID
   * @returns {Promise<Object|null>} Playlist details or null
   */
  async function fetchPlaylistDetail(id) {
    try {
      loading.value = true
      const response = await getPlaylistDetailApi(id)

      if (response.data) {
        currentPlaylist.value = response.data
        return response.data
      }
      return null
    } catch (error) {
      console.error('Fetch playlist failed:', error)
      ElMessage.error(error.message || 'Failed to fetch playlist')
      return null
    } finally {
      loading.value = false
    }
  }

  /**
   * Update playlist
   *
   * @param {number} id - Playlist ID
   * @param {Object} data - Update data
   * @returns {Promise<boolean>} Success status
   */
  async function updatePlaylist(id, data) {
    try {
      const response = await updatePlaylistApi(id, data)

      if (response.data) {
        ElMessage.success('Playlist updated successfully!')

        // Update in list
        const index = playlists.value.findIndex(p => p.id === id)
        if (index !== -1) {
          playlists.value[index] = response.data
        }

        // Update current
        if (currentPlaylist.value?.id === id) {
          currentPlaylist.value = { ...currentPlaylist.value, ...response.data }
        }

        return true
      }
      return false
    } catch (error) {
      console.error('Update playlist failed:', error)
      ElMessage.error(error.message || 'Failed to update playlist')
      return false
    }
  }

  /**
   * Delete playlist
   *
   * @param {number} id - Playlist ID
   * @returns {Promise<boolean>} Success status
   */
  async function deletePlaylist(id) {
    try {
      await deletePlaylistApi(id)

      ElMessage.success('Playlist deleted successfully!')

      // Remove from list
      playlists.value = playlists.value.filter(p => p.id !== id)
      totalPlaylists.value = Math.max(0, totalPlaylists.value - 1)

      // Clear current
      if (currentPlaylist.value?.id === id) {
        currentPlaylist.value = null
      }

      return true
    } catch (error) {
      console.error('Delete playlist failed:', error)
      ElMessage.error(error.message || 'Failed to delete playlist')
      return false
    }
  }

  /**
   * Get my playlists
   *
   * @param {Object} params - Query parameters
   * @returns {Promise<Object|null>} Page data or null
   */
  async function fetchMyPlaylists(params = { page: 0, size: 20 }) {
    try {
      loading.value = true
      const response = await getMyPlaylistsApi(params)

      if (response.data) {
        playlists.value = response.data.content || []
        totalPlaylists.value = response.data.totalElements || 0
        return response.data
      }
      return null
    } catch (error) {
      console.error('Fetch my playlists failed:', error)
      ElMessage.error(error.message || 'Failed to fetch playlists')
      return null
    } finally {
      loading.value = false
    }
  }

  /**
   * Get public playlists
   *
   * @param {Object} params - Query parameters
   * @returns {Promise<Object|null>} Page data or null
   */
  async function fetchPublicPlaylists(params = { page: 0, size: 20 }) {
    try {
      loading.value = true
      const response = await getPublicPlaylistsApi(params)

      if (response.data) {
        playlists.value = response.data.content || []
        totalPlaylists.value = response.data.totalElements || 0
        return response.data
      }
      return null
    } catch (error) {
      console.error('Fetch public playlists failed:', error)
      ElMessage.error(error.message || 'Failed to fetch playlists')
      return null
    } finally {
      loading.value = false
    }
  }

  /**
   * Search playlists
   *
   * @param {Object} params - Query parameters with keyword
   * @returns {Promise<Object|null>} Page data or null
   */
  async function searchPlaylists(params) {
    try {
      loading.value = true
      const response = await searchPlaylistsApi(params)

      if (response.data) {
        playlists.value = response.data.content || []
        totalPlaylists.value = response.data.totalElements || 0
        return response.data
      }
      return null
    } catch (error) {
      console.error('Search playlists failed:', error)
      ElMessage.error(error.message || 'Failed to search playlists')
      return null
    } finally {
      loading.value = false
    }
  }

  /**
   * Get popular playlists
   *
   * @param {Object} params - Query parameters
   * @returns {Promise<Object|null>} Page data or null
   */
  async function fetchPopularPlaylists(params = { page: 0, size: 20 }) {
    try {
      loading.value = true
      const response = await getPopularPlaylistsApi(params)

      if (response.data) {
        playlists.value = response.data.content || []
        totalPlaylists.value = response.data.totalElements || 0
        return response.data
      }
      return null
    } catch (error) {
      console.error('Fetch popular playlists failed:', error)
      ElMessage.error(error.message || 'Failed to fetch playlists')
      return null
    } finally {
      loading.value = false
    }
  }

  /**
   * Add music to playlist
   *
   * @param {number} playlistId - Playlist ID
   * @param {number} musicId - Music ID
   * @returns {Promise<boolean>} Success status
   */
  async function addMusicToPlaylist(playlistId, musicId) {
    try {
      await addMusicToPlaylistApi(playlistId, musicId)

      ElMessage.success('Music added to playlist!')

      // Update music count if current playlist
      if (currentPlaylist.value?.id === playlistId) {
        currentPlaylist.value.musicCount++
      }

      return true
    } catch (error) {
      console.error('Add music to playlist failed:', error)
      ElMessage.error(error.message || 'Failed to add music')
      return false
    }
  }

  /**
   * Remove music from playlist
   *
   * @param {number} playlistId - Playlist ID
   * @param {number} musicId - Music ID
   * @returns {Promise<boolean>} Success status
   */
  async function removeMusicFromPlaylist(playlistId, musicId) {
    try {
      await removeMusicFromPlaylistApi(playlistId, musicId)

      ElMessage.success('Music removed from playlist!')

      // Update current playlist if loaded
      if (currentPlaylist.value?.id === playlistId) {
        currentPlaylist.value.musicList = currentPlaylist.value.musicList.filter(
          m => m.id !== musicId
        )
        currentPlaylist.value.musicCount = Math.max(0, currentPlaylist.value.musicCount - 1)
      }

      return true
    } catch (error) {
      console.error('Remove music from playlist failed:', error)
      ElMessage.error(error.message || 'Failed to remove music')
      return false
    }
  }

  /**
   * Reorder playlist music
   *
   * @param {number} playlistId - Playlist ID
   * @param {Array<number>} musicIds - Ordered music IDs
   * @returns {Promise<boolean>} Success status
   */
  async function reorderPlaylistMusic(playlistId, musicIds) {
    try {
      await reorderPlaylistMusicApi(playlistId, musicIds)

      ElMessage.success('Playlist reordered successfully!')
      return true
    } catch (error) {
      console.error('Reorder playlist failed:', error)
      ElMessage.error(error.message || 'Failed to reorder playlist')
      return false
    }
  }

  // Clear all data
  function clearAll() {
    musicList.value = []
    currentMusic.value = null
    totalMusic.value = 0
    genres.value = []
    currentGenre.value = null
    playlists.value = []
    currentPlaylist.value = null
    totalPlaylists.value = 0
  }

  return {
    // State
    musicList,
    currentMusic,
    totalMusic,
    loading,
    genres,
    currentGenre,
    playlists,
    currentPlaylist,
    totalPlaylists,

    // Getters
    hasMusicList,
    hasGenres,
    hasPlaylists,

    // Music Actions
    uploadMusic,
    fetchMusicById,
    updateMusic,
    deleteMusic,
    fetchMyMusic,
    searchMusic,
    fetchPopularMusic,
    fetchRecentMusic,
    fetchMusicByGenre,
    fetchAllPublicMusic,
    recordPlay,

    // Genre Actions
    fetchGenres,
    fetchPopularGenres,
    fetchGenreById,

    // Playlist Actions
    createPlaylist,
    fetchPlaylistDetail,
    updatePlaylist,
    deletePlaylist,
    fetchMyPlaylists,
    fetchPublicPlaylists,
    searchPlaylists,
    fetchPopularPlaylists,
    addMusicToPlaylist,
    removeMusicFromPlaylist,
    reorderPlaylistMusic,

    // Utility
    clearAll
  }
})
