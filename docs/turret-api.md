# Turret API

Other mods (Fabric, NeoForge and Forge) can add turrets to Open Modular Turrets (OMT).
An added turret is a full turret: it mounts on a base, uses the base's targeting, energy, ammo, upgrades, addons and trusted players, has a config entry and shows in the OMT creative tab, in tooltips and in Jade.
The 14 built-in turrets are registered through the same API (`registry/BuiltinTurrets.java`), so they are the reference examples.

Works the same on 1.20.1 and 1.21.1 (Fabric), on 1.21.1 (NeoForge) and on 1.20.1 (Forge); only the way OMT finds your classes differs.
On Fabric, add OMT as a `modCompileOnly`/`modImplementation` dependency and to `depends` in your `fabric.mod.json`.
On NeoForge, add it as a `compileOnly`/`implementation` dependency and as a `[[dependencies.<your mod id>]]` entry in your `neoforge.mods.toml`.
On Forge, add it as a `modCompileOnly`/`modImplementation` dependency (OMT's Forge jar uses SRG names) and as a `[[dependencies.<your mod id>]]` entry in your `mods.toml`.

## 1. Register the turret (common)

On Fabric, declare an `openmodularturrets` entrypoint (and `openmodularturrets_client` for the model, section 2):

```json
"entrypoints": {
  "openmodularturrets": ["com.example.turrets.ExampleTurrets"],
  "openmodularturrets_client": ["com.example.turrets.client.ExampleTurretModels"]
}
```

```java
@OMTPlugin.Register // NeoForge and Forge; Fabric ignores it
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
                TurretStats.builder(24, 20, 6, 500).accuracyDeviation(15).maxSimultaneous(2).build()));
    }
}
```

On NeoForge and Forge, annotate the class with `@OMTPlugin.Register` instead; OMT finds it in the mod scan data (the class needs a public no-argument constructor).
A mod for several loaders does both.
OMT calls the plugin at the start of its own initialisation, before it loads its config and registers blocks, so mod load order does not matter.
Several plugins are called in the order Fabric lists the entrypoints, or on NeoForge and Forge sorted by mod id and class name.
Suppliers (`() -> ExampleItems.SHELL`) are only called later, so they may point to things your own initializer registers.
Everything passed directly must already exist when OMT calls the plugin, which on NeoForge and Forge is during mod construction, before any registry opens.
That matters for the weapon's sound: create the `SoundEvent` in a static field (`SoundEvent.createVariableRangeEvent(id)`) and register that same instance in your sound registration, as OMT's own `ModSounds` does; a `DeferredHolder` from a `DeferredRegister` has no value yet at that point.

OMT then registers a `TurretBlock` and a `BlockItem` with the id `example:flak_turret` and adds it to its turret block entity type.
Get them with `OMTApi.block(type)` / `OMTApi.item(type)` once loading is done (not inside `registerTurrets`).

### `TurretType` options

| Builder call | Effect |
| --- | --- |
| `requiredBaseTier(n)` | lowest base tier the turret can be placed on (lower tiers drop it) |
| `ammo(type)` | ammo drawn per shot (one item, plus one per scattershot upgrade) |
| `priorities(…)` | default targeting weights, see `TargetingPriority` |
| `mountFaces(Face…)` | base faces it may mount on (default all six; the melee turret uses `UP, DOWN`) |
| `ignoresScattershot()` | scattershot upgrades add no shots, ammo, energy or scatter |
| `skipsSlowedTargets()` | entities with slowness are not targeted (slowing turret) |

### Weapons

| Kind | Does | Built-in examples |
| --- | --- | --- |
| `ProjectileWeapon` | spawns a `TurretProjectile` per shot, aimed with lead | machine gun, rocket, plasma |
| `RayWeapon` | instant ray to the target; damage from a `RayProfile`, visuals and damage types from the builder | laser, railgun, arc |
| `EffectWeapon` | runs a `TargetEffect` on the base's target, no aiming | slowing, teleporter |
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
With `aims = true` the turret turns towards the first picked entity.
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
Register the entity type and its renderer yourself.

### Stats and config

`TurretStats` are the defaults of the turret's config entry.
OMT writes them to `config/openmodularturrets.json` under `turrets.addon_turrets."example:flak_turret"`, with the same keys as a built-in turret section; server values are synced to clients.
Read the current values with `OMTConfig.get().turrets.get(type)`.

### Ammo

`AmmoDefinition` decides which stacks are an ammo type (`ofItem` for a single item, or any predicate), its name in turret tooltips and a sample stack.
Items matching any definition are accepted by base ammo slots.
An `AmmoItem` created with your ammo type counts as that type even without a definition.

### Sable contraptions (1.21.1)

With Sable installed, turrets can sit on contraptions (sub-levels).
Their blocks are stored in a plot far away from where they are shown, so `turret.getBlockPos()` and the base's position are plot coordinates, while entities are in the world.
`turret.getMount()` (a `TurretMount`, made once per tick) gives the world values: `centre()`, `up()` (out of the mounting face), `velocity()` (the contraption's, in blocks per tick), and converts with `toWorld(plotPoint)`, `toLocal(worldPoint)` and `toLocalDirection(worldDirection)`.
Outside contraptions (and on 1.20.1 and 1.21.11, which have no Sable) it is the plain block position and changes nothing.

- Projectile, ray and effect weapons need nothing: OMT aims, spawns projectiles (with the contraption's velocity) and traces rays in the world.
- An `AreaWeapon`'s `area()` stays in plot coordinates (around `turret.getBlockPos()`); OMT only passes entities that really touch it.
- A `SelfTargetingWeapon`'s picker that measures distances should use `turret.getMount().centre()` instead of the block position, or compare plot positions from `toLocal(...)`.
- A weapon that spawns entities or plays effects itself should place them at world points from the mount.

## 2. Model (client)

On Fabric this is the `openmodularturrets_client` entrypoint; on NeoForge and Forge, annotate it with `@OMTClientPlugin.Register` (found on the client only).

```java
@OMTClientPlugin.Register // NeoForge and Forge; Fabric ignores it
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
- Data (both versions' folder names): loot table `blocks/flak_turret` (drop itself), the recipe, and the `minecraft:mineable/pickaxe` block tag if turrets should be breakable with `turretBreakable`.
