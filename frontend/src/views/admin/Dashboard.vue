<!-- Admin Dashboard Page -->
<template>
  <div class="admin-dashboard">
    <div class="dashboard-header">
      <h1>Admin Dashboard</h1>
      <p class="subtitle">Platform Overview & Statistics</p>
    </div>

    <!-- Statistics Cards -->
    <el-row :gutter="20" class="stats-row">
      <el-col :xs="24" :sm="12" :md="6">
        <el-card class="stat-card" shadow="hover">
          <div class="stat-content">
            <div class="stat-icon users">
              <el-icon :size="32"><User /></el-icon>
            </div>
            <div class="stat-info">
              <div class="stat-value">{{ formatNumber(stats?.totalUsers) }}</div>
              <div class="stat-label">Total Users</div>
              <div class="stat-detail">
                {{ formatNumber(stats?.activeUsers) }} active
              </div>
            </div>
          </div>
        </el-card>
      </el-col>

      <el-col :xs="24" :sm="12" :md="6">
        <el-card class="stat-card" shadow="hover">
          <div class="stat-content">
            <div class="stat-icon music">
              <el-icon :size="32"><Headset /></el-icon>
            </div>
            <div class="stat-info">
              <div class="stat-value">{{ formatNumber(stats?.approvedMusic) }}</div>
              <div class="stat-label">Approved Music</div>
              <div class="stat-detail">
                {{ formatNumber(stats?.totalMusic) }} total
              </div>
            </div>
          </div>
        </el-card>
      </el-col>

      <el-col :xs="24" :sm="12" :md="6">
        <el-card class="stat-card" shadow="hover">
          <div class="stat-content">
            <div class="stat-icon pending">
              <el-icon :size="32"><Clock /></el-icon>
            </div>
            <div class="stat-info">
              <div class="stat-value">{{ formatNumber(stats?.pendingMusic) }}</div>
              <div class="stat-label">Pending Music</div>
              <div class="stat-detail">
                Awaiting approval
              </div>
            </div>
          </div>
        </el-card>
      </el-col>

      <el-col :xs="24" :sm="12" :md="6">
        <el-card class="stat-card" shadow="hover">
          <div class="stat-content">
            <div class="stat-icon playlists">
              <el-icon :size="32"><Tickets /></el-icon>
            </div>
            <div class="stat-info">
              <div class="stat-value">{{ formatNumber(stats?.totalPlaylists) }}</div>
              <div class="stat-label">Playlists</div>
              <div class="stat-detail">
                {{ formatNumber(stats?.totalComments) }} comments
              </div>
            </div>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <!-- Today's Activity -->
    <el-card class="activity-card">
      <template #header>
        <div class="card-header">
          <el-icon><TrendCharts /></el-icon>
          <span>Today's Activity</span>
        </div>
      </template>
      <el-row :gutter="40">
        <el-col :xs="24" :sm="12">
          <div class="activity-item">
            <div class="activity-icon">
              <el-icon :size="24" color="#67C23A"><Upload /></el-icon>
            </div>
            <div class="activity-info">
              <div class="activity-value">{{ formatNumber(stats?.todayUploads) }}</div>
              <div class="activity-label">Music Uploads Today</div>
            </div>
          </div>
        </el-col>
        <el-col :xs="24" :sm="12">
          <div class="activity-item">
            <div class="activity-icon">
              <el-icon :size="24" color="#409EFF"><UserFilled /></el-icon>
            </div>
            <div class="activity-info">
              <div class="activity-value">{{ formatNumber(stats?.todayRegistrations) }}</div>
              <div class="activity-label">New Registrations Today</div>
            </div>
          </div>
        </el-col>
      </el-row>
    </el-card>

    <!-- Quick Actions -->
    <el-card class="actions-card">
      <template #header>
        <div class="card-header">
          <el-icon><Operation /></el-icon>
          <span>Quick Actions</span>
        </div>
      </template>
      <div class="quick-actions">
        <el-button
          type="primary"
          size="large"
          @click="router.push('/admin/users')"
        >
          <el-icon><User /></el-icon>
          <span>Manage Users</span>
        </el-button>
        <el-button
          type="warning"
          size="large"
          @click="router.push('/admin/audit')"
        >
          <el-icon><DocumentChecked /></el-icon>
          <span>Approve Music ({{ formatNumber(stats?.pendingMusic) }})</span>
        </el-button>
        <el-button
          size="large"
          @click="refreshStats"
          :loading="loading"
        >
          <el-icon><Refresh /></el-icon>
          <span>Refresh Stats</span>
        </el-button>
      </div>
    </el-card>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { useAdminStore } from '@/store/admin'
import {
  User,
  UserFilled,
  Headset,
  Clock,
  Tickets,
  TrendCharts,
  Upload,
  Operation,
  DocumentChecked,
  Refresh
} from '@element-plus/icons-vue'
import { ElMessage } from 'element-plus'

const router = useRouter()
const adminStore = useAdminStore()

const stats = ref(null)
const loading = ref(false)

/**
 * Format number with commas
 */
const formatNumber = (num) => {
  if (num === null || num === undefined) return '0'
  return num.toLocaleString()
}

/**
 * Load dashboard statistics
 */
const loadStats = async () => {
  try {
    loading.value = true
    stats.value = await adminStore.fetchDashboardStats()
  } catch (error) {
    console.error('Failed to load dashboard stats:', error)
  } finally {
    loading.value = false
  }
}

/**
 * Refresh statistics
 */
const refreshStats = async () => {
  await loadStats()
  ElMessage.success('Statistics refreshed')
}

onMounted(() => {
  loadStats()
})
</script>

<style scoped>
.admin-dashboard {
  padding: 20px;
  max-width: 1400px;
  margin: 0 auto;
}

.dashboard-header {
  margin-bottom: 30px;
}

.dashboard-header h1 {
  margin: 0 0 8px 0;
  font-size: 28px;
  font-weight: 600;
  color: #303133;
}

.dashboard-header .subtitle {
  margin: 0;
  color: #909399;
  font-size: 14px;
}

/* Statistics Cards */
.stats-row {
  margin-bottom: 20px;
}

.stat-card {
  margin-bottom: 20px;
  transition: transform 0.2s;
}

.stat-card:hover {
  transform: translateY(-4px);
}

.stat-content {
  display: flex;
  align-items: center;
  gap: 16px;
}

.stat-icon {
  width: 64px;
  height: 64px;
  border-radius: 12px;
  display: flex;
  align-items: center;
  justify-content: center;
  flex-shrink: 0;
}

.stat-icon.users {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
}

.stat-icon.music {
  background: linear-gradient(135deg, #f093fb 0%, #f5576c 100%);
  color: white;
}

.stat-icon.pending {
  background: linear-gradient(135deg, #fa709a 0%, #fee140 100%);
  color: white;
}

.stat-icon.playlists {
  background: linear-gradient(135deg, #4facfe 0%, #00f2fe 100%);
  color: white;
}

.stat-info {
  flex: 1;
}

.stat-value {
  font-size: 32px;
  font-weight: 700;
  color: #303133;
  line-height: 1;
  margin-bottom: 8px;
}

.stat-label {
  font-size: 14px;
  color: #606266;
  margin-bottom: 4px;
}

.stat-detail {
  font-size: 12px;
  color: #909399;
}

/* Activity Card */
.activity-card {
  margin-bottom: 20px;
}

.card-header {
  display: flex;
  align-items: center;
  gap: 8px;
  font-size: 16px;
  font-weight: 600;
}

.activity-item {
  display: flex;
  align-items: center;
  gap: 16px;
  padding: 20px 0;
}

.activity-icon {
  width: 48px;
  height: 48px;
  border-radius: 50%;
  background: #f5f7fa;
  display: flex;
  align-items: center;
  justify-content: center;
}

.activity-value {
  font-size: 28px;
  font-weight: 700;
  color: #303133;
  margin-bottom: 4px;
}

.activity-label {
  font-size: 14px;
  color: #606266;
}

/* Actions Card */
.actions-card {
  margin-bottom: 20px;
}

.quick-actions {
  display: flex;
  flex-wrap: wrap;
  gap: 12px;
}

.quick-actions .el-button {
  flex: 1;
  min-width: 180px;
}

/* Responsive */
@media (max-width: 768px) {
  .admin-dashboard {
    padding: 16px;
  }

  .dashboard-header h1 {
    font-size: 24px;
  }

  .stat-value {
    font-size: 24px;
  }

  .activity-value {
    font-size: 24px;
  }

  .quick-actions .el-button {
    min-width: 140px;
  }
}
</style>
