<!-- Upload Music Page -->
<template>
  <div class="page-container">
    <el-card class="placeholder-card">
      <template #header>
        <div class="card-header">
          <span>Upload Music</span>
        </div>
      </template>
      <div class="placeholder-content">
        <el-icon :size="60" color="#67C23A">
          <Upload />
        </el-icon>
        <h2>Upload Music Page</h2>
        <p>This page is under development</p>
        <el-divider />
        <p class="description">
          This will be the upload music page where users can upload their music tracks to the platform.
        </p>
      </div>
    </el-card>
  </div>
</template>

<script setup>
import { Upload } from '@element-plus/icons-vue'
</script>

<style scoped>
.page-container {
  display: flex;
  justify-content: center;
  align-items: center;
  min-height: calc(100vh - 100px);
  padding: 20px;
}

.placeholder-card {
  max-width: 600px;
  width: 100%;
}

.card-header {
  font-size: 20px;
  font-weight: bold;
}

.placeholder-content {
  text-align: center;
  padding: 40px 20px;
}

.placeholder-content h2 {
  margin: 20px 0 10px;
  color: #303133;
}

.placeholder-content p {
  color: #909399;
  margin: 10px 0;
}

.placeholder-content .description {
  font-size: 14px;
  line-height: 1.6;
}
</style>
