<template>
  <div class="music-browse">
    <!-- Header -->
    <div class="page-header">
      <h1>Music Library</h1>
      <el-button
        v-if="userStore.hasRole('MUSICIAN') || userStore.hasRole('ADMIN')"
        type="primary"
        :icon="Upload"
        @click="router.push('/music/upload')"
      >
        Upload Music
      </el-button>
    </div>

    <!-- Search and Filters -->
    <div class="filter-section">
      <div class="filter-row">
        <el-input
          v-model="searchKeyword"
          placeholder="Search by title or artist..."
          :prefix-icon="Search"
          clearable
          @clear="handleSearch"
          @keyup.enter="handleSearch"
          class="search-input"
        >
          <template #append>
            <el-button :icon="Search" @click="handleSearch" />
          </template>
        </el-input>

        <el-select
          v-model="selectedGenre"
          placeholder="All Genres"
          clearable
          @change="handleFilterChange"
          class="filter-select"
        >
          <el-option label="All Genres" :value="null" />
          <el-option
            v-for="genre in musicStore.genres"
            :key="genre.id"
            :label="genre.name"
            :value="genre.id"
          />
        </el-select>

        <el-select
          v-model="sortBy"
          @change="handleFilterChange"
          class="filter-select"
        >
          <el-option label="Latest" value="latest" />
          <el-option label="Most Popular" value="popular" />
          <el-option label="Title A-Z" value="title_asc" />
          <el-option label="Title Z-A" value="title_desc" />
        </el-select>

        <el-radio-group v-model="viewMode" class="view-mode-toggle">
          <el-radio-button value="card">
            <el-icon><Grid /></el-icon>
          </el-radio-button>
          <el-radio-button value="list">
            <el-icon><List /></el-icon>
          </el-radio-button>
        </el-radio-group>
      </div>
    </div>

    <!-- Popular Music Section (only show when not filtering) -->
    <div v-if="!searchKeyword && !selectedGenre" class="section popular-section">
      <div class="section-header">
        <h2>
          <el-icon><TrendCharts /></el-icon>
          Popular Music
        </h2>
      </div>
      <div v-loading="loadingPopular" class="music-grid">
        <MusicCard
          v-for="music in popularMusic"
          :key="'popular-' + music.id"
          :music="music"
          mode="card"
          @play="handlePlay"
        />
        <el-empty
          v-if="!loadingPopular && popularMusic.length === 0"
          description="No popular music found"
        />
      </div>
    </div>

    <!-- All Music Section -->
    <div class="section all-music-section">
      <div class="section-header">
        <h2>
          <el-icon><Headset /></el-icon>
          {{ searchKeyword ? `Search Results for "${searchKeyword}"` : 'All Music' }}
        </h2>
        <span class="result-count">{{ musicStore.pagination.total }} tracks</span>
      </div>

      <div
        v-loading="loading"
        :class="viewMode === 'card' ? 'music-grid' : 'music-list'"
      >
        <MusicCard
          v-for="music in musicStore.musicList"
          :key="music.id"
          :music="music"
          :mode="viewMode"
          @play="handlePlay"
          @delete="handleDelete"
        />
        <el-empty
          v-if="!loading && musicStore.musicList.length === 0"
          description="No music found"
        />
      </div>

      <!-- Pagination -->
      <div v-if="musicStore.pagination.total > 0" class="pagination-wrapper">
        <el-pagination
          v-model:current-page="currentPage"
          v-model:page-size="pageSize"
          :total="musicStore.pagination.total"
          :page-sizes="[12, 24, 48, 96]"
          layout="total, sizes, prev, pager, next, jumper"
          @current-change="handlePageChange"
          @size-change="handlePageSizeChange"
        />
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, watch } from 'vue'
import { useRouter } from 'vue-router'
import { useMusicStore } from '@/store/music'
import { usePlayerStore } from '@/store/player'
import { useUserStore } from '@/store/user'
import MusicCard from '@/components/Music/MusicCard.vue'
import {
  Search,
  Upload,
  Grid,
  List,
  TrendCharts,
  Headset
} from '@element-plus/icons-vue'
import { ElMessage } from 'element-plus'

const router = useRouter()
const musicStore = useMusicStore()
const playerStore = usePlayerStore()
const userStore = useUserStore()

// State
const loading = ref(false)
const loadingPopular = ref(false)
const searchKeyword = ref('')
const selectedGenre = ref(null)
const sortBy = ref('latest')
const viewMode = ref(localStorage.getItem('musicViewMode') || 'card')
const currentPage = ref(1)
const pageSize = ref(24)
const popularMusic = ref([])

// Watch view mode and save to localStorage
watch(viewMode, (newMode) => {
  localStorage.setItem('musicViewMode', newMode)
})

// Methods
async function loadMusic() {
  try {
    loading.value = true
    const params = {
      page: currentPage.value - 1,
      size: pageSize.value,
      sort: getSortParam()
    }

    if (searchKeyword.value) {
      await musicStore.searchMusic(searchKeyword.value, params)
    } else if (selectedGenre.value) {
      await musicStore.fetchMusicByGenre(selectedGenre.value, params)
    } else {
      if (sortBy.value === 'popular') {
        await musicStore.fetchPopularMusic(params)
      } else {
        await musicStore.fetchAllMusic(params)
      }
    }
  } catch (error) {
    console.error('Failed to load music:', error)
    ElMessage.error('Failed to load music')
  } finally {
    loading.value = false
  }
}

async function loadPopularMusic() {
  try {
    loadingPopular.value = true
    const response = await musicStore.fetchPopularMusic({ page: 0, size: 6 })
    if (response && response.content) {
      popularMusic.value = response.content
    }
  } catch (error) {
    console.error('Failed to load popular music:', error)
  } finally {
    loadingPopular.value = false
  }
}

function getSortParam() {
  switch (sortBy.value) {
    case 'latest':
      return 'createdAt,desc'
    case 'popular':
      return 'playCount,desc'
    case 'title_asc':
      return 'title,asc'
    case 'title_desc':
      return 'title,desc'
    default:
      return 'createdAt,desc'
  }
}

function handleSearch() {
  currentPage.value = 1
  loadMusic()
}

function handleFilterChange() {
  currentPage.value = 1
  loadMusic()
}

function handlePageChange(page) {
  currentPage.value = page
  loadMusic()
  window.scrollTo({ top: 0, behavior: 'smooth' })
}

function handlePageSizeChange(size) {
  pageSize.value = size
  currentPage.value = 1
  loadMusic()
}

function handlePlay(music) {
  // Play with current page as playlist
  playerStore.play(music, musicStore.musicList)
}

function handleDelete(music) {
  // Music is already deleted in MusicCard component
  // Just reload the current page
  loadMusic()
}

// Lifecycle
onMounted(async () => {
  // Load genres if not loaded
  if (musicStore.genres.length === 0) {
    await musicStore.fetchGenres()
  }

  // Load popular music
  loadPopularMusic()

  // Load all music
  loadMusic()
})
</script>

<style scoped lang="scss">
.music-browse {
  padding: 20px;
  max-width: 1400px;
  margin: 0 auto;
}

.page-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 24px;

  h1 {
    font-size: 28px;
    font-weight: 600;
    color: #303133;
    margin: 0;
  }
}

.filter-section {
  background: white;
  border-radius: 8px;
  padding: 20px;
  margin-bottom: 24px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);

  .filter-row {
    display: flex;
    gap: 12px;
    align-items: center;

    .search-input {
      flex: 1;
      max-width: 400px;
    }

    .filter-select {
      width: 180px;
    }

    .view-mode-toggle {
      margin-left: auto;
    }
  }
}

.section {
  margin-bottom: 32px;

  .section-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
    margin-bottom: 16px;

    h2 {
      font-size: 20px;
      font-weight: 600;
      color: #303133;
      display: flex;
      align-items: center;
      gap: 8px;
      margin: 0;

      .el-icon {
        color: #409eff;
      }
    }

    .result-count {
      font-size: 14px;
      color: #909399;
    }
  }
}

.popular-section {
  .music-grid {
    display: grid;
    grid-template-columns: repeat(6, 1fr);
    gap: 16px;
  }
}

.all-music-section {
  .music-grid {
    display: grid;
    grid-template-columns: repeat(4, 1fr);
    gap: 20px;
  }

  .music-list {
    display: flex;
    flex-direction: column;
    gap: 8px;
  }
}

.pagination-wrapper {
  display: flex;
  justify-content: center;
  margin-top: 32px;
}

// Responsive
@media (max-width: 1200px) {
  .popular-section .music-grid {
    grid-template-columns: repeat(4, 1fr);
  }

  .all-music-section .music-grid {
    grid-template-columns: repeat(3, 1fr);
  }
}

@media (max-width: 992px) {
  .popular-section .music-grid {
    grid-template-columns: repeat(3, 1fr);
  }

  .all-music-section .music-grid {
    grid-template-columns: repeat(3, 1fr);
  }
}

@media (max-width: 768px) {
  .music-browse {
    padding: 12px;
  }

  .page-header {
    h1 {
      font-size: 22px;
    }
  }

  .filter-section {
    .filter-row {
      flex-wrap: wrap;

      .search-input {
        flex: 1 1 100%;
        max-width: none;
      }

      .filter-select {
        flex: 1;
      }
    }
  }

  .popular-section .music-grid,
  .all-music-section .music-grid {
    grid-template-columns: repeat(2, 1fr);
    gap: 12px;
  }
}

@media (max-width: 480px) {
  .popular-section .music-grid,
  .all-music-section .music-grid {
    grid-template-columns: 1fr;
  }
}
</style>
