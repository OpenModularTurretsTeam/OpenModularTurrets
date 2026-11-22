# CLAUDE.md — OpenModularTurrets (Fabric, NeoForge; Minecraft 1.21.1)

This file guides Claude Code when working in this repository.
Read it fully before making changes.

---

## 1. Project Overview

**OpenModularTurrets (OMT)** is maintained for **Fabric and NeoForge on Minecraft 1.21.1 from a single codebase** using **Stonecutter** (its only axis here is the loader).
This is the branch `1.21.1`, split from the old `1.20.1+` branch (which built 1.20.1, 1.21.1 and 1.21.11 and is frozen until the first release from the new branches).
The sibling branches are `1.20.1` (Fabric and Forge) and `1.21.11` (Fabric and NeoForge); the 1.12.2 version is on `1.12`.
The port from 1.12.2 is complete and released (4.0.0 on 2026-11-03); this repo maintains the 1.21.1 version.
The **NeoForge** build was worked out in OpenModularLighting first (`../OpenModularLighting`; see §4 "Loaders" and §9); it has been released since 4.0.0-beta.6.
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

### OMLib (the library, `omlib/`)

Requested by the user on 2026-10-27: the code that OpenModularTurrets and OpenModularLighting used to share lives in the library OMLib (`../OMLib`, a mod of its own with the id `omlib`, package `omtteam.omlib`), which both mods bundle jar-in-jar.
Its README and `CLAUDE.md` describe the layout and the rules (no mod knowledge in the library, public API only where a mod needs it).

- **Where things are:** the platform layer (`Platform`, `LoaderInfo`, `Fuel`, `Network`, `Menus`, `Energy` and their loader classes), the compat helpers (`VersionCompat`, `NbtCompat`, `BlockCompat`, `NetworkCompat`, `MenuCompat`, `CompatBlockEntity`, `CommonTags`, `ClientCompat`), `core.energy`, `inventory.ItemAccess`, `TransferHost`, the config machinery (`@Comment`, `@Range`, `ConfigWriter`, `ConfigRanges`, `ConfigValidator`, `ConfigMigration`), `RegistrationStep`, the block base `CompatEntityBlock` with `BlockCodec`, the single right-click handling `InteractiveBlock` (with interceptors), `ConfigManager` (with the config sync) and the camouflage menu with its screen, packets and models are in the `omtteam.omlib` packages.
  Wherever the rest of this file names those classes under `omtteam.openmodularturrets`, read `omtteam.omlib`; they kept their names and package layout.
- **Build:** the settings script runs `omlib/tools/bootstrap.sh` first (inside `pluginManagement`), which builds the library for both nodes into `omlib/build/maven` when its sources changed (Loom resolves mod dependencies while it configures, so a task is too late).
  `mod-common` adds that repository and exposes `project.extra["omlibCoordinates"]` / `["omlibVersion"]`; the loader scripts declare the dependency and bundle it, and the metadata templates declare it (`${omlib_version}`).
  The settings script also drops Loom's cached remapped copy of the library when it was rebuilt from other sources.
  `OMLIB_SKIP_BUILD=1` skips the library build.
- **Runtime:** the library's services (`Platform`, `ClientPlatform`) are created on first use, so it does not matter which mod's entrypoint runs first; the library's own entrypoints register the camo menu type and packets.
  The mod passes its transfer block entity types to the registrars (`FabricBlockLookups.register(ModBlockEntities.transferTypes())`, `NeoForgeCapabilities.register(event, ModBlockEntities.transferTypes())` in its own event) and applies its own NeoForge packets in its own `RegisterPayloadHandlersEvent` (`NeoForgeNetwork.applyRegistrations(event, MOD_ID)`).
- **Security and ownership** (moved on 2026-10-27, requested by the user): `core.security` (`TrustManager`, `AccessLevel`, `AccessProfile`, `TrustedPlayer`, `PlayerInfo`, `PlayerIdentity`, `SecurityEnvironment`, `TrustResult`; `DamageRules` stayed here), `security` (`OwnedHost`, `TrustHost`, `OwnershipSettings`, `LevelSecurityEnvironment`, `PlayerAdapter`, `PlayerLookup`, `SecurityNbt`), `blockentity.OwnedBlockEntity`, the claiming classes in `block` (`ClaimLink`, `ClaimSpread`, `ClaimInteraction`, `WandInteraction`, `UnownedMarker`), `item.OwnershipWandItem`, `core.world.Face` / `BlockPosition` / `FloodFill`, the trust packets (`omlib:add_trusted_player`, `omlib:remove_trusted_player`, `omlib:change_access_level`, `omlib:disown`) and the client screens (`TrustedPlayersScreen`, `DisownConfirm`, `CompatScreen`, `OwnershipWandTooltip`, the `trusted_players.png` texture, texts under `*.omlib.*`).
  The mod supplies `OMTOwnershipSettings` (reads the config), `TurretBlock implements ClaimLink` (a turret's owner is its base, and turrets connect claims), `PassiveDefenseBlockEntity extends OwnedBlockEntity`, and `ownershipSettings()` on the base and the trap; the library registers the wand and claim interceptors itself, before the mods' own.
  The mod registers its own item of `OwnershipWandItem` (`ModItems.OWNERSHIP_WAND`) and adds its immunity line to the tooltip (`OwnershipWandImmunityTooltip`).
- **Camouflage:** `CamoBlock` (with `overlayTintIndex()`), `CamoHost` (with `disguiseRules()`) and `DisguiseRules` (this mod: `CamoRules.RULES`) are the hooks; the screen's extra buttons come from `CamoScreenExtras` (`TrapCamoExtras`, registered in `OMTClient.init`: the camouflage trap's Trusted and Disown buttons).
- **Changing the library:** edit and commit it in `../OMLib` (never inside `omlib/`), move the `omlib` pointer of both mods and build both (`./gradlew chiseledBuild`); a public signature change touches both mods.
  The OMLib commit must be pushed before the mod that points to it.

### Loaders

Each Stonecutter node is `<minecraft version>-<loader>`; the loader picks the build script (`build.<loader>.gradle.kts`).
The setup and the patterns come from OpenModularLighting, where the loader ports were done first; OMT follows them, and new cross-cutting interfaces are designed with the user (§7 "Designing APIs").

- **Whole loader-specific classes** go into a `platform/<loader>/` package (`platform/fabric/`, `platform/neoforge/`, also under `client/` and the test mod).
  `mod-common` leaves the other loader's `platform/` package out of the compilation, so these classes need no Stonecutter comments.
- **Shared code calls the loader through services:** `platform.Platform` holds the `PlatformServices` record (`LoaderInfo`, `Fuel`, `Network`, `Menus`, `Energy`), `client.platform.ClientPlatform` the `ClientPlatformServices` (`ClientNetwork`).
  OMLib creates the services on first use (`Platform`, `ClientPlatform`), so it does not matter which mod's entrypoint (`OMTFabric`, `OMTNeoForge`, `OMTFabricClient`, `OMTNeoForgeClient`) runs first.
  `NetworkCompat`, `ClientNetworkCompat` and `MenuCompat` forward to them.
- **Small differences** inside a shared class use the loader constants: `//? if fabric {` / `//? if neoforge {` (set in `stonecutter.gradle.kts`), with separate import blocks where the packages differ.
  Keep them inside `compat/` and `platform/`, never in gameplay code.
- The core (`omtteam.openmodularturrets.core`) comes from OpenModularCore, which has no Minecraft or loader on its classpath; `verifyNoCoreSources` fails if `src` has files in the core package.
- `build.neoforge.gradle.kts` compiles everything except `datagen` (Fabric-only by design: NeoForge gets the converted Fabric data, `convertFabricData`).
  New code must not use Fabric API or Team Reborn Energy outside `platform/fabric` (and `datagen`), or the NeoForge node stops compiling.
- NeoForge publishing is active (enabled by the user on 2026-10-24); `publish_enabled=false` in a node's `gradle.properties` would turn that node back into a dry run even with tokens.
- The mod version is `<mod_version>+<minecraft version>-<loader>`, so the jars of both loaders can be told apart.
  The jar files are named `<mod_id>-<mod_version>-mc<minecraft version>-<loader>.jar` (`archiveVersion` in `mod-common`); the release title on CurseForge and Modrinth is the jar name without `.jar` (`displayName`), while the mod metadata and the published version number keep the `+` version.

### Second-loader design decisions (made by the user)

These were decided step by step while porting OpenModularLighting (branch `neoforge-test`), which is the reference implementation.
Apply them to OMT the same way; do not re-open them without asking.
New questions that these do not answer go through §7 "Designing APIs".

**Overall approach**

- Native NeoForge support, not Sinytra Connector, not Forgified Fabric API, not Architectury, not a MultiLoader subproject layout.
- Stonecutter loader axis: one source set, nodes `<minecraft version>-<loader>`, one build script per loader (NeoForge here).
- ModDevGradle for the NeoForge node (build-time only).
- Whole loader-specific classes in `platform/<loader>/` (left out of the other loader's build); `//? if fabric` / `//? if neoforge` only for small differences inside `compat/` and `platform/`.
- Mod-independent build logic in `omlib/build-logic` (`mod-common`), shared between the mods as the `omlib` git submodule (OMLib, `build-logic/`); publishing for NeoForge was enabled by the user on 2026-10-24.

**Platform layer**

- Two directions: where the loader is in charge (registration, events, capabilities, client registrations), the loader entrypoint calls shared code ("wiring"); only where gameplay code must reach the loader there is a service.
- Services are small interfaces (not one big one), created by OMLib's `Platform` / `ClientPlatform` on first use (originally designed as startup injection by each entrypoint; since the library took over, nobody installs them from a mod).
- Holder: a record `PlatformServices` with every service plus a static holder `Platform` with short getters (`Platform.network()`); client: `ClientPlatformServices` / `ClientPlatform`.
  A new service is a new record component, so each loader's service set in OMLib (`Platform.loaderServices()`) stops compiling until it supplies one.
- The existing `compat/` facades (`NetworkCompat`, `ClientNetworkCompat`, `MenuCompat`) keep their signatures and forward to the services, so call sites do not change.
- `OMT` holds the shared constants (`MOD_ID`, `LOGGER`, `id()`) and nothing else; the shared start-up is `startup.OMTStartup` (moved out of `OMT` by the user on 2026-10-25, so `OMT` leaves the package cycle); the loader entrypoints are `OMTFabric` / `OMTNeoForge` (`OMTFabricClient` / `OMTNeoForgeClient`).

**Services (names and scope as decided)**

- `LoaderInfo`: `configDir()` and `isModLoaded(modId)`; named implementation classes per loader.
- `Fuel`: `burnTime(stack)` and `remainder(stack)` (the loader's stack-aware remainder hook, like a furnace).
- `Network` / `ClientNetwork`: NeoForge registrations are recorded and applied in `RegisterPayloadHandlersEvent` (later registration throws); a server-to-client packet without a client handler is logged and dropped; channels are `optional()`; handlers run on the main thread.
- `Menus`: `createBlockMenuType(factory)` / `openBlockMenu(player, title, pos, factory)`; opening data is a `BlockPos` only.
- `Energy`: `isBattery(stack)`, `dischargeItem(container, slot, target, maxAmount)`, `pushToBlock(level, pos, side, source, maxAmount)` (side = face of the target block; the generator needs the push).
  These services are in OMLib.
  OMT has no battery slot yet, but the battery methods are there (decided by the user on 2026-10-26, so that both mods use the same classes) for when one is added.
  `core.energy.EnergyGroup` is an `EnergyAccess` that spreads an insert over several stores (`fill(maxAmount, StoreInserter)`).
  OMT has no group, OpenModularLighting's panel light cluster is one.

**Energy and items**

- Energy approach: one loader-neutral buffer plus thin adapters per loader (not Team Reborn Energy as the internal model, not a buffer per loader).
- The buffer is `core.energy.EnergyStore` (OMT's `core.base.EnergyStore`, extended in OpenModularLighting): `final`; `insert` / `extract` (limited by `maxInsert` / `maxExtract`) for other blocks, `generate` / `consume` / `drain` for the owner, `setAmountUnchecked` for loading and rollback.
  `onChange` may fire for a Fabric transaction that is rolled back later (an extra `setChanged()` is accepted).
- Block entities expose `@Nullable EnergyAccess getEnergyAccess(side)` (core interface; a store or a view over several) and `getItemAccess(side)` by implementing `blockentity.TransferHost`; `ModBlockEntities.transferTypes()` lists them, and the loader registrations (`FabricBlockLookups`, `NeoForgeCapabilities`) iterate over that list, so a new transfer block entity needs no loader code.
- Fabric adapter: one `SnapshotParticipant` per store, cached in `platform/fabric` with weak keys **and weak values** (a plain `WeakHashMap` would leak, because the adapter references its store).
  NeoForge adapter: `IEnergyStorage`, clamped to `int`; 1 E = 1 FE.
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
  It is registered for this mod's camouflage blocks only (`OMTClient.camoBlocks()`, NeoForge): `CamoModels.camoBlocks()` lists every `CamoBlock` in the registry, and a block takes only one colour provider, so registering it for Open Modular Lighting's floodlights took away their dye colour (fixed on 2026-11-02).

**Data**

- No NeoForge datagen: the NeoForge build converts the Fabric output of `datagen_from` (`fabric:load_conditions` → `neoforge:conditions`, `condition` → `type`); only conditions in the mod's own namespace convert, anything else fails the build.

**GameTests**

- Shared test classes for both loaders: they implement a shared interface (e.g. `OMTGameTests`, like OpenModularLighting's `OMLGameTests`) instead of `FabricGameTest`, use the mod's own empty 8 × 8 × 8 structure (`structure/`), and carry `@GameTestHolder(MOD_ID)` + `@PrefixGameTestTemplate(false)` behind `//? if neoforge`.
- `TestTransfer` with `hasEnergy`, `insertEnergy`, `insertItem`, `extractItem` (Fabric lookups / NeoForge capabilities) replaces direct Fabric lookups in tests.
  OMT's tests have no direct lookups (items go through vanilla hoppers, energy through the generator push), so OMT has no `TestTransfer` yet; add it when a test needs one.
- The test battery keeps its energy in the item's custom data, with loader-specific energy access (OMT has no battery).
- Test mod id with an underscore on both loaders (`openmodularturrets_gametest`); NeoForge mod ids cannot contain hyphens.
- No test class may sit in a main-mod package (NeoForge modules cannot share packages); reach protected methods through public vanilla API.

**Jade**

- `@WailaPlugin` on the plugin without a loader condition (NeoForge finds it by the annotation, Fabric keeps the `jade` entrypoint).
- The plugin is split in two, so that no client class is loaded on a dedicated server: `compat.jade.OMTJadePlugin` and the providers (server data, and `lines(…)` that turn the data into tooltip lines) stay in main, `client.compat.jade.OMTJadeClientPlugin` and `JadeLinesComponent` (the tooltip) are in the client source set.
  Only `OMTJadePlugin` is registered with Jade (`jade` entrypoint, `@WailaPlugin`): Jade creates every plugin it finds on a dedicated server too, where the client class cannot be loaded (Fabric refuses it).
  Its `registerClient`, which Jade calls only on a client, creates `OMTJadeClientPlugin` by name and forwards to it (fixed on 2026-10-27: before, dedicated servers with Jade logged errors).
- Jade stays off the NeoForge GameTest server (it sends a packet to the vanilla mock players, which NeoForge refuses).
  It is in the NeoForge dev runs (`localRuntime`), which load only the mod itself, not the test mod (`loadedMods`).

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

### Core Game Logic Source

The game logic of the mod is plain Java without Minecraft imports, in the package `omtteam.openmodularturrets.core`.
Since 2026-11-18 it is not in this repository: it is the module `omt-core` of OpenModularCore (`../../OpenModularCore`, published on JitPack as `com.github.OpenModularTurretsTeam.OpenModularCore:omt-core`), shared with the 1.12.2 version, so the rules cannot drift between Minecraft versions (see the root `CLAUDE.md`, "Shared core logic").
The logic consists of:

1. The turret base behaviour (targeting, ammo handling, upgrade/addon handling, the sort order of the ammo slots `core.storage.AmmoSort`) and the turret's own rules (`TurretState`: fire cooldown, concealer cycle; `RayMath`, `ProjectileMath`: damage formulas)
2. Who turret damage may hit (`DamageRules`); the trusted player permission system, the machine mode, the targeting settings, the camouflage permissions, the memory card data and its trust list copy rules are OMLib's core (`omlib-core`: `core.security`, `core.machine`, `core.camo`, `core.card`); the memory card's copy rules for a base's settings are `core.card.BaseCardRules` here
3. an abstract representation of a target entity
4. The generator's burning rules (`core.generator.GeneratorLogic`)
5. Unit Tests to test its behaviour (in OpenModularCore, run there on Java 17 and on Java 8).

How the mod uses it (`mod-common` in `omlib/build-logic`, the same in every mod):

- `gradle.properties` names `core_module=omt-core` and `core_version` (the same in every mod and every workspace; raised together, see the root `CLAUDE.md`).
- The module is a compile-only dependency; `extractCore` unpacks its `omtteam.openmodularturrets.core` classes into a classes folder of the main source set, so every jar bundles them (Loom `remapJar`, ModDevGradle `jar` and the dev jar) and the dev runs, game tests and unit tests load them as part of the mod on both loaders.
  OMLib's core comes with OMLib's jar (OMLib declares it `compileOnlyApi`).
- `verifyNoCoreSources` (part of `check`) fails if `src` has files in the core package: a rule change goes to OpenModularCore, never into a copy here.
- The core's isolation, its package cycles, coverage and mutation testing are checked in OpenModularCore.
- **Local core:** when `../../OpenModularCore` exists, `settings.gradle.kts` includes it, so the build (and the `omlib/` bootstrap) uses its source instead of the JitPack artifact and a core edit is live without a release; `-Pomm.noLocalCore` or `OMM_NO_LOCAL_CORE=1` turns that off, as on CI (root `CLAUDE.md`, "Local development").
- Game-layer wiring decided with the reconciliation (2026-11-18): `TurretBaseBlockEntity` builds the base's `TrustManager` (`LevelSecurityEnvironment`, change callback `onLogicChanged(true)`) and passes it to `TurretBaseLogic`; the logic's randomness is `level.random`'s `nextLong`; the machine mode's language key is built in the game layer (`"gui.openmodularturrets.mode." + getSerializedName()`, Jade provider, base screen, memory card tooltip); `TurretBaseInventory.sortAmmo` uses `AmmoSort` with an empty variant name (every item has its own registry name, so items of one kind keep their order).

`docs/architecture.md` is the human-oriented guide to the structure (layers, flows, how to add things, known weak spots); keep it in sync when the structure changes.
`ArchitectureTest` (`src/test/.../architecture`, ArchUnit) checks that main never uses client code, and package cycles against its `KNOWN_CYCLES` list (a ratchet: update the list when a cycle shrinks; never add to it without asking the user); it leaves the bundled core classes out.
PMD (`gradle/pmd/ruleset.xml`, report only, active version) measures complexity and size: `versions/1.21.1-fabric/build/reports/pmd/`.
JaCoCo records coverage in the unit tests and on the game test server; `check` prints the line coverage of `api` per package (active version only).
This repository has no PIT task since the core moved to OpenModularCore (its mutation testing runs there).

`./gradlew check` also runs `checkRecipes` (from `mod-common`): every item (item model) of the mod needs a recipe on that node, unless `recipe-exempt.txt` lists it as `<id> # <reason>`, and every recipe result of the mod's namespace must be one of its items.
Tests live in `src/test/java` (JUnit, test-only dependency); they run on the Fabric node.

### Game tests

In-game behaviour (turrets firing, effects on entities) is tested with GameTests in `src/gametest`, a test mod (`openmodularturrets_gametest`, never shipped) whose test classes are shared by both loaders.
Classes implement `OMTGameTests` and use its `EMPTY_STRUCTURE` (the mod's own 8×8×8 air structure in `data/openmodularturrets/structure/`) with the scene built in code.
On Fabric (Loom `fabricApi.configureTests`) they are listed under the `fabric-gametest` entrypoint in `src/gametest/resources/fabric.mod.json`; on NeoForge they carry `@GameTestHolder(OMT.MOD_ID)` and `@PrefixGameTestTemplate(false)` behind `//? if neoforge` (the gametest source set is a second mod, `META-INF/neoforge.mods.toml`).
The test mod's loader entrypoints (`gametest/platform/<loader>`) run its registration steps (`OMTGameTestMod`) and forward entity loads to `ProjectileTurretGameTests.onEntityLoad`.
`check` (and so `build` / `chiseledBuild`) runs them on a dedicated test server: `runGameTest` on Fabric (`versions/<node>/build/run/gameTest`, the world is deleted before every run), `runGameTestServer` on NeoForge (`check` also requires the "All N required tests passed" line in its `run/logs/latest.log`).
The test server turns mob spawning and the weather cycle off for the whole run (vanilla's `GameTestServer`, which both loaders start), so no mob spawns around the tests and it never rains.
The day still passes: a test that needs daylight sets the time itself (`SolarPanelGameTests`).
1.21.11's test server keeps the default game rules, so that branch sets these rules in every batch's test environment (`GameTestCases.batchEnvironment`); this branch needs nothing of the kind (checked 2026-11-22).

- `TurretScene` builds a ready-to-fire base + turret (owner, always on, full energy, max range) and spawns targets (husks without AI).
  A base placed with `setBlock` has no owner: it stays, but does not fire until it has one (`bases.unownedBasesActive`).
- `TurretScene` also loads the turret's ammo into the base (energy alone is not enough for the railgun and the projectile turrets).
- Turrets that scatter get up to 10 attempts, each with a fresh target (`TargetAttempts`); a miss replaces the target, a hit passes.
  A ray attempt ends with its ray, a projectile attempt when its first projectile is gone.
  The test server does not tick entities in the chunks next to a test, and projectiles remove themselves before flying into such a chunk, so `TurretScene` force-loads the chunks around the scene while a test runs (otherwise a grenade that lands just past the target would vanish instead of exploding).
  Projectile tests fail just before their timeout with their state (shots, followed projectile, target) instead of timing out silently.
- Turret ranges reach into neighbouring tests, so every turret test gets its own `batch` and ends with `TurretScene.succeedWhen`, which removes the turret, base and entities once it passed.
- `BaseGuiGameTests` clicks a base through `ServerPlayerGameMode#useItemOn` (the real server path, including the loader's block-use event) and checks that the menu opens for the owner only.
  The loaders' `FakePlayer`s refuse to open menus, so the test uses `GuiTestPlayer`, a small subclass that opens them without sending a packet (NeoForge opens block menus through the two-argument `openMenu`, which it overrides too); `CamoMenuGameTests` uses it for the camouflage menu and its "Back" to the base GUI.
- `TrustListGameTests`, `CamoMenuGameTests` and the light tests in `CamoGameTests` call OMLib's packet handlers (`TrustPacketHandler`, `CamoPacketHandler`) directly with fake players placed next to the block, as the server does on a packet, and check permission, reach and the access level steps.
  Fake players are not on the server's player list, so the test that adds an online player by name puts one there for the (synchronous) request only (`TrustListGameTests.online`), on Fabric only: NeoForge returns a read-only view of the list, and vanilla's mock players would log in for real (on the NeoForge test server Simulated sends them a payload that NeoForge refuses, which fails the test).
  Names of players who are not online go to the server's name cache, which the test server does not have (not found).
  OMLib's `ConfigSync` is not covered: it only sends to clients that negotiated its channel, and the test server has none.

---

## 5. Compat helpers (the porting layer)

The helpers in OMLib's `compat` packages are the porting layer between the version branches: the game layer is ported between branches by hand, and the helpers keep the call sites the same on every branch.
Use them instead of new `//? if` blocks:

| Difference | Helper |
| --- | --- |
| Reading NBT | `compat.nbt.NbtCompat`: `getInt` / `getLong` / `getFloat` / `getDouble` / `getBoolean` / `getString` (with a fallback), `getCompound` (also of a `ListTag` index), `getCompoundList`, `getIntArray` / `getLongArray`, `hasCompound` / `hasList` / `hasString` / `hasNumber`, `getUuid` (null if missing) / `putUuid`, `readBlockState`; never call the `CompoundTag` getters or `Tag.TAG_*` directly. It depends on Minecraft only, so packages outside the `compat` cycle may use it |
| BlockEntity NBT | `CompatBlockEntity` keeps `readData` / `writeData` with a `CompoundTag` (and the registries), so the saved format stays the same on every branch |
| Block broken or replaced | override `CompatBlockEntity.onBlockRemoved(Level)` (server only, before the block entity is removed; the turret base and the generator drop their items there); the block's `onRemove` calls `CompatBlockEntity.handleRemove` |
| Block state queries, interaction result | `compat.block.BlockCompat.isSolidRender` / `lightBlock`, `sidedSuccess` |
| Damaging entities, singleplayer host | `VersionCompat.hurt(entity, source, amount)`, `VersionCompat.isSingleplayerOwner(player)`; registry lookups through `registryAccess().lookupOrThrow(registry).getOrThrow(key)` |
| Stack and slot lists | `VersionCompat.saveItems` / `loadItems` |
| Registering blocks, items, block entity types, entity types | create blocks and items from their properties through `VersionCompat.blockProperties` / `itemProperties` / `blockItemProperties(properties, id)` (block items keep the `block.` name; `ModBlocks` / `ModItems` take a factory and the properties), block entity types with `VersionCompat.blockEntityType(factory, blocks…)`, entity types with `VersionCompat.buildEntityType(builder, id)`. Also in the test mod |
| Neighbour updates, light absorption, pick block (blocks extending `CompatEntityBlock`) | override `onNeighborChanged(state, level, pos, neighbourPos)` (the position may be `null`), `lightAbsorbed(state, vanilla)` / `skylightPropagates(state, vanilla)` and `pickItem`; never `neighborChanged`, `getLightBlock` or `getCloneItemStack` directly |
| Block facing, render shape | `EnumProperty<Direction>` for `FACING`, `BlockCompat.entityRenderShape()` |
| Creating entities, owners, daylight, per-player sounds | `VersionCompat.createEntity(type, level)`, `ownerUuid(ownable)`, `isDay(level)`, `playNotifySound(player, sound, source, volume, pitch)`; the server of an entity or player is `level().getServer()`, a player's level is `(ServerLevel) player.level()` |
| Profiles and operators | `VersionCompat.profileName` / `profileId`, `cachedProfile(server, name)` (blocking for unknown names, call it off the server thread), `cachedName(server, uuid)`, `isOwnerOperator(player)` |
| Mixins on `LivingEntity` | `LivingEntityMixin` hooks `hurt` (one `HURT` constant) and `dropAllDeathLoot` / `shouldDropLoot` |
| Client: GUI drawing, tooltips, shift key | `client.compat.ClientCompat`: `blit` (256 × 256 GUI textures), `renderTooltip` / `renderComponentTooltip`, `hasShiftDown()`, `partialTick()`; the mouse wheel goes through `CompatScreen#onScroll` |
| Camouflage models, render layers | baked models: `FabricCamoModel` (`ForwardingBakedModel`) and `NeoForgeCamoModel` (`BakedModelWrapper`), which the loader client entrypoints wrap around the models by model id; Fabric render layers through `BlockRenderLayerMap`, NeoForge through `"render_type"` in the model JSON |
| Block entity and entity renderers, turret models | `BlockEntityRendererRegistrar`, `TurretRenderer`, `LeverRenderer`, `BillboardRenderer`, `ArrowShapedRenderer`, `PlasmaRenderer` and `TurretModel` are plain classes in `src/client/java`; `EntityRendererRegistrar` is shared; `OMTClient` and the entrypoints call the same methods on every branch |
| Turret and crank items (item renderers) | `ItemStackRenderer` (`TurretItemRenderer`, `LeverItemRenderer`), registered by `OMTClient.registerItemRenderers` (Fabric `BuiltinItemRendererRegistry`, NeoForge `getCustomRenderer` through `NeoForgeItemRenderer`) |
| World-space effects (rays, placement outline) | `ClientCompat.lines()` / `lightning()` / `renderLineBox`; Fabric draws them in `WorldRenderEvents.AFTER_TRANSLUCENT` (the label in `LAST`), NeoForge in `RenderLevelStageEvent` (`AFTER_TRANSLUCENT_BLOCKS`, the label in `AFTER_PARTICLES`) |
| Item models | every item has an item model `assets/openmodularturrets/models/item/<id>.json`; the item model definitions `assets/<namespace>/items/` exist only on the `1.21.11` branch |
| Furnace fuel | `Fuel.burnTime(level, stack)`, `GeneratorBlockEntity.isFuel(level, stack)` |

### Resources strategy

- **`assets/`** (textures, models, blockstates, lang, sounds): shared by both loaders.
  Keep them handwritten in shared `src/main/resources/assets/openmodularturrets/`.
- **`data/`** (recipes, loot tables, tags, advancements): folder names and JSON formats are those of this Minecraft version.
  **Generate them with Fabric Datagen.**
  - Run datagen on the Fabric node (`:1.21.1-fabric:runDatagen`); the NeoForge node converts the output.
  - Configure the output directory relative to the node's project (`project.file("src/main/generated")` → `versions/<node>/src/main/generated`).
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
│   └── weapon/                  # OMTApi lookups; weapon kinds (Projectile/Ray/Effect/Area/SelfTargetingWeapon)
├── registry/                    # ModBlocks, ModItems, ModBlockEntities, ModEntities, ModMenus, ModSounds,
│                                # ModCreativeTabs, ModDamageTypes; TurretRegistry (definitions, plugin loading), BuiltinTurrets
├── block/                       # MemoryCardInteraction, ContraptionMovement
│   ├── base/                    # TurretBaseBlock (tiers 1–5)
│   ├── turret/                  # TurretBlock (one class for every turret type, built-in and API)
│   ├── defense/                 # hardened blocks, walls, fences, camouflage trap (former OMPD)
│   └── misc/                    # crank (LeverBlock), generator, inventory expanders
├── blockentity/
│   ├── TurretBaseBlockEntity    # forwards to core.base.TurretBaseLogic; inventory, energy, trust list
│   ├── base/                    # adapters to core (BlockEntityBaseWorld, ContainerItemSlot, LivingTargetEntity)
│   ├── turret/                  # TurretBlockEntity (one type for all turrets), TurretMount, RayWeapons, EffectWeapons
│   ├── defense/                 # PassiveDefenseBlockEntity, CamoTrapBlockEntity
│   └── misc/                    # GeneratorBlockEntity, LeverBlockEntity
├── core/                        # (from OpenModularCore, omt-core; bundled, not in src/) game rules without Minecraft:
│                                # base, turret, target, security (DamageRules), generator, card, item, settings, storage, world
├── camo/                        # CamoRules, CamoLightProperties (CamoHost, CamoData and the menu are in OMLib)
├── security/                    # OMTOwnershipSettings (trust lists, access levels and claiming are in OMLib)
├── inventory/                   # TurretBaseInventory, FuelContainer, AmmoRules
├── item/                        # ammo, upgrades (per machine), addons, memory card
├── entity/projectile/           # projectiles, TurretDamage, TurretImmunity, KillCounter
├── menu/                        # TurretBaseMenu, GeneratorMenu
├── network/                     # OMTPackets, BasePackets + BasePacketHandler (C2S), RayPacket (S2C)
├── config/                      # self-written config (JSON via Gson, OMLib's machinery), no library
├── compat/                      # SubLevelCompat (Sable), jade/ (server half); the porting helpers are in OMLib
├── mixin/                       # LivingEntityMixin
├── platform/                    # fabric/, neoforge/: entrypoints, services, adapters (each built for its loader only)
└── datagen/                     # recipes, loot tables, tags, damage types (Fabric only)

(client source set)
omtteam.openmodularturrets.client
├── OMTClient.java               # shared client start-up (loader entrypoints in client/platform/<loader>)
├── api/                         # OMTClientPlugin entrypoint, TurretModelRegistrar
├── model/                       # Java entity-style turret models (TurretModel)
├── render/                      # turret and crank renderers, projectile renderers, item renderers,
│                                # rays (effect/RayRenderer, also the ray packet), placement outline
├── screen/                      # base GUI, configure screen, generator screen
├── tooltip/                     # item tooltips
├── compat/                      # ClientSubLevelCompat, jade/ (tooltips)
└── platform/                    # fabric/, neoforge/: client entrypoints, item renderers (the camo models are in OMLib)
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
- `OMTPlugin` (Fabric entrypoint `openmodularturrets`, NeoForge `@OMTPlugin.Register`) registers `TurretDefinition`s (type + `TurretWeapon` + default `TurretStats`, optionally an `AmbientEffect` for client particles through `TurretDefinition.builder`) and `AmmoDefinition`s.
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
  The formulas live in the core (OpenModularCore); don't change them without asking the user.

### Security / trusted players

- Each base stores:
  - **owner** (UUID + last known name)
  - a list of **TrustedPlayer** (UUID, name, **access level**)
- Access levels gate actions (OMLib's `core.security`):
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
  It pauses while its buffer (`generatorCapacity`, default 1,000,000) is full and pushes up to `generatorMaxOutput` (default 65,536 RF/t, a flat limit) into all neighbours through the `Energy` service (Team Reborn Energy on Fabric, FE on NeoForge).
  GUI: 3×3 fuel grid on the left, the blue upgrade slot centred in the free area (the rest of it stays free for later slots that are not fuel slots), the flame next to the energy bar.
  Hoppers insert fuel only and take out only leftovers (empty buckets).
- **Inventory expanders** (`InventoryExpanderBlock`, tiers 1–3, no block entity; decided with the user on 2026-10-27): they have no inventory, but every ammo slot of a turret base and fuel slot of a generator they reach holds `itemMaxStackSize × base stacks × (1 + Σ stacks)` (`core.storage.SlotCapacity`; `expanders.tier_N.stacks`, defaults 1/2/3; the base stacks are `bases.tier_N.baseAmmoStacks`, defaults 1/1/1/2/2, tiers 4 and 5 doubled with and without expanders as requested by the user on 2026-10-27, 1 on a generator).
  The base item's tooltip shows its max stack size (base stacks × 64, without expanders), the info panel left of the base GUI the current one (with expanders, from the synced menu data, `TurretBaseMenu.getSlotCapacity`).
  They are placed only against a turret base or generator (`isMachine`), and only the block next to a face counts: up to 2 plates as one double block (two of one tier in one block space, like slabs, `DOUBLE`: the second plate stacks onto the first, as a plate is less than half a block deep; decided by the user on 2026-10-27: same tier only, one block of 2 plates per face, a block behind it does not count); an expander counts for every machine next to it; fuel with a remainder (lava buckets) is not enlarged.
  The slots are OMLib large stacks (`LargeStackContainer`, `LargeStackMenu`, `LargeStackSlot`, `LargeStackScreen`): the cursor and the player's inventory only ever get normal stacks out of them, `VersionCompat.saveItems` saves the counts.
  Hardness and blast resistance come from the config while the game runs (defaults of base tiers 1, 3 and 5); models and textures are those of the 1.12.2 inventory expanders of tiers 1, 3 and 5.
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

1. Implement gameplay logic in the core where possible: in OpenModularCore (`../../OpenModularCore`, with unit tests there; see the root `CLAUDE.md`, "Changing the core"), never as sources in this repository; keep Minecraft-facing code thin.
2. Wrap loader differences in `compat/` or `platform/`, and Minecraft API differences between the version branches in the OMLib compat helpers.
3. Add assets, lang entries and datagen entries; regenerate data on the Fabric node (`:1.21.1-fabric:runDatagen`).
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
- [x] Turret base blocks (all tiers) + block entity (inventory, energy) — the old expanders skipped
- [x] Security: owner, trusted players, access levels, permission checks, trusted players screen + server-validated packets
- [x] Base GUI (inventory, energy display, targeting settings, trust management) — camo light sliders pending with camo
- [x] "Sort Ammo" button in the base GUI (requested by the user on 2026-10-27; right column below the admin buttons, for everyone who opens the GUI, `BasePackets.Action.SORT_AMMO`): `TurretBaseInventory.sortAmmo` merges equal ammo up to the slot limit and orders Minecraft items, then OMT ammo, then other mods' items, each by registry name (since 2026-11-18 through the core's `AmmoSort`); `BaseGuiGameTests.sortButtonSortsTheAmmo`, `AmmoSortGameTests` (the order, the slot limit, and 2000 random fillings compared with the sort of 4.0.0)
- [x] Networking for settings and trust changes (server-validated)
- [x] Targeting logic (core `TargetSelector`, per-turret targets in `TurretBaseLogic`; aiming/yaw-pitch limits come with the turret framework)
- [x] Turret block framework (6-face mounting, `TurretMath`, aiming sync)
- [x] Turret renderers (rotating heads on any face) — all turret models; melee turret has spinning blades (top/bottom mount only); ray/arc beams
- [x] Individual turrets + projectiles (all 14 turrets: projectile, ray and effect weapons; projectile visuals and particles)
- [x] Ammo items (items, tooltips, recipes)
- [x] Upgrades and addons (items, tooltips, recipes, base effects, fake drops loot rule)
- [x] The old expanders (own inventory or energy) are skipped
- [x] Inventory expanders without an inventory (2026-10-27, designed with the user): 3 tiers enlarge the ammo slots of bases and the fuel slots of generators (`SlotCapacity`, OMLib's large stacks), game tests `ExpanderGameTests`; not checked in a client yet (count drawing, tooltip, model)
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
- [x] Publishing to CurseForge / Modrinth via Gradle (`chiseledPublish`; CurseForge 224663, Modrinth `GEFsJ5uX`)
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

### Sable contraptions (approach B decided by the user on 2026-10-25)

Turret bases and turrets on Sable contraptions (sub-levels: blocks stored in a far-away plot, placed in the world by a pose) must interact with the world where they are rendered.
Approach B: the core keeps working in the plot frame (`BlockEntityBaseWorld` hands it entity positions converted into the base's frame), firing works in the world frame (turret centre, muzzle and face converted out of the plot; aim directions converted back into the plot for yaw/pitch).
Projectiles inherit the contraption's velocity (decided by the user).
Sable already handles `Level.clip`, entity box searches, entities added inside a plot (moved out), `Entity#distanceToSqr`, block interaction range, sound and particle range and explosions.

- [x] Sable-independent fixes: `Clearable` on turret base and generator (Sable assembly, like `/setblock`, would otherwise duplicate their items), `rotate` / `mirror` on turret, crank and generator (`BlockMoveGameTests`)
- [x] Build: Sable Companion jar-in-jar on `1.21.1-fabric` / `1.21.1-neoforge`; Sable in the dev runs and the NeoForge game test server
- [x] `core.world.Pose` + `Quaternion` (plot ↔ world, with scale; `PoseTest`), `compat.SubLevelCompat` (`poseAt`, `velocityAt` in blocks per tick) and `client.compat.ClientSubLevelCompat` (`renderPoseAt`), signatures decided by the user
- [x] Targeting in the plot frame: `TargetEntity` has `getEyeX/Y/Z` instead of `getEyeHeight`, `LivingTargetEntity(entity, pose)` converts into the base's coordinates, `BlockEntityBaseWorld` filters a contraption's widened entity search by position and starts line of sight from the converted point
- [x] Turret side through `blockentity.turret.TurretMount` (`TurretBlockEntity#getMount()`, made once per tick: pose, face, world centre, world up, contraption velocity at the centre; `toWorld`, `toLocal`, `toLocalDirection`), shape and methods decided by the user: aiming (world direction → plot frame → `TurretMath`), projectiles (world centre, lead with the target's velocity relative to the contraption, contraption velocity added), rays (world origin and tip), line of sight (`BlockEntityBaseWorld.isClear(level, pose, plotFrom, worldTo, context)` leaves the turret's block in the plot frame), teleporter destination, melee victims filtered by their plot position (`AreaWeapon.area()` stays in plot coordinates); API notes in `docs/turret-api.md`
- [x] Client placement outline on contraptions (outline and label drawn with `ClientSubLevelCompat.renderPoseAt`; `ClientCompat.partialTick()`)
- [x] Game tests on a contraption (NeoForge only, `gametest/platform/neoforge/SableContraptionGameTests`): base and turret assembled with `SubLevelAssemblyHelper.assembleBlocks` and turned by 90°, resting on the world floor; a projectile and a ray turret aim at (synced aim turned into the world) and hit a world target, the teleporter pulls to the shown position, assembly keeps the base items without drops; checked to fail when the aim or teleport conversion is removed
- [x] Create contraptions may move bases and turrets (`NeoForgeCreateMovement`; Create refuses unbreakable blocks and `PushReaction.BLOCK`, and its tags can only deny), config `bases.allowContraptionMovement` (default true; Create's check gets no player, so anyone with a contraption can move a base), decided by the user; `CreateMovementGameTests`
- [x] Simulated's Physics Assembler (Create Aeronautics) ignores Create's checks and only refuses unbreakable blocks (`SimAssemblyContraption.movementAllowed`, no API): `platform/neoforge/mixin/SimAssemblyContraptionMixin`, a `@Pseudo` mixin in its own optional config `openmodularturrets.neoforge.mixins.json` (NeoForge only), for testing until Simulated offers a hook (the user asks upstream); `SimulatedAssemblyGameTests` calls the method by reflection and fails if Simulated renames it (Create Aeronautics on the NeoForge game test server)
- [ ] Checked by hand in the NeoForge dev client with Create Aeronautics (moving airship, inherited projectile velocity, placement outline)
