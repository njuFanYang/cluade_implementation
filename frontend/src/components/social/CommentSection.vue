<template>
  <div class="comment-section">
    <!-- Section Header -->
    <div class="section-header">
      <h3 class="section-title">
        <el-icon><ChatDotRound /></el-icon>
        Comments
        <span v-if="totalComments > 0" class="comment-count">({{ totalComments }})</span>
      </h3>
    </div>

    <!-- Comment Input Form -->
    <div v-if="userStore.isLoggedIn" class="comment-input-form">
      <div class="input-wrapper">
        <el-avatar
          :size="40"
          :src="userStore.currentUser?.avatar"
          :alt="userStore.currentUser?.username"
        >
          {{ userStore.currentUser?.username?.charAt(0).toUpperCase() }}
        </el-avatar>

        <el-input
          v-model="newComment"
          type="textarea"
          :rows="3"
          placeholder="Write a comment..."
          maxlength="500"
          show-word-limit
          :disabled="submitting"
        />
      </div>

      <div class="input-actions">
        <el-button
          type="primary"
          :loading="submitting"
          :disabled="!newComment.trim()"
          @click="submitComment"
        >
          <el-icon><CircleCheck /></el-icon>
          Post Comment
        </el-button>
      </div>
    </div>

    <!-- Login Prompt -->
    <div v-else class="login-prompt">
      <el-alert
        title="Please login to comment"
        type="info"
        :closable="false"
        show-icon
      >
        <template #default>
          <p>You need to be logged in to post comments.</p>
          <el-button type="primary" size="small" @click="goToLogin">
            Login
          </el-button>
        </template>
      </el-alert>
    </div>

    <!-- Sort Options -->
    <div v-if="commentsList.length > 0" class="sort-options">
      <el-radio-group v-model="sortBy" size="small" @change="handleSortChange">
        <el-radio-button label="createdAt,desc">
          <el-icon><Clock /></el-icon>
          Newest First
        </el-radio-button>
        <el-radio-button label="createdAt,asc">
          <el-icon><Clock /></el-icon>
          Oldest First
        </el-radio-button>
        <el-radio-button label="likeCount,desc">
          <el-icon><Star /></el-icon>
          Most Liked
        </el-radio-button>
      </el-radio-group>
    </div>

    <!-- Comments List -->
    <div v-loading="loading" class="comments-list">
      <!-- Empty State -->
      <el-empty
        v-if="!loading && commentsList.length === 0"
        description="No comments yet. Be the first to comment!"
        :image-size="120"
      />

      <!-- Comment Items -->
      <CommentItem
        v-for="comment in commentsList"
        :key="comment.id"
        :comment="comment"
        @deleted="handleCommentDeleted"
        @replied="handleCommentReplied"
      />

      <!-- Load More Button -->
      <div v-if="hasMore" class="load-more">
        <el-button
          :loading="loadingMore"
          @click="loadMore"
        >
          Load More Comments
        </el-button>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted, watch } from 'vue'
import { useRouter } from 'vue-router'
import { useSocialStore } from '@/store/social'
import { useUserStore } from '@/store/user'
import { ElMessage } from 'element-plus'
import {
  ChatDotRound,
  CircleCheck,
  Clock,
  Star
} from '@element-plus/icons-vue'
import CommentItem from './CommentItem.vue'

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
  autoLoad: {
    type: Boolean,
    default: true
  }
})

const emit = defineEmits(['commentsLoaded', 'commentAdded'])

const router = useRouter()
const socialStore = useSocialStore()
const userStore = useUserStore()

// State
const newComment = ref('')
const submitting = ref(false)
const loading = ref(false)
const loadingMore = ref(false)
const sortBy = ref('createdAt,desc')

const currentPage = ref(0)
const pageSize = ref(20)
const totalPages = ref(0)
const totalComments = ref(0)

// Computed
const commentsList = computed(() => {
  return socialStore.getCommentsByTargetLocal(props.targetType, props.targetId)
})

const hasMore = computed(() => {
  return currentPage.value + 1 < totalPages.value
})

// Methods
async function loadComments(page = 0) {
  if (page === 0) {
    loading.value = true
  } else {
    loadingMore.value = true
  }

  try {
    const params = {
      page,
      size: pageSize.value,
      sort: sortBy.value
    }

    const response = await socialStore.fetchComments(
      props.targetType,
      props.targetId,
      params
    )

    if (response) {
      currentPage.value = response.number || 0
      totalPages.value = response.totalPages || 0
      totalComments.value = response.totalElements || 0

      emit('commentsLoaded', {
        total: totalComments.value,
        page: currentPage.value
      })
    }
  } catch (error) {
    console.error('Failed to load comments:', error)
  } finally {
    loading.value = false
    loadingMore.value = false
  }
}

async function submitComment() {
  if (!newComment.value.trim()) {
    ElMessage.warning('Comment cannot be empty')
    return
  }

  submitting.value = true
  try {
    const commentData = {
      content: newComment.value.trim(),
      targetType: props.targetType,
      targetId: props.targetId
    }

    const result = await socialStore.createComment(commentData)
    if (result) {
      newComment.value = ''
      totalComments.value++
      emit('commentAdded', result)
    }
  } finally {
    submitting.value = false
  }
}

function handleSortChange() {
  // Reload comments with new sort
  socialStore.clearComments(props.targetType, props.targetId)
  currentPage.value = 0
  loadComments(0)
}

function loadMore() {
  if (!hasMore.value || loadingMore.value) return
  loadComments(currentPage.value + 1)
}

function handleCommentDeleted(commentId) {
  totalComments.value = Math.max(0, totalComments.value - 1)
}

function handleCommentReplied(reply) {
  totalComments.value++
  emit('commentAdded', reply)
}

function goToLogin() {
  router.push({ name: 'Login', query: { redirect: router.currentRoute.value.fullPath } })
}

// Lifecycle
onMounted(() => {
  if (props.autoLoad) {
    loadComments()
  }
})

// Watch for target changes
watch(
  () => [props.targetType, props.targetId],
  () => {
    if (props.autoLoad) {
      socialStore.clearComments(props.targetType, props.targetId)
      currentPage.value = 0
      loadComments(0)
    }
  }
)

// Expose methods
defineExpose({
  loadComments,
  refresh: () => {
    socialStore.clearComments(props.targetType, props.targetId)
    currentPage.value = 0
    loadComments(0)
  }
})
</script>

<style scoped lang="scss">
.comment-section {
  width: 100%;
  padding: 24px;
  background-color: #fff;
  border-radius: 8px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.05);
}

.section-header {
  margin-bottom: 24px;

  .section-title {
    display: flex;
    align-items: center;
    gap: 8px;
    font-size: 20px;
    font-weight: 600;
    color: #303133;
    margin: 0;

    .el-icon {
      font-size: 24px;
      color: #409eff;
    }

    .comment-count {
      font-size: 16px;
      color: #909399;
      font-weight: 400;
    }
  }
}

.comment-input-form {
  margin-bottom: 24px;
  padding: 16px;
  background-color: #f5f7fa;
  border-radius: 8px;
  border: 2px solid transparent;
  transition: all 0.3s ease;

  &:focus-within {
    border-color: #409eff;
    background-color: #fff;
  }

  .input-wrapper {
    display: flex;
    gap: 12px;
    margin-bottom: 12px;

    .el-avatar {
      flex-shrink: 0;
      margin-top: 4px;
    }

    .el-textarea {
      flex: 1;
    }
  }

  .input-actions {
    display: flex;
    justify-content: flex-end;
    gap: 8px;
  }
}

.login-prompt {
  margin-bottom: 24px;

  .el-alert {
    :deep(.el-alert__content) {
      display: flex;
      flex-direction: column;
      gap: 12px;

      p {
        margin: 0;
      }
    }
  }
}

.sort-options {
  display: flex;
  justify-content: flex-end;
  margin-bottom: 16px;
  padding-bottom: 16px;
  border-bottom: 1px solid #f0f0f0;

  .el-radio-button {
    :deep(.el-radio-button__inner) {
      display: flex;
      align-items: center;
      gap: 4px;
    }
  }
}

.comments-list {
  min-height: 200px;
}

.load-more {
  display: flex;
  justify-content: center;
  padding: 24px 0;

  .el-button {
    min-width: 200px;
  }
}

// Responsive
@media (max-width: 768px) {
  .comment-section {
    padding: 16px;
  }

  .section-header {
    margin-bottom: 16px;

    .section-title {
      font-size: 18px;

      .el-icon {
        font-size: 20px;
      }

      .comment-count {
        font-size: 14px;
      }
    }
  }

  .comment-input-form {
    padding: 12px;

    .input-wrapper {
      flex-direction: column;
      gap: 8px;

      .el-avatar {
        margin-top: 0;
      }
    }

    .input-actions {
      justify-content: stretch;

      .el-button {
        flex: 1;
      }
    }
  }

  .sort-options {
    justify-content: center;

    .el-radio-group {
      display: flex;
      flex-wrap: wrap;
      gap: 8px;
    }
  }
}
</style>
