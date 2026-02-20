<template>
  <div class="test-mock-data-page">
    <el-page-header @back="$router.back()" title="返回">
      <template #content>
        <span class="page-title">测试 Mock 数据</span>
      </template>
    </el-page-header>

    <el-card class="section-card">
      <template #header>
        <h2>🎵 Mock 音乐数据 ({{ mockMusic.length }} 首)</h2>
      </template>
      <el-row :gutter="20">
        <el-col :xs="24" :sm="12" :md="8" :lg="6" v-for="music in mockMusic" :key="music.id">
          <el-card :body-style="{ padding: '0px' }" shadow="hover" class="music-card">
            <img :src="music.coverImage" :alt="music.title" class="music-image" />
            <div class="music-info">
              <div class="music-title">{{ music.title }}</div>
              <div class="music-artist">{{ music.artist }}</div>
              <div class="music-stats">
                <el-icon><Headset /></el-icon> {{ music.playCount }}
                <el-icon style="margin-left: 10px"><Star /></el-icon> {{ music.likeCount }}
              </div>
              <el-button type="primary" size="small" @click="viewMusic(music.id)" style="width: 100%; margin-top: 10px">
                查看详情
              </el-button>
            </div>
          </el-card>
        </el-col>
      </el-row>
    </el-card>

    <el-card class="section-card">
      <template #header>
        <h2>📋 Mock 歌单数据 ({{ mockPlaylists.length }} 个)</h2>
      </template>
      <el-row :gutter="20">
        <el-col :xs="24" :sm="12" :md="8" v-for="playlist in mockPlaylists" :key="playlist.id">
          <el-card :body-style="{ padding: '0px' }" shadow="hover" class="playlist-card">
            <img :src="playlist.coverImage" :alt="playlist.name" class="playlist-image" />
            <div class="playlist-info">
              <div class="playlist-name">{{ playlist.name }}</div>
              <div class="playlist-description">{{ playlist.description }}</div>
              <div class="playlist-stats">
                <span>{{ playlist.musicCount }} 首歌曲</span>
                <span>{{ playlist.likeCount }} 个赞</span>
              </div>
              <el-button type="primary" size="small" @click="viewPlaylist(playlist.id)" style="width: 100%; margin-top: 10px">
                查看详情
              </el-button>
            </div>
          </el-card>
        </el-col>
      </el-row>
    </el-card>

    <el-card class="section-card">
      <template #header>
        <h2>💬 Mock 评论数据 ({{ mockComments.length }} 条)</h2>
      </template>
      <div v-for="comment in mockComments" :key="comment.id" class="comment-item">
        <el-avatar :src="comment.user.avatar" :size="40" />
        <div class="comment-content">
          <div class="comment-header">
            <strong>{{ comment.user.username }}</strong>
            <span class="comment-time">{{ formatTime(comment.createdAt) }}</span>
          </div>
          <div class="comment-text">{{ comment.content }}</div>
          <div class="comment-stats">
            <el-icon><Star /></el-icon> {{ comment.likeCount }}
            <span style="margin-left: 15px">
              <el-icon><ChatDotRound /></el-icon> {{ comment.replyCount }} 回复
            </span>
          </div>
        </div>
      </div>
    </el-card>

    <el-card class="section-card">
      <template #header>
        <h2>🎸 Mock 音乐类型 ({{ mockGenres.length }} 种)</h2>
      </template>
      <el-space wrap :size="15">
        <el-tag v-for="genre in mockGenres" :key="genre.id" size="large" type="info">
          {{ genre.name }} ({{ genre.musicCount }})
        </el-tag>
      </el-space>
    </el-card>

    <el-card class="section-card">
      <template #header>
        <h2>📖 使用说明</h2>
      </template>
      <el-alert title="测试数据说明" type="info" :closable="false">
        <p>✅ 这些是前端 Mock 数据，用于测试 UI 界面</p>
        <p>✅ 可以立即查看和测试，无需后端服务器</p>
        <p>✅ 点击"查看详情"按钮可以跳转到对应页面</p>
        <p>⚠️ 由于后端未启动，实际功能（如点赞、评论）暂不可用</p>
        <p>📝 Mock 数据位置：<code>frontend/src/mock/testData.js</code></p>
        <p>📝 SQL 数据位置：<code>backend/src/main/resources/db/migration/V6__insert_test_data.sql</code></p>
      </el-alert>
    </el-card>
  </div>
</template>

<script setup>
import { mockMusic, mockPlaylists, mockComments, mockGenres } from '@/mock/testData'
import { useRouter } from 'vue-router'
import { Headset, Star, ChatDotRound } from '@element-plus/icons-vue'

const router = useRouter()

function viewMusic(id) {
  router.push(`/music/${id}`)
}

function viewPlaylist(id) {
  router.push(`/playlist/${id}`)
}

function formatTime(time) {
  const date = new Date(time)
  const now = new Date()
  const diff = Math.floor((now - date) / 1000 / 60 / 60 / 24)
  return diff === 0 ? '今天' : `${diff} 天前`
}
</script>

<style scoped lang="scss">
.test-mock-data-page {
  padding: 20px;
  max-width: 1400px;
  margin: 0 auto;
}

.page-title {
  font-size: 18px;
  font-weight: 600;
}

.section-card {
  margin-top: 20px;

  h2 {
    margin: 0;
    font-size: 20px;
  }
}

.music-card,
.playlist-card {
  margin-bottom: 20px;
  transition: transform 0.3s;

  &:hover {
    transform: translateY(-5px);
  }
}

.music-image,
.playlist-image {
  width: 100%;
  height: 200px;
  object-fit: cover;
}

.music-info,
.playlist-info {
  padding: 14px;
}

.music-title,
.playlist-name {
  font-size: 16px;
  font-weight: 600;
  margin-bottom: 4px;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.music-artist {
  color: #909399;
  font-size: 14px;
  margin-bottom: 8px;
}

.playlist-description {
  color: #606266;
  font-size: 13px;
  margin-bottom: 10px;
  line-height: 1.4;
  height: 36px;
  overflow: hidden;
  text-overflow: ellipsis;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
}

.music-stats,
.playlist-stats {
  display: flex;
  align-items: center;
  gap: 5px;
  font-size: 13px;
  color: #909399;

  .el-icon {
    font-size: 14px;
  }
}

.playlist-stats {
  justify-content: space-between;
}

.comment-item {
  display: flex;
  gap: 12px;
  padding: 16px;
  border-bottom: 1px solid #ebeef5;

  &:last-child {
    border-bottom: none;
  }
}

.comment-content {
  flex: 1;
}

.comment-header {
  display: flex;
  justify-content: space-between;
  margin-bottom: 8px;

  strong {
    color: #303133;
  }

  .comment-time {
    color: #909399;
    font-size: 13px;
  }
}

.comment-text {
  color: #606266;
  margin-bottom: 8px;
  line-height: 1.5;
}

.comment-stats {
  display: flex;
  align-items: center;
  color: #909399;
  font-size: 13px;

  .el-icon {
    font-size: 14px;
    margin-right: 4px;
  }
}

code {
  background: #f5f7fa;
  padding: 2px 8px;
  border-radius: 4px;
  color: #409eff;
  font-family: monospace;
}
</style>
