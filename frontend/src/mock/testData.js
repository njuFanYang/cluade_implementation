/**
 * Mock Test Data for Frontend Testing
 *
 * This file contains sample data for testing the UI without backend
 * Use this data to populate components during development
 */

export const mockMusic = [
  {
    id: 1,
    title: 'Midnight Dreams',
    artist: 'Luna Rivers',
    album: { id: 1, title: 'Echoes of Tomorrow' },
    genre: { id: 1, name: 'Pop' },
    duration: 245,
    coverImage: 'https://picsum.photos/seed/music1/300/300',
    description: 'An uplifting pop anthem about chasing your dreams',
    releaseDate: '2024-02-15',
    isPublic: true,
    playCount: 1523,
    likeCount: 234,
    isLiked: false,
    uploader: { id: 1, username: 'musiclover', avatar: 'https://i.pravatar.cc/150?img=1' },
    createdAt: '2024-02-15T10:00:00Z'
  },
  {
    id: 2,
    title: 'Neon Lights',
    artist: 'The Synthwave Collective',
    album: { id: 1, title: 'Echoes of Tomorrow' },
    genre: { id: 2, name: 'Electronic' },
    duration: 198,
    coverImage: 'https://picsum.photos/seed/music2/300/300',
    description: 'Synthwave track with retro vibes',
    releaseDate: '2024-03-15',
    isPublic: true,
    playCount: 2341,
    likeCount: 445,
    isLiked: true,
    uploader: { id: 1, username: 'musiclover', avatar: 'https://i.pravatar.cc/150?img=1' },
    createdAt: '2024-03-15T10:00:00Z'
  },
  {
    id: 3,
    title: 'Summer Love',
    artist: 'Tropical House Collective',
    genre: { id: 1, name: 'Pop' },
    duration: 223,
    coverImage: 'https://picsum.photos/seed/music3/300/300',
    description: 'Perfect summer romance song',
    releaseDate: '2024-07-01',
    isPublic: true,
    playCount: 3456,
    likeCount: 567,
    isLiked: false,
    uploader: { id: 1, username: 'musiclover', avatar: 'https://i.pravatar.cc/150?img=1' },
    createdAt: '2024-07-01T10:00:00Z'
  },
  {
    id: 4,
    title: 'Thunder Road',
    artist: 'The Rock Legends',
    genre: { id: 3, name: 'Rock' },
    duration: 267,
    coverImage: 'https://picsum.photos/seed/music4/300/300',
    description: 'Hard-hitting rock anthem',
    releaseDate: '2024-01-20',
    isPublic: true,
    playCount: 2890,
    likeCount: 378,
    isLiked: true,
    uploader: { id: 1, username: 'musiclover', avatar: 'https://i.pravatar.cc/150?img=1' },
    createdAt: '2024-01-20T10:00:00Z'
  },
  {
    id: 5,
    title: 'City Streets',
    artist: 'DJ Phoenix',
    genre: { id: 4, name: 'Hip-Hop' },
    duration: 189,
    coverImage: 'https://picsum.photos/seed/music6/300/300',
    description: 'Urban hip-hop with smooth flow',
    releaseDate: '2023-11-10',
    isPublic: true,
    playCount: 4521,
    likeCount: 678,
    isLiked: false,
    uploader: { id: 1, username: 'musiclover', avatar: 'https://i.pravatar.cc/150?img=1' },
    createdAt: '2023-11-10T10:00:00Z'
  }
]

export const mockPlaylists = [
  {
    id: 1,
    name: 'Chill Vibes',
    description: 'Perfect playlist for relaxing and unwinding after a long day',
    coverImage: 'https://picsum.photos/seed/playlist1/400/400',
    isPublic: true,
    creator: { id: 1, username: 'musiclover', avatar: 'https://i.pravatar.cc/150?img=1' },
    musicCount: 5,
    playCount: 856,
    likeCount: 123,
    isLiked: false,
    createdAt: '2024-01-15T10:00:00Z'
  },
  {
    id: 2,
    name: 'Workout Motivation',
    description: 'High-energy tracks to power through your workout',
    coverImage: 'https://picsum.photos/seed/playlist2/400/400',
    isPublic: true,
    creator: { id: 1, username: 'musiclover', avatar: 'https://i.pravatar.cc/150?img=1' },
    musicCount: 5,
    playCount: 1234,
    likeCount: 234,
    isLiked: true,
    createdAt: '2024-02-01T10:00:00Z'
  },
  {
    id: 3,
    name: 'Study Focus',
    description: 'Instrumental and ambient music for concentration',
    coverImage: 'https://picsum.photos/seed/playlist3/400/400',
    isPublic: true,
    creator: { id: 2, username: 'studybuddy', avatar: 'https://i.pravatar.cc/150?img=2' },
    musicCount: 4,
    playCount: 2341,
    likeCount: 456,
    isLiked: false,
    createdAt: '2024-03-10T10:00:00Z'
  }
]

export const mockComments = [
  {
    id: 1,
    content: 'This track is absolutely amazing! The production quality is top-notch.',
    targetType: 'MUSIC',
    targetId: 1,
    user: { id: 2, username: 'musicfan', avatar: 'https://i.pravatar.cc/150?img=2' },
    likeCount: 15,
    replyCount: 2,
    isLiked: false,
    createdAt: new Date(Date.now() - 5 * 24 * 60 * 60 * 1000).toISOString(),
    updatedAt: new Date(Date.now() - 5 * 24 * 60 * 60 * 1000).toISOString()
  },
  {
    id: 2,
    content: 'I agree! The mixing is incredible.',
    targetType: 'MUSIC',
    targetId: 1,
    parentCommentId: 1,
    user: { id: 1, username: 'musiclover', avatar: 'https://i.pravatar.cc/150?img=1' },
    likeCount: 8,
    replyCount: 0,
    isLiked: true,
    createdAt: new Date(Date.now() - 4 * 24 * 60 * 60 * 1000).toISOString(),
    updatedAt: new Date(Date.now() - 4 * 24 * 60 * 60 * 1000).toISOString()
  },
  {
    id: 3,
    content: 'Love the synthwave vibes! Takes me back to the 80s.',
    targetType: 'MUSIC',
    targetId: 2,
    user: { id: 2, username: 'musicfan', avatar: 'https://i.pravatar.cc/150?img=2' },
    likeCount: 23,
    replyCount: 1,
    isLiked: false,
    createdAt: new Date(Date.now() - 4 * 24 * 60 * 60 * 1000).toISOString(),
    updatedAt: new Date(Date.now() - 4 * 24 * 60 * 60 * 1000).toISOString()
  },
  {
    id: 4,
    content: 'Perfect summer anthem! Playing this all season long.',
    targetType: 'MUSIC',
    targetId: 3,
    user: { id: 1, username: 'musiclover', avatar: 'https://i.pravatar.cc/150?img=1' },
    likeCount: 34,
    replyCount: 0,
    isLiked: false,
    createdAt: new Date(Date.now() - 3 * 24 * 60 * 60 * 1000).toISOString(),
    updatedAt: new Date(Date.now() - 3 * 24 * 60 * 60 * 1000).toISOString()
  },
  {
    id: 5,
    content: 'This playlist is perfect for my morning routine!',
    targetType: 'PLAYLIST',
    targetId: 1,
    user: { id: 2, username: 'musicfan', avatar: 'https://i.pravatar.cc/150?img=2' },
    likeCount: 45,
    replyCount: 1,
    isLiked: true,
    createdAt: new Date(Date.now() - 2 * 24 * 60 * 60 * 1000).toISOString(),
    updatedAt: new Date(Date.now() - 2 * 24 * 60 * 60 * 1000).toISOString()
  }
]

export const mockGenres = [
  { id: 1, name: 'Pop', musicCount: 120 },
  { id: 2, name: 'Electronic', musicCount: 95 },
  { id: 3, name: 'Rock', musicCount: 88 },
  { id: 4, name: 'Hip-Hop', musicCount: 76 },
  { id: 5, name: 'Jazz', musicCount: 54 },
  { id: 6, name: 'Classical', musicCount: 42 },
  { id: 7, name: 'Blues', musicCount: 31 },
  { id: 8, name: 'Reggae', musicCount: 28 }
]

// Helper function to get mock data
export function getMockMusicById(id) {
  return mockMusic.find(m => m.id === parseInt(id))
}

export function getMockPlaylistById(id) {
  return mockPlaylists.find(p => p.id === parseInt(id))
}

export function getMockCommentsByTarget(targetType, targetId) {
  return mockComments.filter(c =>
    c.targetType === targetType &&
    c.targetId === parseInt(targetId) &&
    !c.parentCommentId
  )
}

export function getMockRepliesByCommentId(commentId) {
  return mockComments.filter(c => c.parentCommentId === parseInt(commentId))
}
