<!-- Admin User Management Page -->
<template>
  <div class="user-manage">
    <div class="page-header">
      <h1>User Management</h1>
      <p class="subtitle">Manage all users in the platform</p>
    </div>

    <!-- Search and Filter Bar -->
    <el-card class="search-card">
      <el-form :inline="true" :model="searchForm" class="search-form">
        <el-form-item label="Search">
          <el-input
            v-model="searchForm.keyword"
            placeholder="Username or email"
            clearable
            style="width: 240px"
            @clear="handleSearch"
          >
            <template #prefix>
              <el-icon><Search /></el-icon>
            </template>
          </el-input>
        </el-form-item>
        <el-form-item label="Status">
          <el-select
            v-model="searchForm.enabled"
            placeholder="All"
            clearable
            style="width: 120px"
            @change="handleSearch"
          >
            <el-option label="All" :value="null" />
            <el-option label="Enabled" :value="true" />
            <el-option label="Disabled" :value="false" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleSearch" :loading="loading">
            <el-icon><Search /></el-icon>
            <span>Search</span>
          </el-button>
          <el-button @click="handleReset">
            <el-icon><Refresh /></el-icon>
            <span>Reset</span>
          </el-button>
        </el-form-item>
      </el-form>
    </el-card>

    <!-- User Table -->
    <el-card class="table-card">
      <el-table
        :data="users.content"
        v-loading="loading"
        style="width: 100%"
        stripe
      >
        <el-table-column prop="id" label="ID" width="80" />
        <el-table-column prop="username" label="Username" min-width="120" />
        <el-table-column prop="email" label="Email" min-width="180" />
        <el-table-column label="Roles" min-width="150">
          <template #default="{ row }">
            <el-tag
              v-for="role in row.roles"
              :key="role"
              :type="getRoleType(role)"
              size="small"
              style="margin-right: 4px"
            >
              {{ formatRole(role) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="Status" width="100">
          <template #default="{ row }">
            <el-tag :type="row.enabled ? 'success' : 'danger'" size="small">
              {{ row.enabled ? 'Enabled' : 'Disabled' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="musicCount" label="Music" width="80" align="center" />
        <el-table-column prop="followersCount" label="Followers" width="90" align="center" />
        <el-table-column label="Created" width="180">
          <template #default="{ row }">
            {{ formatDate(row.createdAt) }}
          </template>
        </el-table-column>
        <el-table-column label="Operations" width="240" fixed="right">
          <template #default="{ row }">
            <el-button
              size="small"
              @click="viewUserDetails(row)"
            >
              Details
            </el-button>
            <el-button
              size="small"
              :type="row.enabled ? 'warning' : 'success'"
              @click="toggleUserStatus(row)"
              :disabled="isAdmin(row)"
            >
              {{ row.enabled ? 'Disable' : 'Enable' }}
            </el-button>
            <el-button
              size="small"
              type="danger"
              @click="confirmDeleteUser(row)"
              :disabled="isAdmin(row)"
            >
              Delete
            </el-button>
          </template>
        </el-table-column>
      </el-table>

      <!-- Pagination -->
      <div class="pagination-container">
        <el-pagination
          v-model:current-page="pagination.page"
          v-model:page-size="pagination.size"
          :page-sizes="[10, 20, 50, 100]"
          :total="users.totalElements"
          layout="total, sizes, prev, pager, next, jumper"
          @size-change="handleSearch"
          @current-change="handleSearch"
        />
      </div>
    </el-card>

    <!-- User Details Dialog -->
    <el-dialog
      v-model="detailsDialogVisible"
      title="User Details"
      width="600px"
    >
      <div v-if="selectedUser" class="user-details">
        <el-descriptions :column="2" border>
          <el-descriptions-item label="ID">{{ selectedUser.id }}</el-descriptions-item>
          <el-descriptions-item label="Username">{{ selectedUser.username }}</el-descriptions-item>
          <el-descriptions-item label="Email">{{ selectedUser.email }}</el-descriptions-item>
          <el-descriptions-item label="Nickname">{{ selectedUser.nickname || '-' }}</el-descriptions-item>
          <el-descriptions-item label="Status">
            <el-tag :type="selectedUser.enabled ? 'success' : 'danger'" size="small">
              {{ selectedUser.enabled ? 'Enabled' : 'Disabled' }}
            </el-tag>
          </el-descriptions-item>
          <el-descriptions-item label="Locked">
            <el-tag :type="selectedUser.locked ? 'danger' : 'success'" size="small">
              {{ selectedUser.locked ? 'Yes' : 'No' }}
            </el-tag>
          </el-descriptions-item>
          <el-descriptions-item label="Music Count">{{ selectedUser.musicCount }}</el-descriptions-item>
          <el-descriptions-item label="Followers">{{ selectedUser.followersCount }}</el-descriptions-item>
          <el-descriptions-item label="Created At">{{ formatDate(selectedUser.createdAt) }}</el-descriptions-item>
          <el-descriptions-item label="Last Login">
            {{ selectedUser.lastLoginTime ? formatDate(selectedUser.lastLoginTime) : 'Never' }}
          </el-descriptions-item>
          <el-descriptions-item label="Last Login IP">{{ selectedUser.lastLoginIp || '-' }}</el-descriptions-item>
        </el-descriptions>

        <el-divider />

        <div class="role-management">
          <h4>Role Management</h4>
          <div class="roles-display">
            <el-tag
              v-for="role in selectedUser.roles"
              :key="role"
              :type="getRoleType(role)"
              size="large"
              style="margin-right: 8px"
            >
              {{ formatRole(role) }}
            </el-tag>
          </div>

          <div class="role-actions">
            <el-button
              size="small"
              type="primary"
              @click="addRole('ROLE_MUSICIAN')"
              :disabled="hasRole('ROLE_MUSICIAN') || isAdmin(selectedUser)"
            >
              Add Musician
            </el-button>
            <el-button
              size="small"
              type="danger"
              @click="removeRole('ROLE_MUSICIAN')"
              :disabled="!hasRole('ROLE_MUSICIAN') || isAdmin(selectedUser)"
            >
              Remove Musician
            </el-button>
            <el-button
              size="small"
              type="primary"
              @click="addRole('ROLE_ADMIN')"
              :disabled="hasRole('ROLE_ADMIN')"
            >
              Add Admin
            </el-button>
            <el-button
              size="small"
              type="danger"
              @click="removeRole('ROLE_ADMIN')"
              :disabled="!hasRole('ROLE_ADMIN')"
            >
              Remove Admin
            </el-button>
          </div>
        </div>
      </div>
      <template #footer>
        <el-button @click="detailsDialogVisible = false">Close</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { useAdminStore } from '@/store/admin'
import { Search, Refresh } from '@element-plus/icons-vue'
import { ElMessage, ElMessageBox } from 'element-plus'

const adminStore = useAdminStore()

const loading = ref(false)
const users = ref({ content: [], totalElements: 0 })
const detailsDialogVisible = ref(false)
const selectedUser = ref(null)

const searchForm = reactive({
  keyword: '',
  enabled: null
})

const pagination = reactive({
  page: 1,
  size: 20
})

/**
 * Format role name
 */
const formatRole = (role) => {
  return role.replace('ROLE_', '')
}

/**
 * Get role tag type
 */
const getRoleType = (role) => {
  if (role === 'ROLE_ADMIN') return 'danger'
  if (role === 'ROLE_MUSICIAN') return 'warning'
  return 'info'
}

/**
 * Check if user is admin
 */
const isAdmin = (user) => {
  return user.roles?.includes('ROLE_ADMIN')
}

/**
 * Check if selected user has a role
 */
const hasRole = (roleName) => {
  return selectedUser.value?.roles?.includes(roleName)
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
 * Load users
 */
const loadUsers = async () => {
  try {
    loading.value = true
    const params = {
      keyword: searchForm.keyword || undefined,
      enabled: searchForm.enabled,
      page: pagination.page - 1,
      size: pagination.size
    }
    users.value = await adminStore.fetchUsers(params)
  } catch (error) {
    console.error('Failed to load users:', error)
  } finally {
    loading.value = false
  }
}

/**
 * Handle search
 */
const handleSearch = () => {
  pagination.page = 1
  loadUsers()
}

/**
 * Handle reset
 */
const handleReset = () => {
  searchForm.keyword = ''
  searchForm.enabled = null
  pagination.page = 1
  loadUsers()
}

/**
 * View user details
 */
const viewUserDetails = async (user) => {
  try {
    loading.value = true
    selectedUser.value = await adminStore.fetchUserDetails(user.id)
    detailsDialogVisible.value = true
  } catch (error) {
    console.error('Failed to load user details:', error)
  } finally {
    loading.value = false
  }
}

/**
 * Toggle user status
 */
const toggleUserStatus = async (user) => {
  const action = user.enabled ? 'disable' : 'enable'
  try {
    await ElMessageBox.confirm(
      `Are you sure you want to ${action} user "${user.username}"?`,
      'Confirm',
      { type: 'warning' }
    )

    await adminStore.updateUserStatus(user.id, { enabled: !user.enabled })
    await loadUsers()
  } catch (error) {
    if (error !== 'cancel') {
      console.error('Failed to update user status:', error)
    }
  }
}

/**
 * Confirm delete user
 */
const confirmDeleteUser = async (user) => {
  try {
    await ElMessageBox.confirm(
      `Are you sure you want to permanently delete user "${user.username}"? This action cannot be undone.`,
      'Warning',
      {
        type: 'warning',
        confirmButtonText: 'Delete',
        confirmButtonClass: 'el-button--danger'
      }
    )

    await adminStore.deleteUser(user.id)
    await loadUsers()
  } catch (error) {
    if (error !== 'cancel') {
      console.error('Failed to delete user:', error)
    }
  }
}

/**
 * Add role to user
 */
const addRole = async (roleName) => {
  try {
    await adminStore.manageUserRole({
      userId: selectedUser.value.id,
      roleName,
      action: 'ADD'
    })
    selectedUser.value = await adminStore.fetchUserDetails(selectedUser.value.id)
    await loadUsers()
  } catch (error) {
    console.error('Failed to add role:', error)
  }
}

/**
 * Remove role from user
 */
const removeRole = async (roleName) => {
  try {
    await ElMessageBox.confirm(
      `Are you sure you want to remove ${formatRole(roleName)} role from this user?`,
      'Confirm',
      { type: 'warning' }
    )

    await adminStore.manageUserRole({
      userId: selectedUser.value.id,
      roleName,
      action: 'REMOVE'
    })
    selectedUser.value = await adminStore.fetchUserDetails(selectedUser.value.id)
    await loadUsers()
  } catch (error) {
    if (error !== 'cancel') {
      console.error('Failed to remove role:', error)
    }
  }
}

onMounted(() => {
  loadUsers()
})
</script>

<style scoped>
.user-manage {
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

.search-card {
  margin-bottom: 20px;
}

.search-form {
  margin: 0;
}

.table-card {
  margin-bottom: 20px;
}

.pagination-container {
  margin-top: 20px;
  display: flex;
  justify-content: center;
}

.user-details {
  padding: 10px 0;
}

.role-management {
  margin-top: 20px;
}

.role-management h4 {
  margin: 0 0 16px 0;
  font-size: 16px;
  font-weight: 600;
  color: #303133;
}

.roles-display {
  margin-bottom: 16px;
}

.role-actions {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
}

/* Responsive */
@media (max-width: 768px) {
  .user-manage {
    padding: 16px;
  }

  .page-header h1 {
    font-size: 24px;
  }

  .search-form {
    display: block;
  }

  .search-form .el-form-item {
    display: block;
    margin-bottom: 12px;
  }
}
</style>
