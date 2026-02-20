# Mock Test Data Usage Guide

## 📋 Overview

This directory contains mock test data for frontend testing without a backend.

## 📦 What's Included

- **20 Music Tracks** - Various genres (Pop, Rock, Hip-Hop, Jazz, Electronic, etc.)
- **5 Playlists** - Curated collections with different themes
- **Sample Comments** - Comments and replies on music and playlists
- **Sample Likes** - Like data for music, playlists, and comments
- **Genres** - 8 different music genres with counts

## 🚀 Quick Start

### Option 1: Import in Components (Temporary Testing)

```javascript
import { mockMusic, mockPlaylists, mockComments } from '@/mock/testData'

// In your component
const musicList = ref(mockMusic)
const playlists = ref(mockPlaylists)
```

### Option 2: Use in Store (Better for Testing)

You can temporarily replace API calls in your stores:

```javascript
// In musicStore.js (for testing only)
async function fetchMusic() {
  // Temporarily replace API call
  // const response = await getMusicList(params)

  // Use mock data instead
  const { mockMusic } = await import('@/mock/testData')
  musicList.value = mockMusic
}
```

### Option 3: Mock API Responses (Most Realistic)

Create a mock API interceptor:

```javascript
// In src/api/request.js or a separate mock.js file
if (import.meta.env.MODE === 'development') {
  // Intercept API calls and return mock data
  // This is the most realistic approach
}
```

## 📊 Data Structure

### Music Object
```javascript
{
  id: 1,
  title: 'Midnight Dreams',
  artist: 'Luna Rivers',
  album: { id: 1, title: 'Album Name' },
  genre: { id: 1, name: 'Pop' },
  duration: 245, // seconds
  coverImage: 'https://...',
  description: '...',
  playCount: 1523,
  likeCount: 234,
  isLiked: false,
  uploader: { id: 1, username: '...', avatar: '...' }
}
```

### Playlist Object
```javascript
{
  id: 1,
  name: 'Chill Vibes',
  description: '...',
  coverImage: 'https://...',
  creator: { id: 1, username: '...', avatar: '...' },
  musicCount: 5,
  playCount: 856,
  likeCount: 123,
  isLiked: false
}
```

### Comment Object
```javascript
{
  id: 1,
  content: '...',
  targetType: 'MUSIC', // or 'PLAYLIST', 'COMMENT'
  targetId: 1,
  parentCommentId: null, // for replies
  user: { id: 2, username: '...', avatar: '...' },
  likeCount: 15,
  replyCount: 2,
  isLiked: false,
  createdAt: '2024-02-15T10:00:00Z'
}
```

## 🎯 Example: Testing Music Detail Page

```vue
<script setup>
import { ref, onMounted } from 'vue'
import { useRoute } from 'vue-router'
import { getMockMusicById, getMockCommentsByTarget } from '@/mock/testData'

const route = useRoute()
const music = ref(null)
const comments = ref([])

onMounted(() => {
  // Load mock data instead of API call
  music.value = getMockMusicById(route.params.id)
  comments.value = getMockCommentsByTarget('MUSIC', route.params.id)
})
</script>
```

## 🔄 When Backend is Ready

Once your backend is running:

1. Remove mock data imports
2. Restore original API calls
3. The SQL script in `backend/src/main/resources/db/migration/V6__insert_test_data.sql` will populate the database

## 📝 Backend SQL Script

The SQL script includes:
- 20 music tracks across multiple genres
- 5 curated playlists
- Album data
- Comments and replies
- Like records
- Follow relationships

It will be automatically executed by Flyway when the backend starts.

## 🖼️ Images

All cover images use placeholder services:
- Music covers: `https://picsum.photos/seed/musicX/300/300`
- Playlist covers: `https://picsum.photos/seed/playlistX/400/400`
- User avatars: `https://i.pravatar.cc/150?img=X`

These will load actual images from the internet.

## ⚠️ Important Notes

1. **Temporary Solution**: Mock data is only for UI testing
2. **No Persistence**: Changes won't be saved
3. **Limited Functionality**: Some features (upload, delete) won't work
4. **Use Helper Functions**: Use `getMockMusicById()` etc. for consistency

## 🎨 Customization

Feel free to add more mock data:

```javascript
export const mockMusic = [
  ...mockMusic,
  {
    id: 21,
    title: 'Your New Track',
    // ... more fields
  }
]
```

## 🚀 Next Steps

1. Test UI components with mock data
2. Verify layouts and interactions
3. Test social features (comments, likes)
4. Once satisfied, set up backend and use real data
