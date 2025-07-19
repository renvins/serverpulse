import net.fabricmc.loom.task.RemapJarTask

plugins {
    id("java")
    id("fabric-loom") version "1.11-SNAPSHOT"
    id("com.gradleup.shadow") version "9.0.0-beta11"
    id("io.freefair.lombok") version "8.13.1"
}

group = "it.renvins"

loom {
    splitEnvironmentSourceSets()

    mods {
        create("serverpulse") {
            sourceSet(sourceSets["main"])
            sourceSet(sourceSets["client"])
        }
    }
}

fabricApi {
    configureDataGeneration {
        client.set(true)
    }
}

repositories {
    mavenCentral()
    maven { url = uri("https://jitpack.io") }
}


dependencies {
    minecraft("com.mojang:minecraft:1.21.7")
    mappings("net.fabricmc:yarn:1.21.7+build.2:v2")
    modImplementation("net.fabricmc:fabric-loader:0.16.14") //

    modImplementation("net.fabricmc.fabric-api:fabric-api:0.128.2+1.21.7")

    // API and Common modules
    implementation(project(":api"))
    implementation(project(":common"))
    include(project(":api"))
    include(project(":common"))

    // SimpleYAML
    implementation("com.github.Carleslc.Simple-YAML:Simple-Yaml:1.8.4")
    include("com.github.Carleslc.Simple-YAML:Simple-Yaml:1.8.4")

    // Fabric Permissions API
    modImplementation("me.lucko:fabric-permissions-api:0.3.1")
    include("me.lucko:fabric-permissions-api:0.3.1")
}

tasks.processResources {
    filteringCharset = "UTF-8"

    filesMatching("fabric.mod.json") {
        expand(mapOf(
            "version" to rootProject.version,
            "minecraft_version" to "1.21.4",
            "loader_version" to "0.16.10"
        )
        )
    }
}

java {
    toolchain.languageVersion.set(JavaLanguageVersion.of(21))
}

tasks.withType<JavaCompile>().configureEach {
    options.encoding = "UTF-8"
}

tasks.withType<RemapJarTask>() {
    archiveBaseName = "serverpulse"
    archiveClassifier = "fabric"
    archiveVersion = "${rootProject.version}"
}