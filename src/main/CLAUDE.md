# CLAUDE.md — OpenModularTurrets, resources and data (`src/main/resources`, datagen)

Moved out of the top-level `CLAUDE.md`, so that it is loaded only when you work in this folder; the top-level file has the index.
Section numbers (§) and relative paths (`../CLAUDE.md`, `../../OpenModularCore`) are those of the top-level file; paths are relative to the repository root.

## Resources strategy

- **`assets/`** (textures, models, blockstates, lang, sounds): shared by both loaders.
  Keep them handwritten in shared `src/main/resources/assets/openmodularturrets/`.
- **`data/`** (recipes, loot tables, tags, advancements): folder names and JSON formats are those of this Minecraft version.
  **Generate them with Fabric Datagen.**
  - Run datagen on the Fabric node (`:1.20.1-fabric:runDatagen`); the Forge node converts the output.
  - Configure the output directory relative to the node's project (`project.file("src/main/generated")` → `versions/<node>/src/main/generated`).
  - Add that directory as a resource source dir.
  - Commit the generated files.
- Do not handwrite files into `data/` in shared resources.
- Never generate textures or other art (only GUI textures may be generated); use a placeholder image and tell the user which images are missing.

## Second-loader design decisions: data

**Data**

- No Forge datagen: the Forge build converts the Fabric output of `datagen_from` (`fabric:load_conditions` → `conditions`, `condition` → `type`); only conditions in the mod's own namespace convert, anything else fails the build.
