# CLAUDE.md — OpenModularTurrets, game tests (`src/gametest`)

Moved out of the top-level `CLAUDE.md`, so that it is loaded only when you work in this folder; the top-level file has the index.
Section numbers (§) and relative paths (`../CLAUDE.md`, `../../OpenModularCore`) are those of the top-level file; paths are relative to the repository root.

## Game tests

In-game behaviour (turrets firing, effects on entities) is tested with GameTests in `src/gametest`, a test mod (`openmodularturrets_gametest`, never shipped) whose test classes are shared by both loaders.
Classes implement `OMTGameTests` and use its `EMPTY_STRUCTURE` (the mod's own 8×8×8 air structure in `data/openmodularturrets/structure/`) with the scene built in code.
On Fabric (Loom `fabricApi.configureTests`) they are listed under the `fabric-gametest` entrypoint in `src/gametest/resources/fabric.mod.json`; on NeoForge they are found through the mod file scan (the gametest source set is a second mod, `META-INF/neoforge.mods.toml`), with no `@GameTestHolder` at all.
The test mod's loader entrypoints (`gametest/platform/<loader>`) run its registration steps (`OMTGameTestMod`) and forward entity loads to `ProjectileTurretGameTests.onEntityLoad`.
`check` (and so `build` / `chiseledBuild`) runs them on a dedicated test server: `runGameTest` on Fabric (`versions/<node>/build/run/gameTest`, the world is deleted before every run), `runGameTestServer` on NeoForge.

- **The game test framework** (a test is a registered test function plus a data-driven test instance, batched by test environment; vanilla's `@GameTest` is gone): `src/gametest/java` has a stand-in `@GameTest` with the same name and attributes in the test classes' own package, and `GameTestCases`, which turns the methods into test functions and instances `openmodularturrets_gametest:<class>/<method>` and each `batch` into a test environment (`timeoutTicks`, default 100).
  The test server keeps the default game rules (mob spawning, weather and daylight cycles on), and a run passes tens of thousands of ticks while a test waits for the wall clock, so every batch environment turns mob spawning off and clears the weather (`GameTestCases.batchEnvironment`); otherwise rain stopped the solar panel and naturally spawned mobs walked into turret ranges (2026-11-22).
  The day still passes: a test that needs daylight sets the time itself (`SolarPanelGameTests`).
  Fabric registers them from the `fabric-gametest` entrypoints, NeoForge from the mod file scan (the classes with `@GameTest` methods).
- Every turret has a game test: on a base of its tier with energy and ammo it must hit a hostile target 5 blocks in front of it (the melee turret: a target in its reach); projectile turrets must also spawn their projectile, ray turrets trace their ray (`ProjectileTurretGameTests`, `RayTurretGameTests`, `EffectTurretGameTests`).
- `TurretScene` builds a ready-to-fire base + turret (owner, always on, full energy, max range) and spawns targets (husks without AI).
  A base placed with `setBlock` has no owner: it stays, but does not fire until it has one (`bases.unownedBasesActive`).
- `TurretScene` also loads the turret's ammo into the base (energy alone is not enough for the railgun and the projectile turrets).
- Turrets that scatter get up to 10 attempts, each with a fresh target (`TargetAttempts`); a miss replaces the target, a hit passes.
  A ray attempt ends with its ray, a projectile attempt when its first projectile is gone.
  The test server does not tick entities in the chunks next to a test, and projectiles remove themselves before flying into such a chunk, so `TurretScene` force-loads the chunks around the scene while a test runs (otherwise a grenade that lands just past the target would vanish instead of exploding).
  Projectile tests fail just before their timeout with their state (shots, followed projectile, target) instead of timing out silently.
- Turret ranges reach into neighbouring tests, so every turret test gets its own `batch` and ends with `TurretScene.succeedWhen`, which removes the turret, base and entities once it passed.
- A test that needs other config values for a turret type (the range falloff and turn rate tests) changes them with `TurretScene.configure(helper, type, setting -> …)`, which puts every value back when the scene is cleared (on success, on failure and one tick before the timeout); the setting is shared by all tests, so such a test needs a batch of its own.
  The tests read a turret's aim from its saved `Yaw` / `Pitch`, not through a getter.
  A local test run keeps its config file, and the merge keeps old values of addon turrets: delete `versions/1.21.11-fabric/build/run/gameTest/config/openmodularturrets*` and `versions/1.21.11-neoforge/run/config/openmodularturrets*` after a default changes.
- `BaseGuiGameTests` clicks a base through `ServerPlayerGameMode#useItemOn` (the real server path, including the loader's block-use event) and checks that the menu opens for the owner only.
  The loaders' `FakePlayer`s refuse to open menus, so the test uses `GuiTestPlayer`, a small subclass that opens them without sending a packet (NeoForge opens block menus through the two-argument `openMenu`, which it overrides too); `CamoMenuGameTests` uses it for the camouflage menu and its "Back" to the base GUI.
- `TrustListGameTests`, `CamoMenuGameTests` and the light tests in `CamoGameTests` call OMLib's packet handlers (`TrustPacketHandler`, `CamoPacketHandler`) directly with fake players placed next to the block, as the server does on a packet, and check permission, reach and the access level steps.
  Fake players are not on the server's player list, so the test that adds an online player by name puts one there for the (synchronous) request only (`TrustListGameTests.online`), on Fabric only: NeoForge returns a read-only view of the list, and vanilla's mock players would log in for real.
  Names of players who are not online go to the server's name cache: the test server has one (offline mode, so the name's offline UUID).
  `TrustListGameTests.namesOfOfflinePlayersAreLookedUp` waits for that lookup up to 30 seconds of wall clock (`LOOKUP_SECONDS`) instead of a tick timeout, because the test server runs its ticks back to back while a background worker answers.
  OMLib's `ConfigSync` is not covered: it only sends to clients that negotiated its channel, and the test server has none.

## Second-loader design decisions: GameTests

**GameTests**

- Shared test classes for both loaders: they implement a shared interface (e.g. `OMTGameTests`, like OpenModularLighting's `OMLGameTests`) instead of `FabricGameTest`, use the mod's own empty 8 × 8 × 8 structure (`structure/`) with the scene built in code; NeoForge finds the classes through the mod file scan, so they carry no holder annotation.
- `TestTransfer` with `hasEnergy`, `insertEnergy`, `insertItem`, `extractItem` (Fabric lookups / NeoForge capabilities) replaces direct Fabric lookups in tests.
  OMT's tests have no direct lookups (items go through vanilla hoppers, energy through the generator push), so OMT has no `TestTransfer` yet; add it when a test needs one.
- The test battery keeps its energy in the item's custom data, with loader-specific energy access (OMT has no battery).
- Test mod id with an underscore on both loaders (`openmodularturrets_gametest`); NeoForge mod ids cannot contain hyphens.
- No test class may sit in a main-mod package (NeoForge modules cannot share packages); reach protected methods through public vanilla API.

## Turret API tests (from "Turret API (adding turrets)")

- The game test mod registers one test turret per weapon kind through the API (`ApiTestTurrets`, with its own projectile entity `ApiTestProjectile` registered in `ApiTestEntities`) and tests registration and every custom-logic hook in `ApiTurretGameTests`.
  `ApiTestProjectile` stays on the deprecated `damageWithAmp(int, Entity)`, as the check that projectiles of addons built against older versions still work.
