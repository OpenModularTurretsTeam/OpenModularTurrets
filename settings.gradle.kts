pluginManagement {
    // The git submodule omlib is the library and the shared build logic. The library is built for all nodes first (a no-op
    // while its sources are unchanged): the mod's projects need its jars while they are configured. Set
    // OMLIB_SKIP_BUILD to skip this, for example if the jars are already there.
    val omlibDir = File(settingsDir, "omlib")
    if (File(omlibDir, "tools/bootstrap.sh").exists() && System.getenv("OMLIB_SKIP_BUILD") == null) {
        val build = ProcessBuilder("sh", "tools/bootstrap.sh").directory(omlibDir).inheritIO()
        build.environment()["JAVA_HOME"] = System.getProperty("java.home")
        check(build.start().waitFor() == 0) { "Building the omlib submodule failed (see above)" }
        // Loom keeps the remapped jar of a mod dependency under its version, and a rebuilt library has the same version:
        // drop that cache when the library was built from other sources than last time
        val built = File(omlibDir, "build/maven/.stamp")
        val seen = File(settingsDir, ".gradle/omlib.stamp")
        if (built.exists() && (!seen.exists() || seen.readText() != built.readText())) {
            File(settingsDir, ".gradle/loom-cache/remapped_mods/remapped/omtteam").deleteRecursively()
            seen.parentFile.mkdirs()
            seen.writeText(built.readText())
        }
    }
    // Shared build logic (the mod-common convention plugin and convertFabricData)
    includeBuild("omlib/build-logic")
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
        versions(
            "1.20.1-fabric" to "1.20.1", "1.20.1-forge" to "1.20.1",
            "1.21.1-fabric" to "1.21.1", "1.21.1-neoforge" to "1.21.1",
            "1.21.11-fabric" to "1.21.11", "1.21.11-neoforge" to "1.21.11",
        )
        mapBuilds { _, node -> "build.${node.project.substringAfterLast('-')}.gradle.kts" }
        vcsVersion = "1.21.1-fabric" // the version the committed source is written for
    }
}

rootProject.name = "openmodularturrets"
