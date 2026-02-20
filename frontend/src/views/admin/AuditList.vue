<!-- Music Approval Page -->
<template>
  <div class="music-audit">
    <div class="page-header">
      <h1>Music Approval</h1>
      <p class="subtitle">Review and manage music submissions</p>
    </div>

    <!-- Status Tabs -->
    <el-card class="tabs-card">
      <el-tabs v-model="activeTab" @tab-change="handleTabChange">
        <el-tab-pane label="Pending" name="PENDING">
          <template #label>
            <span class="tab-label">
              <el-icon><Clock /></el-icon>
              Pending
              <el-badge
                v-if="pendingCount > 0"
                :value="pendingCount"
                type="warning"
                class="tab-badge"
              />
            </span>
          </template>
        </el-tab-pane>
        <el-tab-pane label="Approved" name="APPROVED">
          <template #label>
            <span class="tab-label">
              <el-icon><Select /></el-icon>
              Approved
            </span>
          </template>
        </el-tab-pane>
        <el-tab-pane label="Rejected" name="REJECTED">
          <template #label>
            <span class="tab-label">
              <el-icon><Close /></el-icon>
              Rejected
            </span>
          </template>
        </el-tab-pane>
      </el-tabs>

      <!-- Music Table -->
      <el-table
        :data="musicList.content"
        v-loading="loading"
        style="width: 100%"
        stripe
      >
        <el-table-column prop="id" label="ID" width="80" />
        <el-table-column label="Cover" width="80">
          <template #default="{ row }">
            <el-image
              v-if="row.coverImage"
              :src="row.coverImage"
              :preview-src-list="[row.coverImage]"
              fit="cover"
              style="width: 50px; height: 50px; border-radius: 4px"
            />
            <div v-else class="no-cover">
              <el-icon><Picture /></el-icon>
            </div>
          </template>
        </el-table-column>
        <el-table-column prop="title" label="Title" min-width="150" />
        <el-table-column prop="artist" label="Artist" min-width="120" />
        <el-table-column label="Genre" width="100">
          <template #default="{ row }">
            {{ getGenreName(row.genreId) }}
          </template>
        </el-table-column>
        <el-table-column prop="uploaderName" label="Uploader" width="120" />
        <el-table-column label="Duration" width="100">
          <template #default="{ row }">
            {{ formatDuration(row.duration) }}
          </template>
        </el-table-column>
        <el-table-column label="Upload Date" width="180">
          <template #default="{ row }">
            {{ formatDate(row.createdAt) }}
          </template>
        </el-table-column>
        <el-table-column label="Status" width="100">
          <template #default="{ row }">
            <el-tag :type="getStatusType(row.status)" size="small">
              {{ row.status }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="Operations" width="200" fixed="right">
          <template #default="{ row }">
            <div v-if="row.status === 'PENDING'">
              <el-button
                size="small"
                type="success"
                @click="confirmApprove(row)"
              >
                Approve
              </el-button>
              <el-button
                size="small"
                type="danger"
                @click="showRejectDialog(row)"
              >
                Reject
              </el-button>
            </div>
            <div v-else>
              <el-button size="small" disabled>
                {{ row.status === 'APPROVED' ? 'Approved' : 'Rejected' }}
              </el-button>
            </div>
          </template>
        </el-table-column>
      </el-table>

      <!-- Pagination -->
      <div class="pagination-container">
        <el-pagination
          v-model:current-page="pagination.page"
          v-model:page-size="pagination.size"
          :page-sizes="[10, 20, 50, 100]"
          :total="musicList.totalElements"
          layout="total, sizes, prev, pager, next, jumper"
          @size-change="loadMusic"
          @current-change="loadMusic"
        />
      </div>
    </el-card>

    <!-- Reject Dialog -->
    <el-dialog
      v-model="rejectDialogVisible"
      title="Reject Music"
      width="500px"
    >
      <div v-if="selectedMusic" class="reject-dialog">
        <p class="music-info">
          <strong>{{ selectedMusic.title }}</strong> by {{ selectedMusic.artist }}
        </p>
        <el-form :model="rejectForm" label-width="120px">
          <el-form-item label="Reason" prop="reason">
            <el-input
              v-model="rejectForm.reason"
              type="textarea"
              :rows="4"
              placeholder="Enter rejection reason (optional)"
              maxlength="500"
              show-word-limit
            />
          </el-form-item>
        </el-form>
      </div>
      <template #footer>
        <el-button @click="rejectDialogVisible = false">Cancel</el-button>
        <el-button type="danger" @click="confirmReject" :loading="loading">
          Reject
        </el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, computed } from 'vue'
import { useAdminStore } from '@/store/admin'
import { useGenreStore } from '@/store/genre'
import {
  Clock,
  Select,
  Close,
  Picture
} from '@element-plus/icons-vue'
import { ElMessage, ElMessageBox } from 'element-plus'

const adminStore = useAdminStore()
const genreStore = useGenreStore()

const loading = ref(false)
const activeTab = ref('PENDING')
const musicList = ref({ content: [], totalElements: 0 })
const rejectDialogVisible = ref(false)
const selectedMusic = ref(null)

const pagination = reactive({
  page: 1,
  size: 20
})

const rejectForm = reactive({
  reason: ''
})

/**
 * Get pending music count
 */
const pendingCount = computed(() => {
  return activeTab.value === 'PENDING' ? musicList.value.totalElements : 0
})

/**
 * Get genre name by ID
 */
const getGenreName = (genreId) => {
  const genre = genreStore.genres.find(g => g.id === genreId)
  return genre?.name || '-'
}

/**
 * Get status tag type
 */
const getStatusType = (status) => {
  switch (status) {
    case 'PENDING':
      return 'warning'
    case 'APPROVED':
      return 'success'
    case 'REJECTED':
      return 'danger'
    default:
      return 'info'
  }
}

/**
 * Format duration (seconds to mm:ss)
 */
const formatDuration = (seconds) => {
  if (!seconds) return '00:00'
  const minutes = Math.floor(seconds / 60)
  const secs = seconds % 60
  return `${String(minutes).padStart(2, '0')}:${String(secs).padStart(2, '0')}`
}

/**
 * Format date
 */
const formatDate = (dateString) => {
  if (!dateString) return '-'
  const date = new Date(dateString)
  return `${date.toLocaleDateString()} ${date.toLocaleTimeString()}`
}

/**
 * Load music by status
 */
const loadMusic = async () => {
  try {
    loading.value = true
    const params = {
      status: activeTab.value,
      page: pagination.page - 1,
      size: pagination.size
    }
    musicList.value = await adminStore.fetchMusicByStatus(params)
  } catch (error) {
    console.error('Failed to load music:', error)
  } finally {
    loading.value = false
  }
}

/**
 * Handle tab change
 */
const handleTabChange = () => {
  pagination.page = 1
  loadMusic()
}

/**
 * Confirm approve music
 */
const confirmApprove = async (music) => {
  try {
    await ElMessageBox.confirm(
      `Approve "${music.title}" by ${music.artist}?`,
      'Confirm Approval',
      {
        type: 'success',
        confirmButtonText: 'Approve'
      }
    )

    await adminStore.approveMusic(music.id)
    await loadMusic()
  } catch (error) {
    if (error !== 'cancel') {
      console.error('Failed to approve music:', error)
    }
  }
}

/**
 * Show reject dialog
 */
const showRejectDialog = (music) => {
  selectedMusic.value = music
  rejectForm.reason = ''
  rejectDialogVisible.value = true
}

/**
 * Confirm reject music
 */
const confirmReject = async () => {
  try {
    await adminStore.rejectMusic(selectedMusic.value.id, rejectForm.reason)
    rejectDialogVisible.value = false
    await loadMusic()
  } catch (error) {
    console.error('Failed to reject music:', error)
  }
}

onMounted(async () => {
  // Load genres if not loaded
  if (genreStore.genres.length === 0) {
    await genreStore.fetchGenres()
  }
  // Load music
  await loadMusic()
})
</script>

<style scoped>
.music-audit {
  padding: 20px;
  max-width: 1600px;
  margin: 0 auto;
}

.page-header {
  margin-bottom: 20px;
}

.page-header h1 {
  margin: 0 0 8px 0;
  font-size: 28px;
  font-weight: 600;
  color: #303133;
}

.page-header .subtitle {
  margin: 0;
  color: #909399;
  font-size: 14px;
}

.tabs-card {
  margin-bottom: 20px;
}

.tab-label {
  display: flex;
  align-items: center;
  gap: 6px;
}

.tab-badge {
  margin-left: 4px;
}

.no-cover {
  width: 50px;
  height: 50px;
  border-radius: 4px;
  background: #f5f7fa;
  display: flex;
  align-items: center;
  justify-content: center;
  color: #c0c4cc;
}

.pagination-container {
  margin-top: 20px;
  display: flex;
  justify-content: center;
}

.reject-dialog {
  padding: 10px 0;
}

.music-info {
  margin-bottom: 20px;
  padding: 12px;
  background: #f5f7fa;
  border-radius: 4px;
  color: #606266;
}

.music-info strong {
  color: #303133;
}

/* Responsive */
@media (max-width: 768px) {
  .music-audit {
    padding: 16px;
  }

  .page-header h1 {
    font-size: 24px;
  }

  :deep(.el-table) {
    font-size: 12px;
  }

  :deep(.el-table__cell) {
    padding: 8px 0;
  }
}
</style>
