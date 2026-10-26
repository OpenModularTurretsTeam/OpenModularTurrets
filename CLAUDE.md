# CLAUDE.md — OpenModularTurrets (Fabric, Multi-Version)

This file guides Claude Code when working in this repository.
Read it fully before making changes.

---

## 1. Project Overview

**OpenModularTurrets (OMT)** is being maintained on **Fabric**, targeting **1.20.1, 1.21.1 and 1.21.11 from a single codebase** using **Stonecutter**.
The port from 1.12.2 is complete; this repo maintains the 1.20.1 / 1.21.1 / 1.21.11 versions.
A **NeoForge 1.21.1** build is being added on the branch `neoforge-test`, with the setup worked out in OpenModularLighting (`../OpenModularLighting`, branch `neoforge-test`; see §4 "Loaders" and §9).
A **Forge 1.20.1** build is being added on the branch `forge-test` (requested by the user on 2026-10-24), ported from OpenModularLighting (branch `forge-test`; see §4 "Forge 1.20.1" and §9).
**Minecraft 1.21.11** (Fabric and NeoForge nodes, same codebase) was ported on 2026-10-26 following OpenModularLighting's 1.21.11 port (see §9); it builds and passes the game tests, but its client has not been checked by hand yet, and the nodes are not published yet.
Inventory and power expanders were dropped and are not to be re-added.

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
- **Jade** (optional integration, `compat/jade/` for the server half and `client/compat/jade/` for the tooltips, registered through the `jade` entrypoint on Fabric and `@WailaPlugin` on NeoForge and Forge).
  Compile-only (`modCompileOnly`, Modrinth maven), never bundled and never required at runtime.
- **NeoForge** 21.1.x (1.21.1) and 21.11.x (1.21.11), built with **ModDevGradle** (approved by the user; build-time only).
  On NeoForge, the Fabric-only APIs above are replaced by NeoForge's own (capabilities, events, payload registration); see §4 "Loaders".
- **Forge** 47.x (1.20.1 only), built with the legacy variant of ModDevGradle (`net.neoforged.moddev.legacyforge`, same artifact and version as `moddev`; build-time only), requested by the user.
  On Forge, the Fabric-only APIs are replaced by Forge's own (capabilities, events, event network channels); see §4 "Forge 1.20.1".
- **MixinExtras** (`io.github.llamalad7:mixinextras-forge`), jar-in-jar in the **Forge jar only** (approved by the user on 2026-10-24): Fabric Loader and NeoForge ship it, Forge 1.20.1 does not, and `LivingEntityMixin` needs it.
- **Sable Companion** (`dev.ryanhcode.sable-companion`, MIT, from `maven.ryanhcode.dev`), jar-in-jar on the **1.21.1 nodes only** (approved by the user on 2026-10-25), for Sable contraptions.
  It has a do-nothing fallback, so it needs no `isModLoaded` guard.
  There is no Companion (and no Sable) for 1.20.1.
- **Sable** (Modrinth, pinned by version id in `sable_version`), dev and game test runtime only, never bundled (approved by the user on 2026-10-25).
  On Fabric only in the dev client: Sable 2.0.6's bundled Veil requires client-only Fabric API modules, so it cannot load in a server environment (the Fabric game test server excludes it); the NeoForge game test server runs with Sable.
  The NeoForge test mod also compiles against Sable (and Companion) for the contraption game tests (approved by the user on 2026-10-25).
- **Create Aeronautics** and **Create** (Modrinth, pinned by version id), in the NeoForge dev runs only (`localRuntime`), never bundled (requested by the user on 2026-10-25), to try turrets on airships by hand.
  Create is also compile-only on NeoForge (approved by the user on 2026-10-25) for `platform/neoforge/NeoForgeCreateMovement`, a `BlockMovementChecks` check registered only when Create is loaded; Create and Create Aeronautics are on the NeoForge game test server for the movement tests.
- **OMLib** (the user's own library, `../OMLib1.20.1+`, branch `1.20.1+`), mounted as the git submodule `omlib` and bundled into every jar (jar-in-jar; Fabric `include`, NeoForge and Forge `jarJar`; requested by the user on 2026-10-27).
  It holds the code both mods used to share (platform services, version compat, energy and item transfer, config, the camouflage menu) and the build logic.
  The library is a mod of its own (`omlib`), declared as a required dependency in the mod metadata, and built as a nested step of the mod build, see §4 "OMLib".
- Nothing else without asking the user first.
  No Architectury, no Cloth Config, no Kotlin.

---

## 2. Tech Stack

| | 1.20.1 (Fabric) | 1.20.1 (Forge) | 1.21.1 (Fabric) | 1.21.1 (NeoForge) | 1.21.11 (Fabric) | 1.21.11 (NeoForge) |
| --- | --- | --- | --- | --- | --- | --- |
| Java | 17 | 17 | 21 | 21 | 21 | 21 |
| Loader | Fabric Loader 0.19.x | Forge 47.4.x | Fabric Loader 0.19.x | NeoForge 21.1.x | Fabric Loader 0.19.x | NeoForge 21.11.x |
| API | Fabric API `0.92.x+1.20.1` | – | Fabric API `0.116.x+1.21.1` | – | Fabric API `0.141.x+1.21.11` | – |
| Gradle plugin | Loom 1.18 | ModDevGradle 2.0 (legacy) | Loom 1.18 | ModDevGradle 2.0 | Loom 1.18 | ModDevGradle 2.0 |
| Mappings | Mojang official (+ Parchment optional) | Mojang official (jar reobfuscated to SRG, mixin refmap) | Mojang official (+ Parchment optional) | Mojang official (NeoForge runtime names) | Mojang official | Mojang official (NeoForge runtime names) |

The 1.21.11 nodes (Fabric and NeoForge) are built like 1.21.1 (the same Gradle plugins).
They have no Sable, Create or Create Aeronautics properties (no 1.21.11 builds known); the build scripts skip those dependencies when `sable_companion_version` is missing.

- Build system: Gradle 9.8 (Kotlin DSL) + **Stonecutter 0.9**, with Fabric Loom 1.18 (`net.fabricmc.fabric-loom-remap`, the variant for obfuscated MC versions) on the Fabric nodes and ModDevGradle 2.0 (`net.neoforged.moddev`) on the NeoForge node.
- Shared build logic is the convention plugin `omlib/build-logic/src/main/kotlin/mod-common.gradle.kts` (Java toolchain, metadata templating, JUnit, `verifyCoreIsolation`, licenses in the jar via `bundled_licenses`, publishing).
  It is mod-independent and lives in its own repository, `OMLib` (`../OMLib1.20.1+`), which every mod mounts as the git submodule `omlib` (the branch `1.20.1+` of the OMLib repository, clean history; an included build in `pluginManagement` of `settings.gradle.kts`).
  Change it inside `omlib/`, commit and push there, then commit the new submodule pointer in each mod; clone a mod with `--recurse-submodules` (or run `git submodule update --init`).
  The few source files that the mods still copy from each other are listed in `omlib/shared-sources.txt`; after changing one, run `python3 -I omlib/tools/shared_sources.py sync --from . --write . ../OpenModularLighting` and then `check`.
  The OMT-specific quality tools (ArchUnit, JaCoCo, PIT, PMD) stay in `build.fabric.gradle.kts`.
- **Gradle itself must run on Java 25+** (Loom 1.18 requirement).
  The compile toolchains (17 / 21) are resolved separately (foojay auto-provisions them if missing).
- **Use Mojang mappings** (`loom.officialMojangMappings()`).
  All class and method names in code and in this doc are Mojmap names (`BlockEntity`, `ResourceLocation`, `AbstractContainerMenu`, and so on).
- Language: Java only.
- Toolchain: configure the Java toolchain per version (17 for 1.20.1, 21 for 1.21.1) in the shared build script.
  Use the version-specific `gradle.properties` to switch.

Check exact latest patch versions of Loader, API, Loom and Stonecutter when setting up or bumping.
Do not hard-code guesses into the build without verifying.

---

## 3. Repository Layout

```
.
├── CLAUDE.md
├── build.fabric.gradle.kts     # Fabric nodes (Loom, Fabric API, datagen, GameTests, ArchUnit/JaCoCo/PIT/PMD)
├── build.neoforge.gradle.kts   # NeoForge node (ModDevGradle)
├── build.forge.gradle.kts      # Forge node (legacy ModDevGradle: reobfJar, mixin refmap, MixinExtras jar-in-jar, pack.mcmeta)
├── omlib/                      # git submodule (OMLib, branch 1.20.1+): the library (src/) every mod bundles, and build-logic/ (the mod-common convention plugin); see §4 "OMLib"
├── stonecutter.gradle.kts      # Stonecutter controller (active version, loader constants, chiseled tasks)
├── settings.gradle.kts         # declares the Stonecutter nodes (<minecraft version>-<loader>)
├── gradle.properties           # shared properties (mod_id, mod_version, maven_group …)
├── versions/
│   ├── 1.20.1-fabric/
│   │   ├── gradle.properties   # minecraft_version, fabric_api, java_version=17 …
│   │   └── src/main/generated/ # datagen output for 1.20.1 (committed; Forge converts it, datagen_from)
│   ├── 1.20.1-forge/
│   │   └── gradle.properties   # forge_version, forge_dependency, mixinextras_version …
│   ├── 1.21.1-fabric/
│   │   ├── gradle.properties   # minecraft_version, fabric_api, java_version=21 …
│   │   └── src/main/generated/ # datagen output for 1.21.1 (committed; NeoForge converts it, datagen_from)
│   ├── 1.21.1-neoforge/
│   │   └── gradle.properties   # neoforge_version, minecraft_dependency (Maven range) …
│   ├── 1.21.11-fabric/
│   │   └── gradle.properties   # minecraft_version, fabric_api, java_version=21 …
│   └── 1.21.11-neoforge/
│       └── gradle.properties   # neoforge_version, minecraft_dependency (Maven range) …
├── src/
    ├── main/java/omtteam/openmodularturrets/...
    ├── main/mc1.20.1-1.21.1/java/ and main/mc1.21.11-/java/   # version source folders: whole classes that differ between versions (§4)
    ├── main/resources/         # SHARED assets + fabric.mod.json, META-INF/neoforge.mods.toml and META-INF/mods.toml (templated)
    ├── client/java/...         # client-only code (split source sets); client/mc…/java: its version source folders
    ├── gametest/mc1.21.11-/    # the game test framework of 1.21.11 (stand-in @GameTest, GameTestCases)
    ├── test/java/...           # JUnit tests for core/
    └── gametest/               # server game tests for both loaders (own mod id openmodularturrets_gametest)
```

- **Mod ID:** `openmodularturrets`.
  **Base package:** `omtteam.openmodularturrets`.
- Use Loom **split environment source sets** (`splitEnvironmentSourceSets()`).
  Client-only code (renderers, screens, models, client networking handlers) goes in `src/client/java`.
  Never reference client classes from `main`.
- `fabric.mod.json` is templated via `processResources`.
  Inject `${version}`, `${minecraft_dependency}` and `${java_version}` from the per-version properties.

---

## 4. Stonecutter

### Setup (`settings.gradle.kts`)

```kotlin
plugins { id("dev.kikugie.stonecutter") version "0.9.8" }

stonecutter {
    create(rootProject) {
        versions(
            "1.20.1-fabric" to "1.20.1", "1.20.1-forge" to "1.20.1",
            "1.21.1-fabric" to "1.21.1", "1.21.1-neoforge" to "1.21.1",
            "1.21.11-fabric" to "1.21.11", "1.21.11-neoforge" to "1.21.11",
        )
        mapBuilds { _, node -> "build.${node.project.substringAfterLast('-')}.gradle.kts" }
        vcsVersion = "1.21.1-fabric" // the version the committed source is written for
    }
}
```

The Stonecutter DSL changes between releases.
If this does not match the installed version, check the Stonecutter docs and update both this file and the build.

### Rules

- **Committed source must always be in the `vcsVersion` (`1.21.1-fabric`) state.** Before committing, switch back if you switched to another node (`./gradlew "Reset active project"` or `"Set active project to 1.21.1-fabric"`).
- Version-conditional code uses Stonecutter comments:

  ```java
  //? if >=1.21 {
  ResourceLocation id = ResourceLocation.fromNamespaceAndPath(MOD_ID, path);
  //?} else {
  /*ResourceLocation id = new ResourceLocation(MOD_ID, path);
  *///?}
  ```

  Inactive branches must be wrapped in `/* … */` exactly as Stonecutter expects.
  Prefer letting Stonecutter generate this by switching versions over hand-writing it.
- **Minimize conditional blocks.** Hide version differences behind small helpers in `omtteam.openmodularturrets.compat.VersionCompat` (or similar) rather than sprinkling `//? if` across gameplay code.
  Gameplay logic (targeting, permissions, consumption) should be version-agnostic.
- **Whole classes that differ between versions** go into version source folders instead of large conditions: `src/<source set>/mc<first>-<last>/java` (both ends included, no `<last>` = no upper bound).
  `mod-common` adds the folders whose range contains the node's Minecraft version, at the node's own path, so Stonecutter processes them on every node and the replacements below reach them.
  Each variant keeps the same package and class name, the ranges of one class must not overlap, and the folders hold no `//?` conditions.
- **Renames** that Mojang applies everywhere are Stonecutter replacements in `stonecutter.gradle.kts`, not conditions: from 1.21.11 `ResourceLocation` → `Identifier`, `noCollission(` → `noCollision(`, `MOVEMENT_SLOWDOWN` → `SLOWNESS`, `animal.horse` → `animal.equine`, `hasImpulse` → `needsSync` and `net.minecraft.Util` → `net.minecraft.util.Util`.
  They work in both directions, so the new names must not be used for anything else in the source.
- Constants can be compared in conditions: `//? if >=1.21 {`, `//? if <1.21 {`, `//? if 1.20.1 {`.
- Version-specific *properties* live in `versions/<v>/gradle.properties`.
  Read them in the build via `property("…")`.

### OMLib (the library, `omlib/`)

Requested by the user on 2026-10-27: the code that OpenModularTurrets and OpenModularLighting used to share lives in the library OMLib (`../OMLib1.20.1+`, a mod of its own with the id `omlib`, package `omtteam.omlib`), which both mods bundle jar-in-jar.
Its README and `CLAUDE.md` describe the layout and the rules (no mod knowledge in the library, public API only where a mod needs it).

- **Where things are:** the platform layer (`Platform`, `LoaderInfo`, `Fuel`, `Network`, `Menus`, `Energy` and their loader classes), the compat helpers (`VersionCompat`, `NbtCompat`, `BlockCompat`, `NetworkCompat`, `MenuCompat`, `CompatBlockEntity`, `CommonTags`, `ClientCompat`), `core.energy`, `inventory.ItemAccess`, `TransferHost`, the config machinery (`@Comment`, `@Range`, `ConfigWriter`, `ConfigRanges`, `ConfigValidator`, `ConfigMigration`), `RegistrationStep`, the block base `CompatEntityBlock` with `BlockCodec`, the single right-click handling `InteractiveBlock` (with interceptors), `ConfigManager` (with the config sync) and the camouflage menu with its screen, packets and models are in the `omtteam.omlib` packages.
  Wherever the rest of this file names those classes under `omtteam.openmodularturrets`, read `omtteam.omlib`; they kept their names and package layout.
- **Build:** the settings script runs `omlib/tools/bootstrap.sh` first (inside `pluginManagement`), which builds the library for all six nodes into `omlib/build/maven` when its sources changed (Loom resolves mod dependencies while it configures, so a task is too late).
  `mod-common` adds that repository and exposes `project.extra["omlibCoordinates"]` / `["omlibVersion"]`; the loader scripts declare the dependency and bundle it, and the metadata templates declare it (`${omlib_version}`).
  The settings script also drops Loom's cached remapped copy of the library when it was rebuilt from other sources.
  `OMLIB_SKIP_BUILD=1` skips the library build.
- **Runtime:** the library's services (`Platform`, `ClientPlatform`) are created on first use, so it does not matter which mod's entrypoint runs first; the library's own entrypoints register the camo menu type and packets.
  The mod passes its transfer block entity types to the registrars (`FabricBlockLookups.register(ModBlockEntities.transferTypes())`, `NeoForgeCapabilities.register(event, ModBlockEntities.transferTypes())` in its own event) and applies its own NeoForge packets in its own `RegisterPayloadHandlersEvent` (`NeoForgeNetwork.applyRegistrations(event, MOD_ID)`).
- **Security and ownership** (moved on 2026-10-27, requested by the user): `core.security` (`TrustManager`, `AccessLevel`, `AccessProfile`, `TrustedPlayer`, `PlayerInfo`, `PlayerIdentity`, `SecurityEnvironment`, `TrustResult`; `DamageRules` stayed here), `security` (`OwnedHost`, `TrustHost`, `OwnershipSettings`, `LevelSecurityEnvironment`, `PlayerAdapter`, `PlayerLookup`, `SecurityNbt`), `blockentity.OwnedBlockEntity`, the claiming classes in `block` (`ClaimLink`, `ClaimSpread`, `ClaimInteraction`, `WandInteraction`, `UnownedMarker`), `item.OwnershipWandItem`, `core.world.Face` / `BlockPosition` / `FloodFill`, the trust packets (`omlib:add_trusted_player`, `omlib:remove_trusted_player`, `omlib:change_access_level`, `omlib:disown`) and the client screens (`TrustedPlayersScreen`, `DisownConfirm`, `CompatScreen`, `OwnershipWandTooltip`, the `trusted_players.png` texture, texts under `*.omlib.*`).
  The mod supplies `OMTOwnershipSettings` (reads the config), `TurretBlock implements ClaimLink` (a turret's owner is its base, and turrets connect claims), `PassiveDefenseBlockEntity extends OwnedBlockEntity`, and `ownershipSettings()` on the base and the trap; the library registers the wand and claim interceptors itself, before the mods' own.
  The mod registers its own item of `OwnershipWandItem` (`ModItems.OWNERSHIP_WAND`) and adds its immunity line to the tooltip (`OwnershipWandImmunityTooltip`).
- **Camouflage:** `CamoBlock' (with `overlayTintIndex()`), `CamoHost` (with `disguiseRules()`) and `DisguiseRules` (this mod: `CamoRules.RULES`) are the hooks; the screen's extra buttons come from `CamoScreenExtras` (`TrapCamoExtras`, registered in `OMTClient.init`: the camouflage trap's Trusted and Disown buttons).
- **Changing the library:** edit, commit and push it, move the `omlib` pointer of both mods and build both (`./gradlew chiseledBuild`); a public signature change touches both mods.

### Loaders

Each Stonecutter node is `<minecraft version>-<loader>`; the loader picks the build script (`build.<loader>.gradle.kts`).
The setup and the patterns come from OpenModularLighting (branch `neoforge-test`), where the whole port is done; port OMT the same way, step by step, and design new cross-cutting interfaces with the user (§7 "Designing APIs").

- **Whole loader-specific classes** go into a `platform/<loader>/` package (`platform/fabric/`, `platform/neoforge/`, `platform/forge/`, also under `client/` and the test mod).
  `mod-common` leaves the other loaders' `platform/` packages out of the compilation, so these classes need no Stonecutter comments.
- **Shared code calls the loader through services:** `platform.Platform` holds the `PlatformServices` record (`LoaderInfo`, `Fuel`, `Network`, `Menus`, `Energy`), `client.platform.ClientPlatform` the `ClientPlatformServices` (`ClientNetwork`).
  The entrypoints install them first thing (`OMTFabric`, `OMTNeoForge`, `OMTFabricClient`, `OMTNeoForgeClient`); a getter before that or a second install throws.
  `NetworkCompat`, `ClientNetworkCompat` and `MenuCompat` forward to them.
- **Small differences** inside a shared class use the loader constants: `//? if fabric {` / `//? if neoforge {` / `//? if forge {` (set in `stonecutter.gradle.kts`); `//? if neoforge || forge {` where both share an API (model data, GameTest annotations), with separate import blocks because the packages differ.
  Keep them inside `compat/` and `platform/`, never in gameplay code.
- `core/` stays loader-free (`verifyCoreIsolation` also forbids `net.neoforged` and `net.minecraftforge`).
- `build.neoforge.gradle.kts` and `build.forge.gradle.kts` compile everything except `datagen` (Fabric-only by design: NeoForge gets the converted Fabric data, `convertFabricData`).
  New code must not use Fabric API or Team Reborn Energy outside `platform/fabric` (and `datagen`), or the NeoForge and Forge nodes stop compiling.
- NeoForge and Forge publishing is active (enabled by the user on 2026-10-24); `publish_enabled=false` in a node's `gradle.properties` would turn that node back into a dry run even with tokens.
- The mod version is `<mod_version>+<minecraft version>-<loader>`, so the jars of both loaders can be told apart.
  The jar files are named `<mod_id>-<mod_version>-mc<minecraft version>-<loader>.jar` (`archiveVersion` in `mod-common`); the release title on CurseForge and Modrinth is the jar name without `.jar` (`displayName`), while the mod metadata and the published version number keep the `+` version.

### NeoForge port: design decisions (made by the user)

These were decided step by step while porting OpenModularLighting (branch `neoforge-test`), which is the reference implementation.
Apply them to OMT the same way; do not re-open them without asking.
New questions that these do not answer go through §7 "Designing APIs".

**Overall approach**

- Native NeoForge support, not Sinytra Connector, not Forgified Fabric API, not Architectury, not a MultiLoader subproject layout.
- Stonecutter loader axis: one source set, nodes `<minecraft version>-<loader>`, one build script per loader; NeoForge only for 1.21.1 (1.20.1 stays Fabric-only).
- ModDevGradle for the NeoForge node (build-time only).
- Whole loader-specific classes in `platform/<loader>/` (left out of the other loaders' builds); `//? if fabric` / `//? if neoforge` only for small differences inside `compat/` and `platform/`.
- Mod-independent build logic in `omlib/build-logic` (`mod-common`), shared between the mods as the `omlib` git submodule (OMLib, `build-logic/`); publishing for NeoForge and Forge was enabled by the user on 2026-10-24.

**Platform layer**

- Two directions: where the loader is in charge (registration, events, capabilities, client registrations), the loader entrypoint calls shared code ("wiring"); only where gameplay code must reach the loader there is a service.
- Services are small interfaces (not one big one), handed over by **startup injection**: each entrypoint creates its implementations and installs them once, first thing.
- Holder: a record `PlatformServices` with every service plus a static holder `Platform` with short getters (`Platform.network()`); client: `ClientPlatformServices` / `ClientPlatform`.
  A new service is a new record component, so every entrypoint stops compiling until it supplies one.
- Fail fast: a getter before `install`, or a second `install`, throws `IllegalStateException`.
- The existing `compat/` facades (`NetworkCompat`, `ClientNetworkCompat`, `MenuCompat`) keep their signatures and forward to the services, so call sites do not change.
- `OMT` holds the shared constants (`MOD_ID`, `LOGGER`, `id()`) and nothing else; the shared start-up is `startup.OMTStartup` (moved out of `OMT` by the user on 2026-10-25, so `OMT` leaves the package cycle); the loader entrypoints are `OMTFabric` / `OMTNeoForge` (`OMTFabricClient` / `OMTNeoForgeClient`).

**Services (names and scope as decided)**

- `LoaderInfo`: `configDir()` and `isModLoaded(modId)`; named implementation classes per loader.
- `Fuel`: `burnTime(stack)` and `remainder(stack)` (the loader's stack-aware remainder hook, like a furnace).
- `Network` / `ClientNetwork`: NeoForge registrations are recorded and applied in `RegisterPayloadHandlersEvent` (later registration throws); a server-to-client packet without a client handler is logged and dropped; channels are `optional()`; handlers run on the main thread.
- `Menus`: `createBlockMenuType(factory)` / `openBlockMenu(player, title, pos, factory)`; opening data is a `BlockPos` only.
- `Energy`: `isBattery(stack)`, `dischargeItem(container, slot, target, maxAmount)`, `pushToBlock(level, pos, side, source, maxAmount)` (side = face of the target block; the generator needs the push).
  These services are in OMLib. OMT has no battery slot yet, but the battery methods are there (decided by the user on 2026-10-26, so that both mods use the same classes) for when one is added.
  `core.energy.EnergyGroup` is an `EnergyAccess` that spreads an insert over several stores (`fill(maxAmount, StoreInserter)`); the Fabric and NeoForge 1.21.11 adapters insert through their own per-store adapters.
  OMT has no group, OpenModularLighting's panel light cluster is one.

**Energy and items**

- Energy approach: one loader-neutral buffer plus thin adapters per loader (not Team Reborn Energy as the internal model, not a buffer per loader).
- The buffer is `core.energy.EnergyStore` (OMT's `core.base.EnergyStore`, extended in OpenModularLighting): `final`; `insert` / `extract` (limited by `maxInsert` / `maxExtract`) for other blocks, `generate` / `consume` / `drain` for the owner, `setAmountUnchecked` for loading and rollback.
  `onChange` may fire for a Fabric transaction that is rolled back later (an extra `setChanged()` is accepted).
- Block entities expose `@Nullable EnergyAccess getEnergyAccess(side)` (core interface; a store or a view over several) and `getItemAccess(side)` by implementing `blockentity.TransferHost`; `ModBlockEntities.transferTypes()` lists them, and the loader registrations (`FabricBlockLookups`, `NeoForgeCapabilities`, `ForgeBlockCapabilities`) iterate over that list, so a new transfer block entity needs no loader code.
- Fabric adapter: one `SnapshotParticipant` per store, cached in `platform/fabric` with weak keys **and weak values** (a plain `WeakHashMap` would leak, because the adapter references its store).
  NeoForge adapter: `IEnergyStorage` (`EnergyHandler` with one `SnapshotJournal` per store on 1.21.11), clamped to `int`; 1 E = 1 FE.
- Item transfer: the vanilla `Container` stays the inventory; `inventory.ItemAccess` (record: container, first slot, slot count, insert filter, extract filter; `insertOnly(container)`) describes what other blocks may do, exposed by `getItemAccess(side)`.
  Adapters build on the loaders' own container wrappers (Fabric `InventoryStorage`, slotted for Jade; NeoForge `RangedWrapper` + `InvWrapper`).

**Registration and events**

- Registration approach B: each registry class is registered in the `RegisterEvent` of its own registry on NeoForge (not everything in the first event).
  `OMTStartup.init()` loads the config and registers the packets; `OMTStartup.registrationSteps()` lists `RegistrationStep(registry, call)` records in dependency order; Fabric runs them in order.
  In OMT, `OMTStartup.init(plugins)` also loads the turret definitions first (and hands the addon turrets' default stats to `OMTConfig.init`); the loader entrypoint passes the turret plugins it found (decided by the user).
  NeoForge finds plugins by annotation (decided by the user): `@OMTPlugin.Register` / `@OMTClientPlugin.Register` (nested in the interfaces, RUNTIME, TYPE), read from the mod scan data by `platform/neoforge/NeoForgePlugins` (client ones on the client only); the mod id is the first mod of the jar; plugins are sorted by mod id, then class name.
  NeoForge throws at common setup if a step never ran.
- Shared handlers called by the loaders' events: OMLib registers the right-click event (`InteractiveBlock.handleUse`, with the `Interceptor`s: the library's own for the ownership wand and claiming come first, OMT adds the memory card in `OMTStartup.init`) and the join/disconnect events of the config sync (`ConfigSync`) once for all mods (since 2026-10-27, decided by the user).
- Creative tab: `VersionCompat.creativeTabBuilder()` with a loader condition (no service).
- Common tags: plain `TagKey`s with the `c:` ids, no Fabric `ConventionalItemTags`.
- Recipe conditions: the condition logic stays shared; each loader registers it with its own API in `platform/<loader>`.

**Client**

- Shared client start-up class (`OMTClient`, like `OMLClient`) with `init()`, `registerScreens(ScreenRegistrar)` and tooltip content; `ScreenRegistrar` (generic method, takes `MenuScreens::register` and `event::register`) lives in `client/`.
- Renderers and colour providers expose their content (e.g. a `BlockColor` method plus the block list, a render method taking level, pose stack, buffers and camera); the loader entrypoints register them.
- Render types on NeoForge come from `"render_type"` in the model JSON; Fabric keeps `BlockRenderLayerMap` (keep both in sync).
- OMT (decided by the user): client turret plugins are handed in by the loader client entrypoint like on the server (`OMTClient.init(plugins)`); entity renderers, block entity renderers and model layers go through `OMTClient.registerEntityRenderers(EntityRendererRegistrar)`, `registerBlockEntityRenderers(BlockEntityRendererRegistrar)` (both generic interfaces in `client.render`, there to avoid a package cycle) and `registerModelLayers(BiConsumer)`; on NeoForge the camouflage disguise reaches the model as `CompatBlockEntity.RENDER_DATA` model data, and a colour provider on the hosts forwards the disguise's colour.

**Data**

- No NeoForge datagen: the NeoForge build converts the Fabric output of `datagen_from` (`fabric:load_conditions` → `neoforge:conditions`, `condition` → `type`); only conditions in the mod's own namespace convert, anything else fails the build.

**GameTests**

- Shared test classes for all loaders: they implement a shared interface (e.g. `OMTGameTests`, like OpenModularLighting's `OMLGameTests`) instead of `FabricGameTest`, use the mod's own empty 8 × 8 × 8 structure (`structure/` and, for 1.20.1, `structures/`), and carry `@GameTestHolder(MOD_ID)` + `@PrefixGameTestTemplate(false)` behind `//? if neoforge`.
- `TestTransfer` with `hasEnergy`, `insertEnergy`, `insertItem`, `extractItem` (Fabric lookups / NeoForge capabilities) replaces direct Fabric lookups in tests.
  OMT's tests have no direct lookups (items go through vanilla hoppers, energy through the generator push), so OMT has no `TestTransfer` yet; add it when a test needs one.
- The test battery keeps its energy in the item's custom data, with loader-specific energy access (OMT has no battery).
- Test mod id with an underscore on both loaders (`openmodularturrets_gametest`); NeoForge mod ids cannot contain hyphens.
- No test class may sit in a main-mod package (NeoForge modules cannot share packages); reach protected methods through public vanilla API.

**Jade**

- `@WailaPlugin` on the plugin without a loader condition (NeoForge finds it by the annotation, Fabric keeps the `jade` entrypoint).
- The plugin is split in two for every version, because Jade 21's `ITooltip` extends a client class: `compat.jade.OMTJadePlugin` and the providers (server data, and `lines(…)` that turn the data into tooltip lines) stay in main, `client.compat.jade.OMTJadeClientPlugin` and `JadeLinesComponent` (the tooltip) are in the client source set; both are listed under the `jade` entrypoint.
- Jade stays off the NeoForge GameTest server (it sends a packet to the vanilla mock players, which NeoForge refuses).
  It is in the NeoForge dev runs (`localRuntime`), which load only the mod itself, not the test mod (`loadedMods`).

### Forge 1.20.1

The node `1.20.1-forge` follows the NeoForge setup and was worked out in OpenModularLighting first (branch `forge-test`); these are the differences:

- **Build:** `build.forge.gradle.kts` uses `net.neoforged.moddev.legacyforge` (`legacyForge { version = forge_version }`).
  Forge 1.20.1 runs on SRG names: the released jar is `reobfJar` (what `publishMods` uploads), and other mods (Jade) come through the remapping configurations (`modCompileOnly`, `localRuntime` via `obfuscation.createRemappingConfiguration`).
- **Mixins:** the mixin annotation processor writes `openmodularturrets.refmap.json`; the Forge build adds `"refmap"` to its copy of the shared mixin config and names the config in the jar manifest (`MixinConfigs`; Forge 1.20.1 has no `[[mixins]]` in `mods.toml`). MixinExtras (`mixinextras_version`) is jar-in-jar'd into the Forge jar only. Forge's own events cannot replace `LivingEntityMixin`: knockback happens before `hurt` records the damage source, and cancelling `LivingDropsEvent` would also drop a killed player's inventory.
- **Data:** `convertFabricData` (a function in `omlib/build-logic`, shared with NeoForge) copies the 1.20.1 Fabric output with `"conditions"`. The node also generates `pack.mcmeta` (pack format 15) for the mod and the test mod: without one, Forge 1.20.1 loads neither's data.
- **Entrypoints:** `OMTForge` (`@Mod`, mod bus from `FMLJavaModLoadingContext`); Forge 1.20.1 has no client-only `@Mod` classes, so it constructs `client.platform.forge.OMTForgeClient` by name on a client (main cannot see the client source set). Plugins: `ForgePlugins`, the same `@OMTPlugin.Register` / `@OMTClientPlugin.Register` scan as `NeoForgePlugins`.
- **Services:** `ForgeNetwork` uses one `EventNetworkChannel` per packet type (named by its id, missing remote accepted, decoded on the network thread, handled on the main thread; S2C handlers dispatched like on NeoForge). `ForgeMenus` uses `IForgeMenuType` / `NetworkHooks.openScreen` (which builds the menu itself, so `BaseGuiGameTests` needs no Forge override).
- **Capabilities:** no registration event in 1.20.1; `OMTForge` attaches `ForgeBlockCapabilities` to every `TransferHost` in `AttachCapabilitiesEvent` (`ForgeBlockCapabilities::attach`). It asks the block entity on every query (attached during construction) and keeps a side's `LazyOptional` while the block entity returns the same access object.
- **Client:** screens and item renderers are registered in `FMLClientSetupEvent` (no events for them). Forge 1.20.1 takes item renderers only from `Item#initializeClient`, so `ForgeItemRenderer.register` sets the Forge-added `Item.renderProperties` field by reflection (same name in dev and release; the shared item classes stay loader-free). `ForgeCamoModel` is `NeoForgeCamoModel` without the ambient occlusion override (Forge 1.20.1 does not pass model data there); its colour provider reads the model data through `level.getModelDataManager()`.
- **GameTests:** `runGameTestServer` with `forge.enabledGameTestNamespaces`; the same annotations as NeoForge (`net.minecraftforge.gametest`). Vanilla 1.20.1's `makeMockServerPlayerInLevel` breaks Forge's login handling (no netty channel); OMT's tests use the loaders' fake players instead, which work.
  The library jar the client and game test source sets use is the one with the dev names (classifier `dev`, published by OMLib next to the SRG-named one): the game test run has no remapping, and a Forge game test server whose mod fails to load still exits normally, so `check` also requires the "All N required tests passed" line in `run/logs/latest.log` (Forge and NeoForge build scripts).
- Learned in OpenModularLighting: `DamageSources#source` is private in vanilla 1.20.1 (only Fabric API widens it); OMT does not use it.

### Commands

```bash
./gradlew "Set active project to 1.20.1-fabric"   # switch source to 1.20.1
./gradlew "Set active project to 1.21.1-fabric"   # switch back (VCS state)
./gradlew chiseledBuild                           # build ALL nodes (custom task in stonecutter.gradle.kts)
./gradlew :1.20.1-fabric:build                    # build a single node
./gradlew :1.21.1-fabric:runClient                # run client for a node
./gradlew :1.21.1-fabric:runServer
./gradlew :1.21.1-fabric:runGameTest              # server game tests for one node (also run by check / build)
./gradlew :1.21.1-neoforge:runGameTestServer      # the same game tests on NeoForge (also run by check / build)
./gradlew :1.20.1-fabric:runDatagen               # regenerate data for a node
./gradlew :1.21.1-fabric:runDatagen
./gradlew :1.21.11-fabric:runDatagen              # (the 1.21.11 NeoForge node converts this output)
./gradlew :1.21.1-neoforge:runClient              # NeoForge client (run dir versions/1.21.1-neoforge/run; with Jade)
./gradlew :1.21.11-fabric:runClient               # Fabric 1.21.11 client (shared run dir)
./gradlew :1.21.11-neoforge:runClient             # NeoForge 1.21.11 client (run dir versions/1.21.11-neoforge/run; with Jade)
./gradlew :1.21.11-fabric:runGameTest             # the game tests on the 1.21.11 nodes (also run by check / build)
./gradlew :1.21.11-neoforge:runGameTestServer
./gradlew :1.20.1-forge:runClient                 # Forge client (run dir versions/1.20.1-forge/run; with Jade)
./gradlew :1.20.1-forge:runGameTestServer         # the game tests on Forge (also run by check / build)
./gradlew dependencyMap                           # interactive package dependency map -> build/reports/dependency-map/index.html
./gradlew :1.21.1-fabric:pitest                   # mutation testing of core -> versions/1.21.1-fabric/build/reports/pitest/index.html
./gradlew chiseledPublish                         # upload all nodes to CurseForge/Modrinth (custom task)
./gradlew :1.21.1-fabric:publishMods              # upload one node
```

### Publishing

- Uses `me.modmuss50.mod-publish-plugin` (approved by the user), configured in `publishMods` in `omlib/build-logic/src/main/kotlin/mod-common.gradle.kts` (the loader scripts only set the jar to upload).
- Settings in `gradle.properties`: `release_type` (release/beta/alpha), `curseforge_project_id` (224663, the old OMT page), `modrinth_project_id` (empty = Modrinth skipped).
- Tokens are never committed: environment variables `CURSEFORGE_TOKEN` / `MODRINTH_TOKEN`, or `curseforge_token` / `modrinth_token` in `~/.gradle/gradle.properties`.
  Without a token the upload is a dry run (output in `versions/<v>/build/publishMods/`).
- **Changelog** (`CHANGELOG.md`, Keep a Changelog format): every user-visible change gets a one-line entry under `## [Unreleased]` (Added / Changed / Fixed / Removed), by hand or via `./gradlew changelogEntry --type=fixed --entry="…"`.
  Add the entry together with the change.
- **Release steps:** set `mod_version` (e.g. `4.0.0-alpha.2`) and `release_type` → `./gradlew releaseChangelog` (moves Unreleased into `## [<mod_version>] - <date>`) → `./gradlew chiseledBuild` → commit `gradle.properties` and `CHANGELOG.md` as `feat: version <mod_version>` → tag that commit with a lightweight tag named exactly `<mod_version>` (`git tag 4.0.0-beta.8`, no `v` prefix, like the existing tags) → `./gradlew chiseledPublish` → push the branch and the tag (`git push origin <branch> <mod_version>`).
  Publishing uploads only that version's section and fails if it is missing.
  (Task names may differ slightly depending on Stonecutter's version.
  Use `./gradlew tasks` if unsure.)

**Definition of done for any change:** `./gradlew chiseledBuild` succeeds for **all** nodes, and the source is left in the `1.21.1-fabric` state.

### Core Game Logic Source

The game logic of the mod should be implemented in the "core" folder and be completely free from minecraft imports.
The logic consists of:

1. The turret base behaviour (targeting, ammo handling, upgrade/addon handling) and the turret's own rules (`TurretState`: fire cooldown, concealer cycle; `RayMath`, `ProjectileMath`: damage formulas)
2. The trusted player permission system (incl. who turret damage may hit: `DamageRules`)
3. an abstract representation of a target entity
4. The generator's burning rules (`core.generator.GeneratorLogic`)
5. Unit Tests to test its behaviour.

`docs/architecture.md` is the human-oriented guide to the structure (layers, flows, how to add things, known weak spots); keep it in sync when the structure changes.
`ArchitectureTest` (`src/test/.../architecture`, ArchUnit) checks core isolation, that main never uses client code, and package cycles against its `KNOWN_CYCLES` list (a ratchet: update the list when a cycle shrinks; never add to it without asking the user).
PMD (`gradle/pmd/ruleset.xml`, report only, active version) measures complexity and size: `versions/1.21.1/build/reports/pmd/`.
JaCoCo records coverage in the unit tests and on the game test server; `check` prints the line coverage of `core` and `api` per package (active version only).
PIT (`./gradlew :1.21.1-fabric:pitest`, on demand, not part of `check`) runs mutation testing on `core`; surviving mutations show behaviour the tests do not pin down.

`./gradlew check` runs `verifyCoreIsolation`, which fails if anything under `core/` imports `net.minecraft`, `net.fabricmc`, `com.mojang`, `team.reborn` or non-core mod packages.
Tests live in `src/test/java` (JUnit, test-only dependency).

### Game tests

In-game behaviour (turrets firing, effects on entities) is tested with GameTests in `src/gametest`, a test mod (`openmodularturrets_gametest`, never shipped) whose test classes are shared by all loaders.
Classes implement `OMTGameTests` and use its `EMPTY_STRUCTURE` (the mod's own 8×8×8 air structure in `data/openmodularturrets/structure/`, and `structures/` for 1.20.1) with the scene built in code.
On Fabric (Loom `fabricApi.configureTests`) they are listed under the `fabric-gametest` entrypoint in `src/gametest/resources/fabric.mod.json`; on NeoForge and Forge they carry `@GameTestHolder(OMT.MOD_ID)` and `@PrefixGameTestTemplate(false)` behind `//? if neoforge || forge` (the gametest source set is a second mod, `META-INF/neoforge.mods.toml` / `META-INF/mods.toml`).
The test mod's loader entrypoints (`gametest/platform/<loader>`) run its registration steps (`OMTGameTestMod`) and forward entity loads to `ProjectileTurretGameTests.onEntityLoad`.
`check` (and so `build` / `chiseledBuild`) runs them on a dedicated test server: `runGameTest` on Fabric (`versions/<v>/build/run/gameTest`, the world is deleted before every run), `runGameTestServer` on NeoForge and Forge.

- **From 1.21.11 on** (the framework changed in 1.21.5: a test is a registered test function plus a data-driven test instance, batched by test environment) vanilla's `@GameTest` is gone.
  The version folder `src/gametest/mc1.21.11-` has a stand-in `@GameTest` with the same name and attributes in the test classes' own package, and `GameTestCases`, which turns the methods into test functions and instances `openmodularturrets_gametest:<class>/<method>` and each `batch` into an empty test environment (`timeoutTicks`, default 100, as before).
  Fabric registers them from the `fabric-gametest` entrypoints, NeoForge from the mod file scan (the classes with `@GameTest` methods); the `@GameTestHolder` / `@PrefixGameTestTemplate` conditions stop at 1.21.11.
- `TurretScene` builds a ready-to-fire base + turret (owner, always on, full energy, max range) and spawns targets (husks without AI).
  A base placed with `setBlock` has no owner: it stays, but does not fire until it has one (`bases.unownedBasesActive`).
- `TurretScene` also loads the turret's ammo into the base (energy alone is not enough for the railgun and the projectile turrets).
- Turrets that scatter get up to 10 attempts, each with a fresh target (`TargetAttempts`); a miss replaces the target, a hit passes.
  A ray attempt ends with its ray, a projectile attempt when its first projectile is gone.
  The test server does not tick entities in the chunks next to a test, and projectiles remove themselves before flying into such a chunk, so `TurretScene` force-loads the chunks around the scene while a test runs (otherwise a grenade that lands just past the target would vanish instead of exploding).
  Projectile tests fail just before their timeout with their state (shots, followed projectile, target) instead of timing out silently.
- Turret ranges reach into neighbouring tests, so every turret test gets its own `batch` and ends with `TurretScene.succeedWhen`, which removes the turret, base and entities once it passed (1.20.1 keeps a finished batch's blocks and entities next to the following one).
- `BaseGuiGameTests` clicks a base through `ServerPlayerGameMode#useItemOn` (the real server path, including the loader's block-use event) and checks that the menu opens for the owner only.
  The loaders' `FakePlayer`s refuse to open menus, so the test uses a small subclass that opens them without sending a packet (NeoForge opens block menus through the two-argument `openMenu`, which it overrides too).
  Cast `getBlockEntity` results where 1.20.1 returns a plain `BlockEntity` (IntelliJ only sees 1.21 and calls such casts redundant).

---

## 5. Known 1.20.1 vs 1.21.1 Differences

Wrap each of these behind a helper where possible.

| Area | 1.20.1 | 1.21.1 |
| --- | --- | --- |
| `ResourceLocation` | `new ResourceLocation(ns, path)` | `ResourceLocation.fromNamespaceAndPath(ns, path)` → use `OMT.id(path)` helper |
| BlockEntity NBT | `load(CompoundTag)`, `saveAdditional(CompoundTag)` | `loadAdditional(CompoundTag, HolderLookup.Provider)`, `saveAdditional(CompoundTag, HolderLookup.Provider)` |
| BE update packet tag | `getUpdateTag()` | `getUpdateTag(HolderLookup.Provider)` |
| ItemStack extra data | `stack.getTag()` / NBT | **Data Components** (register custom `DataComponentType`s, or `CUSTOM_DATA`) |
| ItemStack NBT save | `stack.save(tag)` / `ItemStack.of(tag)` | `stack.save(provider)` / `ItemStack.parse(provider, tag)` (codecs) |
| Block interaction | `Block#use(...)` → `InteractionResult` | `useWithoutItem(...)` and `useItemOn(...)` → `ItemInteractionResult`; use `InteractiveBlock` (Fabric `UseBlockCallback`), which is the same on both |
| Block codecs | not required | `BaseEntityBlock` and others require `codec()` returning a `MapCodec`; extend `CompatEntityBlock` and pass a `BlockCodec` (`codecOf(...)`) to its constructor |
| Networking | `ResourceLocation` channel + `FriendlyByteBuf` | `CustomPacketPayload` + `StreamCodec` + `PayloadTypeRegistry` |
| Extended menus | `ExtendedScreenHandlerFactory` writing to a buf | `ExtendedScreenHandlerFactory<D>` with a `StreamCodec` for `D` |
| Entity synced data | `defineSynchedData()` | `defineSynchedData(SynchedEntityData.Builder)` |
| Tooltips | `appendHoverText(stack, Level, list, flag)` | `appendHoverText(stack, Item.TooltipContext, list, flag)` |
| Screen background | `renderBackground(GuiGraphics)` | `renderBackground(GuiGraphics, mouseX, mouseY, partialTick)` |
| VertexConsumer | `.vertex().color().uv()…endVertex()` | `.addVertex().setColor().setUv()…` (no `endVertex`) |
| Data folders | `tags/blocks`, `tags/items`, `loot_tables`, `recipes`, `advancements` | `tags/block`, `tags/item`, `loot_table`, `recipe`, `advancement` |
| Recipe JSON result | `"result": {"item": "...", "count": n}` | `"result": {"id": "...", "count": n}` |

**1.21.11 helpers** (use these instead of new `//? if` blocks; the port follows OpenModularLighting's):

| Difference | Helper |
| --- | --- |
| Reading NBT (1.21.11 getters return `Optional`, UUID and list helpers changed) | `compat.nbt.NbtCompat`: `getInt` / `getLong` / `getFloat` / `getDouble` / `getBoolean` / `getString` (with a fallback), `getCompound` (also of a `ListTag` index), `getCompoundList`, `getIntArray` / `getLongArray`, `hasCompound` / `hasList` / `hasString` / `hasNumber`, `getUuid` (null if missing) / `putUuid`, `readBlockState`; never call the `CompoundTag` getters or `Tag.TAG_*` directly. It depends on Minecraft only, so packages outside the `compat` cycle may use it |
| BlockEntity NBT | `CompatBlockEntity` keeps `readData` / `writeData` with a `CompoundTag`; on 1.21.11 it passes the whole compound through `ValueInput` / `ValueOutput`, so the saved format stays the same |
| Block broken or replaced (`onRemove` is gone in 1.21.11) | override `CompatBlockEntity.onBlockRemoved(Level)` (server only, before the block entity is removed; the turret base and the generator drop their items there); on <1.21.11 the block's `onRemove` calls `CompatBlockEntity.handleRemove` (behind `//? if <1.21.11`), on 1.21.11 vanilla's `preRemoveSideEffects` calls it |
| Block state queries, interaction result | `compat.block.BlockCompat.isSolidRender` / `lightBlock`, `sidedSuccess` (gone in 1.21.11, `SUCCESS` there) |
| Damaging entities, singleplayer host | `VersionCompat.hurt(entity, source, amount)` (`hurtServer` on 1.21.11), `VersionCompat.isSingleplayerOwner(player)`; registry lookups through `registryAccess().lookupOrThrow(registry).getOrThrow(key)` (works on every version) |
| Stack and slot lists | `VersionCompat.saveItems` / `loadItems` (1.21.11: `ValueOutput` / `ValueInput` behind the compound) |
| Registering blocks, items, block entity types, entity types | create blocks and items from their properties through `VersionCompat.blockProperties` / `itemProperties` / `blockItemProperties(properties, id)` (1.21.11 needs the registry id before construction; block items keep the `block.` name; `ModBlocks` / `ModItems` take a factory and the properties), block entity types with `VersionCompat.blockEntityType(factory, blocks…)` (no `BlockEntityType.Builder` on 1.21.11), entity types with `VersionCompat.buildEntityType(builder, id)`. Also in the test mod |
| Neighbour updates, light absorption, pick block (blocks extending `CompatEntityBlock`) | override `onNeighborChanged(state, level, pos, neighbourPos)` (the position is `null` on 1.21.11, which does not tell), `lightAbsorbed(state, vanilla)` / `skylightPropagates(state, vanilla)` and `pickItem`; never `neighborChanged`, `getLightBlock` or `getCloneItemStack` directly |
| Block facing, render shape, items' `use`, entity collisions | `EnumProperty<Direction>` for `FACING` (`DirectionProperty` is gone, the same type on all versions), `BlockCompat.entityRenderShape()` (`ENTITYBLOCK_ANIMATED` is gone), `Item#use` returns `InteractionResult` on 1.21.11 (two overrides behind a condition that call one shared method), `DefenseFenceBlock#entityInside` has two overrides like that |
| Creating entities, owners, daylight, per-player sounds | `VersionCompat.createEntity(type, level)` (1.21.11 asks for a spawn reason), `ownerUuid(ownable)`, `isDay(level)`, `playNotifySound(player, sound, source, volume, pitch)`; the server of an entity or player is `level().getServer()`, a player's level is `(ServerLevel) player.level()` |
| Profiles and operators | `VersionCompat.profileName` / `profileId` (authlib 7 records), `cachedProfile(server, name)` (blocking for unknown names, call it off the server thread), `cachedName(server, uuid)`, `isOwnerOperator(player)` (permission sets on 1.21.11) |
| Mixins on `LivingEntity` | `LivingEntityMixin` hooks `hurt` (`hurtServer(ServerLevel, …)` on 1.21.11, one `HURT` constant) and `dropAllDeathLoot` / `shouldDropLoot` (which takes the level on 1.21.11); the injection points exist on 1.21.11 as read from the class file, a game test run on 1.21.11 has to confirm them |
| Client: GUI drawing, tooltips, shift key, input events | `client.compat.ClientCompat`: `blit` (256 × 256 GUI textures; 1.21.11 takes the render pipeline), `renderTooltip` / `renderComponentTooltip` (`setTooltipForNextFrame` on 1.21.11), `hasShiftDown()`, `partialTick()`; GUI text colours need an alpha channel (`0xFF000000`): from 1.21.6 on, text without one is not drawn. 1.21.9 input events: `onClick(MouseButtonEvent, boolean)`, `keyPressed(KeyEvent)`, `onRelease(MouseButtonEvent)` are two overrides behind a condition that call one shared method; the mouse wheel goes through `CompatScreen#onScroll` |
| Camouflage models, render layers | up to 1.21.1 baked models, from 1.21.11 block state models: `FabricCamoModel` (`ForwardingBakedModel` / `WrapperBlockStateModel`) and `NeoForgeCamoModel` (`BakedModelWrapper` / `DelegateBlockStateModel`) exist once per version folder (`src/client/mc1.20.1-1.21.1`, `src/client/mc1.21.11-`) and the loader client entrypoints wrap the models (by model id up to 1.21.1, by block state on 1.21.11); Fabric render layers through `BlockRenderLayerMap` (new package and `ChunkSectionLayer` on 1.21.11), NeoForge through `"render_type"` in the model JSON |
| Block entity and entity renderers, turret models (1.21.11 render states) | `src/client/mc1.20.1-1.21.1` and `src/client/mc1.21.11-` hold `BlockEntityRendererRegistrar`, `TurretRenderer`, `LeverRenderer`, `BillboardRenderer`, `ArrowShapedRenderer`, `PlasmaRenderer` and `TurretModel` (decided by the user). On 1.21.11 a renderer copies what it needs into a render state when the frame is extracted and draws in `submit` through `SubmitNodeCollector`; drawing is deferred, so a model shared by many blocks must not be posed at submit time: `TurretModel` and `LeverRenderer.LeverModel` are `Model<S>` whose pose is applied in `setupAnim` right before drawing. `EntityRendererRegistrar` is shared (`EntityRendererProvider` has one type argument on all versions); `OMTClient` and the entrypoints call the same methods on all versions |
| Turret and crank items (item renderers) | up to 1.21.1 `ItemStackRenderer` (Fabric `BuiltinItemRendererRegistry`, NeoForge `getCustomRenderer`, Forge `ForgeItemRenderer`); from 1.21.11 special model renderers (decided by the user): `TurretSpecialRenderer` / `LeverSpecialRenderer` with the item model types `openmodularturrets:turret` (with `"turret": "<id>"`) and `openmodularturrets:lever` in the item model definitions (`minecraft:special`, `base` = the old `builtin/entity` model for the display transforms), registered by `OMTClient.registerSpecialItemModels` (NeoForge: `RegisterSpecialModelRendererEvent`; Fabric API has no hook, so the client mixin `SpecialModelRenderersMixin` in `openmodularturrets.client.mixins.json` adds the types to vanilla's table; before 1.21.11 the class is an empty `@Pseudo` stub, so the config stays valid) |
| World-space effects (rays, placement outline) | `ClientCompat.lines()` / `lightning()` / `renderLineBox`; Fabric 1.21.11 draws them in `WorldRenderEvents.END_MAIN` (the new world render events have no after-translucent or last event), NeoForge in `RenderLevelStageEvent.AfterTranslucentBlocks` / `AfterParticles` |
| Item models | every item needs an item model definition `assets/openmodularturrets/items/<id>.json` (read from 1.21.4 on, older versions ignore the folder) that points at its `models/item/<id>.json`; without one 1.21.11 shows the missing texture. The turret and lever items (item renderer, `builtin/entity` parents) get theirs with the item renderers |
| Furnace fuel | `Fuel.burnTime(level, stack)` (1.21.11: the level's fuel values; the other versions ignore the level), `GeneratorBlockEntity.isFuel(level, stack)` |

**Same in both** (no conditionals needed): `DamageTypes` / damage-type registry, `GuiGraphics`, `PoseStack`, the general Fabric Transfer API shape, and the `BlockEntityTicker` pattern.

### Resources strategy

- **`assets/`** (textures, models, blockstates, lang, sounds): identical across versions.
  Keep them handwritten in shared `src/main/resources/assets/openmodularturrets/`.
- **`data/`** (recipes, loot tables, tags, advancements): folder names and JSON formats differ.
  **Generate them with Fabric Datagen.**
  - Run datagen per version.
  - Configure the output directory relative to the version project (`project.file("src/main/generated")` → `versions/<v>/src/main/generated`).
  - Add that directory as a resource source dir.
  - Commit the generated files.
- Do not handwrite files into `data/` in shared resources.
- Do not generate new images via AI, f.ex. for textures, if an image is needed, just use a placeholder image and notify the user of the missing images.

---

## 6. Architecture

```
omtteam.openmodularturrets
├── OMT.java                     # MOD_ID, id() helper, logger (constants only)
├── startup/                     # OMTStartup: shared start-up (init, registrationSteps)
├── api/                         # public turret API: OMTPlugin entrypoint, TurretDefinition, AmmoDefinition,
│   └── weapon/                  # OMTApi lookups; weapon kinds (Projectile/Ray/Effect/AreaWeapon)
├── registry/                    # ModBlocks, ModItems, ModBlockEntities, ModEntities,
│                                # ModMenus, ModSounds, ModCreativeTabs, ModDataComponents (1.21),
│                                # TurretRegistry (definitions, plugin loading), BuiltinTurrets
├── block/
│   ├── base/                    # TurretBaseBlock (tiers 1–5)
│   ├── turret/                  # AbstractTurretBlock + one class per turret type
│   └── misc/                    # lever, hard walls, fences, etc.
├── blockentity/
│   ├── TurretBaseBlockEntity    # targeting, inventory, energy, upgrades, addons, permissions
│   └── turret/                  # AbstractTurretBlockEntity + subclasses
├── targeting/                   # TargetingSettings, TargetSelector, line-of-sight, priorities
├── core/                        # The core logic files for turret base logic (targeting/upgrades/ticking etc.)
├── camo/                        # CamoHost (bases + camo trap), CamoRules, CamoData, light block state properties
├── security/                    # TrustedPlayer, AccessLevel, PermissionManager
├── inventory/                   # base inventory, slot filters, ItemAccess (what other blocks may move)
├── item/                        # ammo, upgrades, addons, additional items
├── entity/projectile/           # bullets, grenades, rockets, lasers, slugs, …
├── menu/                        # AbstractContainerMenu implementations
├── network/                     # packets (C2S: settings/trust changes; S2C: sync)
├── config/                      # self-written config (JSON via Gson), no library
└── compat/                      # VersionCompat, CompatBlockEntity, CompatEntityBlock (Stonecutter-conditional code lives here)

(client source set)
omtteam.openmodularturrets.client
├── OMTClient.java               # shared client start-up (loader entrypoints in client/platform/<loader>)
├── api/                         # OMTClientPlugin entrypoint, TurretModelRegistrar
├── render/blockentity/          # turret renderers (rotating heads), base renderer
├── render/entity/               # projectile renderers
├── model/                       # Java entity-style turret models
├── screen/                      # base GUI, configuration/targeting/trust screens
└── network/                     # client packet handlers
```

### Turret ↔ base relationship

- A turret block has a `FACING` property (all 6 `Direction`s).
  `FACING` is the direction **away from the base**.
  The base is at `pos.relative(facing.getOpposite())`.
- Turrets can only be placed against a base face, and only by an admin of that base (`TurretBlock.attachResult`, `TurretPlacement.Result.NO_PERMISSION`; placing without a player is refused).
  A turret without a base does nothing and should break or drop if its base is removed.
- Whether a turret may be attached or stay is decided in one place, `core.base.TurretPlacement` (`canAttach` / `canStay`, returning a `Result` with the reason); the client asks it too (placement outline), with the turrets read from the world.
  `maxSimultaneous` only limits placing, unless `turrets.dropTurretsOverMaxSimultaneous` (default false) is set.
- The **base** finds its turrets by checking its 6 neighbours.
- The **base** owns all state:
  - targeting settings
  - the current target (or targets per turret)
  - ammo
  - energy
  - upgrades and addons
  - trusted players
- **Turret block entities are thin.** They handle:
  - their aim (yaw/pitch)
  - cooldown
  - firing visuals and sounds
  - spawning projectiles

  They request ammo and energy from the base.

### Turret API (adding turrets)

Other mods add turrets through `omtteam.openmodularturrets.api` (developer guide: `docs/turret-api.md`).
The built-in turrets use the same path, so there is no special casing per turret type anywhere:

- `core.turret.TurretType` is a namespaced value class (built-in constants keep their names), built with `TurretType.builder(ns, path)`; `TurretTypes` is the registry (built-in first, frozen after loading).
  Per-type behaviour is data on the type (`canMountOn`, `usesScattershot`, `skipsSlowedTargets`) or on the weapon, never a `switch`/`==` on a specific type.
  `AmmoType` is a value class too (`AmmoType.of(ns, path)`); compare with `equals`.
- `OMTPlugin` (Fabric entrypoint `openmodularturrets`, NeoForge `@OMTPlugin.Register`) registers `TurretDefinition`s (type + `TurretWeapon` + default `TurretStats`) and `AmmoDefinition`s.
  `TurretRegistry.load(plugins)` runs first in `OMTStartup.init` (built-ins via `BuiltinTurrets`, then the plugins the loader entrypoint found), then freezes the types; `ModBlocks`/`ModItems` register a block and item per type with the type's id, all on the shared `TURRET` block entity type.
- Weapons (`api.weapon`): `ProjectileWeapon`, `RayWeapon` (damage rules = core `RayProfile`), `EffectWeapon`, `AreaWeapon`, `SelfTargetingWeapon` (finds its own targets); projectile and ray weapons take entity hit handlers; `TurretBlockEntity` dispatches on the kind, `RayWeapons` / `EffectWeapons` do the work.
- Config: built-in turrets keep their typed sections; other mods' turrets live in `turrets.addon_turrets` (a `Map<String, TurretSetting>` keyed by id, defaults from their definition).
  `ConfigWriter`/`ConfigRanges` handle such section maps.
- Client: `OMTClientPlugin` (Fabric entrypoint `openmodularturrets_client`, NeoForge `@OMTClientPlugin.Register`) registers models via `TurretModelRegistrar`; `TurretModels.init(plugins)` collects built-in and plugin models (the loader client entrypoint passes the plugins it found).
- The game test mod registers one test turret per weapon kind through the API (`ApiTestTurrets`, with its own projectile entity `ApiTestProjectile` registered in `ApiTestEntities`) and tests registration and every custom-logic hook in `ApiTurretGameTests`.

### Aiming on arbitrary faces

- Compute the target direction in world space, then transform it into the turret's **local frame**, where the local "up" is `FACING`.
  Derive local yaw/pitch from that.
- The renderer applies the mount rotation first (rotate the model so its up-axis aligns with `FACING`), then local yaw, then local pitch.
- Put the math in one place, e.g. `TurretMath`, used by both server logic (can it aim there?) and the client renderer.
- Sync yaw/pitch (or the target entity ID) to clients for smooth interpolated rendering.
  Interpolate on the client; don't sync every tick unless necessary.

### Targeting (base)

- Run on the server tick via `BlockEntityTicker`.
  Throttle scans; do not scan entities every tick.
- Settings include target categories (hostile mobs, neutral/passive mobs, players), range (base tier + range upgrades), and any additional options.
- **Never target** the owner or trusted players.
  Respect line of sight.
- Range is affected by tier and upgrades.
  Accuracy, fire rate, efficiency and scattershot are upgrades.
  The formulas live in `core/`; don't change them without asking the user.

### Security / trusted players

- Each base stores:
  - **owner** (UUID + last known name)
  - a list of **TrustedPlayer** (UUID, name, **access level**)
- Access levels gate actions (`core.security`):
  - a low level can open the GUI / view
  - a higher level can change targeting and settings
  - the highest level can manage trusted players
  - nobody but the owner can change the owner
- **Every C2S packet must re-validate permissions on the server** (player distance to the block, access level for the specific action).
  Never trust the client.
- Breaking or opening a base also respects permissions.
- **Access profiles:** a `TrustManager` only uses the levels of its `AccessProfile` (`ALL` for bases; `TRUSTED_AND_ADMIN` for the camouflage trap, whose trusted players can stand on it and whose admins use its menus and manage the list).
  Requested, loaded and pasted levels are clamped to the profile.
  Everything that manages a trust list goes through the `TrustHost` interface.
- **Teams:** members of the owner's vanilla scoreboard team count as trusted, but only at the lowest rank, so turrets do not shoot them.
- Use UUIDs for identity; names are display-only.
  Resolve names to UUIDs server-side via the server's profile cache when adding a player.
  A cache miss is a blocking request to Mojang, so `PlayerLookup` runs it on a background thread (one lookup per player at a time) and the handler re-checks distance and permission when the result arrives.

### Power & items

- Bases store energy internally.
  Expose it through the energy API on all sides (`getEnergyAccess(side)`); expose the ammo slots, insert only (a stranger's hopper must not disarm a base), through `getItemAccess(side)` (Fabric `ItemStorage.SIDED` via `FabricBlockLookups`, NeoForge item handler capability).
- Ammo is consumed from the base inventory.
  Costs and capacities are configurable in `config/`.
- Power generation addons (solar, redstone reactor) are ticked by the base.
- **Generator** (separate block, no owner/trust): a long-running power source for places without other power infrastructure.
  Burns any furnace fuel (the loader's fuel values through the `Fuel` service; remainders like empty buckets stay in the slot) from its 9 fuel slots, using them in order.
  One fuel tick yields `generatorEnergyPerTick` (default 40 RF).
  Energy multiplier upgrades (one upgrade slot, up to 4) burn `2^n` fuel ticks per tick, so production and fuel use both scale by `2^n` while one fuel item still yields the same energy (`GeneratorLogic.multiplier`).
  It pauses while its buffer (`generatorCapacity`, default 1,000,000) is full and pushes up to `generatorMaxOutput` (default 65,536 RF/t, a flat limit) into all neighbours via Team Reborn Energy.
  GUI: 3×3 fuel grid on the left, the blue upgrade slot centred in the free area (the rest of it stays free for later slots that are not fuel slots), the flame next to the energy bar.
  Hoppers insert fuel only and take out only leftovers (empty buckets).
- **Upgrades are per machine:** `core.item.UpgradeType` is the common interface, each machine has its own enum (`TurretBaseUpgradeType`, `GeneratorUpgradeType`) and item subclass of `item.UpgradeItem` (`TurretBaseUpgradeItem`, `GeneratorUpgradeItem`).
  A machine's upgrade slots accept only its own subclass.
  A new machine gets its own enum and item subclass.
- **Config versions:** `configVersion` in the config file; `ConfigMigration` (OMLib) runs the `ConfigMigrationSteps.STEPS` newer than the file, replacing old default values (only if unchanged) when a file of an older version is loaded.
  Bump `CURRENT_VERSION` and add a step when a default changes.

### Config

- No config library.
  Write a minimal Gson-based JSON config at `config/openmodularturrets.json`.
- Load it on init, write defaults if missing, and validate ranges.
- Fields are documented with `@Comment` and range-checked with `@Range` (package `config`).
  `ConfigWriter` writes them as `//` comments (JSONC) above each key, together with the default, and `ConfigRanges` clamps on load.
  Add both annotations to every new config field.
- Server-authoritative values that clients need (e.g. for tooltips) are synced on join.

### Known performance / memory trade-offs

Deliberately not optimized yet; revisit only if profiling shows a problem.

- **Light block state properties on every base tier.** `TurretBaseBlock` declares `light_value` and `light_opacity` (16 × 16 = 256 states) for tiers 1–5, because the block states are built in the super constructor before the tier is known.
  Tiers 1–3 therefore carry 255 unused states each (765 extra `BlockState` objects, about 1–3 KB each, so roughly 1–2.5 MB of heap, plus one model-lookup entry per state on the client).
  Their values always stay at the defaults, so there is no per-tick cost.
  - Option: a separate subclass or block class for tiers 4–5 that adds the properties (the earlier `LightTurretBaseBlock` design), or a constructor-independent way to pick the properties per tier.
  - Option: store the light settings in the block entity instead of block state properties (smaller state count, but the light engine then needs a block state change anyway to update).

---

## 7. Change Workflow

1. Implement gameplay logic in `core/` (with unit tests) where possible; keep Minecraft-facing code thin.
2. Wrap version differences in `compat/`.
3. Add assets, lang entries and datagen entries; regenerate data for **both** versions.
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

Update this checklist as work progresses.

- [x] Gradle + Loom + Stonecutter skeleton; both versions build with an empty mod
- [x] `fabric.mod.json` templating, split source sets, datagen per version
- [x] `compat/` helpers (id, NBT save/load, item data, networking registration)
- [x] Config system (incl. server → client sync on join)
- [x] Core Game Logic for turret base and trusted player system (`core.security`, `core.base.TurretBaseLogic`, `core.target`, turret/ammo/upgrade/addon enums)
- [x] Turret base blocks (all tiers) + block entity (inventory, energy) — expanders skipped
- [x] Security: owner, trusted players, access levels, permission checks, trusted players screen + server-validated packets
- [x] Base GUI (inventory, energy display, targeting settings, trust management) — camo light sliders pending with camo
- [x] Networking for settings and trust changes (server-validated)
- [x] Targeting logic (core `TargetSelector`, per-turret targets in `TurretBaseLogic`; aiming/yaw-pitch limits come with the turret framework)
- [x] Turret block framework (6-face mounting, `TurretMath`, aiming sync)
- [x] Turret renderers (rotating heads on any face) — all turret models; melee turret has spinning blades (top/bottom mount only); ray/arc beams
- [x] Individual turrets + projectiles (all 14 turrets: projectile, ray and effect weapons; projectile visuals and particles)
- [x] Ammo items (items, tooltips, recipes)
- [x] Upgrades and addons (items, tooltips, recipes, base effects, fake drops loot rule)
- [x] Expanders are to be skipped
- [x] Misc blocks (crank; OMPD hardened blocks, walls, fences, camouflage trap; loot deleter and power generator skipped)
- [x] Recipes, loot tables, tags via datagen for all content
- [x] Lang, sounds, creative tab
- [x] Jade tooltips for turret bases and turrets (replaces the 1.12.2 WAILA handlers)
- [x] Camouflage for turret bases and the camouflage trap, one shared system (`core.camo`, `camo/`, `CamoMenu`, `FabricCamoModel` / `NeoForgeCamoModel`):
  - Disguise is set and reset the same way on both blocks (via gui with simple phantom slot) and rendered the same way.
  - A small camo settings GUI on both blocks (light value / light opacity sliders for tier 4+ bases and the trap), changes server-validated.
- [x] The code shared with OpenModularLighting is in the library OMLib, bundled jar-in-jar (2026-10-27, requested by the user; §4 "OMLib"). The camouflage menu, screen, packets, `CamoHost` and the camo models are part of it: `CamoBlock.overlayTintIndex()` (default -1) is the hook for quads of the block's own model on top of the disguise, `CamoData(key, valid)` takes the NBT key (`"Camo"`) and the read validation, `CamoHost.disguiseRules()` the rules (`CamoRules.RULES`), `CamoHost.hasParentMenu()` / `openParentMenu` give the screen its generic "Back" (`CamoPackets.Back`), and `CamoScreenExtras` takes the trap's Trusted and Disown buttons (`TrapCamoExtras`, registered in `OMTClient.init`). `camo` must not depend on `compat` (it would join a package cycle): hosts call `remesh()` themselves after `CamoData.read`.
- [x] Memory card (copy/paste of base and camo trap settings, shift tooltip); camo trap with trusted players (`AccessProfile.TRUSTED_AND_ADMIN`)
- [x] Playtest both versions (client + dedicated server)
- [x] Game tests for every turret (`src/gametest`, see "Game tests").
  Each turret, on a base of its tier with energy and ammo, must hit a hostile target 5 blocks in front of it.
  Inaccurate turrets get up to 10 attempts (fresh target each time); the test fails if none of them hits.
  Additionally, projectile turrets must spawn their projectile and ray turrets must trace their ray to the target.
  - Projectile turrets (projectile spawned + hit; `ProjectileTurretGameTests`, spawns observed through the loaders' entity-load events; explosion and fire damage count as hits):
    - [x] Disposable turret
    - [x] Potato cannon turret
    - [x] Crossbow turret
    - [x] Machine gun turret
    - [x] Incendiary turret
    - [x] Grenade turret
    - [x] Rocket turret
    - [x] Plasma turret
  - Ray turrets (ray traced to the target + hit; `RayTurretGameTests`, rays observed through `RayShotCallback`):
    - [x] Arc turret
    - [x] Laser turret
    - [x] Railgun turret
    - [x] Railgun ignores scattershot upgrades: with 8 of them it shoots one ray dealing `baseDamage * (armor / 20 + 0.6)` for the normal energy (`railgunIgnoresScattershot`; target at the 1024 max health cap)
  - Effect turrets (hit = effect applied; no projectile or ray):
    - [x] Slowing turret (slowness lowers the movement speed; `EffectTurretGameTests`)
    - [x] Teleporter turret (target pulled onto the turret)
    - [x] Melee turret (only reaches the 3×2×3 area around itself and its base, so it is tested with a target in reach instead of 5 blocks away)
- [x] Publishing to CurseForge / Modrinth via Gradle (`chiseledPublish`; CurseForge 224663, Modrinth `open-modular-turrets`)
- [x] Changelog system (`CHANGELOG.md`, `changelogEntry`, `releaseChangelog`; publish uploads the version's section)
- [x] Generator block (new in this version; design agreed with the user, see "Power & items")
- [x] Turret API for other mods (`api/`, `docs/turret-api.md`); built-in turrets registered through it, game test for an API turret

### NeoForge 1.21.1 (branch `neoforge-test`; follow OpenModularLighting's steps)

- [x] Build: Stonecutter nodes `<version>-<loader>`, `buildSrc` (`mod-common`, `bundled_licenses`), `build.neoforge.gradle.kts` (ModDevGradle, porting filter, data converted from the Fabric output), `neoforge.mods.toml` template, publishing for NeoForge (disabled at first, enabled by the user on 2026-10-24)
- [x] Platform services copied from OpenModularLighting: `LoaderInfo`, `Fuel`, `Network` + `ClientNetwork`, `Menus`; facades forward to them; entrypoints split (`OMTStartup.init()`, `OMTClient.init()`, `platform/<loader>` entrypoints)
- [x] Copied from OpenModularLighting: `inventory.ItemAccess` + Fabric/NeoForge adapters (in use since the item transfer step), `registry.RegistrationStep`, `client.ScreenRegistrar` (not used yet)
- [x] ArchUnit: the cycle `compat` / `compat.network` ↔ `platform` (facades call the services, the service interfaces use the facades' types) is accepted in `KNOWN_CYCLES` (decided by the user)
- [x] Energy: `core.energy.EnergyStore` (from OpenModularLighting: + `maxExtract`, `extract`, `drain`) and `EnergyAccess`, exposed by `getEnergyAccess(side)` on the base and the generator (which now uses the core store too); service `Energy` with `pushToBlock` only; adapters `FabricEnergyStorages` / `NeoForgeEnergyHandler` (replace `BaseEnergyStorage`); Fabric lookups in `platform/fabric/FabricBlockLookups`; game test for the generator push
- [x] Item transfer: turret base ammo slots and generator fuel slots through `ItemAccess` (`getItemAccess(side)`); Fabric lookups via `FabricItemStorages`; hopper game tests (`ItemTransferGameTests`); the NeoForge capabilities come with the registration step
- [x] Use `LoaderInfo` (config folder in `OMTConfig`) and `Fuel` (generator: `isFuel(stack)`, burn time, stack-aware remainder) instead of `FabricLoader` / `FuelRegistry`; game test for the lava bucket remainder (the plugin entrypoints in `TurretRegistry` / `TurretModels` stay for "Turret API plugin discovery")
- [x] Registration: `OMTStartup.init(plugins)` + `OMTStartup.registrationSteps()` (sounds, blocks, items, block entities, entity types, creative tab, menus), NeoForge per `RegisterEvent` with the common-setup check; capabilities (items, energy); shared handlers `InteractiveBlock.handleUse`, `ConfigSync.onPlayerJoin`, `KillCounter.onDeath` (NeoForge `LivingDeathEvent` at lowest priority); `VersionCompat.creativeTabBuilder()` / `fakePlayer()`; plain `c:` tags; `RayShotCallback` as a plain listener list; Fabric render data behind `CompatBlockEntity.renderData()`; `ModSounds` registers in `init()` (the turret definitions use the sounds earlier).
  OMT has no recipe conditions.
  NeoForge now compiles all of `main` except `datagen` (Fabric-only by design).
- [x] Mixins on NeoForge: the same `openmodularturrets.mixins.json` under `[[mixins]]` in `neoforge.mods.toml`; both `LivingEntityMixin` injection points exist in NeoForge's patched `LivingEntity` (checked once with `mixin.debug.export` on the NeoForge game test server)
- [x] Client: `OMTClient` lists the content (screens, model layers, block entity and entity renderers, item renderers via `ItemStackRenderer`, cutout blocks, tooltips, disconnect); `OMTFabricClient` / `OMTNeoForgeClient` register it and wire ticks, disconnect and the ray rendering; fence models carry `"render_type": "minecraft:cutout"`; camo models `FabricCamoModel` (Renderer API) / `NeoForgeCamoModel` (`ModelData` + forwarding colour provider), `CompatBlockEntity.remesh()` refreshes the model data.
  Both clients start to the title screen without errors; in-world rendering on NeoForge (turrets, projectiles, rays, item renderers, camouflage) was checked by hand by the user (2026-10-24).
- [x] Turret API plugin discovery on NeoForge: `@OMTPlugin.Register` / `@OMTClientPlugin.Register`, found in the mod scan data by `NeoForgePlugins`; `ApiTestTurrets` carries the annotation, so the NeoForge game tests will cover it; `docs/turret-api.md` explains both loaders and the sound timing on NeoForge
- [x] GameTests shared with NeoForge: `OMTGameTests` with the own empty structure, NeoForge annotations behind a condition, test mod `openmodularturrets_gametest` on both loaders (`OMTGameTestMod` + `gametest/platform/<loader>` entrypoints), `runGameTestServer` part of `check`; all 35 tests pass on NeoForge too (covers the capabilities, the FE adapter, plugin discovery and the GUI path); no `TestTransfer` needed yet
- [x] Jade on NeoForge: `@WailaPlugin` on `OMTJadePlugin` (Jade on NeoForge loads it, checked in a dev client), `compat/jade` compiles on NeoForge; Jade in the NeoForge dev runs; the dev client/server runs no longer load the test mod

### Forge 1.20.1 (branch `forge-test`; ported from OpenModularLighting's `forge-test`)

- [x] Build: node `1.20.1-forge`, `build.forge.gradle.kts` (legacy ModDevGradle, `reobfJar`, remapped Jade, mixin refmap + `MixinConfigs`, MixinExtras jar-in-jar), `forge` constant, `mods.toml` template (+ test mod), `buildSrc` taken over from OpenModularLighting (`mod-common` knows `forge`, `convertFabricData` shared by NeoForge and Forge), generated `pack.mcmeta`, publishing (disabled at first, enabled by the user on 2026-10-24)
- [x] `platform/forge`: `OMTForge`, all services, `ForgeBlockCapabilities` (items, energy of base and generator), `ForgePlugins`; client `OMTForgeClient`, `ForgeClientNetwork`, `ForgeCamoModel`, `ForgeItemRenderer`
- [x] GameTests on Forge: `OMTForgeGameTests`; all 35 pass (capabilities, FE adapter, plugin discovery, GUI path, mixin loaded)
- [x] Checked by hand (by the user, 2026-10-24) in a Forge client (`:1.20.1-forge:runClient`) and with the release jar in a real Forge install: turret, projectile, ray and item rendering, camouflage, screens, Jade, joining a dedicated server

### Minecraft 1.21.11 (Fabric + NeoForge, same codebase; started 2026-10-26; steps follow OpenModularLighting's port)

- [x] Build: version source folders in `mod-common`, `ResourceLocation` → `Identifier` and `noCollission` replacements, nodes `1.21.11-fabric` / `1.21.11-neoforge` with their `gradle.properties`, Sable/Create dependencies only where their properties exist, datagen without the client, GameTest namespace property only before 1.21.5; the nodes configure and resolve, they do not compile yet
- [x] Shared compat: `NbtCompat` (all NBT reads go through it), `CompatBlockEntity` with `ValueInput` / `ValueOutput` and `onBlockRemoved` (base and generator drop their items there), `BlockCompat` (`sidedSuccess`), `VersionCompat.hurt` / `isSingleplayerOwner` / item lists / registry lookups, `Fuel.burnTime(level, stack)`; the registration helpers (`blockProperties`, `blockEntityType`, …) come with the registration step
- [x] Registration and block callbacks: ids in the block, item, block entity and entity type registrations, `FACING` as `EnumProperty<Direction>`, `CompatEntityBlock` hooks (`onNeighborChanged`, `lightAbsorbed`, `skylightPropagates`, pick block), `BlockCompat.entityRenderShape()`, items' `use`, fence `entityInside`, `ModSounds` ids; the item model definitions (`items/<id>.json`) come with the client steps
- [x] Entities and damage: projectile entities, `TurretDamage` / `TurretDamageSource` and the explosion calls compile on 1.21.11 (renames as Stonecutter replacements, `createEntity`), `LivingEntityMixin` for `hurtServer` / `shouldDropLoot(ServerLevel)`, profile and permission helpers, `PlayerLookup` through the name cache of `MinecraftServer#services`; behaviour on 1.21.11 is only checked by the GameTests step
- [x] NeoForge transfer API: `NeoForgeEnergy`, `NeoForgeEnergyHandler`, `NeoForgeItemHandler` and `NeoForgeCapabilities` (the registration moved out of `OMTNeoForge`) exist once per version folder, `src/main/mc1.20.1-1.21.1` (`IEnergyStorage` / `IItemHandler`) and `src/main/mc1.21.11-` (`EnergyHandler` / `ResourceHandler<ItemResource>`, `Capabilities.Energy` / `Capabilities.Item`); the Fabric adapters need no change; OMT has no battery slot and no energy cluster, so the 1.21.11 `Energy` has `pushToBlock` only
- [x] Datagen: `OMTRecipeProvider` has an inner `Recipes` class with the recipes (on 1.21.11 a vanilla `RecipeProvider`, the builders need the item registry; before, stand-ins), `tagBuilder` helpers in both tag providers (`valueLookupBuilder` / `builder` on 1.21.11), output in `versions/1.21.11-fabric/src/main/generated` (committed; 201 files, only the recipe ingredient format differs from 1.21.1), converted for NeoForge as before; datagen now runs in the server environment (`client = false`). To get `main` compiling on the 1.21.11 Fabric node, `SubLevelCompat` is limited to `>=1.21 && <1.21.11` (no Sable there) and `compat/jade` is left out of the 1.21.11 nodes in the loader build scripts until the Jade split (client step); remove that exclusion then
- [x] GameTests: stand-in `@GameTest`, `GameTestCases` and the loaders' registrations (`FabricGameTestRegistration`, `NeoForgeGameTestRegistration`) in `src/gametest/mc1.21.11-`, `@GameTestHolder` conditions up to 1.21.11, tests read block entities through `helper.getLevel().getBlockEntity(helper.absolutePos(…))`, `clearContent()` instead of `Clearable.tryClear`, the test player follows `permissions()` / `isInvulnerableTo(level, source)`; all 57 pass on both 1.21.11 nodes (the NeoForge node builds without the Create movement check, the Simulated mixin and its config, and the Sable / Create / Simulated tests, in `build.neoforge.gradle.kts`). The runs need the client source set to compile, which it does not yet on 1.21.11 (client steps): I ran them with a throwaway Gradle init script that lets `compileClientJava` fail
- [x] Client, first half: screens and tooltips through `ClientCompat` (blit, tooltips, shift key, text colours with alpha, input events), Jade split (server half in main, tooltips in `client/compat/jade`; the temporary exclusion of `compat/jade` is gone), camouflage models as block state models in version folders wrapped by the entrypoints, render layers (`BlockRenderLayerMap` on 1.21.11), `ClientPacketDistributor`, item model definitions for the items without item renderer, `ClientSubLevelCompat` limited to `<1.21.11`; not checked by hand (the client does not compile on 1.21.11 before the next step)
- [x] Client, second half: the renderers, turret models and item rendering (rows in §5; the two decisions were made by the user: special model renderers for the items, version folders for the renderer classes), rays and placement outline through `ClientCompat`, world render events per loader, item model definitions for the turret and crank items (all 74 items have one); the client compiles on all nodes, `chiseledBuild` builds all six; not checked in a client (the rendering needs a look by hand: turrets and cranks in the world and as items, projectiles, rays, outline, camouflage, GUI screens, Jade)
- [x] Sable / Create / Create Aeronautics stay 1.21.1-only: checked on 2026-10-26, Modrinth lists Sable and Create Aeronautics for 1.21.1 only (Create also for 1.20.1) and the Sable maven has Companion for 1.21.1 only. The 1.21.11 nodes have no `sable_*` / `create_*` properties; `SubLevelCompat` and `ClientSubLevelCompat` are limited to `>=1.21 && <1.21.11` (every block is outside a contraption), `build.neoforge.gradle.kts` leaves out `NeoForgeCreateMovement`, the Simulated mixin and its config and the Sable / Create / Simulated GameTests there; `bases.allowContraptionMovement` exists but does nothing on 1.21.11. Check again when a new Sable release appears, and add the properties and remove the exclusions then
- [x] `chiseledBuild` on all six nodes (build, unit tests, ArchUnit, 57 game tests on each 1.21.11 node), docs (this file, `docs/architecture.md`, README) and `CHANGELOG.md`
- [ ] Checked by hand in a 1.21.11 client on Fabric and NeoForge (`runClient`): the GUI screens, turret and crank models in the world and as items (special item models; on Fabric they need the client mixin to apply), projectiles, rays, the placement outline, camouflage and Jade
- [ ] Publishing of the 1.21.11 nodes: they go out with the next release through `release.sh` (`chiseledPublish` covers all nodes; `publish_enabled=false` in a node's properties would keep it a dry run); never uploaded from here

### Ownership for adventure maps (requested by the user on 2026-10-25; designed step by step with the user)

Unowned blocks no longer drop; anyone can claim them, and map makers get a creative wand.

- [x] Core: `TrustManager.clearOwner()` / `claim(player)`, `core.world.FloodFill` (breadth-first over faces, limit)
- [x] Unowned bases stay and are inert (`bases.unownedBasesActive`, default false); turrets attach to an unowned base only for game masters (`canUseGameMasterBlocks`)
- [x] Claiming: `security.OwnedHost` (bases and camo trap through `TrustHost` defaults, passive blocks through `OwnedBlockEntity`); a main-hand right-click without sneaking on an unowned block (on a turret: its base) claims it and the connected unowned blocks (`block.ClaimSpread`, in `block` to keep `security` out of the package cycle; turrets connect; `bases.claimSpreadLimit`, default 1024, nearest first); fake players only with `general.fakePlayersCanClaim`
- [x] Visibility: green sparkle (`block.UnownedMarker`, `animateTick`) on unowned bases, their turrets, hardened blocks, walls and fences (not the camo trap); Jade owner line "nobody (right-click to claim)" and a new provider for the passive blocks
- [x] Disown: owner-only "Disown" button with `ConfirmScreen` on the configure screen and the camo trap screen (`TrustPackets.Disown`); clears owner and trust list, open menus close through `stillValid`
- [x] Ownership wand (`ownership_wand`, game masters only): right-click claims (trust list cleared), sneak-right-click resets, spreading to connected blocks of the same owner (sneak-right-click into the air toggles, default on); placeholder texture (copy of the memory card)
- [x] Wand immunity: players holding it are still targeted and fired at, but `TurretImmunity` keeps everything off them (`TurretDamage.canDamage`, so also `OMTApi.canDamage`; the effect weapon dispatch; turret explosions deal a `TurretDamageSource` that `LivingEntityMixin` cancels)
- [ ] Real texture for the ownership wand

### Sable contraptions (1.21.1 only; approach B decided by the user on 2026-10-25)

Turret bases and turrets on Sable contraptions (sub-levels: blocks stored in a far-away plot, placed in the world by a pose) must interact with the world where they are rendered.
Approach B: the core keeps working in the plot frame (`BlockEntityBaseWorld` hands it entity positions converted into the base's frame), firing works in the world frame (turret centre, muzzle and face converted out of the plot; aim directions converted back into the plot for yaw/pitch).
Projectiles inherit the contraption's velocity (decided by the user).
Sable already handles `Level.clip`, entity box searches, entities added inside a plot (moved out), `Entity#distanceToSqr`, block interaction range, sound and particle range and explosions.

- [x] Sable-independent fixes: `Clearable` on turret base and generator (Sable assembly, like `/setblock`, would otherwise duplicate their items), `rotate` / `mirror` on turret, crank and generator (`BlockMoveGameTests`)
- [x] Build: Sable Companion jar-in-jar on `1.21.1-fabric` / `1.21.1-neoforge`; Sable in the dev runs and the NeoForge game test server
- [x] `core.world.Pose` + `Quaternion` (plot ↔ world, with scale; `PoseTest`), `compat.SubLevelCompat` (`poseAt`, `velocityAt` in blocks per tick; identity on 1.20.1) and `client.compat.ClientSubLevelCompat` (`renderPoseAt`), signatures decided by the user
- [x] Targeting in the plot frame: `TargetEntity` has `getEyeX/Y/Z` instead of `getEyeHeight`, `LivingTargetEntity(entity, pose)` converts into the base's coordinates, `BlockEntityBaseWorld` filters a contraption's widened entity search by position and starts line of sight from the converted point
- [x] Turret side through `blockentity.turret.TurretMount` (`TurretBlockEntity#getMount()`, made once per tick: pose, face, world centre, world up, contraption velocity at the centre; `toWorld`, `toLocal`, `toLocalDirection`), shape and methods decided by the user: aiming (world direction → plot frame → `TurretMath`), projectiles (world centre, lead with the target's velocity relative to the contraption, contraption velocity added), rays (world origin and tip), line of sight (`BlockEntityBaseWorld.isClear(level, pose, plotFrom, worldTo, context)` leaves the turret's block in the plot frame), teleporter destination, melee victims filtered by their plot position (`AreaWeapon.area()` stays in plot coordinates); API notes in `docs/turret-api.md`
- [x] Client placement outline on contraptions (outline and label drawn with `ClientSubLevelCompat.renderPoseAt`; `ClientCompat.partialTick()`)
- [x] Game tests on a contraption (NeoForge only, `gametest/platform/neoforge/SableContraptionGameTests`): base and turret assembled with `SubLevelAssemblyHelper.assembleBlocks` and turned by 90°, resting on the world floor; a projectile and a ray turret aim at (synced aim turned into the world) and hit a world target, the teleporter pulls to the shown position, assembly keeps the base items without drops; checked to fail when the aim or teleport conversion is removed
- [x] Create contraptions may move bases and turrets (`NeoForgeCreateMovement`; Create refuses unbreakable blocks and `PushReaction.BLOCK`, and its tags can only deny), config `bases.allowContraptionMovement` (default true; Create's check gets no player, so anyone with a contraption can move a base), decided by the user; `CreateMovementGameTests`
- [x] Simulated's Physics Assembler (Create Aeronautics) ignores Create's checks and only refuses unbreakable blocks (`SimAssemblyContraption.movementAllowed`, no API): `platform/neoforge/mixin/SimAssemblyContraptionMixin`, a `@Pseudo` mixin in its own optional config `openmodularturrets.neoforge.mixins.json` (NeoForge only), for testing until Simulated offers a hook (the user asks upstream); `SimulatedAssemblyGameTests` calls the method by reflection and fails if Simulated renames it (Create Aeronautics on the NeoForge game test server)
- [ ] Checked by hand in the NeoForge dev client with Create Aeronautics (moving airship, inherited projectile velocity, placement outline)
