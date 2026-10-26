# OpenModularTurrets

Repo for [OpenModularTurrets](https://minecraft.curseforge.com/projects/openmodularturrets) for Minecraft.

Build turret bases, mount turrets on any face of them (the number depends on the tier) and keep your home safe from mobs and unwanted visitors, whether you're online or offline.
Open Modular Turrets has long been a popular choice in PvP modpacks, and it is now available for Fabric 1.20.1, 1.21.1 and 1.21.11, and natively for NeoForge 1.21.1 and 1.21.11.

## Loaders

- **Fabric** for Minecraft 1.20.1, 1.21.1 and 1.21.11 (Fabric Loader + Fabric API), published to CurseForge and Modrinth.
- **NeoForge** for Minecraft 1.21.1 and 1.21.11, built natively (no Sinytra Connector, Connector Extras or Forgified Fabric API needed).
  This is still a test build and is not yet published; build it yourself (see "Building" below).
- For Forge, or NeoForge on 1.20.1, run the Fabric mod through [Sinytra Connector](https://www.curseforge.com/minecraft/mc-mods/sinytra-connector), [Connector Extras](https://www.curseforge.com/minecraft/mc-mods/connector-extras) and [Forgified Fabric API](https://www.curseforge.com/minecraft/mc-mods/forgified-fabric-api).

## Features

- **14 turrets**, from the disposable item turret and potato cannon to machine guns, grenade and rocket launchers, lasers, railguns and teleporter turrets
- **Turret bases in 5 tiers** that handle targeting, ammo and energy for every turret mounted on them
- **Choose your targets:** hostile mobs, neutral mobs, players, or any combination
- **Trusted players** with access levels, so friends can use your base without being shot (scoreboard team members are never targeted)
- **Camouflage:** disguise turret bases and camouflage traps as any full block, opaque or transparent, with biome-blended colours.
  Tier 4 and 5 bases and the trap can also set their light value and light opacity
- **Memory card:** copy the settings of a base or camouflage trap (targeting, trusted players, disguise, light) and paste them onto another one, limited to players with admin access
- **Upgrades and addons:** range, fire rate, accuracy, efficiency, scattershot, damage amplifier, solar panel, redstone reactor, concealer and more
- **Passive defenses** from Open Modular Passive Defense: hardened blocks, walls, fences and the camouflage trap, which only trusted players can walk through
- **Generator:** burns furnace fuel (9 slots) and powers adjacent turret bases and other machines; an upgrade slot multiplies production and fuel use together
- **Turret API for other mods:** add turrets with their own weapons, ammo, models and custom hit logic; see [docs/turret-api.md](docs/turret-api.md)
- Uses **Team Reborn Energy** on Fabric, so it works with energy from other Fabric tech mods; the NeoForge build uses its energy capability instead
- Optional **Jade** support for turret bases and turrets, on both loaders

## Requirements

- Minecraft 1.20.1, 1.21.1 or 1.21.11
- Fabric: Fabric Loader and Fabric API
- NeoForge (1.21.1 and 1.21.11): NeoForge 21.1.x or 21.11.x, no extra mods needed
- For Forge, or NeoForge on 1.20.1: (Neo)Forge + Sinytra Connector + Connector Extras + Forgified Fabric API

## Pre-Release

This mod is in beta.
Expect bugs, and please report them.
The NeoForge build is newer still (native port, not yet published) and wants extra testing.

## Building

The project uses Stonecutter to build Fabric 1.20.1, 1.21.1 and 1.21.11, NeoForge 1.21.1 and 1.21.11 and Forge 1.20.1 from one source tree (Gradle must run on Java 25 or newer).

```bash
./gradlew chiseledBuild # build all nodes
./gradlew check         # unit tests, architecture rules, game tests, code quality report
./gradlew :1.21.1-fabric:runClient
./gradlew :1.21.1-neoforge:runClient
./gradlew :1.21.11-fabric:runClient
./gradlew :1.21.11-neoforge:runClient
```

See [docs/architecture.md](docs/architecture.md) for how the code is structured and how to add things, [CLAUDE.md](CLAUDE.md) for the conventions, and [CHANGELOG.md](CHANGELOG.md) for changes.

## License

The projects source code is licensed under the [MIT license](LICENSE), the projects art is licenced under the [CC-BY-NC-SA 4.0 license](LICENSE-ART)
