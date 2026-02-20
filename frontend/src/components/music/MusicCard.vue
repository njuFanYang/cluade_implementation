<template>
  <div class="music-card" :class="{ 'list-mode': mode === 'list' }">
    <div class="card-cover" @click="handlePlay">
      <img
        :src="music.coverImage || '/default-cover.png'"
        :alt="music.title"
        @error="onImageError"
      />
      <div class="cover-overlay">
        <el-icon class="play-icon" :size="40">
          <VideoPlay />
        </el-icon>
      </div>
      <div v-if="music.duration" class="duration-badge">
        {{ formatDuration(music.duration) }}
      </div>
    </div>

    <div class="card-content">
      <div class="music-info">
        <div class="music-title" :title="music.title" @click="handleViewDetail">
          {{ music.title }}
        </div>
        <div class="music-artist" :title="music.artist">
          {{ music.artist }}
        </div>
      </div>

      <div class="music-meta" v-if="showMeta">
        <span v-if="music.genre" class="meta-item">
          <el-icon><Discount /></el-icon>
          {{ music.genre.name }}
        </span>
        <span v-if="music.playCount !== undefined" class="meta-item">
          <el-icon><Headset /></el-icon>
          {{ formatCount(music.playCount) }}
        </span>
      </div>

      <div class="card-actions">
        <el-tooltip content="Add to Playlist">
          <el-button
            :icon="FolderAdd"
            circle
            text
            @click="handleAddToPlaylist"
          />
        </el-tooltip>
        <LikeButton
          target-type="MUSIC"
          :target-id="music.id"
          :initial-liked="music.isLiked || false"
          :initial-count="music.likeCount || 0"
          size="small"
        />
        <el-tooltip content="Download">
          <el-button
            :icon="Download"
            circle
            text
            @click="handleDownload"
          />
        </el-tooltip>
        <el-dropdown v-if="showMore" @command="handleCommand">
          <el-button :icon="More" circle text />
          <template #dropdown>
            <el-dropdown-menu>
              <el-dropdown-item command="detail">
                <el-icon><View /></el-icon>
                View Details
              </el-dropdown-item>
              <el-dropdown-item command="share">
                <el-icon><Share /></el-icon>
                Share
              </el-dropdown-item>
              <el-dropdown-item
                v-if="canEdit"
                command="edit"
                divided
              >
                <el-icon><Edit /></el-icon>
                Edit
              </el-dropdown-item>
              <el-dropdown-item
                v-if="canDelete"
                command="delete"
                divided
              >
                <el-icon><Delete /></el-icon>
                Delete
              </el-dropdown-item>
            </el-dropdown-menu>
          </template>
        </el-dropdown>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed } from 'vue'
import { useRouter } from 'vue-router'
import { usePlayerStore } from '@/store/player'
import { useMusicStore } from '@/store/music'
import { useUserStore } from '@/store/user'
import {
  VideoPlay,
  FolderAdd,
  Download,
  More,
  View,
  Share,
  Edit,
  Delete,
  Discount,
  Headset
} from '@element-plus/icons-vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { LikeButton } from '@/components/social'

const props = defineProps({
  music: {
    type: Object,
    required: true
  },
  mode: {
    type: String,
    default: 'card', // 'card' or 'list'
    validator: (value) => ['card', 'list'].includes(value)
  },
  showMeta: {
    type: Boolean,
    default: true
  },
  showMore: {
    type: Boolean,
    default: true
  }
})

const emit = defineEmits(['play', 'delete', 'edit'])

const router = useRouter()
const playerStore = usePlayerStore()
const musicStore = useMusicStore()
const userStore = useUserStore()

// Computed
const canEdit = computed(() => {
  if (!userStore.isLoggedIn) return false
  return userStore.currentUser?.id === props.music.uploader?.id ||
         userStore.hasRole('ADMIN')
})

const canDelete = computed(() => {
  return canEdit.value
})

// Methods
function handlePlay() {
  playerStore.play(props.music)
  emit('play', props.music)
}

function handleViewDetail() {
  router.push(`/music/${props.music.id}`)
}

function handleAddToPlaylist() {
  if (!userStore.isLoggedIn) {
    ElMessage.warning('Please login first')
    return
  }

  // TODO: Show playlist selection dialog
  playerStore.addToPlaylist(props.music)
  ElMessage.success('Added to current playlist')
}

function handleDownload() {
  if (!userStore.isLoggedIn) {
    ElMessage.warning('Please login first')
    return
  }

  // TODO: Implement download
  ElMessage.info('Download feature coming soon')
}

async function handleCommand(command) {
  switch (command) {
    case 'detail':
      handleViewDetail()
      break
    case 'share':
      // TODO: Implement share
      ElMessage.info('Share feature coming soon')
      break
    case 'edit':
      emit('edit', props.music)
      break
    case 'delete':
      await handleDelete()
      break
  }
}

async function handleDelete() {
  try {
    await ElMessageBox.confirm(
      'Are you sure you want to delete this music?',
      'Confirm Delete',
      {
        confirmButtonText: 'Delete',
        cancelButtonText: 'Cancel',
        type: 'warning'
      }
    )

    await musicStore.deleteMusic(props.music.id)
    emit('delete', props.music)
    ElMessage.success('Music deleted successfully')
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
  return `${mins.toString().padStart(2, '0')}:${secs.toString().padStart(2, '0')}`
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
  e.target.src = '/default-cover.png'
}
</script>

<style scoped lang="scss">
.music-card {
  background: white;
  border-radius: 8px;
  overflow: hidden;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
  transition: all 0.3s ease;
  cursor: pointer;

  &:hover {
    transform: translateY(-4px);
    box-shadow: 0 4px 16px rgba(0, 0, 0, 0.15);

    .cover-overlay {
      opacity: 1;
    }
  }

  // Card Mode (Grid)
  &:not(.list-mode) {
    .card-cover {
      position: relative;
      width: 100%;
      padding-top: 100%; // 1:1 aspect ratio
      overflow: hidden;

      img {
        position: absolute;
        top: 0;
        left: 0;
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

        .play-icon {
          color: white;
        }
      }

      .duration-badge {
        position: absolute;
        bottom: 8px;
        right: 8px;
        background: rgba(0, 0, 0, 0.8);
        color: white;
        padding: 2px 6px;
        border-radius: 4px;
        font-size: 12px;
      }
    }

    .card-content {
      padding: 12px;
    }

    .music-info {
      margin-bottom: 8px;

      .music-title {
        font-size: 14px;
        font-weight: 500;
        color: #303133;
        white-space: nowrap;
        overflow: hidden;
        text-overflow: ellipsis;
        margin-bottom: 4px;
        cursor: pointer;

        &:hover {
          color: #409eff;
        }
      }

      .music-artist {
        font-size: 13px;
        color: #909399;
        white-space: nowrap;
        overflow: hidden;
        text-overflow: ellipsis;
      }
    }

    .music-meta {
      display: flex;
      gap: 12px;
      margin-bottom: 8px;
      font-size: 12px;
      color: #909399;

      .meta-item {
        display: flex;
        align-items: center;
        gap: 4px;

        .el-icon {
          font-size: 14px;
        }
      }
    }

    .card-actions {
      display: flex;
      align-items: center;
      gap: 4px;
      border-top: 1px solid #ebeef5;
      padding-top: 8px;

      .el-button {
        &.liked {
          color: #f56c6c;
        }
      }
    }
  }

  // List Mode
  &.list-mode {
    display: flex;
    align-items: center;
    padding: 12px;
    border-radius: 4px;

    &:hover {
      transform: none;
      box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
    }

    .card-cover {
      position: relative;
      width: 60px;
      height: 60px;
      flex-shrink: 0;
      border-radius: 4px;
      overflow: hidden;
      margin-right: 12px;

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

        .play-icon {
          color: white;
          font-size: 24px;
        }
      }

      .duration-badge {
        position: absolute;
        bottom: 4px;
        right: 4px;
        background: rgba(0, 0, 0, 0.8);
        color: white;
        padding: 2px 4px;
        border-radius: 2px;
        font-size: 10px;
      }
    }

    .card-content {
      flex: 1;
      display: flex;
      align-items: center;
      gap: 16px;
      min-width: 0;
    }

    .music-info {
      flex: 1;
      min-width: 0;

      .music-title {
        font-size: 14px;
        font-weight: 500;
        color: #303133;
        white-space: nowrap;
        overflow: hidden;
        text-overflow: ellipsis;
        margin-bottom: 4px;
        cursor: pointer;

        &:hover {
          color: #409eff;
        }
      }

      .music-artist {
        font-size: 13px;
        color: #909399;
        white-space: nowrap;
        overflow: hidden;
        text-overflow: ellipsis;
      }
    }

    .music-meta {
      display: flex;
      gap: 16px;
      font-size: 12px;
      color: #909399;
      flex-shrink: 0;

      .meta-item {
        display: flex;
        align-items: center;
        gap: 4px;

        .el-icon {
          font-size: 14px;
        }
      }
    }

    .card-actions {
      display: flex;
      align-items: center;
      gap: 4px;
      flex-shrink: 0;

      .el-button {
        &.liked {
          color: #f56c6c;
        }
      }
    }
  }
}

// Responsive
@media (max-width: 768px) {
  .music-card.list-mode {
    .music-meta {
      display: none;
    }

    .card-actions {
      gap: 2px;

      .el-button {
        padding: 4px;
      }
    }
  }
}
</style>
