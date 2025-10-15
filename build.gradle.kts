plugins {
    kotlin("jvm") version "1.9.20"
    application
}

group = "com.memorygame"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    implementation(kotlin("stdlib"))
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.7.3")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-swing:1.7.3")
}

application {
    mainClass.set("com.memorygame.MainKt")
}

tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
    kotlinOptions.jvmTarget = "17"
}

java {
    sourceCompatibility = JavaVersion.VERSION_17
    targetCompatibility = JavaVersion.VERSION_17
}

// Задача для копирования анимаций в resources
tasks.register<Copy>("copyAnimations") {
    from("Animation") {
        include("*.gif")
    }
    into("src/main/resources/animations")
}

// Автоматически копировать анимации перед компиляцией
tasks.named("processResources") {
    dependsOn("copyAnimations")
}

