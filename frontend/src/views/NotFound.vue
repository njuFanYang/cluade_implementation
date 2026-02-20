<!-- 404 Not Found Page -->
<template>
  <div class="page-container">
    <el-card class="placeholder-card">
      <template #header>
        <div class="card-header">
          <span>404 - Page Not Found</span>
        </div>
      </template>
      <div class="placeholder-content">
        <el-icon :size="60" color="#F56C6C">
          <WarningFilled />
        </el-icon>
        <h2>404 - Page Not Found</h2>
        <p>The page you are looking for does not exist</p>
        <el-divider />
        <p class="description">
          The page you requested could not be found. Please check the URL or return to the home page.
        </p>
        <el-button type="primary" @click="goHome" style="margin-top: 20px">
          Go to Home
        </el-button>
      </div>
    </el-card>
  </div>
</template>

<script setup>
import { WarningFilled } from '@element-plus/icons-vue'
import { useRouter } from 'vue-router'

const router = useRouter()

const goHome = () => {
  router.push('/')
}
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
