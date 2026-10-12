# Changelog

All notable changes to Open Modular Turrets (Fabric). The format follows
[Keep a Changelog](https://keepachangelog.com/en/1.1.0/).

Add every user-visible change as a one-line entry under **Unreleased** (sections: Added, Changed, Fixed, Removed),
by hand or with `./gradlew changelogEntry --type=added --entry="…"`. Before a release, `./gradlew releaseChangelog`
turns *Unreleased* into the release section; `chiseledPublish` uploads exactly that section.

## [Unreleased]

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
