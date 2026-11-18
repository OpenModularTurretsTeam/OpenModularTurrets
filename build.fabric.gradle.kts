// Fabric nodes (Loom). Shared logic (Java, metadata templating, tests, publishing) is in buildSrc/mod-common.
plugins {
    id("mod-common")
    id("net.fabricmc.fabric-loom-remap")
    `maven-publish`
    // Code quality report (gradle/pmd/ruleset.xml): complexity and size, report only
    pmd
    // Test coverage of api, from the unit tests and the game tests
    jacoco
}

val modId = property("mod_id") as String
val minecraftVersion = property("minecraft_version") as String

loom {
    splitEnvironmentSourceSets()

    mods {
        register(modId) {
            sourceSet(sourceSets["main"])
            sourceSet(sourceSets["client"])
        }
    }

    runConfigs.all {
        generateRunConfig = true
        runDirectory = rootProject.file("run") // shared run directory for all versions
    }
}

fabricApi {
    configureDataGeneration {
        // The providers need no client classes (block models are handwritten), so datagen runs without the client
        // source set; it also works while a new Minecraft version's client code is not ported yet
        client = false
        // versions/<v>/src/main/generated, committed per version
        outputDirectory = project.file("src/main/generated")
    }
    // Server game tests in src/gametest (./gradlew :<v>:runGameTest, also part of check)
    configureTests {
        createSourceSet = true
        modId = "${modId}_gametest"
        enableClientGameTests = false
    }
}

// runConfigs.all above points every run at the shared run directory; game tests get a fresh world of their own
loom.runConfigs.named("gameTest") {
    runDirectory = project.file("build/run/gameTest")
}
// ... and a new world for every run, so nothing is left over from earlier runs
tasks.named("runGameTest") {
    val world = project.file("build/run/gameTest/world")
    doFirst { world.deleteRecursively() }
}

// The client source set has no resources, so Gradle never creates build/resources/client, but Loom still puts that
// folder on the classpath of every game run and Fabric Loader warns about it ("Class path entries reference missing
// files"). Creating the empty folder before a run avoids the warning.
val clientResources = layout.buildDirectory.dir("resources/client")
tasks.withType<net.fabricmc.loom.task.AbstractRunTask>().configureEach {
    doFirst { clientResources.get().asFile.mkdirs() }
}

// Sable Companion (Sable contraptions) comes from the Sable maven
repositories {
    exclusiveContent {
        forRepository { maven("https://maven.ryanhcode.dev/releases") { name = "Sable" } }
        filter { includeGroup("dev.ryanhcode.sable-companion") }
    }
}

dependencies {
    minecraft("com.mojang:minecraft:$minecraftVersion")
    mappings(loom.officialMojangMappings())
    modImplementation("net.fabricmc:fabric-loader:${property("fabric_loader_version")}")
    modImplementation("net.fabricmc.fabric-api:fabric-api:${property("fabric_api_version")}")

    // OMLib (git submodule omlib, built before this project is configured): a mod of its own, jar-in-jar. In the dev
    // environment its own dependencies (Fabric API, Team Reborn Energy) come through it; only the library itself is
    // bundled (it bundles the energy API itself).
    val omlib = project.extra["omlibCoordinates"] as String
    modImplementation(omlib)
    "include"(omlib) { isTransitive = false }

    // Jade tooltips (optional, not bundled: the mod only registers the plugin when Jade is installed)
    modCompileOnly("maven.modrinth:jade:${property("jade_version")}")

    // Sable contraptions (1.21.1 only): Sable Companion is jar-in-jar and does nothing without Sable. Sable itself is
    // only in the dev client: its Fabric jar (2.0.6) does not load in a server environment (the Veil it bundles
    // requires client-only Fabric API modules), so the game tests run with Sable on NeoForge only.
    findProperty("sable_companion_version")?.let { companion ->
        val artifact = "dev.ryanhcode.sable-companion:sable-companion-fabric-$minecraftVersion:$companion"
        modImplementation(artifact)
        include(artifact)
        "modClientRuntimeOnly"("maven.modrinth:sable:${property("sable_version")}")
    }

    // Architecture rules (src/test/.../architecture): they read the compiled main and client classes
    testImplementation("com.tngtech.archunit:archunit-junit5:${property("archunit_version")}")
    testRuntimeOnly(sourceSets["client"].output)
}

// The game test source set also gets the client's runtime dependencies; Sable (see above) must stay off the test
// server. Loom renames remapped mods to the group remapped.<group>; Jade is compile-only, so nothing else is affected.
configurations.named("gametestRuntimeClasspath") {
    exclude(group = "remapped.maven.modrinth")
}

// ---- Test coverage (JaCoCo): check prints the line coverage of api ----
// Both the unit tests and the game test server record coverage; api is covered by the game tests (API turrets). The
// core (omtteam.openmodularturrets.core) comes from OpenModularCore, whose own build measures its coverage, so only this
// repository's classes count here. Active version only (both versions share the source).
// Report: versions/<v>/build/reports/jacoco/test/html/index.html
val coverageActive = stonecutter.current.isActive
val coveredPackages = listOf("omtteam/openmodularturrets/api/**")
jacoco {
    toolVersion = property("jacoco_version") as String
}
tasks.test {
    extensions.configure<JacocoTaskExtension> { isEnabled = coverageActive }
}
tasks.named<JavaExec>("runGameTest") {
    jacoco.applyTo(this)
    extensions.configure<JacocoTaskExtension> { isEnabled = coverageActive }
}
tasks.jacocoTestReport {
    enabled = coverageActive
    dependsOn(tasks.test, tasks.named("runGameTest"))
    executionData.setFrom(layout.buildDirectory.files("jacoco/test.exec", "jacoco/runGameTest.exec"))
    sourceDirectories.setFrom(sourceSets.main.get().java.srcDirs)
    // main's own classes only: its classes folders also hold the bundled OpenModularCore classes (mod-common)
    classDirectories.setFrom(sourceSets.main.get().java.classesDirectory.map { dir -> dir.asFileTree.matching { include(coveredPackages) } })
    reports {
        csv.required = true
        xml.required = true
        html.required = true
    }
    val csv = reports.csv.outputLocation
    val html = reports.html.outputLocation
    doLast {
        // line coverage per package, summed per area
        val rows = csv.get().asFile.readLines().drop(1).map { it.split(',') }
        val byPackage = sortedMapOf<String, IntArray>()
        for (row in rows) {
            val lines = byPackage.getOrPut(row[1].removePrefix("omtteam.openmodularturrets.")) { IntArray(2) }
            lines[0] += row[7].toInt() // LINE_MISSED
            lines[1] += row[8].toInt() // LINE_COVERED
        }
        fun percent(l: IntArray) = "%5.1f%%  (%d/%d lines)".format(100.0 * l[1] / maxOf(1, l[0] + l[1]), l[1], l[0] + l[1])
        println("Test coverage (unit and game tests):")
        for (area in listOf("api")) {
            val packages = byPackage.filterKeys { it == area || it.startsWith("$area.") }
            val total = IntArray(2).also { t -> packages.values.forEach { t[0] += it[0]; t[1] += it[1] } }
            println("  %-24s %s".format(area, percent(total)))
            packages.forEach { (name, lines) -> println("    %-22s %s".format(name, percent(lines))) }
        }
        println("  Report: ${html.get().asFile.resolve("index.html").toURI()}")
    }
}
tasks.check {
    dependsOn(tasks.jacocoTestReport)
}

// Report only for now: the limits in gradle/pmd/ruleset.xml are not enforced yet. Runs for the active version only
// (both versions share the source); reports in versions/<v>/build/reports/pmd/{main,client}.html
pmd {
    toolVersion = property("pmd_version") as String
    sourceSets = listOf(project.sourceSets["main"], project.sourceSets["client"])
    ruleSets = listOf()
    ruleSetFiles = rootProject.files("gradle/pmd/ruleset.xml")
    isIgnoreFailures = true
    isConsoleOutput = false
}
tasks.withType<Pmd>().configureEach {
    enabled = stonecutter.current.isActive
    reports {
        html.required = true
        xml.required = true
    }
}

publishMods {
    file = tasks.remapJar.flatMap { it.archiveFile }
    additionalFiles.from(tasks.remapSourcesJar.flatMap { it.archiveFile })
}
