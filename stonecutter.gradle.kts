plugins {
    id("dev.kikugie.stonecutter")
    id("net.fabricmc.fabric-loom-remap") version "1.18.4" apply false
    // Uploads to CurseForge / Modrinth (build-time only)
    id("me.modmuss50.mod-publish-plugin") version "2.2.1" apply false
    // Formatting checks (./gradlew spotlessCheck / spotlessApply)
    id("com.diffplug.spotless") version "8.10.4"
}

// Deliberately lightweight: a full formatter (google-java-format etc.) would mangle the Stonecutter
// conditional comments (//? if ... and the /* ... *///?} blocks holding the inactive branch).
// For the same reason imports are not reordered (conditional imports sit between //? lines).
spotless {
    java {
        target("src/**/*.java", "versions/*/src/**/*.java")
        targetExclude("versions/*/src/main/generated/**")
        leadingTabsToSpaces(4)
        trimTrailingWhitespace()
        endWithNewline()
    }
    format("misc") {
        target("*.gradle.kts", "versions/*/*.gradle.kts", ".gitignore", "src/**/*.json", "src/**/*.properties")
        leadingTabsToSpaces(2)
        trimTrailingWhitespace()
        endWithNewline()
    }
}

stonecutter active "1.21.1"

// Builds every version in one go: ./gradlew chiseledBuild
tasks.register("chiseledBuild") {
    group = "build"
    description = "Builds the mod for all Stonecutter versions"
    dependsOn(stonecutter.versions.map { ":${it.project}:build" })
}

// Publishes every version to CurseForge / Modrinth: ./gradlew chiseledPublish (see publishMods in build.gradle.kts)
tasks.register("chiseledPublish") {
    group = "publishing"
    description = "Publishes the mod for all Stonecutter versions"
    dependsOn(stonecutter.versions.map { ":${it.project}:publishMods" })
}

// ---- Changelog (CHANGELOG.md, Keep a Changelog format) ----

/** Matches a version heading like "## [4.0.0] - 2026-10-12" or "## [Unreleased]". */
val versionHeading = Regex("""(?m)^## \[([^\]]+)]""")

/**
 * Adds a one-line entry under "Unreleased":
 * ./gradlew changelogEntry --type=fixed --entry="Laser turret model"
 */
abstract class ChangelogEntryTask : DefaultTask() {
    @get:Input
    @get:Option(option = "type", description = "added, changed, fixed or removed")
    abstract val type: Property<String>

    @get:Input
    @get:Option(option = "entry", description = "The change, one line")
    abstract val entry: Property<String>

    @get:Internal
    abstract val changelog: RegularFileProperty

    @TaskAction
    fun addEntry() {
        val section = type.get().lowercase().replaceFirstChar { it.uppercase() }
        require(section in listOf("Added", "Changed", "Fixed", "Removed")) { "--type must be added, changed, fixed or removed" }
        val line = "- " + entry.get().trim()
        val file = changelog.get().asFile
        val text = file.readText()
        val start = text.indexOf("## [Unreleased]")
        require(start >= 0) { "CHANGELOG.md has no '## [Unreleased]' section" }
        val headings = Regex("""(?m)^## \[""").findAll(text).map { it.range.first }.filter { it > start }
        val end = headings.firstOrNull() ?: text.length
        var block = text.substring(start, end).trimEnd()
        val heading = "### $section"
        block = if (block.contains(heading)) {
            // insert after the last entry of that subsection
            val sectionStart = block.indexOf(heading)
            val nextSection = block.indexOf("\n### ", sectionStart + heading.length).let { if (it < 0) block.length else it }
            block.substring(0, nextSection).trimEnd() + "\n" + line +
                    (if (nextSection < block.length) "\n" + block.substring(nextSection) else "")
        } else {
            "$block\n\n$heading\n\n$line"
        }
        file.writeText(text.substring(0, start) + block + "\n\n" + text.substring(end).trimStart().let { if (it.isEmpty()) "" else it })
        println("Added to $section: ${entry.get().trim()}")
    }
}

tasks.register<ChangelogEntryTask>("changelogEntry") {
    group = "release"
    description = "Adds an entry under Unreleased in CHANGELOG.md (--type=added|changed|fixed|removed --entry=\"...\")"
    changelog = layout.projectDirectory.file("CHANGELOG.md")
}

/**
 * Turns "Unreleased" into "## [<mod_version>] - <today>" and starts a new empty "Unreleased" section.
 * Run once before publishing a release.
 */
tasks.register("releaseChangelog") {
    group = "release"
    description = "Moves the Unreleased entries of CHANGELOG.md into a section for mod_version"
    val file = layout.projectDirectory.file("CHANGELOG.md").asFile
    val version = providers.gradleProperty("mod_version").get()
    doLast {
        val text = file.readText()
        val headings = versionHeading.findAll(text).toList()
        val unreleased = headings.firstOrNull { it.groupValues[1] == "Unreleased" }
            ?: throw GradleException("CHANGELOG.md has no '## [Unreleased]' section")
        if (headings.any { it.groupValues[1] == version }) {
            throw GradleException("CHANGELOG.md already has a section for $version; raise mod_version first")
        }
        val end = headings.firstOrNull { it.range.first > unreleased.range.first }?.range?.first ?: text.length
        val body = text.substring(unreleased.range.last + 1, end)
        // drop subsections without entries
        val sections = body.split(Regex("""(?m)^(?=### )""")).map { it.trim() }.filter { it.isNotEmpty() }
            .filter { section -> section.lines().any { it.startsWith("- ") } }
        if (sections.isEmpty()) throw GradleException("Nothing to release: Unreleased has no entries")
        val released = "## [$version] - ${java.time.LocalDate.now()}\n\n" + sections.joinToString("\n\n")
        val newText = text.substring(0, unreleased.range.first) + "## [Unreleased]\n\n" + released + "\n" +
                text.substring(end).let { if (it.isBlank()) "" else "\n" + it.trimStart() }
        file.writeText(newText)
        println("CHANGELOG.md: released ${sections.sumOf { s -> s.lines().count { it.startsWith("- ") } }} entries as $version")
    }
}
