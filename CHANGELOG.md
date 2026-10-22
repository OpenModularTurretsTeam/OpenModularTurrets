# Changelog

All notable changes to Open Modular Turrets for Minecraft 1.12.2.
The format follows [Keep a Changelog](https://keepachangelog.com/en/1.1.0/).

Add every user-visible change as a one-line entry under **Unreleased** (sections: Added, Changed, Fixed, Removed), by hand or with `./gradlew changelogEntry -Ptype=added -Pentry="…"`.
Before a release, `./gradlew releaseChangelog` turns *Unreleased* into the section of the version set in `gradle.properties`; the CurseForge and Modrinth uploads of that version (`./gradlew -b build.gradle release`) use exactly that section.

## [Unreleased]

### Added

- The Melee Turret has a model: its blades extend and spin while something is in reach
- Generator: burns furnace fuel into energy for adjacent turret bases, with 9 fuel slots and an upgrade slot for the new Energy Multiplier upgrade (each doubles output and fuel use); placed 3.x power generators become generators and keep their fuel
- Turret API for other mods (`@OMTPlugin.Register`, `api` and `api.weapon`, client models via `@OMTClientPlugin.Register`); added turrets get their config in `config/openmodularturrets_addon_turrets.cfg`, see `docs/turret-api.md`

### Fixed

- The Melee, Relativistic and Teleporter Turrets crashed the game when they were saved (3.2.0 alpha)
- Player kills of a base are counted and saved (they were loaded into the kill counter and never saved)
- The neutral force list config was read into the mob block list
- Turrets on auto fire took the ammo of a shot twice
- Forced shots of Laser, Arc and Railgun Turrets crashed the game without a target and ignored missing energy
- The Grenade Turret was only available while the Laser Turret was enabled in the config
- The Crossbow Turret's config section had the name of the Potato Cannon Turret's; it is "Crossbow Turret" now
- Rays of Laser, Railgun and Arc Turrets could hit entities next to the ray
- The Plasma Turret tooltip showed tier 3, although the turret needs a tier 5 base
- The configure GUI no longer draws its buttons below its background
- The info panel of the turret base GUI no longer covers the GUI when its lines are long (e.g. long player names)

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
- The Railgun Turret ignores scattershot upgrades; Melee, Slowing and Teleporter Turrets no longer use more energy with them
- Turret bases no longer give their energy to other blocks
- The Relativistic Turret is now the Slowing Turret; placed turrets, items and its config section are converted
- Turrets aim relative to the side of the base they sit on, the same way on server and client, and turn smoothly
- Projectile turrets lead moving targets using the real flight time and drop of their projectiles
- Projectiles scatter by a fifth of the turret's inaccuracy instead of a twentieth
- The Melee Turret strikes everything it may hurt around itself and its base (3x2x3 blocks) without waiting for the base to pick a target
- The Melee Turret only fits on top of or below a base; melee turrets on the sides of a base drop as items
- The Arc Turret can be placed on every side of a base, not only on top
- The particles of the Teleporter Turret are shown in multiplayer too
- Mobs killed by turrets drop nothing unless the base has a fake drops addon (each further addon adds a level of Looting); the config options `doTurretsKillsDropMobLoot` and `doLootAddonsOverrideMobLootSetting` are gone
- Turret tooltips show what the turret does, the base tier it needs, its ammo and its energy per shot; turrets that ignore scattershot upgrades say so without shift
- The camouflage of a turret base is set in the new camouflage menu (Configure → Camouflage), which also holds the light sliders of tier 4 and 5 bases; right-clicking a base with a block no longer camouflages it
- The memory card needs the admin access level to copy and to write settings, also stores the camouflage, works on camouflage traps, and its tooltip shows what it holds; sneak-clicking anything else clears it (3.x cards still work)
- The Turret Memory Card is now called OM Memory Card

### Removed

- Inventory and power expanders: they disappear from worlds, together with the items in them
- The loot deleter base addon (turret kills drop nothing without a fake drops addon now); placed loot deleters disappear
- The power generator attachment (it had no function); it is replaced by the generator
- The base controller API (`IBaseController`, `ILaserController`) of the discontinued OMTControl addon## [3.2.0]
- The entity and ammo lists of the 3.x API (`api.lists`): they no longer affected targeting; the config lists and the disposable ammo allow list stay

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
