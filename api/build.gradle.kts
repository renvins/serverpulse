plugins {
    id("java")
    id("java-library")
}
group = "it.renvins"

java {
    toolchain.languageVersion.set(JavaLanguageVersion.of(21))
}

repositories {
    mavenCentral()
}

dependencies {
    api("com.influxdb:influxdb-client-java:7.2.0")
}