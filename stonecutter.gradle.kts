plugins {
    id("dev.kikugie.stonecutter")
    // Loader plugins, applied by build.fabric.gradle.kts / build.neoforge.gradle.kts
    // (the publish plugin comes from buildSrc, see buildSrc/build.gradle.kts)
    id("net.fabricmc.fabric-loom-remap") version "1.18.4" apply false
    id("net.neoforged.moddev") version "2.0.148" apply false
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
        target("*.gradle.kts", "buildSrc/*.gradle.kts", "buildSrc/src/**/*.gradle.kts", "versions/*/*.gradle.kts", ".gitignore",
            "src/**/*.json", "src/**/*.properties", "src/**/*.toml")
        leadingTabsToSpaces(2)
        trimTrailingWhitespace()
        endWithNewline()
    }
}

stonecutter active "1.21.1-fabric"

// Loader constants for conditions: //? if fabric { ... //? if neoforge {
// Prefer whole classes in platform/<loader>/ packages (left out of the other loaders' builds) over conditions.
stonecutter parameters {
    constants.match(current.project.substringAfterLast('-'), "fabric", "neoforge")
}

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

// ---- Dependency map (./gradlew dependencyMap) ----

/**
 * Writes an interactive map of the package dependencies to build/reports/dependency-map/index.html: one node per
 * package (tests as their own nodes), one arrow per package pair with the class imports behind it, the libraries each
 * package uses and the architecture rules (core isolation, no client code in main). Built from the active import lines
 * of src/{main,client,gametest,test}; imports in comments (inactive Stonecutter branches) do not count. Cycles and
 * layers are worked out by the page (gradle/dependency-map/template.html).
 */
abstract class DependencyMapTask : DefaultTask() {
    @get:InputDirectory
    @get:PathSensitive(PathSensitivity.RELATIVE)
    abstract val sources: DirectoryProperty

    @get:InputFile
    @get:PathSensitive(PathSensitivity.RELATIVE)
    abstract val template: RegularFileProperty

    @get:OutputFile
    abstract val output: RegularFileProperty

    private class SourceClass(val fqcn: String, val set: String, val imports: List<String>, val lines: Int, val path: String)

    @TaskAction
    fun write() {
        val base = "omtteam.openmodularturrets"
        val libraries = listOf(
            "net.minecraft." to "Minecraft", "com.mojang." to "Minecraft", "net.fabricmc." to "Fabric", "net.neoforged." to "NeoForge",
            "team.reborn." to "Team Reborn Energy", "snownee.jade." to "Jade", "com.google.gson." to "Gson",
            "org.junit." to "JUnit",
        )
        val importLine = Regex("""^import\s+(static\s+)?([\w.]+?)(\.\*)?;""")
        val srcDir = sources.get().asFile
        val repo = srcDir.parentFile

        val classes = linkedMapOf<String, SourceClass>()
        for (set in listOf("main", "client", "gametest", "test")) {
            val dir = srcDir.resolve("$set/java")
            if (!dir.isDirectory) continue
            dir.walkTopDown().filter { it.isFile && it.extension == "java" && it.name != "package-info.java" }
                .sortedBy { it.path }.forEach { file ->
                    val fqcn = file.relativeTo(dir).path.removeSuffix(".java").replace(java.io.File.separatorChar, '.')
                    val text = file.readText()
                    val imports = mutableListOf<String>()
                    var inComment = false
                    for (line in text.lines()) {
                        val trimmed = line.trimStart()
                        if (inComment) {
                            if ("*/" in line) inComment = false
                            continue
                        }
                        if (trimmed.startsWith("/*")) {
                            inComment = "*/" !in trimmed.substring(2)
                            continue
                        }
                        val match = importLine.find(line) ?: continue
                        val target = match.groupValues[2]
                        imports += if (match.groupValues[1].isNotEmpty()) target.substringBeforeLast('.') else target
                    }
                    classes[fqcn] = SourceClass(fqcn, set, imports, text.count { it == '\n' } + 1, file.relativeTo(repo).path)
                }
        }

        // tests share packages with the main code, so they are keyed apart
        fun packageOf(c: SourceClass) = c.fqcn.substringBeforeLast('.').let { if (c.set == "test") "test:$it" else it }
        fun short(pkg: String): String = when {
            pkg.startsWith("test:") -> "test: " + short(pkg.removePrefix("test:"))
            pkg == base -> "(root)"
            pkg.startsWith("$base.") -> pkg.removePrefix("$base.")
            else -> pkg
        }
        fun simpleName(fqcn: String) = fqcn.substringAfterLast('.')
        /** An imported name -> the class file of this mod it refers to (nested classes map to their file). */
        fun resolve(target: String): String? {
            var candidate = target
            while (true) {
                if (candidate in classes) return candidate
                val dot = candidate.lastIndexOf('.')
                if (dot < 0) return null
                candidate = candidate.substring(0, dot)
            }
        }
        fun isCore(id: String) = id.startsWith("core")

        class Node(val id: String, var set: String) {
            val classes = mutableListOf<Map<String, Any>>()
            var lines = 0
            val external = sortedMapOf<String, java.util.SortedSet<String>>()
        }
        val nodes = sortedMapOf<String, Node>()
        val edges = sortedMapOf<String, Triple<String, String, java.util.SortedSet<String>>>()
        for (c in classes.values) {
            val pkg = packageOf(c)
            val node = nodes.getOrPut(pkg) { Node(short(pkg), c.set) }
            if (node.set != c.set) node.set = "mixed"
            node.classes += mapOf("name" to simpleName(c.fqcn), "lines" to c.lines, "path" to c.path)
            node.lines += c.lines
            for (target in c.imports) {
                if (target == base || target.startsWith("$base.")) {
                    val to = resolve(target) ?: continue
                    val toPkg = packageOf(classes.getValue(to))
                    if (toPkg == pkg) continue
                    edges.getOrPut("$pkg→$toPkg") { Triple(short(pkg), short(toPkg), sortedSetOf()) }
                        .third += simpleName(c.fqcn) + "\u0000" + simpleName(to)
                } else {
                    val group = libraries.firstOrNull { target.startsWith(it.first) }?.second ?: continue
                    node.external.getOrPut(group) { sortedSetOf() } += simpleName(c.fqcn)
                }
            }
        }
        val setOf = nodes.values.associate { it.id to it.set }
        val data = mapOf(
            "classCount" to classes.size,
            "nodes" to nodes.values.map { n ->
                mapOf(
                    "id" to n.id, "set" to n.set, "lines" to n.lines,
                    "classes" to n.classes.sortedBy { it["name"] as String },
                    "external" to n.external.mapValues { it.value.toList() },
                    "violations" to n.external.keys.filter { isCore(n.id) && it != "JUnit" }.map { "core uses $it" },
                )
            },
            "edges" to edges.values.map { (source, target, imports) ->
                mapOf(
                    "source" to source, "target" to target,
                    "imports" to imports.map { it.split('\u0000') },
                    "violations" to listOfNotNull(
                        "core depends on non-core code".takeIf { isCore(source) && !isCore(target) },
                        "main code depends on client code".takeIf { setOf[source] == "main" && setOf[target] == "client" },
                    ),
                )
            },
        )
        val json = groovy.json.JsonOutput.toJson(data).replace("</", "<\\/")
        val page = template.get().asFile.readText()
        check("/*DATA*/null" in page) { "The template has no /*DATA*/null placeholder" }
        val file = output.get().asFile
        file.parentFile.mkdirs()
        file.writeText(
            "<!doctype html>\n<html lang=\"en\">\n<head>\n<meta charset=\"utf-8\">\n" +
                    "<meta name=\"viewport\" content=\"width=device-width, initial-scale=1, viewport-fit=cover\">\n" +
                    page.replace("/*DATA*/null", json) + "\n</html>\n"
        )
        println("Dependency map: ${nodes.size} packages, ${edges.size} dependencies, ${classes.size} classes -> ${file.toURI()}")
    }
}

tasks.register<DependencyMapTask>("dependencyMap") {
    group = "documentation"
    description = "Writes an interactive package dependency map to build/reports/dependency-map/index.html"
    sources = layout.projectDirectory.dir("src")
    template = layout.projectDirectory.file("gradle/dependency-map/template.html")
    output = layout.buildDirectory.file("reports/dependency-map/index.html")
}
