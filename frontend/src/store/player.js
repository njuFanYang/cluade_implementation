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
import { getMusicStreamUrl } from '@/api/music'
import { useMusicStore } from './music'

export const usePlayerStore = defineStore('player', () => {
  // State
  const currentMusic = ref(null)
  const playlist = ref([])
  const currentIndex = ref(-1)

  const isPlaying = ref(false)
  const isPaused = ref(false)
  const isLoading = ref(false)

  const volume = ref(parseFloat(localStorage.getItem('playerVolume') || '0.7'))
  const isMuted = ref(false)
  const previousVolume = ref(0.7)

  const currentTime = ref(0)
  const duration = ref(0)
  const buffered = ref(0)

  const playMode = ref(localStorage.getItem('playerMode') || 'list') // list, shuffle, repeat-one
  const showLyrics = ref(false)

  // Audio element reference (set by MusicPlayer component)
  const audioElement = ref(null)

  // Getters
  const hasMusic = computed(() => currentMusic.value !== null)
  const hasPlaylist = computed(() => playlist.value.length > 0)
  const progress = computed(() => {
    if (duration.value === 0) return 0
    return (currentTime.value / duration.value) * 100
  })
  const bufferedProgress = computed(() => {
    if (duration.value === 0) return 0
    return (buffered.value / duration.value) * 100
  })
  const canPlayPrevious = computed(() => {
    if (playMode.value === 'repeat-one') return true
    return currentIndex.value > 0
  })
  const canPlayNext = computed(() => {
    if (playMode.value === 'repeat-one') return true
    if (playMode.value === 'shuffle') return true
    return currentIndex.value < playlist.value.length - 1
  })
  const formattedCurrentTime = computed(() => formatTime(currentTime.value))
  const formattedDuration = computed(() => formatTime(duration.value))

  // Set audio element reference
  function setAudioElement(element) {
    audioElement.value = element
  }

  // Play a music track
  async function play(music, newPlaylist = null) {
    try {
      isLoading.value = true

      if (newPlaylist) {
        playlist.value = newPlaylist
        currentIndex.value = newPlaylist.findIndex(m => m.id === music.id)
      } else if (!playlist.value.some(m => m.id === music.id)) {
        playlist.value.push(music)
        currentIndex.value = playlist.value.length - 1
      } else {
        currentIndex.value = playlist.value.findIndex(m => m.id === music.id)
      }

      currentMusic.value = music

      if (audioElement.value) {
        const streamUrl = getMusicStreamUrl(music.id)
        audioElement.value.src = streamUrl
        await audioElement.value.play()
        isPlaying.value = true
        isPaused.value = false

        const musicStore = useMusicStore()
        musicStore.recordPlay(music.id)
      }
    } catch (error) {
      console.error('Play failed:', error)
      isPlaying.value = false
    } finally {
      isLoading.value = false
    }
  }

  // Resume playback
  async function resume() {
    if (audioElement.value && isPaused.value) {
      try {
        await audioElement.value.play()
        isPlaying.value = true
        isPaused.value = false
      } catch (error) {
        console.error('Resume failed:', error)
      }
    }
  }

  // Pause playback
  function pause() {
    if (audioElement.value && isPlaying.value) {
      audioElement.value.pause()
      isPlaying.value = false
      isPaused.value = true
    }
  }

  // Toggle play/pause
  function togglePlay() {
    if (isPlaying.value) {
      pause()
    } else if (isPaused.value) {
      resume()
    } else if (currentMusic.value) {
      play(currentMusic.value)
    }
  }

  // Play next track
  function playNext() {
    if (!hasPlaylist.value) return

    let nextIndex
    if (playMode.value === 'repeat-one') {
      nextIndex = currentIndex.value
    } else if (playMode.value === 'shuffle') {
      do {
        nextIndex = Math.floor(Math.random() * playlist.value.length)
      } while (nextIndex === currentIndex.value && playlist.value.length > 1)
    } else {
      nextIndex = currentIndex.value + 1
      if (nextIndex >= playlist.value.length) nextIndex = 0
    }

    if (playlist.value[nextIndex]) {
      play(playlist.value[nextIndex])
    }
  }

  // Play previous track
  function playPrevious() {
    if (!hasPlaylist.value) return

    let prevIndex
    if (playMode.value === 'repeat-one') {
      prevIndex = currentIndex.value
    } else if (playMode.value === 'shuffle') {
      do {
        prevIndex = Math.floor(Math.random() * playlist.value.length)
      } while (prevIndex === currentIndex.value && playlist.value.length > 1)
    } else {
      prevIndex = currentIndex.value - 1
      if (prevIndex < 0) prevIndex = playlist.value.length - 1
    }

    if (playlist.value[prevIndex]) {
      play(playlist.value[prevIndex])
    }
  }

  // Seek to position
  function seek(time) {
    if (audioElement.value) {
      audioElement.value.currentTime = time
      currentTime.value = time
    }
  }

  // Set volume
  function setVolume(value) {
    volume.value = Math.max(0, Math.min(1, value))
    if (audioElement.value) {
      audioElement.value.volume = volume.value
    }
    localStorage.setItem('playerVolume', volume.value.toString())
    if (volume.value > 0) isMuted.value = false
  }

  // Toggle mute
  function toggleMute() {
    if (isMuted.value) {
      setVolume(previousVolume.value)
      isMuted.value = false
    } else {
      previousVolume.value = volume.value
      setVolume(0)
      isMuted.value = true
    }
  }

  // Set play mode
  function setPlayMode(mode) {
    playMode.value = mode
    localStorage.setItem('playerMode', mode)
  }

  // Cycle through play modes
  function cyclePlayMode() {
    const modes = ['list', 'shuffle', 'repeat-one']
    const currentModeIndex = modes.indexOf(playMode.value)
    const nextModeIndex = (currentModeIndex + 1) % modes.length
    setPlayMode(modes[nextModeIndex])
  }

  // Add music to playlist
  function addToPlaylist(music) {
    if (!playlist.value.some(m => m.id === music.id)) {
      playlist.value.push(music)
    }
  }

  // Remove music from playlist
  function removeFromPlaylist(musicId) {
    const index = playlist.value.findIndex(m => m.id === musicId)
    if (index !== -1) {
      playlist.value.splice(index, 1)
      if (index < currentIndex.value) {
        currentIndex.value--
      } else if (index === currentIndex.value) {
        if (playlist.value.length > 0) {
          if (currentIndex.value >= playlist.value.length) currentIndex.value = 0
          play(playlist.value[currentIndex.value])
        } else {
          stop()
        }
      }
    }
  }

  // Clear playlist
  function clearPlaylist() {
    playlist.value = []
    currentIndex.value = -1
    stop()
  }

  // Stop playback
  function stop() {
    if (audioElement.value) {
      audioElement.value.pause()
      audioElement.value.currentTime = 0
    }
    isPlaying.value = false
    isPaused.value = false
    currentTime.value = 0
  }

  // Toggle lyrics display
  function toggleLyrics() {
    showLyrics.value = !showLyrics.value
  }

  // Update current time
  function updateCurrentTime(time) {
    currentTime.value = time
  }

  // Update duration
  function updateDuration(value) {
    duration.value = value
  }

  // Update buffered
  function updateBuffered(value) {
    buffered.value = value
  }

  // Handle track ended
  function onTrackEnded() {
    if (playMode.value === 'repeat-one') {
      seek(0)
      play(currentMusic.value)
    } else {
      playNext()
    }
  }

  // Format time in MM:SS format
  function formatTime(seconds) {
    if (isNaN(seconds) || seconds === Infinity) return '00:00'
    const mins = Math.floor(seconds / 60)
    const secs = Math.floor(seconds % 60)
    return `${mins.toString().padStart(2, '0')}:${secs.toString().padStart(2, '0')}`
  }

  return {
    // State
    currentMusic,
    playlist,
    currentIndex,
    isPlaying,
    isPaused,
    isLoading,
    volume,
    isMuted,
    currentTime,
    duration,
    buffered,
    playMode,
    showLyrics,
    audioElement,

    // Getters
    hasMusic,
    hasPlaylist,
    progress,
    bufferedProgress,
    canPlayPrevious,
    canPlayNext,
    formattedCurrentTime,
    formattedDuration,

    // Actions
    setAudioElement,
    play,
    resume,
    pause,
    togglePlay,
    playNext,
    playPrevious,
    seek,
    setVolume,
    toggleMute,
    setPlayMode,
    cyclePlayMode,
    addToPlaylist,
    removeFromPlaylist,
    clearPlaylist,
    stop,
    toggleLyrics,
    updateCurrentTime,
    updateDuration,
    updateBuffered,
    onTrackEnded
  }
})
