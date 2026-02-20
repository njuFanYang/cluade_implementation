/**
 * Music API module
 *
 * @description Provides API methods for music-related operations including
 *              upload, playback, search, and playlist management.
 *
 * @author MusicShare Team
 * @version 1.0.0
 */

import request from './request'

// Music APIs will be implemented in Phase 3.2
// Placeholder for future implementation

export function getMusicList(params) {
  return request({
    url: '/music/list',
    method: 'get',
    params
  })
}

export function getMusicById(id) {
  return request({
    url: `/music/${id}`,
    method: 'get'
  })
}

export function uploadMusic(formData) {
  return request({
    url: '/music/upload',
    method: 'post',
    data: formData,
    headers: {
      'Content-Type': 'multipart/form-data'
    }
  })
}

export function searchMusic(keyword) {
  return request({
    url: '/music/search',
    method: 'get',
    params: { keyword }
  })
}
