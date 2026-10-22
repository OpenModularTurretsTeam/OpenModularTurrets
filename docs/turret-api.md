# Turret API (1.12.2)

Other mods can add turrets to Open Modular Turrets (OMT) 4.0.0.
An added turret is a full turret: it mounts on a base, uses the base's targeting, energy, ammo, upgrades, addons and trusted players, has a config entry and shows in the OMT creative tab and in tooltips.
The 14 built-in turrets are registered through the same API (`registry/BuiltinTurrets.java`), so they are the reference examples; `runenv/src/main/java/omtteam/apiexample/ExampleTurretPlugin.java` in the dev workspace is a small addon turret.

Add OMT as a dependency and `required-after:openmodularturrets` to your `@Mod` dependencies.
The API is `omtteam.openmodularturrets.api` (common) and `omtteam.openmodularturrets.client.api` (client); the rules of a turret are the core classes `TurretType`, `AmmoType` and `TurretStats` (`omtteam.openmodularturrets.core`).

## 1. Register the turret (common)

```java
@OMTPlugin.Register
public final class ExampleTurrets implements OMTPlugin {
    public static final AmmoType SHELL = AmmoType.of("example", "shell");
    public static final TurretType FLAK_TURRET = TurretType.builder("example", "flak_turret")
            .requiredBaseTier(3)          // 1–5, default 1
            .ammo(SHELL)                  // omit for energy-only turrets
            .priorities(10, 10, 20, 1, 10) // MAX_HP, HP_REMAINING, DISTANCE, ARMOR, PLAYER
            .build();

    @Override
    public void registerTurrets(TurretRegistrar registrar) {
        registrar.registerAmmo(AmmoDefinition.ofItem(SHELL, () -> ExampleItems.SHELL, 0));
        registrar.registerTurret(new TurretDefinition(FLAK_TURRET,
                ProjectileWeapon.of(ExampleSounds.FLAK, (world, ammo, target, base) -> new FlakProjectile(world, base)),
                TurretStats.builder(24, 20, 6, 500).accuracyDeviation(15).maxSimultaneous(2).build()));
    }
}
```

OMT finds classes annotated with `@OMTPlugin.Register` in the mod scan data (they need a public no-argument constructor) and calls them in its own preInit, sorted by class name, before it registers its blocks; mod load order does not matter.
Suppliers (`() -> ExampleItems.SHELL`) are only called later, so they may point to things your mod registers in its registry events.
Everything passed directly must exist when OMT calls the plugin.
That matters for the weapon's sound: create the `SoundEvent` in a static field (`new SoundEvent(id).setRegistryName(id)`) and register that same instance in your `RegistryEvent.Register<SoundEvent>`, as OMT's own `ModSounds` does.

OMT then registers a `BlockTurret` and its item with the id `example:flak_turret` (as your mod).
Get them with `OMTApi.block(type)` / `OMTApi.item(type)` once the blocks are registered.

`TurretDefinition.builder(type, weapon, stats).ambientEffect((world, pos, random) -> …).build()` adds client particles around the placed turret.

### `TurretType` options

| Builder call | Effect |
| --- | --- |
| `requiredBaseTier(n)` | lowest base tier the turret can be placed on (lower tiers drop it) |
| `ammo(type)` | ammo drawn per shot (one item, plus one per scattershot upgrade) |
| `priorities(…)` | default targeting weights |
| `mountFaces(Face…)` | base faces it may mount on (default all six; the melee turret uses `UP, DOWN`) |
| `ignoresScattershot()` | scattershot upgrades add no shots, ammo, energy or scatter |
| `skipsSlowedTargets()` | entities with slowness are not targeted (slowing turret) |

### Weapons (`api.weapon`)

| Kind | Does | Built-in examples |
| --- | --- | --- |
| `ProjectileWeapon` | spawns a `TurretProjectile` per shot, aimed with lead | machine gun, rocket, plasma |
| `RayWeapon` | instant ray to the target; damage from a `RayProfile`, visuals from the builder | laser, railgun, arc |
| `EffectWeapon` | runs a `TargetEffect` on the base's target, no aiming | slowing, teleporter |
| `AreaWeapon` | ignores the base's target, strikes every damageable living entity in an area | melee |
| `SelfTargetingWeapon` | ignores the base's target, picks its own | – |

Energy and ammo are taken by OMT before your code runs, once per shot, and the shot follows the turret's cooldown (fire rate and its upgrades).
Only these five classes are accepted.

**Own targeting**: once the turret is ready to fire, a `SelfTargetingWeapon`'s picker gets the world, the turret and the base and returns the entities to shoot at; if it returns any, OMT takes energy and ammo and calls the shot, otherwise nothing is consumed and the picker is asked again 5 ticks later.
Filter with `OMTApi.canDamage(base, entity)` to spare the owner, trusted players and their pets, and damage with `OMTApi.hurt(base, entity, damageType, amount, bypassArmor, direct)` so kills count for the base and the fake drops addon applies (without it, killed mobs drop nothing).

Projectiles extend `omtteam.openmodularturrets.entity.projectile.TurretProjectile`; register the entity and its renderer yourself.

### Stats and config

`TurretStats` are the defaults of the turret's config entry.
OMT writes them to `config/openmodularturrets_addon_turrets.cfg`, one category per turret id with the keys of a built-in turret section; read the current values with `ConfigLogicSettings.INSTANCE.turret(type)`.

### Ammo

`AmmoDefinition` decides which stacks are an ammo type (`ofItem(type, item, meta)` for one item variant, or any predicate), the translation key of its name in turret tooltips and a sample stack.
Items matching any definition are accepted by base ammo slots (with `useWhitelistForAmmo`).

## 2. Model (client)

```java
@SideOnly(Side.CLIENT)
@OMTClientPlugin.Register
public final class ExampleTurretModels implements OMTClientPlugin {
    @Override
    public void registerModels(TurretModelRegistrar registrar) {
        registrar.register(ExampleTurrets.FLAK_TURRET, ModelFlakTurret::new,
                new ResourceLocation("example", "textures/blocks/flak_turret.png"), null);
    }
}
```

Keep it in its own class: it is only loaded on the client.
Models extend `omtteam.openmodularturrets.client.model.ModelAbstractTurret` like the built-in ones (64×64 texture); the renderer turns them into the frame of the base face the turret sits on.
A turret without a model is drawn with the machine gun's model and texture.

## 3. Assets you provide

OMT registers the block and item, not their resources. In your namespace:

- `assets/example/blockstates/flak_turret.json` with the variants `concealed=false` and `concealed=true` (a model that only sets a `particle` texture; the turret is drawn by the tile entity renderer), and an `inventory` variant or `models/item/flak_turret.json` for the item (drawn by OMT's item renderer).
- Lang: `tile.example.flak_turret.name` and `tooltip.example.turret.flak_turret.description` (shift tooltip).
- The recipe.
