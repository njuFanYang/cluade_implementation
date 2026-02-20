/**
 * Validation utility functions
 *
 * @description Provides validation functions for forms and user input
 *
 * @author MusicShare Team
 * @version 1.0.0
 */

/**
 * Validate email format
 *
 * @param {string} email - Email address to validate
 * @returns {boolean} True if valid email format
 */
export function validateEmail(email) {
  const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/
  return emailRegex.test(email)
}

/**
 * Validate password strength
 * Requirements: At least 8 characters, contains letters and numbers
 *
 * @param {string} password - Password to validate
 * @returns {Object} Validation result with isValid and message
 */
export function validatePassword(password) {
  if (!password || password.length < 8) {
    return {
      isValid: false,
      message: 'Password must be at least 8 characters long'
    }
  }

  const hasLetter = /[a-zA-Z]/.test(password)
  const hasNumber = /\d/.test(password)

  if (!hasLetter || !hasNumber) {
    return {
      isValid: false,
      message: 'Password must contain both letters and numbers'
    }
  }

  return {
    isValid: true,
    message: 'Password is valid'
  }
}

/**
 * Validate username format
 * Requirements: 3-20 characters, alphanumeric and underscores only
 *
 * @param {string} username - Username to validate
 * @returns {Object} Validation result
 */
export function validateUsername(username) {
  if (!username || username.length < 3 || username.length > 20) {
    return {
      isValid: false,
      message: 'Username must be 3-20 characters long'
    }
  }

  const usernameRegex = /^[a-zA-Z0-9_]+$/
  if (!usernameRegex.test(username)) {
    return {
      isValid: false,
      message: 'Username can only contain letters, numbers, and underscores'
    }
  }

  return {
    isValid: true,
    message: 'Username is valid'
  }
}

/**
 * Validate file type
 *
 * @param {File} file - File to validate
 * @param {Array<string>} allowedTypes - Allowed MIME types
 * @returns {boolean} True if file type is allowed
 */
export function validateFileType(file, allowedTypes) {
  if (!file) return false
  return allowedTypes.includes(file.type)
}

/**
 * Validate file size
 *
 * @param {File} file - File to validate
 * @param {number} maxSizeMB - Maximum size in MB
 * @returns {boolean} True if file size is within limit
 */
export function validateFileSize(file, maxSizeMB) {
  if (!file) return false
  const maxSizeBytes = maxSizeMB * 1024 * 1024
  return file.size <= maxSizeBytes
}

/**
 * Validate URL format
 *
 * @param {string} url - URL to validate
 * @returns {boolean} True if valid URL format
 */
export function validateURL(url) {
  try {
    new URL(url)
    return true
  } catch {
    return false
  }
}
