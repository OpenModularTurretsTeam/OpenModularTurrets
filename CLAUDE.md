# CLAUDE.md — OpenModularTurrets (Fabric, Forge; Minecraft 1.20.1)

This file guides Claude Code when working in this repository.
Read it fully before making changes; the workspace `../CLAUDE.md` (git, OMLib submodule, core, CI, releases) and the root `../../CLAUDE.md` (cross-version rules) apply on top.

---

## 1. Project Overview

**OpenModularTurrets (OMT)** is maintained for **Fabric and Forge on Minecraft 1.20.1 from a single codebase** using **Stonecutter** (its only axis here is the loader).
This is the branch `1.20.1`; the sibling branches are `1.21.1` and `1.21.11` (Fabric and NeoForge), the 1.12.2 version is on `1.12`, and the old multi-version `1.20.1+` is frozen.
The port from 1.12.2 is complete and released (4.0.0 on 2026-11-03).
The second-loader patterns come from OpenModularLighting (`../OpenModularLighting`), where they were worked out first.
Inventory expanders exist in 4.x with new functionality (see "Power & items"); the 1.12.2 design (inventory expanders with their own storage, and power expanders) is not to be brought back.
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
- **Jade** (optional integration, `compat/jade/` for the server half and `client/compat/jade/` for the tooltips, registered through the `jade` entrypoint on Fabric and `@WailaPlugin` on Forge).
  Compile-only (`modCompileOnly`, Modrinth maven), never bundled and never required at runtime.
- **NEEPMeat** (Fabric, Modrinth `maven.modrinth:neepmeat`, version in `versions/1.20.1-fabric/gradle.properties`; requested by the user on 2026-11-04): its vascular network charges the turret bases through OMLib (`platform/fabric/FabricNeepMeatPower`, registered by `FabricBlockLookups` when NEEPMeat is loaded; 1 eJ = 1 E, a full buffer takes nothing), so the mod has no code of its own for it.
  Never bundled (`suggests` in `fabric.mod.json`); `modCompileOnly` for the game test `gametest/platform/fabric/NeepMeatGameTests`, and `modLocalRuntime` with the mods nested in its jar (Loom leaves them out of its remapped copy; OMLib's `nestedModJars`) and **GeckoLib**, which it requires, for the dev runs and the game test server.
- **Forge** 47.x, built with the legacy variant of ModDevGradle (`net.neoforged.moddev.legacyforge`, same artifact and version as `moddev`; build-time only), requested by the user.
  On Forge, the Fabric-only APIs are replaced by Forge's own (capabilities, events, event network channels); see §4 "Forge 1.20.1".
- **MixinExtras** (`io.github.llamalad7:mixinextras-forge`), jar-in-jar in the **Forge jar only** (approved by the user on 2026-10-24): Fabric Loader ships it, Forge 1.20.1 does not, and `LivingEntityMixin` needs it.
- **OpenModularCore** (the team's shared core, `../../OpenModularCore`, module `omt-core` from JitPack, approved by the user on 2026-11-16): the game rules (`omtteam.openmodularturrets.core`), the same on every Minecraft version; compile-only, its classes are bundled into the mod's jar (see "Core Game Logic Source").
- **OMLib** (the user's own library, `../OMLib`, branch `1.20.1`), mounted as the git submodule `omlib` and bundled into every jar (jar-in-jar; requested by the user on 2026-10-27); see §4 "OMLib".
- **CC: Tweaked** (`cc.tweaked`, from `maven.squiddev.cc`), on both nodes (approved by the user on 2026-11-27): compile-only against its `-api` jar (on Forge also `-core-api`, as its `-forge-api` module lists no dependencies), never bundled, an optional dependency in the loader metadata (`suggests` on Fabric, an optional dependency in `mods.toml`); the full mod is in the dev runs (`modLocalRuntime`), on the game test servers and in `runenv`.
  Gated on `cctweaked_version` in `versions/<node>/gradle.properties` (1.121.0: 1.122.0 needs Loom 1.19, this branch has 1.18); the code is loaded behind `isModLoaded("computercraft")`.
- Nothing else without asking the user first.
  No Architectury, no Cloth Config, no Kotlin.

---

## 2. Tech Stack

| | 1.20.1 (Fabric) | 1.20.1 (Forge) |
| --- | --- | --- |
| Java | 17 | 17 |
| Loader | Fabric Loader 0.19.x | Forge 47.4.x |
| API | Fabric API `0.92.x+1.20.1` | – |
| Gradle plugin | Loom 1.18 | ModDevGradle 2.0 (legacy) |
| Mappings | Mojang official (+ Parchment optional) | Mojang official (jar reobfuscated to SRG, mixin refmap) |

- Build system: Gradle 9.8 (Kotlin DSL) + **Stonecutter 0.9**, with Fabric Loom 1.18 (`net.fabricmc.fabric-loom-remap`, the variant for obfuscated MC versions) on the Fabric node and the legacy variant of ModDevGradle 2.0 (`net.neoforged.moddev.legacyforge`) on the Forge node.
  **Gradle itself must run on Java 25+** (Loom 1.18); the compile toolchain (17, from `java_version` in the node's `gradle.properties`) is auto-provisioned by foojay.
- Shared build logic is the convention plugin `omlib/build-logic/src/main/kotlin/mod-common.gradle.kts` (Java toolchain, metadata templating, JUnit, the OpenModularCore wiring with `extractCore` and `verifyNoCoreSources`, `checkRecipes`, licenses in the jar via `bundled_licenses`, publishing), from the `omlib` submodule; change it in `../OMLib`, never inside `omlib/` (workspace `CLAUDE.md`, "OMLib in the mods").
  The OMT-specific quality tools (ArchUnit, JaCoCo, PMD) stay in `build.fabric.gradle.kts`; Spotless is in `stonecutter.gradle.kts` (`./gradlew spotlessApply`).
- **Use Mojang mappings** (`loom.officialMojangMappings()`).
  All class and method names in code and in this doc are Mojmap names (`BlockEntity`, `ResourceLocation`, `AbstractContainerMenu`, and so on).
- Language: Java only.

Check exact latest patch versions of Loader, API, Loom and Stonecutter when setting up or bumping.
Do not hard-code guesses into the build without verifying.

---

## 3. Repository Layout

```
.
├── CLAUDE.md
├── build.fabric.gradle.kts     # Fabric node (Loom, Fabric API, datagen, GameTests, ArchUnit/JaCoCo/PMD)
├── build.forge.gradle.kts      # Forge node (legacy ModDevGradle: reobfJar, mixin refmap, MixinExtras jar-in-jar, pack.mcmeta)
├── omlib/                      # git submodule (OMLib, branch 1.20.1): the library (src/) every mod bundles, and build-logic/ (the mod-common convention plugin); see §4 "OMLib"
├── stonecutter.gradle.kts      # Stonecutter controller (active version, loader constants, chiseled tasks)
├── settings.gradle.kts         # declares the Stonecutter nodes (<minecraft version>-<loader>)
├── gradle.properties           # shared properties (mod_id, mod_version, maven_group, core_version …)
├── versions/
│   ├── 1.20.1-fabric/
│   │   ├── gradle.properties   # minecraft_version, fabric_api, java_version=17 …
│   │   └── src/main/generated/ # datagen output (committed; Forge converts it, datagen_from)
│   └── 1.20.1-forge/
│       └── gradle.properties   # forge_version, forge_dependency, mixinextras_version …
└── src/
    ├── main/java/omtteam/openmodularturrets/...
    ├── main/resources/         # SHARED assets + fabric.mod.json and META-INF/mods.toml (templated)
    ├── client/java/...         # client-only code (split source sets)
    ├── test/java/...           # JUnit tests: architecture rules, config (Fabric node; the core's tests are in OpenModularCore)
    └── gametest/               # server game tests for both loaders (own mod id openmodularturrets_gametest)
```

- **Mod ID:** `openmodularturrets`.
  **Base package:** `omtteam.openmodularturrets`.
- Use Loom **split environment source sets** (`splitEnvironmentSourceSets()`).
  Client-only code (renderers, screens, models, client networking handlers) goes in `src/client/java`.
  Never reference client classes from `main`.
- `fabric.mod.json` and `mods.toml` are templated via `processResources`.
  Inject `${version}`, `${minecraft_dependency}` and `${java_version}` from the node's properties.

---

## 4. Stonecutter

### Setup (`settings.gradle.kts`)

```kotlin
plugins { id("dev.kikugie.stonecutter") version "0.9.8" }

stonecutter {
    create(rootProject) {
        versions("1.20.1-fabric" to "1.20.1", "1.20.1-forge" to "1.20.1")
        mapBuilds { _, node -> "build.${node.project.substringAfterLast('-')}.gradle.kts" }
        vcsVersion = "1.20.1-fabric" // the version the committed source is written for
    }
}
```

The Stonecutter DSL changes between releases.
If this does not match the installed version, check the Stonecutter docs and update both this file and the build.

### Rules

- **Committed source must always be in the `vcsVersion` (`1.20.1-fabric`) state.** Before committing, switch back if you switched to another node (`./gradlew "Reset active project"` or `"Set active project to 1.20.1-fabric"`).
- The only axis is the loader: conditions use the loader constants (`//? if fabric {`, `//? if forge {`), the other loader's branch sits in a `/* … */` comment exactly as Stonecutter expects:

  ```java
  //? if fabric {
  import net.fabricmc.fabric.api.entity.FakePlayer;
  //?}
  //? if forge {
  /*import net.minecraftforge.common.util.FakePlayer;
  *///?}
  ```

  Prefer letting Stonecutter generate this by switching nodes over hand-writing it.
- **Minimize conditional blocks.** Hide loader differences behind the services in `omtteam.openmodularturrets.platform` and whole classes in `platform/<loader>/` rather than sprinkling `//? if` across gameplay code.
  Gameplay logic (targeting, permissions, consumption) should be loader-agnostic.
  The OMLib compat helpers (`VersionCompat`, `NbtCompat`, `BlockCompat`, `ClientCompat`, `CompatBlockEntity`, …) stay as the porting layer between the version branches: call sites are identical on all of them, so the game layer is ported by hand (see §5).
- There are no Minecraft-version conditions on this branch: version differences are resolved by branch, and `mod-common` fails the build if a version source folder (`src/<set>/mc<first>-<last>/java`) exists.
- Node *properties* live in `versions/<node>/gradle.properties`.
  Read them in the build via `property("…")`.

### Loaders: publishing

- Forge publishing is active (enabled by the user on 2026-10-24); `publish_enabled=false` in a node's `gradle.properties` would turn that node back into a dry run even with tokens.

### Commands

```bash
./gradlew "Set active project to 1.20.1-forge" # switch the source to the Forge node
./gradlew "Reset active project"               # switch back (VCS state, 1.20.1-fabric)
./gradlew chiseledBuild                        # build BOTH nodes (custom task in stonecutter.gradle.kts)
./gradlew :1.20.1-fabric:build                 # build a single node
./gradlew :1.20.1-fabric:runClient             # run client for a node (shared run dir)
./gradlew :1.20.1-fabric:runServer
./gradlew :1.20.1-fabric:runGameTest           # server game tests for one node (also run by check / build)
./gradlew :1.20.1-forge:runGameTestServer      # the same game tests on Forge (also run by check / build)
./gradlew :1.20.1-fabric:runDatagen            # regenerate data (the Forge node converts this output)
./gradlew :1.20.1-forge:runClient              # Forge client (run dir versions/1.20.1-forge/run; with Jade)
./gradlew dependencyMap                        # interactive package dependency map -> build/reports/dependency-map/index.html
./gradlew chiseledPublish                      # upload both nodes to CurseForge/Modrinth (the user only)
./gradlew :1.20.1-fabric:publishMods           # upload one node (the user only)
```

### Publishing

- Uses `me.modmuss50.mod-publish-plugin` (approved by the user), configured in `publishMods` in `omlib/build-logic/src/main/kotlin/mod-common.gradle.kts` (the loader scripts only set the jar to upload).
- Settings in `gradle.properties`: `release_type` (release/beta/alpha), `curseforge_project_id` (224663, the old OMT page), `modrinth_project_id` (`GEFsJ5uX`; empty would skip Modrinth).
- Tokens are never committed: environment variables `CURSEFORGE_TOKEN` / `MODRINTH_TOKEN`, or `curseforge_token` / `modrinth_token` in `~/.gradle/gradle.properties`.
  Without a token the upload is a dry run (output in `versions/<node>/build/publishMods/`).
- **Changelog** (`CHANGELOG.md`, Keep a Changelog format): every user-visible change gets a one-line entry under `## [Unreleased]` (Added / Changed / Fixed / Removed), by hand or via `./gradlew changelogEntry --type=fixed --entry="…"`.
  Add the entry together with the change.
- **Release steps** (the user releases and pushes): set `mod_version` (e.g. `4.0.2`) and `release_type` → `./gradlew releaseChangelog` (moves Unreleased into `## [<mod_version>] - <date>`) → `./gradlew chiseledBuild` → commit `gradle.properties` and `CHANGELOG.md` as `feat: version <mod_version>` → tag that commit `<mod_version>+1.20.1` (workspace `CLAUDE.md`, "Release tags") → `./gradlew chiseledPublish` → push the branch and the tag.
  Publishing uploads only that version's section and fails if it is missing.

**Definition of done for any change:** `./gradlew chiseledBuild` succeeds for **both** nodes, and the source is left in the `1.20.1-fabric` state.

### Docs and checks

`docs/architecture.md` is the human-oriented guide to the structure (layers, flows, how to add things, known weak spots); keep it in sync when the structure changes.
`ArchitectureTest` (`src/test/.../architecture`, ArchUnit) checks that main never uses client code, and package cycles against its `KNOWN_CYCLES` list (a ratchet: update the list when a cycle shrinks; never add to it without asking the user); it leaves the bundled core classes out.
PMD (`gradle/pmd/ruleset.xml`, report only, active version) measures complexity and size: `versions/1.20.1-fabric/build/reports/pmd/`.
JaCoCo records coverage in the unit tests and on the game test server; `check` prints the line coverage of `api` per package (active version only).
This repository has no PIT task (the core's mutation testing runs in OpenModularCore).

`./gradlew check` also runs `checkRecipes` (from `mod-common`): every item (item model) of the mod needs a recipe on that node, unless `recipe-exempt.txt` lists it as `<id> # <reason>`, and every recipe result of the mod's namespace must be one of its items.
Tests live in `src/test/java` (JUnit, test-only dependency); they run on the Fabric node.

## Where the rest lives (nested `CLAUDE.md` files)

The sections of this file that explain one subsystem were moved into a `CLAUDE.md` in the folder of that subsystem's code.
A nested `CLAUDE.md` is loaded only when you read files in its folder; read the one for the area you work in before changing it.
The § numbers and headings of the moved sections are kept, so references such as "§5" or "§4 Loaders" in the text still resolve.

| `CLAUDE.md` in | What it covers (former sections) |
| --- | --- |
| `src/main/java/omtteam/openmodularturrets/` | §6 package layout of `src/main`; §4 "OMLib (the library)"; §4 "Core Game Logic Source" (how the core is wired in); §5 compat helpers (the porting layer); "Power & items" (energy, ammo, generator, inventory expanders, per-machine upgrades, module toggles, moving and clearing blocks); known performance trade-offs |
| `src/main/java/omtteam/openmodularturrets/platform/` | §4 "Loaders", "Second-loader design decisions" (overall approach, platform layer and services, energy and items, registration and events) and "Forge 1.20.1" (build, mixins, entrypoints, services, capabilities, client) |
| `src/main/java/omtteam/openmodularturrets/blockentity/` | turret ↔ base relationship, aiming on arbitrary faces, range falloff and turn rate, targeting (base) |
| `src/main/java/omtteam/openmodularturrets/api/` | the turret API (adding turrets) |
| `src/main/java/omtteam/openmodularturrets/security/` | security and trusted players; ownership for adventure maps |
| `src/main/java/omtteam/openmodularturrets/config/` | config (Gson, `@Comment` / `@Range`), config versions |
| `src/main/` | §5 "Resources strategy" (assets, datagen, no generated art) and the data decision (Forge converts the Fabric data) |
| `src/client/java/omtteam/openmodularturrets/client/` | §6 client package layout, client compat helpers, client decisions, Jade |
| `src/gametest/` | game tests: framework, scenes, fake players, the loader-neutral test decisions, API test turrets, the Forge 1.20.1 game test server and its flat world |

---

## 7. Change Workflow

1. Implement gameplay logic in the core where possible: in OpenModularCore (`../../OpenModularCore`, with unit tests there; see the root `CLAUDE.md`, "Changing the core"), never as sources in this repository; keep Minecraft-facing code thin.
2. Wrap loader differences in `compat/` or `platform/`, and Minecraft API differences between the version branches in the OMLib compat helpers.
3. Add assets, lang entries and datagen entries; regenerate data on the Fabric node (`:1.20.1-fabric:runDatagen`).
4. Add a `CHANGELOG.md` entry for user-visible changes.
5. Run `./gradlew chiseledBuild`.

- Every variant (base tier, ammo type, upgrade, addon) is an **individual registered block/item** with a lowercase snake_case ID.
  `docs/id-mapping.md` maps the 1.12.2 IDs to the current ones.
- Computers: the loader-free base is present (the commands in `core/computer`, `BaseComputerCommands`, a port of 1.12.2's plus computer control; computer control of a base and its turrets, `blockentity/base/BaseComputerTurrets`; the Serial Port addon, `addon_serial_port`), and **CC: Tweaked** is supported on both nodes (added on 2026-11-27, decided by the user): `compat/computercraft/TurretBasePeripheral` (one loader-free class, the peripheral `turret_base`) reuses `BaseComputerCommands`; only its registration is per loader (`platform/<loader>/computercraft`: `PeripheralLookup` on Fabric, `ForgeComputerCraftAPI.registerPeripheralProvider` on Forge).
  OpenComputers: Rebooted belongs to `1.21.1` only.
- New APIs (public interfaces for other mods, such as `api/`, and new cross-cutting interfaces inside the mod) are designed step by step with the user: workspace `CLAUDE.md`, "Designing APIs".

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
- Markdown files: one sentence per line, the sentences of a paragraph on consecutive lines; `CHANGELOG.md` entries stay one line each.
- Don't add new features or change balance values unless the user asks.

---

## 9. Open items

The port, the Forge node and every feature above are done and covered by game tests where noted.
Still open:

- [ ] Real texture for the ownership wand (placeholder: a copy of the memory card).
- [ ] Not checked in a client yet: the inventory expanders (count drawing, tooltip, model) and the range falloff and turn rate lines.
