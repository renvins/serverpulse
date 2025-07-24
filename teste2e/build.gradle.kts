plugins {
    id("java")
    id("io.freefair.lombok") version "8.13.1"
}

group = "it.renvins"

repositories {
    mavenCentral()
    maven {
        name = "opencollabRepositoryMavenReleases"
        url = uri("https://repo.opencollab.dev/maven-releases")
    }
}

dependencies {
    // === FIX: Import the JUnit 5 Bill of Materials (BOM) ===
    // This forces all JUnit dependencies to use the same version
    testImplementation(platform("org.junit:junit-bom:5.10.2"))
    testImplementation(platform("org.testcontainers:testcontainers-bom:1.21.3")) // ADDED THIS LINE

    testImplementation("org.junit.jupiter:junit-jupiter-params")

    // Testcontainers dependencies
    testImplementation("org.testcontainers:testcontainers")
    testImplementation("org.testcontainers:junit-jupiter")

    testImplementation("org.junit.jupiter:junit-jupiter-api")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine")

    // To handle the Minecraft protocol
    testImplementation("org.geysermc.mcprotocollib:protocol:1.21.7-1")

}

tasks.test {
    useJUnitPlatform()
}
