# CLAUDE.md — OpenModularTurrets, turrets, bases, aiming and targeting (`blockentity/`)

Moved out of the top-level `CLAUDE.md`, so that it is loaded only when you work in this folder; the top-level file has the index.
Section numbers (§) and relative paths (`../CLAUDE.md`, `../../OpenModularCore`) are those of the top-level file; paths are relative to the repository root.

## Turret ↔ base relationship

- A turret block has a `FACING` property (all 6 `Direction`s).
  `FACING` is the direction **away from the base**.
  The base is at `pos.relative(facing.getOpposite())`.
- Turrets can only be placed against a base face, and only by an admin of that base (`TurretBlock.attachResult`, `TurretPlacement.Result.NO_PERMISSION`; placing without a player is refused).
  A turret without a base does nothing and should break or drop if its base is removed.
- Whether a turret may be attached or stay is decided in one place, `core.base.TurretPlacement` (`canAttach` / `canStay`, returning a `Result` with the reason); the client asks it too (placement outline), with the turrets read from the world.
  `maxSimultaneous` only limits placing, unless `turrets.dropTurretsOverMaxSimultaneous` (default false) is set.
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

## Aiming on arbitrary faces

- Compute the target direction in world space, then transform it into the turret's **local frame**, where the local "up" is `FACING`.
  Derive local yaw/pitch from that.
- The renderer applies the mount rotation first (rotate the model so its up-axis aligns with `FACING`), then local yaw, then local pitch.
- Put the math in one place, e.g. `TurretMath`, used by both server logic (can it aim there?) and the client renderer.
- Sync yaw/pitch (or the target entity ID) to clients for smooth interpolated rendering.
  Interpolate on the client; don't sync every tick unless necessary.
- The server turns the aim at most the type's `turnRate` degrees per tick on each axis (`TurretMath.turnTowards`); the client (`TurretBlockEntity.clientTick`) eases towards the synced aim for turrets that turn instantly, and turns at the turn rate with a catch-up for the others (it reads the synced config), so the model does not pulse between the syncs.
  The pose is saved (`Yaw`, `Pitch`), so a reloaded turret resumes its turn; the target angles are not saved.
  A slow turret may never catch a target that circles it closely; the base keeps such a target (option A of issue 539, no core change).

## Range falloff and turn rate (from "Turret API (adding turrets)")

- Range falloff and turn rate (issue 539, option A chosen by the user on 2026-11-22; the same on every version): `TurretStats` / `TurretSetting` carry `optimalRange`, `falloffDamage` and `turnRate` per turret type, `turrets.aimTolerance` is global (`ConfigLogicSettings.aimTolerance`); tooltip and Jade lines come from `turret.TurretStatLines`.
  Built-in projectiles deal `TurretProjectile.damage(entity)` (damage amp and falloff from the launch point set by `TurretBlockEntity.fire`), explosions and bursts `ampDamage(…) * damageFactor()`; `RayWeapons` scales the ray damage by `TurretBaseLogic.getDamageFactor` at the ray's entry point.
  `TurretBlockEntity.aimAt` turns at most `turnRate` per tick, and `engage` / `pickAndFire` fire only once the aim is within the tolerance (nothing is used while turning); the gate is in those two, never in the public `fire`.
  Not scaled by the falloff: the incendiary fire time, the vanilla explosion of grenades and rockets, effect, area and self-targeting weapons.

## Targeting (base)

- Run on the server tick via `BlockEntityTicker`.
  Throttle scans; do not scan entities every tick.
- Settings include target categories (hostile mobs, neutral/passive mobs, players), range (base tier + range upgrades), and any additional options.
- **Never target** the owner or trusted players.
  Respect line of sight.
- Range is affected by tier and upgrades.
  Accuracy, fire rate, efficiency and scattershot are upgrades.
  The formulas live in the core (OpenModularCore); don't change them without asking the user.
