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
        name = "bungeecord"
        url = uri("https://oss.sonatype.org/content/repositories/snapshots")
    }
6    maven {
        name = "mojang"
        url = uri("https://libraries.minecraft.net/")
    }
}

dependencies {
    implementation(project(":api"))
    implementation(project(":common"))

    compileOnly("net.md-5:bungeecord-api:1.21-R0.2")
}

java {
    toolchain.languageVersion.set(JavaLanguageVersion.of(21))
}

tasks.withType<ShadowJar> {
    archiveBaseName = "serverpulse"
    archiveClassifier = "bungeecord"
    archiveVersion = "${rootProject.version}"
}