# CLAUDE.md — OpenModularTurrets (Fabric, Multi-Version)

This file guides Claude Code when working in this repository. Read it fully before making changes.

---

## 1. Project Overview

**OpenModularTurrets (OMT)** is being maintained on **Fabric**, targeting **1.20.1 and 1.21.1 from a single codebase** using **Stonecutter**.

Core gameplay:

- **Turret bases** are the "brain". They handle:
  - all targeting
  - item (ammo) and power consumption
  - upgrades and addons
  - permission management
- **Turrets** are mounted on any of the **6 faces** of a base. They rotate (yaw and pitch, relative to their mounting face) to aim at the target the base selects, then fire.
- **Permissions** use a **trusted player** system. Each trusted player has a **power/access level** that limits what they can do (open the GUI, change targeting, manage other trusted players, and so on). The owner always has full rights.

### Allowed dependencies

- Fabric Loader and **Fabric API**. Fabric API, including Transfer API, Networking, Rendering and Datagen, may be used freely.
- **Team Reborn Energy** for power, as the de facto Fabric energy standard. Jar-in-jar it. *(See Open Questions; confirm before adding.)*
- Nothing else without asking the user first. No Architectury, no Cloth Config, no OMLib, no Kotlin.

---

## 2. Tech Stack

| | 1.20.1 | 1.21.1 |
| --- | --- | --- |
| Java | 17 | 21 |
| Fabric Loader | 0.19.x | 0.19.x |
| Fabric API | `0.92.x+1.20.1` | `0.116.x+1.21.1` |
| Mappings | Mojang official (+ Parchment optional) | Mojang official (+ Parchment optional) |

- Build system: Gradle 9.8 (Kotlin DSL) + Fabric Loom 1.18 (`net.fabricmc.fabric-loom-remap`, the variant for obfuscated MC versions) + **Stonecutter 0.9**.
- **Gradle itself must run on Java 25+** (Loom 1.18 requirement). The compile toolchains (17 / 21) are resolved separately (foojay auto-provisions them if missing).
- **Use Mojang mappings** (`loom.officialMojangMappings()`). All class and method names in code and in this doc are Mojmap names (`BlockEntity`, `ResourceLocation`, `AbstractContainerMenu`, and so on).
- Language: Java only.
- Toolchain: configure the Java toolchain per version (17 for 1.20.1, 21 for 1.21.1) in the shared build script. Use the version-specific `gradle.properties` to switch.

Check exact latest patch versions of Loader, API, Loom and Stonecutter when setting up or bumping. Do not hard-code guesses into the build without verifying.

---

## 3. Repository Layout

```
.
├── CLAUDE.md
├── build.gradle.kts            # shared build script, runs once per version
├── stonecutter.gradle.kts      # Stonecutter controller (active version, chiseled tasks)
├── settings.gradle.kts         # declares Stonecutter versions
├── gradle.properties           # shared properties (mod_id, mod_version, maven_group …)
├── versions/
│   ├── 1.20.1/
│   │   ├── gradle.properties   # minecraft_version, fabric_api, java_version=17 …
│   │   └── src/main/generated/ # datagen output for 1.20.1 (committed)
│   └── 1.21.1/
│       ├── gradle.properties   # minecraft_version, fabric_api, java_version=21 …
│       └── src/main/generated/ # datagen output for 1.21.1 (committed)
├── src/
    ├── main/java/omtteam/openmodularturrets/...
    ├── main/resources/         # SHARED assets + fabric.mod.json (templated)
    └── client/java/...         # client-only code (split source sets)
```

- **Mod ID:** `openmodularturrets`. **Base package:** `omtteam.openmodularturrets`.
- Use Loom **split environment source sets** (`splitEnvironmentSourceSets()`). Client-only code (renderers, screens, models, client networking handlers) goes in `src/client/java`. Never reference client classes from `main`.
- `fabric.mod.json` is templated via `processResources`. Inject `${version}`, `${minecraft_dependency}` and `${java_version}` from the per-version properties.

---

## 4. Stonecutter

### Setup (`settings.gradle.kts`)

```kotlin
plugins { id("dev.kikugie.stonecutter") version "0.9.8" }

stonecutter {
    create(rootProject) {
        versions("1.20.1", "1.21.1")
        vcsVersion = "1.21.1"   // the version the committed source is written for
    }
}
```

The Stonecutter DSL changes between releases. If this does not match the installed version, check the Stonecutter docs and update both this file and the build.

### Rules

- **Committed source must always be in the `vcsVersion` (1.21.1) state.** Before committing, switch back to 1.21.1 if you switched to 1.20.1 (`./gradlew "Reset active project"` or `"Set active project to 1.21.1"`).
- Version-conditional code uses Stonecutter comments:

  ```java
  //? if >=1.21 {
  ResourceLocation id = ResourceLocation.fromNamespaceAndPath(MOD_ID, path);
  //?} else {
  /*ResourceLocation id = new ResourceLocation(MOD_ID, path);
  *///?}
  ```

  Inactive branches must be wrapped in `/* … */` exactly as Stonecutter expects. Prefer letting Stonecutter generate this by switching versions over hand-writing it.
- **Minimize conditional blocks.** Hide version differences behind small helpers in `omtteam.openmodularturrets.compat.VersionCompat` (or similar) rather than sprinkling `//? if` across gameplay code. Gameplay logic (targeting, permissions, consumption) should be version-agnostic.
- Constants can be compared in conditions: `//? if >=1.21 {`, `//? if <1.21 {`, `//? if 1.20.1 {`.
- Version-specific *properties* live in `versions/<v>/gradle.properties`. Read them in the build via `property("…")`.

### Commands

```bash
./gradlew "Set active project to 1.20.1"   # switch source to 1.20.1
./gradlew "Set active project to 1.21.1"   # switch back (VCS state)
./gradlew chiseledBuild                    # build ALL versions (custom task in stonecutter.gradle.kts)
./gradlew :1.20.1:build                    # build a single version
./gradlew :1.21.1:runClient                # run client for a version
./gradlew :1.21.1:runServer
./gradlew :1.20.1:runDatagen               # regenerate data for a version
./gradlew :1.21.1:runDatagen
./gradlew chiseledPublish                  # upload all versions to CurseForge/Modrinth (custom task)
./gradlew :1.21.1:publishMods              # upload one version
```

### Publishing

- Uses `me.modmuss50.mod-publish-plugin` (approved by the user), configured in `publishMods` in `build.gradle.kts`.
- Settings in `gradle.properties`: `release_type` (release/beta/alpha), `curseforge_project_id` (224663, the old OMT page), `modrinth_project_id` (empty = Modrinth skipped).
- Tokens are never committed: environment variables `CURSEFORGE_TOKEN` / `MODRINTH_TOKEN`, or `curseforge_token` / `modrinth_token` in `~/.gradle/gradle.properties`. Without a token the upload is a dry run (output in `versions/<v>/build/publishMods/`).
- **Changelog** (`CHANGELOG.md`, Keep a Changelog format): every user-visible change gets a one-line entry under `## [Unreleased]` (Added / Changed / Fixed / Removed), by hand or via `./gradlew changelogEntry --type=fixed --entry="…"`. Add the entry together with the change.
- **Release steps:** set `mod_version` (e.g. `4.0.0-alpha.2`) and `release_type` → `./gradlew releaseChangelog` (moves Unreleased into `## [<mod_version>] - <date>`) → `./gradlew chiseledPublish`. Publishing uploads only that version's section and fails if it is missing.
(Task names may differ slightly depending on Stonecutter's version. Use `./gradlew tasks` if unsure.)

**Definition of done for any change:** `./gradlew chiseledBuild` succeeds for **both** versions, and the source is left in the 1.21.1 state.

### Core Game Logic Source

The game logic of the mod should be implemented in the "core" folder and be completely free from minecraft imports.
The logic consists of:

1. The turret base behaviour (targeting, ammo handling, upgrade/addon handling)
2. The trusted player permission system
3. an abstract representation of a target entity
4. Unit Tests to test its behaviour.

`./gradlew check` runs `verifyCoreIsolation`, which fails if anything under `core/` imports `net.minecraft`, `net.fabricmc`,
`com.mojang`, `team.reborn` or non-core mod packages. Tests live in `src/test/java` (JUnit, test-only dependency).

---

## 5. Known 1.20.1 vs 1.21.1 Differences

Wrap each of these behind a helper where possible.

| Area | 1.20.1 | 1.21.1 |
| --- | --- | --- |
| `ResourceLocation` | `new ResourceLocation(ns, path)` | `ResourceLocation.fromNamespaceAndPath(ns, path)` → use `OMT.id(path)` helper |
| BlockEntity NBT | `load(CompoundTag)`, `saveAdditional(CompoundTag)` | `loadAdditional(CompoundTag, HolderLookup.Provider)`, `saveAdditional(CompoundTag, HolderLookup.Provider)` |
| BE update packet tag | `getUpdateTag()` | `getUpdateTag(HolderLookup.Provider)` |
| ItemStack extra data | `stack.getTag()` / NBT | **Data Components** (register custom `DataComponentType`s, or `CUSTOM_DATA`) |
| ItemStack NBT save | `stack.save(tag)` / `ItemStack.of(tag)` | `stack.save(provider)` / `ItemStack.parse(provider, tag)` (codecs) |
| Block interaction | `Block#use(...)` → `InteractionResult` | `useWithoutItem(...)` and `useItemOn(...)` → `ItemInteractionResult` |
| Block codecs | not required | `BaseEntityBlock` and others require `codec()` returning a `MapCodec` |
| Networking | `ResourceLocation` channel + `FriendlyByteBuf` | `CustomPacketPayload` + `StreamCodec` + `PayloadTypeRegistry` |
| Extended menus | `ExtendedScreenHandlerFactory` writing to a buf | `ExtendedScreenHandlerFactory<D>` with a `StreamCodec` for `D` |
| Entity synced data | `defineSynchedData()` | `defineSynchedData(SynchedEntityData.Builder)` |
| Tooltips | `appendHoverText(stack, Level, list, flag)` | `appendHoverText(stack, Item.TooltipContext, list, flag)` |
| Screen background | `renderBackground(GuiGraphics)` | `renderBackground(GuiGraphics, mouseX, mouseY, partialTick)` |
| VertexConsumer | `.vertex().color().uv()…endVertex()` | `.addVertex().setColor().setUv()…` (no `endVertex`) |
| Data folders | `tags/blocks`, `tags/items`, `loot_tables`, `recipes`, `advancements` | `tags/block`, `tags/item`, `loot_table`, `recipe`, `advancement` |
| Recipe JSON result | `"result": {"item": "...", "count": n}` | `"result": {"id": "...", "count": n}` |

**Same in both** (no conditionals needed): `DamageTypes` / damage-type registry, `GuiGraphics`, `PoseStack`, the general Fabric Transfer API shape, and the `BlockEntityTicker` pattern.

### Resources strategy

- **`assets/`** (textures, models, blockstates, lang, sounds): identical across versions. Keep them handwritten in shared `src/main/resources/assets/openmodularturrets/`.
- **`data/`** (recipes, loot tables, tags, advancements): folder names and JSON formats differ. **Generate them with Fabric Datagen.**
  - Run datagen per version.
  - Configure the output directory relative to the version project (`project.file("src/main/generated")` → `versions/<v>/src/main/generated`).
  - Add that directory as a resource source dir.
  - Commit the generated files.
- Do not handwrite files into `data/` in shared resources.

---

## 6. Architecture

```
omtteam.openmodularturrets
├── OMT.java                     # ModInitializer, MOD_ID, id() helper, logger
├── registry/                    # ModBlocks, ModItems, ModBlockEntities, ModEntities,
│                                # ModMenus, ModSounds, ModCreativeTabs, ModDataComponents (1.21)
├── block/
│   ├── base/                    # TurretBaseBlock (tiers 1–5)
│   ├── turret/                  # AbstractTurretBlock + one class per turret type
│   ├── expander/                # inventory & power expanders
│   └── misc/                    # lever, hard walls, fences, etc.
├── blockentity/
│   ├── TurretBaseBlockEntity    # targeting, inventory, energy, upgrades, addons, permissions
│   └── turret/                  # AbstractTurretBlockEntity + subclasses
├── targeting/                   # TargetingSettings, TargetSelector, line-of-sight, priorities
├── core/                        # The core logic files for turret base logic (targeting/upgrades/ticking etc.)
├── security/                    # TrustedPlayer, AccessLevel, PermissionManager
├── energy/                      # internal energy storage + energy API adapter
├── inventory/                   # base inventory, slot filters, Transfer API exposure
├── item/                        # ammo, upgrades, addons, additional items
├── entity/projectile/           # bullets, grenades, rockets, lasers, slugs, …
├── menu/                        # AbstractContainerMenu implementations
├── network/                     # packets (C2S: settings/trust changes; S2C: sync)
├── config/                      # self-written config (JSON via Gson), no library
└── compat/                      # VersionCompat helpers (Stonecutter-conditional code lives here)

(client source set)
omtteam.openmodularturrets.client
├── OMTClient.java               # ClientModInitializer
├── render/blockentity/          # turret renderers (rotating heads), base renderer
├── render/entity/               # projectile renderers
├── model/                       # Java entity-style turret models
├── screen/                      # base GUI, configuration/targeting/trust screens
└── network/                     # client packet handlers
```

### Turret ↔ base relationship

- A turret block has a `FACING` property (all 6 `Direction`s). `FACING` is the direction **away from the base**. The base is at `pos.relative(facing.getOpposite())`.
- Turrets can only be placed against a base face. A turret without a base does nothing and should break or drop if its base is removed.
- The **base** finds its turrets by checking its 6 neighbours.
- The **base** owns all state:
  - targeting settings
  - the current target (or targets per turret)
  - ammo
  - energy
  - upgrades and addons
  - trusted players
- **Turret block entities are thin.** They handle:
  - their aim (yaw/pitch)
  - cooldown
  - firing visuals and sounds
  - spawning projectiles
  
  They request ammo and energy from the base.

### Aiming on arbitrary faces

- Compute the target direction in world space, then transform it into the turret's **local frame**, where the local "up" is `FACING`. Derive local yaw/pitch from that.
- The renderer applies the mount rotation first (rotate the model so its up-axis aligns with `FACING`), then local yaw, then local pitch.
- Put the math in one place, e.g. `TurretMath`, used by both server logic (can it aim there?) and the client renderer.
- Sync yaw/pitch (or the target entity ID) to clients for smooth interpolated rendering. Interpolate on the client; don't sync every tick unless necessary.

### Targeting (base)

- Run on the server tick via `BlockEntityTicker`. Throttle scans; do not scan entities every tick.
- Settings include target categories (hostile mobs, neutral/passive mobs, players), range (base tier + range upgrades), and any additional options.
- **Never target** the owner or trusted players. Respect line of sight.
- Range is affected by tier and upgrades. Accuracy, fire rate, efficiency and scattershot are upgrades. The formulas live in `core/`; don't change them without asking the user.

### Security / trusted players

- Each base stores:
  - **owner** (UUID + last known name)
  - a list of **TrustedPlayer** (UUID, name, **access level**)
- Access levels gate actions (`core.security`):
  - a low level can open the GUI / view
  - a higher level can change targeting and settings
  - the highest level can manage trusted players
  - nobody but the owner can change the owner
- **Every C2S packet must re-validate permissions on the server** (player distance to the block, access level for the specific action). Never trust the client.
- Breaking or opening a base also respects permissions.
- Use UUIDs for identity; names are display-only. Resolve names to UUIDs server-side via the server's profile cache when adding a player.

### Power & items

- Bases store energy internally. Expose it through the energy API on all sides; expose inventory through Fabric Transfer API (`ItemStorage.SIDED`).
- Ammo is consumed from the base inventory. Costs and capacities are configurable in `config/`.
- Power generation addons (solar, redstone reactor) are ticked by the base.

### Config

- No config library. Write a minimal Gson-based JSON config at `config/openmodularturrets.json`.
- Load it on init, write defaults if missing, and validate ranges.
- Server-authoritative values that clients need (e.g. for tooltips) are synced on join.

---

## 7. Change Workflow

1. Implement gameplay logic in `core/` (with unit tests) where possible; keep Minecraft-facing code thin.
2. Wrap version differences in `compat/`.
3. Add assets, lang entries and datagen entries; regenerate data for **both** versions.
4. Add a `CHANGELOG.md` entry for user-visible changes.
5. Run `./gradlew chiseledBuild`.

- Every variant (base tier, ammo type, upgrade, addon) is an **individual registered block/item** with a lowercase snake_case ID. `docs/id-mapping.md` maps the 1.12.2 IDs to the current ones.
- OpenComputers / ComputerCraft integration is **out of scope for now** (ask before adding).

---

## 8. Coding Conventions

- Java, 4-space indent, no wildcard imports.
- Registry IDs and resource names: lowercase `snake_case`.
- Keep gameplay logic **server-side**. Clients only render and send requests.
- No static mutable state for per-world data. Base state lives in block entities.
- Use `Level#isClientSide()` checks properly; never load client classes on the server.
- All user-facing text goes through translation keys (`block.openmodularturrets.x`, `gui.openmodularturrets.x`, `tooltip.openmodularturrets.x`).
- Prefer small, focused classes.
- Add a short Javadoc to any non-obvious formula.
- Don't add new features or change balance values unless the user asks.

---

## 9. Roadmap / Status

Update this checklist as work progresses.

- [x] Gradle + Loom + Stonecutter skeleton; both versions build with an empty mod
- [x] `fabric.mod.json` templating, split source sets, datagen per version
- [x] `compat/` helpers (id, NBT save/load, item data, networking registration)
- [x] Config system (incl. server → client sync on join)
- [x] Core Game Logic for turret base and trusted player system (`core.security`, `core.base.TurretBaseLogic`, `core.target`, turret/ammo/upgrade/addon enums)
- [x] Turret base blocks (all tiers) + block entity (inventory, energy) — camo pending, expanders skipped
- [x] Security: owner, trusted players, access levels, permission checks, trusted players screen + server-validated packets
- [x] Base GUI (inventory, energy display, targeting settings, trust management) — camo light sliders pending with camo
- [x] Networking for settings and trust changes (server-validated)
- [x] Targeting logic (core `TargetSelector`, per-turret targets in `TurretBaseLogic`; aiming/yaw-pitch limits come with the turret framework)
- [x] Turret block framework (6-face mounting, `TurretMath`, aiming sync)
- [x] Turret renderers (rotating heads on any face) — all turret models; melee turret has spinning blades (top/bottom mount only); ray/arc beams
- [x] Individual turrets + projectiles (all 14 turrets: projectile, ray and effect weapons; projectile visuals and particles)
- [x] Ammo items (items, tooltips, recipes)
- [x] Upgrades and addons (items, tooltips, recipes, base effects, fake drops loot rule)
- [x] Expanders are to be skipped
- [x] Misc blocks (crank; OMPD hardened blocks, walls, fences, camouflage trap; loot deleter and power generator skipped)
- [x] Recipes, loot tables, tags via datagen for all content
- [x] Lang, sounds, creative tab
- [ ] Camouflage for turret bases and the camouflage trap, one shared system:
  - Disguise is set and reset the same way on both blocks (via gui with simple phantom slot) and rendered the same way.
  - A small camo settings GUI on both blocks (light value / light opacity sliders, for tier 4+ bases), changes server-validated.
- [ ] Playtest both versions (client + dedicated server)
- [x] Publishing to CurseForge / Modrinth via Gradle (`chiseledPublish`; CurseForge 224663, Modrinth `open-modular-turrets`)
- [x] Changelog system (`CHANGELOG.md`, `changelogEntry`, `releaseChangelog`; publish uploads the version's section)
- [x] Generator block (new in this version; design agreed with the user, see section 10)

---

## 10. Further Information

1. **Energy API:** Team Reborn Energy is acceptable (jar-in-jar)
2. **Mappings:** Mojang official
3. **Package name:** keep `omtteam.openmodularturrets`
4. **Scope:** the port from 1.12.2 is complete; this repo maintains the 1.20.1 / 1.21.1 versions. Expanders were dropped.
5. **Team/faction integration:** Should vanilla scoreboard teams count as trusted? Yes, but only at the lowest rank, so turrets do not shoot them.
6. **Generator** (new block, user decisions): burns any furnace fuel (Fabric `FuelRegistry`, remainders like empty buckets stay in the slot), one burn tick = `generatorEnergyPerTick` (default 40 RF/t); pauses while its buffer (`generatorCapacity`, default 50,000) is full; pushes up to `generatorMaxOutput` (default 200 RF/t) into all neighbours via Team Reborn Energy; one block, furnace-like GUI with energy bar, no owner/trust; hoppers insert fuel only.
