# 🎵 音乐共享平台 (Music Share Platform)

一个基于 Spring Boot + Vue.js 的音乐社区平台，支持音乐上传、播放、分享和社交互动。

---

## 📋 项目简介

本项目是一个完整的音乐共享平台，允许用户上传、分享音乐，并通过社交功能建立联系。为音乐爱好者和独立音乐人提供一个开放、互动的社区。

### 核心功能
- 🎵 **音乐管理**：上传、播放、搜索、歌单管理
- 👥 **用户系统**：注册、登录、个人主页、权限控制
- 💬 **社交互动**：评论、点赞、关注、动态流
- 🛡️ **后台管理**：内容审核、用户管理、数据统计

---

## 🛠️ 技术栈

| 层级 | 技术选型 |
|------|----------|
| **后端框架** | Spring Boot 3.x |
| **前端框架** | Vue.js 3.x + Vite |
| **数据库** | MySQL 8.x |
| **缓存** | Redis 7.x |
| **安全认证** | Spring Security + JWT |
| **API文档** | Swagger/OpenAPI 3.0 |
| **文件存储** | 本地存储 |
| **构建工具** | Maven (后端) + npm (前端) |
| **容器化** | Docker + Docker Compose |

---

## 🚀 快速开始

### 前置要求

- ✅ **Docker Desktop** (已安装)
- ✅ **Node.js 18+** (已安装 v20.14.0)
- ⚠️ **JDK 17+** (需要安装)
- ⚠️ **Maven 3.6+** (需要安装)

> 📖 详细的环境搭建指南请查看 [ENVIRONMENT_SETUP.md](./ENVIRONMENT_SETUP.md)

### 1. 启动数据库和缓存服务

```bash
# 启动 MySQL + Redis
docker-compose up -d

# 查看服务状态
docker-compose ps

# 访问 phpMyAdmin（数据库管理界面）
# http://localhost:8080
# 用户名: root
# 密码: root123456
```

### 2. 启动后端服务

```bash
cd backend

# 安装依赖
mvn clean install

# 启动应用
mvn spring-boot:run

# 后端 API 地址: http://localhost:8081
# Swagger 文档: http://localhost:8081/swagger-ui.html
```

### 3. 启动前端服务

```bash
cd frontend

# 安装依赖
npm install

# 启动开发服务器
npm run dev

# 前端访问地址: http://localhost:5173
```

---

## 📁 项目结构

```
music-share-platform/
├── backend/                    # 后端 Spring Boot 项目
│   ├── src/
│   │   ├── main/
│   │   │   ├── java/com/musicshare/
│   │   │   │   ├── config/            # 配置类
│   │   │   │   ├── controller/        # 控制器
│   │   │   │   ├── service/           # 业务逻辑
│   │   │   │   ├── repository/        # 数据访问
│   │   │   │   ├── entity/            # 实体类
│   │   │   │   ├── dto/               # 数据传输对象
│   │   │   │   ├── security/          # 安全认证
│   │   │   │   └── exception/         # 异常处理
│   │   │   └── resources/
│   │   │       ├── application.yml    # 配置文件
│   │   │       └── db/migration/      # 数据库脚本
│   │   └── test/                      # 测试代码
│   ├── docs/                          # 后端文档
│   │   ├── README.md
│   │   ├── DATABASE_DESIGN.md
│   │   ├── API_DESIGN.md
│   │   └── modules/                   # 模块文档
│   └── pom.xml                        # Maven 配置
│
├── frontend/                   # 前端 Vue.js 项目
│   ├── src/
│   │   ├── api/                # API 接口封装
│   │   ├── assets/             # 静态资源
│   │   ├── components/         # 通用组件
│   │   ├── composables/        # 组合式函数
│   │   ├── router/             # 路由配置
│   │   ├── store/              # 状态管理 (Pinia)
│   │   ├── styles/             # 全局样式
│   │   ├── utils/              # 工具函数
│   │   ├── views/              # 页面组件
│   │   ├── App.vue             # 根组件
│   │   └── main.js             # 入口文件
│   ├── docs/                   # 前端文档
│   │   ├── README.md
│   │   ├── COMPONENT_GUIDE.md
│   │   └── STATE_MANAGEMENT.md
│   ├── package.json
│   └── vite.config.js
│
├── document/                   # 项目文档
│   ├── instruction.md          # 开发指导手册
│   └── task_book.md            # 需求规格说明书
│
├── docker-compose.yml          # Docker 编排配置
├── .env                        # 环境变量
├── .gitignore
├── ENVIRONMENT_SETUP.md        # 环境搭建指南
└── README.md                   # 项目说明（本文件）
```

---

## 🔧 开发指南

### 代码规范

- **后端**：遵循阿里巴巴 Java 开发规范，使用 JavaDoc 注释
- **前端**：使用 ESLint + Prettier，组件使用 PascalCase 命名
- **提交**：遵循 Conventional Commits 规范

### API 文档

- 后端 API 文档通过 Swagger 自动生成
- 访问地址：http://localhost:8081/swagger-ui.html

### 数据库管理

- 使用 phpMyAdmin：http://localhost:8080
- 或使用 Docker 命令行：
  ```bash
  docker exec -it musicshare-mysql mysql -u musicshare -pmusicshare123 musicshare
  ```

---

## 👥 用户角色

| 角色 | 权限 |
|------|------|
| **普通用户** | 浏览、播放、评论、点赞、关注、创建歌单 |
| **音乐人** | 普通用户权限 + 上传音乐、管理作品 |
| **管理员** | 后台管理、内容审核、用户管理、数据监控 |

---

## 📦 核心模块

### 1. 用户管理模块
- 用户注册/登录（JWT）
- 个人信息管理
- 角色权限控制（RBAC）

### 2. 音乐内容管理模块
- 音乐上传（分片上传、断点续传）
- 音频播放器
- 歌单管理
- 搜索与推荐

### 3. 社交互动模块
- 评论系统（支持回复）
- 点赞功能
- 关注/粉丝
- 动态流

### 4. 后台管理模块
- 内容审核
- 用户管理
- 数据统计
- 系统配置

---

## 🔒 安全特性

- ✅ 密码加密存储 (BCrypt)
- ✅ JWT 身份认证
- ✅ 接口权限校验
- ✅ HTTPS 传输加密
- ✅ SQL 注入防护
- ✅ XSS/CSRF 防护

---

## 📊 开发进度

### MVP 阶段 (100% 完成) ✅

- [x] **Phase 1**: 项目初始化 ✅
  - [x] 后端 Spring Boot 项目搭建
  - [x] 前端 Vue.js 项目搭建
  - [x] Docker 环境配置

- [x] **Phase 2**: 用户管理模块 ✅
  - [x] 用户注册/登录（JWT 认证）
  - [x] 个人资料管理
  - [x] 角色权限控制（RBAC）

- [x] **Phase 3**: 音乐内容管理模块 ✅
  - [x] 音乐上传（支持元数据提取）
  - [x] 全局音乐播放器（3种播放模式）
  - [x] 歌单管理
  - [x] 音乐搜索和浏览

- [x] **Phase 4**: 社交互动模块 ✅
  - [x] 评论系统（支持嵌套回复）
  - [x] 点赞功能
  - [x] 关注/粉丝系统

- [x] **Phase 5**: 后台管理模块 ✅
  - [x] 管理员仪表板（9项统计）
  - [x] 用户管理（搜索、启用/禁用、角色分配）
  - [x] 音乐审核工作流（批准/拒绝）

**Git Commit**: fd23a6c | **总代码量**: ~27,000 行 | **API 端点**: 60+

### 下一阶段计划

- [ ] **Phase 6**: 待选择开发方向
  - 🔍 选项 A: 搜索与发现增强（推荐）
  - 🔔 选项 B: 通知与实时系统
  - ⚡ 选项 C: 性能优化与缓存
  - 📊 选项 D: 分析与报表仪表板

> 📖 详细规划请查看 [NEXT_PHASE_OPTIONS.md](./NEXT_PHASE_OPTIONS.md)

- [ ] **Phase 7-9**: 根据 Phase 6 选择继续开发
- [ ] **Phase 10**: 性能优化与部署

---

## 📝 许可证

本项目为毕业设计项目，仅供学习交流使用。

---

## 📮 联系方式

如有问题或建议，欢迎提交 Issue。

---

## 📚 项目文档

- 📖 [PROJECT_STATUS.MD](./PROJECT_STATUS.MD) - 项目进度状态
- 📖 [ENVIRONMENT_SETUP.md](./ENVIRONMENT_SETUP.md) - 环境搭建指南
- 📖 [TESTING_GUIDE.md](./TESTING_GUIDE.md) - 测试指南
- 📖 [PHASE_5_IMPLEMENTATION_COMPLETE.md](./PHASE_5_IMPLEMENTATION_COMPLETE.md) - Phase 5 实施总结
- 📖 [NEXT_PHASE_OPTIONS.md](./NEXT_PHASE_OPTIONS.md) - 下一阶段开发选项

---

*项目版本：1.0.0 MVP*
*创建日期：2026-02-20*
*最后更新：2026-02-20*
*当前状态：MVP 完成，进入测试阶段*
