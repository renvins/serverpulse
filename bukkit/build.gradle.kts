import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar

plugins {
    id("java")
    id("com.gradleup.shadow") version "9.0.0-beta11"
}

group =
    "it.renvins"

repositories {
    mavenCentral()
    maven {
        name = "SpigotMC"
        url = uri("https://hub.spigotmc.org/nexus/content/repositories/snapshots/")
    }
}

dependencies {
    implementation(project(":api"))
    compileOnly("org.spigotmc:spigot-api:1.21.5-R0.1-SNAPSHOT")
}

java {
    toolchain.languageVersion.set(JavaLanguageVersion.of(21))
}

val relocatePath = "it.renvins.serverpulse.bukkit.libs"

tasks.withType<ShadowJar> {
    archiveBaseName = "serverpulse"
    archiveClassifier = "bukkit"
    archiveVersion = "${rootProject.version}"

    relocate("com.influxdb", "$relocatePath.influxdb")
    relocate("okhttp3", "$relocatePath.okhttp3")
    relocate("okio", "$relocatePath.okio")
    relocate("org.jetbrains", "$relocatePath.jetbrains")
    relocate("com.google", "$relocatePath.google")
    relocate("io.reactivex", "$relocatePath.reactivex")
    relocate("javax.annotation", "$relocatePath.annotation")
    relocate("org.apache", "$relocatePath.apache")
    relocate("org.intellij", "$relocatePath.intellij")
    relocate("org.reactivestreams", "$relocatePath.reactivestreams")
    relocate("retrofit2", "$relocatePath.retrofit2")

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