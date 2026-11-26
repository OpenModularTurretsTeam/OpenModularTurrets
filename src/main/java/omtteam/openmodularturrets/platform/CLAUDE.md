# CLAUDE.md — OpenModularTurrets, loaders and the platform layer (`platform/`)

Moved out of the top-level `CLAUDE.md`, so that it is loaded only when you work in this folder; the top-level file has the index.
Section numbers (§) and relative paths (`../CLAUDE.md`, `../../OpenModularCore`) are those of the top-level file; paths are relative to the repository root.

## Loaders

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

## Second-loader design decisions (made by the user)

These were decided step by step while porting OpenModularLighting, which is the reference implementation.
Apply them to OMT the same way; do not re-open them without asking.
New questions that these do not answer go through §7 "Designing APIs".

**Overall approach**

- Native NeoForge support, not Sinytra Connector, not Forgified Fabric API, not Architectury, not a MultiLoader subproject layout.
- Stonecutter loader axis: one source set, nodes `<minecraft version>-<loader>`, one build script per loader (NeoForge here).
- ModDevGradle for the NeoForge node (build-time only).
- Whole loader-specific classes in `platform/<loader>/` (left out of the other loader's build); `//? if fabric` / `//? if neoforge` only for small differences inside `compat/` and `platform/`.
- Mod-independent build logic in `omlib/build-logic` (`mod-common`), shared between the mods as the `omlib` git submodule.

**Platform layer**

- Two directions: where the loader is in charge (registration, events, capabilities, client registrations), the loader entrypoint calls shared code ("wiring"); only where gameplay code must reach the loader there is a service.
- Services are small interfaces (not one big one), created by OMLib's `Platform` / `ClientPlatform` on first use (no mod installs them).
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
- The buffer is `core.energy.EnergyStore` (in `omlib-core`): `final`; `insert` / `extract` (limited by `maxInsert` / `maxExtract`) for other blocks, `generate` / `consume` / `drain` for the owner, `setAmountUnchecked` for loading and rollback.
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
