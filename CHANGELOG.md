# Changelog

All notable changes to Open Modular Turrets (Fabric, NeoForge and Forge).
The format follows [Keep a Changelog](https://keepachangelog.com/en/1.1.0/).

Add every user-visible change as a one-line entry under **Unreleased** (sections: Added, Changed, Fixed, Removed), by hand or with `./gradlew changelogEntry --type=added --entry="…"`.
Before a release, `./gradlew releaseChangelog` turns *Unreleased* into the release section; `chiseledPublish` uploads exactly that section.

## [Unreleased]

### Added

- Minecraft 1.21.11 for Fabric and NeoForge support

### Changed

- The mod bundles the new library OMLib (jar-in-jar), which now holds the code it shares with Open Modular Lighting; nothing to install separately
- The camouflage menu has a dark grey background like the turret base GUI instead of the vanilla light grey
- The generator GUI labels are black like in the other turret GUIs (were dark grey on the dark background)

### Fixed

- Camouflaged turret bases and traps no longer crash the client on Fabric 1.20.1 when no Fabric Renderer API implementation is installed (Sodium without Indium); they show their own look then
- A turret base placed without saved data (e.g. by `/setblock` or a structure) with turrets already attached no longer starts with range 0; it starts at the range of its shortest reaching turret
- The base configure screen dims the world behind it like the base and camouflage GUIs (it was blurred before on 1.21 and later)
- The info panel left of the base GUI no longer disappears while a button tooltip is shown (Minecraft 1.21.11)
- The mouse stays where it was when you move between the base, configure, trusted players and camouflage GUIs instead of jumping to the screen centre

## [4.0.0-beta.9] - 2026-10-25

### Added

- Right-clicking a turret base, turret, camouflage trap or hardened block/wall/fence without an owner claims it and the unowned blocks connected to it (up to `bases.claimSpreadLimit`, default 1024); machines' fake players only claim if `general.fakePlayersCanClaim` is set
- Ownership Wand (creative, operators only) for mapmakers: right-click makes you the owner of an owned block, sneak-right-click removes its owner; it spreads to the connected blocks of the same owner unless switched off with a sneak-right-click into the air
- While you hold the Ownership Wand, turrets still target and fire at you, but their shots, explosions and effects do not affect you
- The owner of a turret base (configure screen) or camouflage trap can disown it after a confirmation; the trust list is cleared and anyone can claim it
- Turret bases, their turrets and hardened blocks/walls/fences without an owner sparkle green, and Jade shows "Owner: nobody (right-click to claim)" (also for hardened blocks, walls and fences, which now show their owner in Jade)

### Changed

- Releases on CurseForge and Modrinth are titled like the jar file (`openmodularturrets-<version>-mc<minecraft version>-<loader>`)
- Only admins of a turret base can attach turrets to it (the placement outline says why otherwise)
- Hoppers and pipes can no longer take ammo out of a turret base, so nobody can disarm someone else's base with them
- Turret bases without an owner (e.g. placed by a machine or a structure) no longer drop themselves; they stay inert, unless `bases.unownedBasesActive` is set
- Turrets can be attached to a turret base without an owner only by players who may use game master blocks (operators, mapmakers)

### Fixed

- Adding a trusted player by a name the server does not know no longer freezes the server while the name is looked up at Mojang
- "Drop turrets" only breaks blocks that are still turrets

## [4.0.0-beta.8] - 2026-10-25

### Added

- Sable contraptions (1.21.1): turret bases and turrets on a contraption find, aim at and shoot targets in the world around it; projectiles take the contraption's speed with them
- Create contraptions and Create Aeronautics' Physics Assembler (NeoForge 1.21.1) can move turret bases and turrets; config option `bases.allowContraptionMovement` (default true)

### Changed

- Jar files are named `openmodularturrets-<version>-mc<minecraft version>-<loader>.jar` (`-mc` instead of the `+` before the Minecraft version)

### Fixed

- Turret bases and generators no longer drop a second copy of their items when they are moved or replaced without breaking (`/setblock`, mods that move blocks)
- Turrets, cranks and generators turn with rotated or mirrored structures and moved blocks, so turrets and cranks stay attached to their base

## [4.0.0-beta.7] - 2026-10-24

### Added

- Holding a turret while looking at a block next to a turret base outlines where it would go: cyan if it can be placed there, red with a short reason if not (as in 1.12.2)
- Config option `turrets.dropTurretsOverMaxSimultaneous` (default false): attached turrets over their type's `maxSimultaneous` drop, e.g. after the limit was lowered

## [4.0.0-beta.6] - 2026-10-24

### Added

- NeoForge build for Minecraft 1.21.1
- Forge build for Minecraft 1.20.1

### Changed

- Hardened blocks can be used as camouflage now (other blocks with a block entity still cannot)

## [4.0.0-beta.5] - 2026-10-21

### Added

- Turret API for other mods: they can add turrets (with their own weapons, ammo, models and config entries under `turrets.addon_turrets`) through the `openmodularturrets` entrypoint, see `docs/turret-api.md`
- Turret API: custom turret logic. Self-targeting weapons find their own targets instead of using the base's, projectile and ray weapons can run code when they hit an entity, and `OMTApi.canDamage` / `OMTApi.hurt` apply the base's trust and damage rules
- The crank plays its windup sound when it charges a tier 1 base
- Turret tooltips (with shift): what the turret does, the base tier it needs, its ammo and its energy per shot
- Energy Multiplier Upgrade for the Generator (up to 4 in its new upgrade slot): each doubles energy production and fuel use; the energy per fuel item stays the same
- Upgrade tooltips show which machine the upgrade fits into (turret base or generator)
- Turret API on NeoForge: mods mark their plugins with `@OMTPlugin.Register` / `@OMTClientPlugin.Register` instead of the Fabric entrypoints, see `docs/turret-api.md`

### Changed

- German translation completed (missing turret names, death messages, trust list messages and tooltips)
- English (UK) translation completed (73 entries were missing and showed the American text); fixed its broken Turret Limit label
- The Relativistic Turret is now called Slowing Turret in all languages
- The Slowing Turret's sound event is renamed from `openmodularturrets:relativistic` to `openmodularturrets:slowing` (resource packs replacing it need the new name)
- The Rail Gun Turret ignores Scatter Shot upgrades (no extra rays, ammo, energy or scatter); with them, it dealt a multiple of its damage, since it barely scatters and every ray hit. Its tooltip and the upgrade's details say so
- The Generator has 9 fuel slots instead of 1, so it runs much longer without refuelling
- Generator: energy storage raised from 50,000 to 1,000,000 RF and output limit from 200 to 65,536 RF/t; existing config files still on the old defaults are updated, other values stay

### Fixed

- The Melee, Slowing and Teleporter Turrets no longer use more energy with Scatter Shot upgrades installed; they act once per shot, so the upgrades only multiplied their energy use. Their tooltips and the upgrade's details say so now.
- The `turretBreakable` config option did nothing; turrets can now be broken with a pickaxe when it is enabled
- Turret projectiles that flew into a chunk without entity ticking froze there; they remove themselves now, as in 1.12.2
- On the very first start (no config file yet), the disposable ammo list and the entity black and force lists were ignored until the next restart
- The Redstone Reactor addon now burns a redstone block or dust when its energy fits the free storage exactly; before, it needed more room than that
- The mod metadata named the wrong license (GPL-3.0); it now says MIT for the code and CC BY-NC-SA 4.0 for the art, and the jar contains all three license files

## [4.0.0-beta.4] - 2026-10-17

### Added

- Turret bases and camouflage traps can be camouflaged as any full block (opaque or transparent) through a new camouflage menu with a disguise slot; tier 4 and 5 bases and the camouflage trap also get light value and light opacity sliders
- Turret memory card (craftable): sneak + right-click a turret base or camouflage trap (admin access) to store its settings, right-click another one to write them. Targeting, mode, trust list, disguise and light are stored (traps: trust list, disguise and light); the tooltip lists them while shift is held
- Camouflage traps have a trusted player list now: the owner and trusted players can stand on a trap, its admins open its menus and manage the list (new "Trusted" button in the camouflage menu; levels are "trusted" and "admin")
- Jade tooltips for turret bases (mode, active, owner) and turrets (active, owner and, for players who may open the base: ammo, damage boost, accuracy, rate of fire)

### Fixed

- Arc Turret now chains a bit farther and bounces up to two times. It was only bouncing once before.

### Changed

- The camouflage of the camouflage trap is drawn like a real block now: ambient occlusion, transparent blocks and biome-blended grass and leaf colours
- Setting the camouflage of the camouflage trap uses the new camouflage menu (right-click it as an admin) instead of right-clicking with a block

## [4.0.0-beta.3] - 2026-10-15

### Added

- The potato cannon turret can now also fire poisonous potatoes

### Fixed

- Projectile turrets now lead the target with the real flight time (air drag) and raise the aim by the projectile drop, so slow projectiles no longer hit below the target
- Projectile turrets aim at the target's eyes instead of its body when a ledge hides the body, so shots no longer hit the block in front of it
- Potato cannon and disposable turret projectiles are now visible right at the turret instead of a few blocks away
- Potato cannon and disposable turret projectiles show a potato or cobblestone when fired without ammo (creative upgrade)

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
