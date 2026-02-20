<template>
  <div class="upload-page">
    <div class="page-header">
      <h1>
        <el-icon><Upload /></el-icon>
        Upload Music
      </h1>
      <p>Share your music with the world</p>
    </div>

    <el-card class="upload-card">
      <el-steps :active="currentStep" finish-status="success" align-center>
        <el-step title="Upload File" />
        <el-step title="Music Info" />
        <el-step title="Review & Submit" />
      </el-steps>

      <!-- Step 1: File Upload -->
      <div v-show="currentStep === 0" class="step-content">
        <el-upload
          ref="uploadRef"
          class="music-uploader"
          drag
          :auto-upload="false"
          :limit="1"
          :accept="acceptedFormats"
          :on-change="handleFileChange"
          :on-exceed="handleExceed"
          :before-remove="handleBeforeRemove"
        >
          <el-icon class="upload-icon"><UploadFilled /></el-icon>
          <div class="upload-text">
            <div class="primary-text">Drop music file here or click to upload</div>
            <div class="secondary-text">Supported formats: MP3, FLAC, AAC, OGG, WAV (Max 100MB)</div>
          </div>
        </el-upload>

        <div v-if="musicFile" class="file-info">
          <el-alert
            title="File selected successfully"
            type="success"
            :closable="false"
          >
            <template #default>
              <div class="file-details">
                <p><strong>File:</strong> {{ musicFile.name }}</p>
                <p><strong>Size:</strong> {{ formatFileSize(musicFile.size) }}</p>
                <p v-if="extractedMetadata.duration">
                  <strong>Duration:</strong> {{ formatDuration(extractedMetadata.duration) }}
                </p>
              </div>
            </template>
          </el-alert>
        </div>

        <div class="step-actions">
          <el-button @click="router.back()">Cancel</el-button>
          <el-button type="primary" :disabled="!musicFile" @click="nextStep">
            Next Step
          </el-button>
        </div>
      </div>

      <!-- Step 2: Music Information -->
      <div v-show="currentStep === 1" class="step-content">
        <el-form
          ref="formRef"
          :model="form"
          :rules="rules"
          label-width="120px"
          label-position="left"
        >
          <el-form-item label="Title" prop="title" required>
            <el-input
              v-model="form.title"
              placeholder="Enter music title"
              maxlength="200"
              show-word-limit
            />
          </el-form-item>

          <el-form-item label="Artist" prop="artist" required>
            <el-input
              v-model="form.artist"
              placeholder="Enter artist name"
              maxlength="100"
              show-word-limit
            />
          </el-form-item>

          <el-form-item label="Genre" prop="genreId" required>
            <el-select
              v-model="form.genreId"
              placeholder="Select genre"
              filterable
              style="width: 100%"
            >
              <el-option
                v-for="genre in musicStore.genres"
                :key="genre.id"
                :label="genre.name"
                :value="genre.id"
              />
            </el-select>
          </el-form-item>

          <el-form-item label="Album" prop="albumId">
            <el-select
              v-model="form.albumId"
              placeholder="Select album (optional)"
              clearable
              filterable
              style="width: 100%"
            >
              <el-option
                v-for="album in albums"
                :key="album.id"
                :label="`${album.title} - ${album.artist}`"
                :value="album.id"
              />
            </el-select>
          </el-form-item>

          <el-form-item label="Release Date" prop="releaseDate">
            <el-date-picker
              v-model="form.releaseDate"
              type="date"
              placeholder="Select release date"
              style="width: 100%"
            />
          </el-form-item>

          <el-form-item label="Description" prop="description">
            <el-input
              v-model="form.description"
              type="textarea"
              :rows="4"
              placeholder="Enter description (optional)"
              maxlength="1000"
              show-word-limit
            />
          </el-form-item>

          <el-form-item label="Lyrics" prop="lyrics">
            <el-input
              v-model="form.lyrics"
              type="textarea"
              :rows="6"
              placeholder="Enter lyrics (optional)"
            />
          </el-form-item>

          <el-form-item label="Cover Image" prop="coverImage">
            <el-upload
              class="cover-uploader"
              :show-file-list="false"
              :auto-upload="false"
              accept="image/*"
              :on-change="handleCoverChange"
            >
              <img v-if="coverImageUrl" :src="coverImageUrl" class="cover-preview" />
              <el-icon v-else class="cover-uploader-icon"><Plus /></el-icon>
            </el-upload>
            <div class="cover-hint">
              Recommended size: 500x500px, Max 5MB
            </div>
          </el-form-item>

          <el-form-item label="Visibility" prop="isPublic">
            <el-radio-group v-model="form.isPublic">
              <el-radio :label="true">Public - Everyone can see and play</el-radio>
              <el-radio :label="false">Private - Only you can see and play</el-radio>
            </el-radio-group>
          </el-form-item>
        </el-form>

        <div class="step-actions">
          <el-button @click="prevStep">Previous</el-button>
          <el-button type="primary" @click="nextStep">Review</el-button>
        </div>
      </div>

      <!-- Step 3: Review & Submit -->
      <div v-show="currentStep === 2" class="step-content">
        <div class="review-section">
          <h3>Review Your Upload</h3>

          <el-descriptions :column="1" border>
            <el-descriptions-item label="File">
              {{ musicFile?.name }}
            </el-descriptions-item>
            <el-descriptions-item label="Title">
              {{ form.title }}
            </el-descriptions-item>
            <el-descriptions-item label="Artist">
              {{ form.artist }}
            </el-descriptions-item>
            <el-descriptions-item label="Genre">
              {{ getGenreName(form.genreId) }}
            </el-descriptions-item>
            <el-descriptions-item label="Album" v-if="form.albumId">
              {{ getAlbumName(form.albumId) }}
            </el-descriptions-item>
            <el-descriptions-item label="Release Date" v-if="form.releaseDate">
              {{ formatDate(form.releaseDate) }}
            </el-descriptions-item>
            <el-descriptions-item label="Description" v-if="form.description">
              {{ form.description }}
            </el-descriptions-item>
            <el-descriptions-item label="Visibility">
              {{ form.isPublic ? 'Public' : 'Private' }}
            </el-descriptions-item>
          </el-descriptions>

          <div v-if="coverImageUrl" class="review-cover">
            <h4>Cover Image</h4>
            <img :src="coverImageUrl" alt="Cover" />
          </div>
        </div>

        <!-- Upload Progress -->
        <div v-if="uploading" class="upload-progress">
          <el-progress
            :percentage="uploadProgress"
            :status="uploadProgress === 100 ? 'success' : undefined"
          />
          <p class="progress-text">{{ uploadStatusText }}</p>
        </div>

        <div class="step-actions">
          <el-button @click="prevStep" :disabled="uploading">Previous</el-button>
          <el-button
            type="primary"
            @click="handleSubmit"
            :loading="uploading"
            :disabled="uploading"
          >
            {{ uploading ? 'Uploading...' : 'Upload Music' }}
          </el-button>
        </div>
      </div>
    </el-card>
  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { useMusicStore } from '@/store/music'
import { Upload, UploadFilled, Plus } from '@element-plus/icons-vue'
import { ElMessage } from 'element-plus'

const router = useRouter()
const musicStore = useMusicStore()

// State
const currentStep = ref(0)
const uploadRef = ref(null)
const formRef = ref(null)
const musicFile = ref(null)
const coverImageFile = ref(null)
const coverImageUrl = ref('')
const uploading = ref(false)
const uploadProgress = ref(0)
const uploadStatusText = ref('')
const extractedMetadata = ref({})
const albums = ref([]) // TODO: Load from API

const acceptedFormats = '.mp3,.flac,.aac,.ogg,.wav'

// Form data
const form = reactive({
  title: '',
  artist: '',
  genreId: null,
  albumId: null,
  releaseDate: null,
  description: '',
  lyrics: '',
  isPublic: true
})

// Validation rules
const rules = {
  title: [
    { required: true, message: 'Please enter music title', trigger: 'blur' },
    { min: 1, max: 200, message: 'Length should be 1 to 200', trigger: 'blur' }
  ],
  artist: [
    { required: true, message: 'Please enter artist name', trigger: 'blur' },
    { min: 1, max: 100, message: 'Length should be 1 to 100', trigger: 'blur' }
  ],
  genreId: [
    { required: true, message: 'Please select a genre', trigger: 'change' }
  ]
}

// Methods
function handleFileChange(file) {
  // Validate file size (100MB)
  const maxSize = 100 * 1024 * 1024
  if (file.size > maxSize) {
    ElMessage.error('File size cannot exceed 100MB')
    uploadRef.value.clearFiles()
    return
  }

  musicFile.value = file.raw

  // Try to extract metadata from filename
  const filename = file.name.replace(/\.[^/.]+$/, '')
  const parts = filename.split('-').map(p => p.trim())

  if (parts.length >= 2 && !form.title) {
    form.artist = parts[0]
    form.title = parts.slice(1).join(' - ')
  } else if (!form.title) {
    form.title = filename
  }

  // TODO: Extract metadata using Web Audio API or similar
  extractedMetadata.value = {}
}

function handleExceed() {
  ElMessage.warning('You can only upload one file at a time')
}

function handleBeforeRemove() {
  return new Promise((resolve) => {
    ElMessageBox.confirm('Are you sure you want to remove this file?')
      .then(() => {
        musicFile.value = null
        extractedMetadata.value = {}
        resolve(true)
      })
      .catch(() => resolve(false))
  })
}

function handleCoverChange(file) {
  // Validate file size (5MB)
  const maxSize = 5 * 1024 * 1024
  if (file.size > maxSize) {
    ElMessage.error('Cover image size cannot exceed 5MB')
    return
  }

  coverImageFile.value = file.raw
  coverImageUrl.value = URL.createObjectURL(file.raw)
}

async function nextStep() {
  if (currentStep.value === 1) {
    // Validate form before moving to review
    try {
      await formRef.value.validate()
      currentStep.value++
    } catch (error) {
      ElMessage.error('Please fill in all required fields')
    }
  } else {
    currentStep.value++
  }
}

function prevStep() {
  if (currentStep.value > 0) {
    currentStep.value--
  }
}

async function handleSubmit() {
  try {
    uploading.value = true
    uploadProgress.value = 0
    uploadStatusText.value = 'Preparing upload...'

    // Create FormData
    const formData = new FormData()
    formData.append('file', musicFile.value)
    formData.append('title', form.title)
    formData.append('artist', form.artist)
    formData.append('genreId', form.genreId)

    if (form.albumId) formData.append('albumId', form.albumId)
    if (form.releaseDate) formData.append('releaseDate', formatDateForAPI(form.releaseDate))
    if (form.description) formData.append('description', form.description)
    if (form.lyrics) formData.append('lyrics', form.lyrics)
    formData.append('isPublic', form.isPublic)

    if (coverImageFile.value) {
      formData.append('coverImage', coverImageFile.value)
    }

    // Simulate progress (actual progress tracking would require backend support)
    const progressInterval = setInterval(() => {
      if (uploadProgress.value < 90) {
        uploadProgress.value += 10
        uploadStatusText.value = `Uploading... ${uploadProgress.value}%`
      }
    }, 500)

    // Upload
    const result = await musicStore.uploadMusic(formData)

    clearInterval(progressInterval)
    uploadProgress.value = 100
    uploadStatusText.value = 'Upload completed!'

    ElMessage.success('Music uploaded successfully!')

    // Navigate to the uploaded music detail page after a short delay
    setTimeout(() => {
      router.push(`/music/${result.id}`)
    }, 1500)
  } catch (error) {
    console.error('Upload failed:', error)
    ElMessage.error(error.message || 'Upload failed')
    uploading.value = false
    uploadProgress.value = 0
  }
}

function formatFileSize(bytes) {
  if (bytes === 0) return '0 B'
  const k = 1024
  const sizes = ['B', 'KB', 'MB', 'GB']
  const i = Math.floor(Math.log(bytes) / Math.log(k))
  return Math.round(bytes / Math.pow(k, i) * 100) / 100 + ' ' + sizes[i]
}

function formatDuration(seconds) {
  const mins = Math.floor(seconds / 60)
  const secs = Math.floor(seconds % 60)
  return `${mins}:${secs.toString().padStart(2, '0')}`
}

function formatDate(date) {
  if (!date) return ''
  return new Date(date).toLocaleDateString()
}

function formatDateForAPI(date) {
  if (!date) return ''
  const d = new Date(date)
  return d.toISOString().split('T')[0]
}

function getGenreName(genreId) {
  const genre = musicStore.genres.find(g => g.id === genreId)
  return genre ? genre.name : ''
}

function getAlbumName(albumId) {
  const album = albums.value.find(a => a.id === albumId)
  return album ? `${album.title} - ${album.artist}` : ''
}

// Lifecycle
onMounted(async () => {
  // Load genres if not loaded
  if (musicStore.genres.length === 0) {
    await musicStore.fetchGenres()
  }

  // TODO: Load user's albums
  // albums.value = await loadUserAlbums()
})
</script>

<style scoped lang="scss">
.upload-page {
  padding: 20px;
  max-width: 900px;
  margin: 0 auto;
  padding-bottom: 100px;
}

.page-header {
  text-align: center;
  margin-bottom: 32px;

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

.upload-card {
  :deep(.el-card__body) {
    padding: 32px;
  }

  .el-steps {
    margin-bottom: 40px;
  }
}

.step-content {
  min-height: 400px;
}

// File Upload Styles
.music-uploader {
  :deep(.el-upload) {
    width: 100%;
  }

  :deep(.el-upload-dragger) {
    width: 100%;
    height: 240px;
    display: flex;
    flex-direction: column;
    align-items: center;
    justify-content: center;
  }

  .upload-icon {
    font-size: 80px;
    color: #409eff;
    margin-bottom: 16px;
  }

  .upload-text {
    .primary-text {
      font-size: 16px;
      color: #303133;
      margin-bottom: 8px;
    }

    .secondary-text {
      font-size: 13px;
      color: #909399;
    }
  }
}

.file-info {
  margin-top: 20px;

  .file-details {
    p {
      margin: 8px 0;
      font-size: 14px;
    }
  }
}

// Cover Upload Styles
.cover-uploader {
  :deep(.el-upload) {
    border: 1px dashed #d9d9d9;
    border-radius: 6px;
    cursor: pointer;
    position: relative;
    overflow: hidden;
    transition: all 0.3s;

    &:hover {
      border-color: #409eff;
    }
  }

  .cover-preview {
    width: 178px;
    height: 178px;
    display: block;
    object-fit: cover;
  }

  .cover-uploader-icon {
    font-size: 28px;
    color: #8c939d;
    width: 178px;
    height: 178px;
    text-align: center;
    line-height: 178px;
  }
}

.cover-hint {
  font-size: 12px;
  color: #909399;
  margin-top: 8px;
}

// Review Section
.review-section {
  h3 {
    font-size: 18px;
    font-weight: 600;
    color: #303133;
    margin: 0 0 20px 0;
  }

  .el-descriptions {
    margin-bottom: 20px;
  }

  .review-cover {
    h4 {
      font-size: 14px;
      font-weight: 500;
      color: #606266;
      margin: 20px 0 12px 0;
    }

    img {
      max-width: 200px;
      border-radius: 8px;
      box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
    }
  }
}

// Upload Progress
.upload-progress {
  margin: 24px 0;
  padding: 20px;
  background: #f5f7fa;
  border-radius: 8px;

  .el-progress {
    margin-bottom: 12px;
  }

  .progress-text {
    text-align: center;
    font-size: 14px;
    color: #606266;
    margin: 0;
  }
}

// Step Actions
.step-actions {
  display: flex;
  justify-content: center;
  gap: 12px;
  margin-top: 32px;
  padding-top: 24px;
  border-top: 1px solid #ebeef5;
}

// Responsive
@media (max-width: 768px) {
  .upload-page {
    padding: 12px;
  }

  .page-header h1 {
    font-size: 24px;
  }

  .upload-card :deep(.el-card__body) {
    padding: 20px;
  }

  .el-form {
    :deep(.el-form-item__label) {
      text-align: left;
    }
  }
}
</style>
