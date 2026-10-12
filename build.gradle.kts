import org.gradle.process.CommandLineArgumentProvider

plugins {
    id("java")
    id("io.quarkus") version "3.15.4"
}

group = "com.litehub"
version = "0.1.0-SNAPSHOT"

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(21))
    }
}

repositories {
    mavenCentral()
}

dependencies {
    implementation(enforcedPlatform("io.quarkus.platform:quarkus-bom:3.15.4"))
    implementation("io.quarkus:quarkus-arc")
    implementation("io.quarkus:quarkus-resteasy-reactive")
    implementation("io.quarkus:quarkus-resteasy-reactive-jsonb")
    implementation("io.quarkus:quarkus-smallrye-openapi")
    implementation("io.quarkus:quarkus-smallrye-jwt")
    implementation("io.quarkus:quarkus-hibernate-orm-panache")
    implementation("io.quarkus:quarkus-jdbc-mysql")
    implementation("io.quarkus:quarkus-container-image-docker")
    implementation("io.quarkus:quarkus-flyway")
    implementation("io.quarkus:quarkus-hibernate-validator")

    implementation("io.jsonwebtoken:jjwt-api:0.11.5")
    runtimeOnly("io.jsonwebtoken:jjwt-impl:0.11.5")
    runtimeOnly("io.jsonwebtoken:jjwt-jackson:0.11.5")
    implementation("at.favre.lib:bcrypt:0.9.0")

    // MySQL driver
    runtimeOnly("com.mysql:mysql-connector-j:8.1.0")

    // Caffeine cache for authorization caching
    implementation("com.github.ben-manes.caffeine:caffeine:3.1.8")

    // Casbin
    implementation("org.casbin:jcasbin:1.20.0")

    testImplementation("io.quarkus:quarkus-junit5")
    testImplementation("io.rest-assured:rest-assured")
    testImplementation("io.quarkus:quarkus-jdbc-h2")
}

tasks.withType<JavaCompile>().configureEach {
    options.encoding = "UTF-8"
}

// JVMs launched by the build default their stdout/stderr to the Windows native code page (GBK),
// which garbles the localised (zh_CN) JDK messages when the terminal is UTF-8. Force UTF-8 for
// every forked JVM, in addition to the Gradle daemon that is configured in gradle.properties.
val utf8ConsoleArgs = listOf("-Dfile.encoding=UTF-8", "-Dstdout.encoding=UTF-8", "-Dstderr.encoding=UTF-8")

tasks.withType<Test>().configureEach {
    useJUnitPlatform()
    jvmArgumentProviders.add(CommandLineArgumentProvider { utf8ConsoleArgs })
}

tasks.withType<io.quarkus.gradle.tasks.QuarkusDev>().configureEach {
    jvmArguments.addAll(utf8ConsoleArgs)
}

tasks.withType<io.quarkus.gradle.tasks.QuarkusRun>().configureEach {
    jvmArguments.addAll(utf8ConsoleArgs)
}
