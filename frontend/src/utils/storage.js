/**
 * LocalStorage utility functions
 *
 * @description Provides wrapper functions for localStorage with JSON support
 *
 * @author MusicShare Team
 * @version 1.0.0
 */

/**
 * Get item from localStorage
 *
 * @param {string} key - Storage key
 * @param {*} defaultValue - Default value if key doesn't exist
 * @returns {*} Stored value or default value
 */
export function getItem(key, defaultValue = null) {
  try {
    const value = localStorage.getItem(key)
    return value ? JSON.parse(value) : defaultValue
  } catch (error) {
    console.error(`Error getting item from localStorage: ${key}`, error)
    return defaultValue
  }
}

/**
 * Set item in localStorage
 *
 * @param {string} key - Storage key
 * @param {*} value - Value to store (will be JSON stringified)
 * @returns {boolean} Success status
 */
export function setItem(key, value) {
  try {
    localStorage.setItem(key, JSON.stringify(value))
    return true
  } catch (error) {
    console.error(`Error setting item in localStorage: ${key}`, error)
    return false
  }
}

/**
 * Remove item from localStorage
 *
 * @param {string} key - Storage key
 * @returns {boolean} Success status
 */
export function removeItem(key) {
  try {
    localStorage.removeItem(key)
    return true
  } catch (error) {
    console.error(`Error removing item from localStorage: ${key}`, error)
    return false
  }
}

/**
 * Clear all items from localStorage
 *
 * @returns {boolean} Success status
 */
export function clear() {
  try {
    localStorage.clear()
    return true
  } catch (error) {
    console.error('Error clearing localStorage', error)
    return false
  }
}

/**
 * Check if key exists in localStorage
 *
 * @param {string} key - Storage key
 * @returns {boolean} True if key exists
 */
export function hasItem(key) {
  return localStorage.getItem(key) !== null
}
