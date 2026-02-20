/**
 * Format utility functions
 *
 * @description Provides formatting functions for dates, numbers, time duration, etc.
 *
 * @author MusicShare Team
 * @version 1.0.0
 */

/**
 * Format date to string
 *
 * @param {Date|string|number} date - Date object, timestamp, or date string
 * @param {string} format - Format pattern (default: 'YYYY-MM-DD HH:mm:ss')
 * @returns {string} Formatted date string
 */
export function formatDate(date, format = 'YYYY-MM-DD HH:mm:ss') {
  if (!date) return ''

  const d = new Date(date)
  if (isNaN(d.getTime())) return ''

  const year = d.getFullYear()
  const month = String(d.getMonth() + 1).padStart(2, '0')
  const day = String(d.getDate()).padStart(2, '0')
  const hour = String(d.getHours()).padStart(2, '0')
  const minute = String(d.getMinutes()).padStart(2, '0')
  const second = String(d.getSeconds()).padStart(2, '0')

  return format
    .replace('YYYY', year)
    .replace('MM', month)
    .replace('DD', day)
    .replace('HH', hour)
    .replace('mm', minute)
    .replace('ss', second)
}

/**
 * Format time duration (seconds to MM:SS or HH:MM:SS)
 *
 * @param {number} seconds - Duration in seconds
 * @returns {string} Formatted duration string
 */
export function formatDuration(seconds) {
  if (!seconds || seconds < 0) return '00:00'

  const hours = Math.floor(seconds / 3600)
  const minutes = Math.floor((seconds % 3600) / 60)
  const secs = Math.floor(seconds % 60)

  if (hours > 0) {
    return `${String(hours).padStart(2, '0')}:${String(minutes).padStart(2, '0')}:${String(secs).padStart(2, '0')}`
  }

  return `${String(minutes).padStart(2, '0')}:${String(secs).padStart(2, '0')}`
}

/**
 * Format file size
 *
 * @param {number} bytes - File size in bytes
 * @returns {string} Formatted size string (e.g., '1.5 MB')
 */
export function formatFileSize(bytes) {
  if (!bytes || bytes === 0) return '0 B'

  const units = ['B', 'KB', 'MB', 'GB', 'TB']
  const k = 1024
  const i = Math.floor(Math.log(bytes) / Math.log(k))

  return `${(bytes / Math.pow(k, i)).toFixed(2)} ${units[i]}`
}

/**
 * Format large numbers (e.g., 1000 -> 1K, 1000000 -> 1M)
 *
 * @param {number} num - Number to format
 * @returns {string} Formatted number string
 */
export function formatNumber(num) {
  if (!num || num === 0) return '0'

  if (num < 1000) return String(num)
  if (num < 1000000) return `${(num / 1000).toFixed(1)}K`
  if (num < 1000000000) return `${(num / 1000000).toFixed(1)}M`

  return `${(num / 1000000000).toFixed(1)}B`
}

/**
 * Format relative time (e.g., 'just now', '5 minutes ago', '2 hours ago')
 *
 * @param {Date|string|number} date - Date to compare
 * @returns {string} Relative time string
 */
export function formatRelativeTime(date) {
  if (!date) return ''

  const d = new Date(date)
  if (isNaN(d.getTime())) return ''

  const now = new Date()
  const diff = Math.floor((now - d) / 1000) // seconds

  if (diff < 60) return 'just now'
  if (diff < 3600) return `${Math.floor(diff / 60)} minutes ago`
  if (diff < 86400) return `${Math.floor(diff / 3600)} hours ago`
  if (diff < 2592000) return `${Math.floor(diff / 86400)} days ago`
  if (diff < 31536000) return `${Math.floor(diff / 2592000)} months ago`

  return `${Math.floor(diff / 31536000)} years ago`
}

/**
 * Truncate text with ellipsis
 *
 * @param {string} text - Text to truncate
 * @param {number} maxLength - Maximum length
 * @returns {string} Truncated text
 */
export function truncateText(text, maxLength = 50) {
  if (!text) return ''
  if (text.length <= maxLength) return text

  return `${text.substring(0, maxLength)}...`
}
