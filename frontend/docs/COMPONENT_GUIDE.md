# 组件使用指南

## 📋 概述

本文档详细说明 MusicShare 前端项目中各个组件的使用方法、API 和示例。

---

## 🎯 用户模块组件

### 1. Login.vue - 登录页面

**路径**: `src/views/user/Login.vue`

**功能描述**:
- 用户登录表单
- 支持用户名或邮箱登录
- Remember Me 功能
- 表单验证
- 登录后自动跳转

**Props**: 无

**使用示例**:
```vue
<template>
  <router-link to="/login">Login</router-link>
</template>
```

**关键功能**:
- 用户名/邮箱输入
- 密码输入（可显示/隐藏）
- Remember Me 复选框
- 表单验证
- Loading 状态
- 登录成功后跳转到来源页面或首页

---

### 2. Register.vue - 注册页面

**路径**: `src/views/user/Register.vue`

**功能描述**:
- 用户注册表单
- 实时表单验证
- 密码强度检查
- 注册成功后跳转登录

**Props**: 无

**使用示例**:
```vue
<template>
  <router-link to="/register">Register</router-link>
</template>
```

**关键功能**:
- 用户名输入（3-20字符，字母数字下划线）
- 邮箱输入
- 密码输入（至少8位，包含字母和数字）
- 确认密码
- 昵称（可选）
- 实时验证
- Loading 状态

---

### 3. Profile.vue - 个人中心

**路径**: `src/views/user/Profile.vue`

**功能描述**:
- 显示用户个人信息
- 统计数据展示
- 选项卡内容（概览、我的音乐、我的歌单）
- 编辑入口

**Props**: 无

**使用示例**:
```vue
<template>
  <router-link to="/profile">My Profile</router-link>
</template>
```

**关键功能**:
- 头像显示
- 用户基本信息
- 统计卡片（粉丝、关注、音乐、歌单）
- 选项卡切换
- 编辑个人资料按钮
- Loading 骨架屏

---

### 4. Settings.vue - 设置页面

**路径**: `src/views/user/Settings.vue`

**功能描述**:
- 个人资料编辑
- 密码修改
- 选项卡布局

**Props**: 无

**使用示例**:
```vue
<template>
  <router-link to="/settings">Settings</router-link>
</template>
```

**关键功能**:
- **资料设置选项卡**:
  - 昵称
  - 个人简介
  - 性别
  - 手机号
  - 地区

- **修改密码选项卡**:
  - 旧密码
  - 新密码
  - 确认新密码

---

## 🎨 通用组件

### Header Component（待实现）

**路径**: `src/components/common/Header.vue`

**功能描述**:
- 全局导航栏
- 搜索框
- 用户菜单

**Props**:
```typescript
interface Props {
  transparent?: boolean  // 是否透明背景
}
```

**使用示例**:
```vue
<template>
  <Header :transparent="true" />
</template>
```

---

### Footer Component（待实现）

**路径**: `src/components/common/Footer.vue`

**功能描述**:
- 全局页脚
- 版权信息
- 链接

---

## 🎵 音乐模块组件（待实现）

### MusicPlayer.vue - 音乐播放器

**路径**: `src/components/music/MusicPlayer.vue`

**功能描述**:
- 音乐播放控制
- 进度条
- 音量控制
- 播放列表

**Props**:
```typescript
interface Props {
  playlist?: Array<Music>  // 播放列表
  autoplay?: boolean       // 自动播放
}
```

**Events**:
- `ended` - 播放结束
- `statusChange` - 播放状态变化

---

### MusicCard.vue - 歌曲卡片

**路径**: `src/components/music/MusicCard.vue`

**功能描述**:
- 显示歌曲信息
- 播放按钮
- 操作菜单

**Props**:
```typescript
interface Props {
  music: Music     // 歌曲对象
  showActions?: boolean  // 是否显示操作按钮
}
```

**Events**:
- `play` - 播放歌曲
- `like` - 点赞歌曲

---

## 💬 社交模块组件（待实现）

### CommentList.vue - 评论列表

**路径**: `src/components/social/CommentList.vue`

**功能描述**:
- 显示评论列表
- 支持回复
- 点赞评论

**Props**:
```typescript
interface Props {
  targetId: number    // 目标ID（歌曲、歌单等）
  targetType: string  // 目标类型
}
```

---

### CommentInput.vue - 评论输入框

**路径**: `src/components/social/CommentInput.vue`

**功能描述**:
- 发表评论
- @ 提及用户
- Emoji 支持

**Props**:
```typescript
interface Props {
  placeholder?: string  // 占位文本
  maxLength?: number    // 最大长度
}
```

**Events**:
- `submit` - 提交评论

---

## 🎨 组件开发规范

### 文件命名

- 组件文件使用 **PascalCase**：`MusicPlayer.vue`
- 通用组件放在 `components/common/`
- 业务组件按模块分类：`components/music/`, `components/social/`

### 组件结构

```vue
<template>
  <!-- 模板内容 -->
</template>

<script setup>
/**
 * Component Name
 *
 * @description Component description
 *
 * @author MusicShare Team
 * @version 1.0.0
 */

// Imports
import { ref, computed } from 'vue'

// Props
const props = defineProps({
  /**
   * Prop description
   * @type {Type}
   */
  propName: {
    type: String,
    default: ''
  }
})

// Emits
const emit = defineEmits(['eventName'])

// State and logic
</script>

<style scoped lang="scss">
/* 组件样式 */
</style>
```

### Props 定义

```javascript
const props = defineProps({
  /**
   * Prop description
   * @type {string}
   */
  title: {
    type: String,
    required: true
  },
  /**
   * Optional prop
   * @type {boolean}
   */
  visible: {
    type: Boolean,
    default: false
  }
})
```

### Events 定义

```javascript
const emit = defineEmits([
  /**
   * Emitted when button is clicked
   * @param {string} value - The button value
   */
  'click',
  /**
   * Emitted when value changes
   * @param {any} newValue - The new value
   */
  'change'
])

// 使用
emit('click', 'value')
emit('change', newValue)
```

---

## 🔌 Store 使用

### User Store

```javascript
import { useUserStore } from '@/store/user'

const userStore = useUserStore()

// 获取状态
console.log(userStore.isLoggedIn)
console.log(userStore.username)

// 调用方法
await userStore.login({ usernameOrEmail, password })
await userStore.logout()
await userStore.fetchUserProfile()
```

### Player Store

```javascript
import { usePlayerStore } from '@/store/player'

const playerStore = usePlayerStore()

// 播放音乐
playerStore.playMusic(music)

// 控制播放
playerStore.togglePlay()
playerStore.playNext()
playerStore.playPrevious()

// 设置播放列表
playerStore.setPlaylist(musicList, 0)
```

---

## 🎨 样式规范

### 使用 SCSS 变量

```scss
<style scoped lang="scss">
@use '@/styles/variables.scss' as *;

.component {
  color: $primary-color;
  padding: $spacing-md;
  border-radius: $border-radius-base;
}
</style>
```

### 响应式设计

```scss
// 移动端适配
@media (max-width: 768px) {
  .component {
    padding: $spacing-sm;
  }
}
```

---

## 📖 最佳实践

1. **使用组合式 API**：优先使用 `<script setup>`
2. **Props 验证**：始终定义 Props 的类型和默认值
3. **事件命名**：使用小写和连字符（kebab-case）
4. **响应式数据**：使用 `ref` 和 `reactive` 管理状态
5. **计算属性**：使用 `computed` 处理派生状态
6. **生命周期**：使用 `onMounted`, `onUnmounted` 等钩子
7. **类型注释**：使用 JSDoc 注释提供类型信息

---

*文档版本：1.0.0*
*创建日期：2026-02-20*
*最后更新：2026-02-20*
