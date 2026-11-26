# Turret API

Other mods (Fabric and NeoForge) can add turrets to Open Modular Turrets (OMT).
An added turret is a full turret: it mounts on a base, uses the base's targeting, energy, ammo, upgrades, addons and trusted players, has a config entry and shows in the OMT creative tab, in tooltips and in Jade.
The 17 built-in turrets are registered through the same API (`registry/BuiltinTurrets.java`), so they are the reference examples.

This guide is for Minecraft 1.21.1 (branch `1.21.1`, Fabric and NeoForge); the wiki page `Turret-API` covers the other Minecraft versions.
The API is the same on both loaders; only the way OMT finds your classes differs.
On Fabric, add OMT as a `modCompileOnly`/`modImplementation` dependency and to `depends` in your `fabric.mod.json`.
On NeoForge, add it as a `compileOnly`/`implementation` dependency and as a `[[dependencies.<your mod id>]]` entry in your `neoforge.mods.toml`.
The rule classes the API uses (`TurretType`, `AmmoType`, `TurretStats`, `TargetingPriority`, `RayProfile`; package `omtteam.openmodularturrets.core`) come from the shared core OpenModularCore and are part of the OMT jar, and OMLib's `Face` (`omtteam.omlib.core.world`) is part of the OMLib jar.

## 1. Register the turret (common)

On Fabric, declare an `openmodularturrets` entrypoint (and `openmodularturrets_client` for the model, section 2):

```json
"entrypoints": {
  "openmodularturrets": ["com.example.turrets.ExampleTurrets"],
  "openmodularturrets_client": ["com.example.turrets.client.ExampleTurretModels"]
}
```

```java
@OMTPlugin.Register // NeoForge; Fabric ignores it
public final class ExampleTurrets implements OMTPlugin {
    public static final AmmoType SHELL = AmmoType.of("example", "shell");
    public static final TurretType FLAK_TURRET = TurretType.builder("example", "flak_turret")
            .requiredBaseTier(3)          // 1–5, default 1
            .ammo(SHELL)                  // omit for energy-only turrets
            .priorities(10, 10, 20, 1, 10) // MAX_HP, HP_REMAINING, DISTANCE, ARMOR, PLAYER
            .build();

    @Override
    public void registerTurrets(TurretRegistrar registrar) {
        registrar.registerAmmo(AmmoDefinition.ofItem(SHELL, () -> ExampleItems.SHELL));
        registrar.registerTurret(new TurretDefinition(FLAK_TURRET,
                ProjectileWeapon.of(ExampleSounds.FLAK, (level, ammo, target) -> ExampleEntities.FLAK.create(level)),
                TurretStats.builder(24, 20, 6, 500).accuracyDeviation(15).maxSimultaneous(2)
                        .falloff(0.6, 0.5)   // full damage up to 60 % of the range, half damage at its end
                        .turnRate(10)        // degrees per tick on each axis; 0 (the default) turns instantly
                        .build()));
    }
}
```

On NeoForge, annotate the class with `@OMTPlugin.Register` instead; OMT finds it in the mod scan data (the class needs a public no-argument constructor).
A mod for both loaders does both.
OMT calls the plugin at the start of its own initialisation, before it loads its config and registers blocks, so mod load order does not matter.
Several plugins are called in the order Fabric lists the entrypoints, or on NeoForge sorted by mod id and class name.
Suppliers (`() -> ExampleItems.SHELL`) are only called later, so they may point to things your own initializer registers.
Everything passed directly must already exist when OMT calls the plugin, which on NeoForge is during mod construction, before any registry opens.
That matters for the weapon's sound: create the `SoundEvent` in a static field (`SoundEvent.createVariableRangeEvent(id)`) and register that same instance in your sound registration, as OMT's own `ModSounds` does; a `DeferredHolder` from a `DeferredRegister` has no value yet at that point.

OMT then registers a `TurretBlock` and a `BlockItem` with the id `example:flak_turret` and adds it to its turret block entity type.
Get them with `OMTApi.block(type)` / `OMTApi.item(type)` once loading is done (not inside `registerTurrets`).

`TurretDefinition.builder(type, weapon, stats).ambientEffect((level, pos, random) -> …).build()` adds client particles around the placed turret.
The turret block calls the effect from `animateTick` (client only), also while the turret is concealed; spawn the particles with `level.addParticle`.
The built-in slowing and railgun turrets use it for their coloured dust, as on 1.12.2.

### `TurretType` options

| Builder call | Effect |
| --- | --- |
| `requiredBaseTier(n)` | lowest base tier the turret can be placed on (lower tiers drop it) |
| `ammo(type)` | ammo drawn per shot (one item, plus one per scattershot upgrade) |
| `priorities(…)` | default targeting weights, see `TargetingPriority` |
| `mountFaces(Face…)` | base faces it may mount on (default all six; the melee turret uses `UP, DOWN`) |
| `ignoresScattershot()` | scattershot upgrades add no shots, ammo, energy or scatter |
| `skipsSlowedTargets()` | entities with slowness are not targeted (slowing turret) |
| `playersOnly()` | only players are targeted, whatever the base's mob settings (energy drain turret) |
| `skipsTargetsWithoutEnergy()` | entities whose `TargetEntity.hasDrainableEnergy()` is false are not targeted; OMT's adapter answers true for players with energy in their armour or held items (energy drain turret) |

### Weapons

| Kind | Does | Built-in examples |
| --- | --- | --- |
| `ProjectileWeapon` | spawns a `TurretProjectile` per shot, aimed with lead | machine gun, rocket, plasma |
| `RayWeapon` | instant ray to the target; damage from a `RayProfile`, visuals and damage types from the builder | laser, railgun, arc |
| `EffectWeapon` | runs a `TargetEffect` on the base's target, no aiming | slowing, teleporter, repelling, tractor, energy drain |
| `AreaWeapon` | ignores the base's target, strikes every damageable living entity in an area | melee |
| `SelfTargetingWeapon` | ignores the base's target, picks its own from all entities around the base | – |

### Custom logic

Every weapon kind takes your code.
Energy and ammo are taken by OMT before your code runs, once per shot, and the shot follows the turret's cooldown (fire rate and its upgrades).

**On the base's target** (the base does targeting, trust, line of sight and priorities): an `EffectWeapon` gets the world, the turret, the base and the target once per shot.
A player holding the ownership wand (map makers) is still targeted and fired at, but OMT does not call the effect for them.

```java
new EffectWeapon(ExampleSounds.ZAP, (level, turret, base, target) -> {
    if (target instanceof LivingEntity living) {
        living.addEffect(new MobEffectInstance(MobEffects.POISON, 100));
    }
});
```

**Own targeting**: a `SelfTargetingWeapon` ignores the base's target.
Once the turret is ready to fire (cooldown passed, base active, enough energy), its picker gets the world, the turret and the base, looks for targets itself and returns the ones to shoot at.
`base.getLogic().getRange(turret.getTurretType())` is the turret's range (base tier and range upgrades).
If it returns any, OMT takes energy and ammo and calls the shot with them; if not, nothing is consumed and the picker is asked again 5 ticks later.
With `aims = true` the turret turns towards the first picked entity at its `turnRate`.
While its aim is more than `turrets.aimTolerance` degrees off, nothing is consumed and the shot is not called; the picker is asked again on the next tick (with a `turnRate` of 0 the turret turns instantly and fires at once, as before).
Filter with `OMTApi.canDamage(base, entity)` to spare the owner, trusted players, their pets and players holding the ownership wand, and damage with `OMTApi.hurt(base, entity, damageType, amount, direct)` so kills count for the base and the fake drops addon applies.

```java
new SelfTargetingWeapon(ExampleSounds.ZAP, true,
        (level, turret, base) -> {
            AABB area = new AABB(base.getBlockPos()).inflate(base.getLogic().getRange(turret.getTurretType()));
            return List.copyOf(level.getEntitiesOfClass(Monster.class, area, e -> OMTApi.canDamage(base, e)));
        },
        (level, turret, base, targets) -> targets.forEach(
                e -> OMTApi.hurt(base, e, ExampleDamageTypes.ZAP, 4, null)));
```

**Projectile hits**: `ProjectileWeapon.onHit` runs code when one of the weapon's projectiles damages an entity (a direct hit or its explosion), once per projectile and entity, after the damage.
It gets the world, the projectile, the base that fired it (null if it is gone) and the hit entity.
It works with the built-in projectiles too.
Your own projectiles that deal no damage call `notifyHit(entity)` themselves.

```java
ProjectileWeapon.of(ExampleSounds.FLAK, (level, ammo, target) -> ExampleEntities.FLAK.create(level))
        .onHit((level, projectile, base, hit) -> hit.setGlowingTag(true));
```

**Ray hits**: `RayWeapon.builder(…).onEntityHit((level, turret, base, hit) -> …)` runs after a ray damaged an entity: the entity it hit and, with an arc chain profile, every entity the arc jumps to.
Each ray of a scattershot counts on its own.
`onBlockHit` is called when a ray hits a block instead.

All callbacks run on the server thread.

Projectiles extend `omtteam.openmodularturrets.entity.projectile.TurretProjectile` (or `ItemCarryingProjectile`); `getTurretType()` selects the config entry for damage and damage amp, `hurt(…)` / `canDamage(…)` apply the base's trust rules and kill counting.
Deal `damage(entity)` on a direct hit: the turret's damage with the damage amp bonus and the range falloff, measured from the launch point to where the projectile touches the entity (`damage(base, entity)` for your own base damage).
For explosions and bursts, multiply `ampDamage(baseDamage(), victim)` by `damageFactor()`, the falloff at the projectile's position, the same for every victim.
The older `damageWithAmp(int, Entity)` still works but is deprecated: it rounds to whole points and has no falloff.
Register the entity type and its renderer yourself.

### Stats and config

`TurretStats` are the defaults of the turret's config entry.
OMT writes them to `config/openmodularturrets.json` under `turrets.addon_turrets."example:flak_turret"`, with the same keys as a built-in turret section; server values are synced to clients.
Read the current values with `OMTConfig.get().turrets.get(type)`.
`falloff(optimalShare, damageAtMaxRange)` sets the range falloff (keys `optimalRange` and `falloffDamage`): full damage up to `optimalShare` of the turret's range (with range upgrades), then linearly less down to `damageAtMaxRange` of the damage at the end of the range and beyond.
`turnRate(degreesPerTick)` sets how fast the turret turns towards its target (key `turnRate`, on yaw and pitch each); an aiming turret fires only once its aim is within `turrets.aimTolerance` degrees.
Without these calls a turret has no falloff and turns instantly.
`effectStrength(value)` is a free number for an effect weapon (key `effectStrength`, default 0): the built-in repelling and tractor turrets read it as their push or pull speed in blocks per tick, the energy drain turret as the energy it destroys per shot.
Read it in your effect with `OMTConfig.get().turrets.get(type).effectStrength`; with a value above 0 the turret tooltip and Jade show it with the lang key `tooltip.<namespace>.turret.<path>.effect` (one `%s` for the value).
OMT applies the falloff to projectile and ray weapons: a projectile measures from its launch point to the hit, a ray from the turret to where it enters the entity (arc jumps take the first hit's share).
Effect, area and self-targeting weapons get no falloff; one that wants it scales its damage by `base.getLogic().getDamageFactor(turret.getTurretType(), distance)`.
A turn rate only makes sense for a turret whose model turns (`aims() == true`): a ray weapon always aims, so a turn rate on a turret with a static model only delays its shots.

### Ammo

`AmmoDefinition` decides which stacks are an ammo type (`ofItem` for a single item, or any predicate), its name in turret tooltips and a sample stack.
Items matching any definition are accepted by base ammo slots.
An `AmmoItem` created with your ammo type counts as that type even without a definition.

### Sable contraptions

With Sable installed, turrets can sit on contraptions (sub-levels).
Their blocks are stored in a plot far away from where they are shown, so `turret.getBlockPos()` and the base's position are plot coordinates, while entities are in the world.
`turret.getMount()` (a `TurretMount`, made once per tick) gives the world values: `centre()`, `up()` (out of the mounting face), `velocity()` (the contraption's, in blocks per tick), and converts with `toWorld(plotPoint)`, `toLocal(worldPoint)` and `toLocalDirection(worldDirection)`.
Outside contraptions it is the plain block position and changes nothing.

- Projectile, ray and effect weapons need nothing: OMT aims, spawns projectiles (with the contraption's velocity) and traces rays in the world.
- An `AreaWeapon`'s `area()` stays in plot coordinates (around `turret.getBlockPos()`); OMT only passes entities that really touch it.
- A `SelfTargetingWeapon`'s picker that measures distances should use `turret.getMount().centre()` instead of the block position, or compare plot positions from `toLocal(...)`.
- A weapon that spawns entities or plays effects itself should place them at world points from the mount.
- The range falloff is measured in the world: from the projectile's world launch point, or from the ray's world origin, to the hit.
- The turn rate turns the aim relative to the plot, so a turret on a turning contraption also has to turn against the contraption's rotation at its `turnRate`; a slow turret may never come within `turrets.aimTolerance` on a fast-spinning contraption, and then never fires.

## 2. Model (client)

On Fabric this is the `openmodularturrets_client` entrypoint; on NeoForge, annotate it with `@OMTClientPlugin.Register` (found on the client only).

```java
@OMTClientPlugin.Register // NeoForge; Fabric ignores it
public final class ExampleTurretModels implements OMTClientPlugin {
    @Override
    public void registerTurretModels(TurretModelRegistrar registrar) {
        registrar.register(ExampleTurrets.FLAK_TURRET, ExampleTurretModels::flakLayer,
                ResourceLocation.tryParse("example:textures/block/turret/flak_turret.png"),
                "base/holder", "base/holder/weapon"); // part that yaws, part that pitches
    }
}
```

Models are entity-style `LayerDefinition`s like the built-in ones (`client/model/TurretModelDefinitions`): Y down, base plate at the bottom.
For full control pass a `Function<ModelPart, TurretModel>` instead.
A turret without a model is invisible (a warning is logged).

## 3. Assets and data you provide

OMT registers the block but not its resources.
In your namespace:

- `assets/example/blockstates/flak_turret.json`: one variant `""` pointing to a block model that only sets a `particle` texture (the turret is drawn by the block entity renderer).
- `assets/example/models/item/flak_turret.json`: `"parent": "minecraft:builtin/entity"` and display transforms, e.g. copied from `openmodularturrets:item/laser_turret` (the item renderer draws the turret model).
- Lang: `block.example.flak_turret`, and `tooltip.example.turret.flak_turret.description` (shift tooltip).
- Data (folder names of 1.21.1: `loot_table`, `recipe`, `tags/block`): loot table `blocks/flak_turret` (drop itself), the recipe, and the `minecraft:mineable/pickaxe` block tag if turrets should be breakable with `turretBreakable`.
