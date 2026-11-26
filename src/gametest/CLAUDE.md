# CLAUDE.md — OpenModularTurrets, game tests (`src/gametest`)

Moved out of the top-level `CLAUDE.md`, so that it is loaded only when you work in this folder; the top-level file has the index.
Section numbers (§) and relative paths (`../CLAUDE.md`, `../../OpenModularCore`) are those of the top-level file; paths are relative to the repository root.

## Game tests

In-game behaviour (turrets firing, effects on entities) is tested with GameTests in `src/gametest`, a test mod (`openmodularturrets_gametest`, never shipped) whose test classes are shared by both loaders.
Classes implement `OMTGameTests` and use its `EMPTY_STRUCTURE` (the mod's own 8×8×8 air structure in `data/openmodularturrets/structures/`) with the scene built in code.
On Fabric (Loom `fabricApi.configureTests`) they are listed under the `fabric-gametest` entrypoint in `src/gametest/resources/fabric.mod.json`; on Forge they carry `@GameTestHolder(OMT.MOD_ID)` and `@PrefixGameTestTemplate(false)` behind `//? if forge` (the gametest source set is a second mod, `META-INF/mods.toml`).
The test mod's loader entrypoints (`gametest/platform/<loader>`) run its registration steps (`OMTGameTestMod`) and forward entity loads to `ProjectileTurretGameTests.onEntityLoad`.
`check` (and so `build` / `chiseledBuild`) runs them on a dedicated test server: `runGameTest` on Fabric, `runGameTestServer` on Forge, both in `versions/<node>/build/run/gameTest` with a new flat world every run (the world is deleted before it; on Forge the build also writes the flat world settings, see §4 "Forge 1.20.1").
Mob spawning and the weather cycle are off for the whole run, so no mob spawns around the tests and it never rains: on Fabric through vanilla's `GameTestServer`, on Forge, whose test server takes the default game rules of the dedicated server's settings, through the test mod's `OMTForgeGameTests`, which turns them off at server start.
The day still passes: a test that needs daylight sets the time itself (`SolarPanelGameTests`).

- Each turret, on a base of its tier with energy and ammo, must hit a hostile target 5 blocks in front of it (the melee turret, which only reaches the 3×2×3 area around itself and its base, a target in reach); projectile turrets must also spawn their projectile (`ProjectileTurretGameTests`, spawns observed through the loaders' entity-load events; explosion and fire damage count as hits), ray turrets trace their ray to the target (`RayTurretGameTests`, through `RayShotCallback`), effect turrets apply their effect (`EffectTurretGameTests`).
- `TurretScene` builds a ready-to-fire base + turret (owner, always on, full energy, max range) and spawns targets (husks without AI).
  A base placed with `setBlock` has no owner: it stays, but does not fire until it has one (`bases.unownedBasesActive`).
- `TurretScene` also loads the turret's ammo into the base (energy alone is not enough for the railgun and the projectile turrets).
- Turrets that scatter get up to 10 attempts, each with a fresh target (`TargetAttempts`); a miss replaces the target, a hit passes.
  A ray attempt ends with its ray, a projectile attempt when its first projectile is gone.
  The test server does not tick entities in the chunks next to a test, and projectiles remove themselves before flying into such a chunk, so `TurretScene` force-loads the chunks around the scene while a test runs (otherwise a grenade that lands just past the target would vanish instead of exploding).
  Projectile tests fail just before their timeout with their state (shots, followed projectile, target) instead of timing out silently.
- A turret test that ends with "Didn't succeed or fail within N ticks" (ray tests) or without its effect (effect tests) means the turret never fired, which so far always meant that its base found no valid target: check the base's targets and the line of sight from the turret to the target's eyes first.
- Turret ranges reach into neighbouring tests, so every turret test gets its own `batch` and ends with `TurretScene.succeedWhen`, which removes the turret, base and entities once it passed (a finished batch's blocks and entities stay next to the following one).
- A test that needs other config values for a turret type (the range falloff and turn rate tests) changes them with `TurretScene.configure(helper, type, setting -> …)`, which puts every value back when the scene is cleared (on success, on failure and one tick before the timeout); the setting is shared by all tests, so such a test needs a batch of its own.
  The tests read a turret's aim from its saved `Yaw` / `Pitch`, not through a getter.
  A local test run keeps its config file, and the merge keeps old values of addon turrets: delete `versions/1.20.1-fabric/build/run/gameTest/config/openmodularturrets*` and `versions/1.20.1-forge/build/run/gameTest/config/openmodularturrets*` after a default changes.
- `BaseGuiGameTests` clicks a base through `ServerPlayerGameMode#useItemOn` (the real server path, including the loader's block-use event) and checks that the menu opens for the owner only.
  The loaders' `FakePlayer`s refuse to open menus, so the test uses `GuiTestPlayer`, a small subclass that opens them without sending a packet; `CamoMenuGameTests` uses it for the camouflage menu and its "Back" to the base GUI.
  Cast `getBlockEntity` results, which are a plain `BlockEntity` here.
- `TrustListGameTests`, `CamoMenuGameTests` and the light tests in `CamoGameTests` call OMLib's packet handlers (`TrustPacketHandler`, `CamoPacketHandler`) directly with fake players placed next to the block, as the server does on a packet, and check permission, reach and the access level steps.
  Fake players are not on the server's player list, so the test that adds an online player by name puts one there for the (synchronous) request only (`TrustListGameTests.online`), on Fabric only: Forge returns a read-only view of the list, and vanilla's mock players would log in for real, which breaks Forge's login handling.
  Names of players who are not online go to the server's name cache: the test server has none (not found).
  OMLib's `ConfigSync` is not covered: it only sends to clients that negotiated its channel, and the test server has none.
- A test that needs open air (the wand's sneak-click into the air) stays inside its own 8 × 8 × 8 structure, looking from one corner to the opposite one; the solar panel test under open sky runs on both loaders.

## Second-loader design decisions: GameTests

**GameTests**

- Shared test classes for both loaders: they implement a shared interface (e.g. `OMTGameTests`, like OpenModularLighting's `OMLGameTests`) instead of `FabricGameTest`, use the mod's own empty 8 × 8 × 8 structure (`structures/`), and carry `@GameTestHolder(MOD_ID)` + `@PrefixGameTestTemplate(false)` behind `//? if forge`.
- `TestTransfer` with `hasEnergy`, `insertEnergy`, `insertItem`, `extractItem` (Fabric lookups / Forge capabilities) replaces direct Fabric lookups in tests.
  OMT's tests have no direct lookups (items go through vanilla hoppers, energy through the generator push), so OMT has no `TestTransfer` yet; add it when a test needs one.
- The test battery keeps its energy in the item's custom data, with loader-specific energy access (OMT has no battery).
- Test mod id with an underscore on both loaders (`openmodularturrets_gametest`); a Forge mod id cannot contain hyphens.
- No test class may sit in a main-mod package (the loaders' modules cannot share packages); reach protected methods through public vanilla API.

## Turret API tests (from "Turret API (adding turrets)")

- The game test mod registers one test turret per weapon kind through the API (`ApiTestTurrets`, with its own projectile entity `ApiTestProjectile` registered in `ApiTestEntities`) and tests registration and every custom-logic hook in `ApiTurretGameTests`.
  `ApiTestProjectile` stays on the deprecated `damageWithAmp(int, Entity)`, as the check that projectiles of addons built against older versions still work.

## Forge 1.20.1: game tests (from "Forge 1.20.1")

- **GameTests:** `runGameTestServer` with `forge.enabledGameTestNamespaces`; the test classes carry `@GameTestHolder` and `@PrefixGameTestTemplate` from `net.minecraftforge.gametest`.
  Vanilla 1.20.1's `makeMockServerPlayerInLevel` breaks Forge's login handling (no netty channel); OMT's tests use the loaders' fake players instead, which work.
  The library jar the client and game test source sets use is the one with the dev names (classifier `dev`, published by OMLib next to the SRG-named one): the game test run has no remapping, and a Forge game test server whose mod fails to load still exits normally, so `check` also requires the "All N required tests passed" line in `build/run/gameTest/logs/latest.log` (Forge build script).
  Forge 1.20.1 builds its game test server from the dedicated server's `server.properties` instead of vanilla's flat test world, so `runGameTestServer` runs in its own folder `versions/<node>/build/run/gameTest` and writes a `server.properties` there before every run (flat, seed 0, no structures) and deletes the old world.
  Without it, the tests get normal terrain with a random seed, where falling gravel or lava blocks a turret's sight or hurts its target at random.
