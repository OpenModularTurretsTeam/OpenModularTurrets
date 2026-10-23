import groovy.json.JsonOutput
import groovy.json.JsonSlurper
import org.gradle.api.GradleException
import java.io.File

/**
 * Copies the Fabric datagen output [fabricData] into [output] for a loader without datagen of its own (NeoForge, Forge).
 * The data only differs in its load conditions: "fabric:load_conditions" becomes [conditionsKey] ("neoforge:conditions"
 * on NeoForge, "conditions" on Forge 1.20.1), with "condition" -> "type" in each entry.
 * Only conditions in [namespace] convert: the mod registers them under the same id on every loader. Any other condition
 * (e.g. fabric:all_mods_loaded) fails the build instead of producing wrong data.
 */
fun convertFabricData(fabricData: File, output: File, namespace: String, conditionsKey: String) {
    output.deleteRecursively()
    fabricData.walkTopDown().onEnter { it.name != ".cache" }.filter { it.isFile }.forEach { file ->
        val target = output.resolve(file.relativeTo(fabricData))
        target.parentFile.mkdirs()
        if (file.extension != "json" || !file.readText().contains("\"fabric:")) {
            file.copyTo(target)
            return@forEach
        }
        @Suppress("UNCHECKED_CAST")
        val json = JsonSlurper().parse(file) as Map<String, Any?>
        val converted = LinkedHashMap<String, Any?>()
        json.forEach { (key, value) ->
            if (key == "fabric:load_conditions") {
                @Suppress("UNCHECKED_CAST")
                converted[conditionsKey] = (value as List<Map<String, Any?>>).map { condition ->
                    val id = condition["condition"] as String
                    if (!id.startsWith("$namespace:")) {
                        throw GradleException("${file.relativeTo(fabricData)}: no counterpart for condition $id")
                    }
                    linkedMapOf<String, Any?>("type" to id) + condition.filterKeys { it != "condition" }
                }
            } else {
                converted[key] = value
            }
        }
        val text = JsonOutput.prettyPrint(JsonOutput.toJson(converted)) + "\n"
        if (text.contains("\"fabric:")) {
            throw GradleException("${file.relativeTo(fabricData)}: Fabric-specific data left after the conversion")
        }
        target.writeText(text)
    }
}
