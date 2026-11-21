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

### Prerequisites

- **Java 25 or newer** to run Gradle (a Fabric Loom 1.18 requirement): set `JAVA_HOME`, or the Gradle JVM in your IDE, to it.
  The wrapper brings Gradle 9.8.1.
- The **Java 17** (1.20.1 nodes) and **Java 21** (1.21.1 and 1.21.11 nodes) compile toolchains.
  Gradle uses installed JDKs of any vendor and downloads the missing ones (foojay toolchain resolver).
- For the tests of a local OpenModularCore (see below), also a **Java 8** toolchain; Gradle downloads it too if it is missing.
- **Git**, and a POSIX `sh` with `find`, `sort`, `xargs` and `sha256sum` on the `PATH`: the build of the `omlib` submodule (see below) runs through `omlib/tools/bootstrap.sh`.
- An IDE with Gradle support (IntelliJ IDEA is recommended).
  The IDE compiles and highlights the source as the active node, `1.21.1-fabric`.

### Getting the code

```bash
git clone -b 1.20.1+ --recurse-submodules git@github.com:OpenModularTurretsTeam/OpenModularTurrets.git
# or over HTTPS: git clone -b 1.20.1+ --recurse-submodules https://github.com/OpenModularTurretsTeam/OpenModularTurrets.git
cd OpenModularTurrets
git submodule update --init   # in an existing clone without the submodule
git pull --recurse-submodules # update the clone; or: git pull && git submodule update
```

After every pull, update the submodule too, or the build uses the OMLib commit you had before.

The library [OMLib](https://github.com/OpenModularTurretsTeam/OMLib) is the git submodule `omlib`, on OMLib's branch `1.20.1+`.
Its URL is relative (`../OMLib.git`), so git looks for OMLib next to the repository you cloned from.
If you clone your fork and have no fork of OMLib, clone it without `--recurse-submodules`, then point the submodule at the team's repository and fetch it:

```bash
git clone -b 1.20.1+ git@github.com:<you>/OpenModularTurrets.git
cd OpenModularTurrets
git config submodule.omlib.url https://github.com/OpenModularTurretsTeam/OMLib.git
git submodule update --init
```

### Building

```bash
./gradlew chiseledBuild          # build every node (1.20.1-fabric, 1.20.1-forge, 1.21.1-fabric, 1.21.1-neoforge, 1.21.11-fabric, 1.21.11-neoforge)
./gradlew :1.21.1-neoforge:build # build one node
```

The jars are in `versions/<node>/build/libs/`, named `openmodularturrets-<mod_version>-mc<minecraft version>-<loader>.jar`; each one bundles OMLib and the core classes.

Before Gradle configures anything, the settings script builds `omlib` for all six nodes into `omlib/build/maven`, which the mod compiles against.
This takes a while the first time.
Afterwards it is a no-op as long as the library's sources (and the local core, see below) are unchanged: a hash of them is kept in `omlib/build/maven/.stamp`.
Set the environment variable `OMLIB_SKIP_BUILD` (any value) to skip that step; the jars in `omlib/build/maven` must then already be there and current.

### Changing OMLib

The mod only ever uses the library from its own `omlib` submodule, never from another OMLib clone.
To try an OMLib change here before it is pushed:

- **Quick experiment:** edit the files in `omlib/` directly.
  The next Gradle run sees the changed sources, rebuilds the library and drops Loom's cached copy of the old one.
- **Change you want to keep:** commit it in your own clone of OMLib (branch `1.20.1+`), then move the submodule to that commit, straight from the local clone:

  ```bash
  git -C omlib fetch ../../OMLib my-branch  # path of your OMLib clone, relative to omlib/
  git -C omlib checkout --detach FETCH_HEAD
  ./gradlew chiseledBuild                   # rebuilds the library, then the mod
  ```

The library's own build and tests run in the OMLib clone (`./gradlew chiseledBuild`, `./gradlew check` there).
For a pull request, push the OMLib change first (a pull request to OMLib), then commit the new `omlib` pointer here.
CI checks out the submodule commit this branch records, so that commit must be on GitHub.
Don't leave the only copy of a change in `omlib/`: this repository records only the submodule's commit.

### Working on the shared core locally

The game rules (`omtteam.openmodularturrets.core`) come from [OpenModularCore](https://github.com/OpenModularTurretsTeam/OpenModularCore), module `omt-core`, in the version `core_version` from `gradle.properties`.
By default Gradle downloads that version from JitPack.
To build against your own core sources instead, clone OpenModularCore next to this repository's folder:

```bash
cd ..                    # the folder that holds OpenModularTurrets/
git clone git@github.com:OpenModularTurretsTeam/OpenModularCore.git
# or over HTTPS: git clone https://github.com/OpenModularTurretsTeam/OpenModularCore.git
git -C OpenModularCore checkout 2.0.0 # the tag named by core_version
# parent/
#   OpenModularCore/
#   OpenModularTurrets/
```

A fresh clone is on the core's `main` branch, which can be ahead of the release the mod is built against.
To build against the same core as CI, check out the tag that `core_version` names, as above; stay on `main` or your own branch only while you change the core.
For work on the mod alone, `OMM_NO_LOCAL_CORE=1` (see below) uses the JitPack artifact instead.

The settings script looks for `OpenModularCore/settings.gradle.kts` in this repository's folder and then in every folder above it, and includes the first one it finds; so the folder must be named `OpenModularCore`.
The `omlib` build searches the same way, from `omlib/` upwards.
What changes when it is found:

- Gradle prints `OpenModularCore: local source at <path> (OMM_NO_LOCAL_CORE=1 for the JitPack artifact)` (hidden by `-q`).
  Without that line the build uses JitPack.
- The local core replaces the JitPack modules (its group is the JitPack group, its project names are the module names), so `core_version` has no effect and nothing needs to be published.
  An edit to `omt-core` is used by the next build of the mod.
- The library bundles `omlib-core`: an edit there (or in the core's build logic) changes the `omlib` hash, so the next Gradle run rebuilds the library for all six nodes.
  With `OMLIB_SKIP_BUILD` set, that edit does not reach the mod.

The core has its own tests; run them in its folder (`./gradlew check` there, a Java 17 and a Java 8 test run).
Files in this repository's `src` under the core package fail `./gradlew check` (`verifyNoCoreSources`): game rules go into OpenModularCore.

To build with the JitPack artifact of `core_version` even though a local core is there:

```bash
OMM_NO_LOCAL_CORE=1 ./gradlew chiseledBuild # environment variable: any value except empty or 0
./gradlew chiseledBuild -Pomm.noLocalCore   # Gradle property: any value except false
```

Switching between the local core and JitPack also rebuilds `omlib`, because the hash includes which core it uses.
CI checks out only this repository with its submodules, so it has no `OpenModularCore/` and always uses the JitPack artifact of `core_version`.
Run `OMM_NO_LOCAL_CORE=1 ./gradlew check` to see what CI will see.
A change here that needs a new core passes CI only once `core_version` names an OpenModularCore release that JitPack serves.

### Testing locally

```bash
./gradlew check                                # all nodes: unit tests, game tests, spotlessCheck, verifyNoCoreSources, checkRecipes
./gradlew :1.21.1-fabric:test                  # unit tests incl. the ArchUnit rules (Fabric nodes only)
./gradlew :1.21.1-fabric:runGameTest           # game tests on Fabric (also :1.20.1-fabric, :1.21.11-fabric)
./gradlew :1.21.1-neoforge:runGameTestServer   # game tests on NeoForge (also :1.21.11-neoforge)
./gradlew :1.20.1-forge:runGameTestServer      # game tests on Forge
./gradlew :1.21.1-fabric:runClient             # start a client (any node; :runServer for a dedicated server)
./gradlew :1.21.1-fabric:runDatagen            # regenerate recipes, loot tables, tags (run for all three Fabric nodes)
```

- **Unit tests** (`src/test`) run on the Fabric nodes; the NeoForge and Forge nodes leave them out.
- **Game tests** (`src/gametest`, the test mod `openmodularturrets_gametest`) run on a headless server on every node and are part of `check`.
  On Fabric they run in `versions/<node>/build/run/gameTest` with a new world every time.
  On NeoForge and Forge they run in `versions/<node>/run`, and the task also fails unless `run/logs/latest.log` there says the required tests passed.
  The 1.21.11 NeoForge node has no Sable, Create or Simulated game tests.
- **Client and server runs** load the mod without the test mod.
  The Fabric nodes share the run folder `run/` in the repository root; the NeoForge and Forge nodes each use `versions/<node>/run`.
  The NeoForge and Forge runs also load Jade; the 1.21.1 runs add Sable (Fabric: client only), and the 1.21.1 NeoForge runs Create and Create Aeronautics.
  On its first start a dedicated server writes `eula.txt` and stops: set `eula=true` there, and `online-mode=false` in `server.properties` to join with a dev client.
- **Datagen** runs on the Fabric nodes only and writes `versions/<node>/src/main/generated`, which is committed.
  The NeoForge and Forge nodes convert the Fabric output of the same Minecraft version (`datagen_from`) when they build.
- **Logs and reports:**
  - `run/logs/` and `run/crash-reports/` for the Fabric runs.
  - `versions/<node>/run/logs/` and `crash-reports/` for the NeoForge and Forge runs and game tests.
  - `versions/<node>/build/run/gameTest/logs/` for the Fabric game tests.
  - `versions/<node>/build/reports/` for the test reports, and on the active node the JaCoCo coverage (`jacoco/test/html/index.html`) and PMD (`pmd/main.html`, `pmd/client.html`) reports.
  - `versions/<node>/logs/` for unit tests that load game classes (Fabric nodes); tools started from the repository root write `logs/` there.

### Switching the active node

```bash
./gradlew "Set active project to 1.20.1-fabric" # switch the source to 1.20.1
./gradlew "Reset active project"                # back to 1.21.1-fabric, before you commit
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
- Game tests in `src/gametest` run on all loaders: test classes implement `OMTGameTests` (see [docs/architecture.md](docs/architecture.md)).
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
- Dependencies are limited to Fabric Loader, Fabric API and Team Reborn Energy (Fabric), NeoForge, Forge (with MixinExtras), the library OMLib, the shared core OpenModularCore, Sable Companion (1.21.1), and the optional Jade (all loaders) and Create (NeoForge 1.21.1, compile-only) integrations.
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
