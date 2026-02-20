# 状态管理文档

## 📋 概述

本项目使用 **Pinia** 作为状态管理库。Pinia 是 Vue 3 的官方状态管理库，提供了更简洁的 API 和更好的 TypeScript 支持。

---

## 🏪 Store 模块

### User Store (user.js)

管理用户认证状态和个人信息。

#### State

| 状态 | 类型 | 说明 |
|------|------|------|
| `token` | String | JWT 认证令牌 |
| `userInfo` | Object | 用户信息对象 |

#### Getters

| Getter | 类型 | 说明 |
|--------|------|------|
| `isLoggedIn` | Boolean | 是否已登录 |
| `userId` | Number | 用户 ID |
| `username` | String | 用户名 |
| `avatar` | String | 头像 URL |
| `role` | String | 用户角色 |
| `isMusician` | Boolean | 是否为音乐人 |
| `isAdmin` | Boolean | 是否为管理员 |

#### Actions

**login(credentials)**
- 参数：`{ username, password }`
- 返回：`Promise<boolean>`
- 说明：用户登录，成功后保存 token 和用户信息

**logout()**
- 说明：用户登出，清除本地数据

**fetchUserProfile()**
- 说明：获取当前用户详细信息

**updateUserInfo(newInfo)**
- 参数：`Object` - 新的用户信息
- 说明：更新本地用户信息

#### 使用示例

```vue
<script setup>
import { useUserStore } from '@/store/user'

const userStore = useUserStore()

// 登录
async function handleLogin() {
  const success = await userStore.login({
    username: 'user@example.com',
    password: 'password123'
  })

  if (success) {
    console.log('Login successful')
  }
}

// 获取用户信息
console.log(userStore.username)
console.log(userStore.isLoggedIn)

// 登出
function handleLogout() {
  userStore.logout()
}
</script>
```

---

### Player Store (player.js)

管理音乐播放器状态。

#### State

| 状态 | 类型 | 说明 |
|------|------|------|
| `currentMusic` | Object | 当前播放的音乐 |
| `playlist` | Array | 播放列表 |
| `currentIndex` | Number | 当前播放索引 |
| `isPlaying` | Boolean | 是否正在播放 |
| `volume` | Number | 音量 (0-1) |
| `currentTime` | Number | 当前播放时间（秒） |
| `duration` | Number | 音乐总时长（秒） |
| `playMode` | String | 播放模式 |

#### Getters

| Getter | 类型 | 说明 |
|--------|------|------|
| `progress` | Number | 播放进度百分比 (0-100) |
| `hasNext` | Boolean | 是否有下一首 |
| `hasPrevious` | Boolean | 是否有上一首 |

#### Actions

**playMusic(music)**
- 参数：音乐对象
- 说明：播放指定音乐

**togglePlay()**
- 说明：切换播放/暂停状态

**playNext()**
- 说明：播放下一首

**playPrevious()**
- 说明：播放上一首

**setPlaylist(list, index)**
- 参数：播放列表和起始索引
- 说明：设置播放列表并从指定位置开始播放

**addToPlaylist(music)**
- 参数：音乐对象
- 说明：添加音乐到播放列表

**removeFromPlaylist(index)**
- 参数：索引
- 说明：从播放列表移除指定音乐

**clearPlaylist()**
- 说明：清空播放列表

**setVolume(val)**
- 参数：音量值 (0-1)
- 说明：设置音量

**setPlayMode(mode)**
- 参数：'loop' | 'single' | 'shuffle'
- 说明：设置播放模式

#### 使用示例

```vue
<script setup>
import { usePlayerStore } from '@/store/player'

const playerStore = usePlayerStore()

// 播放音乐
function play(music) {
  playerStore.playMusic(music)
}

// 控制播放
function togglePlayback() {
  playerStore.togglePlay()
}

// 下一首
function next() {
  playerStore.playNext()
}

// 设置播放列表
function playAll(musicList) {
  playerStore.setPlaylist(musicList, 0)
}

// 监听播放状态
watch(() => playerStore.isPlaying, (playing) => {
  console.log('Playing:', playing)
})
</script>
```

---

## 🎯 最佳实践

### 1. 在组件中使用 Store

```vue
<script setup>
import { useUserStore } from '@/store/user'
import { storeToRefs } from 'pinia'

const userStore = useUserStore()

// 使用 storeToRefs 保持响应性
const { username, isLoggedIn } = storeToRefs(userStore)

// 直接调用 actions
userStore.logout()
</script>
```

### 2. 组合多个 Store

```vue
<script setup>
import { useUserStore } from '@/store/user'
import { usePlayerStore } from '@/store/player'

const userStore = useUserStore()
const playerStore = usePlayerStore()

// 结合使用
if (userStore.isLoggedIn) {
  playerStore.playMusic(music)
}
</script>
```

### 3. 在路由守卫中使用

```javascript
// router/index.js
import { useUserStore } from '@/store/user'

router.beforeEach((to, from, next) => {
  const userStore = useUserStore()

  if (to.meta.requiresAuth && !userStore.isLoggedIn) {
    next('/login')
  } else {
    next()
  }
})
```

### 4. 持久化存储

User Store 已实现自动持久化到 localStorage：

```javascript
// 登录时自动保存
localStorage.setItem('token', token)
localStorage.setItem('userInfo', JSON.stringify(userInfo))

// 页面刷新时自动恢复
const token = ref(localStorage.getItem('token') || '')
const userInfo = ref(JSON.parse(localStorage.getItem('userInfo') || 'null'))
```

---

## 🔄 状态流转

### 用户登录流程

```
1. 组件调用 userStore.login()
   ↓
2. Store 发送 API 请求
   ↓
3. 接收响应，保存 token 和 userInfo
   ↓
4. 更新 localStorage
   ↓
5. 组件响应式更新
```

### 音乐播放流程

```
1. 组件调用 playerStore.playMusic()
   ↓
2. Store 更新 currentMusic 和 isPlaying
   ↓
3. 音频组件监听状态变化
   ↓
4. 播放音乐并更新 currentTime
```

---

## 📝 注意事项

1. **始终使用 storeToRefs**：从 store 解构状态时使用 `storeToRefs` 保持响应性
2. **不要直接修改 state**：使用 actions 来修改状态
3. **清理副作用**：在组件卸载时清理监听器
4. **错误处理**：在 actions 中妥善处理错误

---

*文档版本：1.0.0*
*创建日期：2026-02-20*
*最后更新：2026-02-20*
