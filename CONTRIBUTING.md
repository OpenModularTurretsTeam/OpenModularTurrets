# Contributing to OpenModularTurrets

Thanks for helping out!
This branch (`1.20.1+`) holds OpenModularTurrets 4.x, the port of OpenModularTurrets and Open Modular Passive Defence from Minecraft 1.12.2.
It targets **Fabric on 1.20.1, 1.21.1 and 1.21.11, NeoForge on 1.21.1 and 1.21.11 and Forge on 1.20.1 from a single codebase** using [Stonecutter](https://stonecutter.kikugie.dev/).

Questions are welcome on our [Discord](https://discord.gg/SEG695u).

## Reporting issues

Use the [issue templates](https://github.com/OpenModularTurretsTeam/OpenModularTurrets/issues/new/choose):

- **Bug report** for crashes and broken behaviour.
  Include the Minecraft and mod versions, your loader (Fabric Loader and Fabric API, NeoForge or Forge) with its version, and your log or crash report.
- **Feature request** for new ideas.

## Development setup

You need:

- **Java 25 or newer** to run Gradle (a Fabric Loom 1.18 requirement).
  The Java 17 and 21 compile toolchains are downloaded automatically if they are missing.
- Git and an IDE with Gradle support (IntelliJ IDEA is recommended).

### Building the mod for all versions and loaders

```bash
git clone -b 1.20.1+ --recurse-submodules git@github.com:OpenModularTurretsTeam/OpenModularTurrets.git
cd OpenModularTurrets
./gradlew chiseledBuild
```

The library [OMLib](https://github.com/OpenModularTurretsTeam/OMLib) is the git submodule `omlib` (hence `--recurse-submodules`, or `git submodule update --init` in an existing clone).
The first Gradle run builds it for all versions and loaders into `omlib/build/maven`, and each jar bundles it; this takes a while the first time and is a no-op while the library is unchanged.
To change the library, change and push it in its own clone of the OMLib repository (a pull request there), then move the `omlib` submodule here to that commit and commit the new pointer; never leave changes inside `omlib/` itself, the next checkout drops them.

### Useful commands

```bash
./gradlew chiseledBuild                         # build every node (1.20.1-fabric, 1.20.1-forge, 1.21.1-fabric, 1.21.1-neoforge, 1.21.11-fabric, 1.21.11-neoforge)
./gradlew :1.21.1-fabric:runClient              # start a client (also :1.20.1-fabric:runClient, :runServer)
./gradlew :1.21.1-neoforge:runClient            # start a NeoForge client (also :runServer, and the 1.21.11 nodes)
./gradlew :1.20.1-forge:runClient               # start a Forge client (also :runServer)
./gradlew check                                 # unit tests, game tests, recipe check
./gradlew :1.21.1-fabric:runDatagen             # regenerate recipes, loot tables, tags (run for all three Fabric nodes)
./gradlew "Set active project to 1.20.1-fabric" # switch the source to 1.20.1
./gradlew "Set active project to 1.21.1-fabric" # switch back
```

## Working with three Minecraft versions and three loaders

- Every Stonecutter node is `<minecraft version>-<loader>`, built by `build.<loader>.gradle.kts`.
- The committed source is always in the **1.21.1-fabric** state.
  If you switched to another node, switch back before you commit.
- Version-specific code uses Stonecutter comments:

  ```java
  //? if >=1.21 {
  ResourceLocation id = ResourceLocation.fromNamespaceAndPath(MOD_ID, path);
  //?} else {
  /*ResourceLocation id = new ResourceLocation(MOD_ID, path);
  *///?}
  ```

  Keep these blocks out of gameplay code.
  Put version differences behind small helpers in `omtteam.openmodularturrets.compat` instead.
- Loader-specific classes go into `platform/fabric/`, `platform/neoforge/` or `platform/forge/` (each is only compiled for its loader).
  Shared code reaches the loader through the services in `platform.Platform`; small differences in `compat/` and `platform/` use `//? if fabric {` / `//? if neoforge {` / `//? if forge {`.
  Fabric API and Team Reborn Energy may only be used in `platform/fabric/` and `datagen`, or the NeoForge and Forge builds break.
- Game tests in `src/gametest` run on all loaders: test classes implement `OMTGameTests` (see `CLAUDE.md`, "Game tests").
- `assets/` (textures, models, blockstates, lang, sounds) are shared and written by hand in `src/main/resources`.
- `data/` (recipes, loot tables, tags) is **generated** with Fabric Datagen into `versions/<node>/src/main/generated`; the NeoForge and Forge builds convert the Fabric output of the same Minecraft version.
  Don't write data files by hand.
  Change the datagen providers, run datagen for all three Fabric nodes and commit the output.

## Code layout

- `src/main/java` holds common and server code.
  `src/client/java` holds client-only code (renderers, screens, models).
  Never reference client classes from `main`.
- `omtteam.openmodularturrets.core` holds the game logic (targeting, ammo, upgrades), plain Java without Minecraft.
  It is not in this repository: it comes from [OpenModularCore](https://github.com/OpenModularTurretsTeam/OpenModularCore) (module `omt-core`, version `core_version` in `gradle.properties`), the same on every Minecraft version, and the build bundles it into the jar.
  Changes to the game rules and their unit tests go there; `./gradlew check` fails if `src` has files in the core package.

## Code style

- Java only, 4-space indent, no wildcard imports.
- The project uses Spotless for code styling.
- Mojang mappings (`BlockEntity`, `ResourceLocation`, `AbstractContainerMenu`, …).
- Registry IDs and resource names are lowercase `snake_case`.
- All user-facing text goes through translation keys (`block.openmodularturrets.…`, `gui.openmodularturrets.…`, `tooltip.openmodularturrets.…`) in `en_us.json`.
- Gameplay logic runs on the server.
  Every client-to-server packet must check permissions again on the server.
- Add a short Javadoc to any non-obvious formula, noting where it came from in the old mod.
- Dependencies are limited to Fabric Loader, Fabric API and Team Reborn Energy (Fabric), NeoForge, Forge (with MixinExtras), the library OMLib, the shared core OpenModularCore, Sable Companion (1.21.1) and the optional Jade integration.
  Ask before adding anything else.

## Pull requests

1. Branch off `1.20.1+` and keep each pull request focused on one change.
2. Add a one-line entry for every user-visible change under `## [Unreleased]` in `CHANGELOG.md`, by hand or with:

   ```bash
   ./gradlew changelogEntry --type=fixed --entry="Rocket turrets no longer target trusted players"
   ```

   Types are `added`, `changed`, `fixed` and `removed`.
3. Make sure `./gradlew chiseledBuild`, `./gradlew check` and `./gradlew spotlessCheck` pass and the source is back in the 1.21.1-fabric state.
4. If spotlessCheck fails you can use `./gradlew spotlessApply` to automatically apply code styling.
5. If you can, test in game on every version and loader your change touches, and on a dedicated server for anything involving networking or GUIs.
6. Describe what you changed and how you tested it in the pull request.

## License

By contributing, you agree that your code is licensed under the [MIT license](LICENSE) and your art (textures, models, sounds) under [CC BY-NC-SA 4.0](LICENSE-ART), as [LICENSE-DUAL](LICENSE-DUAL) describes.
You also agree to the restriction from the [CLA](CLA.md).
