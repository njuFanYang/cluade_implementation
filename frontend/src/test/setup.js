import { vi } from 'vitest'

// Mock localStorage
const localStorageMock = {
  store: {},
  getItem: vi.fn((key) => localStorageMock.store[key] || null),
  setItem: vi.fn((key, value) => { localStorageMock.store[key] = String(value) }),
  removeItem: vi.fn((key) => { delete localStorageMock.store[key] }),
  clear: vi.fn(() => { localStorageMock.store = {} }),
}
Object.defineProperty(global, 'localStorage', {
  value: localStorageMock,
  writable: true,
})

// Mock ElMessage from element-plus
vi.mock('element-plus', () => ({
  ElMessage: {
    success: vi.fn(),
    error: vi.fn(),
    warning: vi.fn(),
    info: vi.fn(),
  },
  ElMessageBox: {
    confirm: vi.fn().mockResolvedValue('confirm'),
  },
}))
