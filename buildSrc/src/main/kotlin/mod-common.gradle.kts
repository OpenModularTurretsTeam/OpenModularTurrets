// Build logic shared by every Stonecutter node, whatever its loader. The loader build scripts
// (build.fabric.gradle.kts, build.neoforge.gradle.kts, build.forge.gradle.kts) apply this first, then their loader plugin.
// Mod-independent: all names come from gradle.properties, so other mods can copy buildSrc as it is.
plugins {
    java
    id("me.modmuss50.mod-publish-plugin")
}

/** The loaders this setup knows; the node name ends with one of them ("1.21.1-neoforge"). */
val loaders = listOf("fabric", "neoforge", "forge")
val loader = project.name.substringAfterLast('-')
require(loader in loaders) { "Stonecutter node '${project.name}' must end with one of $loaders" }

val modId = property("mod_id") as String
val modName = property("mod_name") as String
val minecraftVersion = property("minecraft_version") as String
val javaVersion = (property("java_version") as String).toInt()
/** The base package, derived from maven_group (they are the same in our mods). */
val basePackage = property("maven_group") as String

version = "${property("mod_version")}+$minecraftVersion-$loader"
group = basePackage
base.archivesName = property("archives_base_name") as String
// Jar names are <archives_base_name>-<mod_version>-mc<minecraft version>-<loader>; the mod metadata keeps the "+" version
val archiveVersionName = "${property("mod_version")}-mc$minecraftVersion-$loader"
tasks.withType<AbstractArchiveTask>().configureEach { archiveVersion = archiveVersionName }

repositories {
    // Optional integrations such as Jade, only from Modrinth's maven
    maven("https://api.modrinth.com/maven") {
        name = "Modrinth"
        content { includeGroup("maven.modrinth") }
    }
}

// Loader-specific classes live in .../platform/<loader>/ packages (main and client); other loaders' packages
// are left out of this node's compilation entirely, so they never need Stonecutter comments.
sourceSets.configureEach {
    java {
        (loaders - loader).forEach { exclude("**/platform/$it/**") }
    }
}

dependencies {
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

/** Mod metadata file of each loader; the others are left out of this node's jar. */
val metadataFiles = mapOf(
    "fabric" to "fabric.mod.json",
    "neoforge" to "META-INF/neoforge.mods.toml",
    "forge" to "META-INF/mods.toml",
)

tasks {
    processResources {
        // Values a metadata template may use; a key a loader does not have is empty
        val props = mapOf(
            "version" to project.version.toString(),
            "mod_id" to modId,
            "mod_name" to modName,
            "java_version" to javaVersion.toString(),
            "minecraft_dependency" to findProperty("minecraft_dependency")?.toString().orEmpty(),
            "loader_version" to findProperty("fabric_loader_version")?.toString().orEmpty(),
            "neoforge_version" to findProperty("neoforge_version")?.toString().orEmpty(),
            "neoforge_loader_range" to findProperty("neoforge_loader_range")?.toString().orEmpty(),
            "forge_dependency" to findProperty("forge_dependency")?.toString().orEmpty(),
            "forge_loader_range" to findProperty("forge_loader_range")?.toString().orEmpty(),
        )
        inputs.properties(props)
        filesMatching(metadataFiles.getValue(loader)) { expand(props) }
    }

    // Every source set (main, test mods) may carry the metadata of all loaders; each jar keeps only its own
    withType<ProcessResources>().configureEach {
        (metadataFiles - loader).values.forEach { exclude(it) }
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
        description = "Fails if the core package imports Minecraft, a loader or other game classes"
        val coreDir = rootProject.file("src/main/java/${basePackage.replace('.', '/')}/core")
        val root = rootProject.projectDir
        inputs.dir(coreDir)
        doLast {
            val ownPackage = Regex.escape(basePackage)
            val forbidden = Regex("""^\s*import\s+(static\s+)?(net\.minecraft|net\.fabricmc|net\.neoforged|net\.minecraftforge|com\.mojang|team\.reborn|$ownPackage\.(?!core\b))""")
            val violations = coreDir.walkTopDown().filter { it.extension == "java" }.flatMap { file ->
                file.readLines().withIndex().filter { forbidden.containsMatchIn(it.value) }
                    .map { "${file.relativeTo(root)}:${it.index + 1}: ${it.value.trim()}" }
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
        // License files bundled into the jar: bundled_licenses (comma-separated), default all three
        val licenses = (findProperty("bundled_licenses") as String? ?: "LICENSE,LICENSE-ART,LICENSE-DUAL")
            .split(',').map { it.trim() }
        from(rootProject.files(licenses)) {
            rename { "${it}_${base.archivesName.get()}" }
        }
    }
}

// ---- Publishing to CurseForge / Modrinth ----
// The loader script sets `file` and `additionalFiles` (Loom: remapJar, ModDevGradle: jar, legacy ModDevGradle: reobfJar).

/** A token from the environment (CI) or ~/.gradle/gradle.properties (local); never from the repository. */
fun token(environmentVariable: String, gradleProperty: String): Provider<String> =
    providers.environmentVariable(environmentVariable).orElse(providers.gradleProperty(gradleProperty))

val curseforgeToken = token("CURSEFORGE_TOKEN", "curseforge_token")
val modrinthToken = token("MODRINTH_TOKEN", "modrinth_token")
val curseforgeProjectId = (findProperty("curseforge_project_id") as String?).orEmpty()
val modrinthProjectId = (findProperty("modrinth_project_id") as String?).orEmpty()
/** publish_enabled=false in a node's gradle.properties keeps that node a dry run, tokens or not. */
val publishEnabled = (findProperty("publish_enabled") as String?)?.toBoolean() ?: true
/** Mods the uploaded file requires on its loader. */
val requiredMods = if (loader == "fabric") listOf("fabric-api") else emptyList()

/** True if this site would upload without a token (an empty project id skips the site entirely). */
fun missingToken(projectId: String, token: Provider<String>): Provider<Boolean> =
    if (projectId.isEmpty()) providers.provider { false } else token.map { false }.orElse(true)

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
    // Shown like the jar file name (<archives_base_name>-<mod_version>-mc<minecraft version>-<loader>)
    displayName = "${base.archivesName.get()}-$archiveVersionName"
    version = project.version.toString()
    changelog = releaseNotes
    type = when (property("release_type")) {
        "release" -> STABLE
        "beta" -> BETA
        else -> ALPHA
    }
    modLoaders.add(loader)
    // Uploads only run for real when publishing is enabled and the tokens of all configured sites are present
    dryRun = missingToken(curseforgeProjectId, curseforgeToken)
        .zip(missingToken(modrinthProjectId, modrinthToken)) { curseforgeDry, modrinthDry ->
            !publishEnabled || curseforgeDry || modrinthDry
        }

    if (curseforgeProjectId.isNotEmpty()) {
        curseforge {
            projectId = curseforgeProjectId
            accessToken = curseforgeToken
            minecraftVersions.add(minecraftVersion)
            javaVersions.add(JavaVersion.toVersion(javaVersion))
            client = true
            server = true
            requiredMods.forEach { requires(it) }
        }
    }
    if (modrinthProjectId.isNotEmpty()) {
        modrinth {
            projectId = modrinthProjectId
            accessToken = modrinthToken
            minecraftVersions.add(minecraftVersion)
            environment = CLIENT_AND_SERVER
            requiredMods.forEach { requires(it) }
        }
    }
}
