# OpenModularTurrets 1.12.2: architecture, gameplay rules and decisions

Moved out of the workspace's top-level `CLAUDE.md` (`../CLAUDE.md` from here), so that it is loaded only when you work in `OpenModularTurrets/`; the top-level file has the index.
Section names in quotes ("The shared core", "Running", "Versions", …) and relative paths (`../OpenModularCore`, `../CLAUDE.md` is the OMM root file) are those of the top-level file; paths are relative to the workspace folder `OMTTeam1.12/`.

## Architecture (layers and package layout, from "Architecture")

Each mod is layered like 1.20.1+:

- **`core`**: the game rules as plain Java, **free of Minecraft and Forge imports** (`net.minecraft`, `net.minecraftforge`, `com.mojang`, other mods' packages, non-core packages of the mod).
  Since 2026-11-19 it is not in the repos: it comes from OpenModularCore (see "The shared core"), with the same package names: OMLib's core (`omtteam.omlib.core`: trust lists, access levels, access profiles, energy buffer, camo, machine mode and targeting settings, memory card data and trust rules, world), OMT's (`omtteam.openmodularturrets.core`, including `card.BaseCardRules`, the base part of the memory card), OML's (`omtteam.openmodularlighting.core`).
  It is unit-tested there against fakes; OMPD has no core (its rules are game layer).
- **game layer**: blocks, tile entities, items, containers, network, registry, config, projectiles, api; translates between Minecraft and the core and stays thin.
- **client**: GUIs, renderers, models, client packet handlers.
- **`compat`**: integrations with other mods (JEI, TOP, HWYLA, OpenComputers, ComputerCraft).

The core defines small interfaces for what it needs from the world and the game layer implements them (1.20.1+: `BaseWorld`, `ItemSlot`, `TargetEntity`, `LogicSettings`, `SecurityEnvironment`).
Tile entities own one core object each and forward to it (`TurretBase` → `TurretBaseLogic`, turrets → `TurretState`).
Per-type behaviour is data on the type or the weapon (`TurretType`, `TurretWeapon`), never a `switch` / `==` on a specific turret type.

Target package layout of OMT (OMLib and OMPD follow the same pattern for the parts they have):

```
omtteam.openmodularturrets
├── OpenModularTurrets.java   # @Mod, start-up (MOD_ID and LOGGER in reference.Reference)
├── core/                     # game rules, no Minecraft: from OpenModularCore, bundled into the jar, not in the repo
├── api/                      # public turret API (definitions, weapons); built-in turrets use it too
├── registry/                 # registration: blocks, items, tile entities, entities, creative tab, built-in turrets, TurretLoader
├── turret/                   # TurretRegistry: turret and ammo definitions and the turret blocks, looked up by everything
├── sound/                    # ModSounds
├── block/                    # base/, turret/, misc/
├── tileentity/               # base/ (incl. the adapters to core), turret/, misc/
├── entity/projectile/        # projectiles, TurretDamage
├── item/, inventory/, container/
├── network/                  # C2S requests (always re-validated), S2C sync
├── config/                   # Forge config + LogicSettings adapter
├── handler/, fixes/, reference/
├── compat/                   # other mods (computer/: the commands ComputerCraft and OpenComputers share)
└── client/                   # gui/, render/, model/, network/; only via proxy
```

Dependency direction of OMT's game layer (`ArchitectureTest.noNewPackageCycles` keeps the known cycles from growing):
the mod class, `registry` and `handler` are on top and may use everything; nothing else imports the mod class or `registry` (registration only).
`config`, `reference`, `sound` and `turret` are looked up from everywhere, so they import no blocks, items or tile entities (`config` takes core types, not `api` ones).
No `util` grab-bag packages: a helper goes next to what it serves.

## Gameplay rules

- **Turret bases are the brain:** targeting, ammo, energy, upgrades, addons and the trust list live in the base.
  **Turrets are thin:** aim, cooldown, firing visuals and sounds, spawning projectiles; they request ammo and energy from the base.
- A turret has a facing (direction away from the base, all 6 faces); the base finds its turrets in its 6 neighbours; a turret without a base does nothing and drops.
- Aiming: compute the direction in world space, transform it into the turret's local frame (local "up" = facing), derive yaw/pitch; one math class (`TurretMath`) used by server and renderer.
  Sync aim sparingly and interpolate on the client.
- Targeting runs on the server tick, throttled (no entity scan every tick); never target the owner or trusted players; respect line of sight.
  Range, accuracy, fire rate, efficiency and scattershot formulas live in `core` and are not changed without asking the user.
- **Security:** owner (UUID + last known name) plus trusted players with access levels; only the owner changes the owner; members of the owner's scoreboard team count as trusted at the lowest level.
  Identity is the UUID, names are display only.
  **Every C2S packet re-validates on the server** (player distance, access level for the specific action); breaking and opening also respect permissions.
  Handlers find their block through OMLib's `network.PacketValidation.find` (loaded, within reach, of the type; a lookup must never load a chunk) and run through `PacketValidation.schedule`; OMT's base GUI messages are `network.BasePackets` / `BasePacketHandler`, as on 1.20.1+.
- **Upgrades are per machine:** one upgrade type per machine, its slots accept only its own upgrades.
- Server-authoritative config values that clients need are synced on login: the Forge `@Config` values of OMLib and OMT through OMLib's `config.ConfigSync` (`register(modId[, onChange])` in pre-init, `onChange` rebuilding derived state such as OMT's parsed ammo list; `registerValue` for config outside `@Config`, such as OMT's addon turret file; the client restores its own values on disconnect, and config-changed handlers call `ConfigSync.syncLocal` instead of `ConfigManager.sync`, so server values never reach the local file); OML's JSON config goes through the same sync as one value (`registerValue`, `OMLConfig.toJson` / `applyServerConfig`).
  The Forge config system stays for OMLib, OMT and OMPD because of the 3.x config files (decided by the user, 2026-10-25).

## Decisions for 4.0.0 (the OMT parts, from "Decisions for 4.0.0")

- **Taken over from 1.20.1+:** the generator block (with its energy multiplier upgrade, replacing the functionless power generator attachment), the turret API for other mods, the camouflage menu with light sliders, the trust list of the camouflage trap, the memory card copy rules, turret tooltips and the balance fixes (railgun ignores scattershot; melee, slowing and teleporter turrets do not use more energy with scattershot).
- **Removed:** the 3.x expanders and the loot deleter base attachment (the inventory expanders of 1.20.1+ were added later under new ids, `inventory_expander_tier_<one…three>`).
  Looting is inverted as on 1.20.1+: turret kills drop nothing unless the base has the fake drops addon.
- **Kept although 1.20.1+ has none:** the serial port addon with the OpenComputers / ComputerCraft integration, as it is.
- **Potentia converter addon removed** (decided by the user, 2026-11-04; it had no recipe and no function on 1.12): `addon_meta` meta 2 stays reserved (`AddonMetaItem.REMOVED_POTENTIA_META`, no `AddonType`, `ItemClassifier` maps it to none), hidden from the creative tab and JEI, without tooltip; old stacks keep their name and texture (model and `item.addon_potentia.name` kept) and do nothing in a base.

## Status and open items (the OMT parts, from "Status and open items")

### Decisions that still apply

- Behaviour differences settled on 2026-10-29 (the user chose per difference, mostly 1.20.1+'s behaviour): fences spare only the owner, no knockback from turrets, grenades and rockets break blocks only by config, crossbow bolts instead of arrows, blue arcs, turret API hit handlers as on 1.20.1+; OMPD's hardened blocks stay valid disguises.
- Not taken over from 1.20.1+: Sable/Create (1.21 only), the Mojang name lookup fix (1.12 uses `UsernameCache`).
- The turret API signatures were all decided by the user (`OpenModularTurrets/docs/turret-api.md`).

### Open

- OMT package cycle: `api.weapon` and `OMTApi` take `TurretBase` / `TurretTileEntity`; whether the 3.x `api.tileentity` interfaces (`ITurretBase`, `ITurretHead`, `ITurretBaseAddonTileEntity`) are kept, deprecated or removed is a decision for the user.
- **Upgrade and addon switches (4.1, merged into `1.12` with PR #597, unreleased):** config sections `upgrades` / `addons`; a disabled module counts as not installed and its recipes do not load.
  Its core part (`LogicSettings.upgradeEnabled` / `addonEnabled`) is in OpenModularCore since 2.1.0; when the branch is revived, use the core's API instead of the branch's own core changes.

## Build notes (from "One build per mod" and "Workspace gotchas")

- **ComputerCraft API** (`src/api/java` of OMLib and OMT): its own source set, compiled against, not packed into the jar (ComputerCraft provides it); OMT's peripheral is `compat.computercraft.TurretBasePeripheral` (`TurretBase` does not implement `IPeripheral`), `runenv` takes OMT's `sourceSets.api` compile-only.
- FML reports `The API ComputerCraft… loaded from an incompatible classloader` only if the CC API is on the classpath twice; it is not packed any more.
