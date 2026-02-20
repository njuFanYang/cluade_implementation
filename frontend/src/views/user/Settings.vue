<!--
  * Settings Page Component
  *
  * @description Complete settings page for managing user profile and account settings.
  *
  * @features
  * - El-tabs with two tabs: "Profile Settings" and "Change Password"
  * - Profile Settings tab: Form to update nickname, bio, gender, phone, location
  * - Change Password tab: Form with oldPassword, newPassword, confirmPassword
  * - Both forms with comprehensive validation
  * - Save buttons with loading states
  * - Integration with useUserStore and user API
  * - Success messages after updates
  * - Responsive design
  * - Beautiful styling with SCSS
  *
  * @author MusicShare Team
  * @version 1.0.0
  -->

<template>
  <div class="settings-page">
    <div class="settings-container">
      <el-card class="settings-card" shadow="hover">
        <template #header>
          <div class="card-header">
            <el-icon :size="24" color="#E6A23C">
              <Setting />
            </el-icon>
            <span>Account Settings</span>
          </div>
        </template>

        <el-tabs v-model="activeTab" class="settings-tabs">
          <!-- Profile Settings Tab -->
          <el-tab-pane name="profile">
            <template #label>
              <span class="tab-label">
                <el-icon><User /></el-icon>
                Profile Settings
              </span>
            </template>

            <div class="tab-content">
              <el-form
                ref="profileFormRef"
                :model="profileForm"
                :rules="profileRules"
                label-width="120px"
                label-position="left"
                class="settings-form"
              >
                <el-form-item label="Username">
                  <el-input
                    :value="userStore.username"
                    disabled
                    placeholder="Username cannot be changed"
                  >
                    <template #prefix>
                      <el-icon><User /></el-icon>
                    </template>
                  </el-input>
                  <span class="form-tip">Username cannot be changed</span>
                </el-form-item>

                <el-form-item label="Email">
                  <el-input
                    :value="userStore.userInfo?.email"
                    disabled
                    placeholder="Email cannot be changed"
                  >
                    <template #prefix>
                      <el-icon><Message /></el-icon>
                    </template>
                  </el-input>
                  <span class="form-tip">Email cannot be changed</span>
                </el-form-item>

                <el-form-item label="Nickname" prop="nickname">
                  <el-input
                    v-model="profileForm.nickname"
                    placeholder="Enter your display name"
                    clearable
                  >
                    <template #prefix>
                      <el-icon><Avatar /></el-icon>
                    </template>
                  </el-input>
                </el-form-item>

                <el-form-item label="Bio" prop="bio">
                  <el-input
                    v-model="profileForm.bio"
                    type="textarea"
                    :rows="4"
                    placeholder="Tell us about yourself..."
                    maxlength="200"
                    show-word-limit
                  />
                </el-form-item>

                <el-form-item label="Gender" prop="gender">
                  <el-radio-group v-model="profileForm.gender">
                    <el-radio label="MALE">Male</el-radio>
                    <el-radio label="FEMALE">Female</el-radio>
                    <el-radio label="OTHER">Other</el-radio>
                    <el-radio label="">Prefer not to say</el-radio>
                  </el-radio-group>
                </el-form-item>

                <el-form-item label="Phone" prop="phone">
                  <el-input
                    v-model="profileForm.phone"
                    placeholder="Enter your phone number"
                    clearable
                  >
                    <template #prefix>
                      <el-icon><Phone /></el-icon>
                    </template>
                  </el-input>
                </el-form-item>

                <el-form-item label="Location" prop="location">
                  <el-input
                    v-model="profileForm.location"
                    placeholder="Enter your location"
                    clearable
                  >
                    <template #prefix>
                      <el-icon><Location /></el-icon>
                    </template>
                  </el-input>
                </el-form-item>

                <el-form-item>
                  <el-button
                    type="primary"
                    :loading="profileLoading"
                    @click="handleUpdateProfile"
                  >
                    {{ profileLoading ? 'Saving...' : 'Save Changes' }}
                  </el-button>
                  <el-button @click="resetProfileForm">
                    Reset
                  </el-button>
                </el-form-item>
              </el-form>
            </div>
          </el-tab-pane>

          <!-- Change Password Tab -->
          <el-tab-pane name="password">
            <template #label>
              <span class="tab-label">
                <el-icon><Lock /></el-icon>
                Change Password
              </span>
            </template>

            <div class="tab-content">
              <el-form
                ref="passwordFormRef"
                :model="passwordForm"
                :rules="passwordRules"
                label-width="150px"
                label-position="left"
                class="settings-form password-form"
              >
                <el-alert
                  title="Password Security"
                  type="info"
                  :closable="false"
                  show-icon
                  class="security-alert"
                >
                  <template #default>
                    <ul class="security-tips">
                      <li>Use at least 6 characters</li>
                      <li>Mix uppercase and lowercase letters</li>
                      <li>Include numbers and special characters</li>
                      <li>Don't use common words or patterns</li>
                    </ul>
                  </template>
                </el-alert>

                <el-form-item label="Current Password" prop="oldPassword">
                  <el-input
                    v-model="passwordForm.oldPassword"
                    type="password"
                    placeholder="Enter your current password"
                    show-password
                    clearable
                  >
                    <template #prefix>
                      <el-icon><Lock /></el-icon>
                    </template>
                  </el-input>
                </el-form-item>

                <el-form-item label="New Password" prop="newPassword">
                  <el-input
                    v-model="passwordForm.newPassword"
                    type="password"
                    placeholder="Enter your new password"
                    show-password
                    clearable
                  >
                    <template #prefix>
                      <el-icon><Lock /></el-icon>
                    </template>
                  </el-input>
                  <div v-if="passwordForm.newPassword" class="password-strength">
                    <span class="strength-label">Password strength:</span>
                    <div class="strength-bar">
                      <div
                        class="strength-fill"
                        :class="passwordStrength.class"
                        :style="{ width: passwordStrength.percentage + '%' }"
                      ></div>
                    </div>
                    <span class="strength-text" :class="passwordStrength.class">
                      {{ passwordStrength.text }}
                    </span>
                  </div>
                </el-form-item>

                <el-form-item label="Confirm Password" prop="confirmPassword">
                  <el-input
                    v-model="passwordForm.confirmPassword"
                    type="password"
                    placeholder="Confirm your new password"
                    show-password
                    clearable
                  >
                    <template #prefix>
                      <el-icon><Lock /></el-icon>
                    </template>
                  </el-input>
                </el-form-item>

                <el-form-item>
                  <el-button
                    type="primary"
                    :loading="passwordLoading"
                    @click="handleChangePassword"
                  >
                    {{ passwordLoading ? 'Updating...' : 'Change Password' }}
                  </el-button>
                  <el-button @click="resetPasswordForm">
                    Reset
                  </el-button>
                </el-form-item>
              </el-form>
            </div>
          </el-tab-pane>
        </el-tabs>
      </el-card>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted } from 'vue'
import { useUserStore } from '@/store/user'
import { updateProfile, changePassword } from '@/api/user'
import {
  Setting,
  User,
  Lock,
  Message,
  Avatar,
  Phone,
  Location
} from '@element-plus/icons-vue'
import { ElMessage } from 'element-plus'

/**
 * User store instance
 * @type {ReturnType<typeof useUserStore>}
 */
const userStore = useUserStore()

/**
 * Active tab name
 * @type {import('vue').Ref<string>}
 */
const activeTab = ref('profile')

/**
 * Profile form reference for validation
 * @type {import('vue').Ref<any>}
 */
const profileFormRef = ref(null)

/**
 * Password form reference for validation
 * @type {import('vue').Ref<any>}
 */
const passwordFormRef = ref(null)

/**
 * Profile loading state
 * @type {import('vue').Ref<boolean>}
 */
const profileLoading = ref(false)

/**
 * Password loading state
 * @type {import('vue').Ref<boolean>}
 */
const passwordLoading = ref(false)

/**
 * Profile form data
 * @type {Object}
 * @property {string} nickname - Display nickname
 * @property {string} bio - User biography
 * @property {string} gender - User gender
 * @property {string} phone - Phone number
 * @property {string} location - User location
 */
const profileForm = reactive({
  nickname: '',
  bio: '',
  gender: '',
  phone: '',
  location: ''
})

/**
 * Password form data
 * @type {Object}
 * @property {string} oldPassword - Current password
 * @property {string} newPassword - New password
 * @property {string} confirmPassword - Password confirmation
 */
const passwordForm = reactive({
  oldPassword: '',
  newPassword: '',
  confirmPassword: ''
})

/**
 * Validate phone number format
 *
 * @param {any} rule - Validation rule
 * @param {string} value - Phone number value
 * @param {Function} callback - Validation callback
 * @returns {void}
 */
const validatePhone = (rule, value, callback) => {
  if (!value) {
    callback()
    return
  }
  if (!/^[\d\s\-+()]+$/.test(value)) {
    callback(new Error('Please enter a valid phone number'))
  } else {
    callback()
  }
}

/**
 * Validate new password
 *
 * @param {any} rule - Validation rule
 * @param {string} value - New password value
 * @param {Function} callback - Validation callback
 * @returns {void}
 */
const validateNewPassword = (rule, value, callback) => {
  if (!value) {
    callback(new Error('Please enter a new password'))
  } else if (value.length < 6) {
    callback(new Error('Password must be at least 6 characters'))
  } else if (value === passwordForm.oldPassword) {
    callback(new Error('New password must be different from current password'))
  } else {
    callback()
  }
}

/**
 * Validate password confirmation
 *
 * @param {any} rule - Validation rule
 * @param {string} value - Confirm password value
 * @param {Function} callback - Validation callback
 * @returns {void}
 */
const validateConfirmPassword = (rule, value, callback) => {
  if (!value) {
    callback(new Error('Please confirm your new password'))
  } else if (value !== passwordForm.newPassword) {
    callback(new Error('Passwords do not match'))
  } else {
    callback()
  }
}

/**
 * Profile form validation rules
 * @type {Object}
 */
const profileRules = {
  nickname: [
    {
      max: 50,
      message: 'Nickname cannot exceed 50 characters',
      trigger: 'blur'
    }
  ],
  bio: [
    {
      max: 200,
      message: 'Bio cannot exceed 200 characters',
      trigger: 'blur'
    }
  ],
  phone: [
    { validator: validatePhone, trigger: 'blur' }
  ],
  location: [
    {
      max: 100,
      message: 'Location cannot exceed 100 characters',
      trigger: 'blur'
    }
  ]
}

/**
 * Password form validation rules
 * @type {Object}
 */
const passwordRules = {
  oldPassword: [
    {
      required: true,
      message: 'Please enter your current password',
      trigger: 'blur'
    }
  ],
  newPassword: [
    { required: true, validator: validateNewPassword, trigger: 'blur' }
  ],
  confirmPassword: [
    { required: true, validator: validateConfirmPassword, trigger: 'blur' }
  ]
}

/**
 * Calculate password strength
 *
 * @description Evaluates password strength based on length, character types,
 *              and complexity. Returns strength level, percentage, CSS class,
 *              and descriptive text.
 *
 * @returns {Object} Password strength information
 * @property {number} level - Strength level (0-4)
 * @property {number} percentage - Visual percentage (0-100)
 * @property {string} class - CSS class for styling
 * @property {string} text - Descriptive text
 */
const passwordStrength = computed(() => {
  const password = passwordForm.newPassword
  if (!password) {
    return { level: 0, percentage: 0, class: '', text: '' }
  }

  let strength = 0

  // Length check
  if (password.length >= 8) strength++
  if (password.length >= 12) strength++

  // Character type checks
  if (/[a-z]/.test(password)) strength++
  if (/[A-Z]/.test(password)) strength++
  if (/[0-9]/.test(password)) strength++
  if (/[^a-zA-Z0-9]/.test(password)) strength++

  // Normalize to 0-4 scale
  const level = Math.min(Math.floor(strength / 1.5), 4)

  const strengthMap = {
    0: { percentage: 25, class: 'weak', text: 'Weak' },
    1: { percentage: 25, class: 'weak', text: 'Weak' },
    2: { percentage: 50, class: 'medium', text: 'Medium' },
    3: { percentage: 75, class: 'good', text: 'Good' },
    4: { percentage: 100, class: 'strong', text: 'Strong' }
  }

  return { level, ...strengthMap[level] }
})

/**
 * Initialize profile form with current user data
 *
 * @returns {void}
 */
const initProfileForm = () => {
  const user = userStore.userInfo
  if (user) {
    profileForm.nickname = user.nickname || ''
    profileForm.bio = user.bio || ''
    profileForm.gender = user.gender || ''
    profileForm.phone = user.phone || ''
    profileForm.location = user.location || ''
  }
}

/**
 * Reset profile form to original values
 *
 * @returns {void}
 */
const resetProfileForm = () => {
  initProfileForm()
  if (profileFormRef.value) {
    profileFormRef.value.clearValidate()
  }
}

/**
 * Reset password form
 *
 * @returns {void}
 */
const resetPasswordForm = () => {
  passwordForm.oldPassword = ''
  passwordForm.newPassword = ''
  passwordForm.confirmPassword = ''
  if (passwordFormRef.value) {
    passwordFormRef.value.clearValidate()
  }
}

/**
 * Handle profile update submission
 *
 * @description Validates the form and updates user profile
 * @returns {Promise<void>}
 */
const handleUpdateProfile = async () => {
  if (!profileFormRef.value) return

  try {
    // Validate form
    await profileFormRef.value.validate()

    profileLoading.value = true

    // Prepare update data (only send fields that have values)
    const updateData = {}
    if (profileForm.nickname) updateData.nickname = profileForm.nickname
    if (profileForm.bio) updateData.bio = profileForm.bio
    if (profileForm.gender) updateData.gender = profileForm.gender
    if (profileForm.phone) updateData.phone = profileForm.phone
    if (profileForm.location) updateData.location = profileForm.location

    // Call API
    await updateProfile(updateData)

    // Update local store
    userStore.updateUserInfo(updateData)

    ElMessage.success('Profile updated successfully')
  } catch (error) {
    console.error('Update profile error:', error)
    if (error.errors) {
      // Validation errors
      return
    }
    // Error message is shown by API interceptor
  } finally {
    profileLoading.value = false
  }
}

/**
 * Handle password change submission
 *
 * @description Validates the form and changes user password
 * @returns {Promise<void>}
 */
const handleChangePassword = async () => {
  if (!passwordFormRef.value) return

  try {
    // Validate form
    await passwordFormRef.value.validate()

    passwordLoading.value = true

    // Call API
    await changePassword({
      oldPassword: passwordForm.oldPassword,
      newPassword: passwordForm.newPassword
    })

    ElMessage.success('Password changed successfully')

    // Reset form
    resetPasswordForm()
  } catch (error) {
    console.error('Change password error:', error)
    if (error.errors) {
      // Validation errors
      return
    }
    // Error message is shown by API interceptor
  } finally {
    passwordLoading.value = false
  }
}

/**
 * Initialize component
 *
 * @description Load user data into form on mount
 */
onMounted(() => {
  initProfileForm()
})
</script>

<style scoped lang="scss">
.settings-page {
  min-height: calc(100vh - 60px);
  background: #f5f7fa;
  padding: 20px;
}

.settings-container {
  max-width: 900px;
  margin: 0 auto;
}

.settings-card {
  border-radius: 12px;
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

.card-header {
  display: flex;
  align-items: center;
  gap: 12px;
  font-size: 20px;
  font-weight: 600;
  color: #303133;
}

// Tabs Styling
.settings-tabs {
  :deep(.el-tabs__header) {
    margin-bottom: 0;
    background: #fafafa;
    padding: 0 20px;
    border-radius: 8px 8px 0 0;
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
    gap: 8px;

    .el-icon {
      font-size: 16px;
    }
  }
}

.tab-content {
  padding: 30px;
}

// Form Styling
.settings-form {
  max-width: 600px;

  .el-form-item {
    margin-bottom: 24px;
  }

  .form-tip {
    font-size: 12px;
    color: #909399;
    margin-top: 4px;
    display: block;
  }

  :deep(.el-input__wrapper) {
    border-radius: 6px;
    box-shadow: 0 1px 4px rgba(0, 0, 0, 0.08);
    transition: all 0.3s;

    &:hover:not(.is-disabled) {
      box-shadow: 0 1px 6px rgba(0, 0, 0, 0.12);
    }
  }

  :deep(.el-textarea__inner) {
    border-radius: 6px;
    box-shadow: 0 1px 4px rgba(0, 0, 0, 0.08);
    transition: all 0.3s;

    &:hover {
      box-shadow: 0 1px 6px rgba(0, 0, 0, 0.12);
    }
  }

  :deep(.el-radio-group) {
    display: flex;
    flex-wrap: wrap;
    gap: 16px;
  }

  :deep(.el-button) {
    min-width: 120px;
  }
}

// Password Form Specific Styling
.password-form {
  .security-alert {
    margin-bottom: 24px;
    border-radius: 6px;

    :deep(.el-alert__content) {
      padding: 0;
    }
  }

  .security-tips {
    margin: 8px 0 0 0;
    padding-left: 20px;
    font-size: 13px;
    line-height: 1.8;

    li {
      color: #606266;
    }
  }

  .password-strength {
    margin-top: 8px;
    display: flex;
    align-items: center;
    gap: 8px;

    .strength-label {
      font-size: 12px;
      color: #606266;
      white-space: nowrap;
    }

    .strength-bar {
      flex: 1;
      height: 4px;
      background: #e4e7ed;
      border-radius: 2px;
      overflow: hidden;

      .strength-fill {
        height: 100%;
        transition: all 0.3s;
        border-radius: 2px;

        &.weak {
          background: #f56c6c;
        }

        &.medium {
          background: #e6a23c;
        }

        &.good {
          background: #409eff;
        }

        &.strong {
          background: #67c23a;
        }
      }
    }

    .strength-text {
      font-size: 12px;
      font-weight: 500;
      white-space: nowrap;

      &.weak {
        color: #f56c6c;
      }

      &.medium {
        color: #e6a23c;
      }

      &.good {
        color: #409eff;
      }

      &.strong {
        color: #67c23a;
      }
    }
  }
}

// Responsive Design
@media (max-width: 768px) {
  .settings-page {
    padding: 12px;
  }

  .tab-content {
    padding: 20px 15px;
  }

  .settings-form {
    max-width: 100%;

    :deep(.el-form-item__label) {
      text-align: left;
      padding-right: 0;
      margin-bottom: 8px;
    }

    :deep(.el-form-item__content) {
      margin-left: 0 !important;
    }
  }

  .settings-tabs {
    :deep(.el-tabs__item) {
      font-size: 14px;
      padding: 0 12px;
    }

    .tab-label {
      gap: 6px;

      .el-icon {
        font-size: 14px;
      }
    }
  }
}

@media (max-width: 576px) {
  .card-header {
    font-size: 18px;

    .el-icon {
      font-size: 20px;
    }
  }

  .settings-tabs {
    .tab-label {
      span {
        display: none;
      }

      .el-icon {
        margin: 0;
        font-size: 18px;
      }
    }
  }

  .password-form {
    .security-alert {
      :deep(.el-alert__content) {
        font-size: 12px;
      }
    }

    .security-tips {
      font-size: 12px;
      padding-left: 16px;
    }
  }
}
</style>
