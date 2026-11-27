# OpenModularTurrets

Repo for [OpenModularTurrets](https://minecraft.curseforge.com/projects/openmodularturrets) for Minecraft.

Build turret bases, mount turrets on any face of them (the number depends on the tier) and keep your home safe from mobs and unwanted visitors, whether you're online or offline.
Open Modular Turrets has long been a popular choice in PvP modpacks, and it is now available for Minecraft 1.20.1 on Fabric and, natively, on Forge.

## Loaders

- **Fabric** for Minecraft 1.20.1 (Fabric Loader + Fabric API), published to CurseForge and Modrinth.
- **Forge** for Minecraft 1.20.1, built natively, published to CurseForge and Modrinth.

## Branches and Minecraft versions

This branch (`1.20.1`) holds the code for Minecraft 1.20.1.
The other Minecraft versions have their own branches:

- `1.12`: Minecraft 1.12.2 (Forge)
- `1.20.1`: Minecraft 1.20.1 (Fabric and Forge)
- `1.21.1`: Minecraft 1.21.1 (Fabric and NeoForge)
- `1.21.11`: Minecraft 1.21.11 (Fabric and NeoForge)

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
- Uses **Team Reborn Energy** on Fabric, so it works with energy from other Fabric tech mods; the Forge build uses its energy capability (FE) instead
- Optional **NEEPMeat** support (Fabric): vascular conduits power turret bases, 1 eJ per energy unit
- Optional **Jade** support for turret bases and turrets, on both loaders
- Computer control of turret bases (a Serial Port addon, or tier 5): a computer can aim and fire the turrets itself
- Optional **CC: Tweaked** support (both loaders): turret bases are the peripheral `turret_base`, with the same functions as 1.12.2's computer commands, so a computer finds and controls a base with the Serial Port addon or at tier 5

## Requirements

- Minecraft 1.20.1
- Fabric: Fabric Loader and Fabric API
- Forge: Forge 47.x, no extra mods needed

## Reporting bugs

Version 4.0.0 is the first full release of the port.
If you find a bug, please report it on the [issue tracker](https://github.com/OpenModularTurretsTeam/OpenModularTurrets/issues).

## Building

The project uses Stonecutter to build Fabric and Forge for Minecraft 1.20.1 from one source tree.
Gradle must run on Java 25 or newer; the library OMLib is the git submodule `omlib`, so clone with `--recurse-submodules`.

```bash
git clone -b 1.20.1 --recurse-submodules https://github.com/OpenModularTurretsTeam/OpenModularTurrets.git
cd OpenModularTurrets
./gradlew chiseledBuild            # build both nodes, jars in versions/<node>/build/libs/
./gradlew check                    # unit tests, game tests, recipe and style checks
./gradlew :1.20.1-fabric:runClient # start a client (either node, also :runServer)
```

See [CONTRIBUTING.md](CONTRIBUTING.md) for the full development setup (changing OMLib, working on the shared core OpenModularCore locally, testing), [docs/architecture.md](docs/architecture.md) for how the code is structured and how to add things, and [CHANGELOG.md](CHANGELOG.md) for changes.

## License

The project's source code is licensed under the [MIT license](LICENSE), the project's art is licensed under the [CC-BY-NC-SA 4.0 license](LICENSE-ART)
