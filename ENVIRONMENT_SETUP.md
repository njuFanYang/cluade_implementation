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

## 下一步

环境准备完成后，可以：

1. **启动 Docker 服务**
   ```bash
   docker-compose up -d
   ```

2. **初始化后端项目**
   ```bash
   cd backend
   mvn clean install
   ```

3. **初始化前端项目**
   ```bash
   cd frontend
   npm install
   ```

4. **开始开发** 🎉

---

*文档版本：1.0.0*
*创建日期：2026-02-20*
*最后更新：2026-02-20*
