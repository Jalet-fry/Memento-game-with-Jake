plugins {
    kotlin("jvm") version "1.9.20"
    application
    jacoco
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
    
    // Тестовые зависимости
    testImplementation("org.junit.jupiter:junit-jupiter:5.10.0")
    testImplementation("org.junit.jupiter:junit-jupiter-api:5.10.0")
    testImplementation("org.junit.jupiter:junit-jupiter-engine:5.10.0")
    testImplementation("org.jetbrains.kotlin:kotlin-test:1.9.20")
    testImplementation("io.mockk:mockk:1.13.8")
    testImplementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:1.7.3")
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

// Конфигурация тестов
tasks.test {
    useJUnitPlatform()
    testLogging {
        events("passed", "skipped", "failed")
        exceptionFormat = org.gradle.api.tasks.testing.logging.TestExceptionFormat.FULL
    }
    finalizedBy(tasks.jacocoTestReport) // Запускать отчет о покрытии после тестов
}

// Конфигурация JaCoCo для анализа покрытия кода
jacoco {
    toolVersion = "0.8.8"
}

tasks.jacocoTestReport {
    dependsOn(tasks.test) // Запускать только после тестов
    reports {
        xml.required.set(true)
        html.required.set(true)
        csv.required.set(false)
    }
    finalizedBy(tasks.jacocoTestCoverageVerification)
    finalizedBy("openJacocoReport") // Автоматически открывать отчет
}

tasks.jacocoTestCoverageVerification {
    violationRules {
        rule {
            limit {
                minimum = "0.60".toBigDecimal() // Минимум 60% покрытия
            }
        }
    }
}

// Задача для автоматического открытия JaCoCo отчета
tasks.register<Exec>("openJacocoReport") {
    group = "reporting"
    description = "Открывает HTML отчет JaCoCo в браузере"
    
    // Определяем команду в зависимости от операционной системы
    if (System.getProperty("os.name").lowercase().contains("windows")) {
        commandLine("cmd", "/c", "start", "", "build/reports/jacoco/test/html/index.html")
    } else if (System.getProperty("os.name").lowercase().contains("mac")) {
        commandLine("open", "build/reports/jacoco/test/html/index.html")
    } else {
        commandLine("xdg-open", "build/reports/jacoco/test/html/index.html")
    }
    
    // Задача выполняется только если отчет существует
    onlyIf { 
        file("build/reports/jacoco/test/html/index.html").exists() 
    }
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

// Настройка для создания исполняемого JAR
tasks.named<Jar>("jar") {
    manifest {
        attributes["Main-Class"] = "com.memorygame.MainKt"
    }
    
    // Включаем все зависимости в JAR
    from(configurations.runtimeClasspath.get().map { if (it.isDirectory) it else zipTree(it) })
    
    // Избегаем дублирования файлов
    duplicatesStrategy = DuplicatesStrategy.EXCLUDE
    
    archiveBaseName.set("MemoryGame")
    archiveVersion.set("1.0")
}

// Создаем задачу для сборки готового приложения
tasks.register<Jar>("fatJar") {
    group = "build"
    description = "Создает исполняемый JAR файл с зависимостями"
    
    manifest {
        attributes["Main-Class"] = "com.memorygame.MainKt"
        attributes["Implementation-Title"] = "Memory Game with Jake"
        attributes["Implementation-Version"] = "1.0"
    }
    
    archiveBaseName.set("MemoryGame")
    archiveVersion.set("1.0")
    
    from(sourceSets.main.get().output)
    dependsOn(configurations.runtimeClasspath)
    from({
        configurations.runtimeClasspath.get().filter { it.name.endsWith("jar") }.map { zipTree(it) }
    })
    
    duplicatesStrategy = DuplicatesStrategy.EXCLUDE
}

