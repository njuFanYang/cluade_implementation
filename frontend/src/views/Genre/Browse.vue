<template>
  <div class="genre-browse">
    <!-- Page Header -->
    <div class="page-header">
      <h1>
        <el-icon><Discount /></el-icon>
        Music Genres
      </h1>
      <p>Explore music by genre</p>
    </div>

    <!-- Genres Grid -->
    <div v-loading="loading" class="genres-grid">
      <div
        v-for="genre in genres"
        :key="genre.id"
        class="genre-card"
        @click="router.push(`/genres/${genre.id}`)"
      >
        <div class="genre-icon">
          <el-icon :size="48">
            <Headset />
          </el-icon>
        </div>
        <div class="genre-info">
          <div class="genre-name">{{ genre.name }}</div>
          <div class="genre-name-zh" v-if="genre.nameZh">{{ genre.nameZh }}</div>
          <div class="genre-count">
            {{ genre.musicCount || 0 }} tracks
          </div>
        </div>
      </div>

      <el-empty
        v-if="!loading && genres.length === 0"
        description="No genres available"
        :image-size="200"
      />
    </div>

    <!-- Selected Genre Music List -->
    <el-dialog
      v-model="musicDialogVisible"
      :title="selectedGenre?.name"
      width="80%"
      top="5vh"
    >
      <div v-loading="loadingMusic" class="genre-music-list">
        <div class="music-grid">
          <MusicCard
            v-for="music in genreMusic"
            :key="music.id"
            :music="music"
            mode="card"
            @play="handlePlay"
          />
        </div>

        <div v-if="genreMusicPagination.total > 0" class="pagination-wrapper">
          <el-pagination
            v-model:current-page="genreMusicPage"
            v-model:page-size="genreMusicPageSize"
            :total="genreMusicPagination.total"
            :page-sizes="[12, 24, 48]"
            layout="total, prev, pager, next"
            @current-change="handleGenreMusicPageChange"
            @size-change="handleGenreMusicPageSizeChange"
          />
        </div>

        <el-empty
          v-if="!loadingMusic && genreMusic.length === 0"
          description="No music in this genre yet"
          :image-size="150"
        />
      </div>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { useMusicStore } from '@/store/music'
import { usePlayerStore } from '@/store/player'
import MusicCard from '@/components/Music/MusicCard.vue'
import {
  Discount,
  Headset
} from '@element-plus/icons-vue'
import { ElMessage } from 'element-plus'

const router = useRouter()
const musicStore = useMusicStore()
const playerStore = usePlayerStore()

// State
const loading = ref(false)
const genres = ref([])

// Genre music dialog
const musicDialogVisible = ref(false)
const loadingMusic = ref(false)
const selectedGenre = ref(null)
const genreMusic = ref([])
const genreMusicPage = ref(1)
const genreMusicPageSize = ref(24)
const genreMusicPagination = ref({
  total: 0,
  totalPages: 0
})

// Methods
async function loadGenres() {
  try {
    loading.value = true

    // Try to get popular genres first (sorted by music count)
    let response = await musicStore.fetchPopularGenres()

    if (!response || response.length === 0) {
      // Fallback to all genres
      response = await musicStore.fetchGenres()
    }

    genres.value = response || []
  } catch (error) {
    console.error('Failed to load genres:', error)
    ElMessage.error('Failed to load genres')
  } finally {
    loading.value = false
  }
}

async function loadGenreMusic(genreId) {
  try {
    loadingMusic.value = true
    const params = {
      page: genreMusicPage.value - 1,
      size: genreMusicPageSize.value,
      sort: 'playCount,desc'
    }

    const response = await musicStore.fetchMusicByGenre(genreId, params)
    genreMusic.value = response.content || []
    genreMusicPagination.value = {
      total: response.totalElements || 0,
      totalPages: response.totalPages || 0
    }
  } catch (error) {
    console.error('Failed to load genre music:', error)
    ElMessage.error('Failed to load music')
  } finally {
    loadingMusic.value = false
  }
}

function handleGenreMusicPageChange(page) {
  genreMusicPage.value = page
  if (selectedGenre.value) {
    loadGenreMusic(selectedGenre.value.id)
  }
}

function handleGenreMusicPageSizeChange(size) {
  genreMusicPageSize.value = size
  genreMusicPage.value = 1
  if (selectedGenre.value) {
    loadGenreMusic(selectedGenre.value.id)
  }
}

function handlePlay(music) {
  playerStore.play(music, genreMusic.value)
}

// Lifecycle
onMounted(async () => {
  // Load from store if already loaded
  if (musicStore.genres.length > 0) {
    genres.value = musicStore.genres
  }

  // Always refresh
  await loadGenres()
})
</script>

<style scoped lang="scss">
.genre-browse {
  padding: 20px;
  max-width: 1400px;
  margin: 0 auto;
  padding-bottom: 100px;
}

.page-header {
  text-align: center;
  margin-bottom: 48px;

  h1 {
    font-size: 32px;
    font-weight: 600;
    color: #303133;
    margin: 0 0 8px 0;
    display: flex;
    align-items: center;
    justify-content: center;
    gap: 12px;

    .el-icon {
      color: #409eff;
    }
  }

  p {
    font-size: 16px;
    color: #909399;
    margin: 0;
  }
}

.genres-grid {
  display: grid;
  grid-template-columns: repeat(6, 1fr);
  gap: 24px;
  min-height: 400px;
}

.genre-card {
  background: white;
  border-radius: 12px;
  padding: 32px 24px;
  text-align: center;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
  cursor: pointer;
  transition: all 0.3s ease;

  &:hover {
    transform: translateY(-8px);
    box-shadow: 0 8px 24px rgba(0, 0, 0, 0.15);

    .genre-icon {
      background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);

      .el-icon {
        color: white;
      }
    }
  }

  .genre-icon {
    width: 80px;
    height: 80px;
    margin: 0 auto 20px;
    background: #f5f7fa;
    border-radius: 50%;
    display: flex;
    align-items: center;
    justify-content: center;
    transition: all 0.3s ease;

    .el-icon {
      color: #409eff;
      transition: color 0.3s ease;
    }
  }

  .genre-info {
    .genre-name {
      font-size: 18px;
      font-weight: 600;
      color: #303133;
      margin-bottom: 4px;
    }

    .genre-name-zh {
      font-size: 14px;
      color: #909399;
      margin-bottom: 12px;
    }

    .genre-count {
      font-size: 13px;
      color: #606266;
    }
  }
}

// Genre Music Dialog
.genre-music-list {
  .music-grid {
    display: grid;
    grid-template-columns: repeat(4, 1fr);
    gap: 20px;
    margin-bottom: 24px;
  }

  .pagination-wrapper {
    display: flex;
    justify-content: center;
  }
}

// Responsive
@media (max-width: 1200px) {
  .genres-grid {
    grid-template-columns: repeat(4, 1fr);
  }

  .genre-music-list .music-grid {
    grid-template-columns: repeat(3, 1fr);
  }
}

@media (max-width: 992px) {
  .genres-grid {
    grid-template-columns: repeat(3, 1fr);
  }

  .genre-music-list .music-grid {
    grid-template-columns: repeat(2, 1fr);
  }
}

@media (max-width: 768px) {
  .genre-browse {
    padding: 12px;
  }

  .page-header {
    h1 {
      font-size: 24px;
    }
  }

  .genres-grid {
    grid-template-columns: repeat(2, 1fr);
    gap: 16px;
  }

  .genre-card {
    padding: 24px 16px;

    .genre-icon {
      width: 60px;
      height: 60px;
      margin-bottom: 16px;

      .el-icon {
        font-size: 32px;
      }
    }

    .genre-info {
      .genre-name {
        font-size: 16px;
      }
    }
  }

  .genre-music-list .music-grid {
    grid-template-columns: 1fr;
  }
}
</style>
