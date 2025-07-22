rootProject.name =
    "serverpulse"

include("api")
include("bukkit")
include("common")
include("velocity")
include("fabric")

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
include("bungeecord")
include("teste2e")
include("teste2e")