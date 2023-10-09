import org.springframework.boot.gradle.tasks.bundling.BootJar

val jar: Jar by tasks
val bootJar: BootJar by tasks

bootJar.enabled = false
jar.enabled = true

plugins {
    kotlin("plugin.jpa") version "1.8.22"
}

dependencies {

    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.5.2")
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")
    implementation("org.springframework.boot:spring-boot-starter")
    testImplementation("org.springframework.boot:spring-boot-starter-test")

    implementation("org.springframework.boot:spring-boot-starter-data-redis")

    /* MySQL */
    runtimeOnly("com.mysql:mysql-connector-j")
    implementation("mysql:mysql-connector-java:8.0.32")

    /* SAGA */
    implementation("org.springframework.kafka:spring-kafka")
    implementation("org.apache.kafka:kafka-clients:3.4.0")

    implementation(project(":domain"))
    implementation(project(":util"))
    implementation(project(":application"))
}
