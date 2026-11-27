# CLAUDE.md — OpenModularTurrets (Fabric, NeoForge; Minecraft 1.21.1)

This file guides Claude Code when working in this repository.
Read it fully before making changes.

---

## 1. Project Overview

**OpenModularTurrets (OMT)** is maintained for **Fabric and NeoForge on Minecraft 1.21.1 from a single codebase** using **Stonecutter** (its only axis here is the loader).
This is the branch `1.21.1`, split from the old `1.20.1+` branch (which built 1.20.1, 1.21.1 and 1.21.11 and is frozen until the first release from the new branches).
The sibling branches are `1.20.1` (Fabric and Forge) and `1.21.11` (Fabric and NeoForge); the 1.12.2 version is on `1.12`.
The port from 1.12.2 is complete and released (4.0.0 on 2026-11-03); this repo maintains the 1.21.1 version.
The **NeoForge** setup was worked out in OpenModularLighting first (`../OpenModularLighting`; see §4 "Loaders").
Inventory expanders exist in 4.x with new functionality: three tiers that hold no items themselves but raise the stack limit of the ammo slots of turret bases and the fuel slots of generators they touch (designed with the user on 2026-10-27, see "Power & items").
The 1.12.2 design (inventory expanders with their own storage, and power expanders) is not to be brought back.
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
- **NeoForge** 21.1.x, built with **ModDevGradle** (approved by the user; build-time only).
  On NeoForge, the Fabric-only APIs above are replaced by NeoForge's own (capabilities, events, payload registration); see §4 "Loaders".
- **Sable Companion** (`dev.ryanhcode.sable-companion`, MIT, from `maven.ryanhcode.dev`), jar-in-jar on both nodes (approved by the user on 2026-10-25), for Sable contraptions.
  It has a do-nothing fallback, so it needs no `isModLoaded` guard.
- **Sable** (Modrinth, pinned by version id in `sable_version`), dev and game test runtime only, never bundled (approved by the user on 2026-10-25).
  On Fabric only in the dev client: Sable 2.0.6's bundled Veil requires client-only Fabric API modules, so it cannot load in a server environment (the Fabric game test server excludes it); the NeoForge game test server runs with Sable.
  The NeoForge test mod also compiles against Sable (and Companion) for the contraption game tests (approved by the user on 2026-10-25).
- **Create Aeronautics** and **Create** (Modrinth, pinned by version id), in the NeoForge dev runs only (`localRuntime`), never bundled (requested by the user on 2026-10-25), to try turrets on airships by hand.
  Create is also compile-only on NeoForge (approved by the user on 2026-10-25) for `platform/neoforge/NeoForgeCreateMovement`, a `BlockMovementChecks` check registered only when Create is loaded; Create and Create Aeronautics are on the NeoForge game test server for the movement tests.
- **OpenModularCore** (the team's shared core, `../../OpenModularCore`, module `omt-core` from JitPack, approved by the user on 2026-11-16): the game rules (`omtteam.openmodularturrets.core`), the same on every Minecraft version; compile-only, its classes are bundled into the mod's jar (see "Core Game Logic Source").
  The JitPack repository is asked only for the core's group.
- **OMLib** (the user's own library, `../OMLib`, branch `1.21.1`), mounted as the git submodule `omlib` and bundled into every jar (jar-in-jar; Fabric `include`, NeoForge `jarJar`; requested by the user on 2026-10-27).
  It holds the code both mods used to share (platform services, version compat, energy and item transfer, config, the camouflage menu) and the build logic.
  The library is a mod of its own (`omlib`), declared as a required dependency in the mod metadata, and built as a nested step of the mod build, see §4 "OMLib".
- **OpenComputers: Rebooted** (`li.cil.oc:opencomputers`, from `maven.michiyo.me`), on the **`1.21.1-neoforge` node only** (approved by the user on 2026-11-04): compile-only against its `api` jar, never bundled, an optional dependency in `neoforge.mods.toml`; the full mod (Scala bundled inside, so no ScalableCatsForce) is in the NeoForge dev runs (`localRuntime`), on the NeoForge game test server and in `runenv/1.21.1-neoforge`, all non-transitive (its POM lists Create, AE2 and others it does not require).
  Gated on `opencomputers_version`, which only `versions/1.21.1-neoforge/gradle.properties` has; the code is loaded behind `isModLoaded("opencomputers")`.
- **CC: Tweaked** (`cc.tweaked`, from `maven.squiddev.cc`), on both nodes (approved by the user on 2026-11-27): compile-only against its `-api` jar, never bundled, an optional dependency in the loader metadata (`suggests` on Fabric, an optional dependency in `neoforge.mods.toml`); the full mod is in the dev runs (`modLocalRuntime` / `localRuntime`), on the game test servers and in `runenv`.
  Gated on `cctweaked_version` in `versions/<node>/gradle.properties` (1.120.2: 1.121.0 needs NeoForge 21.1.265, 1.122.0 Loom 1.19); the code is loaded behind `isModLoaded("computercraft")`.
- Nothing else without asking the user first.
  No Architectury, no Cloth Config, no Kotlin.

---

## 2. Tech Stack

| | 1.21.1 (Fabric) | 1.21.1 (NeoForge) |
| --- | --- | --- |
| Java | 21 | 21 |
| Loader | Fabric Loader 0.19.x | NeoForge 21.1.x |
| API | Fabric API `0.116.x+1.21.1` | – |
| Gradle plugin | Loom 1.18 | ModDevGradle 2.0 |
| Mappings | Mojang official (+ Parchment optional) | Mojang official (NeoForge runtime names) |

- Build system: Gradle 9.8 (Kotlin DSL) + **Stonecutter 0.9**, with Fabric Loom 1.18 (`net.fabricmc.fabric-loom-remap`, the variant for obfuscated MC versions) on the Fabric node, ModDevGradle 2.0 (`net.neoforged.moddev`) on the NeoForge node.
- Shared build logic is the convention plugin `omlib/build-logic/src/main/kotlin/mod-common.gradle.kts` (Java toolchain, metadata templating, JUnit, the OpenModularCore wiring with `extractCore` and `verifyNoCoreSources`, `checkRecipes`, licenses in the jar via `bundled_licenses`, publishing).
  It is mod-independent and lives in its own repository, `OMLib` (`../OMLib`), which every mod mounts as the git submodule `omlib` (the branch `1.21.1` of the OMLib repository, clean history; an included build in `pluginManagement` of `settings.gradle.kts`).
  Change it in the OMLib repository (`../OMLib` in the workspace, never inside `omlib/`), then move the `omlib` submodule of each mod to the new commit and commit the pointer (see the workspace `CLAUDE.md`, "OMLib in the mods"); clone a mod with `--recurse-submodules` (or run `git submodule update --init`).
  The few source files that the mods still copy from each other are listed in `omlib/shared-sources.txt`; after changing one, run `python3 -I omlib/tools/shared_sources.py sync --from . --write . ../OpenModularLighting` and then `check`.
  The OMT-specific quality tools (ArchUnit, JaCoCo, PMD) stay in `build.fabric.gradle.kts`.
- **Gradle itself must run on Java 25+** (Loom 1.18 requirement).
  The compile toolchain (21) is resolved separately (foojay auto-provisions it if missing).
- **Use Mojang mappings** (`loom.officialMojangMappings()`).
  All class and method names in code and in this doc are Mojmap names (`BlockEntity`, `ResourceLocation`, `AbstractContainerMenu`, and so on).
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
├── omlib/                      # git submodule (OMLib, branch 1.21.1): the library (src/) every mod bundles, and build-logic/ (the mod-common convention plugin); see §4 "OMLib"
├── stonecutter.gradle.kts      # Stonecutter controller (active version, loader constants, chiseled tasks)
├── settings.gradle.kts         # declares the Stonecutter nodes (<minecraft version>-<loader>)
├── gradle.properties           # shared properties (mod_id, mod_version, maven_group …)
├── versions/
│   ├── 1.21.1-fabric/
│   │   ├── gradle.properties   # minecraft_version, fabric_api, java_version=21 …
│   │   └── src/main/generated/ # datagen output (committed; NeoForge converts it, datagen_from)
│   └── 1.21.1-neoforge/
│       └── gradle.properties   # neoforge_version, minecraft_dependency (Maven range) …
└── src/
    ├── main/java/omtteam/openmodularturrets/...
    ├── main/resources/         # SHARED assets + fabric.mod.json and META-INF/neoforge.mods.toml (templated)
    ├── client/java/...         # client-only code (split source sets)
    ├── test/java/...           # JUnit tests: architecture rules, config (Fabric node; the core's tests are in OpenModularCore)
    └── gametest/               # server game tests for both loaders (own mod id openmodularturrets_gametest)
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
        versions("1.21.1-fabric" to "1.21.1", "1.21.1-neoforge" to "1.21.1")
        mapBuilds { _, node -> "build.${node.project.substringAfterLast('-')}.gradle.kts" }
        vcsVersion = "1.21.1-fabric" // the version the committed source is written for
    }
}
```

The Stonecutter DSL changes between releases.
If this does not match the installed version, check the Stonecutter docs and update both this file and the build.

### Rules

- **Committed source must always be in the `vcsVersion` (`1.21.1-fabric`) state.** Before committing, switch back if you switched to another node (`./gradlew "Reset active project"` or `"Set active project to 1.21.1-fabric"`).
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
- Node *properties* live in `versions/<node>/gradle.properties`.
  Read them in the build via `property("…")`.

### Loaders: jar names and publishing

- NeoForge publishing is active (enabled by the user on 2026-10-24); `publish_enabled=false` in a node's `gradle.properties` would turn that node back into a dry run even with tokens.
- The mod version is `<mod_version>+<minecraft version>-<loader>`, so the jars of both loaders can be told apart.
  The jar files are named `<mod_id>-<mod_version>-mc<minecraft version>-<loader>.jar` (`archiveVersion` in `mod-common`); the release title on CurseForge and Modrinth is the jar name without `.jar` (`displayName`), while the mod metadata and the published version number keep the `+` version.

### Commands

```bash
./gradlew "Set active project to 1.21.1-neoforge" # switch the source to the NeoForge node
./gradlew "Reset active project"                  # switch back (VCS state, 1.21.1-fabric)
./gradlew chiseledBuild                           # build BOTH nodes (custom task in stonecutter.gradle.kts)
./gradlew :1.21.1-fabric:build                    # build a single node
./gradlew :1.21.1-fabric:runClient                # run client for a node (shared run dir)
./gradlew :1.21.1-fabric:runServer
./gradlew :1.21.1-fabric:runGameTest              # server game tests for one node (also run by check / build)
./gradlew :1.21.1-neoforge:runGameTestServer      # the same game tests on NeoForge (also run by check / build)
./gradlew :1.21.1-fabric:runDatagen               # regenerate data (the NeoForge node converts this output)
./gradlew :1.21.1-neoforge:runClient              # NeoForge client (run dir versions/1.21.1-neoforge/run; with Jade)
./gradlew dependencyMap                           # interactive package dependency map -> build/reports/dependency-map/index.html
./gradlew chiseledPublish                         # upload both nodes to CurseForge/Modrinth (custom task)
./gradlew :1.21.1-fabric:publishMods              # upload one node
```

### Publishing

- Uses `me.modmuss50.mod-publish-plugin` (approved by the user), configured in `publishMods` in `omlib/build-logic/src/main/kotlin/mod-common.gradle.kts` (the loader scripts only set the jar to upload).
- Settings in `gradle.properties`: `release_type` (release/beta/alpha), `curseforge_project_id` (224663, the old OMT page), `modrinth_project_id` (`GEFsJ5uX`; empty would skip Modrinth).
- Tokens are never committed: environment variables `CURSEFORGE_TOKEN` / `MODRINTH_TOKEN`, or `curseforge_token` / `modrinth_token` in `~/.gradle/gradle.properties`.
  Without a token the upload is a dry run (output in `versions/<node>/build/publishMods/`).
- **Changelog** (`CHANGELOG.md`, Keep a Changelog format): every user-visible change gets a one-line entry under `## [Unreleased]` (Added / Changed / Fixed / Removed), by hand or via `./gradlew changelogEntry --type=fixed --entry="…"`.
  Add the entry together with the change.
- **Release steps:** set `mod_version` (e.g. `4.0.0-alpha.2`) and `release_type` → `./gradlew releaseChangelog` (moves Unreleased into `## [<mod_version>] - <date>`) → `./gradlew chiseledBuild` → commit `gradle.properties` and `CHANGELOG.md` as `feat: version <mod_version>` → tag that commit with a lightweight tag named exactly `<mod_version>+1.21.1` (`git tag 4.0.2+1.21.1`, no `v` prefix; the same `mod_version` is released from several branches, so the tag names the Minecraft version; older tags without the suffix stay as they are) → `./gradlew chiseledPublish` → push the branch and the tag (`git push origin <branch> <mod_version>+1.21.1`).
  Publishing uploads only that version's section and fails if it is missing.
  (Task names may differ slightly depending on Stonecutter's version.
  Use `./gradlew tasks` if unsure.)

**Definition of done for any change:** `./gradlew chiseledBuild` succeeds for **both** nodes, and the source is left in the `1.21.1-fabric` state.

### Docs and checks

`docs/architecture.md` is the human-oriented guide to the structure (layers, flows, how to add things, known weak spots); keep it in sync when the structure changes.
`ArchitectureTest` (`src/test/.../architecture`, ArchUnit) checks that main never uses client code, and package cycles against its `KNOWN_CYCLES` list (a ratchet: update the list when a cycle shrinks; never add to it without asking the user; the cycle `compat` / `compat.network` ↔ `platform` was accepted by the user); it leaves the bundled core classes out.
PMD (`gradle/pmd/ruleset.xml`, report only, active version) measures complexity and size: `versions/1.21.1-fabric/build/reports/pmd/`.
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
| `src/main/java/omtteam/openmodularturrets/platform/neoforge/opencomputers/` | the roadmap section "Computers: OpenComputers: Rebooted" (1.21.1-neoforge only): what is done, what is open |
| `src/main/java/omtteam/openmodularturrets/blockentity/` | turret ↔ base relationship, aiming on arbitrary faces, range falloff and turn rate, targeting (base) |
| `src/main/java/omtteam/openmodularturrets/api/` | the turret API (adding turrets) |
| `src/main/java/omtteam/openmodularturrets/security/` | security and trusted players; ownership for adventure maps |
| `src/main/java/omtteam/openmodularturrets/config/` | config (Gson, `@Comment` / `@Range`), config versions |
| `src/main/java/omtteam/openmodularturrets/compat/` | the CC: Tweaked peripheral (`computercraft/`) and the roadmap section "Sable contraptions" (poses, targeting in the plot frame, turret mount, Create movement, game tests) |
| `src/main/` | §5 "Resources strategy" (assets, datagen, no AI images) and the data decision (NeoForge converts the Fabric data) |
| `src/client/java/omtteam/openmodularturrets/client/` | §6 client package layout, client compat helpers, client decisions, Jade |
| `src/gametest/` | game tests: framework, scenes, fake players, the loader-neutral test decisions, API test turrets |

---

## 7. Change Workflow

1. Implement gameplay logic in the core where possible: in OpenModularCore (`../../OpenModularCore`, with unit tests there; see the root `CLAUDE.md`, "Changing the core"), never as sources in this repository; keep Minecraft-facing code thin.
2. Wrap loader differences in `compat/` or `platform/`, and Minecraft API differences between the version branches in the OMLib compat helpers.
3. Add assets, lang entries and datagen entries; regenerate data on the Fabric node (`:1.21.1-fabric:runDatagen`).
4. Add a `CHANGELOG.md` entry for user-visible changes.
5. Run `./gradlew chiseledBuild`.

- Every variant (base tier, ammo type, upgrade, addon) is an **individual registered block/item** with a lowercase snake_case ID.
  `docs/id-mapping.md` maps the 1.12.2 IDs to the current ones.
- Computers: **OpenComputers: Rebooted** is supported on `1.21.1-neoforge` only (the only node it exists for; added on 2026-11-04, decided by the user): the commands are loader-free in `core/computer` (`BaseComputerCommands`, a port of 1.12.2's, plus computer control), the OpenComputers driver and component are in `platform/neoforge/opencomputers` (see §6).
  **CC: Tweaked** is supported on both nodes (added on 2026-11-27, decided by the user): `compat/computercraft/TurretBasePeripheral` (one loader-free class, the peripheral `turret_base`, the same methods as the OpenComputers component) reuses `BaseComputerCommands`; only its registration is per loader (`platform/<loader>/computercraft`).

### Designing APIs

The user wants close control over every API (public interfaces for other mods, such as `api/`, but also new cross-cutting interfaces inside the mod).
Present 2–3 approaches with trade-offs and a recommendation and wait for the choice; then repeat per package, class and public method or callback signature (names, parameters, return values, nullability), implementing one piece at a time.
Never pick a signature on your own because it seems obvious.

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

The port is done (all turrets, bases, upgrades, addons, generator, inventory expanders, camouflage, memory card, Jade, turret API, publishing, both loaders); the sections below keep the design of the larger features and their open items.
Update the open items as work progresses.

- [ ] Checked in a client: the inventory expanders (count drawing, tooltip, model) and the range falloff and turn rate (issue 539; tooltip and Jade lines, `turret.TurretStatLines`).
- "Sort Ammo" (requested by the user on 2026-10-27): right column of the base GUI below the admin buttons, for everyone who opens the GUI (`BasePackets.Action.SORT_AMMO`); `TurretBaseInventory.sortAmmo` merges equal ammo up to the slot limit and orders Minecraft items, then OMT ammo, then other mods' items, each by registry name (the core's `AmmoSort`); `BaseGuiGameTests.sortButtonSortsTheAmmo`, `AmmoSortGameTests`.
- Every turret has a game test (`ProjectileTurretGameTests`, `RayTurretGameTests`, `EffectTurretGameTests`): on a base of its tier with energy and ammo it must hit a hostile target 5 blocks in front of it (the melee turret one in its 3×2×3 reach); projectile turrets must spawn their projectile, ray turrets trace their ray; the railgun ignores scattershot upgrades (`railgunIgnoresScattershot`).
- Mixins on NeoForge: the same `openmodularturrets.mixins.json` under `[[mixins]]` in `neoforge.mods.toml`.
