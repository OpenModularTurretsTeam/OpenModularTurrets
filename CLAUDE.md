# CLAUDE.md — OpenModularTurrets (Fabric, Multi-Version)

This file guides Claude Code when working in this repository.
Read it fully before making changes.

---

## 1. Project Overview

**OpenModularTurrets (OMT)** is being maintained on **Fabric**, targeting **1.20.1 and 1.21.1 from a single codebase** using **Stonecutter**.
The port from 1.12.2 is complete; this repo maintains the 1.20.1 / 1.21.1 versions.
A **NeoForge 1.21.1** build is being added on the branch `neoforge-test`, with the setup worked out in OpenModularLighting (`../OpenModularLighting`, branch `neoforge-test`; see §4 "Loaders" and §9).
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
- **Jade** (optional integration, `compat/jade/`, registered through the `jade` entrypoint).
  Compile-only (`modCompileOnly`, Modrinth maven), never bundled and never required at runtime.
- **NeoForge** 21.1.x (1.21.1 only), built with **ModDevGradle** (approved by the user; build-time only).
  On NeoForge, the Fabric-only APIs above are replaced by NeoForge's own (capabilities, events, payload registration); see §4 "Loaders".
- Nothing else without asking the user first.
  No Architectury, no Cloth Config, no OMLib, no Kotlin.

---

## 2. Tech Stack

| | 1.20.1 (Fabric) | 1.21.1 (Fabric) | 1.21.1 (NeoForge) |
| --- | --- | --- | --- |
| Java | 17 | 21 | 21 |
| Loader | Fabric Loader 0.19.x | Fabric Loader 0.19.x | NeoForge 21.1.x |
| API | Fabric API `0.92.x+1.20.1` | Fabric API `0.116.x+1.21.1` | – |
| Gradle plugin | Loom 1.18 | Loom 1.18 | ModDevGradle 2.0 |
| Mappings | Mojang official (+ Parchment optional) | Mojang official (+ Parchment optional) | Mojang official (NeoForge runtime names) |

- Build system: Gradle 9.8 (Kotlin DSL) + **Stonecutter 0.9**, with Fabric Loom 1.18 (`net.fabricmc.fabric-loom-remap`, the variant for obfuscated MC versions) on the Fabric nodes and ModDevGradle 2.0 (`net.neoforged.moddev`) on the NeoForge node.
- Shared build logic is the convention plugin `buildSrc/src/main/kotlin/mod-common.gradle.kts` (Java toolchain, metadata templating, JUnit, `verifyCoreIsolation`, licenses in the jar via `bundled_licenses`, publishing).
  It is mod-independent and should stay identical to OpenModularLighting's copy.
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
├── build.neoforge.gradle.kts   # NeoForge node (ModDevGradle; temporary "ported packages" filter)
├── buildSrc/                   # mod-common convention plugin: build logic shared by all loaders
├── stonecutter.gradle.kts      # Stonecutter controller (active version, loader constants, chiseled tasks)
├── settings.gradle.kts         # declares the Stonecutter nodes (<minecraft version>-<loader>)
├── gradle.properties           # shared properties (mod_id, mod_version, maven_group …)
├── versions/
│   ├── 1.20.1-fabric/
│   │   ├── gradle.properties   # minecraft_version, fabric_api, java_version=17 …
│   │   └── src/main/generated/ # datagen output for 1.20.1 (committed)
│   ├── 1.21.1-fabric/
│   │   ├── gradle.properties   # minecraft_version, fabric_api, java_version=21 …
│   │   └── src/main/generated/ # datagen output for 1.21.1 (committed; NeoForge converts it, datagen_from)
│   └── 1.21.1-neoforge/
│       └── gradle.properties   # neoforge_version, minecraft_dependency (Maven range), publish_enabled=false …
├── src/
    ├── main/java/omtteam/openmodularturrets/...
    ├── main/resources/         # SHARED assets + fabric.mod.json and META-INF/neoforge.mods.toml (templated)
    ├── client/java/...         # client-only code (split source sets)
    ├── test/java/...           # JUnit tests for core/
    └── gametest/               # server game tests (own mod id openmodularturrets-gametest)
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
        versions("1.20.1-fabric" to "1.20.1", "1.21.1-fabric" to "1.21.1", "1.21.1-neoforge" to "1.21.1")
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
- Constants can be compared in conditions: `//? if >=1.21 {`, `//? if <1.21 {`, `//? if 1.20.1 {`.
- Version-specific *properties* live in `versions/<v>/gradle.properties`.
  Read them in the build via `property("…")`.

### Loaders

Each Stonecutter node is `<minecraft version>-<loader>`; the loader picks the build script (`build.<loader>.gradle.kts`).
The setup and the patterns come from OpenModularLighting (branch `neoforge-test`), where the whole port is done; port OMT the same way, step by step, and design new cross-cutting interfaces with the user (§7 "Designing APIs").

- **Whole loader-specific classes** go into a `platform/<loader>/` package (`platform/fabric/`, `platform/neoforge/`, also under `client/`).
  `mod-common` leaves the other loaders' `platform/` packages out of the compilation, so these classes need no Stonecutter comments.
- **Shared code calls the loader through services:** `platform.Platform` holds the `PlatformServices` record (`LoaderInfo`, `Fuel`, `Network`, `Menus`, `Energy`), `client.platform.ClientPlatform` the `ClientPlatformServices` (`ClientNetwork`).
  The entrypoints install them first thing (`OMTFabric`, `OMTNeoForge`, `OMTFabricClient`, `OMTNeoForgeClient`); a getter before that or a second install throws.
  `NetworkCompat`, `ClientNetworkCompat` and `MenuCompat` forward to them.
- **Small differences** inside a shared class use the loader constants: `//? if fabric {` / `//? if neoforge {` (set in `stonecutter.gradle.kts`).
  Keep them inside `compat/` and `platform/`, never in gameplay code.
- `core/` stays loader-free (`verifyCoreIsolation` also forbids `net.neoforged`).
- **Porting in progress:** `build.neoforge.gradle.kts` compiles only the packages (or single files) listed in `ported`.
  Add one once it no longer uses Fabric API or Team Reborn Energy directly; at the end the filter becomes an exclusion of `datagen` only (Fabric-only by design: NeoForge gets the converted Fabric data, `convertFabricData`).
- `publish_enabled=false` in `versions/1.21.1-neoforge/gradle.properties` keeps the NeoForge node a dry run even with tokens; remove it only when the user wants NeoForge releases.
- The mod version is `<mod_version>+<minecraft version>-<loader>`, so the jars of both loaders can be told apart.

### NeoForge port: design decisions (made by the user)

These were decided step by step while porting OpenModularLighting (branch `neoforge-test`), which is the reference implementation.
Apply them to OMT the same way; do not re-open them without asking.
New questions that these do not answer go through §7 "Designing APIs".

**Overall approach**

- Native NeoForge support, not Sinytra Connector, not Forgified Fabric API, not Architectury, not a MultiLoader subproject layout.
- Stonecutter loader axis: one source set, nodes `<minecraft version>-<loader>`, one build script per loader; NeoForge only for 1.21.1 (1.20.1 stays Fabric-only).
- ModDevGradle for the NeoForge node (build-time only).
- Whole loader-specific classes in `platform/<loader>/` (left out of the other loaders' builds); `//? if fabric` / `//? if neoforge` only for small differences inside `compat/` and `platform/`.
- Mod-independent build logic in `buildSrc` (`mod-common`), copied between the mods; publishing stays disabled for NeoForge (`publish_enabled=false`) until the user says otherwise.

**Platform layer**

- Two directions: where the loader is in charge (registration, events, capabilities, client registrations), the loader entrypoint calls shared code ("wiring"); only where gameplay code must reach the loader there is a service.
- Services are small interfaces (not one big one), handed over by **startup injection**: each entrypoint creates its implementations and installs them once, first thing.
- Holder: a record `PlatformServices` with every service plus a static holder `Platform` with short getters (`Platform.network()`); client: `ClientPlatformServices` / `ClientPlatform`.
  A new service is a new record component, so every entrypoint stops compiling until it supplies one.
- Fail fast: a getter before `install`, or a second `install`, throws `IllegalStateException`.
- The existing `compat/` facades (`NetworkCompat`, `ClientNetworkCompat`, `MenuCompat`) keep their signatures and forward to the services, so call sites do not change.
- `OMT` stays the shared class (constants, start-up); the loader entrypoints are `OMTFabric` / `OMTNeoForge` (`OMTFabricClient` / `OMTNeoForgeClient`).

**Services (names and scope as decided)**

- `LoaderInfo`: `configDir()` and `isModLoaded(modId)`; named implementation classes per loader.
- `Fuel`: `burnTime(stack)` and `remainder(stack)` (the loader's stack-aware remainder hook, like a furnace).
- `Network` / `ClientNetwork`: NeoForge registrations are recorded and applied in `RegisterPayloadHandlersEvent` (later registration throws); a server-to-client packet without a client handler is logged and dropped; channels are `optional()`; handlers run on the main thread.
- `Menus`: `createBlockMenuType(factory)` / `openBlockMenu(player, title, pos, factory)`; opening data is a `BlockPos` only.
- `Energy`: `isBattery(stack)`, `dischargeItem(container, slot, target, maxAmount)`, `pushToBlock(level, pos, side, source, maxAmount)` (side = face of the target block; the generator needs the push).
  OMT has no battery slot, so its `Energy` has `pushToBlock` only (decided by the user); copy the other two from OpenModularLighting if one is ever added.

**Energy and items**

- Energy approach: one loader-neutral buffer plus thin adapters per loader (not Team Reborn Energy as the internal model, not a buffer per loader).
- The buffer is `core.energy.EnergyStore` (OMT's `core.base.EnergyStore`, extended in OpenModularLighting): `final`; `insert` / `extract` (limited by `maxInsert` / `maxExtract`) for other blocks, `generate` / `consume` / `drain` for the owner, `setAmountUnchecked` for loading and rollback.
  `onChange` may fire for a Fabric transaction that is rolled back later (an extra `setChanged()` is accepted).
- Block entities expose `@Nullable EnergyAccess getEnergyAccess(side)` (core interface; a store or a view over several).
- Fabric adapter: one `SnapshotParticipant` per store, cached in `platform/fabric` with weak keys **and weak values** (a plain `WeakHashMap` would leak, because the adapter references its store).
  NeoForge adapter: `IEnergyStorage`, clamped to `int`; 1 E = 1 FE.
- Item transfer: the vanilla `Container` stays the inventory; `inventory.ItemAccess` (record: container, first slot, slot count, insert filter, extract filter; `insertOnly(container)`) describes what other blocks may do, exposed by `getItemAccess(side)`.
  Adapters build on the loaders' own container wrappers (Fabric `InventoryStorage`, slotted for Jade; NeoForge `RangedWrapper` + `InvWrapper`).

**Registration and events**

- Registration approach B: each registry class is registered in the `RegisterEvent` of its own registry on NeoForge (not everything in the first event).
  `OMT.init()` loads the config and registers the packets; `OMT.registrationSteps()` lists `RegistrationStep(registry, call)` records in dependency order; Fabric runs them in order.
  NeoForge throws at common setup if a step never ran.
- Shared handlers called by the loaders' events: `InteractiveBlock.handleUse(player, level, hand, hit)` and `OMTConfig.onPlayerJoin(ServerPlayer)` (config `init()` only loads).
- Creative tab: `VersionCompat.creativeTabBuilder()` with a loader condition (no service).
- Common tags: plain `TagKey`s with the `c:` ids, no Fabric `ConventionalItemTags`.
- Recipe conditions: the condition logic stays shared; each loader registers it with its own API in `platform/<loader>`.

**Client**

- Shared client start-up class (`OMTClient`, like `OMLClient`) with `init()`, `registerScreens(ScreenRegistrar)` and tooltip content; `ScreenRegistrar` (generic method, takes `MenuScreens::register` and `event::register`) lives in `client/`.
- Renderers and colour providers expose their content (e.g. a `BlockColor` method plus the block list, a render method taking level, pose stack, buffers and camera); the loader entrypoints register them.
- Render types on NeoForge come from `"render_type"` in the model JSON; Fabric keeps `BlockRenderLayerMap` (keep both in sync).

**Data**

- No NeoForge datagen: the NeoForge build converts the Fabric output of `datagen_from` (`fabric:load_conditions` → `neoforge:conditions`, `condition` → `type`); only conditions in the mod's own namespace convert, anything else fails the build.

**GameTests**

- Shared test classes for both loaders: they implement a shared interface (e.g. `OMTGameTests`, like OpenModularLighting's `OMLGameTests`) instead of `FabricGameTest`, use the mod's own empty 8 × 8 × 8 structure (`structure/` and, for 1.20.1, `structures/`), and carry `@GameTestHolder(MOD_ID)` + `@PrefixGameTestTemplate(false)` behind `//? if neoforge`.
- `TestTransfer` with `hasEnergy`, `insertEnergy`, `insertItem`, `extractItem` (Fabric lookups / NeoForge capabilities) replaces direct Fabric lookups in tests.
- The test battery keeps its energy in the item's custom data, with loader-specific energy access.
- Test mod id with an underscore on both loaders (`openmodularturrets_gametest`); NeoForge mod ids cannot contain hyphens.
- No test class may sit in a main-mod package (NeoForge modules cannot share packages); reach protected methods through public vanilla API.

**Jade**

- `@WailaPlugin` on the plugin without a loader condition (NeoForge finds it by the annotation, Fabric keeps the `jade` entrypoint).
- Jade stays off the NeoForge GameTest server (it sends a packet to the vanilla mock players, which NeoForge refuses).

### Commands

```bash
./gradlew "Set active project to 1.20.1-fabric"   # switch source to 1.20.1
./gradlew "Set active project to 1.21.1-fabric"   # switch back (VCS state)
./gradlew chiseledBuild                           # build ALL nodes (custom task in stonecutter.gradle.kts)
./gradlew :1.20.1-fabric:build                    # build a single node
./gradlew :1.21.1-fabric:runClient                # run client for a node
./gradlew :1.21.1-fabric:runServer
./gradlew :1.21.1-fabric:runGameTest              # server game tests for one node (also run by check / build)
./gradlew :1.20.1-fabric:runDatagen               # regenerate data for a node
./gradlew :1.21.1-fabric:runDatagen
./gradlew :1.21.1-neoforge:runClient              # NeoForge client (run dir versions/1.21.1-neoforge/run; port in progress)
./gradlew dependencyMap                           # interactive package dependency map -> build/reports/dependency-map/index.html
./gradlew :1.21.1-fabric:pitest                   # mutation testing of core -> versions/1.21.1-fabric/build/reports/pitest/index.html
./gradlew chiseledPublish                         # upload all nodes to CurseForge/Modrinth (custom task)
./gradlew :1.21.1-fabric:publishMods              # upload one node
```

### Publishing

- Uses `me.modmuss50.mod-publish-plugin` (approved by the user), configured in `publishMods` in `buildSrc/src/main/kotlin/mod-common.gradle.kts` (the loader scripts only set the jar to upload).
- Settings in `gradle.properties`: `release_type` (release/beta/alpha), `curseforge_project_id` (224663, the old OMT page), `modrinth_project_id` (empty = Modrinth skipped).
- Tokens are never committed: environment variables `CURSEFORGE_TOKEN` / `MODRINTH_TOKEN`, or `curseforge_token` / `modrinth_token` in `~/.gradle/gradle.properties`.
  Without a token the upload is a dry run (output in `versions/<v>/build/publishMods/`).
- **Changelog** (`CHANGELOG.md`, Keep a Changelog format): every user-visible change gets a one-line entry under `## [Unreleased]` (Added / Changed / Fixed / Removed), by hand or via `./gradlew changelogEntry --type=fixed --entry="…"`.
  Add the entry together with the change.
- **Release steps:** set `mod_version` (e.g. `4.0.0-alpha.2`) and `release_type` → `./gradlew releaseChangelog` (moves Unreleased into `## [<mod_version>] - <date>`) → `./gradlew chiseledPublish`.
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

In-game behaviour (turrets firing, effects on entities) is tested with Fabric GameTest in `src/gametest` (Loom `fabricApi.configureTests`, own source set and mod id, never shipped).
Classes implement `FabricGameTest`, are listed under the `fabric-gametest` entrypoint in `src/gametest/resources/fabric.mod.json` and use `EMPTY_STRUCTURE` (8×8×8 air) with the scene built in code.
`check` (and so `build` / `chiseledBuild`) runs them on a dedicated test server in `versions/<v>/build/run/gameTest` (the world is deleted before every run).

- `TurretScene` builds a ready-to-fire base + turret (owner, always on, full energy, max range) and spawns targets (husks without AI).
  A base placed with `setBlock` has no owner: set one right away or it drops itself.
- `TurretScene` also loads the turret's ammo into the base (energy alone is not enough for the railgun and the projectile turrets).
- Turrets that scatter get up to 10 attempts, each with a fresh target (`TargetAttempts`); a miss replaces the target, a hit passes.
  A ray attempt ends with its ray, a projectile attempt when its first projectile is gone.
  The test server does not tick entities in the chunks next to a test, and projectiles remove themselves before flying into such a chunk, so `TurretScene` force-loads the chunks around the scene while a test runs (otherwise a grenade that lands just past the target would vanish instead of exploding).
  Projectile tests fail just before their timeout with their state (shots, followed projectile, target) instead of timing out silently.
- Turret ranges reach into neighbouring tests, so every turret test gets its own `batch` and ends with `TurretScene.succeedWhen`, which removes the turret, base and entities once it passed (1.20.1 keeps a finished batch's blocks and entities next to the following one).
- `BaseGuiGameTests` clicks a base through `ServerPlayerGameMode#useItemOn` (the real server path, including Fabric's `UseBlockCallback`) and checks that the menu opens for the owner only.
  Fabric's `FakePlayer` refuses to open menus, so the test uses a small subclass that opens them without sending a packet.
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
├── OMT.java                     # ModInitializer, MOD_ID, id() helper, logger
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
├── OMTClient.java               # ClientModInitializer
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
- Turrets can only be placed against a base face.
  A turret without a base does nothing and should break or drop if its base is removed.
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
- `OMTPlugin` (entrypoint `openmodularturrets`) registers `TurretDefinition`s (type + `TurretWeapon` + default `TurretStats`) and `AmmoDefinition`s.
  `TurretRegistry.load()` runs first in `OMT.onInitialize` (built-ins via `BuiltinTurrets`, then plugins), then freezes the types; `ModBlocks`/`ModItems` register a block and item per type with the type's id, all on the shared `TURRET` block entity type.
- Weapons (`api.weapon`): `ProjectileWeapon`, `RayWeapon` (damage rules = core `RayProfile`), `EffectWeapon`, `AreaWeapon`, `SelfTargetingWeapon` (finds its own targets); projectile and ray weapons take entity hit handlers; `TurretBlockEntity` dispatches on the kind, `RayWeapons` / `EffectWeapons` do the work.
- Config: built-in turrets keep their typed sections; other mods' turrets live in `turrets.addon_turrets` (a `Map<String, TurretSetting>` keyed by id, defaults from their definition).
  `ConfigWriter`/`ConfigRanges` handle such section maps.
- Client: `OMTClientPlugin` (entrypoint `openmodularturrets_client`) registers models via `TurretModelRegistrar`; `TurretModels.init()` collects built-in and plugin models.
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

### Power & items

- Bases store energy internally.
  Expose it through the energy API on all sides (`getEnergyAccess(side)`); expose the ammo slots through `getItemAccess(side)` (Fabric `ItemStorage.SIDED` via `FabricBlockLookups`, NeoForge item handler capability).
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
- **Config versions:** `configVersion` in the config file; `ConfigMigration` replaces old default values (only if unchanged) when a file of an older version is loaded.
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
- [x] Camouflage for turret bases and the camouflage trap, one shared system (`core.camo`, `camo/`, `CamoMenu`, `CamoBakedModel`):
  - Disguise is set and reset the same way on both blocks (via gui with simple phantom slot) and rendered the same way.
  - A small camo settings GUI on both blocks (light value / light opacity sliders for tier 4+ bases and the trap), changes server-validated.
- [x] Memory card (copy/paste of base and camo trap settings, shift tooltip); camo trap with trusted players (`AccessProfile.TRUSTED_AND_ADMIN`)
- [x] Playtest both versions (client + dedicated server)
- [x] Game tests for every turret (`src/gametest`, see "Game tests").
  Each turret, on a base of its tier with energy and ammo, must hit a hostile target 5 blocks in front of it.
  Inaccurate turrets get up to 10 attempts (fresh target each time); the test fails if none of them hits.
  Additionally, projectile turrets must spawn their projectile and ray turrets must trace their ray to the target.
  - Projectile turrets (projectile spawned + hit; `ProjectileTurretGameTests`, spawns observed through `ServerEntityEvents.ENTITY_LOAD`; explosion and fire damage count as hits):
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

- [x] Build: Stonecutter nodes `<version>-<loader>`, `buildSrc` (`mod-common`, `bundled_licenses`), `build.neoforge.gradle.kts` (ModDevGradle, porting filter, data converted from the Fabric output), `neoforge.mods.toml` template, publishing disabled for NeoForge
- [x] Platform services copied from OpenModularLighting: `LoaderInfo`, `Fuel`, `Network` + `ClientNetwork`, `Menus`; facades forward to them; entrypoints split (`OMT.init()`, `OMTClient.init()`, `platform/<loader>` entrypoints)
- [x] Copied from OpenModularLighting: `inventory.ItemAccess` + Fabric/NeoForge adapters (in use since the item transfer step), `registry.RegistrationStep`, `client.ScreenRegistrar` (not used yet)
- [x] ArchUnit: the cycle `compat` / `compat.network` ↔ `platform` (facades call the services, the service interfaces use the facades' types) is accepted in `KNOWN_CYCLES` (decided by the user)
- [x] Energy: `core.energy.EnergyStore` (from OpenModularLighting: + `maxExtract`, `extract`, `drain`) and `EnergyAccess`, exposed by `getEnergyAccess(side)` on the base and the generator (which now uses the core store too); service `Energy` with `pushToBlock` only; adapters `FabricEnergyStorages` / `NeoForgeEnergyHandler` (replace `BaseEnergyStorage`); Fabric lookups in `platform/fabric/FabricBlockLookups`; game test for the generator push
- [x] Item transfer: turret base ammo slots and generator fuel slots through `ItemAccess` (`getItemAccess(side)`); Fabric lookups via `FabricItemStorages`; hopper game tests (`ItemTransferGameTests`); the NeoForge capabilities come with the registration step
- [x] Use `LoaderInfo` (config folder in `OMTConfig`) and `Fuel` (generator: `isFuel(stack)`, burn time, stack-aware remainder) instead of `FabricLoader` / `FuelRegistry`; game test for the lava bucket remainder (the plugin entrypoints in `TurretRegistry` / `TurretModels` stay for "Turret API plugin discovery")
- [ ] Registration: `OMT.init()` + `OMT.registrationSteps()` (also sounds, entity types), NeoForge per `RegisterEvent`; capabilities; block use, config sync, creative tab, recipe conditions, `c:` tags
- [ ] Mixins on NeoForge (`[[mixins]]` in `neoforge.mods.toml`)
- [ ] Client: screens, block entity/entity renderers, model layers, built-in item renderer, render layers, tooltips; camo models (Fabric Renderer API → NeoForge `ModelData`)
- [ ] Turret API plugin discovery (`api/`, Fabric entrypoint → NeoForge)
- [ ] GameTests shared with NeoForge (OpenModularLighting: `OMLGameTests`, empty structure, `TestTransfer`, no test classes in main packages)
- [ ] Jade on NeoForge (`@WailaPlugin`)
