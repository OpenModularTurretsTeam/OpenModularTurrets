# Contributing to OpenModularTurrets

Thanks for helping out!
This branch (`1.12`) holds OpenModularTurrets 4.x for **Minecraft 1.12.2 with Forge**.
It is built with [RetroFuturaGradle](https://github.com/GTNewHorizons/RetroFuturaGradle), the maintained Forge 1.12.2 toolchain, on current Gradle.
The versions for Minecraft 1.20.1, 1.21.1 and 1.21.11 live on the branch `1.20.1+` and have their own `CONTRIBUTING.md`.

Questions are welcome on our [Discord](https://discord.gg/SEG695u).

## Reporting issues

Open an [issue](https://github.com/OpenModularTurretsTeam/OpenModularTurrets/issues) and fill in the template from [ISSUE_TEMPLATE.md](ISSUE_TEMPLATE.md).
Include the OMT, OMLib, Minecraft and Forge versions (not "latest"), whether it happens in single player or on a server, and your log or crash report.

## Development setup

You need:

- **Java 25** to run Gradle, because RetroFuturaGradle 2.0.6 is compiled for it.
  This repository does not pin a Gradle JVM, so `JAVA_HOME` (or the Gradle JVM setting of your IDE) has to point to a JDK 25.
- The mod is compiled, tested and run with a **Java 8 toolchain from Azul**.
  Gradle downloads it if it is missing (foojay toolchain resolver); a JDK 8 from another vendor does not count.
- Git and an IDE with Gradle support (IntelliJ IDEA is recommended).

The Gradle wrapper (Gradle 9.8.1) is part of the repository.

### Getting the code

Clone the repositories side by side into one parent folder:

```bash
mkdir omt-dev && cd omt-dev
git clone -b 1.12 https://github.com/OpenModularTurretsTeam/OpenModularTurrets.git
git clone -b 1.12 https://github.com/OpenModularTurretsTeam/OMLib.git
git clone https://github.com/OpenModularTurretsTeam/OpenModularCore.git   # optional: only to work on the core
```

This gives:

```
omt-dev/
├── OpenModularCore/      # optional
├── OpenModularTurrets/   # branch 1.12
└── OMLib/                # branch 1.12
```

The commands below run in `OpenModularTurrets/` unless they say otherwise.

### OMLib

OMT needs the library [OMLib](https://github.com/OpenModularTurretsTeam/OMLib) (branch `1.12`) to compile.
`build.gradle` takes it from one of three places:

- **Default:** the OMLib file from CurseForge named by `omlib_curse_file_id` in `gradle.properties`.
  That file is the old OMLib 3.2.0, which does not have the 4.x API that this code uses, so don't build with the default.
- **`-Pomlib_local`:** `omtteam:omlib:1.12.2-<omlib_version>:dev` from your local Maven repository (`~/.m2`), with `omlib_version` from `gradle.properties` (`4.0.1` today).
  Use this.
- **An `:OMLib` project in the same Gradle build** (for example a parent build that includes both): OMT then uses that project directly and needs neither of the above.

To put OMLib into your local Maven repository, publish your OMLib clone (branch `1.12`):

```bash
cd ../OMLib
./gradlew publishToMavenLocal   # builds and tests OMLib, then publishes omtteam:omlib:1.12.2-<its version> with the dev jar
cd ../OpenModularTurrets
```

Publish again after every change to OMLib you want to use here.
The publication has the same coordinates as the released OMLib of that version, so it replaces a released copy in `~/.m2`.
The published version must match `omlib_version`.
If OMLib's `mod_version` (and `release_type`) give a different version, pass it on the command line:

```bash
./gradlew build -Pomlib_local -Pomlib_version=4.0.2   # the version your OMLib clone published
```

### The shared core (OpenModularCore)

The game rules (`omtteam.openmodularturrets.core`: targeting, ammo, energy, upgrades, addons) are not in this repository.
They come from [OpenModularCore](https://github.com/OpenModularTurretsTeam/OpenModularCore), the same rules as on every other Minecraft version, downgraded to Java 8: module `omt-core-j8`, version `core_version` in `gradle.properties` (`2.0.0` today), from JitPack.
The build copies its classes into the OMT jars, so players still install one jar.
OMLib's part of the core (`omlib-core-j8`) comes with OMLib.

To work on the core and the mod together, clone OpenModularCore next to this repository, as in "Getting the code":

```bash
git clone https://github.com/OpenModularTurretsTeam/OpenModularCore.git   # in the parent folder, next to OpenModularTurrets/ and OMLib/
git -C OpenModularCore checkout 2.0.0                                      # optional: the released core_version instead of main
```

Without a checkout of the tag, the build uses the core's `main` branch, which may contain unreleased changes.

`settings.gradle` looks for a folder named `OpenModularCore` (with a `settings.gradle.kts`) in this folder and in every folder above it, and includes the first one it finds as a composite build.
Gradle then builds the core from that source instead of downloading `core_version` from JitPack, so a core change can be tested here before it is released.
The build says so at the start:

```
OpenModularCore: local source at <path> (OMM_NO_LOCAL_CORE=1 for the JitPack artifact)
```

Without that line the core comes from JitPack.
Every build that finds this checkout (OMLib's, this one, those of the other mods) builds the core into the checkout's own build folders.
Don't run two of them at the same time, or set `OMM_NO_LOCAL_CORE=1` for one of them.
To build against JitPack although a checkout is there (to check that your change works with the released core):

```bash
OMM_NO_LOCAL_CORE=1 ./gradlew build -Pomlib_local   # or pass -Pomm.noLocalCore
```

OMLib's core is in the OMLib dev jar too, so after a change to `omlib-core`, publish OMLib again (with the core checkout in place, OMLib's build picks it up the same way).
Changes to the game rules and their unit tests go into OpenModularCore; see its README.

### Useful commands

```bash
./gradlew build -Pomlib_local         # compile, run the unit tests, build the jars
./gradlew test -Pomlib_local          # unit tests only
./gradlew setupDecompWorkspace        # decompiled Minecraft and Forge sources for the IDE (build/rfg/minecraft-src)
./gradlew dependencyMap -Pomlib_local # interactive package map in build/reports/dependency-map/index.html
```

The jars are in `build/libs`:

- `openmodularturrets-<version>-mc1.12.2-forge.jar`: the jar players install (SRG names).
- `openmodularturrets-<version>-mc1.12.2-forge-dev.jar`: the same with MCP names, for development.
- `openmodularturrets-<version>-mc1.12.2-forge-sources.jar`.

## Testing locally

### Unit tests

`./gradlew test -Pomlib_local` runs the JUnit 5 tests in `src/test/java` on Java 8.
They cover the game layer (config and its migration, NBT, the data fixers for 3.x worlds, computer commands, turret damage) and the architecture rules in `ArchitectureTest` (ArchUnit, with the frozen violations in `src/test/resources/archunit_store`).
The core's own tests, coverage and mutation tests run in OpenModularCore, not here.
There are no game tests on 1.12.

### In game

RetroFuturaGradle gives this project `runClient` and `runServer`.
Both run in `run/` (ignored by git) as the player `Developer`.
In a standalone build OMLib is only on the compile and test classpaths, though, so these runs don't load it, and OMT requires OMLib.
Without it, `runClient` and `runServer` stop because OMLib is missing.

To run OMT on its own, put OMLib's release jar (SRG names) into the mods folder of the run; FML remaps jars in the mods folder to the dev names at runtime:

```bash
mkdir -p run/mods
cp ../OMLib/build/libs/omlib-4.0.1-mc1.12.2-forge.jar run/mods/   # OMLib's jar from its ./gradlew build or publishToMavenLocal
./gradlew runClient -Pomlib_local                                 # or runServer; both run in run/
```

Copy OMLib's jar again after every OMLib change.
Optional mods (JEI, The One Probe, ...) go into `run/mods` the same way, as their release jars.

The team also tests in game with OMT, OMLib, OpenModularPassiveDefense and OpenModularLighting together (with JEI and The One Probe), with a server boot check, and loads old 3.x worlds to check that they still work.
It also checks that every item has a recipe; items without one on purpose are listed with a reason in [recipe-exempt.txt](recipe-exempt.txt).
Ask on Discord if you need help testing something in game.

## Code layout

- `src/main/java` holds all code.
  Client-only code (GUIs, models, renderers) is in the `client` package, which common code reaches only through `ClientProxy`; `ArchitectureTest` checks this.
- `src/api/java` holds the ComputerCraft API: compiled against, not packed into the jar.
- `src/main/resources/assets/openmodularturrets` holds the models, blockstates, lang files, textures and the handwritten recipes (there is no datagen on 1.12).
- `src/test/java` holds the unit tests.

[docs/architecture.md](docs/architecture.md) explains the layers, where things live, the main flows and how to add a turret, an upgrade, a config value or a packet.
[docs/turret-api.md](docs/turret-api.md) describes the API other mods use to add turrets.

## Code style

- Java 8 (the toolchain), 4-space indent, no wildcard imports.
  There is no formatter in this build.
- MCP names (`TileEntity`, `Container`, `GuiContainer`, …).
- New registry ids and resource names are lowercase `snake_case`.
  Existing 3.x ids, metadata and NBT keys stay as they are, so worlds from 3.x still load; renames go through the fixers in `fixes`.
- All user-facing text goes through translation keys in the `.lang` files (`en_US.lang` at least).
- Gameplay logic runs on the server.
  Every client-to-server packet must check permissions again on the server.
- Add a short Javadoc to any non-obvious formula.
- Dependencies are limited to Forge, OMLib, the shared core OpenModularCore and the optional integrations already in `build.gradle` (JEI, Hwyla, The One Probe, OpenComputers, ComputerCraft, RedstoneFlux, Tesla).
  Ask before adding anything else.

## Pull requests

1. Branch off `1.12` and keep each pull request focused on one change.
2. Add a one-line entry for every user-visible change under `## [Unreleased]` in `CHANGELOG.md`, by hand or with:

   ```bash
   ./gradlew changelogEntry -Ptype=fixed -Pentry="Rocket turrets no longer target trusted players"
   ```

   Types are `added`, `changed`, `fixed` and `removed`.
3. Make sure `./gradlew build -Pomlib_local` passes.
   If you built against a local OpenModularCore, say which core change your pull request needs; it can only be merged once that change is in a core release.
4. If you can, test in game, and on a dedicated server for anything involving networking or GUIs.
5. Describe what you changed and how you tested it in the pull request.

## License

By contributing, you agree that your code is licensed under the [MIT license](LICENSE) and your art (textures, models, sounds) under [CC BY-NC-SA 4.0](LICENSE-ART), as [LICENSE-DUAL](LICENSE-DUAL) describes.
