plugins {
    id("java")
    id("fabric-loom") version "1.10-SNAPSHOT"
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
    minecraft("com.mojang:minecraft:1.21.5")
    mappings("net.fabricmc:yarn:1.21.5+build.1:v2")
    modImplementation("net.fabricmc:fabric-loader:0.16.10")

    modImplementation("net.fabricmc.fabric-api:fabric-api:0.119.5+1.21.5")

    implementation(project(":api"))
    implementation(project(":common"))
    implementation("com.github.Carleslc.Simple-YAML:Simple-Yaml:1.8.4")
    modImplementation("me.lucko:fabric-permissions-api:0.3.1")
    include("me.lucko:fabric-permissions-api:0.3.1") // automatically include the dependency in the jar
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