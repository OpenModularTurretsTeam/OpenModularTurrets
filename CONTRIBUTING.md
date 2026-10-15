# Contributing to OpenModularTurrets

Thanks for helping out! This branch (`1.20.1+`) is the Fabric port of OpenModularTurrets and Open Modular Passive
Defence from Minecraft 1.12.2. It targets **1.20.1 and 1.21.1 from a single codebase** using
[Stonecutter](https://stonecutter.kikugie.dev/).

Questions are welcome on our [Discord](https://discord.gg/SEG695u).

## Reporting issues

Use the [issue templates](https://github.com/OpenModularTurretsTeam/OpenModularTurrets/issues/new/choose):

- **Bug report** for crashes and broken behaviour. Include the Minecraft, mod, Fabric Loader and Fabric API versions,
  and your log or crash report.
- **Feature request** for new ideas. The port comes first, so new features may take a while.

## Development setup

You need:

- **Java 25 or newer** to run Gradle (a Fabric Loom 1.18 requirement). The Java 17 and 21 compile toolchains are
  downloaded automatically if they are missing.
- Git and an IDE with Gradle support (IntelliJ IDEA is recommended).


###  Building the mods for both 1.20.1 and 1.21.1

```bash
git clone -b 1.20.1+ git@github.com:OpenModularTurretsTeam/OpenModularTurrets.git
cd OpenModularTurrets
./gradlew chiseledBuild
```

### Useful commands

```bash
./gradlew chiseledBuild                    # build both versions
./gradlew :1.21.1:runClient                # start a client (also :1.20.1:runClient, :runServer)
./gradlew check                            # unit tests + core isolation check
./gradlew :1.21.1:runDatagen               # regenerate recipes, loot tables, tags (run for both versions)
./gradlew "Set active project to 1.20.1"   # switch the source to 1.20.1
./gradlew "Set active project to 1.21.1"   # switch back
```

## Working with two Minecraft versions

- The committed source is always in the **1.21.1** state. If you switched to 1.20.1, switch back before you commit.
- Version-specific code uses Stonecutter comments:

  ```java
  //? if >=1.21 {
  ResourceLocation id = ResourceLocation.fromNamespaceAndPath(MOD_ID, path);
  //?} else {
  /*ResourceLocation id = new ResourceLocation(MOD_ID, path);
  *///?}
  ```

  Keep these blocks out of gameplay code. Put version differences behind small helpers in
  `omtteam.openmodularturrets.compat` instead.
- `assets/` (textures, models, blockstates, lang, sounds) are shared and written by hand in `src/main/resources`.
- `data/` (recipes, loot tables, tags) is **generated** with Fabric Datagen into `versions/<version>/src/main/generated`.
  Don't write data files by hand. Change the datagen providers, run datagen for both versions and commit the output.

## Code layout

- `src/main/java` holds common and server code. `src/client/java` holds client-only code (renderers, screens, models).
  Never reference client classes from `main`.
- `omtteam.openmodularturrets.core` holds the game logic (targeting, ammo, upgrades, trusted players). It must not
  import Minecraft, Fabric, Mojang or Team Reborn Energy classes; `./gradlew check` fails if it does. Unit tests for it
  live in `src/test/java`.

## Code style

- Java only, 4-space indent, no wildcard imports.
- The project uses spotless for code styling
- Mojang mappings (`BlockEntity`, `ResourceLocation`, `AbstractContainerMenu`, …).
- Registry IDs and resource names are lowercase `snake_case`.
- All user-facing text goes through translation keys (`block.openmodularturrets.…`, `gui.openmodularturrets.…`,
  `tooltip.openmodularturrets.…`) in `en_us.json`.
- Gameplay logic runs on the server. Every client-to-server packet must check permissions again on the server.
- Add a short Javadoc to any non-obvious formula, noting where it came from in the old mod.
- Dependencies are limited to Fabric Loader, Fabric API and Team Reborn Energy. Ask before adding anything else.

## Pull requests

1. Branch off `1.20.1+` and keep each pull request focused on one change.
2. Add a one-line entry for every user-visible change under `## [Unreleased]` in `CHANGELOG.md`, by hand or with:

   ```bash
   ./gradlew changelogEntry --type=fixed --entry="Rocket turrets no longer target trusted players"
   ```

   Types are `added`, `changed`, `fixed` and `removed`.
3. Make sure `./gradlew chiseledBuild`, `./gradlew check` and `./gradlew spotlessCheck` pass and the source is back in the 1.21.1 state.
4. If spotlessCheck fails you can use `./gradlew spotlessApply` to automatically apply code styling.
5. If you can, test in game on both versions, and on a dedicated server for anything involving networking or GUIs.
6. Describe what you changed and how you tested it in the pull request.

## License

By contributing, you agree that your code is licensed under the [MIT license](LICENSE) and your art (textures,
models, sounds) under [CC BY-NC 4.0](LICENSE-ART).
You also agree to the restriction from the [CLA](CLA.md).
