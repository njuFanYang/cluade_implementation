import { describe, it, expect, vi, beforeEach, afterEach } from 'vitest'
import { setActivePinia, createPinia } from 'pinia'
import { useUserStore } from '@/store/user'

// Mock API modules
vi.mock('@/api/user', () => ({
  register: vi.fn(),
  login: vi.fn(),
  logout: vi.fn(),
  getUserProfile: vi.fn(),
  updateProfile: vi.fn(),
  changePassword: vi.fn(),
}))

import * as userApi from '@/api/user'

describe('User Store', () => {
  let store

  beforeEach(() => {
    setActivePinia(createPinia())
    localStorage.clear()
    localStorage.getItem.mockReturnValue(null)
    store = useUserStore()
  })

  afterEach(() => {
    vi.clearAllMocks()
  })

  // ==================== STATE & GETTERS ====================

  describe('isLoggedIn getter', () => {
    it('returns false when no token', () => {
      expect(store.isLoggedIn).toBe(false)
    })

    it('returns true after successful login', async () => {
      userApi.login.mockResolvedValue({
        token: 'test-token',
        userInfo: { id: 1, username: 'user', roles: ['ROLE_USER'] }
      })
      await store.login({ usernameOrEmail: 'user', password: 'pass' })
      expect(store.isLoggedIn).toBe(true)
    })
  })

  describe('isAdmin getter', () => {
    it('returns false when no roles', () => {
      expect(store.isAdmin).toBe(false)
    })

    it('returns true when ROLE_ADMIN in roles', async () => {
      userApi.login.mockResolvedValue({
        token: 'test-token',
        userInfo: { id: 1, username: 'admin', roles: ['ROLE_USER', 'ROLE_ADMIN'] }
      })
      await store.login({ usernameOrEmail: 'admin', password: 'pass' })
      expect(store.isAdmin).toBe(true)
    })

    it('returns false for regular user', async () => {
      userApi.login.mockResolvedValue({
        token: 'test-token',
        userInfo: { id: 1, username: 'user', roles: ['ROLE_USER'] }
      })
      await store.login({ usernameOrEmail: 'user', password: 'pass' })
      expect(store.isAdmin).toBe(false)
    })
  })

  describe('isMusician getter', () => {
    it('returns false for regular user', async () => {
      userApi.login.mockResolvedValue({
        token: 'test-token',
        userInfo: { id: 1, username: 'user', roles: ['ROLE_USER'] }
      })
      await store.login({ usernameOrEmail: 'user', password: 'pass' })
      expect(store.isMusician).toBe(false)
    })

    it('returns true for ROLE_MUSICIAN', async () => {
      userApi.login.mockResolvedValue({
        token: 'test-token',
        userInfo: { id: 2, username: 'musician', roles: ['ROLE_USER', 'ROLE_MUSICIAN'] }
      })
      await store.login({ usernameOrEmail: 'musician', password: 'pass' })
      expect(store.isMusician).toBe(true)
    })

    it('returns true for ROLE_ADMIN (admin can do musician things)', async () => {
      userApi.login.mockResolvedValue({
        token: 'test-token',
        userInfo: { id: 3, username: 'admin', roles: ['ROLE_ADMIN'] }
      })
      await store.login({ usernameOrEmail: 'admin', password: 'pass' })
      expect(store.isMusician).toBe(true)
    })
  })

  describe('computed user properties', () => {
    it('returns values from userInfo', async () => {
      userApi.login.mockResolvedValue({
        token: 'token',
        userInfo: {
          id: 42, username: 'testuser', nickname: 'Test User',
          email: 'test@example.com', bio: 'Hello', roles: []
        }
      })
      await store.login({ usernameOrEmail: 'testuser', password: 'pass' })

      expect(store.userId).toBe(42)
      expect(store.username).toBe('testuser')
      expect(store.nickname).toBe('Test User')
      expect(store.email).toBe('test@example.com')
      expect(store.bio).toBe('Hello')
    })

    it('uses username as nickname fallback when nickname is null', async () => {
      userApi.login.mockResolvedValue({
        token: 'token',
        userInfo: { id: 42, username: 'testuser', nickname: null, roles: [] }
      })
      await store.login({ usernameOrEmail: 'testuser', password: 'pass' })
      expect(store.nickname).toBe('testuser')
    })
  })

  describe('social count getters', () => {
    it('returns 0 defaults when not set', () => {
      expect(store.followersCount).toBe(0)
      expect(store.followingCount).toBe(0)
      expect(store.musicCount).toBe(0)
      expect(store.playlistCount).toBe(0)
    })

    it('returns values from userInfo', async () => {
      userApi.login.mockResolvedValue({
        token: 'token',
        userInfo: {
          id: 1, username: 'user', roles: [],
          followersCount: 10, followingCount: 5,
          musicCount: 20, playlistCount: 3
        }
      })
      await store.login({ usernameOrEmail: 'user', password: 'pass' })

      expect(store.followersCount).toBe(10)
      expect(store.followingCount).toBe(5)
      expect(store.musicCount).toBe(20)
      expect(store.playlistCount).toBe(3)
    })
  })

  // ==================== LOGIN ACTION ====================

  describe('login action', () => {
    it('stores token and user info on success', async () => {
      const mockResponse = {
        token: 'jwt-token-123',
        userInfo: { id: 1, username: 'user', roles: ['ROLE_USER'] }
      }
      userApi.login.mockResolvedValue(mockResponse)

      const result = await store.login({ usernameOrEmail: 'user', password: 'pass' })

      expect(result).toBe(true)
      expect(store.token).toBe('jwt-token-123')
      expect(store.userInfo).toEqual(mockResponse.userInfo)
      expect(localStorage.setItem).toHaveBeenCalledWith('token', 'jwt-token-123')
      expect(localStorage.setItem).toHaveBeenCalledWith('userInfo', JSON.stringify(mockResponse.userInfo))
    })

    it('returns false and does not update state on failure', async () => {
      userApi.login.mockRejectedValue(new Error('Invalid credentials'))

      const result = await store.login({ usernameOrEmail: 'bad', password: 'wrong' })

      expect(result).toBe(false)
      expect(store.token).toBe('')
      expect(store.userInfo).toBeNull()
    })
  })

  // ==================== LOGOUT ACTION ====================

  describe('logout action', () => {
    it('clears token, userInfo and localStorage on logout', async () => {
      userApi.login.mockResolvedValue({
        token: 'token',
        userInfo: { id: 1, username: 'user', roles: [] }
      })
      await store.login({ usernameOrEmail: 'user', password: 'pass' })

      userApi.logout.mockResolvedValue({})
      await store.logout()

      expect(store.token).toBe('')
      expect(store.userInfo).toBeNull()
      expect(localStorage.removeItem).toHaveBeenCalledWith('token')
      expect(localStorage.removeItem).toHaveBeenCalledWith('userInfo')
    })

    it('clears local state even if API logout fails', async () => {
      userApi.login.mockResolvedValue({ token: 'token', userInfo: { id: 1, username: 'user', roles: [] } })
      await store.login({ usernameOrEmail: 'user', password: 'pass' })

      userApi.logout.mockRejectedValue(new Error('Network error'))
      await store.logout()

      expect(store.token).toBe('')
      expect(store.userInfo).toBeNull()
    })
  })

  // ==================== REGISTER ACTION ====================

  describe('register action', () => {
    it('returns true on successful registration', async () => {
      userApi.register.mockResolvedValue({ id: 1, username: 'newuser' })

      const result = await store.register({
        username: 'newuser',
        email: 'new@example.com',
        password: 'pass',
        confirmPassword: 'pass'
      })

      expect(result).toBe(true)
      expect(userApi.register).toHaveBeenCalledWith({
        username: 'newuser',
        email: 'new@example.com',
        password: 'pass',
        confirmPassword: 'pass'
      })
    })

    it('returns false on registration failure', async () => {
      userApi.register.mockRejectedValue(new Error('Username already exists'))

      const result = await store.register({
        username: 'existing',
        email: 'ex@example.com',
        password: 'pass',
        confirmPassword: 'pass'
      })

      expect(result).toBe(false)
    })
  })

  // ==================== UPDATE PROFILE ACTION ====================

  describe('updateProfile action', () => {
    it('updates userInfo state and localStorage on success', async () => {
      userApi.login.mockResolvedValue({
        token: 'token',
        userInfo: { id: 1, username: 'user', nickname: 'Old Nick', roles: [] }
      })
      await store.login({ usernameOrEmail: 'user', password: 'pass' })

      const updatedInfo = { id: 1, username: 'user', nickname: 'New Nickname', roles: [] }
      userApi.updateProfile.mockResolvedValue(updatedInfo)

      const result = await store.updateProfile({ nickname: 'New Nickname' })

      expect(result).toBe(true)
      expect(store.userInfo).toEqual(updatedInfo)
      expect(localStorage.setItem).toHaveBeenCalledWith('userInfo', JSON.stringify(updatedInfo))
    })

    it('returns false on update failure', async () => {
      userApi.updateProfile.mockRejectedValue(new Error('Update failed'))

      const result = await store.updateProfile({ nickname: 'Nick' })

      expect(result).toBe(false)
    })
  })

  // ==================== CHANGE PASSWORD ACTION ====================

  describe('changePassword action', () => {
    it('returns true on success', async () => {
      userApi.changePassword.mockResolvedValue({})

      const result = await store.changePassword({
        oldPassword: 'oldPass',
        newPassword: 'newPass',
        confirmPassword: 'newPass'
      })

      expect(result).toBe(true)
    })

    it('returns false on failure', async () => {
      userApi.changePassword.mockRejectedValue(new Error('Incorrect password'))

      const result = await store.changePassword({
        oldPassword: 'wrong',
        newPassword: 'newPass',
        confirmPassword: 'newPass'
      })

      expect(result).toBe(false)
    })
  })

  // ==================== FETCH USER PROFILE ACTION ====================

  describe('fetchUserProfile action', () => {
    it('updates userInfo from API response', async () => {
      const profileData = { id: 1, username: 'user', nickname: 'Fresh Data', roles: [] }
      userApi.getUserProfile.mockResolvedValue(profileData)

      await store.fetchUserProfile()

      expect(store.userInfo).toEqual(profileData)
      expect(localStorage.setItem).toHaveBeenCalledWith('userInfo', JSON.stringify(profileData))
    })

    it('throws on API failure', async () => {
      userApi.getUserProfile.mockRejectedValue(new Error('Unauthorized'))

      await expect(store.fetchUserProfile()).rejects.toThrow('Unauthorized')
    })
  })

  // ==================== UPDATE USER INFO ACTION ====================

  describe('updateUserInfo action', () => {
    it('merges new info into existing userInfo', async () => {
      userApi.login.mockResolvedValue({
        token: 'token',
        userInfo: { id: 1, username: 'user', bio: 'old bio', roles: [] }
      })
      await store.login({ usernameOrEmail: 'user', password: 'pass' })

      store.updateUserInfo({ bio: 'new bio', location: 'NYC' })

      expect(store.userInfo.bio).toBe('new bio')
      expect(store.userInfo.location).toBe('NYC')
      expect(store.userInfo.username).toBe('user') // unchanged
    })
  })
})
