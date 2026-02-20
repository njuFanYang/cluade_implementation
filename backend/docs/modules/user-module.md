# 用户管理模块文档

## 📋 模块概述

用户管理模块负责处理用户认证、授权和个人信息管理，是整个系统的核心基础模块。

### 功能描述

- 用户注册（邮箱/用户名）
- 用户登录（JWT 认证）
- 个人信息查看与编辑
- 密码修改
- 基于角色的权限控制（RBAC）

### 业务场景

1. **新用户注册**：用户通过邮箱或用户名注册账号
2. **用户登录**：通过用户名/邮箱 + 密码登录，获取 JWT Token
3. **个人中心**：查看和编辑个人资料
4. **权限管理**：根据用户角色控制功能访问

### 依赖关系

- **被依赖**：所有其他模块都依赖用户模块进行身份验证
- **依赖**：无（基础模块）

---

## 🗄️ 数据模型

### User 实体类

| 字段 | 类型 | 说明 | 约束 |
|------|------|------|------|
| id | Long | 主键 | AUTO_INCREMENT |
| username | String(20) | 用户名 | UNIQUE, NOT NULL |
| email | String(100) | 邮箱 | UNIQUE, NOT NULL |
| password | String(100) | 密码（加密） | NOT NULL |
| nickname | String(50) | 昵称 | |
| avatar | String(500) | 头像 URL | |
| bio | String(500) | 个人简介 | |
| gender | String(1) | 性别 | M/F/O/U |
| phone | String(20) | 手机号 | |
| location | String(100) | 地区 | |
| enabled | Boolean | 账户状态 | DEFAULT true |
| locked | Boolean | 锁定状态 | DEFAULT false |
| followersCount | Integer | 粉丝数 | DEFAULT 0 |
| followingCount | Integer | 关注数 | DEFAULT 0 |
| musicCount | Integer | 上传音乐数 | DEFAULT 0 |
| playlistCount | Integer | 歌单数 | DEFAULT 0 |
| lastLoginTime | DateTime | 最后登录时间 | |
| lastLoginIp | String(50) | 最后登录IP | |
| createdAt | DateTime | 创建时间 | AUTO |
| updatedAt | DateTime | 更新时间 | AUTO |

### Role 实体类

| 字段 | 类型 | 说明 | 约束 |
|------|------|------|------|
| id | Long | 主键 | AUTO_INCREMENT |
| name | String(50) | 角色名称 | UNIQUE, NOT NULL |
| displayName | String(50) | 显示名称 | |
| description | String(200) | 角色描述 | |
| createdAt | DateTime | 创建时间 | AUTO |

### 关联关系

- **User ↔ Role**: 多对多关系
  - 中间表：`user_roles`
  - 字段：`user_id`, `role_id`

---

## 🔌 API 接口

### 1. 用户注册

**接口**：`POST /api/auth/register`

**权限**：公开

**请求体**：
```json
{
  "username": "john_doe",
  "email": "john@example.com",
  "password": "password123",
  "confirmPassword": "password123",
  "nickname": "John Doe"
}
```

**响应**：
```json
{
  "code": 200,
  "message": "Registration successful",
  "data": {
    "id": 1,
    "username": "john_doe",
    "email": "john@example.com",
    "nickname": "John Doe",
    "roles": ["ROLE_USER"],
    "createdAt": "2026-02-20T10:00:00"
  },
  "timestamp": 1707987654321
}
```

**错误响应**：
- `1002`: 用户名已存在
- `1007`: 邮箱已存在
- `9001`: 验证错误（密码不匹配等）

---

### 2. 用户登录

**接口**：`POST /api/auth/login`

**权限**：公开

**请求体**：
```json
{
  "usernameOrEmail": "john_doe",
  "password": "password123",
  "rememberMe": false
}
```

**响应**：
```json
{
  "code": 200,
  "message": "Login successful",
  "data": {
    "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
    "tokenType": "Bearer",
    "expiresIn": 86400000,
    "userInfo": {
      "id": 1,
      "username": "john_doe",
      "email": "john@example.com",
      "roles": ["ROLE_USER"]
    }
  },
  "timestamp": 1707987654321
}
```

**错误响应**：
- `1001`: 用户不存在
- `1003`: 用户名或密码错误
- `1010`: 账户已被禁用

---

### 3. 获取当前用户信息

**接口**：`GET /api/users/profile`

**权限**：需要登录

**请求头**：
```
Authorization: Bearer <token>
```

**响应**：
```json
{
  "code": 200,
  "message": "Success",
  "data": {
    "id": 1,
    "username": "john_doe",
    "email": "john@example.com",
    "nickname": "John Doe",
    "avatar": "https://example.com/avatar.jpg",
    "bio": "Music lover",
    "gender": "M",
    "location": "New York",
    "roles": ["ROLE_USER"],
    "followersCount": 100,
    "followingCount": 50,
    "createdAt": "2026-02-20T10:00:00"
  }
}
```

---

### 4. 更新用户信息

**接口**：`PUT /api/users/profile`

**权限**：需要登录

**请求体**：
```json
{
  "nickname": "John Smith",
  "bio": "Music enthusiast",
  "gender": "M",
  "location": "Los Angeles"
}
```

**响应**：与获取用户信息相同

---

### 5. 修改密码

**接口**：`PUT /api/users/password`

**权限**：需要登录

**请求体**：
```json
{
  "oldPassword": "password123",
  "newPassword": "newpassword123",
  "confirmPassword": "newpassword123"
}
```

**响应**：
```json
{
  "code": 200,
  "message": "Password changed successfully",
  "data": null
}
```

**错误响应**：
- `1011`: 旧密码错误
- `9001`: 新密码与确认密码不匹配

---

### 6. 获取用户信息（按ID）

**接口**：`GET /api/users/{id}`

**权限**：公开

**响应**：与获取当前用户信息类似（不包含敏感信息如邮箱）

---

## ⚙️ 业务逻辑

### 注册流程

```
1. 接收注册请求
   ↓
2. 验证用户名/邮箱是否已存在
   ↓
3. 验证密码格式和一致性
   ↓
4. 加密密码（BCrypt）
   ↓
5. 创建用户记录
   ↓
6. 分配默认角色（ROLE_USER）
   ↓
7. 返回用户信息
```

### 登录流程

```
1. 接收登录请求（用户名/邮箱 + 密码）
   ↓
2. 查找用户记录
   ↓
3. 验证密码（BCrypt）
   ↓
4. 检查账户状态（启用/禁用）
   ↓
5. 生成 JWT Token
   ↓
6. 更新最后登录时间和IP
   ↓
7. 返回 Token 和用户信息
```

### 权限控制

**角色定义：**
- `ROLE_USER`: 普通用户，基本功能
- `ROLE_MUSICIAN`: 音乐人，可上传音乐
- `ROLE_ADMIN`: 管理员，全部权限

**权限检查机制：**
1. JWT Token 验证
2. Spring Security 角色检查
3. 方法级别权限控制（@PreAuthorize）

---

## 🔒 安全设计

### 密码安全

- **加密算法**：BCrypt（自动加盐）
- **密码强度**：至少 8 位，包含字母和数字
- **传输安全**：HTTPS 加密传输

### JWT 认证

- **算法**：HMAC-SHA256
- **过期时间**：24 小时（可配置）
- **Remember Me**：30 天过期
- **Token 刷新**：前端检测过期后重新登录

### 防护措施

- SQL 注入防护（JPA 参数化查询）
- XSS 防护（前端输入验证）
- CSRF 防护（Stateless JWT，已禁用）
- 暴力破解防护（TODO：登录失败次数限制）

---

## 🧪 测试要点

### 单元测试

1. **UserService 测试**
   - 注册成功/失败场景
   - 登录成功/失败场景
   - 密码修改场景
   - 用户信息更新

2. **UserRepository 测试**
   - 按用户名查询
   - 按邮箱查询
   - 唯一性约束测试

3. **JWT Token 测试**
   - Token 生成
   - Token 验证
   - Token 过期处理

### 集成测试

1. **完整注册流程**
2. **完整登录流程**
3. **带 Token 的 API 访问**
4. **无 Token 的受保护 API 访问（应拒绝）**

---

## 📝 注意事项

1. **密码安全**
   - 永远不要记录用户密码
   - 密码字段在 DTO 中不应返回

2. **Token 管理**
   - Token 存储在客户端（localStorage/sessionStorage）
   - 不要在 URL 中传递 Token

3. **账户状态**
   - 禁用的账户无法登录
   - 锁定的账户需要管理员解锁

4. **数据验证**
   - 前后端都要进行数据验证
   - 使用 Jakarta Validation 注解

5. **性能优化**
   - 用户信息可以缓存（Redis）
   - 角色关系使用 EAGER 加载
   - 分页查询用户列表

---

## 🔄 版本历史

| 版本 | 日期 | 说明 |
|------|------|------|
| 1.0.0 | 2026-02-20 | 初始版本，实现基本用户管理功能 |

---

*文档版本：1.0.0*
*创建日期：2026-02-20*
*最后更新：2026-02-20*
