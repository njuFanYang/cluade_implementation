# MusicShare 后端项目

## 📋 项目概述

本项目是音乐共享平台的后端服务，基于 **Spring Boot 3.x** 构建，提供 RESTful API 接口。

---

## 🛠️ 技术栈

| 技术 | 版本 | 说明 |
|------|------|------|
| **Java** | 17 | 编程语言 |
| **Spring Boot** | 3.2.2 | 应用框架 |
| **Spring Security** | 6.x | 安全框架 |
| **Spring Data JPA** | 3.x | 数据访问 |
| **MySQL** | 8.x | 关系数据库 |
| **Redis** | 7.x | 缓存数据库 |
| **JWT** | 0.12.3 | 身份认证 |
| **MyBatis Plus** | 3.5.5 | ORM 增强 |
| **SpringDoc** | 2.3.0 | API 文档 |
| **Lombok** | 最新 | 代码简化 |

---

## 📁 项目结构

```
backend/
├── src/
│   ├── main/
│   │   ├── java/com/musicshare/
│   │   │   ├── MusicShareApplication.java    # 应用入口
│   │   │   ├── config/                       # 配置类
│   │   │   │   ├── CorsConfig.java          # CORS 配置
│   │   │   │   └── SwaggerConfig.java       # Swagger 配置
│   │   │   ├── controller/                   # 控制器层
│   │   │   ├── service/                      # 业务逻辑层
│   │   │   │   └── impl/                     # 实现类
│   │   │   ├── repository/                   # 数据访问层
│   │   │   ├── entity/                       # 实体类
│   │   │   ├── dto/                          # 数据传输对象
│   │   │   │   ├── request/                  # 请求 DTO
│   │   │   │   └── response/                 # 响应 DTO
│   │   │   │       └── ApiResponse.java      # 统一响应格式
│   │   │   ├── security/                     # 安全相关
│   │   │   ├── exception/                    # 异常处理
│   │   │   │   ├── BusinessException.java    # 业务异常
│   │   │   │   ├── ErrorCode.java            # 错误码
│   │   │   │   └── GlobalExceptionHandler.java # 全局异常处理
│   │   │   └── util/                         # 工具类
│   │   └── resources/
│   │       ├── application.yml               # 主配置文件
│   │       ├── application-dev.yml           # 开发环境配置
│   │       ├── application-prod.yml          # 生产环境配置
│   │       ├── db/migration/                 # 数据库脚本
│   │       ├── static/                       # 静态资源
│   │       └── templates/                    # 模板文件
│   └── test/                                 # 测试代码
├── docs/                                     # 文档目录
│   ├── README.md                             # 本文件
│   ├── DATABASE_DESIGN.md                    # 数据库设计文档
│   ├── API_DESIGN.md                         # API 设计文档
│   └── modules/                              # 模块文档
└── pom.xml                                   # Maven 配置
```

---

## 🚀 快速开始

### 前置要求

- ✅ JDK 17 或更高版本
- ✅ Maven 3.6 或更高版本
- ✅ MySQL 8.x
- ✅ Redis 7.x
- ✅ Docker（推荐，用于运行数据库）

### 1. 启动数据库服务

```bash
# 在项目根目录运行
docker-compose up -d

# 验证服务状态
docker-compose ps
```

### 2. 编译项目

```bash
cd backend
mvn clean install
```

### 3. 运行应用

```bash
# 使用 Maven
mvn spring-boot:run

# 或直接运行 JAR
java -jar target/musicshare-backend-1.0.0.jar
```

### 4. 访问服务

- **应用地址**: http://localhost:8081
- **API 文档**: http://localhost:8081/swagger-ui.html
- **健康检查**: http://localhost:8081/actuator/health

---

## 🔧 配置说明

### 环境配置

通过 `spring.profiles.active` 切换环境：

```yaml
# application.yml
spring:
  profiles:
    active: dev  # dev / prod
```

### 数据库配置

开发环境配置（`application-dev.yml`）：

```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/musicshare
    username: musicshare
    password: musicshare123
```

### Redis 配置

```yaml
spring:
  data:
    redis:
      host: localhost
      port: 6379
      password: redis123456
```

### JWT 配置

```yaml
jwt:
  secret: your-secret-key
  expiration: 86400000  # 24 hours
```

### 文件上传配置

```yaml
file:
  upload-dir: ./uploads
  max-music-size: 104857600  # 100MB
  max-image-size: 5242880    # 5MB
```

---

## 📝 开发指南

### 代码规范

- 遵循阿里巴巴 Java 开发规范
- 所有类和公共方法必须有 JavaDoc 注释
- 使用 Lombok 减少样板代码
- 统一使用 `ApiResponse` 包装响应

### 统一响应格式

```java
// 成功响应
return ApiResponse.success(data);

// 错误响应
throw new BusinessException(ErrorCode.USER_NOT_FOUND);
```

### 异常处理

```java
// 抛出业务异常
if (user == null) {
    throw new BusinessException(ErrorCode.USER_NOT_FOUND);
}

// 全局异常处理器会自动捕获并格式化
```

### API 文档注解

```java
@RestController
@RequestMapping("/api/users")
@Tag(name = "User", description = "User management APIs")
public class UserController {

    @Operation(summary = "Get user by ID")
    @GetMapping("/{id}")
    public ApiResponse<UserResponse> getUserById(@PathVariable Long id) {
        // ...
    }
}
```

---

## 🗄️ 数据库

### 初始化

首次运行时，JPA 会自动创建表结构（`ddl-auto: update`）。

也可以手动执行 SQL 脚本：

```bash
docker exec -it musicshare-mysql mysql -u musicshare -pmusicshare123 musicshare < sql/init/schema.sql
```

### 访问数据库

```bash
# 使用 Docker
docker exec -it musicshare-mysql mysql -u musicshare -pmusicshare123 musicshare

# 使用 phpMyAdmin
open http://localhost:8080
```

---

## 🧪 测试

### 运行测试

```bash
# 运行所有测试
mvn test

# 运行特定测试类
mvn test -Dtest=UserServiceTest
```

---

## 📚 相关文档

- [数据库设计文档](./docs/DATABASE_DESIGN.md)
- [API 设计文档](./docs/API_DESIGN.md)
- [用户模块文档](./docs/modules/user-module.md)

---

## 🐛 常见问题

### Q: 启动失败，提示数据库连接错误

A: 确保 Docker 服务已启动，运行 `docker-compose ps` 检查 MySQL 状态

### Q: Redis 连接失败

A: 检查 Redis 密码配置是否正确，确保 Docker 中的 Redis 容器正在运行

### Q: 端口被占用

A: 修改 `application.yml` 中的 `server.port` 配置

---

## 📞 技术支持

如有问题，请查看：
1. [Swagger API 文档](http://localhost:8081/swagger-ui.html)
2. [Spring Boot 官方文档](https://spring.io/projects/spring-boot)
3. 项目日志文件

---

*文档版本：1.0.0*
*创建日期：2026-02-20*
*最后更新：2026-02-20*
