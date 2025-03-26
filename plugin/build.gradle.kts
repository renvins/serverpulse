plugins {
    id("java")
    id("com.gradleup.shadow") version "9.0.0-beta11"
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

tasks.shadowJar {
    archiveBaseName = "serverpulse"
    archiveClassifier = "plugin"
    archiveVersion = "${rootProject.version}"
}