# CLAUDE.md — OpenModularTurrets, game layer (`src/main/java/omtteam/openmodularturrets`)

Moved out of the top-level `CLAUDE.md`, so that it is loaded only when you work in this folder; the top-level file has the index.
Section numbers (§) and relative paths (`../CLAUDE.md`, `../../OpenModularCore`) are those of the top-level file; paths are relative to the repository root.

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
│   ├── base/                    # adapters to core (BlockEntityBaseWorld, ContainerItemSlot, LivingTargetEntity), BaseComputerTurrets
│   ├── turret/                  # TurretBlockEntity (one type for all turrets), TurretMount, RayWeapons, EffectWeapons
│   ├── defense/                 # PassiveDefenseBlockEntity, CamoTrapBlockEntity
│   └── misc/                    # GeneratorBlockEntity, LeverBlockEntity
├── core/                        # (from OpenModularCore, omt-core; bundled, not in src/) game rules without Minecraft:
│                                # base, turret, target, security (DamageRules), generator, card, item, settings, storage, world,
│                                # computer (BaseComputerCommands, the computer commands of 1.12.2; ComputerTurrets, PlayerNames; PlayerNames is implemented by compat/computercraft/ServerPlayerNames)
├── camo/                        # CamoRules, CamoLightProperties (CamoHost, CamoData and the menu are in OMLib)
├── security/                    # OMTOwnershipSettings (trust lists, access levels and claiming are in OMLib)
├── inventory/                   # TurretBaseInventory, FuelContainer, AmmoRules
├── item/                        # ammo, upgrades (per machine), addons, memory card, ModuleToggles
├── entity/projectile/           # projectiles, TurretDamage, TurretImmunity, KillCounter
├── menu/                        # TurretBaseMenu, GeneratorMenu
├── network/                     # OMTPackets, BasePackets + BasePacketHandler (C2S), RayPacket (S2C)
├── config/                      # self-written config (JSON via Gson, OMLib's machinery), no library
├── compat/                      # SubLevelCompat (contraption poses, always the identity pose here), ModuleEnabledCondition, jade/ (server half), computercraft/ (the CC: Tweaked peripheral turret_base); the porting helpers are in OMLib
├── mixin/                       # LivingEntityMixin
├── platform/                    # fabric/, forge/: entrypoints, services, adapters (each built for its loader only)
│                                # <loader>/computercraft: registers the CC: Tweaked peripheral
└── datagen/                     # recipes, loot tables, tags, damage types (Fabric only)
```

## 5. Compat helpers (the porting layer)

The helpers in OMLib's `compat` packages are the porting layer between the version branches: the game layer is ported between branches by hand, and the helpers keep the call sites the same on every branch.
Use them instead of new `//? if` blocks:

| Difference | Helper |
| --- | --- |
| Reading NBT | `compat.nbt.NbtCompat`: `getInt` / `getLong` / `getFloat` / `getDouble` / `getBoolean` / `getString` (with a fallback), `getCompound` (also of a `ListTag` index), `getCompoundList`, `getIntArray` / `getLongArray`, `hasCompound` / `hasList` / `hasString` / `hasNumber`, `getUuid` (null if missing) / `putUuid`, `readBlockState`; never call the `CompoundTag` getters or `Tag.TAG_*` directly. It depends on Minecraft only, so packages outside the `compat` cycle may use it |
| BlockEntity NBT | `CompatBlockEntity` keeps `readData` / `writeData` with a `CompoundTag` |
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
| Furnace fuel | `Fuel.burnTime(level, stack)`, `GeneratorBlockEntity.isFuel(level, stack)` |

## OMLib (the library, `omlib/`)

Requested by the user on 2026-10-27: the code that OpenModularTurrets and OpenModularLighting used to share lives in the library OMLib (`../OMLib`, a mod of its own with the id `omlib`, package `omtteam.omlib`), which both mods bundle jar-in-jar.
Its README and `CLAUDE.md` describe the layout and the rules (no mod knowledge in the library, public API only where a mod needs it).
Build (bootstrap into `omlib/build/maven`, `OMLIB_SKIP_BUILD=1`), changing the library and moving the pointer: workspace `CLAUDE.md`, "OMLib in the mods".

- **Where things are:** the platform layer (`Platform`, `LoaderInfo`, `Fuel`, `Network`, `Menus`, `Energy` and their loader classes), the compat helpers (`VersionCompat`, `NbtCompat`, `BlockCompat`, `NetworkCompat`, `MenuCompat`, `CompatBlockEntity`, `CommonTags`, `ClientCompat`), `core.energy`, `inventory.ItemAccess`, `TransferHost`, the config machinery (`@Comment`, `@Range`, `ConfigWriter`, `ConfigRanges`, `ConfigValidator`, `ConfigMigration`), `RegistrationStep`, the block base `CompatEntityBlock` with `BlockCodec`, the single right-click handling `InteractiveBlock` (with interceptors), `ConfigManager` (with the config sync) and the camouflage menu with its screen, packets and models are in the `omtteam.omlib` packages.
  Wherever the rest of this file names those classes under `omtteam.openmodularturrets`, read `omtteam.omlib`; they kept their names and package layout.
- **Runtime:** the library's services (`Platform`, `ClientPlatform`) are created on first use, so it does not matter which mod's entrypoint runs first; the library's own entrypoints register the camo menu type and packets.
  The mod passes its transfer block entity types to the Fabric registrar (`FabricBlockLookups.register(ModBlockEntities.transferTypes())`); on Forge the library's own entrypoint attaches the capabilities to every `TransferHost` (see §4 "Forge 1.20.1").
- **Security and ownership** (in OMLib since 2026-10-27, requested by the user): `core.security` (`TrustManager`, `AccessLevel`, `AccessProfile`, `TrustedPlayer`, `PlayerInfo`, `PlayerIdentity`, `SecurityEnvironment`, `TrustResult`; `DamageRules` is in `omt-core`), `security` (`OwnedHost`, `TrustHost`, `OwnershipSettings`, `LevelSecurityEnvironment`, `PlayerAdapter`, `PlayerLookup`, `SecurityNbt`), `blockentity.OwnedBlockEntity`, the claiming classes in `block` (`ClaimLink`, `ClaimSpread`, `ClaimInteraction`, `WandInteraction`, `UnownedMarker`), `item.OwnershipWandItem`, `core.world.Face` / `BlockPosition` / `FloodFill`, the trust packets (`omlib:add_trusted_player`, `omlib:remove_trusted_player`, `omlib:change_access_level`, `omlib:disown`) and the client screens (`TrustedPlayersScreen`, `DisownConfirm`, `CompatScreen`, `OwnershipWandTooltip`, the `trusted_players.png` texture, texts under `*.omlib.*`).
  The mod supplies `OMTOwnershipSettings` (reads the config), `TurretBlock implements ClaimLink` (a turret's owner is its base, and turrets connect claims), `PassiveDefenseBlockEntity extends OwnedBlockEntity`, and `ownershipSettings()` on the base and the trap; the library registers the wand and claim interceptors itself, before the mods' own.
  The mod registers its own item of `OwnershipWandItem` (`ModItems.OWNERSHIP_WAND`) and adds its immunity line to the tooltip (`OwnershipWandImmunityTooltip`).
  `TurretBaseBlockEntity` builds the base's `TrustManager` (`LevelSecurityEnvironment`, change callback `onLogicChanged(true)`) and passes it to `TurretBaseLogic`.
- **Camouflage:** `CamoBlock` (with `overlayTintIndex()`, default -1: the quads of the block's own model drawn on top of the disguise), `CamoHost` (with `disguiseRules()`, `hasParentMenu()` / `openParentMenu` for the screen's "Back") and `DisguiseRules` (this mod: `CamoRules.RULES`) are the hooks; `CamoData(key, valid)` takes the NBT key (`"Camo"` here; it must stay per mod, or saved disguises are lost); the screen's extra buttons come from `CamoScreenExtras` (`TrapCamoExtras`, registered in `OMTClient.init`: the camouflage trap's Trusted and Disown buttons).
  `camo` must not depend on `compat` (it would join a package cycle): hosts call `remesh()` themselves after `CamoData.read`.

## Core Game Logic Source

The game logic of the mod is plain Java without Minecraft imports, in the package `omtteam.openmodularturrets.core`.
It is not in this repository: it is the module `omt-core` of OpenModularCore (`../../OpenModularCore`), shared with every Minecraft version; wiring, local core and `core_version`: workspace `CLAUDE.md`, "Builds", and the root `CLAUDE.md`, "Shared core logic".
A rule change goes to OpenModularCore, never into a copy here (`verifyNoCoreSources`, part of `check`, fails on files in the core package).
The logic consists of:

1. The turret base behaviour (targeting, ammo handling, upgrade/addon handling, the sort order of the ammo slots `core.storage.AmmoSort`) and the turret's own rules (`TurretState`: fire cooldown, concealer cycle; `RayMath`, `ProjectileMath`: damage formulas)
2. Who turret damage may hit (`DamageRules`); the trusted player permission system, the machine mode, the targeting settings, the camouflage permissions, the memory card data and its trust list copy rules are OMLib's core (`omlib-core`: `core.security`, `core.machine`, `core.camo`, `core.card`); the memory card's copy rules for a base's settings are `core.card.BaseCardRules` here
3. an abstract representation of a target entity
4. The generator's burning rules (`core.generator.GeneratorLogic`)
5. Unit Tests to test its behaviour (in OpenModularCore, run there on Java 17 and on Java 8).

The logic's randomness is `level.random`'s `nextLong`; the machine mode's language key is built in the game layer (`"gui.openmodularturrets.mode." + getSerializedName()`).

## Power & items

- Bases store energy internally.
  Expose it through the energy API on all sides (`getEnergyAccess(side)`); expose the ammo slots, insert only (a stranger's hopper must not disarm a base), through `getItemAccess(side)` (Fabric `ItemStorage.SIDED` via `FabricBlockLookups`, Forge item handler capability).
- Ammo is consumed from the base inventory.
  Costs and capacities are configurable in `config/`.
- "Sort Ammo" button in the base GUI (requested by the user on 2026-10-27; for everyone who opens the GUI, `BasePackets.Action.SORT_AMMO`): `TurretBaseInventory.sortAmmo` merges equal ammo up to the slot limit and orders Minecraft items, then OMT ammo, then other mods' items, each by registry name, through the core's `AmmoSort` (empty variant name: every item has its own registry name).
- Power generation addons (solar, redstone reactor) are ticked by the base.
- **Generator** (separate block, no owner/trust): a long-running power source for places without other power infrastructure.
  Burns any furnace fuel (the loader's fuel values through the `Fuel` service; remainders like empty buckets stay in the slot) from its 9 fuel slots, using them in order.
  One fuel tick yields `generatorEnergyPerTick` (default 40 RF).
  Energy multiplier upgrades (one upgrade slot, up to 4) burn `2^n` fuel ticks per tick, so production and fuel use both scale by `2^n` while one fuel item still yields the same energy (`GeneratorLogic.multiplier`).
  It pauses while its buffer (`generatorCapacity`, default 1,000,000) is full and pushes up to `generatorMaxOutput` (default 65,536 RF/t, a flat limit) into all neighbours through the `Energy` service (Team Reborn Energy on Fabric, FE on Forge).
  GUI: 3×3 fuel grid on the left, the blue upgrade slot centred in the free area (the rest of it stays free for later slots that are not fuel slots), the flame next to the energy bar.
  Hoppers insert fuel only and take out only leftovers (empty buckets).
- **Inventory expanders** (`InventoryExpanderBlock`, tiers 1–3, no block entity; decided with the user on 2026-10-27): they have no inventory, but every ammo slot of a turret base and fuel slot of a generator they reach holds `itemMaxStackSize × base stacks × (1 + Σ stacks)` (`core.storage.SlotCapacity`; `expanders.tier_N.stacks`, defaults 1/2/3; the base stacks are `bases.tier_N.baseAmmoStacks`, defaults 1/1/1/2/2, tiers 4 and 5 doubled with and without expanders as requested by the user, 1 on a generator).
  The base item's tooltip shows its max stack size (base stacks × 64, without expanders), the info panel left of the base GUI the current one (with expanders, from the synced menu data, `TurretBaseMenu.getSlotCapacity`).
  They are placed only against a turret base or generator (`isMachine`), and only the block next to a face counts: up to 2 plates as one double block (two of one tier in one block space, like slabs, `DOUBLE`: the second plate stacks onto the first, as a plate is less than half a block deep; decided by the user: same tier only, one block of 2 plates per face, a block behind it does not count); an expander counts for every machine next to it; fuel with a remainder (lava buckets) is not enlarged.
  The slots are OMLib large stacks (`LargeStackContainer`, `LargeStackMenu`, `LargeStackSlot`, `LargeStackScreen`): the cursor and the player's inventory only ever get normal stacks out of them, `VersionCompat.saveItems` saves the counts.
  Hardness and blast resistance come from the config while the game runs (defaults of base tiers 1, 3 and 5); models and textures are those of the 1.12.2 inventory expanders of tiers 1, 3 and 5.
- **Upgrades are per machine:** `core.item.UpgradeType` is the common interface, each machine has its own enum (`TurretBaseUpgradeType`, `GeneratorUpgradeType`) and item subclass of `item.UpgradeItem` (`TurretBaseUpgradeItem`, `GeneratorUpgradeItem`).
  A machine's upgrade slots accept only its own subclass.
  A new machine gets its own enum and item subclass.
- **Upgrade and addon switches** (requested by the user on 2026-10-29): the config sections `upgrades` and `addons` have one boolean per upgrade (both machines) and addon item (`ConfigData.Upgrades` / `Addons`, `isEnabled`; `item.ModuleToggles` for stacks and item ids).
  A disabled one stays in its slot but counts as not installed (`TurretBaseLogic.getUpgradeCount` / `getAddonCount` through `LogicSettings.upgradeEnabled` / `addonEnabled`, `GeneratorBlockEntity.getMultiplierUpgrades`), and its recipe and unlock advancement do not load: the resource condition `openmodularturrets:module_enabled` (`compat.ModuleEnabledCondition`, `"items"`: item id paths; registered per loader like OpenModularLighting's `block_enabled`: `FabricModuleEnabledCondition`, `ForgeModuleEnabledCondition`), added by datagen to every upgrade and addon recipe.
  Recipes follow the config only when data loads (restart).
  The client shows it with the synced config (`client.render.DisabledModuleOverlay`): a red frame and tint behind the machine slots holding one (base and generator `renderBg`), vanilla's barrier sprite over the icon in every GUI (Fabric: client mixin `GuiGraphicsMixin` at the end of `renderItemDecorations`; Forge: `RegisterItemDecorationsEvent`), and a red first tooltip line (`ModuleTooltip`, `tooltip.openmodularturrets.module_disabled`).
  Game tests: `ModuleToggleGameTests`.
- **Moving and clearing blocks:** turret base and generator are `Clearable` (`/setblock` and similar would otherwise duplicate their items); turret, crank and generator implement `rotate` / `mirror` (`BlockMoveGameTests`).

## Known performance / memory trade-offs

Deliberately not optimized yet; revisit only if profiling shows a problem.

- **Light block state properties on every base tier.** `TurretBaseBlock` declares `light_value` and `light_opacity` (16 × 16 = 256 states) for tiers 1–5, because the block states are built in the super constructor before the tier is known.
  Tiers 1–3 therefore carry 255 unused states each (765 extra `BlockState` objects, about 1–3 KB each, so roughly 1–2.5 MB of heap, plus one model-lookup entry per state on the client).
  Their values always stay at the defaults, so there is no per-tick cost.
  - Option: a separate subclass or block class for tiers 4–5 that adds the properties (the earlier `LightTurretBaseBlock` design), or a constructor-independent way to pick the properties per tier.
  - Option: store the light settings in the block entity instead of block state properties (smaller state count, but the light engine then needs a block state change anyway to update).
