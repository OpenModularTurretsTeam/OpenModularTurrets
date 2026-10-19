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

// datagen is Fabric-only by design (NeoForge gets the converted Fabric output, convertFabricData below).
// PORTING IN PROGRESS: compat/jade still waits for its NeoForge step; once it is ported, only datagen stays excluded.
val notPorted = listOf("datagen", "compat/jade")
sourceSets.main { java { notPorted.forEach { exclude("$packagePath/$it/**") } } }
sourceSets.test { java { include("$packagePath/core/**") } }

// GameTests (src/gametest) are not set up for NeoForge yet: the test classes still use FabricGameTest. See the
// shared-test setup in OpenModularLighting (OMLGameTests, TestTransfer, gametest source set as a second mod).

neoForge {
    version = property("neoforge_version") as String
    addModdingDependenciesTo(client)
    addModdingDependenciesTo(sourceSets.test.get()) // unit tests use game libraries (Gson), as on Loom

    mods {
        register(modId) {
            sourceSet(sourceSets.main.get())
            sourceSet(client)
        }
    }

    runs {
        // Run directory: versions/<v>/run (kept apart from the Fabric runs, whose worlds would not load here)
        register("client") { client() }
        register("server") {
            server()
            programArgument("--nogui")
        }
        // Headless in-world tests (no EULA needed); runs the GameTests of this mod's namespace and exits with the
        // number of failed tests, which fails the task
        register("gameTestServer") {
            type = "gameTestServer"
            systemProperty("neoforge.enabledGameTestNamespaces", modId)
        }
    }
}

// Dev-run-only dependencies, never in the published metadata (Loom's modLocalRuntime; same as the NeoForge MDK)
val localRuntime: Configuration = configurations.create("localRuntime")
configurations.runtimeClasspath { extendsFrom(localRuntime) }

dependencies {
    // Jade plugin (compat/jade): optional, not bundled
    compileOnly("maven.modrinth:jade:${property("jade_version")}")
}

// ---- Data (recipes, loot tables, tags, damage types, advancements) ----
// Not generated here: copied from the Fabric datagen output of the same Minecraft version (datagen_from), which only
// differs in its load conditions. "fabric:load_conditions" becomes "neoforge:conditions" ("condition" -> "type").
// Only conditions in this mod's namespace convert: they are registered under the same id on both loaders. Any other
// condition (e.g. fabric:all_mods_loaded) fails the build instead of producing wrong data.

val fabricData: File = rootProject.file("versions/${property("datagen_from")}/src/main/generated")
val neoForgeData: Provider<Directory> = layout.buildDirectory.dir("generated/neoforgeData")

val convertFabricData = tasks.register("convertFabricData") {
    group = "build"
    description = "Copies the Fabric datagen output of datagen_from with NeoForge load conditions"
    inputs.dir(fabricData)
    outputs.dir(neoForgeData)
    val namespace = modId
    doLast {
        val output = neoForgeData.get().asFile
        output.deleteRecursively()
        fabricData.walkTopDown().onEnter { it.name != ".cache" }.filter { it.isFile }.forEach { file ->
            val target = output.resolve(file.relativeTo(fabricData))
            target.parentFile.mkdirs()
            if (file.extension != "json" || !file.readText().contains("\"fabric:")) {
                file.copyTo(target)
                return@forEach
            }
            @Suppress("UNCHECKED_CAST")
            val json = groovy.json.JsonSlurper().parse(file) as Map<String, Any?>
            val converted = LinkedHashMap<String, Any?>()
            json.forEach { (key, value) ->
                if (key == "fabric:load_conditions") {
                    @Suppress("UNCHECKED_CAST")
                    converted["neoforge:conditions"] = (value as List<Map<String, Any?>>).map { condition ->
                        val id = condition["condition"] as String
                        if (!id.startsWith("$namespace:")) {
                            throw GradleException("${file.relativeTo(fabricData)}: no NeoForge counterpart for condition $id")
                        }
                        linkedMapOf<String, Any?>("type" to id) + condition.filterKeys { it != "condition" }
                    }
                } else {
                    converted[key] = value
                }
            }
            val text = groovy.json.JsonOutput.prettyPrint(groovy.json.JsonOutput.toJson(converted)) + "\n"
            if (text.contains("\"fabric:")) {
                throw GradleException("${file.relativeTo(fabricData)}: Fabric-specific data left after the conversion")
            }
            target.writeText(text)
        }
    }
}
sourceSets.main { resources.srcDir(convertFabricData) }

tasks.jar { from(client.output) }
tasks.named<Jar>("sourcesJar") { from(client.allSource) }

publishMods {
    file = tasks.jar.flatMap { it.archiveFile }
    additionalFiles.from(tasks.named<Jar>("sourcesJar").flatMap { it.archiveFile })
}
