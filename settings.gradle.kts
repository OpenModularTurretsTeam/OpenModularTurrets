pluginManagement {
    repositories {
        mavenCentral()
        gradlePluginPortal()
        maven("https://maven.fabricmc.net/") { name = "Fabric" }
        maven("https://maven.kikugie.dev/releases") { name = "KikuGie Releases" }
        maven("https://maven.neoforged.net/releases") { name = "NeoForged" }
    }
}

plugins {
    id("dev.kikugie.stonecutter") version "0.9.8"
    // Auto-provisions the Java toolchains (17 / 21) if they are not installed locally
    id("org.gradle.toolchains.foojay-resolver-convention") version "1.0.0"
}

stonecutter {
    create(rootProject) {
        // Node name = "<minecraft version>-<loader>"; the build script follows the loader
        versions("1.20.1-fabric" to "1.20.1", "1.21.1-fabric" to "1.21.1", "1.21.1-neoforge" to "1.21.1")
        mapBuilds { _, node -> "build.${node.project.substringAfterLast('-')}.gradle.kts" }
        vcsVersion = "1.21.1-fabric" // the version the committed source is written for
    }
}

rootProject.name = "openmodularturrets"
