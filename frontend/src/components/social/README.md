# Social Interaction Components

Reusable Vue components for social interaction features including likes and follows.

## Components

### LikeButton

A reusable button component for liking/unliking content (music, playlists, comments).

#### Props

| Prop | Type | Required | Default | Description |
|------|------|----------|---------|-------------|
| `targetType` | String | Yes | - | Type of target entity (`MUSIC`, `PLAYLIST`, `COMMENT`) |
| `targetId` | Number/String | Yes | - | ID of the target entity |
| `initialLiked` | Boolean | No | `false` | Initial liked state |
| `initialCount` | Number | No | `0` | Initial like count |
| `size` | String | No | `'default'` | Button size (`small`, `default`, `large`) |
| `showCount` | Boolean | No | `true` | Whether to display the like count |

#### Events

| Event | Payload | Description |
|-------|---------|-------------|
| `update:liked` | Boolean | Emitted when like state changes |
| `update:count` | Number | Emitted when like count updates |

#### Methods

| Method | Description |
|--------|-------------|
| `refreshLikeCount()` | Refreshes the like count from the backend |

#### Usage Example

```vue
<template>
  <LikeButton
    target-type="MUSIC"
    :target-id="music.id"
    :initial-liked="music.isLiked"
    :initial-count="music.likeCount"
    size="default"
    @update:liked="onLikeChanged"
    @update:count="onCountChanged"
  />
</template>

<script setup>
import { LikeButton } from '@/components/Social'

const onLikeChanged = (isLiked) => {
  console.log('Like status changed:', isLiked)
}

const onCountChanged = (count) => {
  console.log('Like count changed:', count)
}
</script>
```

#### Features

- Heart icon animation on like
- Optimistic UI updates
- Automatic count formatting (K, M suffixes)
- Login check before allowing interactions
- Error handling with automatic rollback
- Red color when liked, gray when not
- Integration with social store

---

### FollowButton

A reusable button component for following/unfollowing users.

#### Props

| Prop | Type | Required | Default | Description |
|------|------|----------|---------|-------------|
| `userId` | Number/String | Yes | - | ID of the user to follow |
| `username` | String | No | `''` | Username (for display purposes) |
| `initialFollowing` | Boolean | No | `false` | Initial following state |
| `size` | String | No | `'default'` | Button size (`small`, `default`, `large`) |
| `type` | String | No | `'primary'` | Button type (`primary`, `default`, `success`, etc.) |
| `showIcon` | Boolean | No | `true` | Whether to show the user icon |

#### Events

| Event | Payload | Description |
|-------|---------|-------------|
| `update:following` | Boolean | Emitted when following state changes |

#### Methods

| Method | Description |
|--------|-------------|
| `handleFollow()` | Manually trigger follow/unfollow action |

#### Usage Example

```vue
<template>
  <FollowButton
    :user-id="user.id"
    :username="user.username"
    :initial-following="user.isFollowing"
    size="default"
    type="primary"
    @update:following="onFollowChanged"
  />
</template>

<script setup>
import { FollowButton } from '@/components/Social'

const onFollowChanged = (isFollowing) => {
  console.log('Follow status changed:', isFollowing)
}
</script>
```

#### Features

- Text changes from "Follow" to "Following"
- Hover state shows "Unfollow" when following
- Optimistic UI updates
- Login check before allowing interactions
- Prevents self-following
- Error handling with automatic rollback
- Different styles based on following state
- Integration with social store

---

## Store Integration

Both components integrate with the `useSocialStore` from `@/store/social` for state management and API calls. The store handles:

- Optimistic updates
- API communication
- Error handling and rollback
- State persistence
- Success/error messages

## Error Handling

Both components include comprehensive error handling:

1. **Login Check**: Prompts user to login before allowing interactions
2. **Optimistic Updates**: UI updates immediately for better UX
3. **Automatic Rollback**: Reverts state if API call fails
4. **Error Messages**: Displays appropriate messages via Element Plus
5. **Loading States**: Prevents multiple simultaneous requests

## Styling

Both components use Element Plus buttons with custom styling:

- Smooth transitions and animations
- Responsive design
- Consistent spacing and sizing
- Accessible color schemes
- Mobile-friendly touch targets

## Best Practices

1. Always provide `targetType` and `targetId` / `userId`
2. Initialize with correct `initialLiked` / `initialFollowing` state
3. Listen to update events to sync with parent component state
4. Use appropriate `size` prop based on UI context
5. Consider disabling during loading states in parent component
6. Handle the update events to refresh related data if needed

## Dependencies

- Vue 3 Composition API
- Element Plus UI library
- Pinia store (`useSocialStore`, `useUserStore`)
- Social API module (`@/api/social`)
- Element Plus icons

## Browser Support

Compatible with all modern browsers that support Vue 3 and Element Plus.

---

### CommentSection

A complete comment section component with nested replies, sorting, and pagination.

#### Props

| Prop | Type | Required | Default | Description |
|------|------|----------|---------|-------------|
| `targetType` | String | Yes | - | Type of target entity (`MUSIC`, `PLAYLIST`, `COMMENT`) |
| `targetId` | Number/String | Yes | - | ID of the target entity |
| `autoLoad` | Boolean | No | `true` | Whether to automatically load comments on mount |

#### Events

| Event | Payload | Description |
|-------|---------|-------------|
| `commentsLoaded` | Object | Emitted when comments are loaded (contains total and page) |
| `commentAdded` | Object | Emitted when a new comment is posted |

#### Methods

| Method | Description |
|--------|-------------|
| `loadComments(page)` | Load comments for a specific page |
| `refresh()` | Refresh and reload all comments from the beginning |

#### Usage Example

```vue
<template>
  <CommentSection
    target-type="MUSIC"
    :target-id="musicId"
    :auto-load="true"
    @comments-loaded="onCommentsLoaded"
    @comment-added="onCommentAdded"
  />
</template>

<script setup>
import { ref } from 'vue'
import { CommentSection } from '@/components/social'

const musicId = ref(1)

const onCommentsLoaded = ({ total, page }) => {
  console.log(`Loaded ${total} comments, page ${page}`)
}

const onCommentAdded = (comment) => {
  console.log('New comment:', comment)
}
</script>
```

#### Features

- Comment input with character count
- Login check and redirect
- Three sort options: Newest, Oldest, Most Liked
- Nested reply support
- Pagination with "Load More" button
- Empty state when no comments
- Real-time comment statistics
- Responsive design
- Integration with social store

---

### CommentItem

A single comment item component with reply, edit, and delete functionality.

#### Props

| Prop | Type | Required | Default | Description |
|------|------|----------|---------|-------------|
| `comment` | Object | Yes | - | Comment data object |
| `isReply` | Boolean | No | `false` | Whether this is a reply (affects styling) |

#### Events

| Event | Payload | Description |
|-------|---------|-------------|
| `deleted` | Number | Emitted when comment is deleted (comment ID) |
| `replied` | Object | Emitted when a reply is posted |

#### Comment Object Structure

```typescript
{
  id: number
  content: string
  user: {
    id: number
    username: string
    avatar?: string
  }
  targetType: string
  targetId: number
  parentCommentId?: number
  replyCount?: number
  likeCount?: number
  isLiked?: boolean
  createdAt: string
  updatedAt: string
}
```

#### Usage Example

```vue
<template>
  <CommentItem
    :comment="comment"
    :is-reply="false"
    @deleted="handleDeleted"
    @replied="handleReplied"
  />
</template>

<script setup>
import { CommentItem } from '@/components/social'

const comment = {
  id: 1,
  content: 'Great music!',
  user: {
    id: 1,
    username: 'john_doe',
    avatar: '/avatar.jpg'
  },
  targetType: 'MUSIC',
  targetId: 100,
  replyCount: 2,
  likeCount: 5,
  isLiked: false,
  createdAt: '2026-02-20T10:00:00Z'
}

const handleDeleted = (commentId) => {
  console.log('Comment deleted:', commentId)
}

const handleReplied = (reply) => {
  console.log('New reply:', reply)
}
</script>
```

#### Features

- User avatar and username display
- Time ago formatting (e.g., "2 hours ago")
- Edit comment (own comments only)
- Delete comment (own comments or admin)
- Reply to comments
- Like button integration
- Load and display nested replies
- Reply count display
- Confirmation dialog for delete
- Optimistic UI updates
- Error handling with rollback
- Hover actions for better UX
- Mobile responsive design

#### Permissions

- **Edit**: Only the comment owner can edit
- **Delete**: Comment owner or users with ADMIN role
- **Reply**: All logged-in users
- **Like**: All logged-in users
