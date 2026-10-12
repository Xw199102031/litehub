# litehub (Quarkus + Gradle + Java 21)

快速启动：

1. 本地开发（热重载）:
   ./gradlew quarkusDev

2. 打包：
   ./gradlew build
   java -jar build/quarkus-app/quarkus-run.jar

3. Docker：
   docker build -t litehub:latest .
   docker run -p 8080:8080 litehub:latest

常见端点：
- GET /api/hello
- OpenAPI: /openapi

说明：
- Quarkus 版本与插件版本请在 build.gradle.kts 中替换 REPLACE_WITH_QUARKUS_BOM_VERSION 和 REPLACE_WITH_QUARKUS_PLUGIN_VERSION
