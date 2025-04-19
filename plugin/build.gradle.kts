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
    implementation(project(":api"))
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

    relocate("com.influxdb", "$relocatePath.influxdb")

    // Exclude Kotlin to avoid conflicts with eco
    exclude("kotlin/**")
    exclude("org/jetbrains/kotlin/**")
    exclude("META-INF/kotlin*")

    // Prevent Paper from giving errors about duplicate files
    exclude("META-INF/AL2.0")
    exclude("META-INF/LGPL2.1")
    exclude("META-INF/LICENSE")
    exclude("META-INF/LICENSE.txt")
    exclude("META-INF/NOTICE.txt")
}

tasks.withType<ProcessResources> {
    // Define the encoding for resource files
    filteringCharset = "UTF-8"

    filesMatching("plugin.yml") {
        // Enable property expansion for plugin.yml
        expand(project.properties) // Expands properties like 'version'
    }
}