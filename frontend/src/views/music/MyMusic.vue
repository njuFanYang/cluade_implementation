<template>
  <div class="my-music-page">
    <!-- Page Header -->
    <div class="page-header">
      <div class="header-left">
        <h1>
          <el-icon><Collection /></el-icon>
          My Music
        </h1>
        <p>Manage your uploaded music tracks</p>
      </div>
      <el-button
        type="primary"
        :icon="Upload"
        @click="router.push('/music/upload')"
      >
        Upload Music
      </el-button>
    </div>

    <!-- Stats Cards -->
    <el-row :gutter="20" class="stats-section">
      <el-col :span="6">
        <el-card class="stat-card">
          <div class="stat-content">
            <el-icon class="stat-icon" color="#409eff"><Headset /></el-icon>
            <div class="stat-info">
              <div class="stat-value">{{ totalStats.totalTracks }}</div>
              <div class="stat-label">Total Tracks</div>
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card class="stat-card">
          <div class="stat-content">
            <el-icon class="stat-icon" color="#67c23a"><VideoPlay /></el-icon>
            <div class="stat-info">
              <div class="stat-value">{{ formatCount(totalStats.totalPlays) }}</div>
              <div class="stat-label">Total Plays</div>
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card class="stat-card">
          <div class="stat-content">
            <el-icon class="stat-icon" color="#f56c6c"><Star /></el-icon>
            <div class="stat-info">
              <div class="stat-value">{{ formatCount(totalStats.totalLikes) }}</div>
              <div class="stat-label">Total Likes</div>
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card class="stat-card">
          <div class="stat-content">
            <el-icon class="stat-icon" color="#e6a23c"><Download /></el-icon>
            <div class="stat-info">
              <div class="stat-value">{{ formatCount(totalStats.totalDownloads) }}</div>
              <div class="stat-label">Total Downloads</div>
            </div>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <!-- Filters and Actions -->
    <div class="filter-section">
      <div class="filter-row">
        <el-input
          v-model="searchKeyword"
          placeholder="Search your music..."
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
          v-model="statusFilter"
          placeholder="All Status"
          clearable
          @change="handleFilterChange"
          class="filter-select"
        >
          <el-option label="All Status" :value="null" />
          <el-option label="Approved" value="APPROVED" />
          <el-option label="Pending" value="PENDING" />
          <el-option label="Rejected" value="REJECTED" />
        </el-select>

        <el-select
          v-model="sortBy"
          @change="handleFilterChange"
          class="filter-select"
        >
          <el-option label="Latest Upload" value="latest" />
          <el-option label="Most Popular" value="popular" />
          <el-option label="Title A-Z" value="title_asc" />
          <el-option label="Title Z-A" value="title_desc" />
        </el-select>

        <el-radio-group v-model="viewMode" class="view-mode-toggle">
          <el-radio-button value="card">
            <el-icon><Grid /></el-icon>
          </el-radio-button>
          <el-radio-button value="list">
            <el-icon><List /></el-icon>
          </el-radio-button>
        </el-radio-group>
      </div>
    </div>

    <!-- Music List -->
    <div class="music-section">
      <div
        v-loading="loading"
        :class="viewMode === 'card' ? 'music-grid' : 'music-list'"
      >
        <MusicCard
          v-for="music in myMusicList"
          :key="music.id"
          :music="music"
          :mode="viewMode"
          @play="handlePlay"
          @edit="handleEdit"
          @delete="handleDeleteConfirm"
        />
        <el-empty
          v-if="!loading && myMusicList.length === 0"
          description="No music uploaded yet"
          :image-size="200"
        >
          <el-button type="primary" @click="router.push('/music/upload')">
            Upload Your First Track
          </el-button>
        </el-empty>
      </div>

      <!-- Pagination -->
      <div v-if="pagination.total > 0" class="pagination-wrapper">
        <el-pagination
          v-model:current-page="currentPage"
          v-model:page-size="pageSize"
          :total="pagination.total"
          :page-sizes="[12, 24, 48, 96]"
          layout="total, sizes, prev, pager, next, jumper"
          @current-change="handlePageChange"
          @size-change="handlePageSizeChange"
        />
      </div>
    </div>

    <!-- Edit Dialog -->
    <el-dialog
      v-model="editDialogVisible"
      title="Edit Music"
      width="600px"
      :close-on-click-modal="false"
    >
      <el-form
        ref="editFormRef"
        :model="editForm"
        :rules="editRules"
        label-width="120px"
      >
        <el-form-item label="Title" prop="title">
          <el-input v-model="editForm.title" maxlength="200" show-word-limit />
        </el-form-item>
        <el-form-item label="Artist" prop="artist">
          <el-input v-model="editForm.artist" maxlength="100" show-word-limit />
        </el-form-item>
        <el-form-item label="Genre" prop="genreId">
          <el-select v-model="editForm.genreId" style="width: 100%">
            <el-option
              v-for="genre in musicStore.genres"
              :key="genre.id"
              :label="genre.name"
              :value="genre.id"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="Description" prop="description">
          <el-input
            v-model="editForm.description"
            type="textarea"
            :rows="4"
            maxlength="1000"
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
import { useRouter } from 'vue-router'
import { useMusicStore } from '@/store/music'
import { usePlayerStore } from '@/store/player'
import MusicCard from '@/components/Music/MusicCard.vue'
import {
  Collection,
  Upload,
  Search,
  Grid,
  List,
  Headset,
  VideoPlay,
  Star,
  Download
} from '@element-plus/icons-vue'
import { ElMessage, ElMessageBox } from 'element-plus'

const router = useRouter()
const musicStore = useMusicStore()
const playerStore = usePlayerStore()

// State
const loading = ref(false)
const saving = ref(false)
const myMusicList = ref([])
const searchKeyword = ref('')
const statusFilter = ref(null)
const sortBy = ref('latest')
const viewMode = ref(localStorage.getItem('myMusicViewMode') || 'card')
const currentPage = ref(1)
const pageSize = ref(24)
const pagination = ref({
  total: 0,
  totalPages: 0
})

// Edit dialog
const editDialogVisible = ref(false)
const editFormRef = ref(null)
const editingMusic = ref(null)
const editForm = reactive({
  title: '',
  artist: '',
  genreId: null,
  description: '',
  isPublic: true
})

const editRules = {
  title: [{ required: true, message: 'Please enter title', trigger: 'blur' }],
  artist: [{ required: true, message: 'Please enter artist', trigger: 'blur' }],
  genreId: [{ required: true, message: 'Please select genre', trigger: 'change' }]
}

// Computed
const totalStats = computed(() => {
  if (myMusicList.value.length === 0) {
    return {
      totalTracks: 0,
      totalPlays: 0,
      totalLikes: 0,
      totalDownloads: 0
    }
  }

  return {
    totalTracks: pagination.value.total || myMusicList.value.length,
    totalPlays: myMusicList.value.reduce((sum, m) => sum + (m.playCount || 0), 0),
    totalLikes: myMusicList.value.reduce((sum, m) => sum + (m.likeCount || 0), 0),
    totalDownloads: myMusicList.value.reduce((sum, m) => sum + (m.downloadCount || 0), 0)
  }
})

// Watch view mode
watch(viewMode, (newMode) => {
  localStorage.setItem('myMusicViewMode', newMode)
})

// Methods
async function loadMyMusic() {
  try {
    loading.value = true
    const params = {
      page: currentPage.value - 1,
      size: pageSize.value,
      sort: getSortParam()
    }

    if (searchKeyword.value) {
      params.keyword = searchKeyword.value
    }

    if (statusFilter.value) {
      params.status = statusFilter.value
    }

    const response = await musicStore.fetchMyMusic(params)
    myMusicList.value = response.content || []
    pagination.value = {
      total: response.totalElements || 0,
      totalPages: response.totalPages || 0
    }
  } catch (error) {
    console.error('Failed to load my music:', error)
    ElMessage.error('Failed to load your music')
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
    case 'title_asc':
      return 'title,asc'
    case 'title_desc':
      return 'title,desc'
    default:
      return 'createdAt,desc'
  }
}

function handleSearch() {
  currentPage.value = 1
  loadMyMusic()
}

function handleFilterChange() {
  currentPage.value = 1
  loadMyMusic()
}

function handlePageChange(page) {
  currentPage.value = page
  loadMyMusic()
  window.scrollTo({ top: 0, behavior: 'smooth' })
}

function handlePageSizeChange(size) {
  pageSize.value = size
  currentPage.value = 1
  loadMyMusic()
}

function handlePlay(music) {
  playerStore.play(music, myMusicList.value)
}

function handleEdit(music) {
  editingMusic.value = music
  editForm.title = music.title
  editForm.artist = music.artist
  editForm.genreId = music.genre?.id
  editForm.description = music.description || ''
  editForm.isPublic = music.isPublic
  editDialogVisible.value = true
}

async function handleSaveEdit() {
  try {
    await editFormRef.value.validate()
    saving.value = true

    await musicStore.updateMusic(editingMusic.value.id, {
      title: editForm.title,
      artist: editForm.artist,
      genreId: editForm.genreId,
      description: editForm.description,
      isPublic: editForm.isPublic
    })

    ElMessage.success('Music updated successfully')
    editDialogVisible.value = false
    loadMyMusic()
  } catch (error) {
    if (error !== false) { // validation error returns false
      console.error('Update failed:', error)
      ElMessage.error('Failed to update music')
    }
  } finally {
    saving.value = false
  }
}

async function handleDeleteConfirm(music) {
  try {
    await ElMessageBox.confirm(
      `Are you sure you want to delete "${music.title}"? This action cannot be undone.`,
      'Confirm Delete',
      {
        confirmButtonText: 'Delete',
        cancelButtonText: 'Cancel',
        type: 'warning'
      }
    )

    await musicStore.deleteMusic(music.id)
    ElMessage.success('Music deleted successfully')
    loadMyMusic()
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

// Lifecycle
onMounted(async () => {
  // Load genres if not loaded
  if (musicStore.genres.length === 0) {
    await musicStore.fetchGenres()
  }

  loadMyMusic()
})
</script>

<style scoped lang="scss">
.my-music-page {
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

// Stats Section
.stats-section {
  margin-bottom: 24px;

  .stat-card {
    :deep(.el-card__body) {
      padding: 20px;
    }

    .stat-content {
      display: flex;
      align-items: center;
      gap: 16px;

      .stat-icon {
        font-size: 40px;
      }

      .stat-info {
        flex: 1;

        .stat-value {
          font-size: 28px;
          font-weight: 600;
          color: #303133;
          line-height: 1;
          margin-bottom: 4px;
        }

        .stat-label {
          font-size: 13px;
          color: #909399;
        }
      }
    }
  }
}

// Filter Section
.filter-section {
  background: white;
  border-radius: 8px;
  padding: 20px;
  margin-bottom: 24px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);

  .filter-row {
    display: flex;
    gap: 12px;
    align-items: center;

    .search-input {
      flex: 1;
      max-width: 400px;
    }

    .filter-select {
      width: 150px;
    }

    .view-mode-toggle {
      margin-left: auto;
    }
  }
}

// Music Section
.music-section {
  .music-grid {
    display: grid;
    grid-template-columns: repeat(4, 1fr);
    gap: 20px;
  }

  .music-list {
    display: flex;
    flex-direction: column;
    gap: 8px;
  }
}

.pagination-wrapper {
  display: flex;
  justify-content: center;
  margin-top: 32px;
}

// Responsive
@media (max-width: 1200px) {
  .music-grid {
    grid-template-columns: repeat(3, 1fr) !important;
  }

  .stats-section {
    :deep(.el-col) {
      width: 50%;
      margin-bottom: 16px;
    }
  }
}

@media (max-width: 992px) {
  .music-grid {
    grid-template-columns: repeat(2, 1fr) !important;
  }
}

@media (max-width: 768px) {
  .my-music-page {
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
    .filter-row {
      flex-wrap: wrap;

      .search-input {
        flex: 1 1 100%;
        max-width: none;
      }

      .filter-select {
        flex: 1;
      }
    }
  }

  .music-grid {
    grid-template-columns: 1fr !important;
  }

  .stats-section {
    :deep(.el-col) {
      width: 100%;
    }
  }
}
</style>
