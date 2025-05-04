import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar

plugins {
    id("java")
    id("com.gradleup.shadow") version "9.0.0-beta11"
    id("io.freefair.lombok") version "8.13.1"
}

group = "it.renvins"

repositories {
    mavenCentral()
    maven {
        name = "papermc"
        url = uri("https://repo.papermc.io/repository/maven-public/")
    }
    maven { url = uri("https://jitpack.io") }
}

dependencies {
    implementation(project(":api"))
    implementation(project(":common"))
    implementation("com.github.Carleslc.Simple-YAML:Simple-Yaml:1.8.4")

    compileOnly("com.velocitypowered:velocity-api:3.4.0-SNAPSHOT")
    annotationProcessor("com.velocitypowered:velocity-api:3.4.0-SNAPSHOT")
}

java {
    toolchain.languageVersion.set(JavaLanguageVersion.of(21))
}

val relocatePath = "it.renvins.serverpulse.libs"

tasks.withType<ShadowJar> {
    archiveBaseName = "serverpulse"
    archiveClassifier = "velocity"
    archiveVersion = "${rootProject.version}"

    relocate("com.influxdb", "$relocatePath.influxdb")
}