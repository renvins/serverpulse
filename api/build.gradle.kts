plugins {
    id("java")
}
group = "it.renvins"

java {
    toolchain.languageVersion.set(JavaLanguageVersion.of(21))
}

dependencies {
    compileOnly("com.influxdb:influxdb-client-java:7.2.0")
}