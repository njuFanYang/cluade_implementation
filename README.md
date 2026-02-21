# 🎵 音乐共享平台 (MusicShare)

基于 Spring Boot 3.x + Vue.js 3 的音乐社区平台，支持音乐上传、播放、社交互动与后台管理。

---

## 技术栈

| 层级 | 技术 |
|------|------|
| 后端 | Spring Boot 3.x, Spring Security, Spring Data JPA |
| 前端 | Vue.js 3, Pinia, Vue Router, Element Plus, Vite |
| 数据库 | MySQL 8.x |
| 缓存 | Redis 7.x |
| 认证 | JWT |
| API 文档 | Swagger / OpenAPI 3.0 |
| 容器化 | Docker + Docker Compose |
| 测试 | JUnit 5 + Mockito (后端), Vitest (前端), Playwright (E2E) |

---

## 快速启动

详见 [SETUP.md](./SETUP.md)

```bash
# 1. 启动数据库
docker-compose up -d

# 2. 启动后端
cd backend && mvn spring-boot:run

# 3. 启动前端
cd frontend && npm run dev
```

| 服务 | 地址 |
|------|------|
| 前端 | http://localhost:5173 |
| 后端 API | http://localhost:8081 |
| Swagger UI | http://localhost:8081/swagger-ui.html |
| phpMyAdmin | http://localhost:8080 |

---

## 项目结构

```
music-share-platform/
├── backend/                    # Spring Boot 后端
│   ├── src/main/java/com/musicshare/
│   │   ├── controller/         # REST 控制器
│   │   ├── service/            # 业务逻辑
│   │   ├── repository/         # 数据访问层
│   │   ├── entity/             # JPA 实体
│   │   ├── dto/                # 请求/响应 DTO
│   │   ├── security/           # JWT 认证
│   │   └── exception/          # 全局异常处理
│   ├── src/test/               # 单元测试 & 集成测试
│   └── pom.xml
│
├── frontend/                   # Vue.js 前端
│   ├── src/
│   │   ├── api/                # API 接口封装
│   │   ├── components/         # 通用组件
│   │   ├── store/              # Pinia 状态管理
│   │   ├── views/              # 页面组件
│   │   └── router/             # 路由配置
│   ├── src/test/               # Vitest 单元测试
│   ├── e2e/                    # Playwright E2E 测试
│   └── package.json
│
├── document/                   # 需求规格说明书
├── docker-compose.yml
├── SETUP.md                    # 环境搭建 & 部署指南
└── README.md
```

---

## 用户角色

| 角色 | 权限 |
|------|------|
| 普通用户 | 浏览、播放、评论、点赞、关注、创建歌单 |
| 音乐人 | 普通用户权限 + 上传音乐、管理作品 |
| 管理员 | 后台管理、内容审核、用户管理、数据统计 |

---

## 开发进度

- [x] **Phase 1** — 项目初始化（Spring Boot + Vue.js + Docker）
- [x] **Phase 2** — 用户管理（注册/登录/JWT/RBAC）
- [x] **Phase 3** — 音乐管理（上传/播放器/歌单/搜索）
- [x] **Phase 4** — 社交互动（评论/点赞/关注）
- [x] **Phase 5** — 后台管理（仪表板/用户管理/音乐审核）
- [x] **Phase 6** — 测试（单元测试 158 个 + E2E 测试）

---

## 测试覆盖

```
后端 (mvn test)          158 tests — 11 个测试类，全部通过
前端 (npm test)           57 tests — Pinia store 单元测试，全部通过
E2E  (npm run test:e2e)  Playwright — 需启动本地服务
```

详见 [SETUP.md#running-tests](./SETUP.md#running-tests)

---

## API 端点概览

| 模块 | 前缀 | 端点数 |
|------|------|--------|
| 认证 | `/api/auth` | 3 |
| 用户 | `/api/users` | 5 |
| 音乐 | `/api/music` | 9 |
| 歌单 | `/api/playlists` | 10 |
| 评论 | `/api/comments` | 7 |
| 点赞 | `/api/likes` | 4 |
| 关注 | `/api/follows` | 6 |
| 风格 | `/api/genres` | 3 |
| 管理 | `/api/admin` | 8+ |

完整文档：http://localhost:8081/swagger-ui.html

---

## 安全特性

- ✅ BCrypt 密码加密
- ✅ JWT 身份认证（可配置过期时间）
- ✅ 基于角色的接口权限控制（RBAC）
- ✅ Spring Security 过滤链
- ✅ 文件类型校验（防恶意上传）

---

*版本: 1.0.0 | 创建: 2026-02-20 | 最后更新: 2026-02-21*
