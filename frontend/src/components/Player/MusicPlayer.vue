<template>
  <div class="music-player" v-if="playerStore.hasMusic">
    <!-- Hidden audio element -->
    <audio
      ref="audioRef"
      @timeupdate="onTimeUpdate"
      @durationchange="onDurationChange"
      @ended="onEnded"
      @loadstart="onLoadStart"
      @canplay="onCanPlay"
      @error="onError"
      @progress="onProgress"
    />

    <div class="player-container">
      <!-- Left: Music Info -->
      <div class="player-left">
        <div class="music-cover">
          <img
            :src="playerStore.currentMusic?.coverImage || '/default-cover.png'"
            :alt="playerStore.currentMusic?.title"
            @error="onImageError"
          />
        </div>
        <div class="music-info">
          <div class="music-title">{{ playerStore.currentMusic?.title || 'No music playing' }}</div>
          <div class="music-artist">{{ playerStore.currentMusic?.artist || 'Unknown' }}</div>
        </div>
        <el-button
          :icon="playerStore.showLyrics ? View : Hide"
          circle
          text
          @click="playerStore.toggleLyrics"
          title="Toggle Lyrics"
        />
      </div>

      <!-- Center: Controls -->
      <div class="player-center">
        <!-- Control Buttons -->
        <div class="player-controls">
          <el-button
            :icon="RefreshLeft"
            circle
            @click="playerStore.playPrevious"
            :disabled="!playerStore.canPlayPrevious"
            title="Previous"
          />
          <el-button
            :icon="playerStore.isPlaying ? VideoPause : VideoPlay"
            circle
            type="primary"
            size="large"
            @click="playerStore.togglePlay"
            :loading="playerStore.isLoading"
            title="Play/Pause"
          />
          <el-button
            :icon="RefreshRight"
            circle
            @click="playerStore.playNext"
            :disabled="!playerStore.canPlayNext"
            title="Next"
          />
        </div>

        <!-- Progress Bar -->
        <div class="player-progress">
          <span class="time-current">{{ playerStore.formattedCurrentTime }}</span>
          <el-slider
            v-model="localProgress"
            :show-tooltip="false"
            @change="onProgressChange"
            class="progress-slider"
          />
          <span class="time-duration">{{ playerStore.formattedDuration }}</span>
        </div>
      </div>

      <!-- Right: Volume & Actions -->
      <div class="player-right">
        <!-- Play Mode -->
        <el-button
          :icon="playModeIcon"
          circle
          text
          @click="playerStore.cyclePlayMode"
          :title="playModeTitle"
        />

        <!-- Volume Control -->
        <div class="volume-control">
          <el-button
            :icon="playerStore.isMuted ? Mute : Microphone"
            circle
            text
            @click="playerStore.toggleMute"
            title="Mute/Unmute"
          />
          <el-slider
            v-model="localVolume"
            :show-tooltip="false"
            @change="onVolumeChange"
            class="volume-slider"
          />
        </div>

        <!-- Playlist -->
        <el-popover
          placement="top"
          :width="300"
          trigger="click"
        >
          <template #reference>
            <el-badge :value="playerStore.playlist.length" :max="99">
              <el-button :icon="List" circle text title="Playlist" />
            </el-badge>
          </template>
          <div class="playlist-panel">
            <div class="playlist-header">
              <span>Playlist ({{ playerStore.playlist.length }})</span>
              <el-button
                size="small"
                text
                @click="playerStore.clearPlaylist"
                :disabled="playerStore.playlist.length === 0"
              >
                Clear All
              </el-button>
            </div>
            <el-scrollbar max-height="300px">
              <div
                v-for="(music, index) in playerStore.playlist"
                :key="music.id"
                class="playlist-item"
                :class="{ active: index === playerStore.currentIndex }"
                @click="playerStore.play(music)"
              >
                <el-icon v-if="index === playerStore.currentIndex && playerStore.isPlaying">
                  <VideoPlay />
                </el-icon>
                <span class="item-index">{{ index + 1 }}</span>
                <div class="item-info">
                  <div class="item-title">{{ music.title }}</div>
                  <div class="item-artist">{{ music.artist }}</div>
                </div>
                <el-button
                  :icon="Delete"
                  size="small"
                  text
                  @click.stop="playerStore.removeFromPlaylist(music.id)"
                />
              </div>
              <el-empty
                v-if="playerStore.playlist.length === 0"
                description="No music in playlist"
                :image-size="60"
              />
            </el-scrollbar>
          </div>
        </el-popover>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, watch, onMounted } from 'vue'
import { usePlayerStore } from '@/store/player'
import {
  VideoPlay,
  VideoPause,
  RefreshLeft,
  RefreshRight,
  Microphone,
  Mute,
  List,
  Delete,
  View,
  Hide,
  Refresh,
  Sort
} from '@element-plus/icons-vue'
import { ElMessage } from 'element-plus'

const playerStore = usePlayerStore()

// Refs
const audioRef = ref(null)
const localProgress = ref(0)
const localVolume = ref(playerStore.volume * 100)

// Computed
const playModeIcon = computed(() => {
  switch (playerStore.playMode) {
    case 'shuffle':
      return Refresh
    case 'repeat-one':
      return Sort
    default:
      return List
  }
})

const playModeTitle = computed(() => {
  switch (playerStore.playMode) {
    case 'shuffle':
      return 'Shuffle'
    case 'repeat-one':
      return 'Repeat One'
    default:
      return 'List Loop'
  }
})

// Set audio element reference on mount
onMounted(() => {
  if (audioRef.value) {
    playerStore.setAudioElement(audioRef.value)
    audioRef.value.volume = playerStore.volume
  }
})

// Watch for external volume changes
watch(() => playerStore.volume, (newVolume) => {
  localVolume.value = newVolume * 100
})

// Watch for external progress changes
watch(() => playerStore.progress, (newProgress) => {
  localProgress.value = newProgress
})

// Audio event handlers
function onTimeUpdate(e) {
  playerStore.updateCurrentTime(e.target.currentTime)
}

function onDurationChange(e) {
  playerStore.updateDuration(e.target.duration)
}

function onEnded() {
  playerStore.onTrackEnded()
}

function onLoadStart() {
  playerStore.isLoading = true
}

function onCanPlay() {
  playerStore.isLoading = false
}

function onError(e) {
  console.error('Audio error:', e)
  ElMessage.error('Failed to load audio')
  playerStore.isLoading = false
}

function onProgress(e) {
  if (e.target.buffered.length > 0) {
    const buffered = e.target.buffered.end(e.target.buffered.length - 1)
    playerStore.updateBuffered(buffered)
  }
}

// User interaction handlers
function onProgressChange(value) {
  const time = (value / 100) * playerStore.duration
  playerStore.seek(time)
}

function onVolumeChange(value) {
  playerStore.setVolume(value / 100)
}

function onImageError(e) {
  e.target.src = '/default-cover.png'
}
</script>

<style scoped lang="scss">
.music-player {
  position: fixed;
  bottom: 0;
  left: 0;
  right: 0;
  height: 80px;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  box-shadow: 0 -2px 10px rgba(0, 0, 0, 0.1);
  z-index: 1000;
  backdrop-filter: blur(10px);
}

.player-container {
  display: flex;
  align-items: center;
  justify-content: space-between;
  height: 100%;
  padding: 0 20px;
  max-width: 1400px;
  margin: 0 auto;
}

// Left Section
.player-left {
  display: flex;
  align-items: center;
  gap: 12px;
  flex: 1;
  min-width: 0;

  .music-cover {
    width: 50px;
    height: 50px;
    border-radius: 8px;
    overflow: hidden;
    flex-shrink: 0;
    box-shadow: 0 2px 8px rgba(0, 0, 0, 0.2);

    img {
      width: 100%;
      height: 100%;
      object-fit: cover;
    }
  }

  .music-info {
    flex: 1;
    min-width: 0;
    color: white;

    .music-title {
      font-size: 14px;
      font-weight: 500;
      white-space: nowrap;
      overflow: hidden;
      text-overflow: ellipsis;
      margin-bottom: 4px;
    }

    .music-artist {
      font-size: 12px;
      opacity: 0.8;
      white-space: nowrap;
      overflow: hidden;
      text-overflow: ellipsis;
    }
  }
}

// Center Section
.player-center {
  flex: 2;
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 8px;
  padding: 0 20px;

  .player-controls {
    display: flex;
    align-items: center;
    gap: 12px;
  }

  .player-progress {
    display: flex;
    align-items: center;
    gap: 12px;
    width: 100%;
    max-width: 500px;
    color: white;
    font-size: 12px;

    .progress-slider {
      flex: 1;

      :deep(.el-slider__runway) {
        background-color: rgba(255, 255, 255, 0.3);
      }

      :deep(.el-slider__bar) {
        background-color: white;
      }

      :deep(.el-slider__button) {
        border-color: white;
      }
    }
  }
}

// Right Section
.player-right {
  display: flex;
  align-items: center;
  gap: 8px;
  flex: 1;
  justify-content: flex-end;

  .volume-control {
    display: flex;
    align-items: center;
    gap: 8px;
    width: 120px;

    .volume-slider {
      flex: 1;

      :deep(.el-slider__runway) {
        background-color: rgba(255, 255, 255, 0.3);
      }

      :deep(.el-slider__bar) {
        background-color: white;
      }

      :deep(.el-slider__button) {
        border-color: white;
      }
    }
  }
}

// Playlist Panel
.playlist-panel {
  .playlist-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
    padding-bottom: 12px;
    margin-bottom: 12px;
    border-bottom: 1px solid #eee;
    font-weight: 500;
  }

  .playlist-item {
    display: flex;
    align-items: center;
    gap: 8px;
    padding: 8px;
    border-radius: 4px;
    cursor: pointer;
    transition: background-color 0.2s;

    &:hover {
      background-color: #f5f7fa;
    }

    &.active {
      background-color: #ecf5ff;
      color: #409eff;
    }

    .item-index {
      font-size: 12px;
      color: #909399;
      width: 20px;
      text-align: center;
    }

    .item-info {
      flex: 1;
      min-width: 0;

      .item-title {
        font-size: 13px;
        font-weight: 500;
        white-space: nowrap;
        overflow: hidden;
        text-overflow: ellipsis;
        margin-bottom: 2px;
      }

      .item-artist {
        font-size: 12px;
        color: #909399;
        white-space: nowrap;
        overflow: hidden;
        text-overflow: ellipsis;
      }
    }
  }
}

// Button overrides for white theme
:deep(.el-button) {
  color: white;
  border-color: rgba(255, 255, 255, 0.3);

  &:hover {
    color: white;
    border-color: white;
    background-color: rgba(255, 255, 255, 0.1);
  }

  &.is-disabled {
    color: rgba(255, 255, 255, 0.5);
    border-color: rgba(255, 255, 255, 0.2);
  }
}

:deep(.el-button--primary) {
  background-color: white;
  color: #667eea;
  border-color: white;

  &:hover {
    background-color: rgba(255, 255, 255, 0.9);
  }
}

// Responsive
@media (max-width: 768px) {
  .music-player {
    height: auto;
    padding: 8px 0;
  }

  .player-container {
    flex-wrap: wrap;
    padding: 0 12px;
  }

  .player-left,
  .player-right {
    flex: auto;
  }

  .player-center {
    width: 100%;
    order: -1;
    padding: 8px 0;
  }

  .volume-control {
    display: none !important;
  }
}
</style>
