<!--
  * User Profile Page Component
  *
  * @description Complete user profile page displaying user information,
  *              statistics, and tabs for different content sections.
  *
  * @features
  * - Display user information (avatar, username, email, bio, stats)
  * - Tabs for: Overview, My Music, My Playlists
  * - Stats cards: Followers, Following, Music Count, Playlist Count
  * - Avatar upload button (placeholder for now)
  * - Edit profile button (navigate to settings)
  * - Integration with useUserStore
  * - Fetch user profile on mount
  * - Loading skeleton for initial data fetch
  * - Responsive design for all screen sizes
  *
  * @author MusicShare Team
  * @version 1.0.0
  -->

<template>
  <div class="profile-page">
    <div class="profile-container">
      <!-- Loading Skeleton -->
      <div v-if="loading" class="loading-container">
        <el-skeleton :rows="8" animated />
      </div>

      <!-- Profile Content -->
      <div v-else class="profile-content">
        <!-- Profile Header -->
        <el-card class="profile-header-card" shadow="hover">
          <div class="profile-header">
            <div class="avatar-section">
              <el-avatar :size="120" :src="currentUser?.avatar">
                <el-icon :size="60">
                  <User />
                </el-icon>
              </el-avatar>
              <el-button
                type="primary"
                size="small"
                circle
                class="avatar-upload-btn"
                @click="handleAvatarUpload"
              >
                <el-icon><Camera /></el-icon>
              </el-button>
            </div>

            <div class="user-info">
              <div class="user-header">
                <h1 class="username">
                  {{ currentUser?.nickname || currentUser?.username }}
                </h1>
                <el-tag v-if="userStore.isMusician" type="success" effect="dark">
                  Musician
                </el-tag>
                <el-tag v-if="userStore.isAdmin" type="danger" effect="dark">
                  Admin
                </el-tag>
              </div>

              <p class="user-subtitle">@{{ currentUser?.username }}</p>

              <div class="user-meta">
                <div class="meta-item">
                  <el-icon><Message /></el-icon>
                  <span>{{ currentUser?.email }}</span>
                </div>
                <div v-if="currentUser?.location" class="meta-item">
                  <el-icon><Location /></el-icon>
                  <span>{{ currentUser?.location }}</span>
                </div>
                <div v-if="currentUser?.createdAt" class="meta-item">
                  <el-icon><Calendar /></el-icon>
                  <span>Joined {{ formatDate(currentUser.createdAt) }}</span>
                </div>
              </div>

              <p v-if="currentUser?.bio" class="user-bio">
                {{ currentUser.bio }}
              </p>

              <div class="action-buttons">
                <el-button type="primary" @click="goToSettings">
                  <el-icon><Edit /></el-icon>
                  Edit Profile
                </el-button>
                <el-button>
                  <el-icon><Share /></el-icon>
                  Share Profile
                </el-button>
              </div>
            </div>
          </div>
        </el-card>

        <!-- Stats Cards -->
        <div class="stats-section">
          <el-row :gutter="20">
            <el-col :xs="12" :sm="6">
              <el-card class="stat-card" shadow="hover">
                <div class="stat-content">
                  <el-icon :size="30" color="#409EFF">
                    <User />
                  </el-icon>
                  <div class="stat-info">
                    <p class="stat-value">{{ stats.followers }}</p>
                    <p class="stat-label">Followers</p>
                  </div>
                </div>
              </el-card>
            </el-col>

            <el-col :xs="12" :sm="6">
              <el-card class="stat-card" shadow="hover">
                <div class="stat-content">
                  <el-icon :size="30" color="#67C23A">
                    <UserFilled />
                  </el-icon>
                  <div class="stat-info">
                    <p class="stat-value">{{ stats.following }}</p>
                    <p class="stat-label">Following</p>
                  </div>
                </div>
              </el-card>
            </el-col>

            <el-col :xs="12" :sm="6">
              <el-card class="stat-card" shadow="hover">
                <div class="stat-content">
                  <el-icon :size="30" color="#E6A23C">
                    <Headset />
                  </el-icon>
                  <div class="stat-info">
                    <p class="stat-value">{{ stats.musicCount }}</p>
                    <p class="stat-label">Music</p>
                  </div>
                </div>
              </el-card>
            </el-col>

            <el-col :xs="12" :sm="6">
              <el-card class="stat-card" shadow="hover">
                <div class="stat-content">
                  <el-icon :size="30" color="#F56C6C">
                    <Folder />
                  </el-icon>
                  <div class="stat-info">
                    <p class="stat-value">{{ stats.playlistCount }}</p>
                    <p class="stat-label">Playlists</p>
                  </div>
                </div>
              </el-card>
            </el-col>
          </el-row>
        </div>

        <!-- Tabs Section -->
        <el-card class="tabs-card" shadow="hover">
          <el-tabs v-model="activeTab" class="profile-tabs">
            <el-tab-pane label="Overview" name="overview">
              <div class="tab-content">
                <el-empty
                  description="Overview content coming soon"
                  :image-size="150"
                >
                  <template #image>
                    <el-icon :size="80" color="#909399">
                      <Document />
                    </el-icon>
                  </template>
                </el-empty>
              </div>
            </el-tab-pane>

            <el-tab-pane name="music">
              <template #label>
                <span class="tab-label">
                  <el-icon><Headset /></el-icon>
                  My Music
                </span>
              </template>
              <div class="tab-content">
                <el-empty
                  description="No music uploaded yet"
                  :image-size="150"
                >
                  <template #image>
                    <el-icon :size="80" color="#909399">
                      <Headset />
                    </el-icon>
                  </template>
                  <el-button v-if="userStore.isMusician" type="primary">
                    Upload Music
                  </el-button>
                </el-empty>
              </div>
            </el-tab-pane>

            <el-tab-pane name="playlists">
              <template #label>
                <span class="tab-label">
                  <el-icon><Folder /></el-icon>
                  My Playlists
                </span>
              </template>
              <div class="tab-content">
                <el-empty
                  description="No playlists created yet"
                  :image-size="150"
                >
                  <template #image>
                    <el-icon :size="80" color="#909399">
                      <Folder />
                    </el-icon>
                  </template>
                  <el-button type="primary">Create Playlist</el-button>
                </el-empty>
              </div>
            </el-tab-pane>
          </el-tabs>
        </el-card>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, computed } from 'vue'
import { useRouter } from 'vue-router'
import { useUserStore } from '@/store/user'
import {
  User,
  UserFilled,
  Camera,
  Edit,
  Share,
  Message,
  Location,
  Calendar,
  Headset,
  Folder,
  Document
} from '@element-plus/icons-vue'
import { ElMessage } from 'element-plus'

/**
 * Router instance for navigation
 * @type {import('vue-router').Router}
 */
const router = useRouter()

/**
 * User store instance
 * @type {ReturnType<typeof useUserStore>}
 */
const userStore = useUserStore()

/**
 * Loading state indicator
 * @type {import('vue').Ref<boolean>}
 */
const loading = ref(true)

/**
 * Active tab name
 * @type {import('vue').Ref<string>}
 */
const activeTab = ref('overview')

/**
 * User statistics data
 * @type {Object}
 * @property {number} followers - Number of followers
 * @property {number} following - Number of users being followed
 * @property {number} musicCount - Number of music tracks
 * @property {number} playlistCount - Number of playlists
 */
const stats = reactive({
  followers: 0,
  following: 0,
  musicCount: 0,
  playlistCount: 0
})

/**
 * Get current user from store
 * @type {import('vue').ComputedRef<Object|null>}
 */
const currentUser = computed(() => userStore.userInfo)

/**
 * Format date to readable string
 *
 * @param {string|Date} date - Date to format
 * @returns {string} Formatted date string
 */
const formatDate = (date) => {
  if (!date) return ''
  const d = new Date(date)
  const options = { year: 'numeric', month: 'short' }
  return d.toLocaleDateString('en-US', options)
}

/**
 * Navigate to settings page
 *
 * @returns {void}
 */
const goToSettings = () => {
  router.push('/user/settings')
}

/**
 * Handle avatar upload
 *
 * @description Placeholder for avatar upload functionality
 * @returns {void}
 */
const handleAvatarUpload = () => {
  ElMessage.info('Avatar upload feature coming soon!')
}

/**
 * Fetch user profile data
 *
 * @description Fetches user profile and statistics from the API
 * @returns {Promise<void>}
 */
const fetchProfileData = async () => {
  try {
    loading.value = true

    // Fetch user profile
    await userStore.fetchUserProfile()

    // TODO: Fetch statistics from API
    // For now, using mock data
    stats.followers = currentUser.value?.followersCount || 0
    stats.following = currentUser.value?.followingCount || 0
    stats.musicCount = currentUser.value?.musicCount || 0
    stats.playlistCount = currentUser.value?.playlistCount || 0
  } catch (error) {
    console.error('Failed to fetch profile data:', error)
    ElMessage.error('Failed to load profile data')
  } finally {
    loading.value = false
  }
}

/**
 * Initialize component
 *
 * @description Fetch profile data when component is mounted
 */
onMounted(() => {
  fetchProfileData()
})
</script>

<style scoped lang="scss">
.profile-page {
  min-height: calc(100vh - 60px);
  background: #f5f7fa;
  padding: 20px;
}

.profile-container {
  max-width: 1200px;
  margin: 0 auto;
}

.loading-container {
  background: white;
  padding: 40px;
  border-radius: 12px;
}

.profile-content {
  animation: fadeIn 0.5s ease-out;

  @keyframes fadeIn {
    from {
      opacity: 0;
      transform: translateY(20px);
    }
    to {
      opacity: 1;
      transform: translateY(0);
    }
  }
}

// Profile Header Card
.profile-header-card {
  margin-bottom: 20px;
  border-radius: 12px;

  :deep(.el-card__body) {
    padding: 30px;
  }
}

.profile-header {
  display: flex;
  gap: 30px;
  align-items: flex-start;

  @media (max-width: 768px) {
    flex-direction: column;
    align-items: center;
    text-align: center;
  }
}

.avatar-section {
  position: relative;
  flex-shrink: 0;

  .el-avatar {
    border: 4px solid #f0f2f5;
    box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
  }

  .avatar-upload-btn {
    position: absolute;
    bottom: 5px;
    right: 5px;
    width: 36px;
    height: 36px;
    box-shadow: 0 2px 8px rgba(0, 0, 0, 0.15);
  }
}

.user-info {
  flex: 1;
  min-width: 0;
}

.user-header {
  display: flex;
  align-items: center;
  gap: 12px;
  margin-bottom: 8px;
  flex-wrap: wrap;

  @media (max-width: 768px) {
    justify-content: center;
  }
}

.username {
  font-size: 28px;
  font-weight: 600;
  color: #303133;
  margin: 0;
  line-height: 1.2;
}

.user-subtitle {
  color: #909399;
  font-size: 16px;
  margin: 0 0 16px 0;
}

.user-meta {
  display: flex;
  flex-wrap: wrap;
  gap: 20px;
  margin-bottom: 16px;

  @media (max-width: 768px) {
    justify-content: center;
  }

  .meta-item {
    display: flex;
    align-items: center;
    gap: 6px;
    color: #606266;
    font-size: 14px;

    .el-icon {
      color: #909399;
      font-size: 16px;
    }
  }
}

.user-bio {
  color: #606266;
  font-size: 15px;
  line-height: 1.6;
  margin: 0 0 20px 0;
  max-width: 600px;
}

.action-buttons {
  display: flex;
  gap: 12px;
  flex-wrap: wrap;

  @media (max-width: 768px) {
    justify-content: center;
  }

  .el-button {
    .el-icon {
      margin-right: 6px;
    }
  }
}

// Stats Section
.stats-section {
  margin-bottom: 20px;
}

.stat-card {
  border-radius: 12px;
  transition: all 0.3s;
  cursor: default;

  &:hover {
    transform: translateY(-4px);
  }

  :deep(.el-card__body) {
    padding: 20px;
  }
}

.stat-content {
  display: flex;
  align-items: center;
  gap: 16px;
}

.stat-info {
  flex: 1;
  min-width: 0;
}

.stat-value {
  font-size: 24px;
  font-weight: 600;
  color: #303133;
  margin: 0 0 4px 0;
  line-height: 1;
}

.stat-label {
  font-size: 13px;
  color: #909399;
  margin: 0;
  line-height: 1;
}

// Tabs Card
.tabs-card {
  border-radius: 12px;

  :deep(.el-card__body) {
    padding: 0;
  }
}

.profile-tabs {
  :deep(.el-tabs__header) {
    padding: 0 20px;
    margin: 0;
    background: #fafafa;
    border-radius: 12px 12px 0 0;
  }

  :deep(.el-tabs__nav-wrap::after) {
    height: 1px;
  }

  :deep(.el-tabs__item) {
    font-size: 15px;
    font-weight: 500;
    padding: 0 20px;
    height: 50px;
    line-height: 50px;
  }

  :deep(.el-tabs__content) {
    padding: 0;
  }

  .tab-label {
    display: flex;
    align-items: center;
    gap: 6px;

    .el-icon {
      font-size: 16px;
    }
  }
}

.tab-content {
  padding: 40px 20px;
  min-height: 400px;
  display: flex;
  align-items: center;
  justify-content: center;

  :deep(.el-empty) {
    .el-empty__description {
      margin-top: 16px;
      font-size: 15px;
      color: #909399;
    }

    .el-button {
      margin-top: 20px;
    }
  }
}

// Responsive adjustments
@media (max-width: 768px) {
  .profile-page {
    padding: 12px;
  }

  .profile-header-card {
    :deep(.el-card__body) {
      padding: 20px;
    }
  }

  .username {
    font-size: 24px;
  }

  .stats-section {
    :deep(.el-col) {
      margin-bottom: 12px;

      &:last-child {
        margin-bottom: 0;
      }
    }
  }

  .stat-card {
    :deep(.el-card__body) {
      padding: 16px;
    }
  }

  .stat-value {
    font-size: 20px;
  }

  .stat-label {
    font-size: 12px;
  }

  .tab-content {
    padding: 30px 15px;
    min-height: 300px;
  }
}

@media (max-width: 576px) {
  .profile-tabs {
    :deep(.el-tabs__item) {
      font-size: 14px;
      padding: 0 12px;
    }

    .tab-label {
      .el-icon {
        display: none;
      }
    }
  }
}
</style>
