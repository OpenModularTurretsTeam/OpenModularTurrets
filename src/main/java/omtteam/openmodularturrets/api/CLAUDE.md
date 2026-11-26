# CLAUDE.md — OpenModularTurrets, turret API (`api/`)

Moved out of the top-level `CLAUDE.md`, so that it is loaded only when you work in this folder; the top-level file has the index.
Section numbers (§) and relative paths (`../CLAUDE.md`, `../../OpenModularCore`) are those of the top-level file; paths are relative to the repository root.

## Turret API (adding turrets)

Other mods add turrets through `omtteam.openmodularturrets.api` (developer guide: `docs/turret-api.md`).
The built-in turrets use the same path, so there is no special casing per turret type anywhere:

- `core.turret.TurretType` is a namespaced value class (built-in constants keep their names), built with `TurretType.builder(ns, path)`; `TurretTypes` is the registry (built-in first, frozen after loading).
  Per-type behaviour is data on the type (`canMountOn`, `usesScattershot`, `skipsSlowedTargets`) or on the weapon, never a `switch`/`==` on a specific type.
  `AmmoType` is a value class too (`AmmoType.of(ns, path)`); compare with `equals`.
- `OMTPlugin` (Fabric entrypoint `openmodularturrets`, Forge `@OMTPlugin.Register`) registers `TurretDefinition`s (type + `TurretWeapon` + default `TurretStats`, optionally an `AmbientEffect` for client particles through `TurretDefinition.builder`) and `AmmoDefinition`s.
  `TurretRegistry.load(plugins)` runs first in `OMTStartup.init` (built-ins via `BuiltinTurrets`, then the plugins the loader entrypoint found), then freezes the types; `ModBlocks`/`ModItems` register a block and item per type with the type's id, all on the shared `TURRET` block entity type.
- Weapons (`api.weapon`): `ProjectileWeapon`, `RayWeapon` (damage rules = core `RayProfile`), `EffectWeapon`, `AreaWeapon`, `SelfTargetingWeapon` (finds its own targets); projectile and ray weapons take entity hit handlers; `TurretBlockEntity` dispatches on the kind, `RayWeapons` / `EffectWeapons` do the work.
- Config: built-in turrets keep their typed sections; other mods' turrets live in `turrets.addon_turrets` (a `Map<String, TurretSetting>` keyed by id, defaults from their definition).
  `ConfigWriter`/`ConfigRanges` handle such section maps.
- Client: `OMTClientPlugin` (Fabric entrypoint `openmodularturrets_client`, Forge `@OMTClientPlugin.Register`) registers models via `TurretModelRegistrar`; `TurretModels.init(plugins)` collects built-in and plugin models (the loader client entrypoint passes the plugins it found).
