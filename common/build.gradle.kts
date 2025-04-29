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
}

dependencies {
    implementation(project(":api"))
}