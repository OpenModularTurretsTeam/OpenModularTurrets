# CLAUDE.md — OpenModularTurrets, loaders and the platform layer (`platform/`)

Moved out of the top-level `CLAUDE.md`, so that it is loaded only when you work in this folder; the top-level file has the index.
Section numbers (§) and relative paths (`../CLAUDE.md`, `../../OpenModularCore`) are those of the top-level file; paths are relative to the repository root.

## Loaders

Each Stonecutter node is `<minecraft version>-<loader>`; the loader picks the build script (`build.<loader>.gradle.kts`).
OMT follows OpenModularLighting's patterns; new cross-cutting interfaces are designed with the user (workspace `CLAUDE.md`, "Designing APIs").

- **Whole loader-specific classes** go into a `platform/<loader>/` package (`platform/fabric/`, `platform/forge/`, also under `client/` and the test mod).
  `mod-common` leaves the other loader's `platform/` package out of the compilation, so these classes need no Stonecutter comments.
- **Shared code calls the loader through services:** `platform.Platform` holds the `PlatformServices` record (`LoaderInfo`, `Fuel`, `Network`, `Menus`, `Energy`), `client.platform.ClientPlatform` the `ClientPlatformServices` (`ClientNetwork`).
  OMLib creates the services on first use, so it does not matter which mod's entrypoint (`OMTFabric`, `OMTForge`, `OMTFabricClient`, `OMTForgeClient`) runs first.
  `NetworkCompat`, `ClientNetworkCompat` and `MenuCompat` forward to them.
- **Small differences** inside a shared class use the loader constants: `//? if fabric {` / `//? if forge {` (set in `stonecutter.gradle.kts`), with separate import blocks where the packages differ.
  Keep them inside `compat/` and `platform/`, never in gameplay code.
- The core (`omtteam.openmodularturrets.core`) comes from OpenModularCore, which has no Minecraft or loader on its classpath; `verifyNoCoreSources` fails if `src` has files in the core package.
- `build.forge.gradle.kts` compiles everything except `datagen` (Fabric-only by design: Forge gets the converted Fabric data, `convertFabricData`).
  New code must not use Fabric API or Team Reborn Energy outside `platform/fabric` (and `datagen`), or the Forge node stops compiling.

## Second-loader design decisions (made by the user)

These were decided step by step while porting OpenModularLighting to its second loader (the reference implementation).
They apply to OMT the same way, here for Forge; do not re-open them without asking.
New questions that these do not answer go through "Designing APIs" (workspace `CLAUDE.md`).

**Overall approach**

- Native Forge support, not Sinytra Connector, not Architectury, not a MultiLoader subproject layout.
- Stonecutter loader axis: one source set, nodes `<minecraft version>-<loader>`, one build script per loader.
- Legacy ModDevGradle for the Forge node (build-time only).
- Whole loader-specific classes in `platform/<loader>/` (left out of the other loader's build); `//? if fabric` / `//? if forge` only for small differences inside `compat/` and `platform/`.
- Mod-independent build logic in `omlib/build-logic` (`mod-common`), shared between the mods through the `omlib` git submodule.

**Platform layer**

- Two directions: where the loader is in charge (registration, events, capabilities, client registrations), the loader entrypoint calls shared code ("wiring"); only where gameplay code must reach the loader there is a service.
- Services are small interfaces (not one big one), created by OMLib's `Platform` / `ClientPlatform` on first use; nobody installs them from a mod.
- Holder: a record `PlatformServices` with every service plus a static holder `Platform` with short getters (`Platform.network()`); client: `ClientPlatformServices` / `ClientPlatform`.
  A new service is a new record component, so each loader's service set in OMLib (`Platform.loaderServices()`) stops compiling until it supplies one.
- The existing `compat/` facades (`NetworkCompat`, `ClientNetworkCompat`, `MenuCompat`) keep their signatures and forward to the services, so call sites do not change.
- `OMT` holds the shared constants (`MOD_ID`, `LOGGER`, `id()`) and nothing else; the shared start-up is `startup.OMTStartup` (moved out of `OMT` by the user on 2026-10-25, so `OMT` leaves the package cycle); the loader entrypoints are `OMTFabric` / `OMTForge` (`OMTFabricClient` / `OMTForgeClient`).

**Services (names and scope as decided)**

- `LoaderInfo`: `configDir()` and `isModLoaded(modId)`; named implementation classes per loader.
- `Fuel`: `burnTime(stack)` and `remainder(stack)` (the loader's stack-aware remainder hook, like a furnace).
- `Network` / `ClientNetwork`: a server-to-client packet without a client handler is logged and dropped; a missing remote is accepted; handlers run on the main thread.
- `Menus`: `createBlockMenuType(factory)` / `openBlockMenu(player, title, pos, factory)`; opening data is a `BlockPos` only.
- `Energy`: `isBattery(stack)`, `dischargeItem(container, slot, target, maxAmount)`, `pushToBlock(level, pos, side, source, maxAmount)` (side = face of the target block; the generator needs the push).
  These services are in OMLib.
  OMT has no battery slot yet, but the battery methods are there (decided by the user on 2026-10-26, so that both mods use the same classes) for when one is added.
  `core.energy.EnergyGroup` is an `EnergyAccess` that spreads an insert over several stores (`fill(maxAmount, StoreInserter)`).
  OMT has no group, OpenModularLighting's panel light cluster is one.

**Energy and items**

- Energy approach: one loader-neutral buffer plus thin adapters per loader (not Team Reborn Energy as the internal model, not a buffer per loader).
- The buffer is `core.energy.EnergyStore` (OMLib's core): `final`; `insert` / `extract` (limited by `maxInsert` / `maxExtract`) for other blocks, `generate` / `consume` / `drain` for the owner, `setAmountUnchecked` for loading and rollback.
  `onChange` may fire for a Fabric transaction that is rolled back later (an extra `setChanged()` is accepted).
- Block entities expose `@Nullable EnergyAccess getEnergyAccess(side)` (core interface; a store or a view over several) and `getItemAccess(side)` by implementing `blockentity.TransferHost`; `ModBlockEntities.transferTypes()` lists them, and the loader registrations (`FabricBlockLookups`, `ForgeBlockCapabilities`) iterate over that list, so a new transfer block entity needs no loader code.
- Fabric adapter: one `SnapshotParticipant` per store, cached in `platform/fabric` with weak keys **and weak values** (a plain `WeakHashMap` would leak, because the adapter references its store).
  Forge adapter: `IEnergyStorage`, clamped to `int`; 1 E = 1 FE.
- Item transfer: the vanilla `Container` stays the inventory; `inventory.ItemAccess` (record: container, first slot, slot count, insert filter, extract filter; `insertOnly(container)`) describes what other blocks may do, exposed by `getItemAccess(side)`.
  Adapters build on the loaders' own container wrappers (Fabric `InventoryStorage`, slotted for Jade; Forge `RangedWrapper` + `InvWrapper`).

**Registration and events**

- Registration approach B: each registry class is registered in the `RegisterEvent` of its own registry on Forge (not everything in the first event).
  `OMTStartup.init()` loads the config and registers the packets; `OMTStartup.registrationSteps()` lists `RegistrationStep(registry, call)` records in dependency order; Fabric runs them in order.
  In OMT, `OMTStartup.init(plugins)` also loads the turret definitions first (and hands the addon turrets' default stats to `OMTConfig.init`); the loader entrypoint passes the turret plugins it found (decided by the user).
  Forge finds plugins by annotation (decided by the user): `@OMTPlugin.Register` / `@OMTClientPlugin.Register` (nested in the interfaces, RUNTIME, TYPE), read from the mod scan data by `platform/forge/ForgePlugins` (client ones on the client only); the mod id is the first mod of the jar; plugins are sorted by mod id, then class name.
  Forge throws at common setup if a step never ran.
- Shared handlers called by the loaders' events: OMLib registers the right-click event (`InteractiveBlock.handleUse`, with the `Interceptor`s: the library's own for the ownership wand and claiming come first, OMT adds the memory card in `OMTStartup.init`) and the join/disconnect events of the config sync (`ConfigSync`) once for all mods (decided by the user).
- Creative tab: `VersionCompat.creativeTabBuilder()` with a loader condition (no service).
- Common tags: plain `TagKey`s with the `c:` ids, no Fabric `ConventionalItemTags`.
- Recipe conditions: the condition logic stays shared; each loader registers it with its own API in `platform/<loader>`.

## Forge 1.20.1

The node `1.20.1-forge` follows the second-loader design decisions above; these are the Forge specifics:

- **Build:** `build.forge.gradle.kts` uses `net.neoforged.moddev.legacyforge` (`legacyForge { version = forge_version }`).
  Forge 1.20.1 runs on SRG names: the released jar is `reobfJar` (what `publishMods` uploads), and other mods (Jade) come through legacy ModDevGradle's remapping configurations: dependencies are declared on a `mod…` configuration (`modCompileOnly`, built in; `modLocalRuntime`, created by `obfuscation.createRemappingConfiguration(localRuntime)`), and their remapped jars land in its parent (`compileOnly`, `localRuntime`, which `runtimeClasspath` extends).
  Declaring them on the parent instead skips the remapping (Jade would be missing from the Forge dev runs).
- **Mixins:** the mixin annotation processor writes `openmodularturrets.refmap.json`; the Forge build adds `"refmap"` to its copy of the shared mixin config and names the config in the jar manifest (`MixinConfigs`; Forge 1.20.1 has no `[[mixins]]` in `mods.toml`).
  MixinExtras (`mixinextras_version`) is jar-in-jar'd into the Forge jar only.
  Forge's own events cannot replace `LivingEntityMixin`: knockback happens before `hurt` records the damage source, and cancelling `LivingDropsEvent` would also drop a killed player's inventory.
- **Data:** `convertFabricData` (a function in `omlib/build-logic`) copies the Fabric output with `"conditions"`.
  The node also generates `pack.mcmeta` (pack format 15) for the mod and the test mod: without one, Forge 1.20.1 loads neither's data.
- **Entrypoints:** `OMTForge` (`@Mod`, mod bus from `FMLJavaModLoadingContext`); Forge 1.20.1 has no client-only `@Mod` classes, so it constructs `client.platform.forge.OMTForgeClient` by name on a client (main cannot see the client source set).
  Plugins: `ForgePlugins`, the `@OMTPlugin.Register` / `@OMTClientPlugin.Register` scan described above.
- **Services:** `ForgeNetwork` uses one `EventNetworkChannel` per packet type (named by its id, missing remote accepted, decoded on the network thread, handled on the main thread).
  `ForgeMenus` uses `IForgeMenuType` / `NetworkHooks.openScreen` (which builds the menu itself, so `BaseGuiGameTests` needs no Forge override).
- **Capabilities:** no registration event in 1.20.1; OMLib's `OMLibForge` attaches `ForgeBlockCapabilities` to every `TransferHost` in `AttachCapabilitiesEvent` (`ForgeBlockCapabilities::attach`).
  It asks the block entity on every query (attached during construction) and keeps a side's `LazyOptional` while the block entity returns the same access object.
- **Client:** screens and item renderers are registered in `FMLClientSetupEvent` (no events for them).
  Forge 1.20.1 takes item renderers only from `Item#initializeClient`, so `ForgeItemRenderer.register` sets the Forge-added `Item.renderProperties` field by reflection (same name in dev and release; the shared item classes stay loader-free).
  `ForgeCamoModel` (OMLib) does not override ambient occlusion (Forge 1.20.1 does not pass model data there); its colour provider reads the model data through `level.getModelDataManager()`.
- `DamageSources#source` is private in vanilla 1.20.1 (only Fabric API widens it); OMT does not use it.
