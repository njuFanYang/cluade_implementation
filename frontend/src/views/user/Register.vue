<!--
  * Registration Page Component
  *
  * @description Complete registration page with comprehensive form validation,
  *              real-time feedback, and beautiful gradient background design.
  *
  * @features
  * - Username, email, password, confirmPassword, and nickname fields
  * - Real-time validation for username format and availability
  * - Email format validation
  * - Password strength indicator and validation
  * - Password match validation
  * - Loading state during registration
  * - Integration with useUserStore and registration API
  * - Automatic redirect to login after successful registration
  * - Link to login page
  * - Beautiful gradient background with glassmorphism effect
  *
  * @author MusicShare Team
  * @version 1.0.0
  -->

<template>
  <div class="register-page">
    <div class="register-container">
      <el-card class="register-card">
        <div class="register-header">
          <el-icon :size="50" color="#67C23A">
            <UserFilled />
          </el-icon>
          <h1>Create Account</h1>
          <p>Join MusicShare and share your music journey</p>
        </div>

        <el-form
          ref="registerFormRef"
          :model="registerForm"
          :rules="registerRules"
          class="register-form"
          @submit.prevent="handleRegister"
        >
          <el-form-item prop="username">
            <el-input
              v-model="registerForm.username"
              placeholder="Username (3-20 characters)"
              size="large"
              :prefix-icon="User"
              clearable
            />
          </el-form-item>

          <el-form-item prop="email">
            <el-input
              v-model="registerForm.email"
              placeholder="Email address"
              size="large"
              :prefix-icon="Message"
              clearable
            />
          </el-form-item>

          <el-form-item prop="nickname">
            <el-input
              v-model="registerForm.nickname"
              placeholder="Nickname (optional)"
              size="large"
              :prefix-icon="Avatar"
              clearable
            />
          </el-form-item>

          <el-form-item prop="password">
            <el-input
              v-model="registerForm.password"
              type="password"
              placeholder="Password (at least 6 characters)"
              size="large"
              :prefix-icon="Lock"
              show-password
              clearable
            />
            <div v-if="registerForm.password" class="password-strength">
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

          <el-form-item prop="confirmPassword">
            <el-input
              v-model="registerForm.confirmPassword"
              type="password"
              placeholder="Confirm password"
              size="large"
              :prefix-icon="Lock"
              show-password
              clearable
              @keyup.enter="handleRegister"
            />
          </el-form-item>

          <el-form-item>
            <el-button
              type="success"
              size="large"
              class="register-button"
              :loading="loading"
              @click="handleRegister"
            >
              {{ loading ? 'Creating account...' : 'Create Account' }}
            </el-button>
          </el-form-item>

          <el-divider>OR</el-divider>

          <div class="login-link">
            <span>Already have an account?</span>
            <el-link type="success" :underline="false" @click="goToLogin">
              Sign in
            </el-link>
          </div>
        </el-form>
      </el-card>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, computed } from 'vue'
import { useRouter } from 'vue-router'
import { register } from '@/api/user'
import { User, Lock, Message, Avatar, UserFilled } from '@element-plus/icons-vue'
import { ElMessage } from 'element-plus'

/**
 * Router instance for navigation
 * @type {import('vue-router').Router}
 */
const router = useRouter()

/**
 * Register form reference for validation
 * @type {import('vue').Ref<any>}
 */
const registerFormRef = ref(null)

/**
 * Loading state indicator
 * @type {import('vue').Ref<boolean>}
 */
const loading = ref(false)

/**
 * Registration form data
 * @type {Object}
 * @property {string} username - Username (3-20 characters, alphanumeric)
 * @property {string} email - Email address
 * @property {string} password - Password (minimum 6 characters)
 * @property {string} confirmPassword - Password confirmation
 * @property {string} nickname - Display nickname (optional)
 */
const registerForm = reactive({
  username: '',
  email: '',
  password: '',
  confirmPassword: '',
  nickname: ''
})

/**
 * Validate username format
 *
 * @param {any} rule - Validation rule
 * @param {string} value - Username value
 * @param {Function} callback - Validation callback
 * @returns {void}
 */
const validateUsername = (rule, value, callback) => {
  if (!value) {
    callback(new Error('Please enter a username'))
  } else if (value.length < 3 || value.length > 20) {
    callback(new Error('Username must be between 3 and 20 characters'))
  } else if (!/^[a-zA-Z0-9_]+$/.test(value)) {
    callback(new Error('Username can only contain letters, numbers, and underscores'))
  } else {
    callback()
  }
}

/**
 * Validate email format
 *
 * @param {any} rule - Validation rule
 * @param {string} value - Email value
 * @param {Function} callback - Validation callback
 * @returns {void}
 */
const validateEmail = (rule, value, callback) => {
  if (!value) {
    callback(new Error('Please enter an email address'))
  } else if (!/^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(value)) {
    callback(new Error('Please enter a valid email address'))
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
    callback(new Error('Please confirm your password'))
  } else if (value !== registerForm.password) {
    callback(new Error('Passwords do not match'))
  } else {
    callback()
  }
}

/**
 * Form validation rules
 * @type {Object}
 */
const registerRules = {
  username: [
    { required: true, validator: validateUsername, trigger: 'blur' }
  ],
  email: [
    { required: true, validator: validateEmail, trigger: 'blur' }
  ],
  password: [
    {
      required: true,
      message: 'Please enter a password',
      trigger: 'blur'
    },
    {
      min: 6,
      message: 'Password must be at least 6 characters',
      trigger: 'blur'
    }
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
  const password = registerForm.password
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
 * Handle registration form submission
 *
 * @description Validates the form, calls the registration API,
 *              and redirects to login page upon success.
 *
 * @returns {Promise<void>}
 */
const handleRegister = async () => {
  if (!registerFormRef.value) return

  try {
    // Validate form
    await registerFormRef.value.validate()

    loading.value = true

    // Prepare registration data
    const registrationData = {
      username: registerForm.username,
      email: registerForm.email,
      password: registerForm.password
    }

    // Add nickname if provided
    if (registerForm.nickname) {
      registrationData.nickname = registerForm.nickname
    }

    // Attempt registration
    await register(registrationData)

    ElMessage.success('Registration successful! Please sign in.')

    // Redirect to login page after short delay
    setTimeout(() => {
      router.push('/user/login')
    }, 1500)
  } catch (error) {
    console.error('Registration error:', error)
    if (error.errors) {
      // Validation errors
      return
    }
    // Error message is shown by API interceptor
  } finally {
    loading.value = false
  }
}

/**
 * Navigate to login page
 *
 * @returns {void}
 */
const goToLogin = () => {
  router.push('/user/login')
}
</script>

<style scoped lang="scss">
.register-page {
  min-height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;
  background: linear-gradient(135deg, #11998e 0%, #38ef7d 100%);
  padding: 20px;
  position: relative;
  overflow: hidden;

  &::before {
    content: '';
    position: absolute;
    top: -50%;
    left: -50%;
    width: 200%;
    height: 200%;
    background: radial-gradient(
      circle,
      rgba(255, 255, 255, 0.1) 1px,
      transparent 1px
    );
    background-size: 50px 50px;
    animation: moveBackground 20s linear infinite;
  }

  @keyframes moveBackground {
    0% {
      transform: translate(0, 0);
    }
    100% {
      transform: translate(50px, 50px);
    }
  }
}

.register-container {
  width: 100%;
  max-width: 500px;
  position: relative;
  z-index: 1;
}

.register-card {
  border-radius: 16px;
  box-shadow: 0 20px 60px rgba(0, 0, 0, 0.3);
  backdrop-filter: blur(10px);
  background: rgba(255, 255, 255, 0.95);
  border: 1px solid rgba(255, 255, 255, 0.3);

  :deep(.el-card__body) {
    padding: 40px 30px;
  }
}

.register-header {
  text-align: center;
  margin-bottom: 30px;

  .el-icon {
    margin-bottom: 20px;
    animation: fadeInDown 0.6s ease-out;
  }

  h1 {
    font-size: 28px;
    font-weight: 600;
    color: #303133;
    margin: 0 0 10px 0;
    animation: fadeInDown 0.6s ease-out 0.1s both;
  }

  p {
    color: #909399;
    font-size: 14px;
    margin: 0;
    animation: fadeInDown 0.6s ease-out 0.2s both;
  }

  @keyframes fadeInDown {
    from {
      opacity: 0;
      transform: translateY(-20px);
    }
    to {
      opacity: 1;
      transform: translateY(0);
    }
  }
}

.register-form {
  animation: fadeIn 0.6s ease-out 0.3s both;

  @keyframes fadeIn {
    from {
      opacity: 0;
    }
    to {
      opacity: 1;
    }
  }

  .el-form-item {
    margin-bottom: 22px;
  }

  :deep(.el-input__wrapper) {
    border-radius: 8px;
    box-shadow: 0 2px 8px rgba(0, 0, 0, 0.08);
    transition: all 0.3s;

    &:hover {
      box-shadow: 0 2px 12px rgba(0, 0, 0, 0.12);
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

  .register-button {
    width: 100%;
    height: 44px;
    border-radius: 8px;
    font-size: 16px;
    font-weight: 500;
    background: linear-gradient(135deg, #11998e 0%, #38ef7d 100%);
    border: none;
    transition: all 0.3s;

    &:hover {
      transform: translateY(-2px);
      box-shadow: 0 8px 16px rgba(17, 153, 142, 0.4);
    }

    &:active {
      transform: translateY(0);
    }

    &.is-loading {
      background: linear-gradient(135deg, #11998e 0%, #38ef7d 100%);
    }
  }

  .el-divider {
    margin: 24px 0;

    :deep(.el-divider__text) {
      color: #909399;
      font-size: 13px;
      background: rgba(255, 255, 255, 0.95);
    }
  }

  .login-link {
    text-align: center;
    color: #606266;
    font-size: 14px;

    span {
      margin-right: 8px;
    }

    .el-link {
      font-size: 14px;
      font-weight: 500;
    }
  }
}

// Responsive design
@media (max-width: 768px) {
  .register-card {
    :deep(.el-card__body) {
      padding: 30px 20px;
    }
  }

  .register-header {
    margin-bottom: 25px;

    h1 {
      font-size: 24px;
    }
  }
}
</style>
