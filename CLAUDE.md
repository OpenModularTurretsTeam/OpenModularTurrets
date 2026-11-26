# CLAUDE.md — OpenModularTurrets (Fabric, NeoForge; Minecraft 1.21.11)

This file guides Claude Code when working in this repository.
Read it fully before making changes.

---

## 1. Project Overview

**OpenModularTurrets (OMT)** is maintained for **Fabric and NeoForge on Minecraft 1.21.11 from a single codebase** using **Stonecutter** (its only axis here is the loader).
This is the branch `1.21.11` (split off the frozen `1.20.1+` on 2026-11-21); the sibling branches are `1.20.1` (Fabric and Forge) and `1.21.1` (Fabric and NeoForge), the 1.12.2 version is on `1.12`.
Branches, pushing and the workspace build are in the workspace `CLAUDE.md` (`../CLAUDE.md`).
Inventory expanders in 4.x hold no items themselves but raise the stack limit of the slots they touch (see "Power & items"); the 1.12.2 design (inventory expanders with their own storage, and power expanders) is not to be brought back.
The Relativistic Turret is now the Slowing Turret (see CHANGELOG); its 1.12.2 name only appears in port comments.

Core gameplay:

- **Turret bases** are the "brain".
  They handle:
  - all targeting
  - item (ammo) and power consumption
  - upgrades and addons
  - permission management
- **Turrets** are mounted on any of the **6 faces** of a base.
  They rotate (yaw and pitch, relative to their mounting face) to aim at the target the base selects, then fire.
- **Permissions** use a **trusted player** system.
  Each trusted player has a **power/access level** that limits what they can do (open the GUI, change targeting, manage other trusted players, and so on).
  The owner always has full rights.

### Allowed dependencies

- Fabric Loader and **Fabric API**.
  Fabric API, including Transfer API, Networking, Rendering and Datagen, may be used freely.
- **Team Reborn Energy** for power, as the de facto Fabric energy standard.
  Jar-in-jar it (approved).
- **Jade** (optional integration, `compat/jade/` for the server half and `client/compat/jade/` for the tooltips, registered through the `jade` entrypoint on Fabric and `@WailaPlugin` on NeoForge).
  Compile-only (`modCompileOnly`, Modrinth maven), never bundled and never required at runtime.
- **NeoForge** 21.11.x, built with **ModDevGradle** (approved by the user; build-time only).
  On NeoForge, the Fabric-only APIs above are replaced by NeoForge's own (capabilities, events, payload registration); see §4 "Loaders".
- **OpenModularCore** (the team's shared core, `../../OpenModularCore`, module `omt-core` from JitPack, approved by the user on 2026-11-16): the game rules (`omtteam.openmodularturrets.core`), the same on every Minecraft version; compile-only, its classes are bundled into the mod's jar (see "Core Game Logic Source").
  The JitPack repository is asked only for the core's group.
- **OMLib** (the user's own library, `../OMLib`, branch `1.21.11`), mounted as the git submodule `omlib` and bundled into every jar (jar-in-jar; Fabric `include`, NeoForge `jarJar`; requested by the user on 2026-10-27).
  It holds the code both mods used to share (platform services, version compat, energy and item transfer, config, the camouflage menu) and the build logic.
  The library is a mod of its own (`omlib`), declared as a required dependency in the mod metadata, and built as a nested step of the mod build, see §4 "OMLib".
- Nothing else without asking the user first.
  No Architectury, no Cloth Config, no Kotlin.

---

## 2. Tech Stack

| | 1.21.11 (Fabric) | 1.21.11 (NeoForge) |
| --- | --- | --- |
| Java | 21 | 21 |
| Loader | Fabric Loader 0.19.x | NeoForge 21.11.x |
| API | Fabric API `0.141.x+1.21.11` | – |
| Gradle plugin | Loom 1.18 | ModDevGradle 2.0 |
| Mappings | Mojang official | Mojang official (NeoForge runtime names) |

- Build system: Gradle 9.8 (Kotlin DSL) + **Stonecutter 0.9**, with Fabric Loom 1.18 (`net.fabricmc.fabric-loom-remap`, the variant for obfuscated MC versions) on the Fabric node, ModDevGradle 2.0 (`net.neoforged.moddev`) on the NeoForge node.
- Shared build logic is the convention plugin `omlib/build-logic/src/main/kotlin/mod-common.gradle.kts` (Java toolchain, metadata templating, JUnit, the OpenModularCore wiring with `extractCore` and `verifyNoCoreSources`, `checkRecipes`, licenses in the jar via `bundled_licenses`, publishing).
  It lives in the OMLib repository, mounted as the git submodule `omlib` (an included build in `pluginManagement` of `settings.gradle.kts`); change it in `../OMLib`, never inside `omlib/` (workspace `CLAUDE.md`, "OMLib in the mods", also for the shared sources); clone a mod with `--recurse-submodules` (or run `git submodule update --init`).
  The OMT-specific quality tools (ArchUnit, JaCoCo, PMD) stay in `build.fabric.gradle.kts`.
- **Gradle itself must run on Java 25+** (Loom 1.18 requirement).
  The compile toolchain (21) is resolved separately (foojay auto-provisions it if missing).
- **Use Mojang mappings** (`loom.officialMojangMappings()`).
  All class and method names in code and in this doc are Mojmap names (`BlockEntity`, `Identifier`, `AbstractContainerMenu`, and so on).
- Language: Java only.
- Toolchain: the Java toolchain (21) is configured in the shared build script from `java_version` in the node's `gradle.properties`.

Check exact latest patch versions of Loader, API, Loom and Stonecutter when setting up or bumping.
Do not hard-code guesses into the build without verifying.

---

## 3. Repository Layout

```
.
├── CLAUDE.md
├── build.fabric.gradle.kts     # Fabric node (Loom, Fabric API, datagen, GameTests, ArchUnit/JaCoCo/PMD)
├── build.neoforge.gradle.kts   # NeoForge node (ModDevGradle)
├── omlib/                      # git submodule (OMLib, branch 1.21.11): the library (src/) every mod bundles, and build-logic/ (the mod-common convention plugin); see §4 "OMLib"
├── stonecutter.gradle.kts      # Stonecutter controller (active version, loader constants, chiseled tasks)
├── settings.gradle.kts         # declares the Stonecutter nodes (<minecraft version>-<loader>)
├── gradle.properties           # shared properties (mod_id, mod_version, maven_group …)
├── versions/
│   ├── 1.21.11-fabric/
│   │   ├── gradle.properties   # minecraft_version, fabric_api, java_version=21 …
│   │   └── src/main/generated/ # datagen output (committed; NeoForge converts it, datagen_from)
│   └── 1.21.11-neoforge/
│       └── gradle.properties   # neoforge_version, minecraft_dependency (Maven range) …
└── src/
    ├── main/java/omtteam/openmodularturrets/...
    ├── main/resources/         # SHARED assets + fabric.mod.json and META-INF/neoforge.mods.toml (templated)
    ├── client/java/...         # client-only code (split source sets)
    ├── test/java/...           # JUnit tests: architecture rules, config (Fabric node; the core's tests are in OpenModularCore)
    └── gametest/               # server game tests for both loaders (own mod id openmodularturrets_gametest);
                                # gametest/java: also the game test framework of 1.21.11 (stand-in @GameTest, GameTestCases)
```

- **Mod ID:** `openmodularturrets`.
  **Base package:** `omtteam.openmodularturrets`.
- Use Loom **split environment source sets** (`splitEnvironmentSourceSets()`).
  Client-only code (renderers, screens, models, client networking handlers) goes in `src/client/java`.
  Never reference client classes from `main`.
- `fabric.mod.json` and `neoforge.mods.toml` are templated via `processResources`.
  Inject `${version}`, `${minecraft_dependency}` and `${java_version}` from the node's properties.

---

## 4. Stonecutter

### Setup (`settings.gradle.kts`)

```kotlin
plugins { id("dev.kikugie.stonecutter") version "0.9.8" }

stonecutter {
    create(rootProject) {
        versions("1.21.11-fabric" to "1.21.11", "1.21.11-neoforge" to "1.21.11")
        mapBuilds { _, node -> "build.${node.project.substringAfterLast('-')}.gradle.kts" }
        vcsVersion = "1.21.11-fabric" // the version the committed source is written for
    }
}
```

The Stonecutter DSL changes between releases.
If this does not match the installed version, check the Stonecutter docs and update both this file and the build.

### Rules

- **Committed source must always be in the `vcsVersion` (`1.21.11-fabric`) state.** Before committing, switch back if you switched to another node (`./gradlew "Reset active project"` or `"Set active project to 1.21.11-fabric"`).
- The only axis is the loader: conditions use the loader constants (`//? if fabric {`, `//? if neoforge {`), the other loader's branch sits in a `/* … */` comment exactly as Stonecutter expects:

  ```java
  //? if fabric {
  import net.fabricmc.fabric.api.entity.FakePlayer;
  //?}
  //? if neoforge {
  /*import net.neoforged.neoforge.common.util.FakePlayer;
  *///?}
  ```

  Prefer letting Stonecutter generate this by switching nodes over hand-writing it.
- **Minimize conditional blocks.** Hide loader differences behind the services in `omtteam.openmodularturrets.platform` and whole classes in `platform/<loader>/` rather than sprinkling `//? if` across gameplay code.
  Gameplay logic (targeting, permissions, consumption) should be loader-agnostic.
  The OMLib compat helpers (`VersionCompat`, `NbtCompat`, `BlockCompat`, `ClientCompat`, `CompatBlockEntity`, …) stay as the porting layer between the version branches: call sites are identical on all of them, so the game layer is ported by hand (see §5).
- There are no Minecraft-version conditions on this branch: version differences are resolved by branch, and `mod-common` fails the build if a version source folder (`src/<set>/mc<first>-<last>/java`) exists.
- The source uses the 1.21.11 Mojang names (`Identifier`, `noCollision`, `SLOWNESS`, `animal.equine`, `needsSync`, `net.minecraft.util.Util`); there are no Stonecutter replacements.
- Node *properties* live in `versions/<node>/gradle.properties`.
  Read them in the build via `property("…")`.

### Loaders: jar names and publishing

- NeoForge publishing is active (enabled by the user on 2026-10-24); `publish_enabled=false` in a node's `gradle.properties` would turn that node back into a dry run even with tokens.
- The mod version is `<mod_version>+<minecraft version>-<loader>`, so the jars of both loaders can be told apart.
  The jar files are named `<mod_id>-<mod_version>-mc<minecraft version>-<loader>.jar` (`archiveVersion` in `mod-common`); the release title on CurseForge and Modrinth is the jar name without `.jar` (`displayName`), while the mod metadata and the published version number keep the `+` version.

### Commands

```bash
./gradlew "Set active project to 1.21.11-neoforge" # switch the source to the NeoForge node
./gradlew "Reset active project"                   # switch back (VCS state, 1.21.11-fabric)
./gradlew chiseledBuild                            # build BOTH nodes (custom task in stonecutter.gradle.kts)
./gradlew :1.21.11-fabric:build                    # build a single node
./gradlew :1.21.11-fabric:runClient                # run client for a node (shared run dir)
./gradlew :1.21.11-fabric:runServer
./gradlew :1.21.11-fabric:runGameTest              # server game tests for one node (also run by check / build)
./gradlew :1.21.11-neoforge:runGameTestServer      # the same game tests on NeoForge (also run by check / build)
./gradlew :1.21.11-fabric:runDatagen               # regenerate data (the NeoForge node converts this output)
./gradlew :1.21.11-neoforge:runClient              # NeoForge client (run dir versions/1.21.11-neoforge/run; with Jade)
./gradlew dependencyMap                            # interactive package dependency map -> build/reports/dependency-map/index.html
./gradlew chiseledPublish                          # upload both nodes to CurseForge/Modrinth (custom task)
./gradlew :1.21.11-fabric:publishMods              # upload one node
```

### Publishing

- Uses `me.modmuss50.mod-publish-plugin` (approved by the user), configured in `publishMods` in `omlib/build-logic/src/main/kotlin/mod-common.gradle.kts` (the loader scripts only set the jar to upload).
- Settings in `gradle.properties`: `release_type` (release/beta/alpha), `curseforge_project_id` (224663, the old OMT page), `modrinth_project_id` (`GEFsJ5uX`; empty would skip Modrinth).
- Tokens are never committed: environment variables `CURSEFORGE_TOKEN` / `MODRINTH_TOKEN`, or `curseforge_token` / `modrinth_token` in `~/.gradle/gradle.properties`.
  Without a token the upload is a dry run (output in `versions/<node>/build/publishMods/`).
- **Changelog** (`CHANGELOG.md`, Keep a Changelog format): every user-visible change gets a one-line entry under `## [Unreleased]` (Added / Changed / Fixed / Removed), by hand or via `./gradlew changelogEntry --type=fixed --entry="…"`.
  Add the entry together with the change.
- **Release steps:** set `mod_version` (e.g. `4.0.0-alpha.2`) and `release_type` → `./gradlew releaseChangelog` (moves Unreleased into `## [<mod_version>] - <date>`) → `./gradlew chiseledBuild` → commit `gradle.properties` and `CHANGELOG.md` as `feat: version <mod_version>` → tag that commit with a lightweight tag named exactly `<mod_version>+1.21.11` (`git tag 4.0.2+1.21.11`, no `v` prefix; the same `mod_version` is released from several branches, so the tag names the Minecraft version; older tags without the suffix stay as they are) → `./gradlew chiseledPublish` → push the branch and the tag (`git push origin <branch> <mod_version>+1.21.11`).
  Publishing uploads only that version's section and fails if it is missing.
  (Task names may differ slightly depending on Stonecutter's version.
  Use `./gradlew tasks` if unsure.)

**Definition of done for any change:** `./gradlew chiseledBuild` succeeds for **both** nodes, and the source is left in the `1.21.11-fabric` state.

### Docs and checks

`docs/architecture.md` is the human-oriented guide to the structure (layers, flows, how to add things, known weak spots); keep it in sync when the structure changes.
`ArchitectureTest` (`src/test/.../architecture`, ArchUnit) checks that main never uses client code, and package cycles against its `KNOWN_CYCLES` list (a ratchet: update the list when a cycle shrinks; never add to it without asking the user); it leaves the bundled core classes out.
PMD (`gradle/pmd/ruleset.xml`, report only, active version) measures complexity and size: `versions/1.21.11-fabric/build/reports/pmd/`.
JaCoCo records coverage in the unit tests and on the game test server; `check` prints the line coverage of `api` per package (active version only).
This repository has no PIT task since the core moved to OpenModularCore (its mutation testing runs there).

`./gradlew check` also runs `checkRecipes` (from `mod-common`): every item (item model) of the mod needs a recipe on that node, unless `recipe-exempt.txt` lists it as `<id> # <reason>`, and every recipe result of the mod's namespace must be one of its items.
Tests live in `src/test/java` (JUnit, test-only dependency); they run on the Fabric node.

## Where the rest lives (nested `CLAUDE.md` files)

The sections of this file that explain one subsystem were moved into a `CLAUDE.md` in the folder of that subsystem's code.
A nested `CLAUDE.md` is loaded only when you read files in its folder; read the one for the area you work in before changing it.
The § numbers and headings of the moved sections are kept, so references such as "§5" or "§4 Loaders" in the text still resolve.

| `CLAUDE.md` in | What it covers (former sections) |
| --- | --- |
| `src/main/java/omtteam/openmodularturrets/` | §6 package layout of `src/main`; §4 "OMLib (the library)"; §4 "Core Game Logic Source" (how the core is wired in, local core); §5 compat helpers (the porting layer); "Power & items" (energy, ammo, generator, inventory expanders, per-machine upgrades, module toggles); known performance trade-offs |
| `src/main/java/omtteam/openmodularturrets/platform/` | §4 "Loaders" and "Second-loader design decisions": overall approach, platform layer and services, energy and items, registration and events |
| `src/main/java/omtteam/openmodularturrets/blockentity/` | turret ↔ base relationship, aiming on arbitrary faces, range falloff and turn rate, targeting (base) |
| `src/main/java/omtteam/openmodularturrets/api/` | the turret API (adding turrets) |
| `src/main/java/omtteam/openmodularturrets/security/` | security and trusted players; ownership for adventure maps |
| `src/main/java/omtteam/openmodularturrets/config/` | config (Gson, `@Comment` / `@Range`), config versions |
| `src/main/` | §5 "Resources strategy" (assets, datagen, no AI images) and the data decision (NeoForge converts the Fabric data) |
| `src/client/java/omtteam/openmodularturrets/client/` | §6 client package layout, client compat helpers, client decisions, Jade |
| `src/gametest/` | game tests: framework, scenes, fake players, the loader-neutral test decisions, API test turrets |

---

## 7. Change Workflow

1. Implement gameplay logic in the core where possible: in OpenModularCore (`../../OpenModularCore`, with unit tests there; see the root `CLAUDE.md`, "Changing the core"), never as sources in this repository; keep Minecraft-facing code thin.
2. Wrap loader differences in `compat/` or `platform/`, and Minecraft API differences between the version branches in the OMLib compat helpers.
3. Add assets, lang entries and datagen entries; regenerate data on the Fabric node (`:1.21.11-fabric:runDatagen`).
4. Add a `CHANGELOG.md` entry for user-visible changes.
5. Run `./gradlew chiseledBuild`.

- Every variant (base tier, ammo type, upgrade, addon) is an **individual registered block/item** with a lowercase snake_case ID.
  `docs/id-mapping.md` maps the 1.12.2 IDs to the current ones.
- OpenComputers / ComputerCraft integration is **out of scope for now** (ask before adding).

### Designing APIs

The user wants close control over every API (public interfaces for other mods, such as `api/`, but also new cross-cutting interfaces inside the mod).
Design them step by step, together with the user:

1. **Overall shape first:** present a few ideas (e.g. 2–3 approaches with their trade-offs and a recommendation) and wait for the user's choice before writing code.
2. **Then each level in turn:** repeat this for each package, then each class or interface, then each public method or callback signature (names, parameters, return values, nullability, when it is called).
   Propose, wait for the decision, then implement that piece.
3. Do not implement a whole API in one go, and do not pick a signature on your own because it seems obvious; a small choice (e.g. which parameters a callback gets) is still the user's call.

---

## 8. Coding Conventions

- Java, 4-space indent, no wildcard imports.
- Registry IDs and resource names: lowercase `snake_case`.
- Keep gameplay logic **server-side**.
  Clients only render and send requests.
- No static mutable state for per-world data.
  Base state lives in block entities.
- Use `Level#isClientSide()` checks properly; never load client classes on the server.
- All user-facing text goes through translation keys (`block.openmodularturrets.x`, `gui.openmodularturrets.x`, `tooltip.openmodularturrets.x`).
- Prefer small, focused classes.
- Add a short Javadoc to any non-obvious formula.
- Markdown files: one sentence per line, never a line break inside a sentence.
  The sentences of a paragraph go on consecutive lines (no blank line between them), so they render as one paragraph.
  For a visible line break inside a paragraph, end the line with two spaces.
  Exception: `CHANGELOG.md` entries stay one line each.
- Don't add new features or change balance values unless the user asks.

---

## 9. Roadmap / Status

The 1.12.2 port and the NeoForge and 1.21.11 ports are done and released (4.0.0 on 2026-11-03); their history is in git and `CHANGELOG.md`.
Update the open items as work progresses.

- [ ] Inventory expanders: not checked in a client yet (count drawing, tooltip, model).
- [ ] Range falloff and turn rate (issue 539, options A chosen by the user on 2026-11-22; OpenModularCore 2.2.0): not yet ported to 1.20.1, 1.21.1 and 1.12; not checked in a client yet.
- [ ] Repelling, tractor and energy drain turrets (issue 324, decided by the user on 2026-11-22; OpenModularCore 2.3.0): ported to 1.20.1, 1.21.1 and 1.12 on their `new-turrets` branches; not checked in a client yet.
- Skipped on purpose: the old expanders (own inventory or energy), the loot deleter and the power generator of 1.12.2.
- Camouflage: `camo` must not depend on `compat` (it would join a package cycle): hosts call `remesh()` themselves after `CamoData.read`.
- ArchUnit: the cycle `compat` / `compat.network` ↔ `platform` (facades call the services, the service interfaces use the facades' types) is accepted in `KNOWN_CYCLES` (decided by the user).

### Moving and clearing blocks

- [x] `Clearable` on turret base and generator (`/setblock` and similar would otherwise duplicate their items), `rotate` / `mirror` on turret, crank and generator (`BlockMoveGameTests`)
