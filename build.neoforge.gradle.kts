// NeoForge nodes (ModDevGradle). Shared logic (Java, metadata templating, tests, publishing) is in
// omlib/build-logic (mod-common).
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
// The unit tests left in this repository (ArchUnit, config migration) run on the Fabric node only, as before; the
// core's own tests, which ran here on every node, run in OpenModularCore (Java 17 and Java 8)
sourceSets.test { java { exclude("**") } }

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
        // Run directory: versions/<node>/run (kept apart from the Fabric runs, whose worlds would not load here)
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
        // Headless in-world tests (no EULA needed); runs the GameTests and exits with the number of failed tests,
        // which fails the task. Only those of this mod's
        // namespace.
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

// Sable Companion (Sable contraptions) comes from the Sable maven
repositories {
    exclusiveContent {
        forRepository { maven("https://maven.ryanhcode.dev/releases") { name = "Sable" } }
        filter { includeGroup("dev.ryanhcode.sable-companion") }
    }
    // OpenComputers: Rebooted (1.21.1 only) comes from its own maven
    exclusiveContent {
        forRepository { maven("https://maven.michiyo.me/releases/") { name = "OpenComputers" } }
        filter { includeGroup("li.cil.oc") }
    }
}

dependencies {
    // OMLib (git submodule omlib, built by :buildOmlib): a mod of its own, jar-in-jar. The client and game test source
    // sets only see main's classes, not its dependencies.
    val omlib = project.extra["omlibCoordinates"] as String
    val omlibVersion = project.extra["omlibVersion"] as String
    implementation(omlib) { isTransitive = false }
    "clientCompileOnly"(omlib) { isTransitive = false }
    "gametestCompileOnly"(omlib) { isTransitive = false }
    "gametestRuntimeOnly"(omlib) { isTransitive = false }
    jarJar("omtteam.omlib:omlib") {
        version {
            strictly("[$omlibVersion,)")
            prefer("$omlibVersion+${property("minecraft_version")}-neoforge")
        }
    }

    // Jade plugin (compat/jade, client/compat/jade): optional, not bundled; present in dev runs to try it
    compileOnly("maven.modrinth:jade:${property("jade_version")}")
    localRuntime("maven.modrinth:jade:${property("jade_version")}")

    // Sable contraptions: Sable Companion is jar-in-jar and does nothing without Sable; Sable itself is only in the dev
    // runs and on the GameTest server
    (findProperty("sable_companion_version") as String?)?.let { companion ->
        val companionArtifact = "dev.ryanhcode.sable-companion:sable-companion-common-${property("minecraft_version")}"
        implementation("$companionArtifact:$companion")
        // the client source set only sees main's classes, not its dependencies
        "clientCompileOnly"("$companionArtifact:$companion")
        jarJar(companionArtifact) {
            version {
                strictly("[$companion,)")
                prefer(companion)
            }
        }
        localRuntime("maven.modrinth:sable:${property("sable_version")}")
        // the contraption game tests (gametest/platform/neoforge) build sub-levels with Sable's own API
        "gametestCompileOnly"("maven.modrinth:sable:${property("sable_version")}")
        "gametestCompileOnly"("$companionArtifact:$companion")
        "gametestRuntimeOnly"("maven.modrinth:sable:${property("sable_version")}")
        // Create Aeronautics in the dev runs, to try turrets on airships by hand (not on the GameTest server)
        localRuntime("maven.modrinth:create-aeronautics:${property("create_aeronautics_version")}")
        localRuntime("maven.modrinth:create:${property("create_version")}")
        // Create's movement check for contraptions (platform/neoforge/NeoForgeCreateMovement): compile-only, never
        // bundled, only used when Create is installed; on the GameTest server for its test
        compileOnly("maven.modrinth:create:${property("create_version")}")
        "gametestCompileOnly"("maven.modrinth:create:${property("create_version")}")
        "gametestRuntimeOnly"("maven.modrinth:create:${property("create_version")}")
        // Simulated (bundled in Create Aeronautics) on the GameTest server, for the test of the Physics Assembler mixin
        "gametestRuntimeOnly"("maven.modrinth:create-aeronautics:${property("create_aeronautics_version")}")
    }

    // OpenComputers: Rebooted (where the node sets opencomputers_version): the turret base component
    // (platform/neoforge/opencomputers) compiles against its API jar, never bundled, only used when OpenComputers is
    // installed; the full mod (Scala bundled inside) is in the dev runs and on the GameTest server for its test. Not
    // transitive: its POM lists Create, AE2 and others as runtime dependencies, which it does not require.
    (findProperty("opencomputers_version") as String?)?.let { oc ->
        compileOnly("li.cil.oc:opencomputers:$oc:api") { isTransitive = false }
        localRuntime("li.cil.oc:opencomputers:$oc") { isTransitive = false }
        "gametestCompileOnly"("li.cil.oc:opencomputers:$oc:api") { isTransitive = false }
        "gametestRuntimeOnly"("li.cil.oc:opencomputers:$oc") { isTransitive = false }
    }
}
// The client half of the Jade plugin (client/compat/jade) compiles against it too, as with Loom's client source set
configurations.named("clientCompileOnly") { extendsFrom(configurations.compileOnly.get()) }

// ---- Data (recipes, loot tables, tags, damage types, advancements) ----
// Not generated here: copied from the Fabric node's datagen output (datagen_from) with NeoForge load conditions
// ("neoforge:conditions"; see convertFabricData in omlib/build-logic).

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

// A game test server whose mod fails to load can still exit normally, so `check` also needs the server's own
// "All N required tests passed" line in its log.
tasks.named("runGameTestServer") {
    val log = layout.projectDirectory.file("run/logs/latest.log")
    doLast {
        if (!log.asFile.exists() || !log.asFile.readText().contains("required tests passed")) {
            throw GradleException("The game test server did not report that its tests passed (see ${log.asFile})")
        }
    }
}

tasks.jar { from(client.output) }
tasks.named<Jar>("sourcesJar") { from(client.allSource) }

publishMods {
    file = tasks.jar.flatMap { it.archiveFile }
    additionalFiles.from(tasks.named<Jar>("sourcesJar").flatMap { it.archiveFile })
}
