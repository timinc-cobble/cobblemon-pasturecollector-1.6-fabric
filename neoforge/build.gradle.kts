plugins {
    id("dev.architectury.loom")
    id("architectury-plugin")
    id("com.gradleup.shadow") version ("9.2.2")
}

architectury {
    platformSetupLoomIde()
    neoForge()
}
loom {
    silentMojangMappingsLicense()
    enableTransitiveAccessWideners.set(true)
}

val shadowCommon: Configuration by configurations.creating

repositories {
    mavenCentral()
    maven("https://dl.cloudsmith.io/public/geckolib3/geckolib/maven/")
    maven("https://maven.impactdev.net/repository/development/")
    maven("https://hub.spigotmc.org/nexus/content/groups/public/")
    maven("https://thedarkcolour.github.io/KotlinForForge/")
    maven("https://maven.neoforged.net")
}

dependencies {
    minecraft("net.minecraft:minecraft:${property("minecraft_version")}")
    mappings(loom.officialMojangMappings())
    neoForge("net.neoforged:neoforge:${property("neoforge_version")}")

    modImplementation("com.cobblemon:neoforge:${property("cobblemon_version")}") { isTransitive = false }
    //Needed for cobblemon
    forgeRuntimeLibrary("thedarkcolour:kotlinforforge-neoforge:${property("kotlin_for_forge_version")}") {
        exclude("net.neoforged.fancymodloader", "loader")
    }

    implementation(project(":common", configuration = "namedElements"))
    "developmentNeoForge"(project(":common", configuration = "namedElements")) {
        isTransitive = false
    }
    shadowBundle(project(":common", configuration = "transformProductionNeoForge"))

    modImplementation("maven.modrinth:cobblemon-tim-core:${property("tim_core_neoforge_version")}")
    modImplementation("maven.modrinth:cobblemon-droploottables:${property("droploottables_neoforge_version")}")

    testImplementation("org.junit.jupiter:junit-jupiter-api:${property("junit_version")}")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:${property("junit_version")}")
}



tasks {
    getByName<Test>("test") {
        useJUnitPlatform()
    }

    filesMatching("META-INF/neoforge.mods.toml") {
        expand(project.properties)
    }

    jar {
        archiveBaseName.set("${rootProject.property("archives_base_name")}-${project.name}")
        archiveClassifier.set("dev-slim")
    }

    shadowJar {
        configurations = listOf(shadowCommon)

        archiveBaseName.set("${rootProject.name}-${project.name}")
        archiveVersion.set("${project.version}")
        archiveClassifier.set("shadow")
    }

    remapJar {
        injectAccessWidener = true
        dependsOn(shadowJar)
        inputFile.set(shadowJar.flatMap { it.archiveFile })

        archiveBaseName.set("${rootProject.name}-${project.name}")
        archiveVersion.set("${project.version}")
    }

    remapSourcesJar {
        archiveBaseName.set("${rootProject.name}-${project.name}")
        archiveVersion.set("${project.version}")
        archiveClassifier.set("sources")
    }
}
