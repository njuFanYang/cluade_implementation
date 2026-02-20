<template>
  <header class="app-header">
    <div class="header-container">
      <!-- Logo and Brand -->
      <div class="header-left">
        <router-link to="/" class="logo">
          <el-icon :size="28"><Headset /></el-icon>
          <span class="brand-name">MusicShare</span>
        </router-link>

        <!-- Main Navigation -->
        <nav class="main-nav">
          <router-link to="/music" class="nav-item">
            <el-icon><Headset /></el-icon>
            <span>Music</span>
          </router-link>
          <router-link to="/playlists" class="nav-item">
            <el-icon><FolderOpened /></el-icon>
            <span>Playlists</span>
          </router-link>
          <router-link to="/genres" class="nav-item">
            <el-icon><Discount /></el-icon>
            <span>Genres</span>
          </router-link>
        </nav>
      </div>

      <!-- Search -->
      <div class="header-center">
        <el-input
          v-model="searchKeyword"
          placeholder="Search music, playlists, artists..."
          :prefix-icon="Search"
          clearable
          @keyup.enter="handleSearch"
          class="search-input"
        >
          <template #append>
            <el-button :icon="Search" @click="handleSearch" />
          </template>
        </el-input>
      </div>

      <!-- User Actions -->
      <div class="header-right">
        <template v-if="userStore.isLoggedIn">
          <!-- Upload Button (for Musicians/Admins) -->
          <el-button
            v-if="userStore.hasRole('MUSICIAN') || userStore.hasRole('ADMIN')"
            type="primary"
            :icon="Upload"
            @click="router.push('/music/upload')"
            class="upload-btn"
          >
            Upload
          </el-button>

          <!-- User Menu -->
          <el-dropdown trigger="click" @command="handleUserCommand">
            <div class="user-menu-trigger">
              <el-avatar :size="36" :src="userStore.currentUser?.avatar">
                {{ userStore.currentUser?.username?.charAt(0).toUpperCase() }}
              </el-avatar>
              <span class="username">{{ userStore.currentUser?.username }}</span>
              <el-icon><ArrowDown /></el-icon>
            </div>
            <template #dropdown>
              <el-dropdown-menu>
                <el-dropdown-item command="profile" :icon="User">
                  My Profile
                </el-dropdown-item>
                <el-dropdown-item command="my-music" :icon="Headset" divided>
                  My Music
                </el-dropdown-item>
                <el-dropdown-item command="my-playlists" :icon="Folder">
                  My Playlists
                </el-dropdown-item>
                <el-dropdown-item command="settings" :icon="Setting" divided>
                  Settings
                </el-dropdown-item>
                <el-dropdown-item
                  v-if="userStore.hasRole('ADMIN')"
                  command="admin"
                  :icon="Tools"
                  divided
                >
                  Admin Dashboard
                </el-dropdown-item>
                <el-dropdown-item command="logout" :icon="SwitchButton" divided>
                  Logout
                </el-dropdown-item>
              </el-dropdown-menu>
            </template>
          </el-dropdown>
        </template>

        <template v-else>
          <el-button @click="router.push('/login')">Login</el-button>
          <el-button type="primary" @click="router.push('/register')">
            Register
          </el-button>
        </template>
      </div>

      <!-- Mobile Menu Toggle -->
      <el-button
        class="mobile-menu-toggle"
        :icon="Menu"
        text
        @click="mobileMenuVisible = true"
      />
    </div>

    <!-- Mobile Menu Drawer -->
    <el-drawer
      v-model="mobileMenuVisible"
      title="Menu"
      direction="ltr"
      size="280px"
    >
      <div class="mobile-menu">
        <!-- User Info or Login -->
        <div v-if="userStore.isLoggedIn" class="mobile-user-info">
          <el-avatar :size="60" :src="userStore.currentUser?.avatar">
            {{ userStore.currentUser?.username?.charAt(0).toUpperCase() }}
          </el-avatar>
          <div class="user-name">{{ userStore.currentUser?.username }}</div>
          <div class="user-role">{{ userStore.currentUser?.role }}</div>
        </div>
        <div v-else class="mobile-login-actions">
          <el-button type="primary" block @click="goToPage('/login')">
            Login
          </el-button>
          <el-button block @click="goToPage('/register')">Register</el-button>
        </div>

        <el-divider />

        <!-- Main Navigation Links -->
        <div class="mobile-nav-links">
          <div class="nav-link" @click="goToPage('/music')">
            <el-icon><Headset /></el-icon>
            <span>Music</span>
          </div>
          <div class="nav-link" @click="goToPage('/playlists')">
            <el-icon><FolderOpened /></el-icon>
            <span>Playlists</span>
          </div>
          <div class="nav-link" @click="goToPage('/genres')">
            <el-icon><Discount /></el-icon>
            <span>Genres</span>
          </div>
        </div>

        <template v-if="userStore.isLoggedIn">
          <el-divider />

          <!-- User Links -->
          <div class="mobile-nav-links">
            <div class="nav-link" @click="goToPage('/profile')">
              <el-icon><User /></el-icon>
              <span>My Profile</span>
            </div>
            <div class="nav-link" @click="goToPage('/music/my')">
              <el-icon><Headset /></el-icon>
              <span>My Music</span>
            </div>
            <div class="nav-link" @click="goToPage('/playlists/my')">
              <el-icon><Folder /></el-icon>
              <span>My Playlists</span>
            </div>
            <div class="nav-link" @click="goToPage('/settings')">
              <el-icon><Setting /></el-icon>
              <span>Settings</span>
            </div>
            <div
              v-if="userStore.hasRole('ADMIN')"
              class="nav-link"
              @click="goToPage('/admin')"
            >
              <el-icon><Tools /></el-icon>
              <span>Admin Dashboard</span>
            </div>
            <div class="nav-link logout" @click="handleLogout">
              <el-icon><SwitchButton /></el-icon>
              <span>Logout</span>
            </div>
          </div>
        </template>
      </div>
    </el-drawer>
  </header>
</template>

<script setup>
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import { useUserStore } from '@/store/user'
import {
  Headset,
  FolderOpened,
  Discount,
  Search,
  Upload,
  User,
  Folder,
  Setting,
  Tools,
  SwitchButton,
  ArrowDown,
  Menu
} from '@element-plus/icons-vue'
import { ElMessage } from 'element-plus'

const router = useRouter()
const userStore = useUserStore()

// State
const searchKeyword = ref('')
const mobileMenuVisible = ref(false)

// Methods
function handleSearch() {
  if (!searchKeyword.value.trim()) {
    ElMessage.warning('Please enter search keyword')
    return
  }

  router.push({
    path: '/search',
    query: { q: searchKeyword.value }
  })

  searchKeyword.value = ''
}

function handleUserCommand(command) {
  switch (command) {
    case 'profile':
      router.push('/profile')
      break
    case 'my-music':
      router.push('/music/my')
      break
    case 'my-playlists':
      router.push('/playlists/my')
      break
    case 'settings':
      router.push('/settings')
      break
    case 'admin':
      router.push('/admin')
      break
    case 'logout':
      handleLogout()
      break
  }
}

function handleLogout() {
  userStore.logout()
  ElMessage.success('Logged out successfully')
  router.push('/login')
  mobileMenuVisible.value = false
}

function goToPage(path) {
  router.push(path)
  mobileMenuVisible.value = false
}
</script>

<style scoped lang="scss">
.app-header {
  position: sticky;
  top: 0;
  z-index: 999;
  background: white;
  border-bottom: 1px solid #ebeef5;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.05);
}

.header-container {
  max-width: 1400px;
  margin: 0 auto;
  padding: 0 20px;
  height: 64px;
  display: flex;
  align-items: center;
  gap: 24px;
}

// Left Section
.header-left {
  display: flex;
  align-items: center;
  gap: 32px;

  .logo {
    display: flex;
    align-items: center;
    gap: 8px;
    text-decoration: none;
    color: #409eff;
    font-size: 20px;
    font-weight: 600;
    transition: opacity 0.3s;

    &:hover {
      opacity: 0.8;
    }

    .brand-name {
      background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
      -webkit-background-clip: text;
      -webkit-text-fill-color: transparent;
      background-clip: text;
    }
  }

  .main-nav {
    display: flex;
    gap: 4px;

    .nav-item {
      display: flex;
      align-items: center;
      gap: 6px;
      padding: 8px 16px;
      border-radius: 6px;
      text-decoration: none;
      color: #606266;
      font-size: 15px;
      font-weight: 500;
      transition: all 0.3s;

      &:hover {
        background-color: #f5f7fa;
        color: #409eff;
      }

      &.router-link-active {
        background-color: #ecf5ff;
        color: #409eff;
      }
    }
  }
}

// Center Section
.header-center {
  flex: 1;
  max-width: 500px;

  .search-input {
    width: 100%;
  }
}

// Right Section
.header-right {
  display: flex;
  align-items: center;
  gap: 12px;

  .upload-btn {
    font-weight: 500;
  }

  .user-menu-trigger {
    display: flex;
    align-items: center;
    gap: 8px;
    padding: 6px 12px 6px 6px;
    border-radius: 20px;
    cursor: pointer;
    transition: background-color 0.3s;

    &:hover {
      background-color: #f5f7fa;
    }

    .username {
      font-size: 14px;
      font-weight: 500;
      color: #303133;
      max-width: 120px;
      overflow: hidden;
      text-overflow: ellipsis;
      white-space: nowrap;
    }
  }
}

// Mobile Menu
.mobile-menu-toggle {
  display: none;
  font-size: 24px;
}

.mobile-menu {
  .mobile-user-info {
    text-align: center;
    padding: 20px 0;

    .user-name {
      font-size: 18px;
      font-weight: 600;
      color: #303133;
      margin: 12px 0 4px;
    }

    .user-role {
      font-size: 13px;
      color: #909399;
    }
  }

  .mobile-login-actions {
    padding: 20px 0;
    display: flex;
    flex-direction: column;
    gap: 12px;
  }

  .mobile-nav-links {
    display: flex;
    flex-direction: column;
    gap: 4px;

    .nav-link {
      display: flex;
      align-items: center;
      gap: 12px;
      padding: 12px 16px;
      border-radius: 8px;
      cursor: pointer;
      font-size: 15px;
      color: #303133;
      transition: background-color 0.3s;

      &:hover {
        background-color: #f5f7fa;
      }

      &.logout {
        color: #f56c6c;
      }

      .el-icon {
        font-size: 18px;
      }
    }
  }
}

// Responsive
@media (max-width: 992px) {
  .header-left .main-nav {
    display: none;
  }

  .header-center {
    max-width: 300px;
  }

  .header-right .username {
    display: none;
  }
}

@media (max-width: 768px) {
  .header-container {
    padding: 0 12px;
  }

  .header-left .brand-name {
    display: none;
  }

  .header-center {
    display: none;
  }

  .header-right {
    display: none;
  }

  .mobile-menu-toggle {
    display: block;
    margin-left: auto;
  }
}
</style>
