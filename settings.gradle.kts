pluginManagement {
    // The git submodule omlib is the library and the shared build logic. The library is built for all nodes first (a no-op
    // while its sources are unchanged): the mod's projects need its jars while they are configured. Set
    // OMLIB_SKIP_BUILD to skip this, for example if the jars are already there.
    val omlibDir = File(settingsDir, "omlib")
    if (File(omlibDir, "tools/bootstrap.sh").exists() && System.getenv("OMLIB_SKIP_BUILD") == null) {
        val build = ProcessBuilder("sh", "tools/bootstrap.sh").directory(omlibDir).inheritIO()
        build.environment()["JAVA_HOME"] = System.getProperty("java.home")
        // the library build follows the switch of the local OpenModularCore (see below)
        if (providers.gradleProperty("omm.noLocalCore").getOrElse("false") != "false") {
            build.environment()["OMM_NO_LOCAL_CORE"] = "1"
        }
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
    // Auto-provisions the Java toolchain (17) if it is not installed locally
    id("org.gradle.toolchains.foojay-resolver-convention") version "1.0.0"
}

stonecutter {
    create(rootProject) {
        // Node name = "<minecraft version>-<loader>"; the build script follows the loader
        versions("1.20.1-fabric" to "1.20.1", "1.20.1-forge" to "1.20.1")
        mapBuilds { _, node -> "build.${node.project.substringAfterLast('-')}.gradle.kts" }
        vcsVersion = "1.20.1-fabric" // the version the committed source is written for
    }
}

rootProject.name = "openmodularturrets"

// OpenModularCore from source (CLAUDE.md of the OMM root, "Local development"): when a folder above this one
// holds OpenModularCore/, that build replaces the JitPack modules com.github.OpenModularTurretsTeam.OpenModularCore:<module>
// (its group is the JitPack group, its project names are the module names). -Pomm.noLocalCore or OMM_NO_LOCAL_CORE=1
// turns this off: the build then takes the JitPack artifact of core_version, as CI does (CI has no OpenModularCore/).
// The same block is in the mods' settings and the workspace's; omlib/tools/bootstrap.sh finds the folder the same way.
val localCoreOff = providers.gradleProperty("omm.noLocalCore").getOrElse("false") != "false" ||
    !(System.getenv("OMM_NO_LOCAL_CORE") ?: "0").matches(Regex("0?"))
if (!localCoreOff) {
    generateSequence(settingsDir) { it.parentFile }
        .map { File(it, "OpenModularCore") }
        .firstOrNull { File(it, "settings.gradle.kts").isFile }
        ?.let { localCore ->
            logger.lifecycle("OpenModularCore: local source at $localCore (OMM_NO_LOCAL_CORE=1 for the JitPack artifact)")
            includeBuild(localCore)
        }
}
