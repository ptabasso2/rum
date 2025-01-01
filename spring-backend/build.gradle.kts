plugins {
    java
    id("org.springframework.boot") version "3.4.0"
    id("io.spring.dependency-management") version "1.1.6"
}

group = "com.datadoghq.pej"
version = "0.0.1-SNAPSHOT"

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(17)
    }
}

repositories {
    mavenCentral()
}

dependencies {
    implementation("org.springframework.boot:spring-boot-starter-web")
}

tasks {
    jar {
        // Enable the fat JAR creation
        enabled = true
    }

    bootJar {
        // BootJar task creates a runnable JAR with all dependencies
        archiveFileName.set("spring-backend.jar")
    }

}

