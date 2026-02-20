<template>
  <div class="my-playlists-page">
    <!-- Page Header -->
    <div class="page-header">
      <div class="header-left">
        <h1>
          <el-icon><Folder /></el-icon>
          My Playlists
        </h1>
        <p>Manage your music collections</p>
      </div>
      <el-button type="primary" :icon="Plus" @click="showCreateDialog">
        Create Playlist
      </el-button>
    </div>

    <!-- Playlists Grid -->
    <div v-loading="loading" class="playlists-grid">
      <div
        v-for="playlist in playlists"
        :key="playlist.id"
        class="playlist-card"
      >
        <div class="playlist-cover" @click="router.push(`/playlists/${playlist.id}`)">
          <img
            :src="playlist.coverImage || '/default-playlist.png'"
            :alt="playlist.name"
            @error="onImageError"
          />
          <div class="cover-overlay">
            <el-icon class="play-icon" :size="48">
              <VideoPlay />
            </el-icon>
          </div>
          <div class="music-count-badge">
            {{ playlist.musicCount || 0 }} tracks
          </div>
          <el-tag
            v-if="!playlist.isPublic"
            class="privacy-tag"
            type="warning"
            size="small"
          >
            Private
          </el-tag>
        </div>

        <div class="playlist-info">
          <div class="playlist-name" :title="playlist.name">
            {{ playlist.name }}
          </div>
          <div class="playlist-stats">
            <span>
              <el-icon><VideoPlay /></el-icon>
              {{ formatCount(playlist.playCount || 0) }}
            </span>
            <span>
              <el-icon><Star /></el-icon>
              {{ formatCount(playlist.likeCount || 0) }}
            </span>
          </div>

          <div class="playlist-actions">
            <el-button
              type="primary"
              size="small"
              @click="router.push(`/playlists/${playlist.id}`)"
            >
              View
            </el-button>
            <el-button
              size="small"
              :icon="Edit"
              @click="handleEdit(playlist)"
            >
              Edit
            </el-button>
            <el-button
              size="small"
              :icon="Delete"
              type="danger"
              @click="handleDeleteConfirm(playlist)"
            >
              Delete
            </el-button>
          </div>
        </div>
      </div>

      <el-empty
        v-if="!loading && playlists.length === 0"
        description="No playlists created yet"
        :image-size="200"
      >
        <el-button type="primary" @click="showCreateDialog">
          Create Your First Playlist
        </el-button>
      </el-empty>
    </div>

    <!-- Pagination -->
    <div v-if="pagination.total > 0" class="pagination-wrapper">
      <el-pagination
        v-model:current-page="currentPage"
        v-model:page-size="pageSize"
        :total="pagination.total"
        :page-sizes="[12, 24, 48]"
        layout="total, sizes, prev, pager, next, jumper"
        @current-change="handlePageChange"
        @size-change="handlePageSizeChange"
      />
    </div>

    <!-- Create/Edit Playlist Dialog -->
    <el-dialog
      v-model="dialogVisible"
      :title="isEditing ? 'Edit Playlist' : 'Create New Playlist'"
      width="500px"
      :close-on-click-modal="false"
    >
      <el-form
        ref="formRef"
        :model="form"
        :rules="formRules"
        label-width="100px"
      >
        <el-form-item label="Name" prop="name">
          <el-input
            v-model="form.name"
            placeholder="Enter playlist name"
            maxlength="100"
            show-word-limit
          />
        </el-form-item>
        <el-form-item label="Description" prop="description">
          <el-input
            v-model="form.description"
            type="textarea"
            :rows="4"
            placeholder="Enter description (optional)"
            maxlength="500"
            show-word-limit
          />
        </el-form-item>
        <el-form-item label="Visibility" prop="isPublic">
          <el-radio-group v-model="form.isPublic">
            <el-radio :label="true">Public - Everyone can view</el-radio>
            <el-radio :label="false">Private - Only you can view</el-radio>
          </el-radio-group>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">Cancel</el-button>
        <el-button
          type="primary"
          @click="isEditing ? handleUpdate() : handleCreate()"
          :loading="saving"
        >
          {{ isEditing ? 'Update' : 'Create' }}
        </el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { useMusicStore } from '@/store/music'
import {
  Folder,
  Plus,
  VideoPlay,
  Star,
  Edit,
  Delete
} from '@element-plus/icons-vue'
import { ElMessage, ElMessageBox } from 'element-plus'

const router = useRouter()
const musicStore = useMusicStore()

// State
const loading = ref(false)
const saving = ref(false)
const playlists = ref([])
const currentPage = ref(1)
const pageSize = ref(24)
const pagination = ref({
  total: 0,
  totalPages: 0
})

// Dialog
const dialogVisible = ref(false)
const isEditing = ref(false)
const editingPlaylist = ref(null)
const formRef = ref(null)
const form = reactive({
  name: '',
  description: '',
  isPublic: true
})

const formRules = {
  name: [
    { required: true, message: 'Please enter playlist name', trigger: 'blur' },
    { min: 1, max: 100, message: 'Length should be 1 to 100', trigger: 'blur' }
  ]
}

// Methods
async function loadPlaylists() {
  try {
    loading.value = true
    const params = {
      page: currentPage.value - 1,
      size: pageSize.value,
      sort: 'createdAt,desc'
    }

    const response = await musicStore.fetchMyPlaylists(params)
    playlists.value = response.content || []
    pagination.value = {
      total: response.totalElements || 0,
      totalPages: response.totalPages || 0
    }
  } catch (error) {
    console.error('Failed to load playlists:', error)
    ElMessage.error('Failed to load your playlists')
  } finally {
    loading.value = false
  }
}

function handlePageChange(page) {
  currentPage.value = page
  loadPlaylists()
  window.scrollTo({ top: 0, behavior: 'smooth' })
}

function handlePageSizeChange(size) {
  pageSize.value = size
  currentPage.value = 1
  loadPlaylists()
}

function showCreateDialog() {
  isEditing.value = false
  editingPlaylist.value = null
  form.name = ''
  form.description = ''
  form.isPublic = true
  dialogVisible.value = true
}

function handleEdit(playlist) {
  isEditing.value = true
  editingPlaylist.value = playlist
  form.name = playlist.name
  form.description = playlist.description || ''
  form.isPublic = playlist.isPublic
  dialogVisible.value = true
}

async function handleCreate() {
  try {
    await formRef.value.validate()
    saving.value = true

    const result = await musicStore.createPlaylist({
      name: form.name,
      description: form.description,
      isPublic: form.isPublic
    })

    ElMessage.success('Playlist created successfully')
    dialogVisible.value = false

    // Navigate to the new playlist
    router.push(`/playlists/${result.id}`)
  } catch (error) {
    if (error !== false) {
      console.error('Create failed:', error)
      ElMessage.error('Failed to create playlist')
    }
  } finally {
    saving.value = false
  }
}

async function handleUpdate() {
  try {
    await formRef.value.validate()
    saving.value = true

    await musicStore.updatePlaylist(editingPlaylist.value.id, {
      name: form.name,
      description: form.description,
      isPublic: form.isPublic
    })

    ElMessage.success('Playlist updated successfully')
    dialogVisible.value = false
    loadPlaylists()
  } catch (error) {
    if (error !== false) {
      console.error('Update failed:', error)
      ElMessage.error('Failed to update playlist')
    }
  } finally {
    saving.value = false
  }
}

async function handleDeleteConfirm(playlist) {
  try {
    await ElMessageBox.confirm(
      `Are you sure you want to delete "${playlist.name}"? This action cannot be undone.`,
      'Confirm Delete',
      {
        confirmButtonText: 'Delete',
        cancelButtonText: 'Cancel',
        type: 'warning'
      }
    )

    await musicStore.deletePlaylist(playlist.id)
    ElMessage.success('Playlist deleted successfully')
    loadPlaylists()
  } catch (error) {
    if (error !== 'cancel') {
      console.error('Delete failed:', error)
    }
  }
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
  e.target.src = '/default-playlist.png'
}

// Lifecycle
onMounted(() => {
  loadPlaylists()
})
</script>

<style scoped lang="scss">
.my-playlists-page {
  padding: 20px;
  max-width: 1400px;
  margin: 0 auto;
  padding-bottom: 100px;
}

.page-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 32px;

  .header-left {
    h1 {
      font-size: 28px;
      font-weight: 600;
      color: #303133;
      margin: 0 0 4px 0;
      display: flex;
      align-items: center;
      gap: 12px;

      .el-icon {
        color: #409eff;
      }
    }

    p {
      font-size: 14px;
      color: #909399;
      margin: 0;
    }
  }
}

.playlists-grid {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 24px;
  min-height: 400px;
}

.playlist-card {
  background: white;
  border-radius: 12px;
  overflow: hidden;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
  transition: all 0.3s ease;

  &:hover {
    box-shadow: 0 4px 16px rgba(0, 0, 0, 0.15);
  }

  .playlist-cover {
    position: relative;
    width: 100%;
    padding-top: 100%;
    overflow: hidden;
    background: #f5f7fa;
    cursor: pointer;

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

    &:hover .cover-overlay {
      opacity: 1;
    }

    .music-count-badge {
      position: absolute;
      bottom: 12px;
      right: 12px;
      background: rgba(0, 0, 0, 0.8);
      color: white;
      padding: 4px 10px;
      border-radius: 12px;
      font-size: 12px;
      font-weight: 500;
    }

    .privacy-tag {
      position: absolute;
      top: 12px;
      left: 12px;
    }
  }

  .playlist-info {
    padding: 16px;

    .playlist-name {
      font-size: 16px;
      font-weight: 600;
      color: #303133;
      white-space: nowrap;
      overflow: hidden;
      text-overflow: ellipsis;
      margin-bottom: 8px;
    }

    .playlist-stats {
      display: flex;
      gap: 16px;
      font-size: 13px;
      color: #606266;
      margin-bottom: 12px;

      span {
        display: flex;
        align-items: center;
        gap: 4px;

        .el-icon {
          font-size: 14px;
        }
      }
    }

    .playlist-actions {
      display: flex;
      gap: 8px;
      padding-top: 12px;
      border-top: 1px solid #ebeef5;

      .el-button {
        flex: 1;
      }
    }
  }
}

.pagination-wrapper {
  display: flex;
  justify-content: center;
  margin-top: 32px;
}

// Responsive
@media (max-width: 1200px) {
  .playlists-grid {
    grid-template-columns: repeat(3, 1fr);
  }
}

@media (max-width: 992px) {
  .playlists-grid {
    grid-template-columns: repeat(2, 1fr);
  }
}

@media (max-width: 768px) {
  .my-playlists-page {
    padding: 12px;
  }

  .page-header {
    flex-direction: column;
    align-items: flex-start;
    gap: 16px;

    h1 {
      font-size: 22px;
    }
  }

  .playlists-grid {
    grid-template-columns: 1fr;
    gap: 16px;
  }
}
</style>
