<template>
  <div class="playlist-browse">
    <!-- Page Header -->
    <div class="page-header">
      <h1>
        <el-icon><FolderOpened /></el-icon>
        Playlists
      </h1>
      <el-button
        v-if="userStore.isLoggedIn"
        type="primary"
        :icon="Plus"
        @click="showCreateDialog"
      >
        Create Playlist
      </el-button>
    </div>

    <!-- Search and Filters -->
    <div class="filter-section">
      <el-input
        v-model="searchKeyword"
        placeholder="Search playlists..."
        :prefix-icon="Search"
        clearable
        @clear="handleSearch"
        @keyup.enter="handleSearch"
        class="search-input"
      >
        <template #append>
          <el-button :icon="Search" @click="handleSearch" />
        </template>
      </el-input>

      <el-select
        v-model="sortBy"
        @change="handleFilterChange"
        class="filter-select"
      >
        <el-option label="Latest" value="latest" />
        <el-option label="Most Popular" value="popular" />
        <el-option label="Name A-Z" value="name_asc" />
        <el-option label="Name Z-A" value="name_desc" />
      </el-select>
    </div>

    <!-- Playlists Grid -->
    <div v-loading="loading" class="playlists-grid">
      <div
        v-for="playlist in playlists"
        :key="playlist.id"
        class="playlist-card"
        @click="router.push(`/playlists/${playlist.id}`)"
      >
        <div class="playlist-cover">
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
        </div>

        <div class="playlist-info">
          <div class="playlist-name" :title="playlist.name">
            {{ playlist.name }}
          </div>
          <div class="playlist-creator">
            By {{ playlist.creator?.username || 'Unknown' }}
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
        </div>
      </div>

      <el-empty
        v-if="!loading && playlists.length === 0"
        description="No playlists found"
        :image-size="200"
      >
        <el-button
          v-if="userStore.isLoggedIn"
          type="primary"
          @click="showCreateDialog"
        >
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

    <!-- Create Playlist Dialog -->
    <el-dialog
      v-model="createDialogVisible"
      title="Create New Playlist"
      width="500px"
      :close-on-click-modal="false"
    >
      <el-form
        ref="createFormRef"
        :model="createForm"
        :rules="createRules"
        label-width="100px"
      >
        <el-form-item label="Name" prop="name">
          <el-input
            v-model="createForm.name"
            placeholder="Enter playlist name"
            maxlength="100"
            show-word-limit
          />
        </el-form-item>
        <el-form-item label="Description" prop="description">
          <el-input
            v-model="createForm.description"
            type="textarea"
            :rows="4"
            placeholder="Enter description (optional)"
            maxlength="500"
            show-word-limit
          />
        </el-form-item>
        <el-form-item label="Visibility" prop="isPublic">
          <el-radio-group v-model="createForm.isPublic">
            <el-radio :label="true">Public - Everyone can view</el-radio>
            <el-radio :label="false">Private - Only you can view</el-radio>
          </el-radio-group>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="createDialogVisible = false">Cancel</el-button>
        <el-button type="primary" @click="handleCreate" :loading="creating">
          Create
        </el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { useMusicStore } from '@/store/music'
import { useUserStore } from '@/store/user'
import {
  FolderOpened,
  Plus,
  Search,
  VideoPlay,
  Star
} from '@element-plus/icons-vue'
import { ElMessage } from 'element-plus'

const router = useRouter()
const musicStore = useMusicStore()
const userStore = useUserStore()

// State
const loading = ref(false)
const creating = ref(false)
const playlists = ref([])
const searchKeyword = ref('')
const sortBy = ref('latest')
const currentPage = ref(1)
const pageSize = ref(24)
const pagination = ref({
  total: 0,
  totalPages: 0
})

// Create dialog
const createDialogVisible = ref(false)
const createFormRef = ref(null)
const createForm = reactive({
  name: '',
  description: '',
  isPublic: true
})

const createRules = {
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
      sort: getSortParam()
    }

    if (searchKeyword.value) {
      const response = await musicStore.searchPlaylists(searchKeyword.value, params)
      playlists.value = response.content || []
      pagination.value = {
        total: response.totalElements || 0,
        totalPages: response.totalPages || 0
      }
    } else {
      const response = await musicStore.fetchPublicPlaylists(params)
      playlists.value = response.content || []
      pagination.value = {
        total: response.totalElements || 0,
        totalPages: response.totalPages || 0
      }
    }
  } catch (error) {
    console.error('Failed to load playlists:', error)
    ElMessage.error('Failed to load playlists')
  } finally {
    loading.value = false
  }
}

function getSortParam() {
  switch (sortBy.value) {
    case 'latest':
      return 'createdAt,desc'
    case 'popular':
      return 'playCount,desc'
    case 'name_asc':
      return 'name,asc'
    case 'name_desc':
      return 'name,desc'
    default:
      return 'createdAt,desc'
  }
}

function handleSearch() {
  currentPage.value = 1
  loadPlaylists()
}

function handleFilterChange() {
  currentPage.value = 1
  loadPlaylists()
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
  if (!userStore.isLoggedIn) {
    ElMessage.warning('Please login first')
    return
  }
  createDialogVisible.value = true
}

async function handleCreate() {
  try {
    await createFormRef.value.validate()
    creating.value = true

    const result = await musicStore.createPlaylist({
      name: createForm.name,
      description: createForm.description,
      isPublic: createForm.isPublic
    })

    ElMessage.success('Playlist created successfully')
    createDialogVisible.value = false

    // Reset form
    createForm.name = ''
    createForm.description = ''
    createForm.isPublic = true

    // Navigate to the new playlist
    router.push(`/playlists/${result.id}`)
  } catch (error) {
    if (error !== false) {
      console.error('Create failed:', error)
      ElMessage.error('Failed to create playlist')
    }
  } finally {
    creating.value = false
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
.playlist-browse {
  padding: 20px;
  max-width: 1400px;
  margin: 0 auto;
  padding-bottom: 100px;
}

.page-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 24px;

  h1 {
    font-size: 28px;
    font-weight: 600;
    color: #303133;
    margin: 0;
    display: flex;
    align-items: center;
    gap: 12px;

    .el-icon {
      color: #409eff;
    }
  }
}

.filter-section {
  display: flex;
  gap: 12px;
  background: white;
  border-radius: 8px;
  padding: 20px;
  margin-bottom: 24px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);

  .search-input {
    flex: 1;
    max-width: 400px;
  }

  .filter-select {
    width: 180px;
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
  cursor: pointer;

  &:hover {
    transform: translateY(-4px);
    box-shadow: 0 4px 16px rgba(0, 0, 0, 0.15);

    .cover-overlay {
      opacity: 1;
    }
  }

  .playlist-cover {
    position: relative;
    width: 100%;
    padding-top: 100%;
    overflow: hidden;
    background: #f5f7fa;

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
      margin-bottom: 6px;
    }

    .playlist-creator {
      font-size: 13px;
      color: #909399;
      margin-bottom: 12px;
    }

    .playlist-stats {
      display: flex;
      gap: 16px;
      font-size: 13px;
      color: #606266;

      span {
        display: flex;
        align-items: center;
        gap: 4px;

        .el-icon {
          font-size: 14px;
        }
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
  .playlist-browse {
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

  .filter-section {
    flex-direction: column;

    .search-input,
    .filter-select {
      width: 100%;
      max-width: none;
    }
  }

  .playlists-grid {
    grid-template-columns: 1fr;
    gap: 16px;
  }
}
</style>
