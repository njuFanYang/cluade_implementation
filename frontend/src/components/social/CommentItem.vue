<template>
  <div class="comment-item" :class="{ 'is-reply': isReply }">
    <!-- User Avatar and Info -->
    <div class="comment-avatar">
      <el-avatar
        :size="isReply ? 36 : 40"
        :src="comment.user?.avatar"
        :alt="comment.user?.username"
      >
        {{ comment.user?.username?.charAt(0).toUpperCase() }}
      </el-avatar>
    </div>

    <div class="comment-content">
      <!-- Header: Username, Time, Actions -->
      <div class="comment-header">
        <div class="user-info">
          <span class="username">{{ comment.user?.username }}</span>
          <span class="time">{{ formatTime(comment.createdAt) }}</span>
          <el-tag v-if="comment.user?.id === currentUserId" type="info" size="small">You</el-tag>
        </div>

        <div class="comment-actions">
          <!-- Edit button (only for own comments) -->
          <el-button
            v-if="canEdit"
            text
            size="small"
            @click="handleEdit"
          >
            <el-icon><Edit /></el-icon>
            Edit
          </el-button>

          <!-- Delete button (only for own comments or admin) -->
          <el-button
            v-if="canDelete"
            text
            type="danger"
            size="small"
            @click="handleDelete"
          >
            <el-icon><Delete /></el-icon>
            Delete
          </el-button>
        </div>
      </div>

      <!-- Comment Text -->
      <div v-if="!isEditing" class="comment-text">
        {{ comment.content }}
      </div>

      <!-- Edit Form -->
      <div v-else class="comment-edit-form">
        <el-input
          v-model="editContent"
          type="textarea"
          :rows="3"
          placeholder="Edit your comment..."
          maxlength="500"
          show-word-limit
        />
        <div class="edit-actions">
          <el-button size="small" @click="cancelEdit">Cancel</el-button>
          <el-button
            type="primary"
            size="small"
            :loading="editLoading"
            :disabled="!editContent.trim()"
            @click="submitEdit"
          >
            Save
          </el-button>
        </div>
      </div>

      <!-- Footer: Like, Reply -->
      <div v-if="!isEditing" class="comment-footer">
        <!-- Like Button -->
        <LikeButton
          target-type="COMMENT"
          :target-id="comment.id"
          :initial-liked="comment.isLiked"
          :initial-count="comment.likeCount || 0"
          size="small"
          @update:count="updateLikeCount"
        />

        <!-- Reply Button -->
        <el-button
          v-if="!isReply"
          text
          size="small"
          @click="toggleReply"
        >
          <el-icon><ChatDotRound /></el-icon>
          Reply
        </el-button>

        <!-- Reply Count -->
        <span v-if="comment.replyCount > 0 && !isReply" class="reply-count">
          {{ comment.replyCount }} {{ comment.replyCount === 1 ? 'reply' : 'replies' }}
        </span>
      </div>

      <!-- Reply Form -->
      <div v-if="showReplyForm" class="reply-form">
        <el-input
          v-model="replyContent"
          type="textarea"
          :rows="2"
          :placeholder="`Reply to ${comment.user?.username}...`"
          maxlength="500"
          show-word-limit
        />
        <div class="reply-actions">
          <el-button size="small" @click="cancelReply">Cancel</el-button>
          <el-button
            type="primary"
            size="small"
            :loading="replyLoading"
            :disabled="!replyContent.trim()"
            @click="submitReply"
          >
            Reply
          </el-button>
        </div>
      </div>

      <!-- Load Replies Button -->
      <div v-if="comment.replyCount > 0 && !isReply && !repliesLoaded" class="load-replies">
        <el-button
          text
          size="small"
          :loading="loadingReplies"
          @click="loadReplies"
        >
          <el-icon><ArrowDown /></el-icon>
          Show {{ comment.replyCount }} {{ comment.replyCount === 1 ? 'reply' : 'replies' }}
        </el-button>
      </div>

      <!-- Replies List -->
      <div v-if="replies.length > 0" class="replies-list">
        <CommentItem
          v-for="reply in replies"
          :key="reply.id"
          :comment="reply"
          :is-reply="true"
          @deleted="handleReplyDeleted"
          @replied="handleReplyReplied"
        />
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed } from 'vue'
import { useSocialStore } from '@/store/social'
import { useUserStore } from '@/store/user'
import { getReplies } from '@/api/social'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Edit, Delete, ChatDotRound, ArrowDown } from '@element-plus/icons-vue'
import LikeButton from './LikeButton.vue'
import { formatDistanceToNow } from 'date-fns'

const props = defineProps({
  comment: {
    type: Object,
    required: true
  },
  isReply: {
    type: Boolean,
    default: false
  }
})

const emit = defineEmits(['deleted', 'replied'])

const socialStore = useSocialStore()
const userStore = useUserStore()

// State
const isEditing = ref(false)
const editContent = ref('')
const editLoading = ref(false)

const showReplyForm = ref(false)
const replyContent = ref('')
const replyLoading = ref(false)

const replies = ref([])
const repliesLoaded = ref(false)
const loadingReplies = ref(false)

// Computed
const currentUserId = computed(() => userStore.currentUser?.id)

const canEdit = computed(() => {
  return props.comment.user?.id === currentUserId.value
})

const canDelete = computed(() => {
  const isOwner = props.comment.user?.id === currentUserId.value
  const isAdmin = userStore.currentUser?.roles?.some(role => role.name === 'ROLE_ADMIN')
  return isOwner || isAdmin
})

// Methods
function formatTime(timestamp) {
  if (!timestamp) return ''
  try {
    return formatDistanceToNow(new Date(timestamp), { addSuffix: true })
  } catch (error) {
    return timestamp
  }
}

function handleEdit() {
  editContent.value = props.comment.content
  isEditing.value = true
}

function cancelEdit() {
  isEditing.value = false
  editContent.value = ''
}

async function submitEdit() {
  if (!editContent.value.trim()) {
    ElMessage.warning('Comment cannot be empty')
    return
  }

  editLoading.value = true
  try {
    const success = await socialStore.updateComment(props.comment.id, editContent.value.trim())
    if (success) {
      isEditing.value = false
      editContent.value = ''
    }
  } finally {
    editLoading.value = false
  }
}

async function handleDelete() {
  try {
    await ElMessageBox.confirm(
      'Are you sure you want to delete this comment? This action cannot be undone.',
      'Delete Comment',
      {
        confirmButtonText: 'Delete',
        cancelButtonText: 'Cancel',
        type: 'warning',
        confirmButtonClass: 'el-button--danger'
      }
    )

    const success = await socialStore.deleteComment(
      props.comment.id,
      props.comment.targetType,
      props.comment.targetId
    )

    if (success) {
      emit('deleted', props.comment.id)
    }
  } catch (error) {
    // User cancelled
  }
}

function toggleReply() {
  showReplyForm.value = !showReplyForm.value
  if (!showReplyForm.value) {
    replyContent.value = ''
  }
}

function cancelReply() {
  showReplyForm.value = false
  replyContent.value = ''
}

async function submitReply() {
  if (!replyContent.value.trim()) {
    ElMessage.warning('Reply cannot be empty')
    return
  }

  replyLoading.value = true
  try {
    const replyData = {
      content: replyContent.value.trim(),
      targetType: props.comment.targetType,
      targetId: props.comment.targetId,
      parentCommentId: props.comment.id
    }

    const result = await socialStore.createComment(replyData)
    if (result) {
      showReplyForm.value = false
      replyContent.value = ''
      emit('replied', result)

      // Add to replies list if already loaded
      if (repliesLoaded.value) {
        replies.value.push(result)
      }

      // Update reply count
      if (props.comment.replyCount !== undefined) {
        props.comment.replyCount++
      }
    }
  } finally {
    replyLoading.value = false
  }
}

async function loadReplies() {
  if (repliesLoaded.value || loadingReplies.value) return

  loadingReplies.value = true
  try {
    const response = await getReplies(props.comment.id, {
      page: 0,
      size: 50,
      sort: 'createdAt,asc'
    })

    if (response.data) {
      replies.value = response.data.content || []
      repliesLoaded.value = true
    }
  } catch (error) {
    console.error('Failed to load replies:', error)
    ElMessage.error('Failed to load replies')
  } finally {
    loadingReplies.value = false
  }
}

function handleReplyDeleted(replyId) {
  replies.value = replies.value.filter(r => r.id !== replyId)
  if (props.comment.replyCount !== undefined) {
    props.comment.replyCount = Math.max(0, props.comment.replyCount - 1)
  }
}

function handleReplyReplied(reply) {
  emit('replied', reply)
}

function updateLikeCount(count) {
  if (props.comment.likeCount !== undefined) {
    props.comment.likeCount = count
  }
}
</script>

<style scoped lang="scss">
.comment-item {
  display: flex;
  gap: 12px;
  padding: 16px 0;
  border-bottom: 1px solid #f0f0f0;

  &.is-reply {
    padding: 12px 0;
    margin-left: 20px;

    .comment-avatar {
      flex-shrink: 0;
    }
  }

  &:last-child {
    border-bottom: none;
  }
}

.comment-avatar {
  flex-shrink: 0;
}

.comment-content {
  flex: 1;
  min-width: 0;
}

.comment-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 8px;
  gap: 12px;

  .user-info {
    display: flex;
    align-items: center;
    gap: 8px;
    flex: 1;
    min-width: 0;

    .username {
      font-weight: 600;
      color: #303133;
      font-size: 14px;
    }

    .time {
      color: #909399;
      font-size: 12px;
      white-space: nowrap;
    }
  }

  .comment-actions {
    display: flex;
    gap: 4px;
    opacity: 0;
    transition: opacity 0.2s;

    .el-button {
      padding: 4px 8px;
    }
  }
}

.comment-item:hover .comment-actions {
  opacity: 1;
}

.comment-text {
  color: #606266;
  font-size: 14px;
  line-height: 1.6;
  word-wrap: break-word;
  white-space: pre-wrap;
  margin-bottom: 8px;
}

.comment-edit-form {
  margin-bottom: 12px;

  .edit-actions {
    display: flex;
    justify-content: flex-end;
    gap: 8px;
    margin-top: 8px;
  }
}

.comment-footer {
  display: flex;
  align-items: center;
  gap: 16px;
  margin-top: 4px;

  .reply-count {
    color: #909399;
    font-size: 13px;
    margin-left: auto;
  }
}

.reply-form {
  margin-top: 12px;
  padding: 12px;
  background-color: #f5f7fa;
  border-radius: 8px;

  .reply-actions {
    display: flex;
    justify-content: flex-end;
    gap: 8px;
    margin-top: 8px;
  }
}

.load-replies {
  margin-top: 12px;

  .el-button {
    color: #409eff;
    font-weight: 500;
  }
}

.replies-list {
  margin-top: 16px;
  border-top: 1px solid #f0f0f0;
  padding-top: 8px;
}

// Responsive
@media (max-width: 768px) {
  .comment-item {
    gap: 8px;

    &.is-reply {
      margin-left: 12px;
    }
  }

  .comment-header {
    flex-wrap: wrap;

    .user-info {
      .username {
        font-size: 13px;
      }

      .time {
        font-size: 11px;
      }
    }

    .comment-actions {
      opacity: 1; // Always show on mobile
    }
  }

  .comment-text {
    font-size: 13px;
  }

  .comment-footer {
    flex-wrap: wrap;
    gap: 8px;
  }
}
</style>
