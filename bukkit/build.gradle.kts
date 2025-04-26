plugins {
    id("java")
}

group =
    "it.renvins"
version =
    "0.1.7-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    testImplementation(
        platform(
            "org.junit:junit-bom:5.10.0"
        )
    )
    testImplementation(
        "org.junit.jupiter:junit-jupiter"
    )
}

tasks.test {
    useJUnitPlatform()
}