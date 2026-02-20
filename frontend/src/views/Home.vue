<template>
  <div class="home">
    <el-container>
      <el-header>
        <div class="header-content">
          <h1>🎵 MusicShare Platform</h1>
          <div class="header-actions">
            <el-button type="primary" @click="handleLogin" v-if="!isLoggedIn">Login</el-button>
            <el-button @click="handleRegister" v-if="!isLoggedIn">Register</el-button>
            <el-dropdown v-else>
              <span class="user-info">
                {{ username }}
                <el-icon class="el-icon--right"><arrow-down /></el-icon>
              </span>
              <template #dropdown>
                <el-dropdown-menu>
                  <el-dropdown-item @click="goToProfile">Profile</el-dropdown-item>
                  <el-dropdown-item @click="goToSettings">Settings</el-dropdown-item>
                  <el-dropdown-item divided @click="handleLogout">Logout</el-dropdown-item>
                </el-dropdown-menu>
              </template>
            </el-dropdown>
          </div>
        </div>
      </el-header>

      <el-main>
        <div class="welcome-section">
          <h2>Welcome to MusicShare! 🎶</h2>
          <p>A community platform for music lovers and independent musicians</p>

          <el-card class="info-card">
            <h3>🚀 Project Status</h3>
            <el-divider />

            <el-row :gutter="20">
              <el-col :span="8">
                <el-statistic title="Frontend" :value="100" suffix="%" />
                <p class="status-label">✅ Initialized</p>
              </el-col>
              <el-col :span="8">
                <el-statistic title="Backend" :value="0" suffix="%" />
                <p class="status-label">⏳ Pending</p>
              </el-col>
              <el-col :span="8">
                <el-statistic title="Overall" :value="20" suffix="%" />
                <p class="status-label">🔨 In Progress</p>
              </el-col>
            </el-row>
          </el-card>

          <el-card class="features-card">
            <h3>✨ Core Features</h3>
            <el-divider />

            <el-row :gutter="20">
              <el-col :xs="24" :sm="12" :md="6">
                <div class="feature-item">
                  <el-icon :size="40" color="#409eff"><headset /></el-icon>
                  <h4>Music Playback</h4>
                  <p>High-quality streaming</p>
                </div>
              </el-col>
              <el-col :xs="24" :sm="12" :md="6">
                <div class="feature-item">
                  <el-icon :size="40" color="#67c23a"><upload-filled /></el-icon>
                  <h4>Upload Music</h4>
                  <p>Share your creations</p>
                </div>
              </el-col>
              <el-col :xs="24" :sm="12" :md="6">
                <div class="feature-item">
                  <el-icon :size="40" color="#e6a23c"><collection-tag /></el-icon>
                  <h4>Playlists</h4>
                  <p>Organize your favorites</p>
                </div>
              </el-col>
              <el-col :xs="24" :sm="12" :md="6">
                <div class="feature-item">
                  <el-icon :size="40" color="#f56c6c"><user /></el-icon>
                  <h4>Social</h4>
                  <p>Connect with others</p>
                </div>
              </el-col>
            </el-row>
          </el-card>

          <el-alert
            title="Environment Setup Required"
            type="warning"
            :closable="false"
            show-icon
            style="margin-top: 20px;"
          >
            <p>Backend services need to be started. Please install JDK 17+ and Maven 3.6+, then run backend services.</p>
            <p>See <strong>ENVIRONMENT_SETUP.md</strong> for detailed instructions.</p>
          </el-alert>
        </div>
      </el-main>
    </el-container>
  </div>
</template>

<script setup>
/**
 * Home page component
 *
 * @description Landing page displaying platform overview and features
 *
 * @author MusicShare Team
 * @version 1.0.0
 */

import { computed } from 'vue'
import { useRouter } from 'vue-router'
import { useUserStore } from '@/store/user'
import { ElMessage } from 'element-plus'
import { ArrowDown, Headset, UploadFilled, CollectionTag, User } from '@element-plus/icons-vue'

const router = useRouter()
const userStore = useUserStore()

const isLoggedIn = computed(() => userStore.isLoggedIn)
const username = computed(() => userStore.username)

function handleLogin() {
  router.push('/login')
}

function handleRegister() {
  router.push('/register')
}

function goToProfile() {
  router.push('/profile')
}

function goToSettings() {
  router.push('/settings')
}

function handleLogout() {
  userStore.logout()
  ElMessage.success('Logged out successfully')
}
</script>

<style scoped lang="scss">
.home {
  min-height: 100vh;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
}

.el-header {
  background-color: rgba(255, 255, 255, 0.95);
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
  padding: 0;
}

.header-content {
  max-width: 1200px;
  margin: 0 auto;
  height: 60px;
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 0 20px;

  h1 {
    margin: 0;
    font-size: 24px;
    background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
    -webkit-background-clip: text;
    -webkit-text-fill-color: transparent;
  }

  .user-info {
    cursor: pointer;
    display: flex;
    align-items: center;
    padding: 8px 16px;
    border-radius: 4px;
    transition: background-color 0.3s;

    &:hover {
      background-color: #f5f7fa;
    }
  }
}

.el-main {
  padding: 40px 20px;
}

.welcome-section {
  max-width: 1200px;
  margin: 0 auto;
  text-align: center;

  h2 {
    color: white;
    font-size: 48px;
    margin: 0 0 16px;
  }

  > p {
    color: rgba(255, 255, 255, 0.9);
    font-size: 20px;
    margin-bottom: 40px;
  }
}

.info-card,
.features-card {
  margin-bottom: 20px;
  text-align: left;

  h3 {
    margin: 0 0 16px;
    font-size: 24px;
  }
}

.status-label {
  text-align: center;
  margin-top: 8px;
  font-size: 14px;
  color: #606266;
}

.feature-item {
  text-align: center;
  padding: 20px;

  h4 {
    margin: 16px 0 8px;
    font-size: 18px;
  }

  p {
    margin: 0;
    color: #909399;
    font-size: 14px;
  }
}
</style>
