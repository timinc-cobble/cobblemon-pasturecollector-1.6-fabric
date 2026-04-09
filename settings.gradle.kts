rootProject.name = "cobblemon-pasturecollector"

pluginManagement {
    repositories {
        gradlePluginPortal()
        mavenCentral()
        maven("https://maven.fabricmc.net/")
        maven("https://maven.architectury.dev/")
        maven("https://maven.neoforged.net/releases/")
    }
}


listOf(
    "common",
//    "neoforge",
    "fabric"
).forEach { include(it) }
