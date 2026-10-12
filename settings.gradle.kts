pluginManagement {
    repositories {
        mavenCentral()
        gradlePluginPortal()
        maven("https://maven.fabricmc.net/") { name = "Fabric" }
        maven("https://maven.kikugie.dev/releases") { name = "KikuGie Releases" }
    }
}

plugins {
    id("dev.kikugie.stonecutter") version "0.9.8"
    // Auto-provisions the Java toolchains (17 / 21) if they are not installed locally
    id("org.gradle.toolchains.foojay-resolver-convention") version "1.0.0"
}

stonecutter {
    create(rootProject) {
        versions("1.20.1", "1.21.1")
        vcsVersion = "1.21.1" // the version the committed source is written for
    }
}

rootProject.name = "openmodularturrets"
