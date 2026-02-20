/**
 * Music player store module
 *
 * @description Manages music player state including playback control,
 *              playlist, and current track information.
 *
 * @author MusicShare Team
 * @version 1.0.0
 */

import { defineStore } from 'pinia'
import { ref, computed } from 'vue'

export const usePlayerStore = defineStore('player', () => {
  // State
  const currentMusic = ref(null)        // Current playing music
  const playlist = ref([])              // Current playlist
  const currentIndex = ref(0)           // Current track index
  const isPlaying = ref(false)          // Playing status
  const volume = ref(0.7)               // Volume (0-1)
  const currentTime = ref(0)            // Current playback time (seconds)
  const duration = ref(0)               // Track duration (seconds)
  const playMode = ref('loop')          // Play mode: 'loop', 'single', 'shuffle'

  // Getters
  const progress = computed(() => {
    return duration.value > 0 ? (currentTime.value / duration.value) * 100 : 0
  })

  const hasNext = computed(() => {
    return playlist.value.length > 0 && currentIndex.value < playlist.value.length - 1
  })

  const hasPrevious = computed(() => {
    return playlist.value.length > 0 && currentIndex.value > 0
  })

  /**
   * Play a specific music track
   *
   * @param {Object} music - Music track object
   */
  function playMusic(music) {
    currentMusic.value = music
    isPlaying.value = true
  }

  /**
   * Toggle play/pause
   */
  function togglePlay() {
    isPlaying.value = !isPlaying.value
  }

  /**
   * Play next track
   */
  function playNext() {
    if (playMode.value === 'shuffle') {
      currentIndex.value = Math.floor(Math.random() * playlist.value.length)
    } else if (hasNext.value) {
      currentIndex.value++
    } else if (playMode.value === 'loop') {
      currentIndex.value = 0
    }

    if (playlist.value[currentIndex.value]) {
      playMusic(playlist.value[currentIndex.value])
    }
  }

  /**
   * Play previous track
   */
  function playPrevious() {
    if (hasPrevious.value) {
      currentIndex.value--
      playMusic(playlist.value[currentIndex.value])
    }
  }

  /**
   * Set playlist and play from specific index
   *
   * @param {Array} list - Music list
   * @param {number} index - Starting index
   */
  function setPlaylist(list, index = 0) {
    playlist.value = list
    currentIndex.value = index
    if (list[index]) {
      playMusic(list[index])
    }
  }

  /**
   * Add track to playlist
   *
   * @param {Object} music - Music track to add
   */
  function addToPlaylist(music) {
    const exists = playlist.value.findIndex(item => item.id === music.id)
    if (exists === -1) {
      playlist.value.push(music)
    }
  }

  /**
   * Remove track from playlist
   *
   * @param {number} index - Index to remove
   */
  function removeFromPlaylist(index) {
    playlist.value.splice(index, 1)
    if (index < currentIndex.value) {
      currentIndex.value--
    }
  }

  /**
   * Clear playlist
   */
  function clearPlaylist() {
    playlist.value = []
    currentIndex.value = 0
  }

  /**
   * Set volume
   *
   * @param {number} val - Volume value (0-1)
   */
  function setVolume(val) {
    volume.value = Math.max(0, Math.min(1, val))
  }

  /**
   * Set current time
   *
   * @param {number} time - Time in seconds
   */
  function setCurrentTime(time) {
    currentTime.value = time
  }

  /**
   * Set duration
   *
   * @param {number} time - Duration in seconds
   */
  function setDuration(time) {
    duration.value = time
  }

  /**
   * Set play mode
   *
   * @param {string} mode - Play mode: 'loop', 'single', 'shuffle'
   */
  function setPlayMode(mode) {
    playMode.value = mode
  }

  return {
    // State
    currentMusic,
    playlist,
    currentIndex,
    isPlaying,
    volume,
    currentTime,
    duration,
    playMode,

    // Getters
    progress,
    hasNext,
    hasPrevious,

    // Actions
    playMusic,
    togglePlay,
    playNext,
    playPrevious,
    setPlaylist,
    addToPlaylist,
    removeFromPlaylist,
    clearPlaylist,
    setVolume,
    setCurrentTime,
    setDuration,
    setPlayMode
  }
})
