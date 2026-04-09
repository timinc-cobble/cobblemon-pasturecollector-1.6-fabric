plugins {
    id("java")
    id("java-library")
    kotlin("jvm") version("2.2.20")

    id("dev.architectury.loom") version("1.14-SNAPSHOT") apply false
    id("architectury-plugin") version("3.4-SNAPSHOT") apply false

    id("com.gradleup.shadow") version ("9.2.2") apply false
}

allprojects {
    repositories {
        mavenCentral()
    }
}

subprojects {
    apply(plugin = "java")
    apply(plugin = "org.jetbrains.kotlin.jvm")

    version = "${project.property("modCobblemonVersion")!!}-${project.property("modMyVersion")!!}"
    group = project.property("maven_group")!!

    repositories {
        maven("https://artefacts.cobblemon.com/releases/")
        maven("https://api.modrinth.com/maven")
    }

    java {
        withSourcesJar()
    }
}
