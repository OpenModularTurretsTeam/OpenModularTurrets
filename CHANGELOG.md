# Changelog

All notable changes to Open Modular Turrets (Fabric). The format follows
[Keep a Changelog](https://keepachangelog.com/en/1.1.0/).

Add every user-visible change as a one-line entry under **Unreleased** (sections: Added, Changed, Fixed, Removed),
by hand or with `./gradlew changelogEntry --type=added --entry="…"`. Before a release, `./gradlew releaseChangelog`
turns *Unreleased* into the release section; `chiseledPublish` uploads exactly that section.

## [Unreleased]

### Added

- The potato cannon turret can now also fire poisonous potatoes

### Fixed

- Projectile turrets now lead the target with the real flight time (air drag) and raise the aim by the projectile drop, so slow projectiles no longer hit below the target
- Projectile turrets aim at the target's eyes instead of its body when a ledge hides the body, so shots no longer hit the block in front of it
- Potato cannon and disposable turret projectiles are now visible right at the turret instead of a few blocks away

### Changed

- BREAKING: Renamed Relativistic Turret to Slowing Turret internally (lang files with  be changed later)
  - This will cause all existing relativistic turrets to disappear
- The inaccuracy of the turrets was increased due to the targeting fixes which made turrets way too accurate

## [4.0.0-beta.2] - 2026-10-15

### Changed

- The config file is now documented: every key has a comment with its description, range and default
- The crossbow turret now fires the new Crossbow Bolt ammo (iron ingot, stick, feather) instead of vanilla arrows (placeholder texture)

### Added

- Creative upgrade: fills the base's energy storage and removes the ammo usage of its turrets (placeholder texture)

## [4.0.0-beta.1] - 2026-10-14

### Fixed

- The melee turret is now visible: its blades extend and spin while it is active

### Changed

- The melee turret can only be mounted on top of or below a turret base
- The melee turret no longer needs a target from its base: it strikes everything in a 3x2x3 area around itself and its base
- Turret hits no longer knock their targets back

### Added

- Crafting recipes for the melee, crossbow, arc and plasma turrets

## [4.0.0-alpha.4] - 2026-10-13

### Fixed

- Projectiles now properly apply on-hit effects on their motion
- Fixed a bug where grenades would hurl away if they hit entities

### Changed

- Grenade now flies for 1.5 seconds instead of 2 seconds
- Grenade now explodes 4 ticks after hitting a valid enemy

## [4.0.0-alpha.3] - 2026-10-13

### Fixed

- Turrets no longer target mobs behind their own base through solid blocks, and can no longer aim into their base

## [4.0.0-alpha.2] - 2026-10-13

### Fixed

- Shift-clicking addons and upgrades into a turret base no longer ignores their slot stack limit

## [4.0.0-alpha.1] - 2026-10-12

### Added

- First release of the Fabric port for Minecraft 1.20.1 and 1.21.1, including Open Modular Passive Defense's items
- All turrets, projectiles, ammo, upgrades and addons from Open Modular Turrets 3.2 (1.12.2)
- Turret bases (tiers 1–5) with GUI, trusted players and access levels
- Hardened blocks, walls, fences and the camouflage trap from Open Modular Passive Defense
- Energy via Team Reborn Energy
- Generator: burns furnace fuel into energy (40 RF/t) and pushes it into adjacent turret bases and other energy acceptors

### Changed

- Turret kills only drop loot with fake drops addons; each additional addon adds one level of Looting

### Not yet implemented

- camo system for base and camo trap
