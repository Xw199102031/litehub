# litehub (Quarkus + Gradle + Java 21)

这是一个 Quarkus 的基础后端骨架，适合在 LiteHub 项目中继续扩展出认证、用户、项目、任务等模块。

## 目录结构

- `src/main/java/com/litehub`：应用代码
  - `GreetingResource.java`：示例 REST 接口
  - `HealthResource.java`：健康检查接口
- `src/main/resources/application.properties`：应用配置
- `src/test/java`：测试代码

## 本地运行

1. 确认已安装 JDK 21 与 Gradle
2. 启动开发模式：
   ```bash
   ./gradlew quarkusDev
   ```
3. 访问：
   - http://localhost:8080/api/hello
   - http://localhost:8080/api/health
   - http://localhost:8080/q/openapi

## 打包

```bash
./gradlew build
java -jar build/quarkus-app/quarkus-run.jar
```

## Docker

```bash
docker build -t litehub:latest .
docker run -p 8080:8080 litehub:latest
```

## 说明

- 依赖已经包含：Quarkus、REST、OpenAPI、Panache、PostgreSQL、Docker
- 当前是基础框架，后续可以继续扩展：
  - 用户认证 / JWT
  - 任务管理
  - 项目/仓库模块
  - WebSocket / 事件推送
  - Redis / Kafka / Elasticsearch
  - PostgreSQL schema migration

## 端点示例

- `GET /api/hello`：返回欢迎信息
- `GET /api/health`：返回应用健康状态
- `GET /q/openapi`：OpenAPI 文档
