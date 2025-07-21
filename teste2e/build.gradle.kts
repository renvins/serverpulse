plugins {
    id("java")
}

group = "it.renvins"

repositories {
    mavenCentral()
    maven("https://repo.opencollab.dev/main/") {
        name = "opencollab"
    }
}

dependencies {
    val testcontainersVersion = "1.19.8"

    // Frameworks for testing
    testImplementation("org.junit.jupiter:junit-jupiter-api:5.13.3")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:5.13.3")

    // Testcontainers for integration testing
    testImplementation("org.testcontainers:testcontainers:\${testcontainersVersion}")
    testImplementation("org.testcontainers:junit-jupiter:\${testcontainersVersion}")
    testImplementation("org.testcontainers:docker-compose:\${testcontainersVersion}")
    testImplementation("org.testcontainers:selenium:\${testcontainersVersion}") // To test web applications

    testImplementation("com.influxdb:influxdb-client-java:6.7.0")
    testImplementation("org.geysermc.mcprotocollib:protocol:1.21.4-1")

    testImplementation(project(":common"))
    testImplementation(project(":bukkit"))

}

tasks.test {
    useJUnitPlatform()
    testLogging.showStandardStreams = true
}