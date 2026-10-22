package omtteam.openmodularturrets.registry;

import omtteam.omlib.api.render.ColorOM;
import omtteam.openmodularturrets.block.turret.BlockTurret;
import omtteam.openmodularturrets.core.turret.RayProfile;
import omtteam.openmodularturrets.core.turret.TurretType;
import omtteam.openmodularturrets.entity.projectile.ArrowProjectile;
import omtteam.openmodularturrets.entity.projectile.BlazingClayProjectile;
import omtteam.openmodularturrets.entity.projectile.BulletProjectile;
import omtteam.openmodularturrets.entity.projectile.DisposableTurretProjectile;
import omtteam.openmodularturrets.entity.projectile.GrenadeProjectile;
import omtteam.openmodularturrets.entity.projectile.PlasmaProjectile;
import omtteam.openmodularturrets.entity.projectile.PotatoProjectile;
import omtteam.openmodularturrets.entity.projectile.RocketProjectile;
import omtteam.openmodularturrets.tileentity.turret.EffectWeapons;
import omtteam.openmodularturrets.tileentity.turret.RayWeapons;
import omtteam.openmodularturrets.turret.AreaWeapon;
import omtteam.openmodularturrets.turret.EffectWeapon;
import omtteam.openmodularturrets.turret.ProjectileWeapon;
import omtteam.openmodularturrets.turret.RayWeapon;
import omtteam.openmodularturrets.turret.TurretDefinition;
import omtteam.openmodularturrets.turret.TurretTooltip;

import java.util.Arrays;
import java.util.List;

/**
 * The turrets of this mod: weapon, tooltip texts and ambient particles per turret type (ported from the 1.12.2
 * turret tile entity, block and item block classes).
 */
final class BuiltinTurrets {
    private static final String ACCURACY = "text.openmodularturrets.turret.accuracy.";
    private static final String AMMO = "text.openmodularturrets.turret.ammo.";
    private static final String FLAVOUR = "text.openmodularturrets.flavour.turret.";

    private BuiltinTurrets() {
    }

    static List<TurretDefinition> definitions() {
        return Arrays.asList(
                new TurretDefinition(TurretType.DISPOSABLE_TURRET,
                        ProjectileWeapon.of(ModSounds.disposableLaunchSound,
                                (world, ammo, target, base) -> new DisposableTurretProjectile(world, ammo, base)),
                        new TurretTooltip(ACCURACY + "low", AMMO + "0", 0, FLAVOUR + "0"), null),
                new TurretDefinition(TurretType.POTATO_CANNON_TURRET,
                        ProjectileWeapon.of(ModSounds.potatoLaunchSound,
                                (world, ammo, target, base) -> new PotatoProjectile(world, ammo, base)),
                        new TurretTooltip(ACCURACY + "medium", AMMO + "6", 0, FLAVOUR + "6"), null),
                new TurretDefinition(TurretType.CROSSBOW_TURRET,
                        ProjectileWeapon.of(ModSounds.potatoLaunchSound,
                                (world, ammo, target, base) -> new ArrowProjectile(world, ammo, base)),
                        new TurretTooltip(ACCURACY + "low", AMMO + "arrow", 0, FLAVOUR), null),
                new TurretDefinition(TurretType.MELEE_TURRET,
                        new AreaWeapon(ModSounds.slowingLaunchSound, EffectWeapons::meleeArea, EffectWeapons::strike),
                        new TurretTooltip(ACCURACY + "high", null, 0, FLAVOUR + "melee"), null),
                new TurretDefinition(TurretType.MACHINE_GUN_TURRET,
                        ProjectileWeapon.of(ModSounds.machinegunLaunchSound,
                                (world, ammo, target, base) -> new BulletProjectile(world, ammo, base)),
                        new TurretTooltip(ACCURACY + "medium", AMMO + "1", 0, FLAVOUR + "1"), null),
                new TurretDefinition(TurretType.INCENDIARY_TURRET,
                        ProjectileWeapon.of(ModSounds.incendiaryLaunchSound,
                                (world, ammo, target, base) -> new BlazingClayProjectile(world, ammo, base)),
                        new TurretTooltip(ACCURACY + "medium", AMMO + "7", 5, FLAVOUR + "7"), null),
                new TurretDefinition(TurretType.GRENADE_TURRET,
                        ProjectileWeapon.of(ModSounds.grenadeLaunchSound,
                                (world, ammo, target, base) -> new GrenadeProjectile(world, ammo, base)),
                        new TurretTooltip(ACCURACY + "medium", AMMO + "2", 3, FLAVOUR + "2"), null),
                new TurretDefinition(TurretType.SLOWING_TURRET,
                        new EffectWeapon(ModSounds.slowingLaunchSound, EffectWeapons::slow),
                        new TurretTooltip(ACCURACY + "high", AMMO + "4", 0, FLAVOUR + "8"),
                        BlockTurret.dust(200, 200, 200, false)),
                new TurretDefinition(TurretType.ROCKET_TURRET,
                        ProjectileWeapon.of(ModSounds.rocketLaunchSound,
                                (world, ammo, target, base) -> new RocketProjectile(world, target, ammo, base)),
                        new TurretTooltip(ACCURACY + "exact", AMMO + "3", 5, FLAVOUR + "3"), null),
                new TurretDefinition(TurretType.TELEPORTER_TURRET,
                        new EffectWeapon(ModSounds.teleportLaunchSound, EffectWeapons::teleport),
                        new TurretTooltip(ACCURACY + "high", AMMO + "4", 0, FLAVOUR + "9"), null),
                // ray colours from 1.12.2 ColorOM(r, g, b, a)
                new TurretDefinition(TurretType.ARC_TURRET,
                        RayWeapon.builder(ModSounds.arcLaunchSound, RayProfile.ARC).duration(3).lightning()
                                .drawFromTip().build(),
                        new TurretTooltip(ACCURACY + "high", AMMO + "4", 0, FLAVOUR + "arc"), null),
                new TurretDefinition(TurretType.LASER_TURRET,
                        RayWeapon.builder(ModSounds.laserLaunchSound, RayProfile.LASER)
                                .color(new ColorOM(1F, 0.1F, 0F, 0.38F)).duration(5).hitSound(ModSounds.laserHitSound)
                                .build(),
                        new TurretTooltip(ACCURACY + "high", AMMO + "4", 0, FLAVOUR + "4"), null),
                new TurretDefinition(TurretType.RAILGUN_TURRET,
                        RayWeapon.builder(ModSounds.railgunLaunchSound, RayProfile.RAILGUN)
                                .color(new ColorOM(1F, 0.5F, 0F, 0.2F)).duration(3).hitSound(ModSounds.railGunHitSound)
                                .onBlockHit(RayWeapons::railgunBlockHit).build(),
                        new TurretTooltip(ACCURACY + "exact", AMMO + "5", 0, FLAVOUR + "5"),
                        BlockTurret.dust(0, 50, 200, true)),
                new TurretDefinition(TurretType.PLASMA_TURRET,
                        ProjectileWeapon.of(ModSounds.plasmaLaunchSound,
                                (world, ammo, target, base) -> new PlasmaProjectile(world, base)),
                        new TurretTooltip(ACCURACY + "medium", AMMO + "air", 3, FLAVOUR), null));
    }
}
