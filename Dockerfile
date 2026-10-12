# Build stage
FROM openjdk:21-jdk AS build
WORKDIR /workspace
COPY . .
RUN ./gradlew -x test build --no-daemon

# Run stage
FROM eclipse-temurin:21-jre
WORKDIR /app
# For Quarkus Gradle builds the runnable jar is usually at build/quarkus-app/quarkus-run.jar
COPY --from=build /workspace/build/quarkus-app/quarkus-run.jar /app/app.jar
EXPOSE 8080
CMD ["java", "-jar", "/app/app.jar"]
