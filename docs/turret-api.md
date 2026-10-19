# Turret API

Other Fabric mods can add turrets to Open Modular Turrets (OMT).
An added turret is a full turret: it mounts on a base, uses the base's targeting, energy, ammo, upgrades, addons and trusted players, has a config entry and shows in the OMT creative tab, in tooltips and in Jade.
The 14 built-in turrets are registered through the same API (`registry/BuiltinTurrets.java`), so they are the reference examples.

Works the same on 1.20.1 and 1.21.1.
Add OMT as a `modCompileOnly`/`modImplementation` dependency and to `depends` in your `fabric.mod.json`.

## 1. Register the turret (common)

Declare an `openmodularturrets` entrypoint:

```json
"entrypoints": {
  "openmodularturrets": ["com.example.turrets.ExampleTurrets"],
  "openmodularturrets_client": ["com.example.turrets.client.ExampleTurretModels"]
}
```

```java
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

OMT calls the entrypoint at the start of its own initializer, before it loads its config and registers blocks, so mod load order does not matter.
On NeoForge, plugins are not found yet (the NeoForge port is in progress).
Suppliers (`() -> ExampleItems.SHELL`) are only called later, so they may point to things your own initializer registers.

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
Filter with `OMTApi.canDamage(base, entity)` to spare the owner, trusted players and their pets, and damage with `OMTApi.hurt(base, entity, damageType, amount, direct)` so kills count for the base and the fake drops addon applies.

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

## 2. Model (client)

```java
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
