# Changelog

All notable changes to Open Modular Turrets for Minecraft 1.12.2.
The format follows [Keep a Changelog](https://keepachangelog.com/en/1.1.0/).

Add every user-visible change as a one-line entry under **Unreleased** (sections: Added, Changed, Fixed, Removed), by hand or with `./gradlew changelogEntry -Ptype=added -Pentry="…"`.
Before a release, `./gradlew releaseChangelog -Pchangelog_version=<tag without v>` turns *Unreleased* into the release section; the CurseForge upload of that tag uses exactly that section.

## [Unreleased]

### Fixed

- The Melee, Relativistic and Teleporter Turrets crashed the game when they were saved (3.2.0 alpha)
- Player kills of a base are counted and saved (they were loaded into the kill counter and never saved)
- The neutral force list config was read into the mob block list
- Turrets on auto fire took the ammo of a shot twice
- Forced shots of Laser, Arc and Railgun Turrets crashed the game without a target and ignored missing energy

### Changed

- Pets of players in the owner's scoreboard team are no longer targeted, and team members no longer get turret warnings
- Turret bases pick the targets of their turrets and drop a target as soon as it is no longer valid (out of range or sight, trusted, settings changed)
- Targeting priorities are added up instead of multiplied and use the real distance, so turrets prefer the targets their priorities describe
- The mob block list config is honoured, and the neutral force list wins over the mob force list
- Turrets no longer target entities they would have to aim into their own base for (more than 45° below the mounting side)
- Only blocks with a collision box block the view of turrets, and turrets no longer see through their own base
- The recycler addon saves ammo with the configured chance (it saved about 1 % of shots whatever the config said)
- The solar panel addon charges a base once per tick instead of once per tick for every turret on it
- Pets are protected by the trust list of their owner also while the owner is offline
- The Railgun Turret ignores scattershot upgrades; Melee, Relativistic and Teleporter Turrets no longer use more energy with them
- Turret bases no longer give their energy to other blocks

### Removed

- Inventory and power expanders: they disappear from worlds, together with the items in them
- The base controller API (`IBaseController`, `ILaserController`) of the discontinued OMTControl addon

## [3.2.0]

### Added

- A priority system for turrets and reworked targeting largely
- Turret max amount info to tooltips on bases/turrets
- Config to disable ammo usage on turrets
- Plasma launcher turret (WIP)
- Arc turret (WIP)
- Melee turret (WIP)

### Changed

- Needs OMLib 3.2.0-255
- Cleaned up Valkyrien Skies integration
- Cleaned up Inventory related functions
- Caching some values for turrets and bases to improve their performance

### Fixed

- Energy usage was not settable to zero
- Crash when ray tracing turrets hit multipart entities (such as the ender dragon)
- Computer Craft missing functions added and bugged ones fixed
- Inventory expanders only having half of their intended stacksize limit
- Lots of targeting bugs fixed
- Accuracy now scales a bit better and won't reach a 100% anymore (except on 0 deviation turrets)
- Range now properly updates when putting in or taking out range upgrades
- Some tooltips errors fixed
- Turrets dropping when tile entities placed/removed next to them
