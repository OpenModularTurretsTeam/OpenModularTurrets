# Changelog

All notable changes to Open Modular Turrets for Minecraft 1.12.2.
The format follows [Keep a Changelog](https://keepachangelog.com/en/1.1.0/).

Add every user-visible change as a one-line entry under **Unreleased** (sections: Added, Changed, Fixed, Removed), by hand or with `./gradlew changelogEntry -Ptype=added -Pentry="…"`.
Before a release, `./gradlew releaseChangelog` turns *Unreleased* into the section of the version set in `gradle.properties`; the CurseForge and Modrinth uploads of that version (`./gradlew release` in the mod folder) use exactly that section.

## [Unreleased]

### Added

- Recipes for the Melee, Crossbow, Arc and Plasma Turrets (as on 1.20.1+; the Arc Turret takes a gold ingot instead of a lightning rod)

### Changed

- The Hwyla and The One Probe turret tooltips show the inaccuracy (higher is less accurate, as on 1.20.1+) instead of an accuracy of 100 minus that value

### Fixed

- Unbreakable turret bases (`baseBreakable=false`) were destroyed by explosions
- The tier 3 turret base tooltip showed a raw translation key instead of "Energy"
- Memory cards stacked; they now stack to 1
- The functionless Throwable Bullet, Throwable Grenade and fake disposable ammo items are no longer in the creative tab
- Tipped arrows were accepted as ammo but never fired
- The redstone reactor counted fuel it could not take out of a slot

## [4.0.0-beta.5] - 2026-10-27

### Added

- Inventory expanders (tiers 1–3, as on 1.20.1+): placed against a turret base or generator, each of its ammo or fuel slots holds 1, 2 or 3 more stacks; two plates of a tier fit into one block space, up to 2 count per face (config section `expanders`)
- The turret base GUI shows the max stack size of the ammo slots, the turret base tooltip that of each tier

### Changed

- Requires OMLib 4.0.0-beta.5 or newer
- Trusted admins of a turret base can change its camouflage disguise, not only the owner
- Turret bases of tiers 4 and 5 hold 2 stacks in each ammo slot, which also doubles what expanders add (config option `baseAmmoStacks` per tier)
- Turret bases store more energy: 5,000 / 500,000 / 1,500,000 / 10,000,000 / 100,000,000 RF for tiers 1–5; config files that still have the old defaults get the new ones if (`general.configVersion`) is low enough

## [4.0.0-beta.4] - 2026-10-27

### Added

- Turret bases accept transparent blocks (glass, ice, leaves) as camouflage; the config option `allowTransparentDisguises` turns it off
- The disposable ammo list accepts `ore:oreName[,damage]` entries (e.g. `ore:cobblestone`, `ore:plankWood`) for every item of an ore dictionary entry

### Changed

- Requires OMLib 4.0.0-beta.4 or newer

### Fixed

- Disposable ammo list entries with a damage bonus (`minecraft:cobblestone,2`, the defaults) were not parsed, so no item was valid disposable ammo

## [4.0.0-beta.3] - 2026-10-26

### Changed

- The generator GUI labels are black like in the other turret GUIs (were dark grey on the dark background)
- Requires OMLib 4.0.0-beta.3 or newer

## [4.0.0-beta.2] - 2026-10-26

### Added

- Holding a turret next to a turret base where it cannot be placed shows a short reason next to the red outline (block in the way, wrong side, base tier too low, base full, too many of this turret), as on 1.20.1+
- Config option `turrets.dropTurretsOverMaxSimultaneous` (default false): attached turrets over their type's `maxSimultaneous` drop, e.g. after the limit was lowered (as on 1.20.1+)
- Turret bases, their turrets and OMPD's hardened blocks/walls/fences without an owner sparkle green, and WAILA/HWYLA and The One Probe show "Owner: nobody (right-click to claim)"; right-click claims them (OMLib), as on 1.20.1+
- The owner of a turret base can disown it ("Disown" in its configure screen, after a confirmation, as on 1.20.1+); the trust list is cleared and anyone can claim it
- While you hold OMLib's Ownership Wand, turrets still target and fire at you, but their shots, explosions and effects do not affect you (as on 1.20.1+)

### Changed

- A broken turret base scatters its items like a chest
- Only admins of a turret base can attach turrets to it (the placement label says why otherwise), as on 1.20.1+
- Hoppers and pipes can no longer take ammo out of a turret base, so nobody can disarm someone else's base with them (as on 1.20.1+)
- The jar files are named `openmodularturrets-<version>-mc1.12.2-forge.jar` (Minecraft version with `mc`, as on 1.20.1+)
- The server sends its config values (`openmodularturrets.cfg` and `openmodularturrets_addon_turrets.cfg`) to clients when they join, so tooltips, the ammo slots and the turret placement outline follow the server's rules; after leaving, the client uses its own config again
- Turret bases without an owner (e.g. placed by a machine or a structure) no longer drop themselves; they stay inert until claimed, unless `bases.unownedBasesActive` is set (as on 1.20.1+)
- Turrets can be attached to a turret base without an owner only by players who may use command blocks (operators in creative mode, mapmakers)
- Requires OMLib 4.0.0-beta.2 or newer

### Fixed

- A turret base placed without saved data (e.g. by `/setblock` or a structure) with turrets already attached no longer starts with range 0; it starts at the range of its shortest reaching turret (as on 1.20.1+)
- Turrets added by other mods could not fire slower than every 20 ticks (their config clamped `baseFireRate` to 20)
- Decimal settings of turrets added by other mods (damage amp, upgrades, recycler chances, accuracy deviation) were not limited to their range
- The config ranges of `baseFireRate` (now 1–200 ticks) and `baseAccuracyDeviation` (0–100 %) did not include the turrets' default values
- Boxes in the turret logic compared `0.0` and `-0.0` as equal while hashing them differently
- Computers (ComputerCraft, OpenComputers) could not change access levels: `changeAccessLevel` / `changeTrustedPlayerAccess` and `addTrustedPlayer` with a level reported success but changed nothing
- OpenComputers: `setTurretYawPitch` rejected valid angles and used the side as yaw, `forceShootTurret` did not check the side, side 6 counted as the bottom, `addTrustedPlayer` without an access level failed, `getTrustedPlayer` returned nothing for unknown players
- Turret base GUI actions (range, mode, targeting, dropping) were handled for unloaded or far-away bases, which could load chunks; they now need the base loaded and the player within reach
- Tooltips and the turret placement outline used the client's own config instead of the server's
- Other mods asking a turret without base (or with its base in an unloaded chunk) for its stats crashed; such a turret now reports its configured values and targets nothing
- The maximum range shown in the turret base GUI also counted turrets mounted on a neighbouring base
- Turret bases could create their tile entity on the chunk render thread (camouflage light settings), which can crash or corrupt the world
- Cranks and generators did not turn with rotated or mirrored structures

## [4.0.0-beta.1] - 2026-10-24

### Changed

- Requires OMLib 4.0.0-beta.1 or newer

### Fixed

- The melee and crossbow turrets had no name (and the arc turret none in German, Portuguese, Russian and Chinese)

## [4.0.0-alpha.2] - 2026-10-23

### Changed

- License: the source code is now MIT (was GPLv3) and the art CC-BY-NC-SA 4.0 (see `LICENSE`, `LICENSE-ART`, `LICENSE-DUAL`)
- The `turretBreakable` config option makes turrets mineable (hardness 3, pickaxe, as on 1.20.1+; needs a restart); it had no effect before

### Fixed

- ComputerCraft: `getTrustedPlayer` added the queried player to the trust list when they were not on it
- ComputerCraft: `setTurretAutoForceFire` and `forceShootTurret` rejected every valid side
- ComputerCraft: number arguments (access levels, modes, sides) were rejected because Lua numbers arrive as decimals; missing arguments now answer "Wrong parameters!"
- WAILA/HWYLA crashed the server on a turret whose base was not loaded, and showed the accuracy deviation instead of the accuracy
- Disposable ammo projectiles were invisible

## [4.0.0-alpha.1] - 2026-10-23

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

## 3.2.0

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
