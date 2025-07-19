plugins {
    id("java")
    id("java-library")
    id("io.freefair.lombok") version "8.13.1"
}
group = "it.renvins"

java {
    toolchain.languageVersion.set(JavaLanguageVersion.of(21))
}

repositories {
    mavenCentral()
    maven {
        url = uri("https://jitpack.io")
    }
}

dependencies {
    implementation(project(":api"))
    api("com.github.Carleslc.Simple-YAML:Simple-Yaml:1.8.4")
}