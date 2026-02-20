# 前端项目说明文档

## 📋 项目概述

本项目是音乐共享平台的前端部分，基于 **Vue.js 3.x + Vite** 构建，提供现代化的单页面应用体验。

---

## 🛠️ 技术栈

| 技术 | 版本 | 说明 |
|------|------|------|
| **Vue.js** | 3.x | 渐进式 JavaScript 框架 |
| **Vite** | 7.x | 下一代前端构建工具 |
| **Vue Router** | 4.x | 官方路由管理器 |
| **Pinia** | 2.x | 官方状态管理库 |
| **Element Plus** | 最新 | UI 组件库 |
| **Axios** | 最新 | HTTP 客户端 |
| **SCSS** | - | CSS 预处理器 |

---

## 📁 项目结构

```
frontend/
├── public/                     # 静态资源
├── src/
│   ├── api/                    # API 接口封装
│   │   ├── request.js          # Axios 封装
│   │   ├── user.js             # 用户相关 API
│   │   └── music.js            # 音乐相关 API
│   │
│   ├── assets/                 # 资源文件
│   │   └── images/             # 图片资源
│   │
│   ├── components/             # 通用组件
│   │   ├── common/             # 公共组件
│   │   ├── music/              # 音乐相关组件
│   │   └── social/             # 社交相关组件
│   │
│   ├── composables/            # 组合式函数
│   │
│   ├── router/                 # 路由配置
│   │   └── index.js            # 路由定义
│   │
│   ├── store/                  # 状态管理
│   │   ├── user.js             # 用户状态
│   │   └── player.js           # 播放器状态
│   │
│   ├── styles/                 # 全局样式
│   │   ├── variables.scss      # SCSS 变量
│   │   └── global.scss         # 全局样式
│   │
│   ├── utils/                  # 工具函数
│   │   ├── format.js           # 格式化工具
│   │   ├── storage.js          # 本地存储工具
│   │   └── validation.js       # 验证工具
│   │
│   ├── views/                  # 页面组件
│   │   ├── Home.vue            # 首页
│   │   ├── Search.vue          # 搜索页
│   │   ├── NotFound.vue        # 404 页面
│   │   ├── user/               # 用户相关页面
│   │   ├── music/              # 音乐相关页面
│   │   ├── playlist/           # 歌单相关页面
│   │   └── admin/              # 管理后台页面
│   │
│   ├── App.vue                 # 根组件
│   └── main.js                 # 入口文件
│
├── docs/                       # 文档目录
│   ├── README.md               # 项目说明（本文件）
│   ├── COMPONENT_GUIDE.md      # 组件使用指南
│   ├── STATE_MANAGEMENT.md     # 状态管理文档
│   └── ROUTER_GUIDE.md         # 路由配置文档
│
├── .gitignore
├── package.json
├── vite.config.js              # Vite 配置
└── index.html
```

---

## 🚀 快速开始

### 安装依赖

```bash
npm install
```

### 启动开发服务器

```bash
npm run dev
```

访问：http://localhost:5173

### 构建生产版本

```bash
npm run build
```

构建产物输出到 `dist/` 目录。

### 预览生产构建

```bash
npm run preview
```

---

## 🔧 开发指南

### 代码规范

- **组件命名**：使用 PascalCase（如 `MusicPlayer.vue`）
- **文件命名**：组件使用 PascalCase，其他文件使用 camelCase
- **组合式 API**：优先使用 `<script setup>` 语法
- **样式**：使用 Scoped CSS 或 SCSS

### 组件开发

创建新组件时遵循以下结构：

```vue
<template>
  <div class="component-name">
    <!-- 模板内容 -->
  </div>
</template>

<script setup>
/**
 * Component description
 *
 * @description Detailed description of component functionality
 *
 * @author MusicShare Team
 * @version 1.0.0
 */

import { ref, computed } from 'vue'

// Props
const props = defineProps({
  /**
   * Prop description
   * @type {string}
   */
  propName: {
    type: String,
    default: ''
  }
})

// Emits
const emit = defineEmits(['eventName'])

// State and logic here
</script>

<style scoped lang="scss">
.component-name {
  /* 样式 */
}
</style>
```

### API 调用

使用封装的 API 模块：

```javascript
import { getUserProfile, updateProfile } from '@/api/user'

// GET 请求
const profile = await getUserProfile()

// POST 请求
const result = await updateProfile({ username: 'newname' })
```

### 路由导航

```javascript
import { useRouter } from 'vue-router'

const router = useRouter()

// 编程式导航
router.push('/profile')
router.push({ name: 'Profile' })
router.push({ path: '/music', query: { id: 123 } })
```

### 状态管理

```javascript
import { useUserStore } from '@/store/user'

const userStore = useUserStore()

// 访问状态
console.log(userStore.username)

// 调用方法
await userStore.login({ username, password })
```

---

## 📝 环境变量

在项目根目录创建 `.env` 文件：

```env
# API 基础地址
VITE_API_BASE_URL=http://localhost:8081/api

# 应用标题
VITE_APP_TITLE=MusicShare
```

在代码中使用：

```javascript
const apiUrl = import.meta.env.VITE_API_BASE_URL
```

---

## 🎨 样式管理

### 使用 SCSS 变量

在组件中导入变量：

```scss
<style scoped lang="scss">
@use '@/styles/variables.scss' as *;

.component {
  color: $primary-color;
  padding: $spacing-md;
}
</style>
```

### 全局样式类

```html
<div class="container">
  <div class="card">
    <h2 class="text-primary mb-md">标题</h2>
    <p class="text-secondary">内容</p>
  </div>
</div>
```

---

## 🔌 API 代理配置

开发环境下，API 请求会自动代理到后端服务：

```javascript
// vite.config.js
server: {
  proxy: {
    '/api': {
      target: 'http://localhost:8081',
      changeOrigin: true
    }
  }
}
```

---

## 📦 依赖说明

### 核心依赖

- **vue**: Vue.js 框架核心
- **vue-router**: 路由管理
- **pinia**: 状态管理
- **axios**: HTTP 请求
- **element-plus**: UI 组件库

### 开发依赖

- **vite**: 构建工具
- **@vitejs/plugin-vue**: Vue 插件
- **sass**: SCSS 编译器

---

## 🐛 常见问题

### Q: 端口被占用

A: 修改 `vite.config.js` 中的 `server.port` 配置

### Q: API 请求失败

A: 检查后端服务是否启动，以及代理配置是否正确

### Q: 样式不生效

A: 确保正确导入全局样式，检查 Scoped 样式作用域

---

## 📚 相关文档

- [组件使用指南](./COMPONENT_GUIDE.md)
- [状态管理文档](./STATE_MANAGEMENT.md)
- [路由配置文档](./ROUTER_GUIDE.md)

---

*文档版本：1.0.0*
*创建日期：2026-02-20*
*最后更新：2026-02-20*
