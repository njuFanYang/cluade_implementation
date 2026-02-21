# 环境搭建与部署指南

## 前置要求

| 工具 | 版本 | 验证命令 |
|------|------|---------|
| JDK | 17+ | `java -version` |
| Maven | 3.6+ | `mvn -version` |
| Node.js | 18+ | `node -v` |
| Docker | 任意 | `docker -v` |

---

## 本地开发

### 1. 启动基础服务（MySQL + Redis）

```bash
docker-compose up -d
```

| 服务 | 地址 | 账号信息 |
|------|------|---------|
| MySQL | `localhost:3306` | 用户: `musicshare` / 密码: `musicshare123` / 库: `musicshare` |
| Redis | `localhost:6379` | 无密码 |
| phpMyAdmin | http://localhost:8080 | root / root123456 |

### 2. 启动后端

```bash
cd backend
mvn spring-boot:run
```

- API 地址：http://localhost:8081
- Swagger 文档：http://localhost:8081/swagger-ui.html

### 3. 启动前端

```bash
cd frontend
npm install
npm run dev
```

- 前端地址：http://localhost:5173

---

## 创建管理员账号

先在前端注册普通用户，再通过 SQL 授予管理员角色：

```bash
docker exec -it musicshare-mysql mysql -u musicshare -pmusicshare123 musicshare
```

```sql
-- 将用户 ID 改为实际用户 ID
INSERT INTO user_roles (user_id, role_id)
SELECT 1, id FROM roles WHERE name = 'ROLE_ADMIN';
```

---

## 运行测试

### 后端（JUnit + Mockito）

```bash
cd backend
mvn test
# 11 个测试类，158 个测试用例，全部通过
```

### 前端（Vitest）

```bash
cd frontend
npm test                  # 单次运行（57 个测试）
npm run test:watch        # 监听模式
npm run test:coverage     # 生成覆盖率报告
```

### E2E 端到端测试（Playwright）— 需先启动本地服务

```bash
cd frontend
npx playwright install chromium   # 首次使用需安装浏览器
npm run test:e2e                  # 运行所有 E2E 测试
npm run test:e2e:ui               # 可视化交互模式
```

---

## 生产构建

### 后端打包

```bash
cd backend
mvn clean package -DskipTests
java -jar target/musicshare-backend-*.jar --spring.profiles.active=prod
```

### 前端打包

```bash
cd frontend
npm run build
# 构建产物位于 frontend/dist/
```

将 `dist/` 目录部署到 Nginx 或任意静态服务器，并在 `.env.production` 中配置后端地址：

```env
VITE_API_BASE_URL=https://你的后端域名.com
```

---

## 环境变量

### 后端（覆盖 `application.yml` 默认值）

| 变量名 | 默认值 | 说明 |
|--------|--------|------|
| `SPRING_DATASOURCE_URL` | `jdbc:mysql://localhost:3306/musicshare` | 数据库连接地址 |
| `SPRING_DATASOURCE_USERNAME` | `musicshare` | 数据库用户名 |
| `SPRING_DATASOURCE_PASSWORD` | `musicshare123` | 数据库密码 |
| `SPRING_REDIS_HOST` | `localhost` | Redis 主机地址 |
| `JWT_SECRET` | （见 application.yml） | JWT 签名密钥 |
| `FILE_UPLOAD_PATH` | `./uploads` | 音乐文件存储路径 |

### 前端（`.env.production`）

```env
VITE_API_BASE_URL=https://你的后端域名.com
```

---

## Docker Compose 常用命令

```bash
docker-compose up -d        # 启动所有服务
docker-compose down         # 停止所有服务
docker-compose logs -f      # 实时查看日志
docker-compose ps           # 查看服务状态
```

---

## 端口一览

| 服务 | 端口 | 地址 |
|------|------|------|
| 前端（开发） | 5173 | http://localhost:5173 |
| 后端 API | 8081 | http://localhost:8081 |
| MySQL | 3306 | — |
| Redis | 6379 | — |
| phpMyAdmin | 8080 | http://localhost:8080 |
