<!--
  * Login Page Component
  *
  * @description Complete login page with form validation, loading states,
  *              and beautiful gradient background design.
  *
  * @features
  * - Username/email and password input fields
  * - Remember me checkbox functionality
  * - Form validation with Element Plus
  * - Loading state during authentication
  * - Integration with useUserStore for login
  * - Automatic redirect after successful login
  * - Link to registration page
  * - Beautiful gradient background with glassmorphism effect
  * - User and Lock icons from Element Plus
  *
  * @author MusicShare Team
  * @version 1.0.0
  -->

<template>
  <div class="login-page">
    <div class="login-container">
      <el-card class="login-card">
        <div class="login-header">
          <el-icon :size="50" color="#409EFF">
            <User />
          </el-icon>
          <h1>Welcome Back</h1>
          <p>Sign in to continue to MusicShare</p>
        </div>

        <el-form
          ref="loginFormRef"
          :model="loginForm"
          :rules="loginRules"
          class="login-form"
          @submit.prevent="handleLogin"
        >
          <el-form-item prop="username">
            <el-input
              v-model="loginForm.username"
              placeholder="Username or Email"
              size="large"
              :prefix-icon="User"
              clearable
              @keyup.enter="handleLogin"
            />
          </el-form-item>

          <el-form-item prop="password">
            <el-input
              v-model="loginForm.password"
              type="password"
              placeholder="Password"
              size="large"
              :prefix-icon="Lock"
              show-password
              clearable
              @keyup.enter="handleLogin"
            />
          </el-form-item>

          <el-form-item>
            <div class="form-options">
              <el-checkbox v-model="loginForm.rememberMe">
                Remember me
              </el-checkbox>
              <el-link type="primary" :underline="false">
                Forgot password?
              </el-link>
            </div>
          </el-form-item>

          <el-form-item>
            <el-button
              type="primary"
              size="large"
              class="login-button"
              :loading="loading"
              @click="handleLogin"
            >
              {{ loading ? 'Signing in...' : 'Sign In' }}
            </el-button>
          </el-form-item>

          <el-divider>OR</el-divider>

          <div class="register-link">
            <span>Don't have an account?</span>
            <el-link type="primary" :underline="false" @click="goToRegister">
              Sign up now
            </el-link>
          </div>
        </el-form>
      </el-card>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { useUserStore } from '@/store/user'
import { User, Lock } from '@element-plus/icons-vue'
import { ElMessage } from 'element-plus'

/**
 * Router instance for navigation
 * @type {import('vue-router').Router}
 */
const router = useRouter()

/**
 * Route instance for query parameters
 * @type {import('vue-router').RouteLocationNormalizedLoaded}
 */
const route = useRoute()

/**
 * User store instance
 * @type {ReturnType<typeof useUserStore>}
 */
const userStore = useUserStore()

/**
 * Login form reference for validation
 * @type {import('vue').Ref<any>}
 */
const loginFormRef = ref(null)

/**
 * Loading state indicator
 * @type {import('vue').Ref<boolean>}
 */
const loading = ref(false)

/**
 * Login form data
 * @type {Object}
 * @property {string} username - Username or email
 * @property {string} password - User password
 * @property {boolean} rememberMe - Remember me checkbox state
 */
const loginForm = reactive({
  username: '',
  password: '',
  rememberMe: false
})

/**
 * Form validation rules
 * @type {Object}
 */
const loginRules = {
  username: [
    {
      required: true,
      message: 'Please enter your username or email',
      trigger: 'blur'
    },
    {
      min: 3,
      message: 'Username must be at least 3 characters',
      trigger: 'blur'
    }
  ],
  password: [
    {
      required: true,
      message: 'Please enter your password',
      trigger: 'blur'
    },
    {
      min: 6,
      message: 'Password must be at least 6 characters',
      trigger: 'blur'
    }
  ]
}

/**
 * Handle login form submission
 *
 * @description Validates the form, calls the login API through the store,
 *              and redirects the user upon successful authentication.
 *
 * @returns {Promise<void>}
 */
const handleLogin = async () => {
  if (!loginFormRef.value) return

  try {
    // Validate form
    await loginFormRef.value.validate()

    loading.value = true

    // Attempt login
    const success = await userStore.login({
      username: loginForm.username,
      password: loginForm.password
    })

    if (success) {
      // Handle remember me functionality
      if (loginForm.rememberMe) {
        localStorage.setItem('rememberMe', 'true')
        localStorage.setItem('savedUsername', loginForm.username)
      } else {
        localStorage.removeItem('rememberMe')
        localStorage.removeItem('savedUsername')
      }

      // Redirect to intended page or home
      const redirect = route.query.redirect || '/'
      router.push(redirect)
    }
  } catch (error) {
    console.error('Login error:', error)
    if (error.errors) {
      // Validation errors
      return
    }
    ElMessage.error('Login failed. Please check your credentials.')
  } finally {
    loading.value = false
  }
}

/**
 * Navigate to registration page
 *
 * @returns {void}
 */
const goToRegister = () => {
  router.push('/user/register')
}

/**
 * Initialize component
 *
 * @description Load saved username if "remember me" was checked
 */
;(() => {
  const rememberMe = localStorage.getItem('rememberMe')
  const savedUsername = localStorage.getItem('savedUsername')

  if (rememberMe === 'true' && savedUsername) {
    loginForm.username = savedUsername
    loginForm.rememberMe = true
  }
})()
</script>

<style scoped lang="scss">
.login-page {
  min-height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
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

.login-container {
  width: 100%;
  max-width: 450px;
  position: relative;
  z-index: 1;
}

.login-card {
  border-radius: 16px;
  box-shadow: 0 20px 60px rgba(0, 0, 0, 0.3);
  backdrop-filter: blur(10px);
  background: rgba(255, 255, 255, 0.95);
  border: 1px solid rgba(255, 255, 255, 0.3);

  :deep(.el-card__body) {
    padding: 40px 30px;
  }
}

.login-header {
  text-align: center;
  margin-bottom: 40px;

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

.login-form {
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
    margin-bottom: 24px;
  }

  :deep(.el-input__wrapper) {
    border-radius: 8px;
    box-shadow: 0 2px 8px rgba(0, 0, 0, 0.08);
    transition: all 0.3s;

    &:hover {
      box-shadow: 0 2px 12px rgba(0, 0, 0, 0.12);
    }
  }

  .form-options {
    display: flex;
    justify-content: space-between;
    align-items: center;
    width: 100%;

    .el-checkbox {
      :deep(.el-checkbox__label) {
        color: #606266;
        font-size: 14px;
      }
    }

    .el-link {
      font-size: 14px;
    }
  }

  .login-button {
    width: 100%;
    height: 44px;
    border-radius: 8px;
    font-size: 16px;
    font-weight: 500;
    background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
    border: none;
    transition: all 0.3s;

    &:hover {
      transform: translateY(-2px);
      box-shadow: 0 8px 16px rgba(102, 126, 234, 0.4);
    }

    &:active {
      transform: translateY(0);
    }

    &.is-loading {
      background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
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

  .register-link {
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
  .login-card {
    :deep(.el-card__body) {
      padding: 30px 20px;
    }
  }

  .login-header {
    margin-bottom: 30px;

    h1 {
      font-size: 24px;
    }
  }
}
</style>
