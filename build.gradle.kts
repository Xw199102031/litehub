import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    id("java")
    id("io.quarkus") version "REPLACE_WITH_QUARKUS_PLUGIN_VERSION"
}

group = "com.litehub"
version = "0.1.0-SNAPSHOT"
java.sourceCompatibility = JavaVersion.VERSION_21

repositories {
    mavenCentral()
}

dependencies {
    implementation(enforcedPlatform("io.quarkus:quarkus-bom:REPLACE_WITH_QUARKUS_BOM_VERSION"))
    implementation("io.quarkus:quarkus-resteasy-reactive")
    implementation("io.quarkus:quarkus-smallrye-openapi")
    implementation("io.quarkus:quarkus-hibernate-orm-panache")
    implementation("io.quarkus:quarkus-jdbc-postgresql")
    implementation("io.quarkus:quarkus-container-image-docker")

    testImplementation("io.quarkus:quarkus-junit5")
    testImplementation("io.rest-assured:rest-assured")
}

tasks.withType<JavaCompile> {
    options.encoding = "UTF-8"
}

quarkus {
    setOutputDirectory(layout.buildDirectory.dir("quarkus-app"))
}
