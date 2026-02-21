import { describe, it, expect, vi, beforeEach, afterEach } from 'vitest'
import { setActivePinia, createPinia } from 'pinia'
import { useMusicStore } from '@/store/music'

// Mock API modules
vi.mock('@/api/music', () => ({
  uploadMusic: vi.fn(),
  getMusicById: vi.fn(),
  updateMusic: vi.fn(),
  deleteMusic: vi.fn(),
  getMyMusic: vi.fn(),
  searchMusic: vi.fn(),
  getPopularMusic: vi.fn(),
  getRecentMusic: vi.fn(),
  getMusicByGenre: vi.fn(),
  getAllPublicMusic: vi.fn(),
  recordPlay: vi.fn(),
}))

vi.mock('@/api/playlist', () => ({
  createPlaylist: vi.fn(),
  getPlaylistDetail: vi.fn(),
  updatePlaylist: vi.fn(),
  deletePlaylist: vi.fn(),
  getMyPlaylists: vi.fn(),
  getPublicPlaylists: vi.fn(),
  searchPlaylists: vi.fn(),
  getPopularPlaylists: vi.fn(),
  addMusicToPlaylist: vi.fn(),
  removeMusicFromPlaylist: vi.fn(),
  reorderPlaylistMusic: vi.fn(),
}))

vi.mock('@/api/genre', () => ({
  getAllGenres: vi.fn(),
  getPopularGenres: vi.fn(),
  getGenreById: vi.fn(),
}))

import * as musicApi from '@/api/music'
import * as playlistApi from '@/api/playlist'
import * as genreApi from '@/api/genre'

// NOTE: The music store wraps all API responses as { data: ... }
// So mocks must return { data: actualValue }

describe('Music Store', () => {
  let store

  beforeEach(() => {
    setActivePinia(createPinia())
    store = useMusicStore()
  })

  afterEach(() => {
    vi.clearAllMocks()
  })

  // ==================== INITIAL STATE ====================

  describe('initial state', () => {
    it('starts with empty music list', () => {
      expect(store.musicList).toEqual([])
      expect(store.totalMusic).toBe(0)
      expect(store.loading).toBe(false)
      expect(store.currentMusic).toBeNull()
    })

    it('starts with empty genres', () => {
      expect(store.genres).toEqual([])
      expect(store.currentGenre).toBeNull()
    })

    it('starts with empty playlists', () => {
      expect(store.playlists).toEqual([])
      expect(store.currentPlaylist).toBeNull()
      expect(store.totalPlaylists).toBe(0)
    })

    it('hasMusicList returns false when empty', () => {
      expect(store.hasMusicList).toBe(false)
    })

    it('hasGenres returns false when empty', () => {
      expect(store.hasGenres).toBe(false)
    })

    it('hasPlaylists returns false when empty', () => {
      expect(store.hasPlaylists).toBe(false)
    })
  })

  // ==================== UPLOAD MUSIC ====================

  describe('uploadMusic', () => {
    it('returns uploaded music data on success', async () => {
      const musicData = { id: 1, title: 'Test Song', artist: 'Artist' }
      musicApi.uploadMusic.mockResolvedValue({ data: musicData })

      const result = await store.uploadMusic(new FormData())

      expect(result).toEqual(musicData)
    })

    it('returns null on upload failure', async () => {
      musicApi.uploadMusic.mockRejectedValue(new Error('Upload failed'))

      const result = await store.uploadMusic(new FormData())

      expect(result).toBeNull()
    })

    it('sets loading to false after completion', async () => {
      musicApi.uploadMusic.mockResolvedValue({ data: { id: 1, title: 'Song' } })
      await store.uploadMusic(new FormData())
      expect(store.loading).toBe(false)
    })
  })

  // ==================== FETCH ALL PUBLIC MUSIC ====================

  describe('fetchAllPublicMusic', () => {
    it('updates musicList and totalMusic on success', async () => {
      const musicList = [
        { id: 1, title: 'Song 1', artist: 'Artist 1' },
        { id: 2, title: 'Song 2', artist: 'Artist 2' },
      ]
      musicApi.getAllPublicMusic.mockResolvedValue({
        data: { content: musicList, totalElements: 2 }
      })

      await store.fetchAllPublicMusic({})

      expect(store.musicList).toEqual(musicList)
      expect(store.totalMusic).toBe(2)
      expect(store.hasMusicList).toBe(true)
    })

    it('handles empty result', async () => {
      musicApi.getAllPublicMusic.mockResolvedValue({
        data: { content: [], totalElements: 0 }
      })

      await store.fetchAllPublicMusic({})

      expect(store.musicList).toEqual([])
      expect(store.totalMusic).toBe(0)
    })

    it('returns null on failure', async () => {
      musicApi.getAllPublicMusic.mockRejectedValue(new Error('Server error'))

      const result = await store.fetchAllPublicMusic({})

      expect(result).toBeNull()
    })
  })

  // ==================== FETCH MUSIC BY ID ====================

  describe('fetchMusicById', () => {
    it('sets currentMusic and returns data on success', async () => {
      const music = { id: 1, title: 'Test Song', artist: 'Artist' }
      musicApi.getMusicById.mockResolvedValue({ data: music })

      const result = await store.fetchMusicById(1)

      expect(result).toEqual(music)
      expect(store.currentMusic).toEqual(music)
    })

    it('returns null on failure', async () => {
      musicApi.getMusicById.mockRejectedValue(new Error('Not found'))

      const result = await store.fetchMusicById(999)

      expect(result).toBeNull()
    })
  })

  // ==================== SEARCH MUSIC ====================

  describe('searchMusic', () => {
    it('updates musicList with search results', async () => {
      const results = [{ id: 1, title: 'Found Song', artist: 'Artist' }]
      musicApi.searchMusic.mockResolvedValue({
        data: { content: results, totalElements: 1 }
      })

      await store.searchMusic({ keyword: 'Found', page: 0, size: 10 })

      expect(store.musicList).toEqual(results)
      expect(store.totalMusic).toBe(1)
    })
  })

  // ==================== DELETE MUSIC ====================

  describe('deleteMusic', () => {
    it('removes music from list on success', async () => {
      // Setup musicList
      musicApi.getAllPublicMusic.mockResolvedValue({
        data: { content: [{ id: 1, title: 'Song 1' }, { id: 2, title: 'Song 2' }], totalElements: 2 }
      })
      await store.fetchAllPublicMusic({})

      musicApi.deleteMusic.mockResolvedValue({})

      const result = await store.deleteMusic(1)

      expect(result).toBe(true)
      expect(store.musicList.find(m => m.id === 1)).toBeUndefined()
      expect(store.totalMusic).toBe(1)
    })

    it('clears currentMusic when current is deleted', async () => {
      store.currentMusic = { id: 5, title: 'Current Song' }
      musicApi.deleteMusic.mockResolvedValue({})

      await store.deleteMusic(5)

      expect(store.currentMusic).toBeNull()
    })

    it('returns false on failure', async () => {
      musicApi.deleteMusic.mockRejectedValue(new Error('Not found'))

      const result = await store.deleteMusic(999)

      expect(result).toBe(false)
    })
  })

  // ==================== POPULAR & RECENT MUSIC ====================

  describe('fetchPopularMusic', () => {
    it('updates musicList with popular music', async () => {
      const music = [{ id: 1, title: 'Popular Song', playCount: 1000 }]
      musicApi.getPopularMusic.mockResolvedValue({
        data: { content: music, totalElements: 1 }
      })

      await store.fetchPopularMusic({})

      expect(store.musicList).toEqual(music)
    })
  })

  describe('fetchRecentMusic', () => {
    it('updates musicList with recent music', async () => {
      const music = [{ id: 2, title: 'New Song' }]
      musicApi.getRecentMusic.mockResolvedValue({
        data: { content: music, totalElements: 1 }
      })

      await store.fetchRecentMusic({})

      expect(store.musicList).toEqual(music)
    })
  })

  // ==================== FETCH GENRES ====================

  describe('fetchGenres', () => {
    it('updates genres state on success', async () => {
      const genres = [
        { id: 1, name: 'Pop' },
        { id: 2, name: 'Rock' },
        { id: 3, name: 'Jazz' },
      ]
      genreApi.getAllGenres.mockResolvedValue({ data: genres })

      await store.fetchGenres()

      expect(store.genres).toEqual(genres)
      expect(store.hasGenres).toBe(true)
    })

    it('returns empty array on failure', async () => {
      genreApi.getAllGenres.mockRejectedValue(new Error('Server error'))

      const result = await store.fetchGenres()

      expect(result).toEqual([])
      expect(store.genres).toEqual([])
    })
  })

  // ==================== PLAYLIST ACTIONS ====================

  describe('createPlaylist', () => {
    it('adds playlist to list and returns data on success', async () => {
      const playlistData = { id: 1, name: 'My Playlist', musicCount: 0 }
      playlistApi.createPlaylist.mockResolvedValue({ data: playlistData })

      const result = await store.createPlaylist({ name: 'My Playlist', isPublic: true })

      expect(result).toEqual(playlistData)
      expect(store.playlists[0]).toEqual(playlistData) // added to front
      expect(store.totalPlaylists).toBe(1)
    })

    it('returns null on failure', async () => {
      playlistApi.createPlaylist.mockRejectedValue(new Error('Failed'))

      const result = await store.createPlaylist({ name: 'Playlist' })

      expect(result).toBeNull()
    })
  })

  describe('fetchMyPlaylists', () => {
    it('updates playlists state on success', async () => {
      const playlists = [
        { id: 1, name: 'Playlist 1' },
        { id: 2, name: 'Playlist 2' },
      ]
      playlistApi.getMyPlaylists.mockResolvedValue({
        data: { content: playlists, totalElements: 2 }
      })

      await store.fetchMyPlaylists({})

      expect(store.playlists).toEqual(playlists)
      expect(store.totalPlaylists).toBe(2)
      expect(store.hasPlaylists).toBe(true)
    })
  })

  describe('deletePlaylist', () => {
    it('removes playlist from list on success', async () => {
      // Setup playlists
      playlistApi.getMyPlaylists.mockResolvedValue({
        data: { content: [{ id: 1, name: 'P1' }, { id: 2, name: 'P2' }], totalElements: 2 }
      })
      await store.fetchMyPlaylists({})

      playlistApi.deletePlaylist.mockResolvedValue({})

      const result = await store.deletePlaylist(1)

      expect(result).toBe(true)
      expect(store.playlists.find(p => p.id === 1)).toBeUndefined()
      expect(store.totalPlaylists).toBe(1)
    })

    it('returns false on failure', async () => {
      playlistApi.deletePlaylist.mockRejectedValue(new Error('Permission denied'))

      const result = await store.deletePlaylist(1)

      expect(result).toBe(false)
    })
  })

  describe('addMusicToPlaylist', () => {
    it('returns true and increments currentPlaylist count when success', async () => {
      store.currentPlaylist = { id: 1, name: 'Playlist', musicCount: 3 }
      playlistApi.addMusicToPlaylist.mockResolvedValue({})

      const result = await store.addMusicToPlaylist(1, 10)

      expect(result).toBe(true)
      expect(store.currentPlaylist.musicCount).toBe(4)
    })

    it('returns false on failure', async () => {
      playlistApi.addMusicToPlaylist.mockRejectedValue(new Error('Already in playlist'))

      const result = await store.addMusicToPlaylist(1, 10)

      expect(result).toBe(false)
    })
  })

  describe('removeMusicFromPlaylist', () => {
    it('returns true on success', async () => {
      playlistApi.removeMusicFromPlaylist.mockResolvedValue({})

      const result = await store.removeMusicFromPlaylist(1, 10)

      expect(result).toBe(true)
    })

    it('returns false on failure', async () => {
      playlistApi.removeMusicFromPlaylist.mockRejectedValue(new Error('Not in playlist'))

      const result = await store.removeMusicFromPlaylist(1, 10)

      expect(result).toBe(false)
    })
  })

  // ==================== CLEAR ALL ====================

  describe('clearAll', () => {
    it('resets all state to initial values', async () => {
      // Populate state first
      musicApi.getAllPublicMusic.mockResolvedValue({
        data: { content: [{ id: 1, title: 'Song' }], totalElements: 1 }
      })
      await store.fetchAllPublicMusic({})

      store.clearAll()

      expect(store.musicList).toEqual([])
      expect(store.currentMusic).toBeNull()
      expect(store.totalMusic).toBe(0)
      expect(store.genres).toEqual([])
      expect(store.playlists).toEqual([])
      expect(store.currentPlaylist).toBeNull()
      expect(store.totalPlaylists).toBe(0)
    })
  })
})
