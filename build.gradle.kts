plugins {
    id("net.fabricmc.fabric-loom-remap")
    id("me.modmuss50.mod-publish-plugin")
    `maven-publish`
    // Code quality report (gradle/pmd/ruleset.xml): complexity and size, report only
    pmd
    // Test coverage of core and api, from the unit tests and the game tests
    jacoco
}

val modId = property("mod_id") as String
val minecraftVersion = property("minecraft_version") as String
val javaVersion = (property("java_version") as String).toInt()
val minecraftDependency = property("minecraft_dependency") as String
val loaderVersion = property("fabric_loader_version") as String
val modName = property("mod_name") as String

version = "${property("mod_version")}+$minecraftVersion"
group = property("maven_group") as String
base.archivesName = property("archives_base_name") as String

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
        client = true
        // versions/<v>/src/main/generated, committed per version
        outputDirectory = project.file("src/main/generated")
    }
    // Server game tests in src/gametest (./gradlew :<v>:runGameTest, also part of check)
    configureTests {
        createSourceSet = true
        modId = "$modId-gametest"
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

repositories {
    // Jade (optional integration, compile only)
    exclusiveContent {
        forRepository { maven("https://api.modrinth.com/maven") { name = "Modrinth" } }
        filter { includeGroup("maven.modrinth") }
    }
}

dependencies {
    minecraft("com.mojang:minecraft:$minecraftVersion")
    mappings(loom.officialMojangMappings())
    modImplementation("net.fabricmc:fabric-loader:$loaderVersion")
    modImplementation("net.fabricmc.fabric-api:fabric-api:${property("fabric_api_version")}")
    // Energy API (jar-in-jar)
    modImplementation("teamreborn:energy:${property("team_reborn_energy_version")}")
    include("teamreborn:energy:${property("team_reborn_energy_version")}")

    // Jade tooltips (optional, not bundled: the mod only registers the plugin when Jade is installed)
    modCompileOnly("maven.modrinth:jade:${property("jade_version")}")

    // Unit tests for the core game logic (test only, not shipped)
    testImplementation(platform("org.junit:junit-bom:${property("junit_version")}"))
    testImplementation("org.junit.jupiter:junit-jupiter")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
    // Architecture rules (src/test/.../architecture): they read the compiled main and client classes
    testImplementation("com.tngtech.archunit:archunit-junit5:${property("archunit_version")}")
    testRuntimeOnly(sourceSets["client"].output)
}

// ---- Test coverage (JaCoCo): check prints the line coverage of core and api ----
// Both the unit tests and the game test server record coverage; core is mostly covered by the unit tests, api only
// by the game tests (API turrets). Active version only (both versions share the source).
// Report: versions/<v>/build/reports/jacoco/test/html/index.html
val coverageActive = stonecutter.current.isActive
val coveredPackages = listOf("omtteam/openmodularturrets/core/**", "omtteam/openmodularturrets/api/**")
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
    classDirectories.setFrom(sourceSets.main.get().output.classesDirs.asFileTree.matching { include(coveredPackages) })
    reports {
        csv.required = true
        xml.required = true
        html.required = true
    }
    val csv = reports.csv.outputLocation
    val html = reports.html.outputLocation
    doLast {
        // line coverage per package, summed per area (core, api)
        val rows = csv.get().asFile.readLines().drop(1).map { it.split(',') }
        val byPackage = sortedMapOf<String, IntArray>()
        for (row in rows) {
            val lines = byPackage.getOrPut(row[1].removePrefix("omtteam.openmodularturrets.")) { IntArray(2) }
            lines[0] += row[7].toInt() // LINE_MISSED
            lines[1] += row[8].toInt() // LINE_COVERED
        }
        fun percent(l: IntArray) = "%5.1f%%  (%d/%d lines)".format(100.0 * l[1] / maxOf(1, l[0] + l[1]), l[1], l[0] + l[1])
        println("Test coverage (unit and game tests):")
        for (area in listOf("core", "api")) {
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

// ---- Mutation testing (PIT): ./gradlew :1.21.1:pitest, on demand ----
// Plants small mistakes in core (flipped conditions, changed boundaries, removed calls) and checks whether a unit test
// fails. Surviving mistakes show behaviour the tests do not pin down. Report only, not part of check.
// Report: versions/<v>/build/reports/pitest/index.html
val pitest = configurations.create("pitest") {
    isCanBeConsumed = false
}
dependencies {
    pitest("org.pitest:pitest-command-line:${property("pitest_version")}")
    pitest("org.pitest:pitest-junit5-plugin:${property("pitest_junit5_version")}")
}
tasks.register<JavaExec>("pitest") {
    group = "verification"
    description = "Mutation testing of the core logic (report in build/reports/pitest)"
    dependsOn(tasks.testClasses)
    classpath = pitest + sourceSets.test.get().runtimeClasspath
    mainClass = "org.pitest.mutationtest.commandline.MutationCoverageReport"
    javaLauncher = javaToolchains.launcherFor(java.toolchain)
    val reportDir = layout.buildDirectory.dir("reports/pitest")
    val sources = sourceSets.main.get().java.srcDirs
    outputs.dir(reportDir)
    argumentProviders.add(CommandLineArgumentProvider {
        listOf(
            "--reportDir", reportDir.get().asFile.path,
            "--targetClasses", "omtteam.openmodularturrets.core.*",
            "--targetTests", "omtteam.openmodularturrets.core.*",
            // the tests and their fakes share the core packages
            "--excludedClasses", "*Test,*Test\$*,*.Fake*",
            "--sourceDirs", sources.joinToString(","),
            "--threads", Runtime.getRuntime().availableProcessors().toString(),
            "--outputFormats", "HTML,XML",
            "--timestampedReports=false",
        )
    })
    doLast {
        println("PIT report: ${reportDir.get().asFile.resolve("index.html").toURI()}")
    }
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

java {
    withSourcesJar()
    sourceCompatibility = JavaVersion.toVersion(javaVersion)
    targetCompatibility = JavaVersion.toVersion(javaVersion)
    toolchain {
        languageVersion = JavaLanguageVersion.of(javaVersion)
    }
}

tasks {
    processResources {
        val props = mapOf(
            "version" to project.version.toString(),
            "minecraft_dependency" to minecraftDependency,
            "java_version" to javaVersion.toString(),
            "loader_version" to loaderVersion,
        )
        inputs.properties(props)
        filesMatching("fabric.mod.json") { expand(props) }
    }

    withType<JavaCompile>().configureEach {
        options.encoding = "UTF-8"
        options.release = javaVersion
    }

    test {
        useJUnitPlatform()
    }

    // The core game logic must stay independent of Minecraft so it can be unit-tested in isolation
    val verifyCoreIsolation = register("verifyCoreIsolation") {
        group = "verification"
        description = "Fails if the core package imports Minecraft, Fabric or other game classes"
        val coreDir = rootProject.file("src/main/java/omtteam/openmodularturrets/core")
        inputs.dir(coreDir)
        doLast {
            val forbidden = Regex("""^\s*import\s+(static\s+)?(net\.minecraft|net\.fabricmc|com\.mojang|team\.reborn|omtteam\.openmodularturrets\.(?!core\b))""")
            val violations = coreDir.walkTopDown().filter { it.extension == "java" }.flatMap { file ->
                file.readLines().withIndex().filter { forbidden.containsMatchIn(it.value) }
                    .map { "${file.relativeTo(rootProject.projectDir)}:${it.index + 1}: ${it.value.trim()}" }
            }.toList()
            if (violations.isNotEmpty()) {
                throw GradleException("Core logic must not depend on the game:\n" + violations.joinToString("\n"))
            }
        }
    }

    check {
        dependsOn(verifyCoreIsolation)
    }

    jar {
        inputs.property("archivesName", base.archivesName)
        from(rootProject.file("LICENSE")) { rename { "${it}_${base.archivesName.get()}" } }
    }
}

// ---- Publishing to CurseForge / Modrinth ----

/** A token from the environment (CI) or ~/.gradle/gradle.properties (local); never from the repository. */
fun token(environmentVariable: String, gradleProperty: String): Provider<String> =
    providers.environmentVariable(environmentVariable).orElse(providers.gradleProperty(gradleProperty))

val curseforgeToken = token("CURSEFORGE_TOKEN", "curseforge_token")
val modrinthToken = token("MODRINTH_TOKEN", "modrinth_token")
val modrinthProjectId = (findProperty("modrinth_project_id") as String?).orEmpty()

/**
 * The CHANGELOG.md section of this mod_version (without its heading). Run ./gradlew releaseChangelog first;
 * publishing fails if the section is missing.
 */
val releaseNotes: Provider<String> = providers.fileContents(rootProject.layout.projectDirectory.file("CHANGELOG.md")).asText
    .map { text ->
        val version = property("mod_version") as String
        val headings = Regex("""(?m)^## \[([^\]]+)].*$""").findAll(text).toList()
        val heading = headings.firstOrNull { it.groupValues[1] == version }
            ?: throw GradleException("CHANGELOG.md has no section for $version; run ./gradlew releaseChangelog first")
        val end = headings.firstOrNull { it.range.first > heading.range.first }?.range?.first ?: text.length
        text.substring(heading.range.last + 1, end).trim()
    }

publishMods {
    file = tasks.remapJar.flatMap { it.archiveFile }
    additionalFiles.from(tasks.remapSourcesJar.flatMap { it.archiveFile })
    displayName = "$modName ${project.version}"
    version = project.version.toString()
    changelog = releaseNotes
    type = when (property("release_type")) {
        "release" -> STABLE
        "alpha" -> ALPHA
        else -> BETA
    }
    modLoaders.add("fabric")
    // Uploads only run for real when the tokens are present
    dryRun = curseforgeToken.map { false }.orElse(true).zip(
        if (modrinthProjectId.isEmpty()) providers.provider { false } else modrinthToken.map { false }.orElse(true)
    ) { curseforgeDry, modrinthDry -> curseforgeDry || modrinthDry }

    curseforge {
        projectId = property("curseforge_project_id") as String
        accessToken = curseforgeToken
        minecraftVersions.add(minecraftVersion)
        javaVersions.add(JavaVersion.toVersion(javaVersion))
        client = true
        server = true
        requires("fabric-api")
    }
    if (modrinthProjectId.isNotEmpty()) {
        modrinth {
            projectId = modrinthProjectId
            accessToken = modrinthToken
            minecraftVersions.add(minecraftVersion)
            environment = CLIENT_AND_SERVER
            requires("fabric-api")
        }
    }
}
