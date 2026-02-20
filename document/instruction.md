# 音乐共享平台 - Claude Code 开发指导手册

---

## 📋 C - Context（上下文背景）

### 项目概述
本项目是一个**音乐共享平台**，旨在构建一个以"共享"与"互动"为核心的音乐社区。平台允许用户上传、分享音乐，并通过社交功能建立联系，为音乐爱好者和独立音乐人提供一个开放、互动的平台。

### 技术栈
| 层级 | 技术选型 |
|------|----------|
| **后端框架** | Spring Boot 3.x |
| **前端框架** | Vue.js 3.x + Vite |
| **数据库** | MySQL 8.x |
| **缓存** | Redis |
| **安全认证** | Spring Security + JWT |
| **API文档** | Swagger/OpenAPI 3.0 |
| **文件存储** | 本地存储 / MinIO / 云OSS |
| **构建工具** | Maven (后端) + npm/pnpm (前端) |

### 用户角色
1. **普通用户**：浏览、播放、评论、点赞、关注、创建歌单
2. **音乐人**：普通用户权限 + 上传音乐、管理作品
3. **管理员**：后台管理、内容审核、用户管理、数据监控

### 核心业务模块
- 用户管理模块（注册、登录、个人信息、权限控制）
- 音乐内容管理模块（上传、播放、歌单、搜索）
- 社交互动模块（评论、点赞、关注、动态、分享）
- 后台管理模块（审核、用户管理、数据监控）

---

## 🎯 O - Objective（目标任务）

### 主要目标
基于需求规格说明书，完整实现音乐共享平台的前后端代码，确保：

1. **功能完整性**：覆盖所有功能需求
2. **代码质量**：遵循最佳实践，结构清晰，可维护性强
3. **文档完善**：每个模块都有详细的说明文档
4. **安全可靠**：满足所有安全性需求
5. **性能达标**：满足响应时间和并发要求

### 交付物清单
- [ ] 后端Spring Boot项目完整代码
- [ ] 前端Vue.js项目完整代码
- [ ] 数据库设计文档及SQL脚本
- [ ] API接口文档（Swagger自动生成）
- [ ] 项目README及部署文档
- [ ] 各模块开发说明文档

---

## 📐 S - Style（风格规范）

### 后端代码规范

#### 项目结构
```
backend/
├── src/main/java/com/musicshare/
│   ├── MusicShareApplication.java          # 启动类
│   ├── config/                             # 配置类
│   │   ├── SecurityConfig.java
│   │   ├── RedisConfig.java
│   │   ├── SwaggerConfig.java
│   │   └── WebMvcConfig.java
│   ├── controller/                         # 控制器层
│   │   ├── UserController.java
│   │   ├── MusicController.java
│   │   ├── PlaylistController.java
│   │   ├── CommentController.java
│   │   ├── SocialController.java
│   │   └── admin/                          # 管理员接口
│   │       ├── AdminUserController.java
│   │       ├── AdminAuditController.java
│   │       └── AdminStatsController.java
│   ├── service/                            # 服务层
│   │   ├── UserService.java
│   │   ├── impl/
│   │   │   └── UserServiceImpl.java
│   │   └── ...
│   ├── repository/                         # 数据访问层
│   │   ├── UserRepository.java
│   │   └── ...
│   ├── entity/                             # 实体类
│   │   ├── User.java
│   │   ├── Music.java
│   │   ├── Playlist.java
│   │   └── ...
│   ├── dto/                                # 数据传输对象
│   │   ├── request/
│   │   │   ├── LoginRequest.java
│   │   │   └── ...
│   │   └── response/
│   │       ├── ApiResponse.java
│   │       └── ...
│   ├── security/                           # 安全相关
│   │   ├── JwtTokenProvider.java
│   │   ├── JwtAuthenticationFilter.java
│   │   └── CustomUserDetailsService.java
│   ├── exception/                          # 异常处理
│   │   ├── GlobalExceptionHandler.java
│   │   ├── BusinessException.java
│   │   └── ErrorCode.java
│   └── util/                               # 工具类
│       ├── FileUtil.java
│       └── PasswordUtil.java
├── src/main/resources/
│   ├── application.yml
│   ├── application-dev.yml
│   ├── application-prod.yml
│   └── db/
│       └── migration/                      # 数据库迁移脚本
└── docs/                                   # 后端文档目录
    ├── README.md                           # 后端说明文档
    ├── API_DESIGN.md                       # API设计文档
    ├── DATABASE_DESIGN.md                  # 数据库设计文档
    └── modules/                            # 模块文档
        ├── user-module.md
        ├── music-module.md
        └── ...
```

#### 编码规范
- **命名规范**：类名PascalCase，方法/变量camelCase，常量UPPER_SNAKE_CASE
- **注释要求**：类、方法必须有JavaDoc注释，复杂逻辑需行内注释
- **接口设计**：遵循RESTful规范，使用标准HTTP状态码
- **异常处理**：统一异常处理，返回规范化错误响应
- **日志规范**：使用SLF4J，关键操作必须记录日志

### 前端代码规范

#### 项目结构
```
frontend/
├── src/
│   ├── main.js                             # 入口文件
│   ├── App.vue                             # 根组件
│   ├── router/                             # 路由配置
│   │   └── index.js
│   ├── store/                              # 状态管理 (Pinia)
│   │   ├── user.js
│   │   ├── music.js
│   │   └── player.js
│   ├── api/                                # API接口封装
│   │   ├── request.js                      # Axios封装
│   │   ├── user.js
│   │   ├── music.js
│   │   └── ...
│   ├── views/                              # 页面组件
│   │   ├── Home.vue
│   │   ├── Login.vue
│   │   ├── user/
│   │   │   ├── Profile.vue
│   │   │   └── Settings.vue
│   │   ├── music/
│   │   │   ├── MusicDetail.vue
│   │   │   └── Upload.vue
│   │   ├── playlist/
│   │   │   ├── PlaylistDetail.vue
│   │   │   └── Create.vue
│   │   └── admin/
│   │       ├── Dashboard.vue
│   │       ├── AuditList.vue
│   │       └── UserManage.vue
│   ├── components/                         # 通用组件
│   │   ├── common/
│   │   │   ├── Header.vue
│   │   │   ├── Footer.vue
│   │   │   └── Sidebar.vue
│   │   ├── music/
│   │   │   ├── MusicPlayer.vue
│   │   │   ├── MusicCard.vue
│   │   │   └── PlaylistCard.vue
│   │   └── social/
│   │       ├── CommentList.vue
│   │       └── UserCard.vue
│   ├── composables/                        # 组合式函数
│   │   ├── usePlayer.js
│   │   └── useAuth.js
│   ├── utils/                              # 工具函数
│   │   ├── format.js
│   │   └── storage.js
│   ├── styles/                             # 全局样式
│   │   ├── variables.scss
│   │   └── global.scss
│   └── assets/                             # 静态资源
├── public/
├── docs/                                   # 前端文档目录
│   ├── README.md                           # 前端说明文档
│   ├── COMPONENT_GUIDE.md                  # 组件使用指南
│   └── STATE_MANAGEMENT.md                 # 状态管理文档
└── package.json
```

#### 编码规范
- **组件命名**：PascalCase，多词组合（如MusicPlayer.vue）
- **组合式API**：优先使用 `<script setup>` 语法
- **样式规范**：使用Scoped CSS或CSS Modules，支持SCSS
- **注释要求**：组件必须有功能描述注释，Props需有类型定义和说明

---

## 🎭 T - Tone（语调风格）

### 代码注释风格

#### 后端Java注释示例
```java
/**
 * 用户服务实现类
 * 
 * <p>提供用户注册、登录、信息管理等核心功能的业务逻辑实现。
 * 
 * <h3>主要功能：</h3>
 * <ul>
 *   <li>用户注册（支持邮箱/手机号）</li>
 *   <li>用户登录（账号密码/第三方OAuth）</li>
 *   <li>个人信息管理</li>
 *   <li>密码修改与重置</li>
 * </ul>
 * 
 * @author MusicShare Team
 * @version 1.0.0
 * @since 2026-02-20
 * @see UserService
 * @see UserRepository
 */
@Service
@Transactional
@Slf4j
public class UserServiceImpl implements UserService {

    /**
     * 用户注册
     * 
     * <p>处理新用户注册逻辑，包括参数校验、密码加密、发送验证邮件等。
     * 
     * @param request 注册请求对象，包含用户名、邮箱、密码等信息
     * @return 注册成功的用户信息
     * @throws BusinessException 当邮箱已被注册时抛出
     */
    @Override
    public UserResponse register(RegisterRequest request) {
        // 实现逻辑...
    }
}
```

#### 前端Vue注释示例
```vue
<script setup>
/**
 * 音乐播放器组件
 * 
 * @description 全局音乐播放控制组件，提供播放、暂停、上下首切换、
 *              进度控制、音量调节等功能。
 * 
 * @example
 * <MusicPlayer 
 *   :playlist="currentPlaylist"
 *   :autoplay="true"
 *   @ended="handleEnded"
 * />
 * 
 * @author MusicShare Team
 * @version 1.0.0
 */

import { ref, computed, watch, onMounted, onUnmounted } from 'vue'
import { usePlayerStore } from '@/store/player'

// Props定义
const props = defineProps({
  /**
   * 播放列表
   * @type {Array<Music>}
   */
  playlist: {
    type: Array,
    default: () => []
  },
  /**
   * 是否自动播放
   * @type {boolean}
   */
  autoplay: {
    type: Boolean,
    default: false
  }
})

// Emits定义
const emit = defineEmits([
  /**
   * 当前歌曲播放结束时触发
   * @param {Music} music - 播放结束的歌曲对象
   */
  'ended',
  /**
   * 播放状态变化时触发
   * @param {boolean} isPlaying - 是否正在播放
   */
  'statusChange'
])
</script>
```

### 文档编写风格
- **清晰简洁**：使用简单明了的语言描述功能和用法
- **结构化**：使用标题、列表、表格组织内容
- **示例丰富**：每个功能点都提供代码示例
- **版本标注**：标明创建日期、版本号、作者信息

---

## 👥 A - Audience（目标受众）

### 主要受众
1. **开发人员**：后续维护和扩展系统的开发者
2. **测试人员**：需要理解系统功能进行测试
3. **运维人员**：负责系统部署和运维
4. **项目管理者**：需要了解项目进度和结构

### 文档需求
| 受众 | 文档类型 | 内容要求 |
|------|----------|----------|
| 开发人员 | 技术文档、API文档、代码注释 | 详细的实现细节和使用方法 |
| 测试人员 | 接口文档、功能说明 | 清晰的功能描述和测试用例 |
| 运维人员 | 部署文档、配置说明 | 部署步骤、环境要求、配置项 |
| 项目管理者 | README、模块说明 | 项目概览、模块划分、进度说明 |

---

## 📝 R - Response（输出要求）

### ⚠️ 文档要求（重要！必须遵守！）

**在实现每个模块的代码时，必须同时创建或更新相应的说明文档。这是强制性要求，不可省略！**

#### 必须创建的文档清单

##### 1. 项目根目录文档
```
/README.md                      # 项目总览文档（必须）
├── 项目简介
├── 技术栈说明
├── 快速开始指南
├── 项目结构说明
├── 环境要求
└── 部署说明
```

##### 2. 后端文档 (`/backend/docs/`)
```
README.md                       # 后端项目说明（必须）
DATABASE_DESIGN.md              # 数据库设计文档（必须）
├── ER图
├── 表结构说明
├── 索引设计
└── 数据字典

API_DESIGN.md                   # API设计规范（必须）
├── 接口规范
├── 认证机制
├── 错误码定义
└── 响应格式

modules/                        # 模块文档（每个模块必须）
├── user-module.md              # 用户模块文档
├── music-module.md             # 音乐模块文档
├── playlist-module.md          # 歌单模块文档
├── social-module.md            # 社交模块文档
└── admin-module.md             # 管理模块文档
```

##### 3. 前端文档 (`/frontend/docs/`)
```
README.md                       # 前端项目说明（必须）
COMPONENT_GUIDE.md              # 组件使用指南（必须）
├── 通用组件
├── 业务组件
└── 使用示例

STATE_MANAGEMENT.md             # 状态管理文档（必须）
├── Store设计
├── 状态流转
└── 使用方法

ROUTER_GUIDE.md                 # 路由配置文档（必须）
├── 路由结构
├── 权限控制
└── 导航守卫
```

##### 4. 模块文档模板
每个模块文档必须包含以下内容：
```markdown
# [模块名称] 模块文档

## 1. 模块概述
- 功能描述
- 业务场景
- 依赖关系

## 2. 数据模型
- 实体类说明
- 字段定义
- 关联关系

## 3. API接口
- 接口列表
- 请求/响应示例
- 错误处理

## 4. 业务逻辑
- 核心流程
- 业务规则
- 注意事项

## 5. 前端页面
- 页面列表
- 组件说明
- 交互逻辑

## 6. 测试要点
- 单元测试
- 集成测试
- 测试用例
```

---

## 📅 详细实现计划

### 第一阶段：项目初始化与基础架构（预计2天）

#### Phase 1.1: 后端项目初始化
| 任务 | 说明 | 文档要求 |
|------|------|----------|
| 创建Spring Boot项目 | 配置Maven依赖、项目结构 | 更新README.md |
| 配置数据库连接 | MySQL配置、连接池设置 | 创建DATABASE_DESIGN.md |
| 配置Redis缓存 | Redis连接配置 | 添加到README.md |
| 配置Swagger | API文档自动生成 | 创建API_DESIGN.md |
| 全局异常处理 | 统一异常处理机制 | 定义错误码文档 |
| 统一响应格式 | ApiResponse封装 | 更新API_DESIGN.md |

**交付文档**：
- `/backend/README.md`
- `/backend/docs/DATABASE_DESIGN.md`（初始版本）
- `/backend/docs/API_DESIGN.md`（初始版本）

#### Phase 1.2: 前端项目初始化
| 任务 | 说明 | 文档要求 |
|------|------|----------|
| 创建Vue3 + Vite项目 | 项目脚手架搭建 | 更新README.md |
| 配置路由(Vue Router) | 基础路由结构 | 创建ROUTER_GUIDE.md |
| 配置状态管理(Pinia) | Store架构设计 | 创建STATE_MANAGEMENT.md |
| 配置Axios | HTTP请求封装 | 更新README.md |
| 配置UI框架 | Element Plus集成 | 更新README.md |
| 全局样式配置 | SCSS变量、主题色 | 创建样式说明 |

**交付文档**：
- `/frontend/README.md`
- `/frontend/docs/ROUTER_GUIDE.md`（初始版本）
- `/frontend/docs/STATE_MANAGEMENT.md`（初始版本）

---

### 第二阶段：用户管理模块（预计3天）

#### Phase 2.1: 用户模块后端
| 任务 | 说明 | 文档要求 |
|------|------|----------|
| 设计用户表结构 | User、Role、UserRole表 | 更新DATABASE_DESIGN.md |
| 实现User实体类 | JPA实体、字段校验 | 添加JavaDoc注释 |
| 实现UserRepository | 数据访问层 | 添加方法注释 |
| 实现UserService | 业务逻辑层 | 详细JavaDoc |
| 实现注册接口 | POST /api/users/register | 更新API文档 |
| 实现登录接口 | POST /api/auth/login | 更新API文档 |
| JWT认证实现 | Token生成、验证、刷新 | 安全机制文档 |
| RBAC权限控制 | 角色权限校验 | 权限设计文档 |
| 个人信息接口 | GET/PUT /api/users/profile | 更新API文档 |

**交付文档**：
- `/backend/docs/modules/user-module.md`
- 更新`DATABASE_DESIGN.md`（添加用户相关表）
- 更新`API_DESIGN.md`（添加用户接口）

#### Phase 2.2: 用户模块前端
| 任务 | 说明 | 文档要求 |
|------|------|----------|
| 登录页面 | Login.vue | 组件注释 |
| 注册页面 | Register.vue | 组件注释 |
| 个人中心页面 | Profile.vue | 组件注释 |
| 设置页面 | Settings.vue | 组件注释 |
| 用户Store | user.js状态管理 | 更新STATE_MANAGEMENT.md |
| 用户API封装 | api/user.js | API方法注释 |
| 路由守卫 | 登录态校验 | 更新ROUTER_GUIDE.md |

**交付文档**：
- `/frontend/docs/COMPONENT_GUIDE.md`（添加用户组件）
- 更新STATE_MANAGEMENT.md
- 更新ROUTER_GUIDE.md

---

### 第三阶段：音乐内容管理模块（预计4天）

#### Phase 3.1: 音乐模块后端
| 任务 | 说明 | 文档要求 |
|------|------|----------|
| 设计音乐表结构 | Music、Album、Genre表 | 更新DATABASE_DESIGN.md |
| 文件上传服务 | 分片上传、断点续传 | 文件存储文档 |
| 音频转码服务 | FFmpeg集成 | 技术实现文档 |
| 音乐CRUD接口 | 上传、修改、删除、查询 | 更新API文档 |
| 歌单表结构 | Playlist、PlaylistMusic表 | 更新DATABASE_DESIGN.md |
| 歌单CRUD接口 | 创建、编辑、添加歌曲 | 更新API文档 |
| 搜索功能 | 全文搜索实现 | 搜索功能文档 |
| 推荐算法 | 基于标签的推荐 | 推荐算法文档 |

**交付文档**：
- `/backend/docs/modules/music-module.md`
- `/backend/docs/modules/playlist-module.md`
- 更新DATABASE_DESIGN.md
- 更新API_DESIGN.md

#### Phase 3.2: 音乐模块前端
| 任务 | 说明 | 文档要求 |
|------|------|----------|
| 音乐播放器组件 | MusicPlayer.vue | 详细组件文档 |
| 播放列表组件 | PlayQueue.vue | 组件注释 |
| 歌曲卡片组件 | MusicCard.vue | 组件注释 |
| 歌单卡片组件 | PlaylistCard.vue | 组件注释 |
| 音乐上传页面 | Upload.vue | 组件注释 |
| 歌曲详情页面 | MusicDetail.vue | 组件注释 |
| 歌单详情页面 | PlaylistDetail.vue | 组件注释 |
| 搜索页面 | Search.vue | 组件注释 |
| 播放器Store | player.js | 更新STATE_MANAGEMENT.md |

**交付文档**：
- 更新COMPONENT_GUIDE.md（添加音乐组件）
- 更新STATE_MANAGEMENT.md（添加播放器状态）

---

### 第四阶段：社交互动模块（预计3天）

#### Phase 4.1: 社交模块后端
| 任务 | 说明 | 文档要求 |
|------|------|----------|
| 设计评论表结构 | Comment表（支持嵌套） | 更新DATABASE_DESIGN.md |
| 评论CRUD接口 | 发表、回复、删除、列表 | 更新API文档 |
| 点赞功能 | Like表、点赞/取消接口 | 更新API文档 |
| 关注功能 | Follow表、关注/取关接口 | 更新API文档 |
| 动态流功能 | Activity表、动态列表接口 | 更新API文档 |
| 分享功能 | 生成分享链接 | 更新API文档 |

**交付文档**：
- `/backend/docs/modules/social-module.md`
- 更新DATABASE_DESIGN.md
- 更新API_DESIGN.md

#### Phase 4.2: 社交模块前端
| 任务 | 说明 | 文档要求 |
|------|------|----------|
| 评论列表组件 | CommentList.vue | 组件注释 |
| 评论输入组件 | CommentInput.vue | 组件注释 |
| 点赞按钮组件 | LikeButton.vue | 组件注释 |
| 关注按钮组件 | FollowButton.vue | 组件注释 |
| 用户卡片组件 | UserCard.vue | 组件注释 |
| 动态流页面 | Feed.vue | 组件注释 |
| 粉丝/关注列表 | FollowList.vue | 组件注释 |

**交付文档**：
- 更新COMPONENT_GUIDE.md（添加社交组件）

---

### 第五阶段：后台管理模块（预计3天）

#### Phase 5.1: 管理模块后端
| 任务 | 说明 | 文档要求 |
|------|------|----------|
| 内容审核接口 | 审核列表、通过、驳回 | 更新API文档 |
| 用户管理接口 | 用户列表、封禁、解封 | 更新API文档 |
| 举报处理接口 | 举报列表、处理 | 更新API文档 |
| 数据统计接口 | 各类统计数据查询 | 更新API文档 |
| 系统设置接口 | 配置读取、修改 | 更新API文档 |

**交付文档**：
- `/backend/docs/modules/admin-module.md`
- 更新API_DESIGN.md

#### Phase 5.2: 管理模块前端
| 任务 | 说明 | 文档要求 |
|------|------|----------|
| 管理后台布局 | AdminLayout.vue | 组件注释 |
| 数据仪表盘 | Dashboard.vue | 组件注释 |
| 审核列表页面 | AuditList.vue | 组件注释 |
| 用户管理页面 | UserManage.vue | 组件注释 |
| 举报处理页面 | ReportList.vue | 组件注释 |
| 系统设置页面 | SystemSettings.vue | 组件注释 |
| 图表组件 | 使用ECharts | 组件注释 |

**交付文档**：
- 更新COMPONENT_GUIDE.md（添加管理组件）

---

### 第六阶段：优化与完善（预计2天）

| 任务 | 说明 | 文档要求 |
|------|------|----------|
| 性能优化 | 缓存、SQL优化、懒加载 | 性能优化文档 |
| 安全加固 | XSS/CSRF防护、参数校验 | 安全设计文档 |
| 单元测试 | 核心功能测试覆盖 | 测试说明文档 |
| 部署配置 | Docker配置、环境变量 | 部署文档 |
| 文档完善 | 检查所有文档完整性 | 文档审核 |

**交付文档**：
- `/DEPLOYMENT.md` - 完整部署文档
- `/CONTRIBUTING.md` - 贡献指南
- 所有文档最终审核

---

## 📌 实现注意事项

### 🔴 强制执行事项

1. **代码即文档**
   - 每个类必须有类级别的JavaDoc/JSDoc注释
   - 每个公共方法必须有方法级别的注释
   - 复杂逻辑必须有行内注释说明

2. **文档同步更新**
   - 新增接口 → 立即更新API文档
   - 新增表结构 → 立即更新数据库文档
   - 新增组件 → 立即更新组件指南

3. **版本控制**
   - 每个文档头部标明版本号和最后更新日期
   - 重要变更在文档中记录变更历史

### 🟡 最佳实践建议

1. **接口设计**
   - 使用统一的响应格式 `{ code, message, data }`
   - 使用语义化的HTTP状态码
   - 接口版本化 `/api/v1/...`

2. **错误处理**
   - 定义完整的错误码体系
   - 前端统一错误提示处理
   - 记录错误日志便于排查

3. **安全考虑**
   - 敏感数据加密存储
   - 接口权限严格校验
   - 防止SQL注入和XSS攻击

4. **性能优化**
   - 热点数据使用Redis缓存
   - 大文件分片上传
   - 前端资源懒加载

---

## 📚 参考资源

- [Spring Boot官方文档](https://spring.io/projects/spring-boot)
- [Vue.js 3官方文档](https://vuejs.org/)
- [Element Plus组件库](https://element-plus.org/)
- [MyBatis-Plus文档](https://baomidou.com/)
- [JWT官方规范](https://jwt.io/)

---

## ✅ 检查清单

在提交每个模块代码前，请确认以下事项：

- [ ] 所有类和方法都有完整的注释
- [ ] 对应的模块文档已创建/更新
- [ ] API文档已同步更新
- [ ] 数据库文档已同步更新
- [ ] 代码符合项目规范
- [ ] 没有硬编码的配置值
- [ ] 异常处理完善
- [ ] 日志记录完整

---

*文档版本：1.0.0*  
*创建日期：2026-02-20*  
*最后更新：2026-02-20*
