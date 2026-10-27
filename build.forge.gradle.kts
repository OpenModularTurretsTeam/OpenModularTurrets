// Forge nodes (1.20.1; legacy ModDevGradle). Shared logic (Java, metadata templating, tests, publishing) is in
// buildSrc/mod-common. Forge 1.20.1 runs on SRG names: the jar is reobfuscated (reobfJar), and mods from other
// sources (Jade) are remapped to Mojang names through the mod* configurations.
plugins {
    id("mod-common")
    id("net.neoforged.moddev.legacyforge")
}

val modId = property("mod_id") as String
val packagePath = (property("maven_group") as String).replace('.', '/')
val mixinConfig = "$modId.mixins.json"
val refmap = "$modId.refmap.json"

// Client-only code keeps its own source set (src/client), like Loom's split source sets on Fabric:
// main cannot see client classes, and the jar contains both.
val client: SourceSet = sourceSets.create("client") {
    compileClasspath += sourceSets.main.get().output
    runtimeClasspath += sourceSets.main.get().output
}

// Datagen is Fabric-only by design: Forge gets the converted Fabric output (convertFabricData below)
sourceSets.main { java { exclude("$packagePath/datagen/**") } }
sourceSets.test { java { include("$packagePath/core/**") } }

// In-world tests (src/gametest): a separate test mod (openmodularturrets_gametest) that is never shipped, sharing
// its test classes with Fabric and NeoForge. `check` runs them on a headless GameTest server.
val gametest: SourceSet = sourceSets.create("gametest") {
    compileClasspath += sourceSets.main.get().output
    runtimeClasspath += sourceSets.main.get().output
}

legacyForge {
    version = property("forge_version") as String
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
            systemProperty("forge.enabledGameTestNamespaces", modId)
        }
    }
}

// ---- Mixins ----
// Forge 1.20.1 runs the mixins on SRG names: the annotation processor writes a refmap, which the Forge copy of the
// mixin config names (the shared config has no refmap: Fabric and NeoForge need none). Forge finds the config through
// the jar manifest (MixinConfigs). MixinExtras is not part of Forge 1.20.1: bundled into this jar (decided by the user).
mixin {
    add(sourceSets.main.get(), refmap)
    config(mixinConfig)
}

tasks.processResources {
    filesMatching(mixinConfig) {
        filter { line -> line.replace("\"package\":", "\"refmap\": \"$refmap\",\n  \"package\":") }
    }
}

// Dev-run-only dependencies, never in the published metadata (Loom's modLocalRuntime), remapped like the mod* ones:
// declared on modLocalRuntime, which puts them remapped to the dev names into localRuntime
val localRuntime: Configuration = configurations.create("localRuntime")
val modLocalRuntime: Configuration = obfuscation.createRemappingConfiguration(localRuntime)
configurations.runtimeClasspath { extendsFrom(localRuntime) }
// Not on the GameTest server, as on NeoForge.

dependencies {
    annotationProcessor("org.spongepowered:mixin:0.8.5:processor")
    val mixinExtras = property("mixinextras_version") as String
    compileOnly("io.github.llamalad7:mixinextras-common:$mixinExtras")
    annotationProcessor("io.github.llamalad7:mixinextras-common:$mixinExtras")
    implementation("io.github.llamalad7:mixinextras-forge:$mixinExtras")
    jarJar("io.github.llamalad7:mixinextras-forge") {
        version {
            strictly("[$mixinExtras,)")
            prefer(mixinExtras)
        }
    }

    // OMLib (git submodule omlib, built by :buildOmlib): a mod of its own, jar-in-jar. The jar is SRG-named; modImplementation
    // remaps it for the dev environment. The client and game test source sets only see main's classes.
    val omlib = project.extra["omlibCoordinates"] as String
    val omlibVersion = project.extra["omlibVersion"] as String
    "modImplementation"(omlib) { isTransitive = false }
    // The client and game test source sets use the jar with the dev names (classifier dev): they have no remapping, and
    // the game test server would fail to load the SRG-named jar (and still exit normally).
    "clientCompileOnly"("$omlib:dev") { isTransitive = false }
    "gametestCompileOnly"("$omlib:dev") { isTransitive = false }
    "gametestRuntimeOnly"("$omlib:dev") { isTransitive = false }
    jarJar("omtteam.omlib:omlib") {
        version {
            strictly("[$omlibVersion,)")
            prefer("$omlibVersion+${property("minecraft_version")}-forge")
        }
    }

    // Jade plugin (compat/jade, client/compat/jade): optional, not bundled; present in dev runs to try it
    "modCompileOnly"("maven.modrinth:jade:${property("jade_version")}")
    modLocalRuntime("maven.modrinth:jade:${property("jade_version")}")
}
// The client half of the Jade plugin (client/compat/jade) compiles against it too (remapped, through compileOnly)
configurations.named("clientCompileOnly") { extendsFrom(configurations.compileOnly.get()) }

// The client half of the Jade plugin (client/compat/jade) compiles against it too (remapped, through compileOnly)
configurations.named("clientCompileOnly") { extendsFrom(configurations.compileOnly.get()) }

// ---- Data (recipes, loot tables, tags, damage types, advancements) ----
// Not generated here: copied from the Fabric datagen output of the same Minecraft version (datagen_from) with Forge
// load conditions ("conditions", read by Forge 1.20.1 on recipes and advancements; see convertFabricData in buildSrc).

val fabricData: File = rootProject.file("versions/${property("datagen_from")}/src/main/generated")
val forgeData: Provider<Directory> = layout.buildDirectory.dir("generated/forgeData")

val convertFabricData = tasks.register("convertFabricData") {
    group = "build"
    description = "Copies the Fabric datagen output of datagen_from with Forge load conditions"
    inputs.dir(fabricData)
    outputs.dir(forgeData)
    val namespace = modId
    doLast { convertFabricData(fabricData, forgeData.get().asFile, namespace, "conditions") }
}
sourceSets.main { resources.srcDir(convertFabricData) }

// Forge 1.20.1 only loads a mod's resources and data as a pack if it has a pack.mcmeta (Fabric and NeoForge 1.21 need
// none, and a shared one would carry the wrong pack_format there), so this node generates it for both mods.
fun packMetadata(name: String, description: String) = tasks.register(name) {
    group = "build"
    val output = layout.buildDirectory.dir("generated/$name")
    outputs.dir(output)
    doLast {
        val text = """{ "pack": { "description": "$description", "pack_format": 15 } }""" + "\n"
        output.get().file("pack.mcmeta").asFile.apply { parentFile.mkdirs() }.writeText(text)
    }
}
sourceSets.main { resources.srcDir(packMetadata("modPackMetadata", "${property("mod_name")} resources")) }
gametest.resources.srcDir(packMetadata("gametestPackMetadata", "${property("mod_name")} game test resources"))

tasks.check { dependsOn("runGameTestServer") }

// A game test server whose mod fails to load can still exit normally (Forge did), so `check` also needs the server's own
// "All N required tests passed" line in its log.
tasks.named("runGameTestServer") {
    val log = layout.projectDirectory.file("run/logs/latest.log")
    doLast {
        if (!log.asFile.exists() || !log.asFile.readText().contains("required tests passed")) {
            throw GradleException("The game test server did not report that its tests passed (see ${log.asFile})")
        }
    }
}

tasks.jar {
    from(client.output)
    manifest.attributes("MixinConfigs" to mixinConfig)
}
tasks.named<Jar>("sourcesJar") { from(client.allSource) }

publishMods {
    // the reobfuscated jar (SRG names), the one Forge loads outside the dev environment
    file = tasks.named<Jar>("reobfJar").flatMap { it.archiveFile }
    additionalFiles.from(tasks.named<Jar>("sourcesJar").flatMap { it.archiveFile })
}
