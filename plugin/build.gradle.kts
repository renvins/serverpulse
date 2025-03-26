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
}

dependencies {
    implementation("com.github.oshi:oshi-core:6.8.0")
    implementation("com.influxdb:influxdb-client-java:7.2.0")
    compileOnly("io.papermc.paper:paper-api:1.21.4-R0.1-SNAPSHOT")
}

java {
    toolchain.languageVersion.set(JavaLanguageVersion.of(21))
}

val relocatePath = "it.renvins.serverpulse.libs"

tasks.withType<ShadowJar> {
    archiveBaseName = "serverpulse"
    archiveClassifier = "plugin"
    archiveVersion = "${rootProject.version}"

    // --- Relocations ---
    relocate("com.influxdb", "$relocatePath.influxdb")
    relocate("okhttp3", "$relocatePath.okhttp3")
    relocate("okio", "$relocatePath.okio")
    relocate("retrofit2", "$relocatePath.retrofit2")
    relocate("com.squareup.moshi", "$relocatePath.moshi")
    relocate("kotlin", "$relocatePath.kotlin")
    relocate("kotlinx", "$relocatePath.kotlinx")
    relocate("org.reactivestreams", "$relocatePath.reactivestreams")
    relocate("oshi", "$relocatePath.oshi")
    relocate("com.sun.jna", "$relocatePath.jna")
    // other relocations needed

    // --- End Relocations ---

    // --- Exclusions ---
    // Prevent Paper from giving errors about duplicate files
    exclude("META-INF/AL2.0")
    exclude("META-INF/LGPL2.1")
    exclude("META-INF/LICENSE")
    exclude("META-INF/LICENSE.txt")
    exclude("META-INF/NOTICE.txt")
}