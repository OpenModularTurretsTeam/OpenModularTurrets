// NeoForge nodes (ModDevGradle). Shared logic (Java, metadata templating, tests, publishing) is in buildSrc/mod-common.
plugins {
    id("mod-common")
    id("net.neoforged.moddev")
}

val modId = property("mod_id") as String
val packagePath = (property("maven_group") as String).replace('.', '/')

// Client-only code keeps its own source set (src/client), like Loom's split source sets on Fabric:
// main cannot see client classes, and the jar contains both.
val client: SourceSet = sourceSets.create("client") {
    compileClasspath += sourceSets.main.get().output
    runtimeClasspath += sourceSets.main.get().output
}

// Datagen is Fabric-only by design: NeoForge gets the converted Fabric output (convertFabricData below)
sourceSets.main { java { exclude("$packagePath/datagen/**") } }
sourceSets.test { java { include("$packagePath/core/**") } }

// In-world tests (src/gametest): a separate test mod (openmodularturrets_gametest) that is never shipped, sharing
// its test classes with Fabric. `check` runs them on a headless GameTest server.
val gametest: SourceSet = sourceSets.create("gametest") {
    compileClasspath += sourceSets.main.get().output
    runtimeClasspath += sourceSets.main.get().output
}

neoForge {
    version = property("neoforge_version") as String
    addModdingDependenciesTo(client)
    addModdingDependenciesTo(sourceSets.test.get()) // unit tests use game libraries (Gson), as on Loom
    addModdingDependenciesTo(gametest)

    mods {
        register(modId) {
            sourceSet(sourceSets.main.get())
            sourceSet(client)
        }
        register("${modId}_gametest") {
            sourceSet(gametest)
        }
    }

    runs {
        // Run directory: versions/<v>/run (kept apart from the Fabric runs, whose worlds would not load here)
        // Only the mod itself; the test mod is for the GameTest server (as on Fabric, where only runGameTest loads it)
        register("client") {
            client()
            loadedMods = setOf(mods.getByName(modId))
        }
        register("server") {
            server()
            programArgument("--nogui")
            loadedMods = setOf(mods.getByName(modId))
        }
        // Headless in-world tests (no EULA needed); runs the GameTests of this mod's namespace and exits with the
        // number of failed tests, which fails the task
        register("gameTestServer") {
            type = "gameTestServer"
            sourceSet = gametest
            systemProperty("neoforge.enabledGameTestNamespaces", modId)
        }
    }
}

// Dev-run-only dependencies, never in the published metadata (Loom's modLocalRuntime; same as the NeoForge MDK)
val localRuntime: Configuration = configurations.create("localRuntime")
configurations.runtimeClasspath { extendsFrom(localRuntime) }
// Not on the GameTest server: Jade there sends its ping to the vanilla mock players of the tests, which NeoForge
// refuses (no negotiated channels).

dependencies {
    // Jade plugin (compat/jade): optional, not bundled; present in dev runs to try it
    compileOnly("maven.modrinth:jade:${property("jade_version")}")
    localRuntime("maven.modrinth:jade:${property("jade_version")}")
}

// ---- Data (recipes, loot tables, tags, damage types, advancements) ----
// Not generated here: copied from the Fabric datagen output of the same Minecraft version (datagen_from) with NeoForge
// load conditions ("neoforge:conditions"; see convertFabricData in buildSrc).

val fabricData: File = rootProject.file("versions/${property("datagen_from")}/src/main/generated")
val neoForgeData: Provider<Directory> = layout.buildDirectory.dir("generated/neoforgeData")

val convertFabricData = tasks.register("convertFabricData") {
    group = "build"
    description = "Copies the Fabric datagen output of datagen_from with NeoForge load conditions"
    inputs.dir(fabricData)
    outputs.dir(neoForgeData)
    val namespace = modId
    doLast { convertFabricData(fabricData, neoForgeData.get().asFile, namespace, "neoforge:conditions") }
}
sourceSets.main { resources.srcDir(convertFabricData) }

tasks.check { dependsOn("runGameTestServer") }

tasks.jar { from(client.output) }
tasks.named<Jar>("sourcesJar") { from(client.allSource) }

publishMods {
    file = tasks.jar.flatMap { it.archiveFile }
    additionalFiles.from(tasks.named<Jar>("sourcesJar").flatMap { it.archiveFile })
}
