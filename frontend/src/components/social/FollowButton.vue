<template>
  <el-button
    :type="buttonType"
    :size="size"
    :loading="loading"
    :class="['follow-button', { 'is-following': isFollowing }]"
    @click="handleFollow"
  >
    <el-icon v-if="showIcon">
      <component :is="isFollowing ? UserFilled : User" />
    </el-icon>
    <span>{{ buttonText }}</span>
  </el-button>
</template>

<script setup>
import { ref, computed, watch } from 'vue'
import { useSocialStore } from '@/store/social'
import { useUserStore } from '@/store/user'
import { User, UserFilled } from '@element-plus/icons-vue'
import { ElMessage } from 'element-plus'

const props = defineProps({
  userId: {
    type: [Number, String],
    required: true
  },
  username: {
    type: String,
    default: ''
  },
  initialFollowing: {
    type: Boolean,
    default: false
  },
  size: {
    type: String,
    default: 'default',
    validator: (value) => ['small', 'default', 'large'].includes(value)
  },
  type: {
    type: String,
    default: 'primary',
    validator: (value) => ['primary', 'default', 'success', 'info', 'warning', 'danger'].includes(value)
  },
  showIcon: {
    type: Boolean,
    default: true
  }
})

const emit = defineEmits(['update:following'])

const socialStore = useSocialStore()
const userStore = useUserStore()

// State
const isFollowing = ref(props.initialFollowing)
const loading = ref(false)

// Computed
const buttonType = computed(() => {
  if (isFollowing.value) {
    return 'default'
  }
  return props.type
})

const buttonText = computed(() => {
  return isFollowing.value ? 'Following' : 'Follow'
})

// Watch for prop changes
watch(() => props.initialFollowing, (newVal) => {
  isFollowing.value = newVal
})

// Check if following status from store
watch(() => socialStore.isFollowingLocal(props.userId), (newVal) => {
  isFollowing.value = newVal
}, { immediate: true })

// Methods
async function handleFollow() {
  // Check if user is logged in
  if (!userStore.isLoggedIn) {
    ElMessage.warning('Please login first')
    return
  }

  // Check if trying to follow self
  if (userStore.currentUser?.id === parseInt(props.userId)) {
    ElMessage.warning('You cannot follow yourself')
    return
  }

  if (loading.value) return

  loading.value = true
  const previousFollowing = isFollowing.value

  try {
    // Optimistic update
    isFollowing.value = !previousFollowing

    // Call API through store
    let success = false
    if (previousFollowing) {
      success = await socialStore.unfollowUser(props.userId)
    } else {
      success = await socialStore.followUser(props.userId)
    }

    if (success) {
      // Emit event
      emit('update:following', isFollowing.value)
    } else {
      // Revert on failure
      isFollowing.value = previousFollowing
    }
  } catch (error) {
    console.error('Follow operation failed:', error)
    // Revert on error
    isFollowing.value = previousFollowing
  } finally {
    loading.value = false
  }
}

// Expose methods
defineExpose({
  handleFollow
})
</script>

<style scoped lang="scss">
.follow-button {
  display: inline-flex;
  align-items: center;
  gap: 6px;
  transition: all 0.3s ease;
  min-width: 90px;

  &.is-following {
    &:hover {
      border-color: #f56c6c;
      color: #f56c6c;
      background-color: #fef0f0;

      span {
        &::before {
          content: 'Unfollow';
        }
      }
    }

    span {
      &::before {
        content: 'Following';
      }
    }
  }

  &:not(.is-following) {
    span {
      &::before {
        content: 'Follow';
      }
    }
  }

  .el-icon {
    font-size: inherit;
  }

  span {
    font-weight: 500;

    // Hide actual text, show via ::before for easier hover state switching
    font-size: 0;

    &::before {
      font-size: 14px;
    }
  }

  &.el-button--small {
    min-width: 80px;

    span::before {
      font-size: 12px;
    }
  }

  &.el-button--large {
    min-width: 100px;

    span::before {
      font-size: 16px;
    }
  }
}

// Responsive
@media (max-width: 768px) {
  .follow-button {
    min-width: 80px;

    &.el-button--small {
      min-width: 70px;
    }
  }
}
</style>
