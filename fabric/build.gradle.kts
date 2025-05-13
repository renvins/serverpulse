import net.fabricmc.loom.task.RemapJarTask

plugins {
    id("java")
    id("fabric-loom") version "1.10-SNAPSHOT"
    id("com.gradleup.shadow") version "9.0.0-beta11"
    id("io.freefair.lombok") version "8.13.1"
}

group = "it.renvins"

loom {
    splitEnvironmentSourceSets()

    mods {
        create("serverpulse") {
            sourceSet(sourceSets["main"])
            sourceSet(sourceSets["client"])
        }
    }
}

fabricApi {
    configureDataGeneration {
        client.set(true)
    }
}

repositories {
    mavenCentral()
    maven { url = uri("https://jitpack.io") }
}


dependencies {
    minecraft("com.mojang:minecraft:1.21.4")
    mappings("net.fabricmc:yarn:1.21.4+build.8:v2")
    modImplementation("net.fabricmc:fabric-loader:0.16.14")

    modImplementation("net.fabricmc.fabric-api:fabric-api:0.119.2+1.21.4")

    // API and Common modules
    implementation(project(":api"))
    implementation(project(":common"))
    include(project(":api"))
    include(project(":common"))

    // SimpleYAML
    implementation("com.github.Carleslc.Simple-YAML:Simple-Yaml:1.8.4")
    include("com.github.Carleslc.Simple-YAML:Simple-Yaml:1.8.4")

    // --- InfluxDB Client and its verbose transitive dependencies ---
    // Main InfluxDB client API
    implementation("com.influxdb:influxdb-client-java:7.2.0")
    include("com.influxdb:influxdb-client-java:7.2.0")

    // InfluxDB modules (explicitly include these as they are separate JARs)
    implementation("com.influxdb:influxdb-client-core:7.2.0")
    include("com.influxdb:influxdb-client-core:7.2.0")
    implementation("com.influxdb:influxdb-client-flux:7.2.0")
    include("com.influxdb:influxdb-client-flux:7.2.0")
    implementation("com.influxdb:influxdb-client-kotlin:7.2.0") // Add Kotlin module
    include("com.influxdb:influxdb-client-kotlin:7.2.0")
    implementation("com.influxdb:influxdb-client-reactive:7.2.0") // Add Reactive module (for RxJava)
    include("com.influxdb:influxdb-client-reactive:7.2.0")
    implementation("com.influxdb:influxdb-client-utils:7.2.0")
    include("com.influxdb:influxdb-client-utils:7.2.0")

    // OkHttp (Dependency of InfluxDB)
    implementation("com.squareup.okhttp3:okhttp:4.11.0") // InfluxDB uses 4.11.0
    include("com.squareup.okhttp3:okhttp:4.11.0")
    implementation("com.squareup.okhttp3:logging-interceptor:4.11.0")
    include("com.squareup.okhttp3:logging-interceptor:4.11.0")
    implementation("com.squareup.okio:okio:3.4.0")       // InfluxDB uses 3.4.0 for okio
    include("com.squareup.okio:okio-jvm:3.4.0")          // Include the JVM specific JAR for okio

    // Kotlin (Dependency of InfluxDB - version 1.9.22 used by InfluxDB 7.2.0)
    implementation("org.jetbrains.kotlin:kotlin-stdlib:1.9.22")
    include("org.jetbrains.kotlin:kotlin-stdlib:1.9.22")
    // kotlin-stdlib-common is usually a transitive dependency of kotlin-stdlib,
    // and kotlin-stdlib (for JVM) includes -jdk7 and -jdk8 extensions.
    // If you face issues, you can add them explicitly:
    // implementation("org.jetbrains.kotlin:kotlin-stdlib-common:1.9.22")
    // include("org.jetbrains.kotlin:kotlin-stdlib-common:1.9.22")
    // implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8:1.9.22")
    // include("org.jetbrains.kotlin:kotlin-stdlib-jdk8:1.9.22")
    implementation("org.jetbrains.kotlin:kotlin-reflect:1.9.22") // Often needed for Kotlin libraries
    include("org.jetbrains.kotlin:kotlin-reflect:1.9.22")

    // Retrofit (Dependency of InfluxDB - version 2.9.0)
    implementation("com.squareup.retrofit2:retrofit:2.9.0")
    include("com.squareup.retrofit2:retrofit:2.9.0")
    implementation("com.squareup.retrofit2:converter-moshi:2.9.0")
    include("com.squareup.retrofit2:converter-moshi:2.9.0")
    implementation("com.squareup.retrofit2:converter-scalars:2.9.0")
    include("com.squareup.retrofit2:converter-scalars:2.9.0")
    implementation("com.squareup.retrofit2:converter-gson:2.9.0") // For Gson support
    include("com.squareup.retrofit2:converter-gson:2.9.0")
    implementation("com.squareup.retrofit2:adapter-rxjava3:2.9.0") // <<-- THIS IS FOR THE NEW ERROR
    include("com.squareup.retrofit2:adapter-rxjava3:2.9.0")      // <<-- THIS IS FOR THE NEW ERROR

    // Moshi (Dependency of Retrofit & InfluxDB - version 1.15.0)
    implementation("com.squareup.moshi:moshi:1.15.0")
    include("com.squareup.moshi:moshi:1.15.0")
    implementation("com.squareup.moshi:moshi-kotlin:1.15.0") // InfluxDB uses Kotlin, Moshi-Kotlin likely needed
    include("com.squareup.moshi:moshi-kotlin:1.15.0")

    // Gson (Dependency of Retrofit if using GsonConverter)
    implementation("com.google.code.gson:gson:2.10.1")
    include("com.google.code.gson:gson:2.10.1")

    // RxJava3 & Reactive Streams (Dependency of Retrofit adapter-rxjava3 & InfluxDB reactive module)
    // InfluxDB 7.2.0 uses RxJava 3.1.8 and reactive-streams 1.0.4
    implementation("io.reactivex.rxjava3:rxjava:3.1.8")
    include("io.reactivex.rxjava3:rxjava:3.1.8")
    implementation("org.reactivestreams:reactive-streams:1.0.4")
    include("org.reactivestreams:reactive-streams:1.0.4")
    // --- End of InfluxDB client dependencies ---

    // Fabric Permissions API
    modImplementation("me.lucko:fabric-permissions-api:0.3.1")
    include("me.lucko:fabric-permissions-api:0.3.1")
}

tasks.processResources {
    filteringCharset = "UTF-8"

    filesMatching("fabric.mod.json") {
        expand(mapOf(
            "version" to rootProject.version,
            "minecraft_version" to "1.21.4",
            "loader_version" to "0.16.10"
        )
        )
    }
}

java {
    toolchain.languageVersion.set(JavaLanguageVersion.of(21))
}

tasks.withType<JavaCompile>().configureEach {
    options.encoding = "UTF-8"
}

tasks.withType<RemapJarTask>() {
    archiveBaseName = "serverpulse"
    archiveClassifier = "fabric"
    archiveVersion = "${rootProject.version}"
}