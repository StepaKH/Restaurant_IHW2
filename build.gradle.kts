import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import org.gradle.api.JavaVersion

plugins {
    id("org.springframework.boot") version "3.2.3"
    id("io.spring.dependency-management") version "1.1.4"
    kotlin("plugin.spring") version "1.9.22"
    kotlin("jvm") version "1.9.22"
}

group = "org.example"
version = "1.0-SNAPSHOT"

java {
    sourceCompatibility = JavaVersion.VERSION_17
}

repositories {
    mavenCentral()
}

dependencies {
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
    implementation("org.jetbrains.kotlin:kotlin-reflect")
    implementation("com.google.code.gson:gson:2.10.1")
    implementation("org.mindrot:jbcrypt:0.4")
    implementation("org.springdoc:springdoc-openapi-starter-webmvc-ui:2.3.0")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.5.0")
    testImplementation("org.jetbrains.kotlin:kotlin-test")
}

tasks.withType<KotlinCompile> {
    kotlinOptions {
        freeCompilerArgs += "-Xjsr305=strict"
        jvmTarget = "17"
    }
}

tasks.test {
    useJUnitPlatform()
}
kotlin {
    jvmToolchain(21)
}