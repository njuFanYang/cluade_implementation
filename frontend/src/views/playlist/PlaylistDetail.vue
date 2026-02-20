<template>
  <div class="playlist-detail-page">
    <el-skeleton :loading="loading" animated>
      <template #template>
        <div class="detail-header">
          <el-skeleton-item variant="image" class="header-cover" />
          <div class="header-info">
            <el-skeleton-item variant="h1" style="width: 50%" />
            <el-skeleton-item variant="text" style="width: 30%" />
          </div>
        </div>
      </template>

      <template #default>
        <div v-if="playlist" class="detail-container">
          <!-- Playlist Header -->
          <div class="detail-header">
            <div class="header-cover">
              <img
                :src="playlist.coverImage || '/default-playlist.png'"
                :alt="playlist.name"
                @error="onImageError"
              />
            </div>

            <div class="header-info">
              <div class="playlist-meta">
                <el-tag type="info" size="small">Playlist</el-tag>
                <el-tag v-if="!playlist.isPublic" type="warning" size="small">
                  Private
                </el-tag>
              </div>

              <h1 class="playlist-name">{{ playlist.name }}</h1>
              <div class="playlist-creator">
                By {{ playlist.creator?.username || 'Unknown' }}
              </div>

              <div v-if="playlist.description" class="playlist-description">
                {{ playlist.description }}
              </div>

              <div class="playlist-stats">
                <span>{{ playlist.musicCount || 0 }} tracks</span>
                <span>•</span>
                <span>{{ formatCount(playlist.playCount || 0) }} plays</span>
                <span>•</span>
                <span>{{ formatCount(playlist.likeCount || 0) }} likes</span>
              </div>

              <div class="action-buttons">
                <el-button
                  type="primary"
                  :icon="VideoPlay"
                  @click="handlePlayAll"
                  size="large"
                  :disabled="musicList.length === 0"
                >
                  Play All
                </el-button>
                <el-button
                  :icon="isLiked ? StarFilled : Star"
                  @click="handleLike"
                  :class="{ liked: isLiked }"
                  size="large"
                >
                  {{ isLiked ? 'Liked' : 'Like' }}
                </el-button>

                <el-dropdown v-if="canEdit" trigger="click">
                  <el-button :icon="More" size="large" />
                  <template #dropdown>
                    <el-dropdown-menu>
                      <el-dropdown-item :icon="Edit" @click="handleEdit">
                        Edit Playlist
                      </el-dropdown-item>
                      <el-dropdown-item :icon="Delete" divided @click="handleDelete">
                        Delete Playlist
                      </el-dropdown-item>
                    </el-dropdown-menu>
                  </template>
                </el-dropdown>
              </div>
            </div>
          </div>

          <!-- Music List -->
          <div class="music-list-section">
            <el-card>
              <template #header>
                <div class="list-header">
                  <h2>Tracks</h2>
                </div>
              </template>

              <div v-if="loadingMusic" v-loading="true" style="min-height: 200px" />

              <div v-else-if="musicList.length > 0" class="music-table">
                <div class="table-header">
                  <div class="col-index">#</div>
                  <div class="col-title">Title</div>
                  <div class="col-artist">Artist</div>
                  <div class="col-album">Album</div>
                  <div class="col-duration">Duration</div>
                  <div class="col-actions">Actions</div>
                </div>

                <div
                  v-for="(music, index) in musicList"
                  :key="music.id"
                  class="table-row"
                  :class="{ playing: isCurrentMusic(music) && playerStore.isPlaying }"
                >
                  <div class="col-index">
                    <span v-if="!isCurrentMusic(music) || !playerStore.isPlaying">
                      {{ index + 1 }}
                    </span>
                    <el-icon v-else class="playing-icon">
                      <VideoPlay />
                    </el-icon>
                  </div>

                  <div class="col-title" @click="handlePlay(music, index)">
                    <img
                      :src="music.coverImage || '/default-cover.png'"
                      :alt="music.title"
                      @error="onImageError"
                    />
                    <span>{{ music.title }}</span>
                  </div>

                  <div class="col-artist">{{ music.artist }}</div>
                  <div class="col-album">{{ music.album?.title || '-' }}</div>
                  <div class="col-duration">{{ formatDuration(music.duration) }}</div>

                  <div class="col-actions">
                    <el-button
                      :icon="FolderAdd"
                      circle
                      text
                      @click="handleAddToPlaylist(music)"
                      title="Add to another playlist"
                    />
                    <el-button
                      :icon="Download"
                      circle
                      text
                      @click="handleDownload(music)"
                      title="Download"
                    />
                    <el-button
                      v-if="canEdit"
                      :icon="Delete"
                      circle
                      text
                      type="danger"
                      @click="handleRemoveMusic(music)"
                      title="Remove from playlist"
                    />
                  </div>
                </div>
              </div>

              <el-empty
                v-else
                description="No tracks in this playlist"
                :image-size="150"
              />
            </el-card>
          </div>

          <!-- Comments Section -->
          <div class="comments-section-wrapper">
            <el-card>
              <CommentSection
                target-type="PLAYLIST"
                :target-id="playlist.id"
                :auto-load="true"
                @comments-loaded="handleCommentsLoaded"
                @comment-added="handleCommentAdded"
              />
            </el-card>
          </div>
        </div>

        <el-empty
          v-else-if="!loading"
          description="Playlist not found"
          :image-size="200"
        >
          <el-button type="primary" @click="router.back()">Go Back</el-button>
        </el-empty>
      </template>
    </el-skeleton>

    <!-- Edit Dialog -->
    <el-dialog
      v-model="editDialogVisible"
      title="Edit Playlist"
      width="500px"
      :close-on-click-modal="false"
    >
      <el-form
        ref="editFormRef"
        :model="editForm"
        :rules="editRules"
        label-width="100px"
      >
        <el-form-item label="Name" prop="name">
          <el-input v-model="editForm.name" maxlength="100" show-word-limit />
        </el-form-item>
        <el-form-item label="Description" prop="description">
          <el-input
            v-model="editForm.description"
            type="textarea"
            :rows="4"
            maxlength="500"
            show-word-limit
          />
        </el-form-item>
        <el-form-item label="Visibility" prop="isPublic">
          <el-radio-group v-model="editForm.isPublic">
            <el-radio :label="true">Public</el-radio>
            <el-radio :label="false">Private</el-radio>
          </el-radio-group>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="editDialogVisible = false">Cancel</el-button>
        <el-button type="primary" @click="handleSaveEdit" :loading="saving">
          Save Changes
        </el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted, watch } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { useMusicStore } from '@/store/music'
import { usePlayerStore } from '@/store/player'
import { useUserStore } from '@/store/user'
import {
  VideoPlay,
  Star,
  StarFilled,
  More,
  Edit,
  Delete,
  FolderAdd,
  Download
} from '@element-plus/icons-vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { CommentSection } from '@/components/social'

const route = useRoute()
const router = useRouter()
const musicStore = useMusicStore()
const playerStore = usePlayerStore()
const userStore = useUserStore()

// State
const loading = ref(true)
const loadingMusic = ref(false)
const saving = ref(false)
const playlist = ref(null)
const musicList = ref([])
const isLiked = ref(false)

// Edit dialog
const editDialogVisible = ref(false)
const editFormRef = ref(null)
const editForm = reactive({
  name: '',
  description: '',
  isPublic: true
})

const editRules = {
  name: [{ required: true, message: 'Please enter name', trigger: 'blur' }]
}

// Computed
const playlistId = computed(() => parseInt(route.params.id))

const canEdit = computed(() => {
  if (!userStore.isLoggedIn || !playlist.value) return false
  return userStore.currentUser?.id === playlist.value.creator?.id ||
         userStore.hasRole('ADMIN')
})

// Methods
async function loadPlaylistDetail() {
  try {
    loading.value = true
    const response = await musicStore.fetchPlaylistDetail(playlistId.value)
    playlist.value = response

    // Load music list
    loadMusicList()
  } catch (error) {
    console.error('Failed to load playlist:', error)
    ElMessage.error('Failed to load playlist')
    playlist.value = null
  } finally {
    loading.value = false
  }
}

async function loadMusicList() {
  try {
    loadingMusic.value = true
    const response = await musicStore.fetchPlaylistMusic(playlistId.value)
    musicList.value = response || []
  } catch (error) {
    console.error('Failed to load music list:', error)
    musicList.value = []
  } finally {
    loadingMusic.value = false
  }
}

function handlePlayAll() {
  if (musicList.value.length > 0) {
    playerStore.play(musicList.value[0], musicList.value)
  }
}

function handlePlay(music, index) {
  playerStore.play(music, musicList.value)
}

function isCurrentMusic(music) {
  return playerStore.currentMusic?.id === music.id
}

async function handleLike() {
  if (!userStore.isLoggedIn) {
    ElMessage.warning('Please login first')
    return
  }

  try {
    if (isLiked.value) {
      // TODO: Implement unlike playlist API
      isLiked.value = false
      if (playlist.value) playlist.value.likeCount--
      ElMessage.success('Unliked')
    } else {
      // TODO: Implement like playlist API
      isLiked.value = true
      if (playlist.value) playlist.value.likeCount++
      ElMessage.success('Liked')
    }
  } catch (error) {
    console.error('Like failed:', error)
  }
}

function handleEdit() {
  editForm.name = playlist.value.name
  editForm.description = playlist.value.description || ''
  editForm.isPublic = playlist.value.isPublic
  editDialogVisible.value = true
}

async function handleSaveEdit() {
  try {
    await editFormRef.value.validate()
    saving.value = true

    await musicStore.updatePlaylist(playlist.value.id, {
      name: editForm.name,
      description: editForm.description,
      isPublic: editForm.isPublic
    })

    ElMessage.success('Playlist updated successfully')
    editDialogVisible.value = false
    loadPlaylistDetail()
  } catch (error) {
    if (error !== false) {
      console.error('Update failed:', error)
      ElMessage.error('Failed to update playlist')
    }
  } finally {
    saving.value = false
  }
}

async function handleDelete() {
  try {
    await ElMessageBox.confirm(
      'Are you sure you want to delete this playlist? This action cannot be undone.',
      'Confirm Delete',
      {
        confirmButtonText: 'Delete',
        cancelButtonText: 'Cancel',
        type: 'warning'
      }
    )

    await musicStore.deletePlaylist(playlist.value.id)
    ElMessage.success('Playlist deleted successfully')
    router.push('/playlists/my')
  } catch (error) {
    if (error !== 'cancel') {
      console.error('Delete failed:', error)
    }
  }
}

async function handleRemoveMusic(music) {
  try {
    await ElMessageBox.confirm(
      `Remove "${music.title}" from this playlist?`,
      'Confirm Remove',
      {
        confirmButtonText: 'Remove',
        cancelButtonText: 'Cancel',
        type: 'warning'
      }
    )

    await musicStore.removeMusicFromPlaylist(playlist.value.id, music.id)
    ElMessage.success('Music removed from playlist')
    loadMusicList()
  } catch (error) {
    if (error !== 'cancel') {
      console.error('Remove failed:', error)
    }
  }
}

function handleAddToPlaylist(music) {
  if (!userStore.isLoggedIn) {
    ElMessage.warning('Please login first')
    return
  }

  playerStore.addToPlaylist(music)
  ElMessage.success('Added to current playlist')
}

function handleDownload(music) {
  if (!userStore.isLoggedIn) {
    ElMessage.warning('Please login first')
    return
  }

  ElMessage.info('Download feature coming soon')
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

function onImageError(e) {
  e.target.src = e.target.alt.includes('playlist') ? '/default-playlist.png' : '/default-cover.png'
}

// Comment handlers
function handleCommentsLoaded({ total }) {
  // Update comment count if needed
  console.log(`Loaded ${total} comments`)
}

function handleCommentAdded(comment) {
  // Handle new comment added
  console.log('New comment added:', comment)
}

// Watchers
watch(() => route.params.id, (newId) => {
  if (newId) {
    loadPlaylistDetail()
  }
})

// Lifecycle
onMounted(() => {
  loadPlaylistDetail()
})
</script>

<style scoped lang="scss">
.playlist-detail-page {
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
    width: 240px;
    height: 240px;
    flex-shrink: 0;
    border-radius: 12px;
    overflow: hidden;
    box-shadow: 0 4px 16px rgba(0, 0, 0, 0.15);

    img {
      width: 100%;
      height: 100%;
      object-fit: cover;
    }
  }

  .header-info {
    flex: 1;
    display: flex;
    flex-direction: column;
    justify-content: center;

    .playlist-meta {
      display: flex;
      gap: 8px;
      margin-bottom: 12px;
    }

    .playlist-name {
      font-size: 32px;
      font-weight: 600;
      color: #303133;
      margin: 0 0 8px 0;
    }

    .playlist-creator {
      font-size: 16px;
      color: #606266;
      margin-bottom: 12px;
    }

    .playlist-description {
      font-size: 14px;
      color: #909399;
      line-height: 1.6;
      margin-bottom: 16px;
    }

    .playlist-stats {
      display: flex;
      gap: 12px;
      font-size: 14px;
      color: #909399;
      margin-bottom: 24px;
    }

    .action-buttons {
      display: flex;
      gap: 12px;

      .liked {
        color: #f56c6c;
      }
    }
  }
}

.music-list-section {
  .list-header {
    display: flex;
    justify-content: space-between;
    align-items: center;

    h2 {
      font-size: 20px;
      font-weight: 600;
      color: #303133;
      margin: 0;
    }
  }

  .music-table {
    .table-header,
    .table-row {
      display: grid;
      grid-template-columns: 40px 1fr 200px 200px 80px 120px;
      gap: 16px;
      align-items: center;
      padding: 12px 16px;
    }

    .table-header {
      font-size: 12px;
      font-weight: 600;
      color: #909399;
      text-transform: uppercase;
      border-bottom: 1px solid #ebeef5;
    }

    .table-row {
      border-bottom: 1px solid #f5f7fa;
      transition: background-color 0.2s;

      &:hover {
        background-color: #f5f7fa;
      }

      &.playing {
        background-color: #ecf5ff;

        .col-title span {
          color: #409eff;
        }

        .playing-icon {
          color: #409eff;
        }
      }

      .col-index {
        text-align: center;
        color: #909399;

        .playing-icon {
          font-size: 16px;
        }
      }

      .col-title {
        display: flex;
        align-items: center;
        gap: 12px;
        cursor: pointer;

        img {
          width: 40px;
          height: 40px;
          border-radius: 4px;
          object-fit: cover;
        }

        span {
          flex: 1;
          white-space: nowrap;
          overflow: hidden;
          text-overflow: ellipsis;
          font-weight: 500;
          color: #303133;

          &:hover {
            color: #409eff;
          }
        }
      }

      .col-artist,
      .col-album,
      .col-duration {
        color: #606266;
        white-space: nowrap;
        overflow: hidden;
        text-overflow: ellipsis;
      }

      .col-actions {
        display: flex;
        gap: 4px;
        justify-content: flex-end;
      }
    }
  }
}

.comments-section-wrapper {
  margin-top: 24px;
}

// Responsive
@media (max-width: 992px) {
  .detail-header {
    flex-direction: column;
    align-items: center;
    text-align: center;

    .header-cover {
      width: 200px;
      height: 200px;
    }

    .header-info {
      align-items: center;

      .playlist-meta,
      .playlist-stats,
      .action-buttons {
        justify-content: center;
      }
    }
  }

  .music-table {
    .table-header,
    .table-row {
      grid-template-columns: 40px 1fr 100px 80px;
    }

    .col-album,
    .col-actions {
      display: none !important;
    }
  }
}

@media (max-width: 768px) {
  .playlist-detail-page {
    padding: 12px;
  }

  .detail-header {
    padding: 20px;

    .header-info {
      .playlist-name {
        font-size: 24px;
      }

      .action-buttons {
        flex-wrap: wrap;

        .el-button {
          font-size: 12px;
        }
      }
    }
  }

  .music-table {
    .table-header {
      display: none;
    }

    .table-row {
      grid-template-columns: 40px 1fr 60px;

      .col-artist,
      .col-duration {
        display: none !important;
      }
    }
  }
}
</style>
