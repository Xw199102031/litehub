# LiteHub

LiteHub 是一个基于 Quarkus + Gradle + Java 21 的项目协作与权限管理后端服务。它提供用户注册/登录、项目管理、任务管理、基于角色（RBAC）和 Casbin 策略的细粒度权限控制，以及带有审计日志的 Admin Console。

## 功能特性

- **用户认证**：基于 JWT 的注册、登录与接口鉴权（使用 RSA 公钥验签）。
- **项目管理**：创建项目、管理项目成员与角色。
- **任务管理**：在项目中创建、更新、分配任务（受项目角色权限控制）。
- **RBAC 权限**：预置 `OWNER / MAINTAINER / MEMBER / VIEWER / ADMIN` 角色，通过 `role_permission` 映射控制项目级权限。
- **Casbin 策略引擎**：支持动态加载、新增、删除策略，并可热重载 Enforcer。
- **审计日志**：记录策略变更等操作的时间、操作人、IP、UA 与详情。
- **Admin Console**：基于纯 HTML/JS 的响应式管理后台，可在浏览器中管理策略与审计日志。
- **数据库迁移**：使用 Flyway 自动执行版本化 SQL 迁移。

## 技术栈

| 组件 | 版本 / 说明 |
|------|------------|
| Java | 21 |
| 构建工具 | Gradle（Kotlin DSL） |
| 框架 | Quarkus 3.15.4 |
| REST | RESTEasy Reactive + JSON-B |
| 安全 | SmallRye JWT + bcrypt |
| 数据库 | MySQL 8（quarkus-jdbc-mysql） |
| ORM / 迁移 | Hibernate ORM Panache + Flyway |
| 缓存 | Caffeine（项目权限缓存） |
| 权限引擎 | Casbin（jcasbin） |
| 容器化 | Dockerfile（openjdk:21-jdk 构建，eclipse-temurin:21-jre 运行） |

## 目录结构

```
litehub/
├── build.gradle.kts              # Gradle 构建配置
├── settings.gradle.kts           # Gradle 项目设置
├── gradle.properties             # Gradle JVM 参数
├── Dockerfile                    # 容器构建与运行镜像
├── README.md                     # 本说明文档
├── docs/                         # 设计/方案文档
│   ├── casbin.md                 # Casbin 集成说明
│   ├── casbin-db-audit.md        # 数据库存储策略与审计说明
│   └── litehub-rbac-erd.mmd      # RBAC 实体关系图
├── src/
│   ├── main/
│   │   ├── java/com/litehub/
│   │   │   ├── LiteHubApplication.java      # JAX-RS Application，根路径 /api
│   │   │   ├── GreetingResource.java        # 示例接口 /api/hello
│   │   │   ├── HealthResource.java          # 健康检查 /api/health
│   │   │   ├── auth/
│   │   │   │   ├── AuthorizationService.java # 项目级权限校验与缓存
│   │   │   │   └── CasbinService.java        # Casbin Enforcer 管理
│   │   │   ├── exception/
│   │   │   │   ├── ApiException.java
│   │   │   │   └── ApiResponse.java          # 统一 API 响应封装
│   │   │   ├── model/             # JPA / Panache 实体
│   │   │   ├── repository/        # 数据访问层
│   │   │   ├── resource/          # REST 接口层
│   │   │   │   ├── AdminResource.java
│   │   │   │   └── CasbinResource.java
│   │   │   └── service/           # 业务逻辑层
│   │   │       ├── AdminService.java
│   │   │       ├── AuditLogService.java
│   │   │       └── UserService.java
│   │   └── resources/
│   │       ├── application.properties       # 应用配置
│   │       ├── keys/public.pem              # JWT 公钥（仅验签）
│   │       ├── casbin/
│   │       │   ├── model.conf               # Casbin 模型定义
│   │       │   └── policy.csv               # 示例策略文件
│   │       ├── db/migration/                # Flyway 迁移脚本
│   │       └── META-INF/resources/admin/
│   │           └── permissions.html         # Admin Console 页面
│   └── test/java/com/litehub/   # 单元与集成测试
└── gradle/wrapper/               # Gradle Wrapper
```

## 环境要求

- JDK 21+
- MySQL 8.0+
-（可选）Docker 20.10+

## 配置说明

主要配置位于 [`src/main/resources/application.properties`](src/main/resources/application.properties)，可通过环境变量覆盖。

| 配置项 | 默认值 | 说明 | 环境变量覆盖 |
|--------|--------|------|-------------|
| `quarkus.http.port` | 8080 | HTTP 服务端口 | - |
| `quarkus.datasource.db-kind` | mysql | 数据库类型 | - |
| `quarkus.datasource.username` | root | 数据库用户名 | `QUARKUS_DATASOURCE_USERNAME` |
| `quarkus.datasource.password` | `nstnhFpBuiZcQ0OM/N3clw==` | 数据库密码 | `QUARKUS_DATASOURCE_PASSWORD` |
| `quarkus.datasource.jdbc.url` | `jdbc:mysql://10.8.0.1:23306/litehub` | JDBC URL | `QUARKUS_DATASOURCE_JDBC_URL` |
| `quarkus.hibernate-orm.database.generation` | none | 禁用 Hibernate 自动建表，由 Flyway 管理 | - |
| `quarkus.flyway.migrate-at-start` | true | 启动时自动执行数据库迁移 | - |
| `mp.jwt.verify.publickey.location` | classpath:keys/public.pem | JWT 公钥位置 | - |
| `mp.jwt.verify.issuer` | litehub | JWT issuer 校验值 | - |
| `casbin.policy.path` | config/casbin/policy.csv | Casbin 策略文件路径 | `CASBIN_POLICY_PATH` |

### 数据库准备

启动应用前，确保 MySQL 中已创建数据库：

```sql
CREATE DATABASE litehub CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
```

### JWT 公钥

项目使用公钥验签 JWT。token 需要由外部签发服务生成，签发时使用的私钥与 `src/main/resources/keys/public.pem` 对应。生产环境请替换为自有密钥对。

## 本地开发

### 1. 编译与测试

```bash
./gradlew build
```

跳过测试构建：

```bash
./gradlew build -x test
```

### 2. 开发模式（热加载）

```bash
./gradlew quarkusDev
```

服务启动后访问：

- 应用首页：`http://localhost:8080`
- 健康检查：`http://localhost:8080/api/health`
- OpenAPI：`http://localhost:8080/q/openapi`
- Admin Console：`http://localhost:8080/admin/permissions.html`

### 3. 运行测试

```bash
./gradlew test
```

## 部署步骤

### 方式一：直接运行

```bash
# 1. 构建可运行 jar
./gradlew -x test build

# 2. 运行 Quarkus 应用
java -jar build/quarkus-app/quarkus-run.jar
```

### 方式二：Docker 部署

```bash
# 构建镜像
docker build -t litehub:latest .

# 运行容器（通过环境变量覆盖数据库配置）
docker run -d \
  -p 8080:8080 \
  -e QUARKUS_DATASOURCE_JDBC_URL=jdbc:mysql://mysql:3306/litehub \
  -e QUARKUS_DATASOURCE_USERNAME=root \
  -e QUARKUS_DATASOURCE_PASSWORD=your_password \
  --name litehub \
  litehub:latest
```

### 生产建议

- 使用独立、高可用的 MySQL 实例。
- 将 `quarkus.hibernate-orm.log.sql` 设置为 `false`，避免日志中泄露 SQL。
- 替换 `public.pem` 为生产密钥对，并妥善保管私钥。
- 使用持久化 Casbin Adapter（如 JDBC Adapter）替代文件策略，确保多实例策略一致。
- Admin Console 仅作为便利演示，生产环境应增加 VPN / IP 白名单 / MFA 等额外防护。

## 核心接口速览

所有接口统一返回 `ApiResponse` 结构（`{ "code": ..., "message": ..., "data": ... }`）。

### 公开接口

- `GET /api/hello` — 示例接口
- `GET /api/health` — 健康检查
- `POST /api/auth/register` — 用户注册
- `POST /api/auth/login` — 用户登录，返回 JWT

### 项目与任务

- `POST /api/projects` — 创建项目
- `POST /api/projects/{id}/members` — 添加项目成员并分配角色
- `POST /api/tasks` — 创建任务（受项目角色权限控制）

### Admin 接口（需要 `ADMIN` 角色）

- `GET /api/admin/roles` / `POST /api/admin/roles` / `DELETE /api/admin/roles/{name}` — 角色管理
- `GET /api/admin/permissions` / `POST /api/admin/permissions` — 权限管理
- `POST /api/admin/roles/{roleName}/permissions` / `DELETE /api/admin/roles/{roleName}/permissions/{permission}` — 角色-权限关联
- `POST /api/admin/users/{userId}/roles` / `DELETE /api/admin/users/{userId}/roles/{role}` — 用户-角色关联
- `POST /api/admin/projects/{projectId}/roles` / `DELETE /api/admin/projects/{projectId}/roles/{userId}` — 项目-成员角色
- `GET /api/admin/casbin/policies` — 列出 Casbin 策略
- `POST /api/admin/casbin/policies` — 新增策略
- `DELETE /api/admin/casbin/policies?subject=&object=&action=` — 删除策略
- `POST /api/admin/casbin/reload` — 热重载策略
- `GET /api/admin/audit-logs?page=&size=&userId=&action=` — 审计日志查询

## Admin Console 使用

1. 通过 `/api/auth/login` 获取具有 `ADMIN` 角色的用户 JWT。
2. 在浏览器控制台写入 token：

   ```js
   localStorage.setItem('litehub_token', '<YOUR_JWT_TOKEN>');
   ```

3. 打开 `http://localhost:8080/admin/permissions.html`。
4. 在 **Policies** 标签页中新增/删除策略，或点击 `Reload` 热重载 Enforcer。
5. 在 **Audit Logs** 标签页中按用户或操作类型过滤审计日志。

> Admin Console 为静态页面，当前通过浏览器 `localStorage` 携带 token；生产环境建议增加页面级认证。

## 注意事项

- 数据库表结构由 Flyway 管理，首次启动会自动执行 `db/migration` 下的脚本。
- 私有 JWT 签名密钥不应提交到仓库；`.gitignore` 已排除 `/keys/` 目录。
- 默认 `application.properties` 中的数据库连接指向本地开发环境，部署前请通过环境变量覆盖。
