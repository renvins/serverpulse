plugins {
    id("java")
    id("java-library")
    id("maven-publish")
}
group = "it.renvins"

java {
    toolchain.languageVersion.set(JavaLanguageVersion.of(21))
    withJavadocJar()
    withSourcesJar()
}

repositories {
    mavenCentral()
}

publishing {
    publications {
        create<MavenPublication>("maven") {
            groupId = "it.renvins"
            artifactId = "serverpulse-api"
            version = rootProject.version.toString()

            from(components["java"])

            pom {
                name.set("ServerPulse API")
                description.set("Effortless Minecraft performance monitoring with pre-configured Grafana/InfluxDB via Docker.")
                url.set("https://github.com/renvins/serverpulse")

                licenses {
                    license {
                        name.set("GNU General Public License v3.0")
                        url.set("https://www.gnu.org/licenses/gpl-3.0.html")
                    }
                }

                developers {
                    developer {
                        id.set("renvins")
                        name.set("renvins")
                    }
                }

                scm {
                    connection.set("scm:git:git://github.com/renvins/serverpulse.git")
                    developerConnection.set("scm:git:ssh://github.com/renvins/serverpulse.git")
                    url.set("https://github.com/renvins/serverpulse")
                }
            }
        }
    }
}