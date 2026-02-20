<template>
  <div class="music-detail-page">
    <el-skeleton :loading="loading" animated>
      <template #template>
        <div class="detail-header">
          <el-skeleton-item variant="image" class="header-cover" />
          <div class="header-info">
            <el-skeleton-item variant="h1" style="width: 60%" />
            <el-skeleton-item variant="text" style="width: 40%" />
            <el-skeleton-item variant="text" style="width: 30%" />
          </div>
        </div>
      </template>

      <template #default>
        <div v-if="music" class="detail-container">
          <!-- Header Section -->
          <div class="detail-header">
            <div class="header-cover">
              <img
                :src="music.coverImage || '/default-cover.png'"
                :alt="music.title"
                @error="onImageError"
              />
              <div class="cover-overlay">
                <el-button
                  :icon="isPlaying && isCurrentMusic ? VideoPause : VideoPlay"
                  circle
                  size="large"
                  type="primary"
                  @click="handlePlayPause"
                />
              </div>
            </div>

            <div class="header-info">
              <div class="music-meta">
                <el-tag v-if="music.genre" type="info" size="small">
                  {{ music.genre.name }}
                </el-tag>
                <el-tag v-if="!music.isPublic" type="warning" size="small">
                  Private
                </el-tag>
              </div>

              <h1 class="music-title">{{ music.title }}</h1>
              <h2 class="music-artist">{{ music.artist }}</h2>

              <div class="music-stats">
                <span class="stat-item">
                  <el-icon><Headset /></el-icon>
                  {{ formatCount(music.playCount || 0) }} plays
                </span>
                <span class="stat-item">
                  <el-icon><Star /></el-icon>
                  {{ formatCount(music.likeCount || 0) }} likes
                </span>
                <span class="stat-item">
                  <el-icon><Download /></el-icon>
                  {{ formatCount(music.downloadCount || 0) }} downloads
                </span>
              </div>

              <div class="action-buttons">
                <el-button
                  type="primary"
                  :icon="VideoPlay"
                  @click="handlePlay"
                  size="large"
                >
                  Play Now
                </el-button>
                <el-button
                  :icon="FolderAdd"
                  @click="handleAddToPlaylist"
                  size="large"
                >
                  Add to Playlist
                </el-button>
                <el-button
                  :icon="isLiked ? StarFilled : Star"
                  @click="handleLike"
                  :class="{ liked: isLiked }"
                  size="large"
                >
                  {{ isLiked ? 'Liked' : 'Like' }}
                </el-button>
                <el-button
                  :icon="Download"
                  @click="handleDownload"
                  size="large"
                >
                  Download
                </el-button>

                <el-dropdown trigger="click">
                  <el-button :icon="More" size="large" />
                  <template #dropdown>
                    <el-dropdown-menu>
                      <el-dropdown-item :icon="Share" @click="handleShare">
                        Share
                      </el-dropdown-item>
                      <el-dropdown-item
                        v-if="canEdit"
                        :icon="Edit"
                        divided
                        @click="handleEdit"
                      >
                        Edit
                      </el-dropdown-item>
                      <el-dropdown-item
                        v-if="canDelete"
                        :icon="Delete"
                        divided
                        @click="handleDelete"
                      >
                        Delete
                      </el-dropdown-item>
                    </el-dropdown-menu>
                  </template>
                </el-dropdown>
              </div>
            </div>
          </div>

          <!-- Content Section -->
          <div class="detail-content">
            <el-row :gutter="24">
              <!-- Main Content -->
              <el-col :span="16">
                <el-card class="info-card">
                  <el-tabs v-model="activeTab">
                    <el-tab-pane label="Information" name="info">
                      <el-descriptions :column="2" border>
                        <el-descriptions-item label="Duration">
                          {{ formatDuration(music.duration) }}
                        </el-descriptions-item>
                        <el-descriptions-item label="Genre">
                          {{ music.genre?.name || 'N/A' }}
                        </el-descriptions-item>
                        <el-descriptions-item label="Album" v-if="music.album">
                          {{ music.album.title }}
                        </el-descriptions-item>
                        <el-descriptions-item label="Release Date" v-if="music.releaseDate">
                          {{ formatDate(music.releaseDate) }}
                        </el-descriptions-item>
                        <el-descriptions-item label="File Format">
                          {{ music.fileFormat?.toUpperCase() || 'N/A' }}
                        </el-descriptions-item>
                        <el-descriptions-item label="File Size">
                          {{ formatFileSize(music.fileSize) }}
                        </el-descriptions-item>
                        <el-descriptions-item label="Uploader">
                          {{ music.uploader?.username || 'Unknown' }}
                        </el-descriptions-item>
                        <el-descriptions-item label="Upload Date">
                          {{ formatDate(music.createdAt) }}
                        </el-descriptions-item>
                      </el-descriptions>

                      <div v-if="music.description" class="description-section">
                        <h3>Description</h3>
                        <p>{{ music.description }}</p>
                      </div>
                    </el-tab-pane>

                    <el-tab-pane label="Lyrics" name="lyrics">
                      <div class="lyrics-section">
                        <div v-if="music.lyrics" class="lyrics-content">
                          <pre>{{ music.lyrics }}</pre>
                        </div>
                        <el-empty
                          v-else
                          description="No lyrics available"
                          :image-size="100"
                        />
                      </div>
                    </el-tab-pane>

                    <el-tab-pane label="Comments" name="comments">
                      <div class="comments-section">
                        <el-empty
                          description="Comments feature coming soon"
                          :image-size="100"
                        />
                      </div>
                    </el-tab-pane>
                  </el-tabs>
                </el-card>
              </el-col>

              <!-- Sidebar -->
              <el-col :span="8">
                <!-- Uploader Info -->
                <el-card class="sidebar-card" v-if="music.uploader">
                  <template #header>
                    <div class="card-header">
                      <el-icon><User /></el-icon>
                      <span>Uploader</span>
                    </div>
                  </template>
                  <div class="uploader-info">
                    <el-avatar :size="60" :src="music.uploader.avatar">
                      {{ music.uploader.username?.charAt(0).toUpperCase() }}
                    </el-avatar>
                    <div class="uploader-details">
                      <div class="uploader-name">{{ music.uploader.username }}</div>
                      <div class="uploader-role">{{ music.uploader.role }}</div>
                    </div>
                  </div>
                </el-card>

                <!-- Related Music -->
                <el-card class="sidebar-card">
                  <template #header>
                    <div class="card-header">
                      <el-icon><Collection /></el-icon>
                      <span>Related Music</span>
                    </div>
                  </template>
                  <div v-loading="loadingRelated" class="related-music">
                    <div
                      v-for="item in relatedMusic"
                      :key="item.id"
                      class="related-item"
                      @click="router.push(`/music/${item.id}`)"
                    >
                      <img
                        :src="item.coverImage || '/default-cover.png'"
                        :alt="item.title"
                        @error="onImageError"
                      />
                      <div class="related-info">
                        <div class="related-title">{{ item.title }}</div>
                        <div class="related-artist">{{ item.artist }}</div>
                      </div>
                    </div>
                    <el-empty
                      v-if="!loadingRelated && relatedMusic.length === 0"
                      description="No related music"
                      :image-size="80"
                    />
                  </div>
                </el-card>
              </el-col>
            </el-row>
          </div>
        </div>

        <el-empty
          v-else-if="!loading"
          description="Music not found"
          :image-size="200"
        >
          <el-button type="primary" @click="router.back()">Go Back</el-button>
        </el-empty>
      </template>
    </el-skeleton>
  </div>
</template>

<script setup>
import { ref, computed, onMounted, watch } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { useMusicStore } from '@/store/music'
import { usePlayerStore } from '@/store/player'
import { useUserStore } from '@/store/user'
import {
  VideoPlay,
  VideoPause,
  FolderAdd,
  Star,
  StarFilled,
  Download,
  More,
  Share,
  Edit,
  Delete,
  Headset,
  User,
  Collection
} from '@element-plus/icons-vue'
import { ElMessage, ElMessageBox } from 'element-plus'

const route = useRoute()
const router = useRouter()
const musicStore = useMusicStore()
const playerStore = usePlayerStore()
const userStore = useUserStore()

// State
const loading = ref(true)
const loadingRelated = ref(false)
const music = ref(null)
const relatedMusic = ref([])
const activeTab = ref('info')
const isLiked = ref(false)

// Computed
const musicId = computed(() => parseInt(route.params.id))

const isPlaying = computed(() => playerStore.isPlaying)

const isCurrentMusic = computed(() => {
  return playerStore.currentMusic?.id === music.value?.id
})

const canEdit = computed(() => {
  if (!userStore.isLoggedIn || !music.value) return false
  return userStore.currentUser?.id === music.value.uploader?.id ||
         userStore.hasRole('ADMIN')
})

const canDelete = computed(() => canEdit.value)

// Methods
async function loadMusicDetail() {
  try {
    loading.value = true
    const response = await musicStore.fetchMusicDetail(musicId.value)
    music.value = response
  } catch (error) {
    console.error('Failed to load music detail:', error)
    ElMessage.error('Failed to load music detail')
    music.value = null
  } finally {
    loading.value = false
  }
}

async function loadRelatedMusic() {
  if (!music.value?.genre?.id) return

  try {
    loadingRelated.value = true
    const response = await musicStore.fetchMusicByGenre(music.value.genre.id, {
      page: 0,
      size: 5
    })

    // Filter out current music
    relatedMusic.value = response.content.filter(m => m.id !== music.value.id).slice(0, 5)
  } catch (error) {
    console.error('Failed to load related music:', error)
  } finally {
    loadingRelated.value = false
  }
}

function handlePlay() {
  playerStore.play(music.value)
}

function handlePlayPause() {
  if (isCurrentMusic.value) {
    playerStore.togglePlay()
  } else {
    handlePlay()
  }
}

function handleAddToPlaylist() {
  if (!userStore.isLoggedIn) {
    ElMessage.warning('Please login first')
    return
  }

  playerStore.addToPlaylist(music.value)
  ElMessage.success('Added to current playlist')
}

async function handleLike() {
  if (!userStore.isLoggedIn) {
    ElMessage.warning('Please login first')
    return
  }

  try {
    if (isLiked.value) {
      await musicStore.unlikeMusic(music.value.id)
      isLiked.value = false
      music.value.likeCount--
      ElMessage.success('Unliked')
    } else {
      await musicStore.likeMusic(music.value.id)
      isLiked.value = true
      music.value.likeCount++
      ElMessage.success('Liked')
    }
  } catch (error) {
    console.error('Like failed:', error)
  }
}

function handleDownload() {
  if (!userStore.isLoggedIn) {
    ElMessage.warning('Please login first')
    return
  }

  ElMessage.info('Download feature coming soon')
}

function handleShare() {
  const url = window.location.href
  navigator.clipboard.writeText(url).then(() => {
    ElMessage.success('Link copied to clipboard')
  }).catch(() => {
    ElMessage.error('Failed to copy link')
  })
}

function handleEdit() {
  router.push(`/music/${music.value.id}/edit`)
}

async function handleDelete() {
  try {
    await ElMessageBox.confirm(
      'Are you sure you want to delete this music? This action cannot be undone.',
      'Confirm Delete',
      {
        confirmButtonText: 'Delete',
        cancelButtonText: 'Cancel',
        type: 'warning'
      }
    )

    await musicStore.deleteMusic(music.value.id)
    ElMessage.success('Music deleted successfully')
    router.push('/music')
  } catch (error) {
    if (error !== 'cancel') {
      console.error('Delete failed:', error)
    }
  }
}

function formatDuration(seconds) {
  if (!seconds) return '00:00'
  const mins = Math.floor(seconds / 60)
  const secs = Math.floor(seconds % 60)
  return `${mins}:${secs.toString().padStart(2, '0')}`
}

function formatCount(count) {
  if (count >= 1000000) {
    return (count / 1000000).toFixed(1) + 'M'
  } else if (count >= 1000) {
    return (count / 1000).toFixed(1) + 'K'
  }
  return count.toString()
}

function formatDate(date) {
  if (!date) return 'N/A'
  return new Date(date).toLocaleDateString()
}

function formatFileSize(bytes) {
  if (!bytes) return 'N/A'
  const k = 1024
  const sizes = ['B', 'KB', 'MB', 'GB']
  const i = Math.floor(Math.log(bytes) / Math.log(k))
  return Math.round(bytes / Math.pow(k, i) * 100) / 100 + ' ' + sizes[i]
}

function onImageError(e) {
  e.target.src = '/default-cover.png'
}

// Watchers
watch(() => route.params.id, (newId) => {
  if (newId) {
    loadMusicDetail()
  }
})

watch(music, (newMusic) => {
  if (newMusic) {
    loadRelatedMusic()
  }
})

// Lifecycle
onMounted(() => {
  loadMusicDetail()
})
</script>

<style scoped lang="scss">
.music-detail-page {
  padding: 20px;
  max-width: 1400px;
  margin: 0 auto;
  padding-bottom: 100px;
}

.detail-header {
  display: flex;
  gap: 40px;
  margin-bottom: 32px;
  background: white;
  padding: 32px;
  border-radius: 12px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.1);

  .header-cover {
    position: relative;
    width: 280px;
    height: 280px;
    flex-shrink: 0;
    border-radius: 12px;
    overflow: hidden;
    box-shadow: 0 4px 16px rgba(0, 0, 0, 0.15);

    img {
      width: 100%;
      height: 100%;
      object-fit: cover;
    }

    .cover-overlay {
      position: absolute;
      top: 0;
      left: 0;
      right: 0;
      bottom: 0;
      background: rgba(0, 0, 0, 0.6);
      display: flex;
      align-items: center;
      justify-content: center;
      opacity: 0;
      transition: opacity 0.3s;

      &:hover {
        opacity: 1;
      }

      .el-button {
        font-size: 48px;
        width: 80px;
        height: 80px;
      }
    }
  }

  .header-info {
    flex: 1;
    display: flex;
    flex-direction: column;
    justify-content: center;

    .music-meta {
      display: flex;
      gap: 8px;
      margin-bottom: 12px;
    }

    .music-title {
      font-size: 36px;
      font-weight: 600;
      color: #303133;
      margin: 0 0 8px 0;
    }

    .music-artist {
      font-size: 24px;
      font-weight: 400;
      color: #606266;
      margin: 0 0 20px 0;
    }

    .music-stats {
      display: flex;
      gap: 24px;
      margin-bottom: 24px;
      font-size: 14px;
      color: #909399;

      .stat-item {
        display: flex;
        align-items: center;
        gap: 6px;

        .el-icon {
          font-size: 16px;
        }
      }
    }

    .action-buttons {
      display: flex;
      gap: 12px;
      flex-wrap: wrap;

      .liked {
        color: #f56c6c;
      }
    }
  }
}

.detail-content {
  .info-card {
    margin-bottom: 24px;

    .description-section {
      margin-top: 24px;
      padding-top: 24px;
      border-top: 1px solid #ebeef5;

      h3 {
        font-size: 16px;
        font-weight: 600;
        color: #303133;
        margin: 0 0 12px 0;
      }

      p {
        font-size: 14px;
        color: #606266;
        line-height: 1.6;
        margin: 0;
        white-space: pre-wrap;
      }
    }

    .lyrics-section {
      min-height: 300px;

      .lyrics-content {
        pre {
          font-family: inherit;
          font-size: 14px;
          line-height: 1.8;
          color: #303133;
          white-space: pre-wrap;
          word-wrap: break-word;
          margin: 0;
        }
      }
    }

    .comments-section {
      min-height: 300px;
    }
  }

  .sidebar-card {
    margin-bottom: 24px;

    .card-header {
      display: flex;
      align-items: center;
      gap: 8px;
      font-weight: 600;
    }

    .uploader-info {
      display: flex;
      align-items: center;
      gap: 16px;

      .uploader-details {
        .uploader-name {
          font-size: 16px;
          font-weight: 500;
          color: #303133;
          margin-bottom: 4px;
        }

        .uploader-role {
          font-size: 13px;
          color: #909399;
        }
      }
    }

    .related-music {
      .related-item {
        display: flex;
        gap: 12px;
        padding: 8px;
        border-radius: 6px;
        cursor: pointer;
        transition: background-color 0.3s;
        margin-bottom: 8px;

        &:hover {
          background-color: #f5f7fa;
        }

        img {
          width: 50px;
          height: 50px;
          border-radius: 4px;
          object-fit: cover;
        }

        .related-info {
          flex: 1;
          min-width: 0;

          .related-title {
            font-size: 14px;
            font-weight: 500;
            color: #303133;
            white-space: nowrap;
            overflow: hidden;
            text-overflow: ellipsis;
            margin-bottom: 4px;
          }

          .related-artist {
            font-size: 12px;
            color: #909399;
            white-space: nowrap;
            overflow: hidden;
            text-overflow: ellipsis;
          }
        }
      }
    }
  }
}

// Responsive
@media (max-width: 992px) {
  .detail-header {
    flex-direction: column;
    align-items: center;
    text-align: center;

    .header-cover {
      width: 240px;
      height: 240px;
    }

    .header-info {
      align-items: center;

      .music-meta,
      .music-stats,
      .action-buttons {
        justify-content: center;
      }
    }
  }

  .detail-content {
    :deep(.el-row) {
      display: flex;
      flex-direction: column;

      .el-col {
        max-width: 100%;
      }
    }
  }
}

@media (max-width: 768px) {
  .music-detail-page {
    padding: 12px;
  }

  .detail-header {
    padding: 20px;

    .header-cover {
      width: 200px;
      height: 200px;
    }

    .header-info {
      .music-title {
        font-size: 24px;
      }

      .music-artist {
        font-size: 18px;
      }

      .action-buttons {
        .el-button {
          font-size: 12px;
          padding: 8px 12px;
        }
      }
    }
  }
}
</style>
