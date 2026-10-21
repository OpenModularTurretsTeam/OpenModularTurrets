# OpenModularTurrets

Repo for [OpenModularTurrets](https://minecraft.curseforge.com/projects/openmodularturrets) for Minecraft.

Build turret bases, mount turrets on any face of them (the number depends on the tier) and keep your home safe from mobs and unwanted visitors, whether you're online or offline.
Open Modular Turrets has long been a popular choice in PvP modpacks, and it is now available on Fabric for Minecraft 1.20.1 and 1.21.1.

## Forge/Neoforge?

You can use the OMT fabric mod on Neoforge/Forge using Sinytra Connector, Connector Extras and ForgifiedFabricApi.
Find them here: [Sinytra Connector](https://www.curseforge.com/minecraft/mc-mods/sinytra-connector) [Connector Extras](https://www.curseforge.com/minecraft/mc-mods/connector-extras) [Forgified Fabric API](https://www.curseforge.com/minecraft/mc-mods/forgified-fabric-api)

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
- **Generator:** burns furnace fuel and powers adjacent turret bases
- Uses **Team Reborn Energy**, so it works with energy from other Fabric tech mods
- Optional **Jade** support for turret bases and turrets

## Requirements

- Minecraft 1.20.1 or 1.21.1
- Fabric Loader and Fabric API or
- For Neoforge/Forge:
  - (Neo)forge + Sinytra Connector + Connector Extras + Forgified Fabric API

## Pre-Release

This is an early release of the Fabric port.
Expect bugs, and please report them.

## Building

The project uses Stonecutter to build 1.20.1 and 1.21.1 from one source tree (Gradle must run on Java 25 or newer).

```bash
./gradlew chiseledBuild # build both versions
./gradlew check         # unit tests, architecture rules, game tests, code quality report
./gradlew :1.21.1-fabric:runClient
```

See [docs/architecture.md](docs/architecture.md) for how the code is structured and how to add things, [CLAUDE.md](CLAUDE.md) for the conventions, and [CHANGELOG.md](CHANGELOG.md) for changes.

## License

The projects source code is licensed under the [MIT license](LICENSE), the projects art is licenced under the [CC-BY-NC-SA 4.0 license](LICENSE-ART)
