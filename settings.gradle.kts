rootProject.name =
    "serverpulse"

include("api")
include("bukkit")
include("common")
include("velocity")
include("fabric")
include("bungeecord")
include("teste2e")

pluginManagement {
    repositories {
        maven {
            name = "Fabric"
            url = uri("https://maven.fabricmc.net/")
        }
        mavenCentral()
        gradlePluginPortal()
    }
}