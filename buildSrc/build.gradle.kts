// Convention plugins shared by the loader build scripts (build.fabric.gradle.kts, build.neoforge.gradle.kts).
// Nothing here is mod-specific: everything comes from gradle.properties, so this folder can be copied to other mods.
// The loader plugins (Loom, ModDevGradle) are NOT on this classpath; the loader scripts apply them by version.
plugins {
    `kotlin-dsl`
}

repositories {
    gradlePluginPortal()
}

dependencies {
    // Uploads to CurseForge / Modrinth (build-time only)
    implementation("me.modmuss50.mod-publish-plugin:me.modmuss50.mod-publish-plugin.gradle.plugin:2.2.1")
}
