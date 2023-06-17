val ktor_version: String by project
val kotlin_version: String by project
val logback_version: String by project

plugins {
    kotlin("jvm") version "1.6.10"
    id("io.ktor.plugin") version "2.2.3"

    id("org.sonarqube") version "4.2.1.3168"

    id("org.jlleitschuh.gradle.ktlint") version "11.4.0"
}

group = "com.showmeyourcode.ktor.demo"
version = "0.0.1"

application {
    mainClass.set("io.ktor.server.netty.EngineMain")

    val isDevelopment: Boolean = project.ext.has("development")
    applicationDefaultJvmArgs = listOf("-Dio.ktor.development=$isDevelopment")
}

repositories {
    mavenCentral()
}

sonarqube {
    properties {
        property("sonar.projectKey", "ShowMeYourCodeYouTube_ktor-demo")
        property("sonar.organization", "showmeyourcodeyoutube")
    }
}

dependencies {
    implementation("io.ktor:ktor-server-core-jvm:$ktor_version")
    implementation("io.ktor:ktor-server-netty-jvm:$ktor_version")
    implementation("ch.qos.logback:logback-classic:$logback_version")
    testImplementation("io.ktor:ktor-server-tests-jvm:$ktor_version")
    testImplementation("org.jetbrains.kotlin:kotlin-test-junit:$kotlin_version")
}
