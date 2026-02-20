# 🛠️ 环境搭建指南

## 目录
- [已安装工具](#已安装工具)
- [需要安装的工具](#需要安装的工具)
- [安装步骤](#安装步骤)
- [启动 Docker 服务](#启动-docker-服务)
- [验证环境](#验证环境)

---

## 已安装工具

✅ **Docker Desktop** - Version 29.2.0
✅ **Docker Compose** - Version v5.0.2
✅ **Node.js** - Version 20.14.0

---

## 需要安装的工具

### 1. JDK 17+ (Spring Boot 3.x 要求)

**推荐：Eclipse Temurin (OpenJDK)**

#### 下载地址
https://adoptium.net/temurin/releases/?version=17

#### 安装步骤
1. 下载 **Windows x64 MSI Installer**
2. 运行安装程序，选择默认安装路径（建议：`C:\Program Files\Eclipse Adoptium\jdk-17`）
3. ✅ 勾选 "Set JAVA_HOME variable"
4. ✅ 勾选 "Add to PATH"
5. 完成安装

#### 验证安装
```bash
java -version
# 应显示：openjdk version "17.x.x"
```

---

### 2. Apache Maven 3.6+

#### 下载地址
https://maven.apache.org/download.cgi

#### 安装步骤

**方法 A：使用 Chocolatey（推荐）**
```bash
# 如果已安装 Chocolatey
choco install maven

# 验证
mvn -version
```

**方法 B：手动安装**
1. 下载 `apache-maven-3.9.x-bin.zip`
2. 解压到 `C:\Program Files\Apache\Maven`
3. 配置环境变量：
   - 新建系统变量 `MAVEN_HOME` = `C:\Program Files\Apache\Maven\apache-maven-3.9.x`
   - 在 `Path` 中添加 `%MAVEN_HOME%\bin`
4. 打开新的命令行窗口验证：
   ```bash
   mvn -version
   ```

#### 配置 Maven 镜像（可选，加速下载）
编辑 `%MAVEN_HOME%\conf\settings.xml`，在 `<mirrors>` 节点添加：

```xml
<mirror>
  <id>aliyun</id>
  <mirrorOf>central</mirrorOf>
  <name>Aliyun Maven Mirror</name>
  <url>https://maven.aliyun.com/repository/public</url>
</mirror>
```

---

## 启动 Docker 服务

### 1. 确保 Docker Desktop 正在运行

检查 Windows 系统托盘，确保 Docker 图标显示为运行状态。

### 2. 启动 MySQL 和 Redis

在项目根目录下运行：

```bash
# 启动所有服务（MySQL + Redis + phpMyAdmin）
docker-compose up -d

# 查看服务状态
docker-compose ps

# 查看日志
docker-compose logs -f
```

### 3. 服务访问信息

| 服务 | 地址 | 用户名 | 密码 |
|------|------|--------|------|
| **MySQL** | `localhost:3306` | `musicshare` | `musicshare123` |
| **MySQL (Root)** | `localhost:3306` | `root` | `root123456` |
| **Redis** | `localhost:6379` | - | `redis123456` |
| **phpMyAdmin** | `http://localhost:8080` | `root` | `root123456` |

### 4. 常用 Docker 命令

```bash
# 停止服务
docker-compose stop

# 启动服务
docker-compose start

# 重启服务
docker-compose restart

# 停止并删除容器
docker-compose down

# 停止并删除容器+数据卷（⚠️ 会删除数据库数据）
docker-compose down -v

# 查看容器日志
docker-compose logs mysql
docker-compose logs redis
```

---

## 验证环境

运行以下命令验证所有工具是否正确安装：

```bash
# 1. Java
java -version
# 预期输出：openjdk version "17.x.x" 或更高

# 2. Maven
mvn -version
# 预期输出：Apache Maven 3.6.x 或更高

# 3. Node.js
node -v
# 预期输出：v20.14.0 ✅ (已安装)

# 4. Docker
docker --version
# 预期输出：Docker version 29.2.0 ✅ (已安装)

# 5. 检查 MySQL 连接
docker exec -it musicshare-mysql mysql -u musicshare -pmusicshare123 -e "SELECT 'MySQL is running!' as status;"

# 6. 检查 Redis 连接
docker exec -it musicshare-redis redis-cli -a redis123456 ping
# 预期输出：PONG
```

---

## 可能的问题与解决方案

### ❌ Docker 服务启动失败

**问题：** 端口被占用（如 3306、6379）

**解决方案：**
```bash
# 检查端口占用
netstat -ano | findstr :3306
netstat -ano | findstr :6379

# 修改 .env 文件中的端口号
MYSQL_PORT=3307
REDIS_PORT=6380
```

### ❌ Maven 下载依赖慢

**解决方案：** 配置阿里云镜像（见上方 Maven 配置部分）

### ❌ JDK 安装后 java 命令不可用

**解决方案：**
1. 重新打开命令行窗口
2. 检查环境变量 `JAVA_HOME` 和 `PATH` 是否正确配置
3. 手动添加：`Path` → 新建 → `%JAVA_HOME%\bin`

---

## MVP 测试准备（Phase 5 完成后）

Phase 1-5 已全部完成（100% MVP），在开始 Phase 6 前，强烈建议完成本地环境测试：

### 📋 测试前检查清单

#### 1. 环境准备
- [ ] JDK 17+ 已安装并配置
- [ ] Maven 3.6+ 已安装并配置
- [ ] Docker Desktop 正在运行
- [ ] MySQL 数据库已启动
- [ ] Node.js 20+ 已安装

#### 2. 启动服务
```bash
# 1. 启动 Docker 服务（MySQL + Redis）
docker-compose up -d

# 2. 启动后端服务
cd backend
mvn clean install
mvn spring-boot:run
# 访问 http://localhost:8080/swagger-ui.html

# 3. 启动前端服务
cd frontend
npm install
npm run dev
# 访问 http://localhost:5173
```

#### 3. 核心功能测试

按以下顺序测试所有功能：

**用户系统测试**
- [ ] 注册新用户账号
- [ ] 登录系统
- [ ] 查看个人资料
- [ ] 修改个人信息
- [ ] 修改密码

**音乐功能测试**
- [ ] 上传音乐（应为 PENDING 状态）⚠️
- [ ] 浏览音乐列表
- [ ] 搜索音乐
- [ ] 播放音乐
- [ ] 创建歌单
- [ ] 添加音乐到歌单

**社交功能测试**
- [ ] 评论音乐
- [ ] 回复评论
- [ ] 点赞音乐
- [ ] 关注用户
- [ ] 查看关注列表

**管理员功能测试**（需要ADMIN角色）
- [ ] 登录管理员账号
- [ ] 查看仪表板统计
- [ ] 搜索和管理用户
- [ ] 启用/禁用用户
- [ ] 分配角色（MUSICIAN, ADMIN）
- [ ] 查看待审核音乐
- [ ] 批准音乐（状态改为 APPROVED）
- [ ] 拒绝音乐（状态改为 REJECTED）
- [ ] 验证批准后的音乐在公开列表中可见

#### 4. 创建测试管理员账号

如果数据库中没有管理员账号，需要手动添加：

```sql
-- 方法 1: 通过 SQL 添加管理员角色
INSERT INTO user_roles (user_id, role_id)
SELECT 1, id FROM roles WHERE name = 'ROLE_ADMIN';

-- 方法 2: 查看现有用户和角色
SELECT u.id, u.username, u.email, r.name as role
FROM users u
LEFT JOIN user_roles ur ON u.id = ur.user_id
LEFT JOIN roles r ON ur.role_id = r.id;
```

#### 5. 测试指南文档

详细测试步骤请参考：
- 📖 `TESTING_GUIDE.md` - 完整测试指南
- 📖 `PHASE_5_IMPLEMENTATION_COMPLETE.md` - Phase 5 功能说明

### ⚠️ 重要提醒

**Phase 5 重大变更：**
- 音乐上传现在默认为 **PENDING** 状态（需管理员审核）
- 只有 **APPROVED** 状态的音乐才会在公开列表中显示
- 测试时请确保创建管理员账号并审批音乐

## 下一步

### 选项 1：测试当前 MVP（推荐 ⭐⭐⭐⭐⭐）

环境准备完成后：

1. **启动所有服务**
   ```bash
   # 启动 Docker
   docker-compose up -d

   # 启动后端（新终端）
   cd backend && mvn spring-boot:run

   # 启动前端（新终端）
   cd frontend && npm run dev
   ```

2. **完成测试清单**
   - 按上方测试清单逐项测试
   - 记录发现的问题
   - 验证所有核心功能

3. **查看测试指南**
   ```bash
   # 查看详细测试说明
   cat TESTING_GUIDE.md
   ```

### 选项 2：继续开发 Phase 6（不推荐在未测试前）

如果跳过测试直接开发：
- ⚠️ 可能基于未验证的代码继续开发
- ⚠️ 后期发现问题需要回头修复
- ⚠️ 缺少真实数据环境进行新功能开发

### 选项 3：查看下一阶段规划

```bash
# 查看 Phase 6 开发选项
cat NEXT_PHASE_OPTIONS.md

# 查看项目状态
cat PROJECT_STATUS.MD
```

---

## 📊 当前项目状态

**进度：** 100% MVP 完成 ✅
- ✅ Phase 1: 项目初始化
- ✅ Phase 2: 用户管理模块
- ✅ Phase 3: 音乐管理模块
- ✅ Phase 4: 社交互动模块
- ✅ Phase 5: 后台管理模块

**下一阶段：** Phase 6（待选择方向）

推荐方向：
1. 🔍 搜索与发现增强（提升用户体验）
2. 🔔 通知与实时系统（提高用户留存）
3. ⚡ 性能优化与缓存（为规模化做准备）

详见 `NEXT_PHASE_OPTIONS.md` 📖

---

*文档版本：2.0.0*
*创建日期：2026-02-20*
*最后更新：2026-02-20*
*项目状态：MVP 完成，进入测试阶段*
