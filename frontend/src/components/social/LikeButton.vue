<template>
  <el-button
    :type="buttonType"
    :size="size"
    :loading="loading"
    :class="['like-button', { 'is-liked': isLiked }]"
    text
    @click="handleLike"
  >
    <el-icon :size="iconSize" :class="{ 'heart-animation': animating }">
      <component :is="isLiked ? HeartFilled : Heart" />
    </el-icon>
    <span v-if="showCount" class="like-count">{{ formattedCount }}</span>
  </el-button>
</template>

<script setup>
import { ref, computed, watch } from 'vue'
import { useSocialStore } from '@/store/social'
import { useUserStore } from '@/store/user'
import { Heart, HeartFilled } from '@element-plus/icons-vue'
import { ElMessage } from 'element-plus'
import { getLikeCount as getLikeCountApi } from '@/api/social'

const props = defineProps({
  targetType: {
    type: String,
    required: true,
    validator: (value) => ['MUSIC', 'PLAYLIST', 'COMMENT'].includes(value)
  },
  targetId: {
    type: [Number, String],
    required: true
  },
  initialLiked: {
    type: Boolean,
    default: false
  },
  initialCount: {
    type: Number,
    default: 0
  },
  size: {
    type: String,
    default: 'default',
    validator: (value) => ['small', 'default', 'large'].includes(value)
  },
  showCount: {
    type: Boolean,
    default: true
  }
})

const emit = defineEmits(['update:liked', 'update:count'])

const socialStore = useSocialStore()
const userStore = useUserStore()

// State
const isLiked = ref(props.initialLiked)
const likeCount = ref(props.initialCount)
const loading = ref(false)
const animating = ref(false)

// Computed
const buttonType = computed(() => {
  return isLiked.value ? 'danger' : 'default'
})

const iconSize = computed(() => {
  const sizeMap = {
    small: 16,
    default: 18,
    large: 20
  }
  return sizeMap[props.size] || 18
})

const formattedCount = computed(() => {
  if (likeCount.value >= 1000000) {
    return (likeCount.value / 1000000).toFixed(1) + 'M'
  } else if (likeCount.value >= 1000) {
    return (likeCount.value / 1000).toFixed(1) + 'K'
  }
  return likeCount.value.toString()
})

// Watch for prop changes
watch(() => props.initialLiked, (newVal) => {
  isLiked.value = newVal
})

watch(() => props.initialCount, (newVal) => {
  likeCount.value = newVal
})

// Check if liked status from store
watch(() => socialStore.isLikedLocal(props.targetType, props.targetId), (newVal) => {
  isLiked.value = newVal
}, { immediate: true })

// Methods
async function handleLike() {
  if (!userStore.isLoggedIn) {
    ElMessage.warning('Please login first')
    return
  }

  if (loading.value) return

  loading.value = true
  const previousLiked = isLiked.value
  const previousCount = likeCount.value

  try {
    // Optimistic update
    isLiked.value = !previousLiked
    likeCount.value = previousLiked ? previousCount - 1 : previousCount + 1

    // Trigger animation
    if (!previousLiked) {
      animating.value = true
      setTimeout(() => {
        animating.value = false
      }, 600)
    }

    // Call API through store
    let success = false
    if (previousLiked) {
      success = await socialStore.unlikeTarget(props.targetType, props.targetId)
    } else {
      success = await socialStore.likeTarget(props.targetType, props.targetId)
    }

    if (success) {
      // Emit events
      emit('update:liked', isLiked.value)
      emit('update:count', likeCount.value)

      // Fetch actual count from backend to ensure consistency
      await refreshLikeCount()
    } else {
      // Revert on failure
      isLiked.value = previousLiked
      likeCount.value = previousCount
    }
  } catch (error) {
    console.error('Like operation failed:', error)
    // Revert on error
    isLiked.value = previousLiked
    likeCount.value = previousCount
  } finally {
    loading.value = false
  }
}

async function refreshLikeCount() {
  try {
    const response = await getLikeCountApi(props.targetType, props.targetId)
    if (response.data !== undefined && response.data !== null) {
      likeCount.value = response.data
      emit('update:count', likeCount.value)
    }
  } catch (error) {
    console.error('Failed to refresh like count:', error)
  }
}

// Expose methods
defineExpose({
  refreshLikeCount
})
</script>

<style scoped lang="scss">
.like-button {
  display: inline-flex;
  align-items: center;
  gap: 4px;
  transition: all 0.3s ease;

  &.is-liked {
    color: #f56c6c;

    &:hover {
      color: #f78989;
    }
  }

  &:not(.is-liked) {
    color: #909399;

    &:hover {
      color: #606266;
    }
  }

  .el-icon {
    transition: transform 0.3s ease;

    &.heart-animation {
      animation: heartBeat 0.6s ease-in-out;
    }
  }

  .like-count {
    font-size: 14px;
    font-weight: 500;
    margin-left: 2px;
  }

  &.el-button--small {
    .like-count {
      font-size: 12px;
    }
  }

  &.el-button--large {
    .like-count {
      font-size: 16px;
    }
  }
}

@keyframes heartBeat {
  0% {
    transform: scale(1);
  }
  25% {
    transform: scale(1.3);
  }
  50% {
    transform: scale(1.1);
  }
  75% {
    transform: scale(1.25);
  }
  100% {
    transform: scale(1);
  }
}
</style>
