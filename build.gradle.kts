plugins {
    id("net.fabricmc.fabric-loom-remap")
    id("me.modmuss50.mod-publish-plugin")
    `maven-publish`
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
}

dependencies {
    minecraft("com.mojang:minecraft:$minecraftVersion")
    mappings(loom.officialMojangMappings())
    modImplementation("net.fabricmc:fabric-loader:$loaderVersion")
    modImplementation("net.fabricmc.fabric-api:fabric-api:${property("fabric_api_version")}")
    // Energy API (jar-in-jar)
    modImplementation("teamreborn:energy:${property("team_reborn_energy_version")}")
    include("teamreborn:energy:${property("team_reborn_energy_version")}")

    // Unit tests for the core game logic (test only, not shipped)
    testImplementation(platform("org.junit:junit-bom:${property("junit_version")}"))
    testImplementation("org.junit.jupiter:junit-jupiter")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
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
            requires("fabric-api")
        }
    }
}
